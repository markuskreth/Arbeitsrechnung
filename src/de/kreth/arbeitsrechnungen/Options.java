package de.kreth.arbeitsrechnungen;

import java.io.File;
import java.util.*;

public class Options {

   private static final String STD_TEX_FILE = "stdtexdatei";
   private static final String TEX_TEMPLATE_DIR = "verzeichnistexdateien";
   private static final String PDF_PROG = "pdfprogramm";
   private static final String DB_USER = "user";
   private static final String DB_HOST = "sqlserver";
   private static final String DB_DATABASE_NAME = "datenbank";
   private static final String DB_PASSWORD = "password";
   private static final String TARGET_DIR = "verzPdfFiles";
   private static final String TMP_DIR = "arbeitsverzeichnis";
   private Properties prop;

   private Options(Build build) {
      prop = build.properties;
   }

   public String getStdTexFile() {
      return prop.getProperty(STD_TEX_FILE);
   }

   public File getTexTemplatesDir() {
      return new File(prop.getProperty(TEX_TEMPLATE_DIR));
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
    * Eigenschaften als Properties - änderungen haben keinen Einfluss auf dieses Objekt.
    * @return Eigenschaften als Properties.
    */
   public Properties getProperties() {
      return (Properties) prop.clone();
   }
   
   public static class Build implements Builder<Options> {

      private Properties properties = null;
      
      Set<String> toSet = new HashSet<>(Arrays.asList("stdTexFile"
            , "texTemplatesDir"
            , "pdfProg"
            , "dbUser"
            , "dbHost"
            , "dbDatabaseName"
            , "dbPassword"
            , "targetDir"
            , "tmpDir"));
      
      public Build() {
         this.properties = new Properties();
      }
      
      public Build(Properties properties) {
         this.properties = properties;
         stdTexFile(properties.getProperty(STD_TEX_FILE));
         texTemplatesDir(properties.getProperty(TEX_TEMPLATE_DIR));
         pdfProg(properties.getProperty(PDF_PROG));
         dbUser(properties.getProperty(DB_USER));
         dbHost(properties.getProperty(DB_HOST));
         dbDatabaseName(properties.getProperty(DB_DATABASE_NAME));
         dbPassword(properties.getProperty(DB_PASSWORD));
         targetDir(properties.getProperty(TARGET_DIR));
         tmpDir(properties.getProperty(TMP_DIR));
      }
      
      public Build stdTexFile(String stdTexFile) {
         properties.setProperty(STD_TEX_FILE, stdTexFile);
         toSet.remove("stdTexFile");
         return this;
      }
      
      public Build texTemplatesDir(String texTemplatesDir) {
         if(texTemplatesDir == null)
            throw new IllegalArgumentException("texTemplatesDir must not be null!");
         properties.setProperty(TEX_TEMPLATE_DIR, texTemplatesDir);
         toSet.remove("texTemplatesDir");
         return this;
      }

      public Build pdfProg(String pdfProg) {
         if(pdfProg == null)
            throw new IllegalArgumentException("pdfProg must not be null!");
         properties.setProperty(PDF_PROG, pdfProg);
         toSet.remove("pdfProg");
         return this;
      }

      public Build dbUser(String dbUser) {
         if(dbUser == null)
            throw new IllegalArgumentException("dbUser must not be null!");
         properties.setProperty(DB_USER, dbUser);
         toSet.remove("dbUser");
         return this;
      }

      public Build dbHost(String dbHost) {
         if(dbHost == null)
            throw new IllegalArgumentException("dbHost must not be null!");
         properties.setProperty(DB_HOST, dbHost);
         toSet.remove("dbHost");
         return this;
      }

      public Build dbDatabaseName(String dbDatabaseName) {
         if(dbDatabaseName == null)
            throw new IllegalArgumentException("dbDatabaseName must not be null!");
         properties.setProperty(DB_DATABASE_NAME, dbDatabaseName);
         toSet.remove("dbDatabaseName");
         return this;
      }

      public Build dbPassword(String dbPassword) {
         if(dbPassword == null)
            throw new IllegalArgumentException("dbPassword must not be null!");
         properties.setProperty(DB_PASSWORD, dbPassword);
         toSet.remove("dbPassword");
         return this;
      }

      public Build targetDir(String targetDir) {
         if(targetDir == null)
            throw new IllegalArgumentException("targetDir must not be null!");
         properties.setProperty(TARGET_DIR, targetDir);
         toSet.remove("targetDir");
         return this;
      }

      
      public Build tmpDir(String tmpDir) {
         if(tmpDir == null)
            throw new IllegalArgumentException("tmpDir must not be null!");
         properties.setProperty(TMP_DIR, tmpDir);
         toSet.remove("tmpDir");
         return this;
      }

      @Override
      public Options build() {
         if(toSet.size()>0)
            throw new IllegalStateException("Alle Optionen müssen gesetzt sein! Offen: " + toSet);
         return new Options(this);
      }
      
   }
}
