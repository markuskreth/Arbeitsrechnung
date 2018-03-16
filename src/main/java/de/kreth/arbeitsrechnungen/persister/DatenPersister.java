package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import de.kreth.arbeitsrechnungen.MySqlDate;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.ArbeitsstundeImpl;
import de.kreth.arbeitsrechnungen.data.Einheit;

public class DatenPersister extends AbstractPersister {

   public DatenPersister(Options optionen) {
      super(optionen);
   }

   public List<Forderung> getForderungen() {
      String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, einheiten.Rechnung_Datum AS datum, SUM(einheiten.Preis) AS summe "
            + "FROM einheiten, klienten " + "WHERE einheiten.klienten_id = klienten.klienten_id " + "AND NOT (ISNULL( einheiten.Rechnung_verschickt )) "
            + "AND ISNULL( einheiten.Bezahlt ) GROUP BY einheiten.Rechnung_Datum,einheiten.klienten_id ORDER BY einheiten.Rechnung_Datum;";

      List<Forderung> result = new Vector<>();
      debugLogSql(sqltext);
      
      try {
         ResultSet daten = verbindung.query(sqltext);
         while (daten.next()) {
            Forderung e = new Forderung();
            e.id = daten.getInt("id");
            e.auftraggeber = daten.getString("auftraggeber");
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(daten.getDate("datum").getTime());
            e.datum = cal;
            e.summe = daten.getDouble("summe");
            result.add(e);
         }
      } catch (SQLException e) {
         logger.warn("Exception in getEinheiten", e);
      }

      return result;
   }

   public List<Einheit> getEinheiten() {
      String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, COUNT(einheiten.Preis) AS anzahl, SUM(einheiten.Preis) AS klientpreis "
            + "FROM einheiten, klienten " + "WHERE einheiten.klienten_id = klienten.klienten_id " + "AND ISNULL( einheiten.Rechnung_verschickt ) "
            + "AND ISNULL( einheiten.Bezahlt ) " + "GROUP BY einheiten.klienten_id " + "ORDER BY klientpreis;";

      debugLogSql(sqltext);

      Vector<Einheit> result = new Vector<>();

      try {
         ResultSet daten = verbindung.query(sqltext);
         while (daten.next()) {
            Einheit f = new Einheit.Builder().auftraggeber(daten.getString("auftraggeber")).anzahl(daten.getInt("anzahl")).klientenpreis(daten.getDouble("klientpreis"))
                  .id(daten.getInt("id")).build();
            result.add(f);
         }
      } catch (Exception e) {
         logger.warn("Exception in getForderungen", e);
      }
      return result;
   }

   public class Forderung {

      private double summe;
      private Calendar datum;
      private String auftraggeber;
      private int id;

      public double getSumme() {
         return summe;
      }

      public Calendar getDatum() {
         return datum;
      }

      public String getAuftraggeber() {
         return auftraggeber;
      }

      public int getId() {
         return id;
      }

      @Override
      public String toString() {
         return auftraggeber + ": " + summe + " €" + DateFormat.getDateInstance(Calendar.SHORT).format(datum.getTime());
      }
   }

