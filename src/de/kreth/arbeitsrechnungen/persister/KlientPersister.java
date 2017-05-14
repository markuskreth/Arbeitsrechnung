package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Einheit;

public class KlientPersister extends AbstractPersister {

   public KlientPersister(Options optionen) {
      super(optionen);
   }

   public Einheit getEinheitById(int einheit) {
      String sqltext = "SELECT einheiten_id,klienten_id,angebote_id,Datum,Beginn,Ende,Preisänderung,zusatz1,zusatz2,Rechnung_Datum,Bezahlt_Datum " + "FROM einheiten "
            + "WHERE einheiten_id=" + einheit + ";";
      logger.info("Einheit_einzel.setEinheit: " + sqltext);

      try {
         ResultSet daten = verbindung.query(sqltext);
         daten.first();

         Einheit e = new Einheit.Builder().angebotId(daten.getInt("angebote_id")).beginn(daten.getTimestamp("Beginn")).ende(daten.getTimestamp("Ende"))
               .klientenId(daten.getInt("klienten_id")).datum(daten.getDate("Datum")).preisAenderung(daten.getDouble("Preisänderung"))
               .rechnungDatum(daten.getDate("Rechnung_Datum")).zusatz1(daten.getString("zusatz1")).zusatz2(daten.getString("zusatz2")).bezahltDatum(daten.getDate("Bezahlt_Datum"))
               .build();

         return e;

      } catch (Exception e) {
         logger.error("Einheit_einzel.setEinheit: ", e);
      }
      return null;
   }

   public class Auftraggeber {

      private String name;
      private String zusatz1;
      private String zusatz2;

      public String getName() {
         return name;
      }

      public boolean hasZusatz1() {
         return zusatz1 != null;
      }

      public boolean hasZustz2() {
         return zusatz2 != null;
      }

      public String getZusatz1() {
         return zusatz1;
      }

      public String getZusatz2() {
         return zusatz2;
      }
      
   }

   public Auftraggeber getAuftraggeber(int klient) {
      // Id und Name des übergebenen auftraggebers einfügen
      String sqltext = "SELECT Auftraggeber, Zusatz1, Zusatz2, Zusatz1_Name, Zusatz2_Name  FROM klienten WHERE klienten_id=" + klient + ";";
      logger.info("Einheit_einzel.setAuftraggeber: " + sqltext);
      try {
         ResultSet daten = verbindung.query(sqltext);
         if (daten.first()) {
            Auftraggeber result = new Auftraggeber();
            result.name = daten.getString("Auftraggeber");
            
            if(daten.getBoolean("Zusatz1")) {
               result.zusatz1 = daten.getString("Zusatz1_Name");
            }
            if(daten.getBoolean("Zusatz2")) {
             result.zusatz2 = daten.getString("Zusatz2_Name");  
            }
            return result;
         }
      } catch (Exception e) {
         logger.error("Einheit_einzel.setAuftraggeber: ", e);
      }
      return null;
   }
}
