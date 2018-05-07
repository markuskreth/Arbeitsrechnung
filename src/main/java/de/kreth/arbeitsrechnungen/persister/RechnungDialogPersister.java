package de.kreth.arbeitsrechnungen.persister;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.*;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;

public class RechnungDialogPersister extends AbstractPersister {

   private final DateFormat sqlDateFormat;

   public RechnungDialogPersister(Options optionen) throws SQLException {
      super(optionen);
      sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

      String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, betrag, texdatei, "
            + "pdfdatei, adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, geldeingang, timestamp " + "FROM rechnungen WHERE rechnungen_id=" + rechnungs_id + ";";

      logger.debug("getRechnungById: " + sql);
      Builder rechnungBuilder = new Rechnung.Builder();

      try {
         ResultSet daten = verbindung.query(sql);
         if (daten.next()) {
            rechnungBuilder.rechnungen_id(rechnungs_id)
            	.klienten_id(daten.getInt("klienten_id"))
            	.datum(getCalendarValue(daten, "datum"))
            	.rechnungnr(daten.getString("rechnungnr"))
            	.betrag(daten.getDouble("betrag"))
            	.texdatei(daten.getString("texdatei"))
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
      try {
         ResultSet auftraggeber = verbindung.query(sql);

         if (auftraggeber.first()) {
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
      String sql = "SELECT rechnungnr FROM rechnungen WHERE rechnungnr LIKE \"" + rechnungsnr + "%\" ORDER BY rechnungnr;";
      logger.debug("RechnungDialog:setRechnungsnr:" + sql);

      // Wenn rechnungsnr bereits existiert wird ein buchstabe angehängt.
      try {
         ResultSet rs = verbindung.query(sql);
         if (rs.last()) {
            char buchstabe = (char) ('a' + rs.getRow() - 1);
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

      try {
         ResultSet daten = verbindung.query(sql);
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

      StringBuilder sql = new StringBuilder();

      // dann die Rechnung in die Datenbank
      if (rechnung.isNew()) { // Bei neuer Rechnung INSERT
         sql.append("INSERT INTO rechnungen (klienten_id, datum, rechnungnr, betrag, texdatei, pdfdatei, adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, timestamp)")
               .append("VALUES (").append(rechnung.getKlienten_id()).append(", \"").append(sqlDateFormat.format(rechnung.getDatum().getTime())).append("\", \"")
               .append(rechnung.getRechnungnr()).append("\", ").append(rechnung.getBetrag().toPlainString()).append(", \"").append(rechnung.getTexdatei()).append("\", \"")
               .append(rechnung.getPdfdatei()).append("\", \"").append(rechnung.getAdresse()).append("\", ");

         if (rechnung.getZusatz1_name() == null)
            sql.append("0");
         else
            sql.append("1");

         if (rechnung.getZusatz2_name() == null)
            sql.append(", 0");
         else
            sql.append(", 1");

         if (rechnung.isZusammenfassungenErlauben())
            sql.append(", 1");
         else
            sql.append(", 0");

         sql.append(", \"").append(sqlDateFormat.format(rechnung.getZahldatum().getTime())).append("\" , \"" + sqlDateFormat.format(new Date()) + "\");");

      } else {
         // Bei vorhandener Rechnung UPDATE
         sql.append("UPDATE rechnungen SET").append(" datum=\"").append(sqlDateFormat.format(rechnung.getDatum().getTime())).append("\", rechnungnr=\"")
               .append(rechnung.getRechnungnr()).append("\", betrag=").append(rechnung.getBetrag().toPlainString()).append(", texdatei=\"").append(rechnung.getTexdatei())
               .append("\", pdfdatei=\"").append(rechnung.getPdfdatei()).append("\", adresse=\"").append(rechnung.getAdresse());

         if (rechnung.getZusatz1_name() == null)
            sql.append("\", zusatz1=\"0");
         else
            sql.append("\", zusatz1=\"1");

         if (rechnung.getZusatz2_name() == null)
            sql.append("\", zusatz2=\"0");
         else
            sql.append("\", zusatz2=\"1");

         sql.append("\", zusammenfassungen=").append(rechnung.isZusammenfassungenErlauben());

         sql.append(", zahldatum=\"").append(sqlDateFormat.format(rechnung.getZahldatum().getTime()));
         sql.append("\" WHERE rechnungen_id =").append(rechnung.getRechnungen_id());

      }
      logger.debug("Rechnung speichern: " + sql);
      try {
         if (verbindung.sql(sql.toString())) {
            int lastInsertId = 0;
            final ResultSet rs = verbindung.query("SELECT LAST_INSERT_ID() AS id");
            if (rs.next())
               lastInsertId = rs.getInt("id");
            rs.close();
            sql.setLength(0);
            rechnung.setRechnungId(lastInsertId);

            // Elemente in "einheiten" werden in WHERE-bedinung aufgenommen
            String in_bedingung = "(" + rechnung.getEinheiten().elementAt(0).getID();

            for (int i = 1; i < rechnung.getEinheiten().size(); i++) {
               in_bedingung += ", " + rechnung.getEinheiten().elementAt(i).getID();
            }

            in_bedingung += ")";

            // Rechnung_verschickt, Rechnungsdatum und rechnung_id bei
            // einheiten ändern
            if (rechnung.isNew()) {
               logger.debug("LAST_INSERT_ID wird benutzt...");
               sql.append("UPDATE einheiten SET Rechnung_verschickt=1, " + "Rechnung_Datum=\"").append(sqlDateFormat.format(rechnung.getDatum().getTime())).append("\", ")
                     .append("rechnung_id=").append(lastInsertId);
               sql.append(" WHERE einheiten_id IN ");
            } else {
               sql.append("UPDATE einheiten SET Rechnung_verschickt=1, " + "Rechnung_Datum=\"").append(sqlDateFormat.format(rechnung.getDatum().getTime())).append("\", ")
                     .append("rechnung_id=").append((rechnung.getRechnungen_id()));
               sql.append(" WHERE einheiten_id IN ");
            }
            sql.append(in_bedingung).append(";");
            logger.debug("UPDATE einheiten: " + sql);
            verbindung.sql(sql);
         }
      } catch (SQLException e) {
         logger.warn("UPDATE einheiten: " + sql, e);
      }

   }

}
