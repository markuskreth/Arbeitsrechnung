package de.kreth.arbeitsrechnungen.gui.dialogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactoryTestingFakeDB;
import de.kreth.arbeitsrechnungen.MockRechnungDialogPersister;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;

public class RechnungDialogTest {

	private ArbeitRechnungFactoryTestingFakeDB factory;

	@Before
	public void setUp() throws Exception {
		factory = ArbeitRechnungFactoryTestingFakeDB.init();
		factory.rechnungDialogPersister = new MockRechnungDialogPersister();
		factory.rechnungDialogPersister.rechnungBuilder = new Builder().datum(new GregorianCalendar()).zusatz2(false)
				.klienten_id(-15).zusatz1(true).rechnungnr("TestRechnung" + getClass().getName()).zahldatum(null)
				.texdatei("").adresse("").einheiten(new Vector<>());
	}

	@Test
	public void testInit() throws InvocationTargetException, InterruptedException {
		final List<Rechnung> result = new ArrayList<>();
		
		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				RechnungDialog dlg = new RechnungDialog(null, 1);
				assertNotNull(dlg);
				result.add(dlg.getRechnung());
			}
		});
		assertEquals(1, result.size());
		
		Rechnung rechn = result.get(0);
		assertNotNull(rechn);
		assertEquals("TestRechnung" + getClass().getName(), rechn.getRechnungnr());
	}

}
