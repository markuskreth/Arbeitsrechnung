package de.kreth.arbeitsrechnungen.persister;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.database.Verbindung;

public class AbstractPersister implements Persister {

   protected final Logger logger = LoggerFactory.getLogger(getClass());
   protected final Verbindung verbindung;

   public AbstractPersister(Options optionen) throws SQLException {
      super();
      verbindung = connectToDb(optionen);
   }

   @Override
   public Verbindung connectToDb(Options optionen) throws SQLException {
      Verbindung vb = Verbindung.getVerbindung(optionen.getProperties());
      return vb;
   }

   protected void debugLogSql(String sqltext) {
      logger.debug(Thread.currentThread().getStackTrace()[2].getMethodName() + ": " + sqltext);
   }

   @Override
   public void close() {
      verbindung.close();
   }

}
