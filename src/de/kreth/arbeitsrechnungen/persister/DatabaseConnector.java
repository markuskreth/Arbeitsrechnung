package de.kreth.arbeitsrechnungen.persister;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_HsqlCreator;
import de.kreth.hsqldbcreator.HsqlCreator;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * 
 * @author markus
 */
public final class DatabaseConnector {

   private Logger logger = LoggerFactory.getLogger(getClass());
   private Verbindung verbindung;

   public DatabaseConnector(Properties optionen) {
      super();

      verbindung = new Verbindung_HsqlCreator(HsqlCreator.getInstance());
//      verbindung = new Verbindung_mysql(optionen);

      if (verbindung.connected()) {
         logger.info("Connected to " + verbindung);
      } else
         logger.error("Not connected to " + verbindung);
   }

   public Verbindung getVerbindung() {
      return verbindung;
   }

}
