package de.kreth.arbeitsrechnungen.persister;

import java.sql.SQLException;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.database.Verbindung;

public interface Persister {

   Verbindung connectToDb(Options optionen) throws SQLException;
}
