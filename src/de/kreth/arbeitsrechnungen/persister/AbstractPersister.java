package de.kreth.arbeitsrechnungen.persister;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import de.kreth.arbeitsrechnungen.Options;

public class AbstractPersister implements Persister {

   protected final Logger logger = Logger.getLogger(getClass());
   protected final Verbindung verbindung;

   public AbstractPersister(Options optionen) {
      super();
      verbindung = connectToDb(optionen);
   }

   @Override
   public Verbindung connectToDb(Options optionen) {
      return new DatabaseConnector(optionen.getProperties()).getVerbindung();
   }

}
