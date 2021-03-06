/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AngebotDialog.java
 * Created on 29.04.2009, 15:15:15
 */

package de.kreth.arbeitsrechnungen.gui.dialogs;

/**
 * @author markus
 */
import java.awt.Font;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;

public class AngebotDialog extends javax.swing.JDialog {

   private static final long serialVersionUID = 5622435973507056306L;
   private Angebot angebot;

   int KlientenID = 0;
   
   NumberFormat nf = NumberFormat.getNumberInstance();
   AngebotPersister persister;

   /**
    * Creates new form AngebotDialog bei DatensatzID == -1 wird ein neuer
    * erstellt.
    */
   public AngebotDialog(Window parent, Angebot angebot) {
      super(parent);
      setModal(true);
      initComponents();

      persister = ArbeitRechnungFactory.getInstance().getPersister(AngebotPersister.class);
      nf.setMaximumFractionDigits(2);

      addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
               persister.close();
            }
      });
      // Bei null ist es ein neuer Datensatz
      this.angebot = angebot;

      // Neuer Datensatz
      if (this.angebot == null) {
         // Neuer Datensatz - Felder nur mit Probewerten füllen
         this.jTextFieldInhalt.setText("Pflichtfeld");
         this.jTextFieldPreis.setValue(Integer.valueOf(0));
      } else {
         // Vorhandenen Datensatz laden und edieren

         this.jTextFieldInhalt.setText(this.angebot.getInhalt());
         this.jTextFieldBeschreibung.setText(this.angebot.getBeschreibung());
         this.jTextFieldPreis.setText(nf.format(this.angebot.getPreis()));
         this.jCheckBox1.setSelected(this.angebot.isPreis_pro_stunde());
      }
   }

   /**
    * Creates new form AngebotDialog mit neuem Datensatz
    */
   public AngebotDialog(Window parent, int KlientenID) {
      this(parent, null);
      this.KlientenID = KlientenID;
   }

   private void initComponents() {

      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      jTextFieldInhalt = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      jTextFieldPreis = new javax.swing.JFormattedTextField();
      jLabel4 = new javax.swing.JLabel();
      jTextFieldBeschreibung = new javax.swing.JTextField();
      jButtonSpeichern = new javax.swing.JButton();
      jButtonVerwerfen = new javax.swing.JButton();
      jCheckBox1 = new javax.swing.JCheckBox();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      setName("Form"); // NOI18N

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

      jLabel1.setFont(Font.getFont(resourceMap.getString("jLabel1.font"))); // NOI18N
      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setName("jLabel1"); // NOI18N

      jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
      jLabel2.setName("jLabel2"); // NOI18N

      jTextFieldInhalt.setText(resourceMap.getString("jTextFieldInhalt.text")); // NOI18N
      jTextFieldInhalt.setName("jTextFieldInhalt"); // NOI18N
      jTextFieldInhalt.setSelectionEnd(jTextFieldInhalt.getText().length());
      jTextFieldInhalt.addFocusListener(new java.awt.event.FocusAdapter() {

         @Override
         public void focusGained(java.awt.event.FocusEvent evt) {
            jTextFieldInhaltFocusGained(evt);
         }
      });

      jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
      jLabel3.setName("jLabel3"); // NOI18N

      jTextFieldPreis.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
      jTextFieldPreis.setName("jTextFieldPreis"); // NOI18N
      jTextFieldPreis.setSelectionEnd(jTextFieldPreis.getText().length());
      jTextFieldPreis.addFocusListener(new java.awt.event.FocusAdapter() {

         @Override
         public void focusGained(java.awt.event.FocusEvent evt) {
            jTextFieldPreisFocusGained(evt);
         }
      });

      jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
      jLabel4.setName("jLabel4"); // NOI18N

      jTextFieldBeschreibung.setText(resourceMap.getString("jTextFieldBeschreibung.text")); // NOI18N
      jTextFieldBeschreibung.setName("jTextFieldBeschreibung"); // NOI18N
      jTextFieldBeschreibung.setSelectionEnd(jTextFieldBeschreibung.getText().length());
      jTextFieldBeschreibung.addFocusListener(new java.awt.event.FocusAdapter() {

         @Override
         public void focusGained(java.awt.event.FocusEvent evt) {
            jTextFieldBeschreibungFocusGained(evt);
         }
      });

      jButtonSpeichern.setText(resourceMap.getString("jButtonSpeichern.text")); // NOI18N
      jButtonSpeichern.setName("jButtonSpeichern"); // NOI18N
      jButtonSpeichern.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonSpeichernActionPerformed(evt);
         }
      });

      jButtonVerwerfen.setText(resourceMap.getString("jButtonVerwerfen.text")); // NOI18N
      jButtonVerwerfen.setName("jButtonVerwerfen"); // NOI18N
      jButtonVerwerfen.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonVerwerfenActionPerformed(evt);
         }
      });

      jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
      jCheckBox1.setName("jCheckBox1"); // NOI18N

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                  layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(
                                          layout.createSequentialGroup().addComponent(jButtonSpeichern)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE).addComponent(jButtonVerwerfen))
                                    .addGroup(
                                          javax.swing.GroupLayout.Alignment.LEADING,
                                          layout.createSequentialGroup()
                                                .addGroup(
                                                      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel4).addComponent(jLabel3)
                                                            .addComponent(jLabel2).addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(
                                                      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(
                                                                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(jTextFieldInhalt)
                                                                        .addComponent(jTextFieldPreis, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jTextFieldBeschreibung, javax.swing.GroupLayout.PREFERRED_SIZE, 264,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(jCheckBox1))))
                        .addContainerGap(21, Short.MAX_VALUE)));
      layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                  layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(54, 54, 54)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jTextFieldInhalt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jTextFieldPreis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jTextFieldBeschreibung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCheckBox1)
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButtonSpeichern).addComponent(jButtonVerwerfen))
                        .addContainerGap(62, Short.MAX_VALUE)));

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void jButtonSpeichernActionPerformed(java.awt.event.ActionEvent evt) {

      String preisString;
      preisString = this.jTextFieldPreis.getText();

      // Falls ein leerzeichen gefunden wird, nur den vorderen Teil nuten (€)
      preisString = preisString.substring(0, preisString.indexOf(" "));
      Double preis;
      try {
         preis = Double.valueOf(preisString);
      } catch (NumberFormatException e) {
         preisString = preisString.replace(",", ".");
         preis = Double.valueOf(preisString);
      }

      String beschreibung = jTextFieldBeschreibung.getText();
      String inhalt = jTextFieldInhalt.getText();
      boolean preisProStunde = jCheckBox1.isSelected();

      int angebotId;
      if (angebot == null) {
         angebotId = -1;
      } else {
         angebotId = angebot.getAngebote_id();
      }

      Angebot angebot = new Angebot.Builder(inhalt, preis.doubleValue()).angebotId(angebotId).beschreibung(beschreibung).preis_pro_stunde(preisProStunde).build();
      persister.insertOrUpdateAngebot(KlientenID, angebot);

      this.setVisible(false);
      this.dispose();
   }

   private void jButtonVerwerfenActionPerformed(java.awt.event.ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
   }

   private void jTextFieldInhaltFocusGained(java.awt.event.FocusEvent evt) {
      if (evt.getSource() instanceof javax.swing.JTextField) {
         javax.swing.JTextField tf = (javax.swing.JTextField) evt.getSource();
         tf.setSelectionStart(0);
         tf.setSelectionEnd(tf.getText().length());
      }
   }

   private void jTextFieldPreisFocusGained(java.awt.event.FocusEvent evt) {
      if (evt.getSource() instanceof javax.swing.JTextField) {
         javax.swing.JTextField tf = (javax.swing.JTextField) evt.getSource();
         tf.setSelectionStart(0);
         tf.setSelectionEnd(tf.getText().length());
      }
   }

   private void jTextFieldBeschreibungFocusGained(java.awt.event.FocusEvent evt) {
      if (evt.getSource() instanceof javax.swing.JTextField) {
         javax.swing.JTextField tf = (javax.swing.JTextField) evt.getSource();
         tf.setSelectionStart(0);
         tf.setSelectionEnd(tf.getText().length());
      }
   }

   /*
    * public static void main(String args[]) {
    * java.awt.EventQueue.invokeLater(new Runnable() { public void run() {
    * AngebotDialog dialog = new AngebotDialog(new javax.swing.JFrame(), true);
    * dialog.addWindowListener(new java.awt.event.WindowAdapter() { public void
    * windowClosing(java.awt.event.WindowEvent e) { System.exit(0); } });
    * dialog.setVisible(true); } }); }
    */
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton jButtonSpeichern;
   private javax.swing.JButton jButtonVerwerfen;
   private javax.swing.JCheckBox jCheckBox1;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JTextField jTextFieldBeschreibung;
   private javax.swing.JTextField jTextFieldInhalt;
   private javax.swing.JFormattedTextField jTextFieldPreis;
   // End of variables declaration//GEN-END:variables

}
