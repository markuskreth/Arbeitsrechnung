package de.kreth.arbeitsrechnungen.gui.jframes;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

import de.kreth.arbeitsrechnungen.MockableArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;
import de.kreth.arbeitsrechnungen.persister.DatenPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister.Auftraggeber;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.RechnungPersister;

public abstract class AbstractFrameTest extends AssertJSwingJUnitTestCase {

	protected KlientPersister klientPersister;
	protected AngebotPersister angebotPersister;
	protected DatenPersister datenPersister;
	protected Auftraggeber auftraggeber;
	protected RechnungPersister rechnungPersister;
	protected List<Angebot> angebote;
	private MockableArbeitRechnungFactory factory;
	protected KlientenEditorPersister klientenEditorPersister;

	@Override
	protected void onSetUp() {
		klientPersister = mock(KlientPersister.class);
		angebotPersister = mock(AngebotPersister.class);
		datenPersister = mock(DatenPersister.class);
		klientenEditorPersister = mock(KlientenEditorPersister.class);
		rechnungPersister = mock(RechnungPersister.class);

		factory = mock(MockableArbeitRechnungFactory.class);
		MockableArbeitRechnungFactory.setInstance(factory);
		
		when(factory.getPersister(KlientPersister.class)).thenReturn(klientPersister);
		when(factory.getPersister(AngebotPersister.class)).thenReturn(angebotPersister);
		when(factory.getPersister(DatenPersister.class)).thenReturn(datenPersister);
		when(factory.getPersister(KlientenEditorPersister.class)).thenReturn(klientenEditorPersister);
		when(factory.getPersister(RechnungPersister.class)).thenReturn(rechnungPersister);
		
		initMockAuftraggeber();
		initAngebote();
		
		when(klientPersister.getAuftraggeber(anyInt())).thenReturn(auftraggeber);
		when(angebotPersister.getForKlient(anyInt())).thenReturn(angebote);
	
	}

	private void initAngebote() {
		angebote = new ArrayList<>();
		angebote.add(new Angebot.Builder("Angebot 9,5Eur", 9.5).angebotId(9).preis_pro_stunde(true).build());
	}

	private void initMockAuftraggeber() {
		auftraggeber = mock(Auftraggeber.class);
		when(auftraggeber.getName()).thenReturn("Auftraggeber Name");
		when(auftraggeber.getKlientId()).thenReturn(1);
		when(auftraggeber.getZusatz1()).thenReturn("Zusatz1");
		when(auftraggeber.hasZusatz1()).thenReturn(true);
		when(auftraggeber.hasZustz2()).thenReturn(false);
	}
}
