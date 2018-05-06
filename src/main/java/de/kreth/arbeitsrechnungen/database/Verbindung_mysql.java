package de.kreth.arbeitsrechnungen.database;

/**
 * @author markus
 */
import java.sql.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Verbindung_mysql extends Verbindung {

	static String URL = "jdbc:mysql://192.168.0.1";

	private Logger logger = LoggerFactory.getLogger(Verbindung_mysql.class);

	private Connection verbindung = null;

	protected Verbindung_mysql(String datenbank, String benutzer, String password) {
		try {
			verbindung = DriverManager.getConnection(URL + "/" + datenbank, benutzer, password);
		} catch (Exception e) {
			logger.error("Verbindung zu " + URL + " konnte nicht hergestellt werden.", e);

		}
	}

	protected Verbindung_mysql(String server, String datenbank, String benutzer, String password) {
		StringBuilder builder = new StringBuilder();
      builder.append("jdbc:mysql://");
      builder.append(server);
      builder.append(":3306/");
      builder.append(datenbank);
      builder.append("?useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin");

      /**
		 * Treiber laden, Verbindung aufbauen und die Tabellen der Datenbank auslesen
		 * und in die tabellenliste speichern.
		 */
		URL = builder.toString();

		// Verbindung aufbauen
		try {
			verbindung = DriverManager.getConnection(URL, benutzer, password);
		} catch (Exception e) {
			logger.error("Verbindung zu " + URL + " konnte nicht hergestellt werden.", e);
		}
	}

	@Override
	public String toString() {
	   StringBuilder txt = new StringBuilder(URL);
	   if(verbindung == null) {
	      txt.append(" (not connected)");
	   } else {
	      boolean closed = true;
	      
	      try {
            closed = verbindung.isClosed();
         } catch (SQLException e) {
            if(logger.isTraceEnabled()) {
               logger.trace("error queriing connection closed status", e);
            }
         }
	      if(closed) {
	         txt.append(" (not connected)");
	      }
	   }
	   return txt.toString();
	}

	public Verbindung_mysql(Properties options) {
		this(options.getProperty("sqlserver"), options.getProperty("datenbank"), options.getProperty("user"),
				options.getProperty("password"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see arbeitsabrechnungendataclass.Verbindung#connected()
	 */
	@Override
	public boolean connected() {
		try {
			return verbindung.isValid(3);
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see arbeitsabrechnungendataclass.Verbindung#query(java.lang.String)
	 */
	@Override
	public ResultSet query(CharSequence sql) {

		ResultSet daten = null;

		try {
			Statement befehl = verbindung.createStatement();
			logger.debug("Executing " + sql);
			daten = befehl.executeQuery(sql.toString());
		} catch (Exception e) {
			logger.error("Fehler in Abfrage: " + sql, e);
		}

		return daten;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see arbeitsabrechnungendataclass.Verbindung#sql(java.lang.String)
	 */
	@Override
	public boolean sql(CharSequence sql) {
		boolean ok = false;
		try {

			Statement befehl = verbindung.createStatement();
			logger.debug("Executing " + sql);
			befehl.execute(sql.toString());
			ok = true;

		} catch (Exception e) {
			logger.error("Fehler in Abfrage: " + sql, e);
		}
		return ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see arbeitsabrechnungendataclass.Verbindung#close()
	 */
	@Override
	public void close() {
		try {
		   if(logger.isTraceEnabled()) {
		      logger.trace("Closing " + verbindung);
		   }
			verbindung.close();
		} catch (Exception e) {
			logger.error("Fehler in close ", e);
		}
	}

	@Override
	public ResultSet getAutoincrement() throws SQLException {
		return verbindung.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
	}
}