   public List<Arbeitsstunde> getEinheiten(int klienten_id, String filter) throws SQLException {

      String sqltext = "SELECT DISTINCT einheiten.einheiten_id, einheiten.klienten_id, "
            + "einheiten.angebote_id, Datum, Beginn, Ende, einheiten.zusatz1, einheiten.zusatz2, Preisänderung, Rechnung_verschickt, "
            + "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.preis_pro_stunde, "
            + "klienten.Zusatz1 AS bool1, klienten.Zusatz2 AS bool2, klienten.Zusatz1_Name, klienten.Zusatz2_Name FROM einheiten, "
            + "angebote, klienten WHERE einheiten.klienten_id=" + klienten_id + " AND einheiten.angebote_id=angebote.angebote_id"
            + " AND einheiten.klienten_id=klienten.klienten_id" + " AND " + filter + " ORDER BY Datum, Preis;";

      this.debugLogSql(sqltext);
      
      List<Arbeitsstunde> result = new ArrayList<>();
      
      ResultSet daten = verbindung.query(sqltext);
      debugLogSql(sqltext);
      
      while (daten.next()) {

         int id = daten.getInt("einheiten_id");
         int angebote_id = daten.getInt("angebote_id");

         ArbeitsstundeImpl.Builder stunde = new ArbeitsstundeImpl.Builder(id, klienten_id, angebote_id)
               .datum(daten.getDate("Datum"))
               .inhalt(daten.getString("Inhalt"))
               .beginn(daten.getTimestamp("Beginn"))
               .ende(daten.getTimestamp("Ende"))
               .preis(daten.getDouble("Preis"))
               .preisaenderung(daten.getDouble("Preisänderung"))
               .dauerInMinuten(daten.getInt("Dauer"));

         if (daten.getBoolean("bool1")) {
            stunde.zusatz1(daten.getString("einheiten.zusatz1"));
         }
         
         if (daten.getBoolean("bool2")) {
            stunde.zusatz2(daten.getString("einheiten.zusatz2"));
         }

         if (daten.getBoolean("preis_pro_stunde")) {
            stunde.dauerInMinuten(daten.getInt("Dauer"));
         }
         
         try {
            stunde.setVerschickt(daten.getDate("Rechnung_Datum"));
         } catch (Exception e) {
            logger.info(daten.getInt("einheiten.einheiten_id") + ": Rechnung Datum nicht gesetzt!", e);
            stunde.setVerschickt(null);
         }
         try {
            stunde.bezahlt(daten.getDate("Bezahlt_Datum"));
         } catch (Exception e) {
            logger.info(daten.getInt("einheiten.einheiten_id") + ": Bezahlt Datum nicht gesetzt!", e);
            stunde.bezahlt(null);
         }
         
         result.add(stunde.build());
      }
      
      return result;
   }

   public List<Date> getDatumForEinheiten(int[] einheit_id) {
      List<Date> daten = new ArrayList<>();

      String sqltext = "";
      sqltext = "SELECT * FROM einheiten WHERE einheiten_id in (" + einheit_id[0];

      for (int i = 1; i < einheit_id.length; i++) {
         sqltext = sqltext + ", " + einheit_id[i];
      }
      sqltext = sqltext + ");";

      debugLogSql(sqltext);

      try {

         ResultSet einheit = verbindung.query(sqltext);
         
         while (einheit.next()) {
            daten.add(einheit.getDate("Datum"));
         }

      } catch (SQLException e) {
         logger.error("Error fetching Daten for EinheitenIds " + einheit_id, e);
      }
      return daten;
   }

   public void deleteEinheiten(int[] einheit_id) {

      String sqltext;
      
      for (int i = 0; i < einheit_id.length; i++) {
         sqltext = "DELETE FROM einheiten WHERE einheiten_id=" + einheit_id[i] + ";";
         debugLogSql(sqltext);
         try {
            verbindung.sql(sqltext);
         } catch (SQLException e) {
            logger.error("Sqltext nicht erfolgreich: " + sqltext, e);
         }

      }
   }

   public boolean updateFields(String feld, String feld2, java.util.Date datum, int[] einheit_id) {
      // SQL-Text erstellen
      String sqltext = "null";
      int wahr = 0;
      if (datum != null) {
         MySqlDate tmpdate = new MySqlDate(datum);
         sqltext = "\"" + tmpdate.getSqlDate() + "\"";
         wahr = 1;
      }

      sqltext = "UPDATE einheiten SET " + feld + "=" + sqltext + ", " + feld2 + "=" + wahr + " WHERE einheiten_id IN (";
      for (int i = 0; i < einheit_id.length; i++) {
         sqltext = sqltext + einheit_id[i] + ",";
         if (i == einheit_id.length - 1)
            sqltext = sqltext.substring(0, sqltext.length() - 1) + ");";
      }
      
      debugLogSql(sqltext);
      
      // SQL ausführen
         try {
            verbindung.sql(sqltext);
            return true;
         } catch (SQLException e) {
            logger.error("SQL-Text: " + sqltext, e);
            return false;
         }
   }

}
