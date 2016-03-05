package de.kreth.arbeitsrechnungen;

/**
 * @author markus
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Einstellungen {

   protected static Einstellungen instance; 
   private final static String programmverzeichnis = ".arbeitrechnungen";
   private final String propertyPath;
   private Options opt;

   protected Einstellungen() {
      Properties optionen = new Properties();
      java.util.Properties sysprops = System.getProperties();
      StringBuilder bld = new StringBuilder();
      bld.append(sysprops.getProperty("user.home"));
      bld.append(sysprops.getProperty("file.separator"));
      bld.append(programmverzeichnis);
      bld.append(sysprops.getProperty("file.separator"));
      bld.append("optionen.ini");
      
      propertyPath = bld.toString();
      java.io.File optionfile = new java.io.File(propertyPath);

      if(optionfile.exists()) {
         try {
            optionen.load(new java.io.FileInputStream(optionfile));
            Options.Build builder = new Options.Build(optionen);
            opt = builder.build();
         } catch (Exception e) {
            Logger.getLogger(getClass()).error("Options-Datei konnte nicht geladen werden.", e);
         }
      }
   }
   
   public static Einstellungen getInstance() {
      if(instance == null || instance.opt == null)
         instance = new Einstellungen();
      return instance;
   }
   
   public Options getEinstellungen() {
      return opt;
   }

   public void store(Options opt) throws FileNotFoundException, IOException {
      opt.getProperties().store(new FileOutputStream(propertyPath), "Eigene Optionen");
   }

}
