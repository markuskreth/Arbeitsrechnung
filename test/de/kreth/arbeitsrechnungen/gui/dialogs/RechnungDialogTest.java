package de.kreth.arbeitsrechnungen.gui.dialogs;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactoryTestingFakeDB;
import de.kreth.arbeitsrechnungen.Options;


public class RechnungDialogTest {

   @Before
   public void setUp() throws Exception {
      ArbeitRechnungFactoryTestingFakeDB.init();
   }

   @Test
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
   }

}
