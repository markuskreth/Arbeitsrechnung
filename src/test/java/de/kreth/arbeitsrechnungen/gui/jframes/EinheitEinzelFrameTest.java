package de.kreth.arbeitsrechnungen.gui.jframes;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.assertj.core.api.AbstractAssert;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import com.toedter.calendar.JDateChooser;

import de.kreth.arbeitsrechnungen.MockableArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.MockableEinstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister.Auftraggeber;

public class EinheitEinzelFrameTest extends AssertJSwingJUnitTestCase {

	private Calendar einheitDate;
	private KlientPersister klientPersister;
	private AngebotPersister angebotPersister;
	private Auftraggeber auftraggeber;
	private List<Angebot> angebote;
	private MockableArbeitRechnungFactory factory;
	private Options options;
	private MockableEinstellungen einstellungen;

	@Override
	protected void onSetUp() {
		einheitDate = new GregorianCalendar(2017, Calendar.AUGUST, 1, 12, 33, 45);
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
	public void testInvalidUserId() throws SQLException {
		final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {
			  protected boolean isMatching(Frame frame) {
			    return "Arbeitsstunde".equals(frame.getTitle()) && frame.isShowing();
			  }
			}).using(robot());
		assertNotNull(frame);
		assertTrue(frame.target() instanceof EinheitEinzelFrame);

		Calendar start = (Calendar)einheitDate.clone();
		start.set(Calendar.HOUR_OF_DAY, 17);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		
		Calendar ende = (Calendar)start.clone();
		ende.set(Calendar.HOUR_OF_DAY, 19);
		ende.set(Calendar.MINUTE, 30);
		
		final Einheit einheit = new Einheit.Builder()
				.angebot(angebote.get(0))
				.angebotId(9)
				.id(-1)
				.auftraggeber(auftraggeber.getName())
				.klientenId(auftraggeber.getKlientId())
				.datum(einheitDate.getTime())
				.beginn(start.getTime())
				.ende(ende.getTime())
				.zusatz1("ca. 13")
				.build();
		
		GuiTask task = new GuiTask() {
			
			@Override
			protected void executeInEDT() throws Throwable {
				EinheitEinzelFrame einh = (EinheitEinzelFrame) frame.target();
				einh.load(1, -1);
				JDateChooser dateChooser = FrameElementAccessor.getDateChooser(einh);
				dateChooser.setCalendar(einheitDate);
				einh.setExpectedEinheit(einheit);
			}
		};
		GuiActionRunner.execute(task);

		JTextComponentFixture startField = frame.textBox("jFormattedTextFieldStart");
		startField.setText("17:00");

		JTextComponentFixture endeField = frame.textBox("jFormattedTextFieldEnde");
		endeField.setText("19:30");

		JTextComponentFixture zusatz1Field = frame.textBox("jTextFieldZusatz1");
		zusatz1Field.setText("ca. 13");
		
		frame.button("jButton2").click();
		verify(angebotPersister).storeEinheit(any(), eq(einheit));
	}

	public static class EinheitEinzelFrameAssert extends AbstractAssert<EinheitEinzelFrameAssert, EinheitEinzelFrame> {

		protected EinheitEinzelFrameAssert(EinheitEinzelFrame actual) {
			super(actual, EinheitEinzelFrame.class);
		}
		
		public EinheitEinzelFrameAssert setDatum(Date d) {
			
			return this;
		}
	}
	
	public static class FrameElementAccessor extends EinheitEinzelFrame {
		public static JDateChooser getDateChooser(EinheitEinzelFrame frame) {
			return frame.jDateChooserDatum;
		}
	}
}
