package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import de.kreth.arbeitsrechnungen.data.*;


public class RechnungDialogPersister extends AbstractPersister {

	public RechnungDialogPersister(Properties optionen) {
		super(optionen);
	}

	public Rechnung getRechnungById(int rechnungs_id) {

		String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, betrag, texdatei, "
				+ "pdfdatei, adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, geldeingang, timestamp "
				+ "FROM rechnungen WHERE rechnungen_id=" + rechnungs_id + ";";

		logger.debug("RechnungDialog: Konstruktor (2): " + sql);
		Rechnung r = new Rechnung(rechnungs_id);

		try {
			ResultSet daten = verbindung.query(sql);
			if (daten.next()) {
				r.setKlienten_id(daten.getInt("klienten_id"));
				Calendar datum = new GregorianCalendar();
				datum.setTimeInMillis(daten.getDate("datum").getTime());
				r.setDatum(datum);
				r.setRechnungnr(daten.getString("rechnungnr"));
				r.setBetrag(daten.getDouble("betrag"));
				r.setTexdatei(daten.getString("texdatei"));
				r.setPdfdatei(daten.getString("pdfdatei"));
				r.setAdresse(daten.getString("adresse"));
				r.setZusatz1(daten.getBoolean("zusatz1"));
				r.setZusatz2(daten.getBoolean("zusatz2"));
				r.setZusammenfassungen(daten.getBoolean("zusammenfassungen"));
				Calendar zahldatum = new GregorianCalendar();
				zahldatum.setTimeInMillis(daten.getDate("zahldatum").getTime());
				r.setZahldatum(zahldatum);
				Calendar geldeingang = new GregorianCalendar();
				geldeingang.setTimeInMillis(daten.getDate("geldeingang")
						.getTime());
				r.setGeldeingang(geldeingang);
			}
		} catch (SQLException e) {
			logger.debug(sql, e);
		}
		return r;
	}

	public Vector<Arbeitsstunde> getEinheiten(int rechnungs_id) {
		String where = " rechnung_id=" + rechnungs_id;
		return getEinheiten(where);
	}

