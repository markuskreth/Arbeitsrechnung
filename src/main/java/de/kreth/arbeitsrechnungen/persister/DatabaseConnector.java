package de.kreth.arbeitsrechnungen.persister;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_mysql;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * 
 * @author markus
 */
public final class DatabaseConnector {

   private Logger logger = LogManager.getLogger(getClass());
   private Verbindung verbindung;

   public DatabaseConnector(Properties optionen) {
      super();

      verbindung = new Verbindung_mysql(optionen);

      if (verbindung.connected()) {
         logger.info("Connected to " + verbindung);
      } else
         logger.error("Not connected to " + verbindung);
   }

   public Verbindung getVerbindung() {
      return verbindung;
   }

}
