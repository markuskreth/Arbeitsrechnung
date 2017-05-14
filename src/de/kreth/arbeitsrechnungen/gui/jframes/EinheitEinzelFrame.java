/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Einheit_einzel.java
 * Created on 21.05.2009, 16:18:44
 */

package de.kreth.arbeitsrechnungen.gui.jframes;

/**
 * @author markus
 */
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

import org.apache.log4j.Logger;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.MySqlDate;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.persister.AngebotPersister;
import de.kreth.arbeitsrechnungen.persister.KlientPersister;

public class EinheitEinzelFrame extends JFrame {

   private static final long serialVersionUID = 3963303174102985288L;
   Logger logger = Logger.getLogger(getClass());
   private NumberFormat preisFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

   private Options optionen;

   private int klient;
   private int einheit = -1;
   private boolean zusatz1 = false;
   private boolean zusatz2 = false;
   private String zusatz1_name = "";
   private String zusatz2_name = "";
   private Vector<Integer> angeboteliste;
   private KlientPersister klientPersister;
   private AngebotPersister angebotPersister;

   /**
    * Sollte nicht benutzt werden!
    * Oder nur in Kombination mit setKlient()
    * Creates new form Einheit_einzel
    */
   public EinheitEinzelFrame() {
      this(1, 2);
   }

   /**
    * Neuen Datensatz anlegen
    * Creates new form Einheit_einzel
    */
   public EinheitEinzelFrame(int klient) {
      this(klient, -1);
   }

   /**
    * Bestehenden Datensatz edieren
    * Creates new form Einheit_einzel
    */
   public EinheitEinzelFrame(int klient, int einheit) {

      optionen = Einstellungen.getInstance().getEinstellungen();

      klientPersister = new KlientPersister(optionen);
      angebotPersister = new AngebotPersister(optionen);
      
      this.klient = klient;
      initComponents();
      MaskFormatter startmask;
      MaskFormatter endemask;
      try {
         startmask = new MaskFormatter("##:##");
         startmask.setPlaceholderCharacter('_');
         startmask.install(jFormattedTextFieldStart);
         endemask = new MaskFormatter("##:##");
         endemask.setPlaceholderCharacter('_');
         endemask.install(jFormattedTextFieldEnde);
      } catch (Exception e) {
         e.printStackTrace();
      }
      initAngebote();
      setAuftraggeber();
      setZusaetze();
      if (einheit > -1) {
         this.einheit = einheit;
         setEinheit();
      }
   }

   private void setZusaetze() {
      if (this.zusatz1)
         this.jLabel8.setText(zusatz1_name);
      else {
         this.jLabel8.setVisible(false);
         this.jTextFieldZusatz1.setVisible(false);
      }
      if (this.zusatz2)
         this.jLabel9.setText(zusatz2_name);
      else {
         this.jLabel9.setVisible(false);
         this.jTextFieldZusatz2.setVisible(false);
      }
   }

   private void setAuftraggeber() {

      KlientPersister.Auftraggeber auftraggeber = klientPersister.getAuftraggeber(this.klient);

      if(auftraggeber != null) {
         this.jTextField1.setText(String.valueOf(this.klient));
   
         this.jTextField6.setText(auftraggeber.getName());
         
         this.zusatz1 = auftraggeber.hasZusatz1();
         this.zusatz2 = auftraggeber.hasZustz2();
         this.zusatz1_name = auftraggeber.getZusatz1();
         this.zusatz2_name = auftraggeber.getZusatz2();
      }
   }

   private void setEinheit() {
      // Füllt das Formular mit existierenden Feldern
      if (this.einheit > -1) {

         Einheit e = klientPersister.getEinheitById(this.einheit);

         if (e.getKlientenId() != this.klient) {
            String msg = "Achtung!!!\n" + "Klienten_id des Konstruktors stimmt nicht mit der des übegebenen" + " Datensatzes überein!" + "\nDatensatz-Klient: " + e.getKlientenId()
                  + "\nKonstruktor-Klient: " + klient;
            JOptionPane.showMessageDialog(this, msg);
         }
         // Set Angebot-Combobox
         this.jComboBoxAngebot.setSelectedIndex(this.angeboteliste.indexOf(Integer.valueOf(e.getAngebotId())));

         // Set Uhrzeit Beginn
         String stzeit = DateFormat.getTimeInstance().format(e.getBeginn());
         stzeit = stzeit.substring(0, stzeit.length() - 2);
         this.jFormattedTextFieldStart.setText(stzeit);

         // Set Uhrzeit Ende
         stzeit = DateFormat.getTimeInstance().format(e.getEnde());
         stzeit = stzeit.substring(0, stzeit.length() - 2);
         this.jFormattedTextFieldEnde.setText(stzeit);
         this.jDateChooserDatum.setDate(e.getDatum());
         this.jTextFieldPreisAenderung.setText(String.valueOf(e.getPreisAenderung()));
         this.jTextFieldZusatz1.setText(e.getZusatz1());
         this.jTextFieldZusatz2.setText(e.getZusatz2());
         this.jDateChooserEingereicht.setDate(e.getRechnungDatum());
         this.jDateChooserBezahlt.setDate(e.getBezahltDatum());
      } else {
         JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Edieren!", "Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
         this.setVisible(false);
         this.dispose();
      }
   }

