package de.kreth.arbeitsrechnungen;

import java.io.File;
import java.util.*;

public class Options {

   public static final String BENUTZERVERZEICHNIS = ".arbeitrechnungen";
   
   public static final String PDF_PROG = "pdfprogramm";
   public static final String DB_USER = "user";
   public static final String DB_HOST = "sqlserver";
   public static final String DB_DATABASE_NAME = "datenbank";
   public static final String DB_PASSWORD = "password";
   public static final String TARGET_DIR = "verzPdfFiles";
   public static final String TMP_DIR = "arbeitsverzeichnis";
   public static final String LOG_LEVEL = "LogLevel";
   public static final String TRAINER_NAME = "TrainerName";
   public static final String TRAINER_ADRESS = "TrainerAdress";
   public static final String BANKVERBINDUNG = "Bankverbindung";
   public static final String BANK_IBAN = "IBAN";
   public static final String BANK_BIC = "BIC";
   
   public static final Set<String> PROPERTIES;
   static {
      PROPERTIES = new HashSet<>();
      PROPERTIES.add(PDF_PROG);
      PROPERTIES.add(DB_USER);
      PROPERTIES.add(DB_HOST);
      PROPERTIES.add(DB_DATABASE_NAME);
      PROPERTIES.add(DB_PASSWORD);
      PROPERTIES.add(TARGET_DIR);
      PROPERTIES.add(TMP_DIR);
      PROPERTIES.add(LOG_LEVEL);
      PROPERTIES.add(TRAINER_NAME);
      PROPERTIES.add(TRAINER_ADRESS);
      PROPERTIES.add(BANKVERBINDUNG);
      PROPERTIES.add(BANK_IBAN);
      PROPERTIES.add(BANK_BIC);
   }
   
   private Properties prop;

   private Options(Build build) {
      prop = build.properties;
   }

   public String getTrainerName() {
      return prop.getProperty(TRAINER_NAME);
   }

   public String getTrainerAdress() {
      return prop.getProperty(TRAINER_ADRESS);
   }

   public String getBankname() {
      return prop.getProperty(BANKVERBINDUNG);
   }

   public String getIban() {
      return prop.getProperty(BANK_IBAN);
   }

   public String getBic() {
      return prop.getProperty(BANK_BIC);
   }
   
   public String getPdfProg() {
      return prop.getProperty(PDF_PROG);
   }

   public String getDbUser() {
      return prop.getProperty(DB_USER);
   }

   public String getDbHost() {
      return prop.getProperty(DB_HOST);
   }

   public String getDbDatabaseName() {
      return prop.getProperty(DB_DATABASE_NAME);
   }

   public String getDbPassword() {
      return prop.getProperty(DB_PASSWORD);
   }

   public File getTargetDir() {
      return new File(prop.getProperty(TARGET_DIR));
   }

   public File getTmpDir() {
      return new File(prop.getProperty(TMP_DIR));
   }

   /**
    * Eigenschaften als Properties - änderungen haben keinen Einfluss auf dieses
    * Objekt.
    * 
    * @return Eigenschaften als Properties.
    */
   public Properties getProperties() {
      return (Properties) prop.clone();
   }

   @Override
   public String toString() {
      if(prop != null)
         return prop.toString();
      return "[]";
   }
   
   public static class Build implements Builder<Options> {

      private Properties properties = null;

      Set<String> toSet = new HashSet<>(Arrays.asList(PDF_PROG, DB_USER, DB_HOST, DB_DATABASE_NAME, DB_PASSWORD, TARGET_DIR, TMP_DIR, TRAINER_NAME, TRAINER_ADRESS, BANKVERBINDUNG, BANK_IBAN));

      @Override
      public String toString() {
         return properties.toString();
      }
      
      public Build() {
         this.properties = new Properties();
      }

      public Build(Properties properties) {
         this.properties = properties;
         toSet.clear();
      }

      public Build pdfProg(String pdfProg) {
         if (pdfProg == null)
            throw new IllegalArgumentException("pdfProg must not be null!");
         properties.setProperty(PDF_PROG, pdfProg);
         toSet.remove(PDF_PROG);
         return this;
      }

      public Build dbUser(String dbUser) {
         if (dbUser == null)
            throw new IllegalArgumentException("dbUser must not be null!");
         properties.setProperty(DB_USER, dbUser);
         toSet.remove(DB_USER);
         return this;
      }

      public Build trainerAdress(String trainerAdress) {
         return setPropertyIfValid(trainerAdress, TRAINER_ADRESS);
      }

      public Build trainerName(String trainerName) {
         return setPropertyIfValid(trainerName, TRAINER_NAME);
      }

      public Build bankverbindung(String bankverbindung) {
         return setPropertyIfValid(bankverbindung, BANKVERBINDUNG);
      }

      public Build iban(String iban) {
         return setPropertyIfValid(iban, BANK_IBAN);
      }

      public Build bic(String bic) {
         return setPropertyIfValid(bic, BANK_BIC);
      }

      private Build setPropertyIfValid(String trainerName, String key) {
         if (trainerName == null)
            throw new IllegalArgumentException(key + " must not be null!");
         properties.setProperty(key, trainerName);
         toSet.remove(key);
         return this;
      }

      public Build dbHost(String dbHost) {
         if (dbHost == null)
            throw new IllegalArgumentException("dbHost must not be null!");
         properties.setProperty(DB_HOST, dbHost);
         toSet.remove(DB_HOST);
         return this;
      }

      public Build dbDatabaseName(String dbDatabaseName) {
         if (dbDatabaseName == null)
            throw new IllegalArgumentException("dbDatabaseName must not be null!");
         properties.setProperty(DB_DATABASE_NAME, dbDatabaseName);
         toSet.remove(DB_DATABASE_NAME);
         return this;
      }

      public Build dbPassword(String dbPassword) {
         if (dbPassword == null)
            throw new IllegalArgumentException("dbPassword must not be null!");
         properties.setProperty(DB_PASSWORD, dbPassword);
         toSet.remove(DB_PASSWORD);
         return this;
      }

      public Build targetDir(String targetDir) {
         if (targetDir == null)
            throw new IllegalArgumentException("targetDir must not be null!");
         properties.setProperty(TARGET_DIR, targetDir);
         toSet.remove(TARGET_DIR);
         return this;
      }

      public Build tmpDir(String tmpDir) {
         if (tmpDir == null)
            throw new IllegalArgumentException("tmpDir must not be null!");
         properties.setProperty(TMP_DIR, tmpDir);
         toSet.remove(TMP_DIR);
         return this;
      }

      @Override
      public Options build() {
         if (toSet.size() > 0)
            throw new IllegalStateException("Alle Optionen müssen gesetzt sein! Offen: " + toSet);
         return new Options(this);
      }

      public void setProperty(String name, String value) {
         if(!PROPERTIES.contains(name)) {
            throw new IllegalArgumentException("Property Key " + name + " unknown!");
         } else {
            properties.setProperty(name, value);
            toSet.remove(name);
         }
         
      }

   }

   
}
