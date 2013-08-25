package arbeitsrechnungen.persister;

import java.util.Properties;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * @author markus
 */
public abstract class AbstractPersister {

	protected Logger logger = Logger.getLogger(getClass());
	protected Verbindung verbindung;

	public AbstractPersister(Properties optionen) {
		super();

		verbindung = new Verbindung(
				optionen.getProperty("sqlserver"),
				optionen.getProperty("datenbank"),
				optionen.getProperty("user"), optionen.getProperty("password"));

		if (verbindung.connected()) {
			logger.info("Connected!");
		} else
			logger.error("Not connected!");
	}

}