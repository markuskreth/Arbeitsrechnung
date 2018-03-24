package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Klient;

public class KlientenEditorPersister extends AbstractPersister {

   public KlientenEditorPersister(Options optionen) {
      super(optionen);
   }

   public Klient getKlientById(int klient_id) {
      List<Klient> allKlienten = getAllKlienten(" where klienten_id=" + klient_id);
      if (allKlienten.size() > 0)
         return allKlienten.get(0);
      return null;
   }

   public List<Klient> getAllKlienten() {
      return getAllKlienten("");
   }

   public List<Klient> getAllKlienten(String whereClause) {
      List<Klient> result = new ArrayList<>();
      String sql = "SELECT * FROM klienten " + whereClause;
      logger.debug(sql);

      try {
         ResultSet rs = verbindung.query(sql);

         while (rs.next()) {

            int klientenId = rs.getInt("klienten_id");
            String auftraggeber = rs.getString("Auftraggeber");
            String aAdress1 = rs.getString("AAdresse1");
            String aAdress2 = rs.getString("AAdresse2");
            String aEmail = rs.getString("AEmail");
            String aOrt = rs.getString("AOrt");
            String aPlz = rs.getString("APLZ");
            String aTelefon = rs.getString("ATelefon");

            String kunde = rs.getString("Kunde");
            String kAdresse1 = rs.getString("KAdresse1");
            String kAdresse2 = rs.getString("KAdresse2");
            String kEmail = rs.getString("KEmail");
            String kOrt = rs.getString("KOrt");
            String kPLZ = rs.getString("KPLZ");
            String kTelefon = rs.getString("KTelefon");
            String bemerkungen = rs.getString("Bemerkungen");
            String tex_datei = rs.getString("tex_datei");
            boolean zusatz1 = rs.getBoolean("Zusatz1");
            String zusatz1_Name = rs.getString("Zusatz1_Name");
            boolean zusatz2 = rs.getBoolean("Zusatz2");
            String zusatz2_Name = rs.getString("Zusatz2_Name");
            String rechnungnummer_bezeichnung = rs.getString("rechnungnummer_bezeichnung");

            result.add(new Klient.Builder(klientenId, auftraggeber, aAdress1, aPlz, aOrt).aEmail(aEmail).aTelefon(aTelefon).aAdress2(aAdress2).kunde(kunde).kEmail(kEmail)
                  .kTelefon(kTelefon).kOrt(kOrt).kPlz(kPLZ).kAdress1(kAdresse1).kAdress2(kAdresse2).bemerkungen(bemerkungen).tex_datei(tex_datei).zusatz1(zusatz1)
                  .zusatz1_Name(zusatz1_Name).zusatz2(zusatz2).zusatz2_Name(zusatz2_Name).rechnungnummer_bezeichnung(rechnungnummer_bezeichnung).build());
         }
      } catch (SQLException e) {
         logger.warn("Fehler bei abfrage klienten tabelle", e);
      }

      return result;
   }

   /**
    * Speichert einen einzelnen Wert in der Datenbank. Parameter sind der Name
    * des zu änderndes Feldes und der neue Wert
    * 
    * @param klientenId
    * @param Feld
    * @param Wert
    */
   public void speicherWert(int klientenId, String Feld, Object Wert) {
      try {
         String sql = "UPDATE klienten SET " + Feld + "=" + Wert + " WHERE klienten_id=" + klientenId + ";";
         verbindung.sql(sql);
         logger.debug(sql);
      } catch (Exception e) {
         logger.warn("Fehler bei speichern des Feldes \"" + Feld + "\" mit Wert \"" + Wert + "\"", e);
      }
   }

