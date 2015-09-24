package de.kreth.arbeitsrechnungen;

import java.util.Vector;

import arbeitsabrechnungendataclass.Verbindung;
import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;


public class MockRechnungDialogPersister extends RechnungDialogPersister {

   public MockRechnungDialogPersister(Options optionen) {
      this();
   }

   public MockRechnungDialogPersister() {
      super(null);
   }

   @Override
   public Verbindung connectToDb(Options optionen) {
      return null;
   }

   @Override
   public Builder getRechnungById(int rechnungs_id) {
      return new Builder();
   }
   
   @Override
   public Vector<Arbeitsstunde> getEinheiten(int rechnungs_id) {
      return new Vector<Arbeitsstunde>();
   }
}
