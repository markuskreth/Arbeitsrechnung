package de.kreth.arbeitsrechnungen;

/**
 * @author markus
 */
import java.io.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Einstellungen {

   static Einstellungen instance; 
   
   private Options opt;

   private Logger logger;

   private String propertyPath;

   protected Einstellungen() {

      logger = LoggerFactory.getLogger(getClass());
      Properties optionen = new Properties();
      java.util.Properties sysprops = System.getProperties();
      // Testen ob das arbeitsverzeichnis im home-verzeichnis existiert
      File homeverzeichnis;
      
      String homedir = sysprops.getProperty("user.home");
      homeverzeichnis = new File(homedir, Options.BENUTZERVERZEICHNIS);

      if (!homeverzeichnis.exists()) {
         // Verzeichnis anlegen
         logger.info(homeverzeichnis.getAbsolutePath() + " existiert nicht!\nwird angelegt...");
         homeverzeichnis.mkdirs();
      }

      java.io.File optionfile = new java.io.File(homeverzeichnis, "optionen.ini");
      propertyPath =optionfile.getAbsolutePath();
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

   public String getPropertyPath() {
      return propertyPath;
   }
   
   public void store(Options opt) throws FileNotFoundException, IOException {
      this.opt = opt;
      opt.getProperties().store(new FileOutputStream(propertyPath), "Eigene Optionen");
   }

   static void setOptions(Options optionen) {
	   if(instance == null) {
		   instance = new Einstellungen();
	   }
      instance.opt = optionen;
   }

}
