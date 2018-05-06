package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.ArbeitsstundeImpl;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.gui.dialogs.Kalenderauswahl;

public class RechnungPersister extends AbstractPersister {

   public RechnungPersister(Options optionen) {
      super(optionen);
   }

   public List<Rechnung> getRechnungenForKlient(int klienten_id) {
      String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, betrag, texdatei, pdfdatei," 
            + "adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, geldeingang"
            + " FROM rechnungen WHERE klienten_id=" + klienten_id;

      logger.debug("FormRechnungen: update: " + sql);

      List<Rechnung> result = new ArrayList<>();
      
      try {
         ResultSet rs = verbindung.query(sql);
         while(rs.next()) {

            Calendar datum = null;
            if(rs.getDate("datum") != null) {
               datum= new GregorianCalendar();
               datum.setTimeInMillis(rs.getDate("datum").getTime());
            }
            
            Calendar zahldatum = null;
            if(rs.getDate("zahldatum") != null) {
               zahldatum = new GregorianCalendar();
               zahldatum.setTimeInMillis(rs.getDate("zahldatum").getTime());
            }
            
            Calendar geldeingang = null;
            if(rs.getDate("geldeingang") != null) {
               geldeingang = new GregorianCalendar();
               geldeingang.setTimeInMillis(rs.getDate("geldeingang").getTime());
            }
            
            int rechnungId = rs.getInt("rechnungen_id");
            Builder r = new Rechnung.Builder()
                  .rechnungen_id(rechnungId)
                  .klienten_id(rs.getInt("klienten_id"))
                  .datum(datum)
                  .rechnungnr(rs.getString("rechnungnr"))
                  .betrag(rs.getDouble("betrag"))
                  .texdatei(rs.getString("texdatei"))
                  .pdfDatei(rs.getString("pdfdatei"))
                  .adresse(rs.getString("adresse"))
                  .zusatz1(rs.getBoolean("zusatz1"))
                  .zusatz2(rs.getBoolean("zusatz2"))
                  .zusammenfassungenErlauben(rs.getBoolean("zusammenfassungen"))
                  .zahldatum(zahldatum)
                  .geldeingang(geldeingang);
            
            r.einheiten(loadEinheitenForRechnung(rechnungId));
            
            result.add(r.build());
         }
      } catch (SQLException e) {
         logger.error("Error fetching rechnungen", e);
      }
      
      return result;
   }

   private Vector<Arbeitsstunde> loadEinheitenForRechnung(int rechnungenId) {

      String sql = "SELECT einheiten_id, angebote_id, Beginn, Bezahlt, Bezahlt_Datum, Datum, Dauer, Ende, Preis, Preisänderung, Rechnung_Datum, rechnung_id, Rechnung_verschickt"
            + ", zusatz1, zusatz2, klienten_id FROM Arbeitrechnungen.einheiten where rechnung_id=" + rechnungenId;

      logger.debug("FormRechnungen: update: " + sql);

      Vector<Arbeitsstunde> result = new Vector<>();

      try {
         ResultSet rs = verbindung.query(sql);

         while (rs.next()) {
            final int einheiten_id = rs.getInt("einheiten_id");
            final int klienten_id2 = rs.getInt("klienten_id");
            final int angebote_id = rs.getInt("angebote_id");
            ArbeitsstundeImpl.Builder std = new ArbeitsstundeImpl.Builder(einheiten_id, klienten_id2, angebote_id).beginn(rs.getDate("Beginn")).datum(rs.getDate("Datum"))
                  .bezahlt(rs.getDate("Bezahlt_Datum")).dauerInMinuten(rs.getInt("Dauer")).ende(rs.getDate("Ende")).preis(rs.getDouble("Preis"))
                  .preisaenderung(rs.getDouble("Preisänderung")).zusatz1(rs.getString("zusatz1")).zusatz2(rs.getString("zusatz2"));

            if (rs.getBoolean("Bezahlt"))
               std.bezahlt(rs.getDate("Bezahlt_Datum"));

            result.add(std.build());
         }
      } catch (SQLException e) {
         logger.error(e.getSQLState(), e);
      }

      return result;
   }

   public boolean delete(Rechnung rechn) {
      String sql = "UPDATE einheiten SET Rechnung_verschickt=null, Rechnung_Datum=null, rechnung_id=null" + " WHERE rechnung_id=" + rechn.getRechnungen_id() + ";";
      logger.info("FormRechnungen: jButtonLoeschenActionPerformed: " + sql);

      boolean deleted = false;
      
      try {
         if (verbindung.sql(sql)) {
            sql = "DELETE from rechnungen WHERE rechnungen_id=" + rechn.getRechnungen_id() + ";";
            deleted = verbindung.sql(sql);
         }
      } catch (SQLException e) {
         logger.error("Rechnung konnte nicht gelöscht werden: " + rechn + "; State=" + e.getSQLState(), e);
      }
      
      return deleted;
   }

   public boolean setRechnungBezahlt(int[] rechnung, String in_klausel, Kalenderauswahl kalender) {
      java.sql.Date sql_date;
      String sql;

      boolean result = false;
      
      sql_date = new java.sql.Date(kalender.getDatum().getTime());
      sql = "UPDATE einheiten SET Bezahlt=1, Bezahlt_Datum=\"" + sql_date.toString() + "\" WHERE rechnung_id IN " + in_klausel + ";";
      logger.debug("FormRechnungen: jButtonBezahltAction: " + sql);
      
      try {
         if (verbindung.sql(sql)) {
            sql = "UPDATE rechnungen SET geldeingang=\"" + sql_date.toString() + "\" WHERE rechnungen_id IN " + in_klausel + ";";
            logger.debug("FormRechnungen: jButtonBezahltAction: " + sql);
            result = verbindung.sql(sql);
            
         }
      } catch (SQLException e) {
         logger.error("Rechnung konnte nicht bezahlt gesetzt werden.", e);
      }
      return result;
   }
}
