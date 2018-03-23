package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.kreth.arbeitsrechnungen.MySqlDate;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.persister.KlientPersister.Auftraggeber;

public class AngebotPersister extends AbstractPersister {

	public AngebotPersister(Options optionen) {
		super(optionen);
	}
	
	public void storeEinheit(Auftraggeber klient, Einheit einheit) throws SQLException {

		String sqlBeginn = new MySqlDate(einheit.getBeginn()).getSqlDate();
		String sqlEnde = new MySqlDate(einheit.getEnde()).getSqlDate();

		String datum = new MySqlDate(einheit.getDatum()).getSqlDate();

		String bezahltDatum = "NULL";
		int isBezahlt = 0;
		if (einheit.getBezahltDatum() != null) {
			MySqlDate tmpdate;
			logger.debug("Bezahlt: " + DateFormat.getDateInstance().format(einheit.getBezahltDatum()));
			tmpdate = new MySqlDate(einheit.getBezahltDatum());
			bezahltDatum = tmpdate.getSqlDate();
			isBezahlt = 1;
		}

		String eingereichtDatum = "NULL";
		int isEingereicht = 0;
		if (einheit.getRechnungDatum() != null) {
			MySqlDate tmpdate;
			if(logger.isDebugEnabled()) {
				logger.debug("Eingereicht: " + DateFormat.getDateInstance().format(einheit.getRechnungDatum()));
			}
			tmpdate = new MySqlDate(einheit.getRechnungDatum());
			eingereichtDatum = tmpdate.getSqlDate();
			isEingereicht = 1;
		}
		storeEinheit(klient.getKlientId(), einheit.getId(), einheit.getPreis(), einheit.getDauerInMinutes(), datum, 
				eingereichtDatum, isEingereicht, bezahltDatum, isBezahlt, sqlBeginn, sqlEnde, 
				einheit.getAngebotId(), einheit.getZusatz1(), einheit.getZusatz2(), String.valueOf(einheit.getPreisAenderung()));
	}

	void storeEinheit(int klient, int einheit, double preis, long dauer, String datum, String eingereichtDatum,
			int isEingereicht, String bezahltDatum, int isBezahlt, String sqlBeginn, String sqlEnde, int angebot_id,
			String zusatz1, String zusatz2, String preisAenderung) throws SQLException {
		String sqltext;

		if (einheit == -1) {
			if ((isEingereicht != 0) && (isBezahlt != 0)) {
				sqltext = "INSERT INTO einheiten "
						+ "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
						+ "Rechnung_verschickt,Rechnung_Datum,Bezahlt,Bezahlt_Datum,Preisänderung) VALUES " + "("
						+ klient + "," + angebot_id + ",\"" + datum + "\",\"" + sqlBeginn + "\",\"" + zusatz1 + "\",\""
						+ zusatz2 + "\",\"" + preis + "\",\"" + dauer + "\",\"" + isEingereicht + "\",\""
						+ eingereichtDatum + "\",\"" + isBezahlt + "\",\"" + bezahltDatum + "\",\"" + preisAenderung
						+ "\");";
			} else if (isEingereicht != 0) {
				sqltext = "INSERT INTO einheiten "
						+ "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
						+ "Rechnung_verschickt,Rechnung_Datum,Preisänderung) VALUES " + "(" + klient + "," + angebot_id
						+ ",\"" + datum + "\",\"" + sqlBeginn + "\",\"" + sqlEnde + "\",\"" + zusatz1.trim() + "\",\""
						+ zusatz2.trim() + "\",\"" + preis + "\",\"" + dauer + "\",\"" + isEingereicht + "\",\""
						+ eingereichtDatum + "\",\"" + preisAenderung.trim() + "\");";
			} else {
				sqltext = "INSERT INTO einheiten "
						+ "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer,"
						+ "Preisänderung) VALUES " + "(" + klient + "," + angebot_id + ",\"" + datum + "\",\""
						+ sqlBeginn + "\",\"" + sqlEnde + "\",\"" + zusatz1.trim() + "\",\"" + zusatz2.trim() + "\",\""
						+ preis + "\",\"" + dauer + "\",\"" + preisAenderung + "\");";
			}
		} else {
			sqltext = "UPDATE einheiten set " + "angebote_id=" + angebot_id + ",Datum=\"" + datum + "\"" + ",Beginn=\""
					+ sqlBeginn + "\",Ende=\"" + sqlEnde + "\",zusatz1=\"" + zusatz1.trim() + "\"" + ",zusatz2=\""
					+ zusatz2.trim() + "\"" + ",Preis=" + preis + ",Dauer=" + dauer;
			if (isEingereicht != 0) {
				sqltext = sqltext + ",Rechnung_verschickt=\"" + isEingereicht + "\"" + ",Rechnung_Datum=\""
						+ eingereichtDatum + "\"";
			} else {
				sqltext = sqltext + ",Rechnung_verschickt=NULL" + ",Rechnung_Datum=NULL";
			}
			if ((isEingereicht != 0) && (isBezahlt != 0)) {
				sqltext = sqltext + ",Bezahlt=\"" + isBezahlt + "\"" + ",Bezahlt_Datum=\"" + bezahltDatum + "\"";
			} else {
				sqltext = sqltext + ",Bezahlt=NULL" + ",Bezahlt_Datum=NULL";
			}
			sqltext = sqltext + " ,Preisänderung=" + preisAenderung + " WHERE einheiten_id=" + einheit + ";";

		}

		if(logger.isInfoEnabled()) {
			logger.info("Einheit_einzel.jButton2ActionPerformed: \n" + sqltext);
		}

		verbindung.sql(sqltext);
	}

