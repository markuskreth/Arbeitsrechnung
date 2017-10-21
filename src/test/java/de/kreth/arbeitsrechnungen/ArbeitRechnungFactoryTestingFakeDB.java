package de.kreth.arbeitsrechnungen;

import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.Persister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;
import de.kreth.arbeitsrechnungen.test.MockKlientenEditorPersister;

public class ArbeitRechnungFactoryTestingFakeDB extends ArbeitRechnungFactory {

   public MockRechnungDialogPersister rechnungDialogPersister = null;
   public KlientenEditorPersister klientenEditorPersister = null;
   
   
   private ArbeitRechnungFactoryTestingFakeDB() {
   }

   public static ArbeitRechnungFactoryTestingFakeDB init() {
      ArbeitRechnungFactoryTestingFakeDB arbeitRechnungFactoryTestingFakeDB = new ArbeitRechnungFactoryTestingFakeDB();
      instance = arbeitRechnungFactoryTestingFakeDB;
      return arbeitRechnungFactoryTestingFakeDB;
   }

   @Override
   public Persister getPersister(Class<? extends Persister> clazz, Options optionen) {
      if (clazz == KlientenEditorPersister.class) {
         if(klientenEditorPersister != null) {
            return klientenEditorPersister;
         } else {
            return new MockKlientenEditorPersister();
         }
      }
      if (clazz == RechnungDialogPersister.class) {
         if(rechnungDialogPersister != null) {
            return rechnungDialogPersister;
         } else {
            return new MockRechnungDialogPersister();
         }
      }
      throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...");
   }

}
