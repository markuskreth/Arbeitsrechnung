package arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;

public class DatenPersister {
	Logger logger = Logger.getLogger(getClass());
	
	private Verbindung verbindung;
	
	public DatenPersister(Properties optionen) {

		verbindung = new Verbindung(
				optionen.getProperty("sqlserver"),
				optionen.getProperty("datenbank"),
				optionen.getProperty("user"), optionen.getProperty("password"));

		if (verbindung.connected()) {
			logger.info("Connected!");
		} else
			logger.error("Not connected!");
	}

	public Vector<Forderung> getForderungen(){

		String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, einheiten.Rechnung_Datum AS datum, SUM(einheiten.Preis) AS summe "
				+ "FROM einheiten, klienten "
				+ "WHERE einheiten.klienten_id = klienten.klienten_id "
				+ "AND NOT (ISNULL( einheiten.Rechnung_verschickt )) "
				+ "AND ISNULL( einheiten.Bezahlt ) "
				+ "GROUP BY einheiten.Rechnung_Datum,einheiten.klienten_id "
				+ "ORDER BY einheiten.Rechnung_Datum;";

		logger.debug(sqltext);

		ResultSet daten = verbindung.query(sqltext);
		Vector<Forderung> result = new Vector<>();
		
		try {
			while(daten.next()){
				Forderung f = new Forderung();
				f.auftraggeber = daten.getString("auftraggeber");
				Calendar dat = new GregorianCalendar();
				dat.setTime(daten.getDate("datum"));
				f.datum = dat;
				f.summe = daten.getDouble("summe");
				f.id = daten.getInt("id");
				result.add(f);
			}
		} catch (SQLException e) {
			logger.error(null, e);
		}
		
		return result;
	}
	
	public Vector<Einheit> getEinheiten(){
		String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, COUNT(einheiten.Preis) AS anzahl, SUM(einheiten.Preis) AS klientpreis "
				+ "FROM einheiten, klienten "
				+ "WHERE einheiten.klienten_id = klienten.klienten_id "
				+ "AND ISNULL( einheiten.Rechnung_verschickt ) "
				+ "AND ISNULL( einheiten.Bezahlt ) "
				+ "GROUP BY einheiten.klienten_id " + "ORDER BY klientpreis;";
		
		logger.debug(sqltext);
		
		ResultSet daten = verbindung.query(sqltext);
		Vector<Einheit> result = new Vector<>();
		
		try {
			while (daten.next()) {
				Einheit f = new Einheit();
				f.auftraggeber = daten.getString("auftraggeber");
				f.anzahl = daten.getInt("anzahl");
				f.klientenpreis = daten.getDouble("klientpreis");
				result.add(f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public class Forderung{
		public int id;
		String auftraggeber;
		Calendar datum;
		double summe;
		public String getAuftraggeber() {
			return auftraggeber;
		}
		public Calendar getDatum() {
			return datum;
		}
		public double getSumme() {
			return summe;
		}
		public int getId() {
			return id;
		}
		
	}
	
	public class Einheit{
		String auftraggeber;
		int anzahl;
		double klientenpreis;
		int id;
		public String getAuftraggeber() {
			return auftraggeber;
		}
		public int getAnzahl() {
			return anzahl;
		}
		public double getKlientenpreis() {
			return klientenpreis;
		}
		public int getId() {
			return id;
		}
		
	}
}
