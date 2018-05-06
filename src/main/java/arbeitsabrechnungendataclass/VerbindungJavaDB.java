/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arbeitsabrechnungendataclass;

/**
 * @author markus
 */
import java.sql.*;

public class VerbindungJavaDB extends Verbindung {

	static String treiber = "org.apache.derby.jdbc.ClientDriver";
	static String URL = "jdbc:derby://localhost:1527/arbeitrechnungenData";

	// static String datenbank = "Arbeitrechnungen";
	Statement befehl = null;
	Connection verbindung = null;

	public VerbindungJavaDB(String datenbank, String benutzer, String password) {
		/**
		 * Treiber laden, Verbindung aufbauen und die Tabellen der Datenbank auslesen
		 * und in die tabellenliste speichern.
		 */

		// Treiber laden
		try {
			Class.forName(treiber).newInstance();
		} catch (Exception e) {
			logger.error("JDBC/Derby-Treiber konnte nicht geladen werden!", e);

			return;
		}
		// Verbindung aufbauen
		try {
			verbindung = DriverManager.getConnection(URL + "/" + datenbank, benutzer, password);
			befehl = verbindung.createStatement();
		} catch (Exception e) {
			logger.error("Verbindung zu " + URL + " konnte nicht hergestellt werden.", e);
		}
	}

	public VerbindungJavaDB(String server, String datenbank, String benutzer, String password) {
		/**
		 * Treiber laden, Verbindung aufbauen und die Tabellen der Datenbank auslesen
		 * und in die tabellenliste speichern.
		 */
		String URL2 = "jdbc:derby://" + server;

		// Treiber laden
		try {
			Class.forName(treiber).newInstance();
		} catch (Exception e) {
			logger.error("JDBC/Derby-Treiber konnte nicht geladen werden!", e);
			return;
		}

		// Verbindung aufbauen
		try {
			verbindung = DriverManager.getConnection(URL2 + "/" + datenbank, benutzer, password);
			befehl = verbindung.createStatement();
		} catch (Exception e) {
			logger.error("Verbindung zu " + URL2 + " konnte nicht hergestellt werden.", e);
		}
	}

	@Override
	public boolean connected() {
		try {
			return verbindung.isValid(3);
		} catch (Exception e) {
			logger.warn("param 3", e);
			return false;
		}
	}

	@Override
	public ResultSet query(CharSequence sqltext) throws SQLException {
		ResultSet daten = null;
		try {
			logger.debug("executing: " + sqltext);
			daten = befehl.executeQuery(sqltext.toString());
		} catch (Exception e) {
			logger.error("queriing: " + sqltext, e);
		}
		return daten;
	}

	@Override
	public boolean sql(CharSequence sql) throws SQLException {
		boolean ok = false;
		try {
			logger.debug("executing: " + sql);
			befehl.execute(sql.toString());
			ok = true;
		} catch (Exception e) {
			logger.error("queriing: " + sql, e);
		}
		return ok;
	}

	@Override
	public void close() {
		try {
			verbindung.close();
		} catch (Exception e) {
			logger.error("close failed: ", e);
		}
	}

	@Override
	public ResultSet getAutoincrement() throws SQLException {
		throw new UnsupportedOperationException();
	}

}
