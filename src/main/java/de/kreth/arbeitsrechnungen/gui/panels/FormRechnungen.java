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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.business.RechnungSystemExecutionService;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.gui.dialogs.Kalenderauswahl;
import de.kreth.arbeitsrechnungen.gui.dialogs.RechnungDialog;
import de.kreth.arbeitsrechnungen.persister.RechnungPersister;

public class FormRechnungen extends JPanel {

   /** PropertyChangeEvent: eine Rechnung wurde geändert. */
   public static final String GEAENDERT = "FromRechnungen_geändert";

   /** PropertyChangeEvent: eine Rechnung wurde gelöscht. */
   public static final String GELOESCHT = "FromRechnungen_gelöscht";

   private static final long serialVersionUID = 5348708429129926664L;

   private Logger logger = LoggerFactory.getLogger(getClass());

   private PropertyChangeSupport pchListeners = new PropertyChangeSupport(this);

   private int klienten_id;
   private Vector<Rechnung> rechnungen = new Vector<Rechnung>();
   private Window owner;

   private RechnungPersister rechnungPersister;

   /**
    * Creates new form FormRechnungen
    * 
    * @param owner
    */
   public FormRechnungen(Window owner, int klienten_id) {
      super();
      this.owner = owner;
      rechnungPersister = ArbeitRechnungFactory.getInstance().getPersister(RechnungPersister.class);

      this.klienten_id = klienten_id;
      initComponents();
      logger.debug("Konstruktor FormRechnungen ausgeführt!");
      update();
   }

   private void update() {
      this.rechnungen.removeAllElements();

      this.rechnungen.addAll(rechnungPersister.getRechnungenForKlient(this.klienten_id));

      makeTable();

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
         Class<?>[] types = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class };
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
      mymodel.setColumnIdentifiers(new String[] { "Datum", "Rechn.Nr", "Betrag", "Fällig", "Bezahlt" });
      java.text.DateFormat df;
      df = java.text.DateFormat.getDateInstance(java.text.DateFormat.DATE_FIELD, java.util.Locale.GERMAN);
      java.text.NumberFormat zf;
      zf = java.text.DecimalFormat.getCurrencyInstance(Locale.GERMANY);

