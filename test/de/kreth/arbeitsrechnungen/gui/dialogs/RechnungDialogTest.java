package de.kreth.arbeitsrechnungen.gui.dialogs;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactoryTestingFakeDB;


public class RechnungDialogTest {

   @Before
   public void setUp() throws Exception {
      ArbeitRechnungFactoryTestingFakeDB.init();
   }

   @Test
   public void testInit() {
      RechnungDialog dlg = new RechnungDialog(null, null, 1);
      assertNotNull(dlg);
   }

}
