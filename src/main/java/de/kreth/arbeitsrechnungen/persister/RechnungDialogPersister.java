package de.kreth.arbeitsrechnungen.persister;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.*;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;

public class RechnungDialogPersister extends AbstractPersister {

   public RechnungDialogPersister(Options optionen) throws SQLException {
      super(optionen);
   }

   public int getKlientenIdForRechnungId(int rechnungs_id) {
      String sql = "SELECT klienten_id FROM rechnungen WHERE rechnungen_id=?";

      if(logger.isDebugEnabled()) {
         logger.debug("getKlientenIdForRechnungId: " + sql);
      }
      
      int result = 0;
      
      try {
         PreparedStatement stm = verbindung.prepareStatement(sql);
         stm.setInt(1, rechnungs_id);
         ResultSet rs = stm.executeQuery();
         if(rs.next()) {
            result = rs.getInt("klienten_id");
            if(rs.next()) {
               logger.warn("More than one Record found in Database for rechnungen_id=" + rechnungs_id);
            }
         }
      } catch (SQLException e) {
         logger.warn(sql, e);
      }
      return result;
   }
   
   public Builder getRechnungById(int rechnungs_id) {

      String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, betrag, "
            + "pdfdatei, adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, geldeingang, timestamp " + "FROM rechnungen WHERE rechnungen_id=" + rechnungs_id + ";";

      logger.debug("getRechnungById: " + sql);
      Builder rechnungBuilder = new Rechnung.Builder();

      try (ResultSet daten = verbindung.query(sql)) {
         
         if (daten.next()) {
            rechnungBuilder.rechnungen_id(rechnungs_id)
            	.klienten_id(daten.getInt("klienten_id"))
            	.datum(getCalendarValue(daten, "datum"))
            	.rechnungnr(daten.getString("rechnungnr"))
            	.betrag(daten.getDouble("betrag"))
            	.pdfDatei(daten.getString("pdfdatei"))
            	.adresse(daten.getString("adresse"))
            	.zusatz1(daten.getBoolean("zusatz1"))
            	.zusatz2(daten.getBoolean("zusatz2"))
            	.zusammenfassungenErlauben(daten.getBoolean("zusammenfassungen"))
            	.zahldatum(getCalendarValue(daten, "zahldatum"))
            	.geldeingang(getCalendarValue(daten, "geldeingang"));
         }
      } catch (SQLException e) {
         logger.warn(sql, e);
      }
      
      return rechnungBuilder;
   }

	private Calendar getCalendarValue(ResultSet daten, String columnLabel) throws SQLException {
		java.sql.Date date = daten.getDate(columnLabel);
		if(date == null) {
			return null;
		}
		Calendar value = new GregorianCalendar();
		value.setTimeInMillis(date.getTime());
		return value;
	}

   public Vector<Arbeitsstunde> getEinheiten(int rechnungs_id) {
      
      // Vector einheiten wird mit zugehörigen Daten von Vector einheiten_int
      // gefüllt
      String sql = "SELECT DISTINCT einheiten.einheiten_id AS einheiten_id, einheiten.klienten_id AS klienten_id, "
            + "einheiten.angebote_id AS angebote_id, Datum, Beginn, Ende, zusatz1, zusatz2, Preisänderung, Rechnung_verschickt, "
            + "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.Preis AS StundenPreis, angebote.preis_pro_stunde FROM einheiten, "
            + "angebote  WHERE einheiten.angebote_id=angebote.angebote_id AND rechnung_id=? ORDER BY Datum, Preis;";
      logger.debug(sql);
      Vector<Arbeitsstunde> result = new Vector<>();
      try {
         PreparedStatement stm = verbindung.prepareStatement(sql);
        
         stm.setInt(1, rechnungs_id);
         ResultSet daten = stm.executeQuery();

         while (daten.next()) {
            // Wenn Erster Datensatz kann klienten_id gesetzt werden und
            // fortgefahren
            // Wenn folgender Datensatz und Klientenid stimmt nicht überein
            // -> Abbruch
            // if((this.klienten_id == null) || (this.klienten_id ==
            // daten.getInt("klienten_id"))){
            int klienten_id = daten.getInt("klienten_id");
            ArbeitsstundeImpl stunde = new ArbeitsstundeImpl.Builder(daten.getInt("einheiten_id"), klienten_id, daten.getInt("angebote_id")).datum(daten.getDate("Datum"))
                  .inhalt(daten.getString("Inhalt")).beginn(daten.getTimestamp("Beginn")).ende(daten.getTimestamp("Ende")).einzelPreis(daten.getDouble("StundenPreis"))
                  .preis(daten.getDouble("Preis")).zusatz1(daten.getString("zusatz1")).zusatz2(daten.getString("zusatz2")).preisaenderung(daten.getDouble("Preisänderung"))
                  .dauerInMinuten(daten.getInt("Dauer")).build();
            try {
               stunde.setVerschicktDatum(daten.getDate("Rechnung_Datum"));
            } catch (Exception e) {

               logger.debug(sql, e);
               stunde.setVerschicktDatum(null);
            }
            try {
               stunde.setBezahltDatum(daten.getDate("Bezahlt_Datum"));
            } catch (Exception e) {
               logger.debug(sql, e);
               stunde.setBezahltDatum(null);
            }

            result.add(stunde);
         }
      } catch (SQLException e) {
         logger.error(sql, e);
      }
      return result;
   }