	private Vector<Arbeitsstunde> getEinheiten(String where) {
		// Vector einheiten wird mit zugehörigen Daten von Vector einheiten_int
		// gefüllt
		String sql = "SELECT DISTINCT einheiten.einheiten_id AS einheiten_id, einheiten.klienten_id AS klienten_id, "
				+ "einheiten.angebote_id AS angebote_id, Datum, Beginn, Ende, zusatz1, zusatz2, Preisänderung, Rechnung_verschickt, "
				+ "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.Preis AS StundenPreis, angebote.preis_pro_stunde FROM einheiten, "
				+ "angebote  WHERE einheiten.angebote_id=angebote.angebote_id AND "
				+ where + " ORDER BY Datum, Preis;";
		logger.debug(sql);
		Vector<Arbeitsstunde> result = new Vector<>();
		try {

			ResultSet daten = verbindung.query(sql);
			
			while (daten.next()) {
				// Wenn Erster Datensatz kann klienten_id gesetzt werden und
				// fortgefahren
				// Wenn folgender Datensatz und Klientenid stimmt nicht überein
				// -> Abbruch
				// if((this.klienten_id == null) || (this.klienten_id ==
				// daten.getInt("klienten_id"))){
				int klienten_id = daten.getInt("klienten_id");
				ArbeitsstundeImpl stunde = new ArbeitsstundeImpl(
						daten.getInt("einheiten_id"), klienten_id,
						daten.getInt("angebote_id"));
				stunde.setDatum(daten.getDate("Datum"));
				stunde.setInhalt(daten.getString("Inhalt"));
				stunde.setBeginn(daten.getTimestamp("Beginn"));
				stunde.setEnde(daten.getTimestamp("Ende"));
				stunde.setEinzelPreis(daten.getDouble("StundenPreis"));
				stunde.setPreis(daten.getDouble("Preis"));
				stunde.setZusatz1(daten.getString("zusatz1"));
				stunde.setZusatz2(daten.getString("zusatz2"));
				stunde.setPreisAenderung(daten.getDouble("Preisänderung"));
				stunde.setDauer(daten.getDouble("Dauer"));
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
			e.printStackTrace();
		}
		return result;
	}

	public String getRechnungsnummer(Rechnung r, Klient k) {
		String rechnungsnr = "";

		// Die Rechnungsnummer setzt sich zusammen aus Auftraggebernamen, Jahr
		// und Monat der Rechnung
		String sql = "SELECT Auftraggeber,rechnungnummer_bezeichnung FROM klienten WHERE klienten_id="
				+ k.getKlienten_id() + ";";
		logger.debug("RechnungDialog:setRechnungsnr:" + sql);
		try {
			ResultSet auftraggeber = verbindung.query(sql);
			
			if (auftraggeber.first()) {
				if (auftraggeber.getString("rechnungnummer_bezeichnung") == null
						|| auftraggeber.getString("rechnungnummer_bezeichnung")
								.isEmpty()) {
					rechnungsnr = auftraggeber.getString("Auftraggeber");
				} else {
					rechnungsnr = auftraggeber
							.getString("rechnungnummer_bezeichnung");
				}

			} else {
				rechnungsnr = r.getAdresse().substring(0,
						r.getAdresse().indexOf("\n"));
			}
		} catch (SQLException e) {
			logger.warn(sql, e);
		}
		return rechnungsnr;
	}

	public String checkAndUpdateRechnungNr(String rechnungsnr) {

		String sql = "SELECT rechnungnr FROM rechnungen WHERE rechnungnr LIKE \""
				+ rechnungsnr + "%\" ORDER BY rechnungnr;";
		logger.debug("RechnungDialog:setRechnungsnr:" + sql);

		// Wenn rechnungsnr bereits existiert wird ein buchstabe angehängt.
		try {
			ResultSet rs = verbindung.query(sql);
			if (rs.last()) {
				Character buchstabe = (char) ('a' + rs.getRow() - 1);
				rechnungsnr += buchstabe;
			}
		} catch (SQLException e) {
			logger.warn(sql, e);
		}
		return rechnungsnr;
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
				+ "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.Preis AS StundenPreis, " +
				"angebote.preis_pro_stunde FROM einheiten, "
				+ "angebote  WHERE einheiten.angebote_id=angebote.angebote_id AND "
				+ where + " ORDER BY Datum, Preis;";

		logger.debug(sql);

		try {
			ResultSet daten = verbindung.query(sql);
			while (daten.next()) {
				// Wenn Erster Datensatz kann klienten_id gesetzt werden und
				// fortgefahren
				// Wenn folgender Datensatz und Klientenid stimmt nicht überein
				// -> Abbruch
					ArbeitsstundeImpl stunde = new ArbeitsstundeImpl(
							daten.getInt("einheiten_id"), daten.getInt("klienten_id"),
							daten.getInt("angebote_id"));
					stunde.setDatum(daten.getDate("Datum"));
					stunde.setInhalt(daten.getString("Inhalt"));
					stunde.setBeginn(daten.getTimestamp("Beginn"));
					stunde.setEnde(daten.getTimestamp("Ende"));
					stunde.setEinzelPreis(daten.getDouble("StundenPreis"));
					stunde.setPreis(daten.getDouble("Preis"));
					stunde.setZusatz1(daten.getString("zusatz1"));
					stunde.setZusatz2(daten.getString("zusatz2"));
					stunde.setPreisAenderung(daten.getDouble("Preisänderung"));
					stunde.setDauer(daten.getDouble("Dauer"));
					stunde.setPreisProStunde(daten.getBoolean("preis_pro_stunde"));
					
					try {
						stunde.setVerschicktDatum(daten
								.getDate("Rechnung_Datum"));
					} catch (Exception e) {

						System.err.println(daten
								.getInt("einheiten.einheiten_id")
								+ ": Rechnung Datum nicht gesetzt!");
						// e.printStackTrace();
						stunde.setVerschicktDatum(null);
					}
					try {
						stunde.setBezahltDatum(daten.getDate("Bezahlt_Datum"));
					} catch (Exception e) {
						System.err.println(daten
								.getInt("einheiten.einheiten_id")
								+ ": Bezahlt Datum nicht gesetzt!");
						// e.printStackTrace();
						stunde.setBezahltDatum(null);
					}

					data.add(stunde);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

}
