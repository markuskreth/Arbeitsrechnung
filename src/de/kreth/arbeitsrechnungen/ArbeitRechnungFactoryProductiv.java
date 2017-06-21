package de.kreth.arbeitsrechnungen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.Persister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;

/**
 * Stellt die Produktive ArbeitRechnungFactory zur Verfügung.
 * <P>
 * {@link #init()} initialisiert {@link ArbeitRechnungFactory} nur, wenn es
 * nicht bereits initialisiert ist!
 * 
 * @author markus
 */
public class ArbeitRechnungFactoryProductiv extends ArbeitRechnungFactory {

   private Logger logger;

   protected ArbeitRechnungFactoryProductiv() {

      logger = LogManager.getLogger(getClass());
   }

   public static void init() {
      if (instance == null)
         instance = new ArbeitRechnungFactoryProductiv();
   }

   @Override
   public Persister getPersister(Class<? extends Persister> clazz, Options optionen) {
      if (clazz == KlientenEditorPersister.class)
         return new KlientenEditorPersister(optionen);
      if (clazz == RechnungDialogPersister.class)
         return new RechnungDialogPersister(optionen);
      throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...");
   }

}
