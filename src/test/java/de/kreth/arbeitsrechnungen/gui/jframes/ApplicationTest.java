package de.kreth.arbeitsrechnungen.gui.jframes;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Frame;
import java.util.*;

import javax.swing.SwingUtilities;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.MockableEinstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.persister.DatenPersister.Forderung;

public class ApplicationTest extends AbstractFrameTest {

   @Override
   protected void onSetUp() {
      super.onSetUp();
      Options optionen = new Options.Build(Einstellungen.getInstance().getEinstellungen().getProperties())
            .dbHost("leer")
            .dbDatabaseName("leer")
            .build();
      MockableEinstellungen.setOptions(optionen);
      application(getClass()).start();
   }

   @Test
   public void testKlientFrame() {
      when(klientenEditorPersister.getAllKlienten()).thenReturn(Collections.emptyList());
      when(rechnungPersister.getRechnungenForKlient(anyInt())).thenReturn(Collections.emptyList());

      initWith(Collections.emptyList(), Collections.emptyList()).button("jButtonKlientenEditor").click();
      final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {

         @Override
         protected boolean isMatching(Frame frame) {
            return frame.isShowing();
         }
      }).using(robot());
      assertNotNull(frame);
   }

   @Test
   public void testEmpty() {

      final FrameFixture frame = initWith(Collections.emptyList(), Collections.emptyList());

      JTableFixture tableForderungen = frame.table("jTableForderungen");
      assertNotNull(tableForderungen.target());
      tableForderungen.requireVisible();
      String[][] contents = tableForderungen.contents();
      assertEquals(1, contents.length);
      assertEquals("Summe", contents[0][0]);
      assertEquals("", contents[0][1]);
      assertEquals("0,00 €", contents[0][2]);

      JTableFixture tableEinheiten = frame.table("jTableEinheiten");
      assertNotNull(tableEinheiten.target());
      contents = tableEinheiten.contents();
      assertEquals(1, contents.length);
      assertEquals("Summe", contents[0][0]);
      assertEquals("", contents[0][1]);
      assertEquals("0,00 €", contents[0][2]);
   }

   @SuppressWarnings("boxing")
   @Test
   public void testOneForderungAndOneEinheit() {

      Calendar fordDate = new GregorianCalendar(2017, 5, 5);
      Forderung forderung = mock(Forderung.class);
      when(forderung.getAuftraggeber()).thenReturn("AuftraggeberName");
      when(forderung.getSumme()).thenReturn(22.2);
      when(forderung.getDatum()).thenReturn(fordDate);

      List<Forderung> forrdList = Arrays.asList(forderung);
      List<Einheit> einheitList = Arrays.asList(new Einheit.Builder().auftraggeber("Auftraggeber2Name")
            .datum(new GregorianCalendar(2017, 7, 5).getTime())
            .beginn(new GregorianCalendar(2017, 7, 5, 17, 0, 0).getTime())
            .ende(new GregorianCalendar(2017, 7, 5, 19, 0, 0).getTime())
            .klientenpreis(11.1)
            .anzahl(2)
            .build());

      final FrameFixture frame = initWith(forrdList, einheitList);

      JTableFixture tableForderungen = frame.table("jTableForderungen");
      assertNotNull(tableForderungen.target());
      tableForderungen.requireVisible();
      String[][] contents = tableForderungen.contents();
      assertEquals(2, contents.length);
      assertEquals("AuftraggeberName", contents[0][0]);
      assertEquals("05.06.2017", contents[0][1]);
      assertEquals("22,20€", contents[0][2]);

      JTableFixture tableEinheiten = frame.table("jTableEinheiten");
      assertNotNull(tableEinheiten.target());
      contents = tableEinheiten.contents();
      assertEquals(2, contents.length);
      assertEquals("Auftraggeber2Name", contents[0][0]);
      assertEquals("2", contents[0][1]);
      assertEquals("11,10 €", contents[0][2]);
   }

   private FrameFixture initWith(List<Forderung> forrdList, List<Einheit> einheitList) {
      when(datenPersister.getForderungen()).thenReturn(forrdList);
      when(datenPersister.getEinheiten()).thenReturn(einheitList);
      final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {

         @Override
         protected boolean isMatching(Frame frame) {
            return frame.isShowing();
         }
      }).using(robot());
      assertNotNull(frame);
      return frame;
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {
            new StartFenster().setVisible(true);
         }
      });
   }
}
