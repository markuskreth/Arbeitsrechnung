package de.kreth.arbeitsrechnungen;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.Persister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;
import de.kreth.arbeitsrechnungen.test.MockKlientenEditorPersister;



public class ArbeitRechnungFactoryTestingFakeDB extends ArbeitRechnungFactory {
   
   private ArbeitRechnungFactoryTestingFakeDB() {
      
   }
   
   public static void init(){
      instance = new ArbeitRechnungFactoryTestingFakeDB();
   }

   @Override
   public Persister getPersister(Class<? extends Persister> clazz, Options optionen) {
      if(clazz == KlientenEditorPersister.class)
         return new MockKlientenEditorPersister();
      if(clazz == RechnungDialogPersister.class)
         return new MockRechnungDialogPersister();
      throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...");
   }
   
}
