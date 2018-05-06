package de.kreth.arbeitsrechnungen;

import java.sql.SQLException;
import java.util.Vector;

import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.database.Verbindung;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;

public class MockRechnungDialogPersister extends RechnungDialogPersister {

   public int klientId;
   public Builder rechnungBuilder;

   public MockRechnungDialogPersister(Options optionen) throws SQLException {
      this();
   }

   public MockRechnungDialogPersister() throws SQLException {
      super(null);
   }

   @Override
   public Verbindung connectToDb(Options optionen) {
      return null;
   }

   @Override
   public int getKlientenIdForRechnungId(int rechnungs_id) {
      return klientId;
   }
   
   @Override
   public Builder getRechnungById(int rechnungs_id) {
      return rechnungBuilder;
   }

   @Override
   public Vector<Arbeitsstunde> getEinheiten(int rechnungs_id) {
      return new Vector<Arbeitsstunde>();
   }
}
