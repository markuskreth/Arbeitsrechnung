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

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Persister> T getPersister(Class<T> clazz) {
		if (clazz == KlientenEditorPersister.class) {
			if (klientenEditorPersister != null) {
				return (T) klientenEditorPersister;
			} else {
				return (T) new MockKlientenEditorPersister();
			}
		}
		if (clazz == RechnungDialogPersister.class) {
			if (rechnungDialogPersister != null) {
				return (T) rechnungDialogPersister;
			} else {
				return (T) new MockRechnungDialogPersister();
			}
		}
		throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...");
	}
}
