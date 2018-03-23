package de.kreth.arbeitsrechnungen;

/**
 * @author markus
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Einstellungen {

   static Einstellungen instance; 
   
   private final String propertyPath;
   private Options opt;

   private Logger logger;

   protected Einstellungen() {

      logger = LoggerFactory.getLogger(getClass());
      Properties optionen = new Properties();
      java.util.Properties sysprops = System.getProperties();
      // Testen ob das arbeitsverzeichnis im home-verzeichnis existiert
      File homeverzeichnis;
      
      String homedir = sysprops.getProperty("user.home");
      homeverzeichnis = new File(homedir + sysprops.getProperty("file.separator") + Options.BENUTZERVERZEICHNIS);

      if (!homeverzeichnis.exists()) {
         // Verzeichnis anlegen
         logger.info(homeverzeichnis.getAbsolutePath() + " existiert nicht!\nwird angelegt...");
         homeverzeichnis.mkdirs();
      }

      StringBuilder bld = new StringBuilder();
      bld.append(sysprops.getProperty("user.home"));
      bld.append(sysprops.getProperty("file.separator"));
      bld.append(Options.BENUTZERVERZEICHNIS);
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
            logger.error("Options-Datei konnte nicht geladen werden.", e);
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
      this.opt = opt;
      opt.getProperties().store(new FileOutputStream(propertyPath), "Eigene Optionen");
   }

   static void setOptions(Options optionen) {
      instance = new Einstellungen();
      instance.opt = optionen;
   }

}