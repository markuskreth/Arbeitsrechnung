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

	public DatabaseConnector(Properties optionen) {
		super();
		this.optionen = optionen;
	}

	public Verbindung getVerbindung() throws SQLException {
	   return connect();
	}

   private Verbindung connect() throws SQLException {

      Verbindung_mysql verbindung = new Verbindung_mysql(optionen);

      if (verbindung.connected()) {
         if(logger.isDebugEnabled()) {
            logger.debug("Connected to " + verbindung);
         }
         // checkVersion();
      } else {
         logger.error("Not connected to " + verbindung);
         throw new SQLException("No connection to Database possible: " + verbindung);
      }
      return verbindung;
   }

}
