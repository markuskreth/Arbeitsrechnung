/*
 * Arbeitsstunden.java
 * Created on 03.05.2009, 19:48:36
 */

package de.kreth.arbeitsrechnungen.gui.jframes;

/**
 * @author markus
 */

import java.awt.Font;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;

public class Arbeitsstunden extends JFrame {

   private static final long serialVersionUID = 4796722096135537141L;
   private Logger logger = LoggerFactory.getLogger(getClass());

   private KlientenEditorPersister persister;

   boolean ready = true;
   private List<Klient> allKlienten;

   /**
    * Creates new form Arbeitsstunden
    * 
    */
   public Arbeitsstunden() {
      ready = false;
      persister = ArbeitRechnungFactory.getInstance().getPersister(KlientenEditorPersister.class);
      logger.debug("Init beginnt:");

      initComponents();

      addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
               persister.close();
            }
      });
      ready = true;
   }

   /**
    * This method is called from within the constructor to initialize the form.
    */
   private void initComponents() {

      jComboBoxKunden = new JComboBox<String>();
      jLabel1 = new JLabel();
      jButton2 = new JButton();
      arbeitsstundenTabelle1 = new de.kreth.arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle(this);

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      setName("Form"); // NOI18N

      allKlienten = persister.getAllKlienten();
      this.jComboBoxKunden.removeAllItems();

      Vector<String> auftraggeberliste = new Vector<String>();

      for (int j = 0; j < allKlienten.size(); j++) {
         auftraggeberliste.add(allKlienten.get(j).getAuftraggeber());
      }

      jComboBoxKunden.setModel(new DefaultComboBoxModel<String>(auftraggeberliste));
      jComboBoxKunden.setName("jComboBoxKunden"); // NOI18N
      jComboBoxKunden.addItemListener(new ItemListener() {

         @Override
         public void itemStateChanged(ItemEvent evt) {
            jComboBoxKundenItemStateChanged(evt);
         }
      });

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

      jLabel1.setFont(Font.getFont(resourceMap.getString("jLabel1.font"))); // NOI18N
      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setName("jLabel1"); // NOI18N

      jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
      jButton2.setName("jButton2"); // NOI18N
      jButton2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButton2ActionPerformed(evt);
         }
      });

      arbeitsstundenTabelle1.setName("arbeitsstundenTabelle1"); // NOI18N

      GroupLayout layout = new GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(
                  GroupLayout.Alignment.TRAILING,
                  layout.createSequentialGroup().addContainerGap().addComponent(jComboBoxKunden, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 361, Short.MAX_VALUE).addComponent(jButton2).addContainerGap())
            .addComponent(arbeitsstundenTabelle1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
            .addGroup(
                  layout.createSequentialGroup().addContainerGap().addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(383, Short.MAX_VALUE)));
      layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
                  .addGap(12, 12, 12)
                  .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButton2)
                              .addComponent(jComboBoxKunden, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(arbeitsstundenTabelle1, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)));

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void jComboBoxKundenItemStateChanged(ItemEvent evt) {
      if (ready) {
         int selectedIndex = jComboBoxKunden.getSelectedIndex();
         Klient klient = allKlienten.get(selectedIndex);
         arbeitsstundenTabelle1.update(klient.getKlienten_id());
         logger.debug("Tabelle aktualisiert - Klient=" + klient.getAuftraggeber() + ", id=" + klient.getKlienten_id());
      }
   }

   private void jButton2ActionPerformed(ActionEvent evt) {
      //
      this.setVisible(false);
      this.dispose();
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private de.kreth.arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle arbeitsstundenTabelle1;
   private JButton jButton2;
   private JComboBox<String> jComboBoxKunden;
   private JLabel jLabel1;
   // End of variables declaration//GEN-END:variables

}
