package de.kreth.arbeitsrechnungen;

import java.io.*;
import java.util.Properties;

import de.kreth.arbeitsrechnungen.gui.jframes.StartFenster;

/**
 * Programm mit Testeinstellungen starten.
 * @author markus
 *
 */
public class ArbeitsrechnungenTestApp {

   
   public static void main(String[] args) throws FileNotFoundException, IOException {
      new ArbeitsrechnungenTestApp().start();
   }

   private void start() throws FileNotFoundException, IOException {
      ArbeitRechnungFactoryProductiv.init();
      StartFenster startFenster = new StartFenster(new TestEinstellungen().o);
      startFenster.setVisible(true);
   }

   private class TestEinstellungen extends Einstellungen {

      Options o;
      
      public TestEinstellungen() throws FileNotFoundException, IOException {
         instance = this;

         Properties sysprops = System.getProperties();
         String homedir = sysprops.getProperty("user.home");
         
         File optionfile = new File(homedir + sysprops.getProperty("file.separator") + Options.BENUTZERVERZEICHNIS + sysprops.getProperty("file.separator") + "optionen.ini");

         File pdfTarget = new File("./pdf");
         
         if(!pdfTarget.exists()) {
            pdfTarget.mkdirs();
         }
         
         Properties prop = new Properties();
         prop.load(new FileInputStream(optionfile));
         
         o = new Options.Build(prop)
               .dbDatabaseName("ArbeitrechnungenTest")
               .dbHost("localhost")
               .dbUser("markus")
               .dbPassword("0773")
               .targetDir(pdfTarget.getAbsolutePath())
               .build();
      }
      
      @Override
      public Options getEinstellungen() {
         return o;
      }
      
      @Override
      public void store(Options opt) throws FileNotFoundException, IOException {
         // Ignore storing options.
      }
   }

}

