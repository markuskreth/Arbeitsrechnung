
package arbeitsabrechnungendataclass;

/**
 * @author markus
 */
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Verbindung_hsqldb {

   Logger logger = LoggerFactory.getLogger(getClass());
   static String treiber = "org.hsqldb.jdbcDriver";
   static String URL = "jdbc:hsqldb:file:";
   static String benutzer = "sa";
   static String password = "";

   private java.util.Properties sysProps = System.getProperties();
   Statement befehl = null;
   Connection verbindung = null;

   /**
    * Treiber laden, Verbindung aufbauen und die Tabellen der Datenbank auslesen
    * und in die tabellenliste speichern.
    * Datenbank wird im Arbeitsverzeichnis im Unterverzeichnis "daten"
    * gespeichert
    */
   public Verbindung_hsqldb() {
      this("daten");
   }

   /**
    * Treiber laden, Verbindung aufbauen und die Tabellen der Datenbank auslesen
    * und in die tabellenliste speichern.
    */
   public Verbindung_hsqldb(String pfad) {
      String datenbank = pfad + sysProps.getProperty("file.separator") + "daten" + ";shutdown=true";

      URL += datenbank;

      logger.debug("Pfad zur Datenbank: " + datenbank);
      logger.debug("URL: " + URL);

      try {
         Class.forName(treiber).newInstance();
      } catch (Exception e) {
         logger.error(treiber + "-Treiber konnte nicht geladen werden!", e);
         return;
      }
      // Verbindung aufbauen

      try {
         verbindung = DriverManager.getConnection(URL, benutzer, password);
         befehl = verbindung.createStatement();
      } catch (Exception e) {
         logger.error("Verbindung zu " + URL + " konnte nicht hergestellt werden.", e);
      }
   }

   public boolean connected() {
      try {
         return verbindung.isValid(3);
      } catch (Exception e) {
         return false;
      }
   }

   public java.sql.DatabaseMetaData getMetaData() {
      try {
         return verbindung.getMetaData();
      } catch (java.sql.SQLException ex) {
         logger.error("Fehler bei abruf!", ex);
         return null;
      }
   }

   /**
    * Die Sql-Abfrage gibt den ResultSet zurück. Dafür muss vermutlich
    * java.sql.*
    * in der aufrufenden Klasse definiert sein :(
    */
   public ResultSet query(String sqltext) {

      ResultSet daten = null;
      try {
         logger.error("Executing: " + sqltext);
         daten = befehl.executeQuery(sqltext);
      } catch (Exception e) {
         logger.error("SQL: " + sqltext, e);
      }
      return daten;
   }

   public boolean sql(String sqltext) {
      boolean ok = false;
      try {

         logger.debug("Executing " + sqltext);
         befehl.execute(sqltext);
         ok = true;
      } catch (Exception e) {
         logger.debug("SQL: " + sqltext, e);
      }
      return ok;
   }

   public void close() {
      try {
         verbindung.close();
      } catch (Exception e) {
         logger.debug("on close: ", e);
      }
   }
}
