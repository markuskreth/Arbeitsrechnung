package de.kreth.arbeitsrechnungen.gui.jframes;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.ArbeitsstundeImpl;
import de.kreth.arbeitsrechnungen.data.Klient;

public class ArbeitsstundenFrameTest extends AbstractFrameTest {

   private Arbeitsstunden frame;
   private List<Klient> klienten;
   private List<Arbeitsstunde> arbeitsstunden;

   @Before
   public void initFrame() throws Exception {
      klienten = new ArrayList<>();
      klienten.add(new Klient.Builder(1, "Auftraggeber1", "Adr", "plz", "ort").build());
      
      when(klientenEditorPersister.getAllKlienten()).thenReturn(klienten);
      when(klientenEditorPersister.getKlientById(1)).thenReturn(klienten.get(0));
      
      arbeitsstunden = new ArrayList<>();
      arbeitsstunden.add(new ArbeitsstundeImpl.Builder(1, 1, 1).
            auftraggeber(klienten.get(0).getAuftraggeber())
            .beginn(new Date())
            .ende(new Date())
            .dauerInMinuten(60)
            .einzelPreis(13.5)
            .inhalt("Trainingsstunde")
            .preis(13.5)
            .preisProStunde(true)
            .preisaenderung(0)
            .build());
      when(datenPersister.getEinheiten(eq(1), anyString())).thenReturn(arbeitsstunden);
      
      SwingUtilities.invokeAndWait(new Runnable() {
         
         @Override
         public void run() {
            frame = new Arbeitsstunden();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            
         }
      });
   }
   
   @After
   public void closeFrame() throws InvocationTargetException, InterruptedException {
      SwingUtilities.invokeAndWait(new Runnable() {
         
         @Override
         public void run() {
            frame.setVisible(false);
            frame.dispose();
         }
      });
   }
   
   @Test
   public void test() {

      final FrameFixture frame = findFrame(new GenericTypeMatcher<Frame>(Frame.class) {

         @Override
         protected boolean isMatching(Frame frame) {
            return frame.isShowing();
         }
      }).using(robot());
      assertNotNull(frame);
      assertTrue(frame.target() instanceof Arbeitsstunden);
      
   }

}
