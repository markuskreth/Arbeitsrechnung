package de.kreth.arbeitsrechnungen.persister;

import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.kreth.arbeitsrechnungen.MySqlDate;
import de.kreth.arbeitsrechnungen.Options;

import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.persister.KlientPersister.Auftraggeber;

public class AngebotPersister extends AbstractPersister {

   public AngebotPersister(Options optionen) throws SQLException {
      super(optionen);
   }

   public void storeEinheit(Auftraggeber klient, Einheit einheit) throws SQLException {

      MySqlDate sqlBeginn = new MySqlDate(einheit.getBeginn());
      MySqlDate sqlEnde = new MySqlDate(einheit.getEnde());

      MySqlDate datum = new MySqlDate(einheit.getDatum());

      MySqlDate bezahltDatum = null;
      int isBezahlt = 0;
      if (einheit.getBezahltDatum() != null) {
         logger.debug("Bezahlt: " + DateFormat.getDateInstance().format(einheit.getBezahltDatum()));
         bezahltDatum = new MySqlDate(einheit.getBezahltDatum());
         isBezahlt = 1;
      }

      MySqlDate eingereichtDatum = null;
      int isEingereicht = 0;
      if (einheit.getRechnungDatum() != null) {
         if (logger.isDebugEnabled()) {
            logger.debug("Eingereicht: " + DateFormat.getDateInstance().format(einheit.getRechnungDatum()));
         }
         eingereichtDatum = new MySqlDate(einheit.getRechnungDatum());
         isEingereicht = 1;
      }
      storeEinheit(klient.getKlientId(), einheit.getId(), einheit.getPreis(), einheit.getDauerInMinutes(), datum, eingereichtDatum, isEingereicht, bezahltDatum, isBezahlt,
            sqlBeginn, sqlEnde, einheit.getAngebotId(), einheit.getZusatz1(), einheit.getZusatz2(), String.valueOf(einheit.getPreisAenderung()));
   }

