package de.kreth.arbeitsrechnungen.gui.jframes;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.assertj.swing.finder.WindowFinder.findFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.MockableArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.MockableEinstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.gui.jframes.EinheitEinzelFrame;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister.Auftraggeber;

public class EinheitEinzelFrameTest extends AssertJSwingJUnitTestCase {

	KlientPersister klientPersister;
	AngebotPersister angebotPersister;
	private Auftraggeber auftraggeber;
	private List<Angebot> angebote;
	private MockableArbeitRechnungFactory factory;
	private Options options;
	private MockableEinstellungen einstellungen;

	@Override
	protected void onSetUp() {
		klientPersister = mock(KlientPersister.class);
		angebotPersister = mock(AngebotPersister.class);
		initMockAuftraggeber();
		initAngebote();
		
		when(klientPersister.getAuftraggeber(anyInt())).thenReturn(auftraggeber);
		when(angebotPersister.getForKlient(anyInt())).thenReturn(angebote);

		options = mock(Options.class);
		einstellungen = mock(MockableEinstellungen.class);
		MockableEinstellungen.setInstance(einstellungen);
		when(einstellungen.getEinstellungen()).thenReturn(options);
		MockableEinstellungen.setOptions(options);
		factory = mock(MockableArbeitRechnungFactory.class);
		MockableArbeitRechnungFactory.setInstance(factory);
		
		when(factory.getPersister(KlientPersister.class, options)).thenReturn(klientPersister);
		when(factory.getPersister(AngebotPersister.class, options)).thenReturn(angebotPersister);
		
		application(EinheitEinzelFrame.class).start();
	}

	private void initAngebote() {
		angebote = new ArrayList<>();
		angebote.add(new Angebot.Builder("Angebot 9,5Eur", 9.5).angebotId(9).build());
	}

	private void initMockAuftraggeber() {
		auftraggeber = mock(Auftraggeber.class);
		when(auftraggeber.getName()).thenReturn("Auftraggeber Name");
		when(auftraggeber.getKlientId()).thenReturn(1);
		when(auftraggeber.getZusatz1()).thenReturn("Zusatz1");
		when(auftraggeber.hasZusatz1()).thenReturn(true);
		when(auftraggeber.hasZustz2()).thenReturn(false);
	}

	@Test
	public void testComboBoxIsSet() {
		final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {
			  protected boolean isMatching(Frame frame) {
			    return "Arbeitsstunde".equals(frame.getTitle()) && frame.isShowing();
			  }
			}).using(robot());
		assertNotNull(frame);
		assertTrue(frame.target() instanceof EinheitEinzelFrame);
		
		GuiTask task = new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				EinheitEinzelFrame einh = (EinheitEinzelFrame) frame.target();
				einh.load(1, -1);
			}
		};
		GuiActionRunner.execute(task);
		
		JComboBoxFixture cmbAngebote = frame.comboBox("jComboBoxAngebot");
		String[] contents = cmbAngebote.contents();
		assertEquals(1, contents.length);
		String comItem = cmbAngebote.selectedItem();
		assertNotNull(comItem);
		assertEquals("Angebot 9,5Eur|9,50 €", comItem);
		
	}

	@Test
	public void testStoreNew() {
		final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {
			  protected boolean isMatching(Frame frame) {
			    return "Arbeitsstunde".equals(frame.getTitle()) && frame.isShowing();
			  }
			}).using(robot());
		assertNotNull(frame);
		assertTrue(frame.target() instanceof EinheitEinzelFrame);
		
		GuiTask task = new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				EinheitEinzelFrame einh = (EinheitEinzelFrame) frame.target();
				einh.load(1, -1);
			}
		};
		GuiActionRunner.execute(task);
		
		
	}

}
