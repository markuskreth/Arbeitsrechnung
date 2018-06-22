/*
 * ArbeitsrechnungenApp.java
 */

package de.kreth.arbeitsrechnungen;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.database.Verbindung;
import de.kreth.arbeitsrechnungen.database.Verbindung_mysql;
import de.kreth.arbeitsrechnungen.gui.jframes.KlientenEditor;
import de.kreth.arbeitsrechnungen.gui.jframes.StartFenster;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;

/**
 * The main class of the application.
 */
public class ArbeitsrechnungenApp {

   protected static final String SINGLE_KLIENT_MODE = "singleKLient";

   /**
    * Main method launching the application.
    */
   public static void main(String[] args) {

      java.awt.EventQueue.invokeLater(new Runnable() {

         @Override
         public void run() {
            String singleKlientMode = System.getProperty(SINGLE_KLIENT_MODE);
            ArbeitRechnungFactoryProductiv.init();
            if (singleKlientMode == null || Boolean.parseBoolean(singleKlientMode) == false) {
               changeColumnDefault(ArbeitRechnungFactoryProductiv.instance);
               new StartFenster().setVisible(true);
            } else {
               Options opts = Einstellungen.getInstance().getEinstellungen();
               
               Properties props = opts.getProperties();
               props.setProperty("dbtype", Verbindung.ConnectionTypes.HSQLDB.name());
               props.remove("sqlserver");
               props.remove("datenbank");
               Einstellungen.setOptions(new Options.Build(props).build());
               ArbeitRechnungFactoryProductiv factory = (ArbeitRechnungFactoryProductiv) ArbeitRechnungFactory.instance;
               factory.setOptions(Einstellungen.getInstance().getEinstellungen());
               
               try {
                  checkSingleKlient(factory);
               } catch (IOException e) {
                  throw new IllegalStateException("Error loading Single Klient from Resources", e);
               }
               KlientenEditor klienteneditor = new KlientenEditor(null);
               klienteneditor.setSingleKlientMode();
               klienteneditor.setFilter(KlientenEditor.NICHTEINGEREICHTE);
               klienteneditor.setVisible(true);
            }
         }

         private void checkSingleKlient(ArbeitRechnungFactoryProductiv factory) throws IOException {
            KlientenEditorPersister pers = factory.getPersister(KlientenEditorPersister.class);
            if(pers.getAllKlienten().isEmpty()) {
               Properties props = new Properties();
               props.load(getClass().getResourceAsStream("/single_klient.properties"));
               Klient klient = pers.createNewAuftraggeber();
               final int id = klient.getKlienten_id();
               for (Object keyO:props.keySet()) {
                  String key = keyO.toString();
                  String value = props.getProperty(key);
                  if(value != null && value.trim().isEmpty() == false) {
                     pers.speicherWert(id, key, value);  
                  }
               }
            }
         }
      });
   }

   protected static void changeColumnDefault(ArbeitRechnungFactory factory) {

      Verbindung_mysql vb = (Verbindung_mysql) Verbindung.getVerbindung(Einstellungen.getInstance().getEinstellungen().getProperties());
      try {
         Connection conn = vb.getConnection();
         DatabaseMetaData dbMeta = conn.getMetaData();
         ResultSet rs = dbMeta.getColumns(conn.getCatalog(), null, "rechnungen", "texdatei");
         if (rs.next()) {
            String columnDefaultVal = rs.getString("COLUMN_DEF");
            if(columnDefaultVal == null) {
               conn.createStatement().execute("ALTER TABLE rechnungen CHANGE COLUMN `texdatei` `texdatei` VARCHAR(255) NULL DEFAULT NULL");
            }
         }
      } catch (SQLException e) {
         JOptionPane.showMessageDialog(null, "Database Exception: " + e.getMessage() + "\n" + e.getStackTrace(), "Error on Database update", JOptionPane.ERROR_MESSAGE);
      }
   }
}