   public List<Angebot> getAngeboteForKlient(int klient) {

      String sqltext = "SELECT angebote_id, Inhalt, Preis, preis_pro_stunde, Beschreibung FROM angebote WHERE klienten_id=" + klient;
      logger.debug("updateKlientenTabelle: " + sqltext);
      List<Angebot> result = new ArrayList<>();

      try {
         ResultSet rs = verbindung.query(sqltext);
         while (rs.next()) {
            int angebote_id = rs.getInt("angebote_id");
            String inhalt = rs.getString("Inhalt");
            double preis = rs.getDouble("Preis");
            boolean preis_pro_stunde = rs.getBoolean("preis_pro_stunde");
            String beschreibung = rs.getString("Beschreibung");
            Angebot angebot = new Angebot.Builder(inhalt, preis).angebotId(angebote_id).beschreibung(beschreibung).preis_pro_stunde(preis_pro_stunde).build();
            result.add(angebot);
         }
      } catch (SQLException e) {
         logger.error("Fehler bei Abfrage Angebote", e);
      }

      return result;
   }

   public Klient createNewAuftraggeber() {

      Klient k = null;

      String auftraggeber = "Auftraggeber eingeben";
      String aAdresse1 = "Strasse eingeben";
      String plz = "00000";
      String ort = "Ort eingeben";
      String sql = "INSERT INTO klienten (Auftraggeber, AAdresse1, APLZ, AOrt) " + "VALUES (\"" + auftraggeber + "\", \"" + aAdresse1 + "\", \"" + plz + "\", \"" + ort + "\");";

      logger.debug(sql);

      try {
         verbindung.sql(sql);
         ResultSet rs = verbindung.query("SELECT LAST_INSERT_ID()");
         rs.first();
         int klient_id = rs.getInt(1);
         k = new Klient.Builder(klient_id, auftraggeber, aAdresse1, plz, ort).build();
      } catch (SQLException e) {
         logger.error("Fehler bei createNewAuftraggeber", e);
      }
      return k;
   }

   public void delete(Klient toDelete) {

      deleteAngebote(toDelete);

      deleteEinheiten(toDelete);

      deleteRechnungen(toDelete);

      deleteKlient(toDelete);
   }

   private void deleteAngebote(Klient toDelete) {
      String sql;

      sql = "DELETE FROM angebote WHERE klienten_id=" + toDelete.getKlienten_id();
      logger.debug(sql);
      try {
         verbindung.sql(sql);
      } catch (SQLException e) {
         logger.error("", e);
      }
   }

   private void deleteEinheiten(Klient toDelete) {

      String sql = "DELETE FROM einheiten WHERE klienten_id=" + toDelete.getKlienten_id();
      logger.debug(sql);
      try {
         verbindung.sql(sql);
      } catch (SQLException e) {
         logger.error("", e);
      }
   }

   private void deleteRechnungen(Klient toDelete) {

      String sql = "DELETE FROM rechnungen WHERE klienten_id=" + toDelete.getKlienten_id();
      logger.debug(sql);
      try {
         verbindung.sql(sql);
      } catch (SQLException e) {
         logger.error("", e);
      }
   }

   private void deleteKlient(Klient toDelete) {

      String sql = "DELETE FROM klienten WHERE klienten_id=" + toDelete.getKlienten_id();
      logger.debug(sql);
      try {
         verbindung.sql(sql);
      } catch (SQLException e) {
         logger.error("", e);
      }
   }

   public int getRechnungenAnzahl(Klient currentKlient) {
      int anzahl = 0;
      if (currentKlient != null) {
         String sql = "SELECT COUNT(rechnungen_id) AS Anzahl FROM rechnungen WHERE klienten_id=" + currentKlient.getKlienten_id() + ";";
         logger.debug("getRechnungenAnzahl: " + sql);

         try {
            ResultSet rs;
            rs = verbindung.query(sql);

            if (rs.first())
               anzahl = rs.getInt(1);

         } catch (SQLException e) {
            logger.error("", e);
         }
      } else
         logger.info("Klient was null");
      return anzahl;
   }

   public void deleteAngebot(Angebot angebot) {

      String sqltext = "DELETE FROM angebote WHERE angebote_id=" + angebot.getAngebote_id() + ";";
      logger.debug("deleteAngebot: " + sqltext);
      try {
         verbindung.sql(sqltext);
      } catch (SQLException e) {
         logger.error("", e);
      }
   }

}