   void storeEinheit(int klient, int einheit, double preis, long dauer, MySqlDate datum, MySqlDate eingereichtDatum, int isEingereicht, MySqlDate bezahltDatum, int isBezahlt,
         MySqlDate sqlBeginn, MySqlDate sqlEnde, int angebot_id, String zusatz1, String zusatz2, String preisAenderung) throws SQLException {
      
      if (einheit == -1) {
         if ((isEingereicht != 0) && (isBezahlt != 0)) {
            String sqltext = "INSERT INTO einheiten " + "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
                  + "Rechnung_verschickt,Rechnung_Datum,Bezahlt,Bezahlt_Datum,Preisänderung) VALUES " + "(" + klient + "," + angebot_id + ",\"" + datum + "\",\"" + sqlBeginn
                  + "\",\"" + zusatz1 + "\",\"" + zusatz2 + "\",\"" + preis + "\",\"" + dauer + "\",\"" + isEingereicht + "\",\"" + eingereichtDatum + "\",\"" + isBezahlt + "\",\""
                  + bezahltDatum + "\",\"" + preisAenderung + "\");";

            if (logger.isInfoEnabled()) {
               logger.info("Einheit_einzel.jButton2ActionPerformed: \n" + sqltext);
            }

            PreparedStatement insert1 = verbindung.prepareStatement("INSERT INTO einheiten ("
                  + "klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
                  + "Rechnung_verschickt,Rechnung_Datum,Bezahlt,Bezahlt_Datum,Preisänderung) "
                  + "VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            insert1.setInt(1, klient);
            insert1.setInt(2, angebot_id);
            insert1.setDate(3, datum.getSqlDate());
            insert1.setTimestamp(4, sqlBeginn.getSqlTimestamp());
            insert1.setString(5, zusatz1);
            insert1.setString(6, zusatz2);
            insert1.setDouble(7, preis);
            insert1.setLong(8, dauer);
            insert1.setInt(9, isEingereicht);
            insert1.setDate(10, eingereichtDatum.getSqlDate());
            insert1.setInt(11, isBezahlt);
            insert1.setDate(12, bezahltDatum.getSqlDate());
            insert1.setString(13, preisAenderung);
            
            insert1.execute();
            
         } else if (isEingereicht != 0) {
            String sqltext = "INSERT INTO einheiten " + "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
                  + "Rechnung_verschickt,Rechnung_Datum,Preisänderung) VALUES " + "(" + klient + "," + angebot_id + ",\"" + datum + "\",\"" + sqlBeginn + "\",\"" + sqlEnde
                  + "\",\"" + zusatz1.trim() + "\",\"" + zusatz2.trim() + "\",\"" + preis + "\",\"" + dauer + "\",\"" + isEingereicht + "\",\"" + eingereichtDatum + "\",\""
                  + preisAenderung.trim() + "\");";

            if (logger.isInfoEnabled()) {
               logger.info("Einheit_einzel.jButton2ActionPerformed: \n" + sqltext);
            }

            PreparedStatement insert2 = verbindung.prepareStatement("INSERT INTO einheiten ("
                  + "klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
                  + "Rechnung_verschickt,Rechnung_Datum,Preisänderung) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

            insert2.setInt(1, klient);
            insert2.setInt(2, angebot_id);
            insert2.setDate(3, datum.getSqlDate());
            insert2.setTimestamp(4, sqlBeginn.getSqlTimestamp());
            insert2.setTimestamp(5, sqlEnde.getSqlTimestamp());
            insert2.setString(6, zusatz1.trim());
            insert2.setString(7, zusatz2.trim());
            insert2.setDouble(8, preis);
            insert2.setLong(9, dauer);
            insert2.setInt(10, isEingereicht);
            insert2.setDate(11, eingereichtDatum.getSqlDate());
            insert2.setString(12, preisAenderung.trim());
            insert2.execute();
            
         } else {
            String sqltext = "INSERT INTO einheiten " + "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer," + "Preisänderung) VALUES " + "(" + klient + ","
                  + angebot_id + ",\"" + datum + "\",\"" + sqlBeginn + "\",\"" + sqlEnde + "\",\"" + zusatz1.trim() + "\",\"" + zusatz2.trim() + "\",\"" + preis + "\",\"" + dauer
                  + "\",\"" + preisAenderung + "\");";

            if (logger.isInfoEnabled()) {
               logger.info("Einheit_einzel.jButton2ActionPerformed: \n" + sqltext);
            }

            PreparedStatement insert3 = verbindung.prepareStatement("INSERT INTO einheiten ("
                  + "klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,Preisänderung) "
                  + "VALUES (?,?,?,?,?,?,?,?,?,?)");

            insert3.setInt(1, klient);
            insert3.setInt(2, angebot_id);
            insert3.setDate(3, datum.getSqlDate());
            insert3.setTimestamp(4, sqlBeginn.getSqlTimestamp());
            insert3.setTimestamp(5, sqlEnde.getSqlTimestamp());
            insert3.setString(6, zusatz1.trim());
            insert3.setString(7, zusatz2.trim());
            insert3.setDouble(8, preis);
            insert3.setLong(9, dauer);
            insert3.setString(10, preisAenderung.trim());
            insert3.execute();
         }
      } else {

         if (logger.isInfoEnabled()) {
            logger.info("Einheit_einzel.jButton2ActionPerformed: updating einheiten_id=" + einheit);
         }

         PreparedStatement update1 = verbindung.prepareStatement("UPDATE einheiten set Datum=?,Beginn=?,Ende=?,zusatz1=?,zusatz2=?,Preis=?,Dauer=?, "
               + "Rechnung_verschickt=?,Rechnung_Datum=?,Bezahlt=?,Bezahlt_Datum=? ,Preisänderung=? WHERE einheiten_id=?");
         update1.setDate(1, datum.getSqlDate());
         update1.setTimestamp(2, sqlBeginn.getSqlTimestamp());
         update1.setTimestamp(3, sqlEnde.getSqlTimestamp());
         update1.setString(4, zusatz1.trim());
         update1.setString(5, zusatz2.trim());
         update1.setDouble(6, preis);
         update1.setLong(7, dauer);
         
         if (isEingereicht != 0) {
            update1.setInt(8, isEingereicht);
            update1.setDate(9, eingereichtDatum.getSqlDate());
         } else {
            update1.setNull(8, java.sql.Types.INTEGER);
            update1.setNull(9, java.sql.Types.DATE);
         }
         if ((isEingereicht != 0) && (isBezahlt != 0)) {
            update1.setInt(10, isBezahlt);
            update1.setDate(11, bezahltDatum.getSqlDate());
         } else {
            update1.setNull(10, java.sql.Types.INTEGER);
            update1.setNull(11, java.sql.Types.DATE);
         }

         update1.setString(12, preisAenderung);
         
         update1.setInt(13, einheit);
         update1.execute();

      }

   }

   public Angebot getAngebot(int angebotId) {
      // sqltext = "SELECT Preis, preis_pro_stunde FROM angebote WHERE
      // angebote_id=" +
      // angebot_id + ";";

      String sqltext = "SELECT angebote_id, klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung  FROM angebote WHERE angebote_id=" + angebotId + ";";

      logger.debug("Sql: " + sqltext);

      Angebot angebot = null;

      try (ResultSet rs = verbindung.query(sqltext)) {
         
         rs.next();
         String inhalt = rs.getString("Inhalt");
         float preis = rs.getFloat("Preis");
         String beschr = rs.getString("Beschreibung");
         boolean preisPHour = rs.getInt("preis_pro_stunde") == 1;

         angebot = new Angebot.Builder(inhalt, preis).angebotId(angebotId).beschreibung(beschr).preis_pro_stunde(preisPHour).build();
         rs.close();
      } catch (Exception e) {
         logger.error("Unable to fetch angebot with id " + angebotId, e);
      }
      return angebot;
   }

   public Angebot insertOrUpdateAngebot(int klientId, Angebot angebot) {

      Angebot result = angebot;

      try {
         if (angebot.getAngebote_id() < 0)
            insertAngebot(klientId, angebot);
         else
            updateAngebot(klientId, angebot);

         if (angebot.getAngebote_id() < 0) {
            ResultSet id = verbindung.getAutoincrement();
            if (id.next()) {
               result = new Angebot.Builder(angebot.getInhalt(), angebot.getPreis()).beschreibung(angebot.getBeschreibung()).preis_pro_stunde(angebot.isPreis_pro_stunde())
                     .angebotId(id.getInt(1)).build();
            }
         }
      } catch (SQLException e) {
         logger.error("insertOrUpdateAngebot for klientId=" + klientId + "; Angebot=" + angebot, e);
      }
      return result;
   }

   private void insertAngebot(int klientId, Angebot angebot) throws SQLException {

      PreparedStatement stm = verbindung.prepareStatement("INSERT INTO angebote (klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung) VALUES (?,?,?,?,?)");
      stm.setInt(1, klientId);
      stm.setString(2, angebot.getInhalt());
      stm.setDouble(3, angebot.getPreis());
      stm.setInt(4, (angebot.isPreis_pro_stunde() ? 1 : 0));
      stm.setString(5, angebot.getBeschreibung());
      stm.executeUpdate();
   }

   private void updateAngebot(int klientId, Angebot angebot) throws SQLException {

      PreparedStatement stm = verbindung.prepareStatement("UPDATE angebote SET Inhalt=?, Preis=?, Beschreibung=?, preis_pro_stunde=? WHERE angebote_id=?");
      stm.setString(1, angebot.getInhalt());
      stm.setDouble(2, angebot.getPreis());
      stm.setString(3, angebot.getBeschreibung());
      stm.setInt(4, (angebot.isPreis_pro_stunde() ? 1 : 0));
      stm.setInt(5, angebot.getAngebote_id());
     
      stm.executeUpdate();
   }

   public List<Angebot> getForKlient(int klient) {
      String sqltext = "SELECT * FROM angebote WHERE klienten_id=?";
      logger.debug("Einheit_einzel.initangebote: " + sqltext);
      List<Angebot> result = new ArrayList<>();

      try {
         PreparedStatement stm = verbindung.prepareStatement(sqltext);
         stm.setInt(1, klient);
         ResultSet daten = stm.executeQuery();
         while (daten.next()) {
            Angebot a = new Angebot.Builder(daten.getString("Inhalt"), daten.getFloat("Preis")).beschreibung(daten.getString("Beschreibung"))
                  .preis_pro_stunde(daten.getBoolean("preis_pro_stunde")).angebotId(daten.getInt("angebote_id")).build();
            result.add(a);
            logger.trace("added to AngebotList: " + a);
         }
      } catch (Exception e) {
         logger.error("Einheit_einzel.initangebote: ", e);
      }
      return result;
   }

}