   public String getRechnungsnummer(Rechnung r, Klient k) {
      String rechnungsnr = "";

      // Die Rechnungsnummer setzt sich zusammen aus Auftraggebernamen, Jahr
      // und Monat der Rechnung
      String sql = "SELECT Auftraggeber,rechnungnummer_bezeichnung FROM klienten WHERE klienten_id=" + k.getKlienten_id() + ";";
      logger.debug("RechnungDialog:setRechnungsnr:" + sql);
      try (ResultSet auftraggeber = verbindung.query(sql)) {
         

         if (auftraggeber.next()) {
            if (auftraggeber.getString("rechnungnummer_bezeichnung") == null || auftraggeber.getString("rechnungnummer_bezeichnung").isEmpty()) {
               rechnungsnr = auftraggeber.getString("Auftraggeber");
            } else {
               rechnungsnr = auftraggeber.getString("rechnungnummer_bezeichnung");
            }

         } else {
            rechnungsnr = r.getAdresse().substring(0, r.getAdresse().indexOf("\n"));
         }
      } catch (SQLException e) {
         logger.warn(sql, e);
      }
      return rechnungsnr;
   }

   public String checkAndUpdateRechnungNr(String rechnungsnr) {

      String newNr = rechnungsnr;
      String sql = "SELECT rechnungnr FROM rechnungen WHERE rechnungnr LIKE '" + rechnungsnr + "' ORDER BY rechnungnr;";
      logger.debug("RechnungDialog:setRechnungsnr:" + sql);

      // Wenn rechnungsnr bereits existiert wird ein buchstabe angehängt.
      try (ResultSet rs = verbindung.query(sql)) {
         
         char buchstabe = 'a';
         while (rs.next()) {
            buchstabe++;
         }
         if (buchstabe>'a') {
            buchstabe--;
            newNr += buchstabe;   
         }
         
      } catch (SQLException e) {
         logger.warn(sql, e);
      }
      return newNr;
   }

   public Vector<Arbeitsstunde> getEinheitenByIds(Vector<Integer> einheiten) {

      Vector<Arbeitsstunde> data = new Vector<>();

      String where = " einheiten.einheiten_id IN (" + einheiten.elementAt(0);

      for (int i = 1; i < einheiten.size(); i++) {
         where += ", " + einheiten.elementAt(i);
      }
      where += ")";

      String sql = "SELECT DISTINCT einheiten.einheiten_id AS einheiten_id, einheiten.klienten_id AS klienten_id, "
            + "einheiten.angebote_id AS angebote_id, Datum, Beginn, Ende, zusatz1, zusatz2, Preisänderung, Rechnung_verschickt, "
            + "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.Preis AS StundenPreis, " + "angebote.preis_pro_stunde FROM einheiten, "
            + "angebote  WHERE einheiten.angebote_id=angebote.angebote_id AND " + where + " ORDER BY Datum, Preis;";

      logger.debug(sql);

      try (ResultSet daten = verbindung.query(sql)) {
         
         while (daten.next()) {
            // Wenn Erster Datensatz kann klienten_id gesetzt werden und
            // fortgefahren
            // Wenn folgender Datensatz und Klientenid stimmt nicht überein
            // -> Abbruch
            ArbeitsstundeImpl stunde = new ArbeitsstundeImpl.Builder(daten.getInt("einheiten_id"), daten.getInt("klienten_id"), daten.getInt("angebote_id"))
                  .datum(daten.getDate("Datum")).inhalt(daten.getString("Inhalt")).beginn(daten.getTimestamp("Beginn")).ende(daten.getTimestamp("Ende"))
                  .einzelPreis(daten.getDouble("StundenPreis")).preis(daten.getDouble("Preis")).zusatz1(daten.getString("zusatz1")).zusatz2(daten.getString("zusatz2"))
                  .preisaenderung(daten.getDouble("Preisänderung")).dauerInMinuten(daten.getInt("Dauer")).preisProStunde(daten.getBoolean("preis_pro_stunde")).build();

            try {
               stunde.setVerschicktDatum(daten.getDate("Rechnung_Datum"));
            } catch (Exception e) {
               logger.error(daten.getInt("einheiten.einheiten_id") + ": Rechnung Datum nicht gesetzt!", e);
               stunde.setVerschicktDatum(null);
            }
            try {
               stunde.setBezahltDatum(daten.getDate("Bezahlt_Datum"));
            } catch (Exception e) {
               logger.error(daten.getInt("einheiten.einheiten_id") + ": Bezahlt Datum nicht gesetzt!", e);
               stunde.setBezahltDatum(null);
            }

            data.add(stunde);

         }
      } catch (SQLException e) {
         logger.error("", e);
      }

      return data;
   }

