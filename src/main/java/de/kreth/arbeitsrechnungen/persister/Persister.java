package de.kreth.arbeitsrechnungen.persister;

import java.sql.SQLException;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.database.Verbindung;

public interface Persister extends AutoCloseable {
   
   Verbindung connectToDb(Options optionen) throws SQLException;
   
   @Override
   void close();
   
}
