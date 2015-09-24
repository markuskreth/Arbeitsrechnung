package de.kreth.arbeitsrechnungen.test;

import arbeitsabrechnungendataclass.Verbindung;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;

public class MockKlientenEditorPersister extends KlientenEditorPersister {

   public MockKlientenEditorPersister(Options optionen) {
      super(optionen);
   }

   public MockKlientenEditorPersister() {
      super(null);
   }

   @Override
   public Verbindung connectToDb(Options optionen) {
      return null;
   }
   
   @Override
   public Klient getKlientById(int klient_id) {
      return new Klient.Builder(1, "Test", "", "", "").build();
   }
}