      for (int i = 0; i < this.rechnungen.size(); i++) {

         Vector<String> zeile = new Vector<String>();
         zeile.add(df.format(rechnungen.elementAt(i).getDatum().getTime()));
         zeile.add(rechnungen.elementAt(i).getRechnungnr());
         zeile.add(zf.format(rechnungen.elementAt(i).getBetrag()));
         zeile.add(df.format(rechnungen.elementAt(i).getZahldatum().getTime()));
         if (rechnungen.elementAt(i).getGeldeingang() != null) {
            zeile.add(df.format(rechnungen.elementAt(i).getGeldeingang().getTime()));
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

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

      jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
      jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N
      jTable1.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
      jTable1.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N

      jButtonLoeschen.setText(resourceMap.getString("jButtonLoeschen.text")); // NOI18N
      jButtonLoeschen.setName("jButtonLoeschen"); // NOI18N
      jButtonLoeschen.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButtonLoeschenActionPerformed(evt);
         }
      });

      jButtonAnsehen.setText(resourceMap.getString("jButtonAnsehen.text")); // NOI18N
      jButtonAnsehen.setName("jButtonAnsehen"); // NOI18N
      jButtonAnsehen.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButtonAnsehenActionPerformed(evt);
         }
      });

      jButtonAendern.setText(resourceMap.getString("jButtonAendern.text")); // NOI18N
      jButtonAendern.setName("jButtonAendern"); // NOI18N
      jButtonAendern.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButtonAendernActionPerformed(evt);
         }
      });

      jButtonBezahlt.setText(resourceMap.getString("jButtonBezahlt.text")); // NOI18N
      jButtonBezahlt.setName("jButtonBezahlt"); // NOI18N
      jButtonBezahlt.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButtonBezahltActionPerformed(evt);
         }
      });

      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
            .addGroup(
                  GroupLayout.Alignment.TRAILING,
                  layout.createSequentialGroup().addContainerGap().addComponent(jButtonBezahlt).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                        .addComponent(jButtonAendern).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonAnsehen)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonLoeschen).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                  .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButtonLoeschen).addComponent(jButtonAnsehen).addComponent(jButtonAendern)
                              .addComponent(jButtonBezahlt)).addContainerGap()));
   }// </editor-fold>//GEN-END:initComponents

   /**
    * Rechnung ansehen - Viewer in optionen aufrufen
    * 
    * @param evt
    */
   private void jButtonAnsehenActionPerformed(ActionEvent evt) {

      if (this.jTable1.getSelectedRow() >= 0) {
         RechnungSystemExecutionService fileService = new RechnungSystemExecutionService();
         final Rechnung rechnungToShow = this.rechnungen.elementAt(jTable1.getSelectedRow());
         fileService.showRechnung(rechnungToShow);
      } else {
         JOptionPane.showMessageDialog(null, "Zum Anzeigen bitte eine Rechnung auswählen.");
      }
   }

   private void jButtonAendernActionPerformed(ActionEvent evt) {
      int rechnung_id = this.rechnungen.elementAt(this.jTable1.getSelectedRow()).getRechnungen_id();
      RechnungDialog dialog = new RechnungDialog(getOwner(), rechnung_id);
      dialog.setVisible(true);
      pchListeners.fireIndexedPropertyChange(GEAENDERT, rechnung_id, true, false);
   }

   private Window getOwner() {
      return this.owner;
   }

   private void jButtonLoeschenActionPerformed(ActionEvent evt) {
      Rechnung rechn = this.rechnungen.elementAt(this.jTable1.getSelectedRow());
      if (JOptionPane.showConfirmDialog(this.getParent(), "Wollen Sie die gewählte Rechnung endgültig löschen?", "Endgültige Löschung!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
         
         if(rechnungPersister.delete(rechn)) {
            // gelöscht
            JOptionPane.showMessageDialog(this.getParent(), "Rechnung erfolgreich gelöscht!");
            pchListeners.fireIndexedPropertyChange(GELOESCHT, rechn.getRechnungen_id(), true, false);
         } else {
            // nicht gelöscht.
            JOptionPane.showMessageDialog(this.getParent(), "Achtung! Rechnung konnte nicht gelöscht werden!");
         }
         
      }
   }

   private void jButtonBezahltActionPerformed(ActionEvent evt) {

      if (this.jTable1.getSelectedColumnCount() == 0) {
         JOptionPane.showMessageDialog(this, "Bitte wählen sie eine oder mehrere Rechnungen aus, um den Zahlungseingang zu bestätigen", "Keine Rechnung ausgewählt!",
               JOptionPane.ERROR_MESSAGE);
      } else {

         int[] rechnung = this.jTable1.getSelectedRows();
         String in_klausel = buildInClauseForRechnungen(rechnung);

         Kalenderauswahl kalender = new Kalenderauswahl(null);
         kalender.setVisible(true);
         // TODO Löschen einer Bezahlung nicht implementiert...
         
         if (kalender.isBestaetigt() && kalender.getDatum() != null) {
            if(rechnungPersister.setRechnungBezahlt(rechnung, in_klausel, kalender)) {
               JOptionPane.showMessageDialog(null, "Rechnung ist abgerechnet", "Rechnung bezahlt", JOptionPane.INFORMATION_MESSAGE);
               this.update();
               for (int i = 1; i < rechnung.length; i++) {
                  pchListeners.fireIndexedPropertyChange(GEAENDERT, this.rechnungen.elementAt(rechnung[i]).getRechnungen_id(), true, false);
               }
            } else {
               JOptionPane
               .showMessageDialog(null, "Rechnung ist nicht abgerechnet!!!\nDie Einheiten aber schon!!! ", "Achtung! Achtung! Achtung!", JOptionPane.ERROR_MESSAGE);
      }
         }
      }
   }

   private String buildInClauseForRechnungen(int[] rechnung) {
      String inClause = "(" + this.rechnungen.elementAt(rechnung[0]).getRechnungen_id();

      for (int i = 1; i < rechnung.length; i++) {
         // rechnung[i] = this.rechnungen.elementAt(i).getRechnungen_id();
         inClause += ", " + this.rechnungen.elementAt(rechnung[i]).getRechnungen_id();
      }

      inClause += ")";
      return inClause;
   }

   @Override
   public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
      pchListeners.addPropertyChangeListener(listener);
   }

   @Override
   public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
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
