package de.kreth.arbeitsrechnungen.persister;

import arbeitsabrechnungendataclass.Verbindung;
import de.kreth.arbeitsrechnungen.Options;

public interface Persister {

   Verbindung connectToDb(Options optionen);
}
