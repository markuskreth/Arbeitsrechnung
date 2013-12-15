package de.kreth.arbeitsrechnungen.persister;

import java.util.Properties;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_mysql;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * @author markus
 */
public final class DatabaseConnector {

	private Logger logger = Logger.getLogger(getClass());
	private Verbindung verbindung;

	public DatabaseConnector(Properties optionen) {
		super();

		verbindung = new Verbindung_mysql(optionen);

		if (verbindung.connected()) {
			logger.info("Connected!");
		} else
			logger.error("Not connected!");
	}

   public Verbindung getVerbindung() {
      return verbindung;
   }
   
}