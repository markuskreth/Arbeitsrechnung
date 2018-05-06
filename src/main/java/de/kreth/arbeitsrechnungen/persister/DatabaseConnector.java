package de.kreth.arbeitsrechnungen.persister;

import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.database.Verbindung;
import de.kreth.arbeitsrechnungen.database.Verbindung_mysql;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * 
 * @author markus
 */
public final class DatabaseConnector {

	private final Logger logger = LoggerFactory.getLogger(getClass());
   private Properties optionen;
	private static Verbindung verbindung = null;

	public DatabaseConnector(Properties optionen) {
		super();
		this.optionen = optionen;
	}

	public static void setVerbindung(Verbindung verbindung) {
		DatabaseConnector.verbindung = verbindung;
	}

	public Verbindung getVerbindung() throws SQLException {
	   connect();
		return verbindung;
	}

   private void connect() throws SQLException {

      if (verbindung == null || verbindung.connected() == false) {

         verbindung = new Verbindung_mysql(optionen);

         if (verbindung.connected()) {
            logger.info("Connected to " + verbindung);
            // checkVersion();
         } else {
            logger.error("Not connected to " + verbindung);
            throw new SQLException("No connection to Database possible: " + verbindung);
         }
      }
   }

}
