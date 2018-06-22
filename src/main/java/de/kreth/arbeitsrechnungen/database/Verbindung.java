package de.kreth.arbeitsrechnungen.database;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hsqldb.jdbc.jdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.Options;

public abstract class Verbindung {

	public enum ConnectionTypes {
		HSQLDB, MYSQL
	}

	private static Verbindung verbindung = null;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DBTYPE = "dbtype";

	public abstract boolean connected();

	/**
	 * Die Sql-Abfrage gibt den ResultSet zurück.
	 * 
	 * @param sql
	 * @return Ergebnis der Abfrage
	 * @throws SQLException
	 */
	public abstract ResultSet query(CharSequence sql) throws SQLException;

	public abstract PreparedStatement prepareStatement(CharSequence sql) throws SQLException;
	
	/**
	 * Führt den sqlbefehl aus und liefert bei erfolg true zurück.
	 * 
	 * @param sql
	 * @return befehl ausgeführt oder nicht?
	 */
	public abstract boolean sql(CharSequence sql) throws SQLException;

	public abstract void close();

	/**
	 * stellt eine Verbindung her
	 * 
	 * @param options
	 * @return {@link Verbindung} passend zu den Einstellungen in options
	 */
	public static Verbindung getVerbindung(Properties options) {

	   if (verbindung != null) {
	      return verbindung;
	   }
	   
		if (isOldConnectionData(options)) {
			verbindung = new Verbindung_mysql(options);
		} else if (options.containsKey(DBTYPE)) {

			ConnectionTypes type = ConnectionTypes.valueOf(options.getProperty(DBTYPE));

			switch (type) {
			case HSQLDB:
			   jdbcDataSource ds = new jdbcDataSource();
			   File dir = new File(System.getProperty("user.home"), Options.BENUTZERVERZEICHNIS);
			   File dbFile = new File(dir, "hsqlDatabase");
			   ds.setDatabase("jdbc:hsqldb:" + dbFile.getAbsolutePath());
				verbindung = new Verbindung_HsqlCreator(ds);
				break;
			case MYSQL:
				verbindung = new Verbindung_mysql(options);
				break;
			default:
				verbindung = null;
				break;

			}
		}

		return verbindung;
	}

	private static boolean isOldConnectionData(Properties options) {
		return options.containsKey("sqlserver") && options.containsKey("datenbank") && options.containsKey("user")
				&& options.containsKey("password");
	}

	public abstract ResultSet getAutoincrement() throws SQLException;

}