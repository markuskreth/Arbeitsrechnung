/*
 * FormRechnungen.java
 * Created on 22.09.2009, 19:08:09
 */

package de.kreth.arbeitsrechnungen.gui.panels;

/**
 * @author markus
 */
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_mysql;
import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.gui.dialogs.Kalenderauswahl;
import de.kreth.arbeitsrechnungen.gui.dialogs.RechnungDialog;

public class FormRechnungen extends JPanel {

   private Logger logger = Logger.getLogger(getClass());

   private static final long serialVersionUID = 5348708429129926664L;
   /** PropertyChangeEvent: eine Rechnung wurde geändert. */
   public static final String GEAENDERT = "FromRechnungen_geändert";
   /** PropertyChangeEvent: eine Rechnung wurde gelöscht. */
   public static final String GELOESCHT = "FromRechnungen_gelöscht";
   Verbindung verbindung;
   Properties optionen = new Properties();
   private PropertyChangeSupport pchListeners = new PropertyChangeSupport(this);

   int klienten_id;
   Vector<Rechnung> rechnungen = new Vector<Rechnung>();
   java.util.Properties sysprops = System.getProperties();
   private Window owner;

   /**
    * Creates new form FormRechnungen
    * 
    * @param owner
    */
   public FormRechnungen(Window owner, int klienten_id) {
      super();
      this.owner = owner;
      optionen = new Einstellungen().getEinstellungen();
      verbindung = new Verbindung_mysql(optionen);
      this.klienten_id = klienten_id;
      initComponents();
      logger.debug("Konstruktor FormRechnungen ausgeführt!");
      update();
   }

   private void update() {
      this.rechnungen.removeAllElements();

      String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, betrag, texdatei, pdfdatei,"
            + "adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum, geldeingang"
            + " FROM rechnungen WHERE klienten_id=" + this.klienten_id + ";";

      logger.debug("FormRechnungen: update: " + sql);

      try {
         ResultSet res_rechnungen = verbindung.query(sql);
         while (res_rechnungen.next()) {

            Date datum = res_rechnungen.getDate("datum");

            GregorianCalendar kalender = null;
            if (datum != null) {
               kalender = new GregorianCalendar();
               kalender.setTimeInMillis(datum.getTime());
            }

            GregorianCalendar kalender2 = new GregorianCalendar();
            kalender2.setTimeInMillis(res_rechnungen.getDate("zahldatum")
                  .getTime());

            Builder builder = new Rechnung.Builder()
                  .setRechnungen_id(res_rechnungen.getInt("rechnungen_id"))
                  .setKlienten_id(res_rechnungen.getInt("klienten_id"))
                  .setDatum(kalender)
                  .setRechnungnr(res_rechnungen.getString("rechnungnr"))
                  .setBetrag(res_rechnungen.getDouble("betrag"))
                  .setTexdatei(res_rechnungen.getString("texdatei"))
                  .setPdfdatei(res_rechnungen.getString("pdfdatei"))
                  .setAdresse(res_rechnungen.getString("adresse"))
                  .setZusatz1(res_rechnungen.getBoolean("zusatz1"))
                  .setZusatz2(res_rechnungen.getBoolean("zusatz2"))
                  .setZusammenfassungen(
                        res_rechnungen.getBoolean("zusammenfassungen"))
                  .setZahldatum(kalender2);

            if (res_rechnungen.getDate("geldeingang") != null) {
               GregorianCalendar kalender3 = new GregorianCalendar();
               kalender3.setTimeInMillis(res_rechnungen.getDate("geldeingang")
                     .getTime());

               builder.setGeldeingang(kalender3);
            }

            this.rechnungen.add(builder.build());
         }
         makeTable();
      } catch (SQLException e) {
         logger.error(e.getSQLState(), e);
      }
   }

   public void update(int klienten_id) {
      this.klienten_id = klienten_id;
      update();
   }

   private void makeTable() {
      this.jTable1.setModel(getMyModel());
   }

