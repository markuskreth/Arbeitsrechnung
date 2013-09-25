package de.kreth.arbeitsrechnungen.persister;

import java.util.Properties;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_mysql;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * @author markus
 */
public abstract class AbstractPersister {

	protected Logger logger = Logger.getLogger(getClass());
	protected Verbindung verbindung;

	public AbstractPersister(Properties optionen) {
		super();

		verbindung = new Verbindung_mysql(optionen);

		if (verbindung.connected()) {
			logger.info("Connected!");
		} else
			logger.error("Not connected!");
	}

}