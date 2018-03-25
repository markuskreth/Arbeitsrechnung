package de.kreth.arbeitsrechnungen;

import de.kreth.arbeitsrechnungen.persister.Persister;

public class MockableArbeitRechnungFactory extends ArbeitRechnungFactory {

	public static void setInstance(ArbeitRechnungFactory instance) {
		ArbeitRechnungFactory.instance = instance;
	}
	
	@Override
	public <T extends Persister> T getPersister(Class<T> clazz) {
		return null;
	}

}
