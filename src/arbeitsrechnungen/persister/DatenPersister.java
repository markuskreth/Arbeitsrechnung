package arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class DatenPersister extends AbstractPersister {
	
	public DatenPersister(Properties optionen) {
		super(optionen);
	}

	public List<Forderung> getForderungen() {
		String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, einheiten.Rechnung_Datum AS datum, SUM(einheiten.Preis) AS summe "
				+ "FROM einheiten, klienten "
				+ "WHERE einheiten.klienten_id = klienten.klienten_id "
				+ "AND NOT (ISNULL( einheiten.Rechnung_verschickt )) "
				+ "AND ISNULL( einheiten.Bezahlt ) "
				+ "GROUP BY einheiten.Rechnung_Datum,einheiten.klienten_id "
				+ "ORDER BY einheiten.Rechnung_Datum;";

		ResultSet daten = verbindung.query(sqltext);
		List<Forderung> result = new Vector<>();
		
		try {
			while (daten.next()){
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
	
	public List<Einheit> getEinheiten(){
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
				f.id = daten.getInt("id");
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
		@Override
		public String toString() {
			return auftraggeber + "(" + anzahl + "): " + klientenpreis + " €";
		}
	}

}