   private DefaultTableModel getMyModel() {
      DefaultTableModel mymodel = new DefaultTableModel() {

         /**
			 * 
			 */
         private static final long serialVersionUID = 4244937355506945056L;
         Class<?>[] types = new Class[] { java.lang.String.class,
               java.lang.String.class, java.lang.String.class,
               java.lang.String.class, java.lang.String.class };
         boolean[] canEdit = new boolean[] { false, false, false, false, false };

         @Override
         public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
         }

         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
         }
      };
      mymodel.setColumnIdentifiers(new String[] { "Datum", "Rechn.Nr",
            "Betrag", "Fällig", "Bezahlt" });
      java.text.DateFormat df;
      df = java.text.DateFormat.getDateInstance(
            java.text.DateFormat.DATE_FIELD, java.util.Locale.GERMAN);
      java.text.NumberFormat zf;
      zf = java.text.DecimalFormat.getCurrencyInstance(Locale.GERMANY);

      for (int i = 0; i < this.rechnungen.size(); i++) {

         Vector<String> zeile = new Vector<String>();
         zeile.add(df.format(rechnungen.elementAt(i).getDatum().getTime()));
         zeile.add(rechnungen.elementAt(i).getRechnungnr());
         zeile.add(zf.format(rechnungen.elementAt(i).getBetrag()));
         zeile.add(df.format(rechnungen.elementAt(i).getZahldatum().getTime()));
         if (rechnungen.elementAt(i).getGeldeingang() != null) {
            zeile.add(df.format(rechnungen.elementAt(i).getGeldeingang()
                  .getTime()));
         } else {
            zeile.add("");
         }
         mymodel.addRow(zeile);
      }
      return mymodel;
   }

   /**
    * This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed"
   // desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jScrollPane1 = new JScrollPane();
      jTable1 = new JTable();
      jButtonLoeschen = new JButton();
      jButtonAnsehen = new JButton();
      jButtonAendern = new JButton();
      jButtonBezahlt = new JButton();

      setName("Form"); // NOI18N

      jScrollPane1.setName("jScrollPane1"); // NOI18N

      jTable1.setAutoCreateRowSorter(true);
      jTable1.setModel(getMyModel());
      jTable1.setName("jTable1"); // NOI18N
      jScrollPane1.setViewportView(jTable1);

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass()
            .getSimpleName());

      jTable1
            .getColumnModel()
            .getColumn(0)
            .setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
      jTable1
            .getColumnModel()
            .getColumn(1)
            .setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N
      jTable1
            .getColumnModel()
            .getColumn(2)
            .setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
      jTable1
            .getColumnModel()
            .getColumn(3)
            .setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N

      jButtonLoeschen.setText(resourceMap.getString("jButtonLoeschen.text")); // NOI18N
      jButtonLoeschen.setName("jButtonLoeschen"); // NOI18N
      jButtonLoeschen.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent evt) {
            jButtonLoeschenActionPerformed(evt);
         }
      });

      jButtonAnsehen.setText(resourceMap.getString("jButtonAnsehen.text")); // NOI18N
      jButtonAnsehen.setName("jButtonAnsehen"); // NOI18N
      jButtonAnsehen.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent evt) {
            jButtonAnsehenActionPerformed(evt);
         }
      });

      jButtonAendern.setText(resourceMap.getString("jButtonAendern.text")); // NOI18N
      jButtonAendern.setName("jButtonAendern"); // NOI18N
      jButtonAendern.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent evt) {
            jButtonAendernActionPerformed(evt);
         }
      });

      jButtonBezahlt.setText(resourceMap.getString("jButtonBezahlt.text")); // NOI18N
      jButtonBezahlt.setName("jButtonBezahlt"); // NOI18N
      jButtonBezahlt.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent evt) {
            jButtonBezahltActionPerformed(evt);
         }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 480,
                  Short.MAX_VALUE)
            .addGroup(
                  GroupLayout.Alignment.TRAILING,
                  layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonBezahlt)
                        .addPreferredGap(
                              LayoutStyle.ComponentPlacement.RELATED, 77,
                              Short.MAX_VALUE)
                        .addComponent(jButtonAendern)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAnsehen)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonLoeschen).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(
            GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                  .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 258,
                        Short.MAX_VALUE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        layout.createParallelGroup(
                              GroupLayout.Alignment.BASELINE)
                              .addComponent(jButtonLoeschen)
                              .addComponent(jButtonAnsehen)
                              .addComponent(jButtonAendern)
                              .addComponent(jButtonBezahlt)).addContainerGap()));
   }// </editor-fold>//GEN-END:initComponents

   /**
    * Rechnung ansehen - Viewer in optionen aufrufen
    * 
    * @param evt
    */
   private void jButtonAnsehenActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonAnsehenActionPerformed
      String befehl = this.optionen.getProperty("pdfprogramm");
      if (this.jTable1.getSelectedRow() >= 0) {
         logger.error("Selected Row: " + this.jTable1.getSelectedRow());

         befehl += " " + this.optionen.getProperty("verzPdfFiles");
         if (!befehl.endsWith(sysprops.getProperty("file.separator"))) {
            befehl += sysprops.getProperty("file.separator");
         }
         befehl += this.rechnungen.elementAt(jTable1.getSelectedRow())
               .getPdfdatei();
         logger.debug("FormRechnungen: " + befehl);

         try {
            Runtime.getRuntime().exec(befehl);
         } catch (java.io.IOException e) {
            logger.error("FormRechnungen: PDFansehen: ", e);
         }
      } else {
         JOptionPane.showMessageDialog(null,
               "Zum Anzeigen bitte eine Rechnung auswählen.");
      }
   }// GEN-LAST:event_jButtonAnsehenActionPerformed

   private void jButtonAendernActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonAendernActionPerformed
      int rechnung_id = this.rechnungen
            .elementAt(this.jTable1.getSelectedRow()).getRechnungen_id();
      // System.out.println("Elemente in rechnungen: " +
      // this.rechnungen.size());
      // System.out.println("Selected Column: " +
      // this.jTable1.getSelectedColumn());
      // System.out.println("Geefundene Rechnung_id: " + rechnung_id);
      RechnungDialog dialog = new RechnungDialog(optionen, getOwner(),
            rechnung_id);
      dialog.setVisible(true);
      pchListeners.fireIndexedPropertyChange(GEAENDERT, rechnung_id, true,
            false);
   }// GEN-LAST:event_jButtonAendernActionPerformed

   private Window getOwner() {
      return this.owner;
   }

   private void jButtonLoeschenActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonLoeschenActionPerformed
      int rechnung_id = this.rechnungen
            .elementAt(this.jTable1.getSelectedRow()).getRechnungen_id();

      if (JOptionPane.showConfirmDialog(this.getParent(),
            "Wollen Sie die gewählte Rechnung endgültig löschen?",
            "Endgültige Löschung!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
         String sql = "UPDATE einheiten SET Rechnung_verschickt=null, Rechnung_Datum=null, rechnung_id=null"
               + " WHERE rechnung_id=" + rechnung_id + ";";
         logger.info("FormRechnungen: jButtonLoeschenActionPerformed: " + sql);

         try {
            if (verbindung.sql(sql)) {
               // Weiter nur, wenn update der einheiten erfolgreich war. Sollte
               // - weshalb nicht?
               sql = "DELETE from rechnungen WHERE rechnungen_id="
                     + rechnung_id + ";";
               if (verbindung.sql(sql)) {
                  sql = "Rechnung erfolgreich gelöscht!";
                  pchListeners.fireIndexedPropertyChange(GELOESCHT,
                        rechnung_id, true, false);
               } else {
                  sql = "Achtung! Rechnung konnte nicht gelöscht werden!";
               }
               JOptionPane.showMessageDialog(this.getParent(), sql);
            }
         } catch (SQLException e) {
            sql = "Achtung! Rechnung konnte nicht gelöscht werden!";
            JOptionPane.showMessageDialog(this.getParent(), sql);
         }
      }
   }// GEN-LAST:event_jButtonLoeschenActionPerformed

   private void jButtonBezahltActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonBezahltActionPerformed

      if (this.jTable1.getSelectedColumnCount() == 0) {
         JOptionPane
               .showMessageDialog(
                     this,
                     "Bitte wählen sie eine oder mehrere Rechnungen aus, um den Zahlungseingang zu bestätigen",
                     "Keine Rechnung ausgewählt!", JOptionPane.ERROR_MESSAGE);
      } else {

         int[] rechnung = this.jTable1.getSelectedRows();
         String in_klausel = buildInClauseForRechnungen(rechnung);
         // Datum abfragen
         java.sql.Date sql_date;
         Kalenderauswahl kalender = new Kalenderauswahl(null);
         kalender.setVisible(true);
         if (kalender.isBestaetigt() && kalender.getDatum() != null) { // TODO
                                                                       // Löschen
                                                                       // einer
                                                                       // Bezahlung
                                                                       // nicht
                                                                       // implementiert...
            String sql;

            sql_date = new java.sql.Date(kalender.getDatum().getTime());
            sql = "UPDATE einheiten SET Bezahlt=1, Bezahlt_Datum=\""
                  + sql_date.toString() + "\" WHERE rechnung_id IN "
                  + in_klausel + ";";

            logger.info("FormRechnungen: jButtonBezahltAction: " + sql);
            try {
               if (verbindung.sql(sql)) {
                  sql = "UPDATE rechnungen SET geldeingang=\""
                        + sql_date.toString() + "\" WHERE rechnungen_id IN "
                        + in_klausel + ";";
                  if (verbindung.sql(sql)) {
                     JOptionPane.showMessageDialog(null,
                           "Rechnung ist abgerechnet", "Rechnung bezahlt",
                           JOptionPane.INFORMATION_MESSAGE);
                     this.update();
                     for (int i = 1; i < rechnung.length; i++) {
                        pchListeners.fireIndexedPropertyChange(GEAENDERT,
                              this.rechnungen.elementAt(rechnung[i])
                                    .getRechnungen_id(), true, false);
                     }
                  } else {
                     JOptionPane
                           .showMessageDialog(
                                 null,
                                 "Rechnung ist nicht abgerechnet!!!\nDie Einheiten aber schon!!! ",
                                 "Achtung! Achtung! Achtung!",
                                 JOptionPane.ERROR_MESSAGE);
                  }
               } else {
                  JOptionPane.showMessageDialog(null,
                        "Rechnung ist nicht abgerechnet", "Achtung!",
                        JOptionPane.WARNING_MESSAGE);
               }
            } catch (SQLException e) {
               JOptionPane.showMessageDialog(null,
                     "Rechnung ist nicht abgerechnet",
                     "Achtung!\n" + e.getLocalizedMessage(),
                     JOptionPane.WARNING_MESSAGE);
            }
         }
      }
   }// GEN-LAST:event_jButtonBezahltActionPerformed

   private String buildInClauseForRechnungen(int[] rechnung) {
      String inClause = "("
            + this.rechnungen.elementAt(rechnung[0]).getRechnungen_id();

      for (int i = 1; i < rechnung.length; i++) {
         // rechnung[i] = this.rechnungen.elementAt(i).getRechnungen_id();
         inClause += ", "
               + this.rechnungen.elementAt(rechnung[i]).getRechnungen_id();
      }

      inClause += ")";
      return inClause;
   }

   @Override
   public void addPropertyChangeListener(
         java.beans.PropertyChangeListener listener) {
      pchListeners.addPropertyChangeListener(listener);
   }

   @Override
   public void removePropertyChangeListener(
         java.beans.PropertyChangeListener listener) {
      pchListeners.removePropertyChangeListener(listener);
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private JButton jButtonAendern;
   private JButton jButtonAnsehen;
   private JButton jButtonBezahlt;
   private JButton jButtonLoeschen;
   private JScrollPane jScrollPane1;
   private JTable jTable1;
   // End of variables declaration//GEN-END:variables

}
