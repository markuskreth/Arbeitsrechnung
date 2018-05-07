package de.kreth.arbeitsrechnungen.gui.jframes;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.awt.Frame;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.assertj.core.api.AbstractAssert;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.Test;

import com.toedter.calendar.JDateChooser;

import de.kreth.arbeitsrechnungen.data.Einheit;

public class EinheitEinzelFrameTest extends AbstractFrameTest {

   Calendar einheitDate;

   @Override
   protected void onSetUp() {
      super.onSetUp();

      application(EinheitEinzelFrame.class).start();

      einheitDate = new GregorianCalendar(2017, Calendar.AUGUST, 1, 12, 33, 45);
   }

   @Test
   public void testComboBoxIsSet() {
      final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {

         @Override
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

         @Override
         protected boolean isMatching(Frame frame) {
            return "Arbeitsstunde".equals(frame.getTitle()) && frame.isShowing();
         }
      }).using(robot());
      assertNotNull(frame);
      assertTrue(frame.target() instanceof EinheitEinzelFrame);

      Calendar start = (Calendar) einheitDate.clone();
      start.set(Calendar.HOUR_OF_DAY, 17);
      start.set(Calendar.MINUTE, 0);
      start.set(Calendar.SECOND, 0);
      start.set(Calendar.MILLISECOND, 0);

      Calendar ende = (Calendar) start.clone();
      ende.set(Calendar.HOUR_OF_DAY, 19);
      ende.set(Calendar.MINUTE, 30);

      final Einheit einheit = new Einheit.Builder().angebot(angebote.get(0)).angebotId(9).id(-1).auftraggeber(auftraggeber.getName()).klientenId(auftraggeber.getKlientId())
            .datum(einheitDate.getTime()).beginn(start.getTime()).ende(ende.getTime()).zusatz1("ca. 13").build();

      GuiTask task = new GuiTask() {

         @Override
         protected void executeInEDT() throws Throwable {
            EinheitEinzelFrame einh = (EinheitEinzelFrame) frame.target();
            einh.load(1, -1);
            JDateChooser dateChooser = FrameElementAccessor.getDateChooser(einh);
            dateChooser.setCalendar(einheitDate);
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

      private static final long serialVersionUID = 1L;

      public static JDateChooser getDateChooser(EinheitEinzelFrame frame) {
         return frame.jDateChooserDatum;
      }
   }
}