   private void initAngebote() {
      this.jComboBoxAngebot.removeAllItems();
      this.angeboteliste = new Vector<Integer>();

      List<Angebot> angebote = angebotPersister.getForKlient(this.klient);
      
      for (Angebot a: angebote) {

         String datensatz;
         datensatz = a.getInhalt() + "|" + preisFormat.format(a.getPreis());
         this.jComboBoxAngebot.addItem(datensatz);
         this.angeboteliste.addElement(Integer.valueOf(a.getAngebote_id()));
      }
      
   }

   private void saveData() {
      // Wenn this.einheit = -1 dann existiert der Datensatz noch nicht und
      // muss angelegt werden.

      double preis = 0.0;
      long dauer = 0;

      final Calendar einheitDate = this.jDateChooserDatum.getCalendar();

      MySqlDate tmpdate = new MySqlDate(einheitDate);
      String datum = tmpdate.getSqlDate();

      String eingereichtDatum = "NULL";
      int isEingereicht = 0;
      String bezahltDatum = "NULL";
      int isBezahlt = 0;

      String sqlBeginn;
      String sqlEnde;

      if (this.jDateChooserEingereicht.getDate() != null) {
         logger.debug("Eingereicht: " + DateFormat.getDateInstance().format(this.jDateChooserEingereicht.getDate()));
         tmpdate = new MySqlDate(this.jDateChooserEingereicht.getDate());
         eingereichtDatum = tmpdate.getSqlDate();
         isEingereicht = 1;
      }

      if (this.jDateChooserBezahlt.getDate() != null) {
         logger.debug("Bezahlt: " + DateFormat.getDateInstance().format(this.jDateChooserBezahlt.getDate()));
         tmpdate = new MySqlDate(this.jDateChooserBezahlt.getDate());
         bezahltDatum = tmpdate.getSqlDate();
         isBezahlt = 1;
      }

      // Setzten der Angebot-Elemente für diese Einheit
      int angebot_id = this.angeboteliste.elementAt(this.jComboBoxAngebot.getSelectedIndex()).intValue();
      
      Angebot a = angebotPersister.getAngebot(angebot_id);
      if(a != null) {

         preis = Math.round((a.getPreis() + Double.parseDouble(this.jTextFieldPreisAenderung.getText())) * 100);
         preis = preis / 100;

         if (a.isPreis_pro_stunde()) {
            GregorianCalendar startcal = new GregorianCalendar();
            startcal.setTime(einheitDate.getTime());
            String starttext = this.jFormattedTextFieldStart.getText();
            String[] startfeld = starttext.split(":");

            startcal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(startfeld[0]));
            startcal.set(GregorianCalendar.MINUTE, Integer.parseInt(startfeld[1]));

            GregorianCalendar endecal = new GregorianCalendar();
            endecal.setTime(einheitDate.getTime());
            String endetext = this.jFormattedTextFieldEnde.getText();
            String[] endefeld = endetext.split(":");

            endecal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(endefeld[0]));
            endecal.set(GregorianCalendar.MINUTE, Integer.parseInt(endefeld[1]));

            sqlBeginn = new MySqlDate(startcal).getSqlDate();
            sqlEnde = new MySqlDate(endecal).getSqlDate();

            dauer = Math.round(((double) endecal.getTime().getTime() - startcal.getTime().getTime()) / (60. * 1000.));

            preis = Math.round(((double) dauer / 60 * preis) * 100);
            preis = preis / 100;
            logger.debug("Dauer: " + dauer + " Minuten\n" + "Dauer: " + (double) dauer / 60 + " Stunden\n Preis: " + preis);

            try {
               
               angebotPersister.storeEinheit(this.klient, this.einheit, preis, dauer, datum, 
                     eingereichtDatum, isEingereicht, bezahltDatum, isBezahlt, sqlBeginn, sqlEnde, angebot_id
                     , this.jTextFieldZusatz1.getText(), this.jTextFieldZusatz2.getText(), this.jTextFieldPreisAenderung.getText());
            } catch (SQLException e) {
               logger.error("Error storing Einheit", e);
            }
         }
      }

      this.setVisible(false);
      this.dispose();
   }

   /**
    * This method is called from within the constructor to
    * initialize the form.
    */
   private void initComponents() {

      jLabel1 = new javax.swing.JLabel();
      jTextField1 = new javax.swing.JTextField();
      jTextFieldPreisAenderung = new javax.swing.JTextField();
      jComboBoxAngebot = new javax.swing.JComboBox<String>();
      jLabel2 = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();
      jLabel4 = new javax.swing.JLabel();
      jLabel5 = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      jLabel7 = new javax.swing.JLabel();
      jTextField6 = new javax.swing.JTextField();
      jFormattedTextFieldStart = new javax.swing.JFormattedTextField();
      jFormattedTextFieldEnde = new javax.swing.JFormattedTextField();
      jDateChooserDatum = new com.toedter.calendar.JDateChooser();
      Calendar now = new GregorianCalendar();
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MILLISECOND, 0);
      jDateChooserDatum.setCalendar(now);

      jButton1 = new javax.swing.JButton();
      jButton2 = new javax.swing.JButton();
      jTextFieldZusatz2 = new javax.swing.JTextField();
      jTextFieldZusatz1 = new javax.swing.JTextField();
      jLabel8 = new javax.swing.JLabel();
      jLabel9 = new javax.swing.JLabel();
      jDateChooserEingereicht = new com.toedter.calendar.JDateChooser();
      jDateChooserBezahlt = new com.toedter.calendar.JDateChooser();
      jLabel10 = new javax.swing.JLabel();
      jLabel11 = new javax.swing.JLabel();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

      setTitle(resourceMap.getString("Form.title")); // NOI18N
      setName("Form"); // NOI18N

      jLabel1.setFont(Font.getFont(resourceMap.getString("jLabel1.font"))); // NOI18N
      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setName("jLabel1"); // NOI18N

      jTextField1.setEditable(false);
      jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
      jTextField1.setEnabled(false);
      jTextField1.setName("jTextField1"); // NOI18N

      jTextFieldPreisAenderung.setText(resourceMap.getString("jTextFieldPreisAenderung.text")); // NOI18N
      jTextFieldPreisAenderung.setMinimumSize(new java.awt.Dimension(70, 60));
      jTextFieldPreisAenderung.setName("jTextFieldPreisAenderung"); // NOI18N

      jComboBoxAngebot.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "Doppelstunde Untericht + Computer|24€", "Item 2" }));
      jComboBoxAngebot.setName("jComboBoxAngebot"); // NOI18N

      jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
      jLabel2.setName("jLabel2"); // NOI18N

      jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
      jLabel3.setName("jLabel3"); // NOI18N

      jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
      jLabel4.setName("jLabel4"); // NOI18N

      jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
      jLabel5.setName("jLabel5"); // NOI18N

      jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
      jLabel6.setName("jLabel6"); // NOI18N

      jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
      jLabel7.setName("jLabel7"); // NOI18N

      jTextField6.setEditable(false);
      jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
      jTextField6.setEnabled(false);
      jTextField6.setName("jTextField6"); // NOI18N

      jFormattedTextFieldStart.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat
            .getTimeInstance(java.text.DateFormat.SHORT))));
      jFormattedTextFieldStart.setName("jFormattedTextFieldStart"); // NOI18N

      jFormattedTextFieldEnde.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat
            .getTimeInstance(java.text.DateFormat.SHORT))));
      jFormattedTextFieldEnde.setName("jFormattedTextFieldEnde"); // NOI18N

      jDateChooserDatum.setName("jDateChooserDatum"); // NOI18N

      jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
      jButton1.setName("jButton1"); // NOI18N
      jButton1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
      jButton2.setName("jButton2"); // NOI18N
      jButton2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButton2ActionPerformed(evt);
         }
      });

      jTextFieldZusatz2.setText(resourceMap.getString("jTextFieldZusatz2.text")); // NOI18N
      jTextFieldZusatz2.setName("jTextFieldZusatz2"); // NOI18N

      jTextFieldZusatz1.setText(resourceMap.getString("jTextFieldZusatz1.text")); // NOI18N
      jTextFieldZusatz1.setName("jTextFieldZusatz1"); // NOI18N

      jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
      jLabel8.setName("jLabel8"); // NOI18N

      jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
      jLabel9.setName("jLabel9"); // NOI18N

      jDateChooserEingereicht.setName("jDateChooserEingereicht"); // NOI18N

      jDateChooserBezahlt.setName("jDateChooserBezahlt"); // NOI18N

      jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
      jLabel10.setName("jLabel10"); // NOI18N

      jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
      jLabel11.setName("jLabel11"); // NOI18N

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                  .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1))
                              .addGroup(
                                    layout.createSequentialGroup()
                                          .addGap(30, 30, 30)
                                          .addGroup(
                                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel2).addComponent(jLabel3)
                                                      .addComponent(jLabel4).addComponent(jLabel5).addComponent(jLabel6).addComponent(jLabel7).addComponent(jLabel11))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                      .addGroup(
                                                            layout.createSequentialGroup()
                                                                  .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                  .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
                                                      .addComponent(jComboBoxAngebot, 0, 331, Short.MAX_VALUE)
                                                      .addGroup(
                                                            layout.createSequentialGroup()
                                                                  .addComponent(jTextFieldPreisAenderung, javax.swing.GroupLayout.PREFERRED_SIZE, 115,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addGap(203, 203, 203))
                                                      .addComponent(jDateChooserDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                      .addGroup(
                                                            layout.createSequentialGroup()
                                                                  .addGroup(
                                                                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                              .addGroup(
                                                                                    layout.createSequentialGroup()
                                                                                          .addGroup(
                                                                                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                                      .addComponent(jFormattedTextFieldEnde,
                                                                                                            javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                      .addComponent(jFormattedTextFieldStart,
                                                                                                            javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                                                                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                          .addGroup(
                                                                                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                      .addComponent(jLabel8).addComponent(jLabel9)))
                                                                              .addComponent(jDateChooserEingereicht, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                  .addGroup(
                                                                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                              .addComponent(jTextFieldZusatz1, javax.swing.GroupLayout.Alignment.LEADING,
                                                                                    javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                                                              .addComponent(jTextFieldZusatz2, javax.swing.GroupLayout.Alignment.LEADING,
                                                                                    javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                                                              .addGroup(
                                                                                    layout.createSequentialGroup()
                                                                                          .addComponent(jLabel10)
                                                                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                          .addComponent(jDateChooserBezahlt, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                              .addGroup(
                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                    layout.createSequentialGroup().addContainerGap(262, Short.MAX_VALUE).addComponent(jButton2)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton1))).addContainerGap()));
      layout.setVerticalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                  layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBoxAngebot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jDateChooserDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                    .addComponent(jLabel5)
                                    .addComponent(jFormattedTextFieldStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8)
                                    .addComponent(jTextFieldZusatz1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jFormattedTextFieldEnde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)
                                    .addComponent(jTextFieldZusatz2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(
                                          layout.createSequentialGroup()
                                                .addGroup(
                                                      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                                            .addComponent(jTextFieldPreisAenderung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel7))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(
                                                      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel11)
                                                            .addComponent(jDateChooserEingereicht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(
                                          layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel10)
                                                .addComponent(jDateChooserBezahlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButton1).addComponent(jButton2)).addContainerGap()));

      pack();
   }

   private void jButton2ActionPerformed(ActionEvent evt) {
      // TODO Kontrolle der Eingaben auf vollständigkeit und richtigkeit

      saveData();
   }

   private void jButton1ActionPerformed(ActionEvent evt) {
      // Fenster schließen
      this.setVisible(false);
      this.dispose();
   }

   private javax.swing.JButton jButton1;
   private javax.swing.JButton jButton2;
   private javax.swing.JComboBox<String> jComboBoxAngebot;
   private com.toedter.calendar.JDateChooser jDateChooserBezahlt;
   private com.toedter.calendar.JDateChooser jDateChooserDatum;
   private com.toedter.calendar.JDateChooser jDateChooserEingereicht;
   private javax.swing.JFormattedTextField jFormattedTextFieldEnde;
   private javax.swing.JFormattedTextField jFormattedTextFieldStart;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel10;
   private javax.swing.JLabel jLabel11;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JLabel jLabel8;
   private javax.swing.JLabel jLabel9;
   private javax.swing.JTextField jTextField1;
   private javax.swing.JTextField jTextField6;
   private javax.swing.JTextField jTextFieldPreisAenderung;
   private javax.swing.JTextField jTextFieldZusatz1;
   private javax.swing.JTextField jTextFieldZusatz2;

}
