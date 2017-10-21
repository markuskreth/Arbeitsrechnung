package de.kreth.arbeitsrechnungen.gui.dialogs;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactoryTestingFakeDB;
import de.kreth.arbeitsrechnungen.MockRechnungDialogPersister;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;

public class RechnungDialogTest {

   private ArbeitRechnungFactoryTestingFakeDB factory;

   @Before
   public void setUp() throws Exception {
      factory = ArbeitRechnungFactoryTestingFakeDB.init();
      factory.rechnungDialogPersister = new MockRechnungDialogPersister();
      factory.rechnungDialogPersister.rechnungBuilder = new Builder()
            .datum(new GregorianCalendar())
            .zusatz2(false)
            .klienten_id(-15)
            .zusatz1(true)
            .rechnungnr("TestRechnung" + getClass().getName())
            .zahldatum(null)
            .texdatei("")
            .adresse("")
            .einheiten(new Vector<>());
   }

   @Test
   @Ignore
   public void testInit() {
      Properties prop = new Properties();
      prop.setProperty(Options.DB_HOST, "localhost");
      prop.setProperty(Options.DB_DATABASE_NAME, "testdb");
      prop.setProperty(Options.DB_USER, "markus");
      prop.setProperty(Options.DB_PASSWORD, "0773");

      prop.setProperty(Options.PDF_PROG, "okular");
      prop.setProperty(Options.STD_TEX_FILE, "/home/data/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/Rechnung_Allgemein.tex");
      prop.setProperty(Options.TARGET_DIR, "/home/data/markus/programming/NetBeansProjects/Arbeitsrechnungen/pdf-speicher");
      prop.setProperty(Options.TEX_TEMPLATE_DIR, "/home/data/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/");
      prop.setProperty(Options.TMP_DIR, "/tmp/");

      Options o = new Options.Build(prop).build();
      RechnungDialog dlg = new RechnungDialog(o, null, 1);
      assertNotNull(dlg);
      Rechnung rechn = dlg.getRechnung();
      assertNotNull(rechn);
      assertEquals("TestRechnung" + getClass().getName(), rechn.getRechnungnr());
      
   }

}
