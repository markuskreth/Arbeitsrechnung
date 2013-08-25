package arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import arbeitsrechnungen.data.Angebot;
import arbeitsrechnungen.data.Klient;

public class KlientenEditorPersister extends AbstractPersister {

	public KlientenEditorPersister(Properties optionen) {
		super(optionen);
	}

	public List<Klient> getAllKlienten() {
		List<Klient> result = new ArrayList<>();
		String sql = "SELECT * FROM klienten;";
		ResultSet rs = verbindung.query(sql);

		logger.debug(sql);
		try {
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
				String rechnungnummer_bezeichnung = rs
						.getString("rechnungnummer_bezeichnung");

				result.add(Klient.createKlient(klientenId, auftraggeber,
						aEmail, aTelefon, aOrt, aPlz, aAdress2, aAdress1,
						kunde, kEmail, kTelefon, kOrt, kPLZ, kAdresse1,
						kAdresse2, bemerkungen, tex_datei, zusatz1,
						zusatz1_Name, zusatz2, zusatz2_Name,
						rechnungnummer_bezeichnung));
			}
		} catch (SQLException e) {
			logger.warn("Fehler bei abfrage klienten tabelle", e);
		}

		return result;
	}
	

    /**
     * Speichert einen einzelnen Wert in der Datenbank. Parameter sind der Name des zu änderndes Feldes und der neue Wert
     * @param klientenId
     * @param Feld
     * @param Wert
     */
    public void speicherWert(int klientenId, String Feld, String Wert) {
        try {
            String sql = "UPDATE klienten SET " + Feld + "=\"" + Wert + "\" WHERE klienten_id=" + klientenId + ";";
            verbindung.sql(sql);
    		logger.debug(sql);
        } catch (Exception e) {
			logger.warn("Fehler bei speichern des Feldes \"" + Feld +
					"\" mit Wert \"" + Wert +
					"\"", e);
        }

    }

	public List<Angebot> getAngeboteForKlient(int klient) {

        String sqltext = "SELECT angebote_id, Inhalt, Preis, preis_pro_stunde, Beschreibung FROM angebote WHERE klienten_id=" + klient;
        logger.debug("updateKlientenTabelle: " + sqltext);
        ResultSet rs = verbindung.query(sqltext);
        List<Angebot> result = new ArrayList<>();

        try {
			while (rs.next()){
			    int angebote_id = rs.getInt("angebote_id");
			    String inhalt = rs.getString("Inhalt");
			    double preis = rs.getDouble("Preis");
			    boolean preis_pro_stunde = rs.getBoolean("preis_pro_stunde");
			    String beschreibung = rs.getString("Beschreibung");
			    result.add(Angebot.createAngebot(angebote_id, inhalt, preis, beschreibung, preis_pro_stunde));
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
        String sql = "INSERT INTO klienten (Auftraggeber, AAdresse1, APLZ, AOrt) " +
                "VALUES (\"" + auftraggeber + "\", \"" + aAdresse1 + "\", \"" + plz + "\", \"" + ort + "\");";
        
        logger.debug(sql);
        
        verbindung.sql(sql);
        
        ResultSet rs = verbindung.query("SELECT LAST_INSERT_ID()");
        try {
			rs.first();
	        int klient_id = rs.getInt(1);
	        k = Klient.createKlient(klient_id, auftraggeber, aAdresse1, plz, ort);
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
        verbindung.sql(sql);
	}

	private void deleteEinheiten(Klient toDelete) {

        String sql = "DELETE FROM einheiten WHERE klienten_id=" + toDelete.getKlienten_id();
        logger.debug(sql);
        verbindung.sql(sql);
	}

	private void deleteRechnungen(Klient toDelete) {

        String sql = "DELETE FROM rechnungen WHERE klienten_id=" + toDelete.getKlienten_id();
        logger.debug(sql);
        verbindung.sql(sql);
	}

	private void deleteKlient(Klient toDelete) {

        String sql = "DELETE FROM klienten WHERE klienten_id=" + toDelete.getKlienten_id();
        logger.debug(sql);
        verbindung.sql(sql);
	}
}