	public Angebot getAngebot(int angebotId) {
		ResultSet rs;
		// sqltext = "SELECT Preis, preis_pro_stunde FROM angebote WHERE angebote_id=" +
		// angebot_id + ";";

		String sqltext = "SELECT angebote_id, klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung  FROM angebote WHERE angebote_id="
				+ angebotId + ";";

		logger.debug("Sql: " + sqltext);

		Angebot angebot = null;

		try {
			rs = verbindung.query(sqltext);

			rs.first();
			String inhalt = rs.getString("Inhalt");
			float preis = rs.getFloat("Preis");
			String beschr = rs.getString("Beschreibung");
			boolean preisPHour = rs.getInt("preis_pro_stunde") == 1;

			angebot = new Angebot.Builder(inhalt, preis).angebotId(angebotId).beschreibung(beschr)
					.preis_pro_stunde(preisPHour).build();
		} catch (Exception e) {
			logger.error("Unable to fetch angebot with id " + angebotId, e);
		}
		return angebot;
	}

	public Angebot insertOrUpdateAngebot(int klientId, Angebot angebot) {
		String sql;
		if (angebot.getAngebote_id() < 0)
			sql = insertAngebot(klientId, angebot);
		else
			sql = updateAngebot(klientId, angebot);

		if(logger.isDebugEnabled()) {
			logger.debug("insertOrUpdateAngebot:" + sql);
		}
		try {
			verbindung.sql(sql);
			if (angebot.getAngebote_id() < 0) {
				ResultSet id = verbindung.getAutoincrement();
				if (id.next()) {
					angebot = new Angebot.Builder(angebot.getInhalt(), angebot.getPreis())
							.beschreibung(angebot.getBeschreibung())
							.preis_pro_stunde(angebot.isPreis_pro_stunde())
							.angebotId(id.getInt(1))
							.build();
				}
			}
		} catch (SQLException e) {
			logger.error("insertOrUpdateAngebot for klientId=" + klientId + "; Angebot=" + angebot, e);
		}
		return angebot;
	}

	private String insertAngebot(int klientId, Angebot angebot) {
		String sqltext;

		sqltext = "INSERT INTO angebote (klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung) " + "VALUES ("
				+ klientId + "," + "\"" + angebot.getInhalt() + "\"," + angebot.getPreis() + ","
				+ (angebot.isPreis_pro_stunde() ? 1 : 0) + ", " + "\"" + angebot.getBeschreibung() + "\");";

		return sqltext;
	}

	private String updateAngebot(int klientId, Angebot angebot) {

		String sqltext;

		sqltext = "UPDATE angebote SET " + "Inhalt=\"" + angebot.getInhalt() + "\", " + "Preis=" + angebot.getPreis()
				+ ", " + "Beschreibung=\"" + angebot.getBeschreibung() + "\", " + "preis_pro_stunde="
				+ (angebot.isPreis_pro_stunde() ? 1 : 0) + " WHERE angebote_id=" + angebot.getAngebote_id() + ";";
		return sqltext;
	}

	public List<Angebot> getForKlient(int klient) {
		String sqltext = "SELECT * FROM angebote WHERE klienten_id=" + klient + ";";
		logger.debug("Einheit_einzel.initangebote: " + sqltext);
		List<Angebot> result = new ArrayList<>();

		try {
			ResultSet daten = verbindung.query(sqltext);
			while (daten.next()) {
				Angebot a = new Angebot.Builder(daten.getString("Inhalt"), daten.getFloat("Preis"))
						.beschreibung(daten.getString("Beschreibung"))
						.preis_pro_stunde(daten.getBoolean("preis_pro_stunde")).angebotId(daten.getInt("angebote_id"))
						.build();
				result.add(a);
				logger.trace("added to AngebotList: " + a);
			}
		} catch (Exception e) {
			logger.error("Einheit_einzel.initangebote: ", e);
		}
		return result;
	}

}