   public void insertOrUpdateRechnung(Rechnung rechnung) {

      // Elemente in "einheiten" werden in WHERE-bedinung aufgenommen
      String in_bedingung = "(" + rechnung.getEinheiten().elementAt(0).getID();

      for (int i = 1; i < rechnung.getEinheiten().size(); i++) {
         in_bedingung += ", " + rechnung.getEinheiten().elementAt(i).getID();
      }

      in_bedingung += ")";

      // dann die Rechnung in die Datenbank
      if (rechnung.isNew()) { // Bei neuer Rechnung INSERT

         try {
            PreparedStatement insert1 = verbindung.prepareStatement("INSERT INTO rechnungen "
                  + "(klienten_id, datum, rechnungnr, betrag, pdfdatei, adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, timestamp)"
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insert1.setInt(1, rechnung.getKlienten_id());
            insert1.setDate(2, new java.sql.Date(rechnung.getDatum().getTimeInMillis()));
            insert1.setString(3, rechnung.getRechnungnr());
            insert1.setBigDecimal(4, rechnung.getBetrag());
            insert1.setString(5, rechnung.getPdfdatei());
            insert1.setString(6, rechnung.getAdresse());
            insert1.setInt(7, rechnung.getZusatz1_name() == null?0:1);
            insert1.setInt(8, rechnung.getZusatz2_name() == null?0:1);
            insert1.setInt(9, rechnung.isZusammenfassungenErlauben() ?1:0);

            insert1.setDate(10, new java.sql.Date(rechnung.getZahldatum().getTimeInMillis()));
            insert1.setTimestamp(11, new Timestamp(new Date().getTime()));
            
            insert1.executeUpdate();

            int lastInsertId = 0;
            final ResultSet rs = verbindung.getAutoincrement();
            if (rs.next())
               lastInsertId = rs.getInt(1);
            rs.close();
            rechnung.setRechnungId(lastInsertId);
            
         } catch (SQLException e) {
            logger.error("Error inserting " + rechnung, e);
            return;
         }
         
      } else {
         // Bei vorhandener Rechnung UPDATE

         PreparedStatement update1;
         try {
            update1 = verbindung.prepareStatement(
                  "UPDATE rechnungen SET datum=?, rechnungnr=?, betrag=?, pdfdatei=?, adresse=?, zusatz1=?, zusatz2=?, zusammenfassungen=?, zahldatum=? WHERE rechnungen_id =?");
            update1.setDate(1, new java.sql.Date(rechnung.getDatum().getTimeInMillis()));
            update1.setString(2, rechnung.getRechnungnr());
            update1.setBigDecimal(3, rechnung.getBetrag());
            update1.setString(4, rechnung.getPdfdatei());
            update1.setString(5, rechnung.getAdresse());
            update1.setInt(6, rechnung.getZusatz1_name() == null?0:1);
            update1.setInt(7, rechnung.getZusatz2_name() == null?0:1);
            update1.setBoolean(8, rechnung.isZusammenfassungenErlauben());
            update1.setDate(9, new java.sql.Date(rechnung.getZahldatum().getTimeInMillis()));
            update1.setInt(10, rechnung.getRechnungen_id());
            
            update1.executeUpdate();
         } catch (SQLException e) {
            logger.error("error updateing Rechnung: " + rechnung, e);
            return;
         }

      }

      try {
         PreparedStatement updateEinheiten = verbindung.prepareStatement("UPDATE einheiten SET Rechnung_verschickt=1, Rechnung_Datum=?, rechnung_id=? WHERE einheiten_id IN " + in_bedingung);
         updateEinheiten.setDate(1, new java.sql.Date(rechnung.getDatum().getTimeInMillis()));
         updateEinheiten.setInt(2, rechnung.getRechnungen_id());
         updateEinheiten.executeUpdate();
         
      } catch (SQLException e) {
         logger.warn("Fehler UPDATE einheiten: " + in_bedingung, e);
      }

   }

}
