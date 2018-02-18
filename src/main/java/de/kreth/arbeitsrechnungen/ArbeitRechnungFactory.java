package de.kreth.arbeitsrechnungen;

import de.kreth.arbeitsrechnungen.persister.Persister;

/**
 * @author markus
 */
public abstract class ArbeitRechnungFactory {

   protected static ArbeitRechnungFactory instance = null;

   public static ArbeitRechnungFactory getInstance() {
      if (instance == null)
         throw new NullPointerException(ArbeitRechnungFactory.class.getSimpleName() + " wurde noch nicht initialisiert!!!");
      return instance;
   }

   public abstract Persister getPersister(Class<? extends Persister> clazz, Options optionen);
}
