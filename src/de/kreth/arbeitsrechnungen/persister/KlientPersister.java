package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import de.kreth.arbeitsrechnungen.data.Einheit;

public class KlientPersister {

   private Verbindung verbindung;
   private Logger logger;

   public KlientPersister(Verbindung verbindung) {
      this.verbindung = verbindung;
      logger = Logger.getLogger(getClass());
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

}
