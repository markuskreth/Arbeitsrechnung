package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Einheit;

public class DatenPersister extends AbstractPersister {

   public DatenPersister(Options optionen) {
      super(optionen);
   }

   public List<Forderung> getForderungen() {
      String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, einheiten.Rechnung_Datum AS datum, SUM(einheiten.Preis) AS summe "
            + "FROM einheiten, klienten " + "WHERE einheiten.klienten_id = klienten.klienten_id " + "AND NOT (ISNULL( einheiten.Rechnung_verschickt )) "
            + "AND ISNULL( einheiten.Bezahlt ) " + "GROUP BY einheiten.Rechnung_Datum,einheiten.klienten_id " + "ORDER BY einheiten.Rechnung_Datum;";

      List<Forderung> result = new Vector<>();

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

      logger.debug(sqltext);

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

}
