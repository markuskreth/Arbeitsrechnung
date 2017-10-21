package de.kreth.arbeitsrechnungen.persister;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import de.kreth.arbeitsrechnungen.Options;

public class AbstractPersister implements Persister {

   protected final Logger logger = LogManager.getLogger(getClass());
   protected final Verbindung verbindung;

   public AbstractPersister(Options optionen) {
      super();
      verbindung = connectToDb(optionen);
   }

   @Override
   public Verbindung connectToDb(Options optionen) {
      return new DatabaseConnector(optionen.getProperties()).getVerbindung();
   }

   protected void debugLogSql(String sqltext) {
      logger.debug(Thread.currentThread().getStackTrace()[2].getMethodName() + ": " + sqltext);
   }

}
