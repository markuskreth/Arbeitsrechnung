package de.kreth.arbeitsrechnungen;

import java.sql.SQLException;

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
				try {
               return (T) new MockKlientenEditorPersister();
            } catch (SQLException e) {
               throw new RuntimeException(e);
            }
			}
		}
		if (clazz == RechnungDialogPersister.class) {
			if (rechnungDialogPersister != null) {
				return (T) rechnungDialogPersister;
			} else {
				try {
               return (T) new MockRechnungDialogPersister();
            } catch (SQLException e) {
               throw new RuntimeException(e);
            }
			}
		}
		throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...");
	}
}
