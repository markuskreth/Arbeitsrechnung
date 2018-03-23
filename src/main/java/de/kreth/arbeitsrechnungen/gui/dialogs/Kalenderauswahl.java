/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Kalenderauswahl.java
 * Created on 30.07.2009, 20:27:44
 */

package de.kreth.arbeitsrechnungen.gui.dialogs;

import java.awt.Window;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.LayoutStyle;

import com.toedter.calendar.JCalendar;

/**
 * @author markus
 */
public class Kalenderauswahl extends JDialog {

   private static final long serialVersionUID = -6079730510220316011L;
   Date datum;
   boolean bestaetigt = false;

   /** Creates new form Kalenderauswahl */
   public Kalenderauswahl(Window parent) {
      super(parent);
      setModal(true);
      initComponents();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed"
   // desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jButton1 = new JButton();
      jCalendar1 = new JCalendar();
      jButtonOK = new JButton();
      jButtonCancel = new JButton();
      jButtonDatumLoeschen = new JButton();

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
      jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
      jButton1.setName("jButton1"); // NOI18N

      setName("Form"); // NOI18N

      jCalendar1.setName("jCalendar1"); // NOI18N

      jButtonOK.setText(resourceMap.getString("jButtonOK.text")); // NOI18N
      jButtonOK.setName("jButtonOK"); // NOI18N
      jButtonOK.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonOKActionPerformed(evt);
         }
      });

      jButtonCancel.setText(resourceMap.getString("jButtonCancel.text")); // NOI18N
      jButtonCancel.setName("jButtonCancel"); // NOI18N
      jButtonCancel.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonCancelActionPerformed(evt);
         }
      });

      jButtonDatumLoeschen.setText(resourceMap.getString("jButtonDatumLoeschen.text")); // NOI18N
      jButtonDatumLoeschen.setName("jButtonDatumLoeschen"); // NOI18N
      jButtonDatumLoeschen.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonDatumLoeschenActionPerformed(evt);
         }
      });

      GroupLayout layout = new GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                              .addComponent(jCalendar1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                              .addGroup(
                                    GroupLayout.Alignment.LEADING,
                                    layout.createSequentialGroup().addComponent(jButtonOK, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonDatumLoeschen)
                                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(jButtonCancel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addContainerGap().addComponent(jCalendar1, GroupLayout.PREFERRED_SIZE, 159, Short.MAX_VALUE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButtonOK).addComponent(jButtonDatumLoeschen).addComponent(jButtonCancel))
                  .addContainerGap()));

      pack();
   }// </editor-fold>//GEN-END:initComponents

   public boolean isBestaetigt() {
      return bestaetigt;
   }

   public Date getDatum() {
      return datum;
   }

   private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {
      datum = this.jCalendar1.getDate();
      bestaetigt = true;
      this.setVisible(false);
   }

   private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
      datum = null;
      this.setVisible(false);
   }

   private void jButtonDatumLoeschenActionPerformed(java.awt.event.ActionEvent evt) {
      datum = null;
      bestaetigt = true;
      this.setVisible(false);
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private JButton jButton1;
   public JButton jButtonCancel;
   private JButton jButtonDatumLoeschen;
   public JButton jButtonOK;
   public JCalendar jCalendar1;
   // End of variables declaration//GEN-END:variables

}