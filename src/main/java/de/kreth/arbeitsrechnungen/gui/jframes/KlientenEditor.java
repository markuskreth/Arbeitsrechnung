/*
 * KlientenEditor.java
 * Umfassende Bearbeitung von Klientendaten, Angeboten und Zugriff auf
 * Arbeitsstunden
 * Created on 17.04.2009, 22:43:52
 */
package de.kreth.arbeitsrechnungen.gui.jframes;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import de.kreth.arbeitsrechnungen.ArbeitRechnungFactory;
import de.kreth.arbeitsrechnungen.data.Angebot;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.gui.LabelComponentBinding;
import de.kreth.arbeitsrechnungen.gui.dialogs.AngebotDialog;
import de.kreth.arbeitsrechnungen.gui.jframes.starttablemodels.LabledStringValueNoneditableTableModel;
import de.kreth.arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle;
import de.kreth.arbeitsrechnungen.gui.panels.FormRechnungen;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;

/**
 * Umfassende Bearbeitung von Klientendaten, Angeboten und Zugriff auf
 * Arbeitsstunden
 * 
 * @author markus
 */
public class KlientenEditor extends JDialog {

   private static final long serialVersionUID = 2229715761785683028L;

   public static final int EINGEREICHTE = 1;
   public static final int NICHTEINGEREICHTE = 2;
   public static final int OFFENE = 3;

   private List<Klient> allKlienten;
   private Klient currentKlient = null;
   private int currentIndex = -1;
   private List<Angebot> angebote;

   /**
    * Focusklasse anonym: Soll die Reihenfolge der Komponenten festgelgen und
    * widergeben.
    */
   final JComponent[] order = new JComponent[] { this.jTextFieldAuftraggeber, this.jTextFieldAAdress1, this.jTextFieldAAdress2, this.jTextFieldAPlz, this.jTextFieldAOrt,
         this.jTextFieldATelefon, this.jTextFieldAEmail, this.jTextAreaBemerkungen };

   private FocusTraversalPolicy policy = new FocusTraversalPolicyImpl();

   private LabledStringValueNoneditableTableModel angeboteTableModel;

   private final ArbeitRechnungFactory factory;

   /**
    * Creates new form KlientenEditor
    * 
    * @param arg0
    */
   public KlientenEditor(final Frame arg0) {
      super(arg0, "Klienteneditor");

      factory = ArbeitRechnungFactory.getInstance();

      KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
      allKlienten = persister.getAllKlienten();
      persister.close();

      if (allKlienten.size() > 0) {
         currentKlient = allKlienten.get(0);
         currentIndex = 0;
      }

      initComponents();

      int maxwidth = jTableAngebote.getColumnModel().getColumn(1).getMaxWidth();

      angeboteTableModel = new LabledStringValueNoneditableTableModel(new String[] { "Inhalt", "Preis", "Beschreibung" });
      jTableAngebote.setModel(angeboteTableModel);
      jTableAngebote.getColumnModel().getColumn(1).setMaxWidth(maxwidth);

      try {
         this.jTextFieldAPlz.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("#####")));
         this.jTextFieldKPlz.setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter("#####")));
      } catch (Exception e) {
         e.printStackTrace();
      }
      setEvents();
   }

   /**
    * Asynchron wird die Datenansicht aktualisiert.
    */
   @Override
   public void setVisible(final boolean b) {
      super.setVisible(b);
      if (b) {
    	  SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateRechnungenPanel();
				updateKlient();
				updateAngeboteTabelle();
			}
		});
      }
   }

   /**
    * Setzt für die Textfelder das Standard-Event
    * TextFieldActionPerformed(ActionEvent evt)
    */
   private void setEvents() {
      jTextFieldKunde.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });

      jTextFieldKunde.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldKAdress1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldKAdress1.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldKAdress2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldKAdress2.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldKPlz.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldKPlz.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldKOrt.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldKOrt.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldKTelefon.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldKTelefon.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldKEmail.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldKEmail.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldAuftraggeber.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldAuftraggeber.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldAAdress1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldAAdress1.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldAAdress2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldAAdress2.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldAPlz.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldAPlz.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldAOrt.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldAOrt.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldATelefon.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldATelefon.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldAEmail.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldAEmail.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldTex_datei.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      this.jTextAreaBemerkungen.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldZusatzBezeichnung1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldZusatzBezeichnung1.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldZusatzBezeichnung2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldZusatzBezeichnung2.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jTextFieldRechnungBezeichnung.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jTextFieldRechnungBezeichnung.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(final FocusEvent evt) {
            TextFieldFocusGained(evt);
         }

         @Override
         public void focusLost(final FocusEvent evt) {
            TextFieldFocusLost(evt);
         }
      });
      jCheckBoxZusatz1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });
      jCheckBoxZusatz2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            TextFieldActionPerformed(evt);
         }
      });

      this.formRechnungen1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            formChange(evt);
         }
      });
      this.arbeitsstundenTabelle1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            formChange(evt);
         }
      });
   }

   /**
    * Setzt Filtern in der ArbeitsstundenTabelle
    */
   public void setFilter(final int Filter) {
      switch (Filter) {
         case EINGEREICHTE:
            this.arbeitsstundenTabelle1.setFilter(ArbeitsstundenTabelle.EINGEREICHTE);
            break;
         case NICHTEINGEREICHTE:
            this.arbeitsstundenTabelle1.setFilter(ArbeitsstundenTabelle.NICHTEINGEREICHTE);
            break;
         case OFFENE:
            this.arbeitsstundenTabelle1.setFilter(ArbeitsstundenTabelle.OFFENE);
            break;
      }
   }

   private void formChange(final PropertyChangeEvent evt) {
      // Bei allen PropertyChangeEvents tabellen aktualisieren
      this.updateTables();
      this.firePropertyChange("", 0, 1);
   }

   /**
    * Hier werden alle Teile des Fensters aktualisiert,
    * wenn sich an der Ansicht etwas geändert hat.
    */
   private void updateComponents() {
      updateKlient();
      updateTables();
   }

   /**
    * Aktualisiert den Klienten und die Buttons
    */
   private void updateKlient() {
      if (currentKlient != null) {
         jTextFieldAuftraggeber.setText(currentKlient.getAuftraggeber());
         jTextFieldAAdress1.setText(currentKlient.getAAdress1());
         jTextFieldAAdress2.setText(currentKlient.getAAdress2());
         jTextFieldAPlz.setText(currentKlient.getAPlz());
         jTextFieldAOrt.setText(currentKlient.getAOrt());
         jTextFieldATelefon.setText(currentKlient.getATelefon());
         jTextFieldAEmail.setText(currentKlient.getAEmail());
         jTextFieldKPlz.setText(currentKlient.getKPlz());
         jTextFieldKOrt.setText(currentKlient.getKOrt());
         jTextFieldKunde.setText(currentKlient.getKunde());
         jTextFieldKAdress1.setText(currentKlient.getKAdress1());
         jTextFieldKAdress2.setText(currentKlient.getKAdress2());
         jTextFieldKTelefon.setText(currentKlient.getKTelefon());
         jTextFieldKEmail.setText(currentKlient.getKEmail());
         jTextAreaBemerkungen.setText(currentKlient.getBemerkungen());
         jTextFieldTex_datei.setText(currentKlient.getTex_datei());

         jCheckBoxZusatz1.setSelected(currentKlient.hasZusatz1());
         jCheckBoxZusatz2.setSelected(currentKlient.hasZusatz2());
         jTextFieldZusatzBezeichnung1.setText(currentKlient.getZusatz1_Name());
         jTextFieldZusatzBezeichnung2.setText(currentKlient.getZusatz2_Name());
         this.jTextFieldRechnungBezeichnung.setText(currentKlient.getRechnungnummer_bezeichnung());
      }
      setVisibilityOfButtons();
   }

   private void setVisibilityOfButtons() {

      jButtonVor.setEnabled(true);
      jButtonZurueck.setEnabled(true);
      jButtonZumEnde.setEnabled(true);
      jButtonZumAnfang.setEnabled(true);

      if (currentKlient == null) {
         jButtonZurueck.setEnabled(false);
         jButtonZumAnfang.setEnabled(false);
         jButtonVor.setEnabled(false);
         jButtonZumEnde.setEnabled(false);
         jTabbedPane1.setEnabled(false);
      } else {
    	  jTabbedPane1.setEnabled(true);
         if (currentKlient.equals(allKlienten.get(0))) {
            jButtonZurueck.setEnabled(false);
            jButtonZumAnfang.setEnabled(false);
         }
         if (currentKlient.equals(allKlienten.get(allKlienten.size() - 1))) {
            jButtonVor.setEnabled(false);
            jButtonZumEnde.setEnabled(false);
         }
      }

   }

   /**
    * Bringt alle Tabellen auf den neuesten Stand (z.B. bei neuer Klientenwahl)
    */
   private void updateTables() {
      updateAngeboteTabelle();
      updateRechnungenPanel();
      if(currentKlient != null) {
    	  this.arbeitsstundenTabelle1.update(currentKlient.getKlienten_id());
      }
   }

   /**
    * This method is called from within the constructor to
    * initialize the form.
    */
   @SuppressWarnings("unused")
   private void initComponents() {

      int klientenId;
      if (currentKlient != null)
         klientenId = currentKlient.getKlienten_id();
      else
         klientenId = -1;

      jSplitPane1 = new JSplitPane();
      jTabbedPane1 = new JTabbedPane();
      jPanelArbeitsstunden = new JPanel();
      arbeitsstundenTabelle1 = new ArbeitsstundenTabelle(this, klientenId);
      jPanelRechnungen = new JPanel();
      formRechnungen1 = new FormRechnungen(getOwner(), klientenId);
      jPanelAngebote = new JPanel();
      jScrollPane2 = new JScrollPane();
      jTableAngebote = new JTable();
      jButtonaddAngebot = new JButton();
      jButtonEditAngebot = new JButton();
      jButtonDeleteAngebot = new JButton();
      jPanelEinstellungen = new JPanel();
      jLabel16 = new JLabel();
      jCheckBoxZusatz1 = new JCheckBox();
      jCheckBoxZusatz2 = new JCheckBox();
      jLabel17 = new JLabel();
      jLabel18 = new JLabel();
      jTextFieldZusatzBezeichnung1 = new JTextField();
      jTextFieldZusatzBezeichnung2 = new JTextField();
      jLabel19 = new JLabel();
      jTextFieldRechnungBezeichnung = new JTextField();
      jPanel3 = new JPanel();
      jLabel6 = new JLabel();
      jLabel7 = new JLabel();
      jLabel1 = new JLabel();
      jLabel2 = new JLabel();
      jLabel3 = new JLabel();
      jLabel4 = new JLabel();
      jTextFieldKPlz = new JFormattedTextField();
      jTextFieldKOrt = new JTextField();
      jTextFieldKAdress2 = new JTextField();
      jTextFieldKAdress1 = new JTextField();
      jTextFieldKunde = new JTextField();
      jTextFieldKTelefon = new JTextField();
      jTextFieldKEmail = new JTextField();
      jLabel13 = new JLabel();
      jButtonNewKlient = new JButton();
      jButtonDelKlient = new JButton();
      jButtonBeenden = new JButton();
      jLabel14 = new JLabel();
      jButtonZumAnfang = new JButton();
      jButtonZurueck = new JButton();
      jButtonVor = new JButton();
      jButtonZumEnde = new JButton();
      jLabel5 = new JLabel();
      jLabel8 = new JLabel();
      jLabel9 = new JLabel();
      jLabel10 = new JLabel();
      jLabel11 = new JLabel();
      jLabel12 = new JLabel();
      jTextFieldAPlz = new JFormattedTextField();
      jTextFieldAOrt = new JTextField();
      jTextFieldAuftraggeber = new JTextField();
      jTextFieldAAdress1 = new JTextField();
      jTextFieldAAdress2 = new JTextField();
      jTextFieldATelefon = new JTextField();
      jTextFieldAEmail = new JTextField();
      jScrollPane1 = new JScrollPane();
      jTextAreaBemerkungen = new JTextArea();
      jLabel15 = new JLabel();
      jTextFieldTex_datei = new JTextField();
      jButtonfindeTexDatei = new JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
      setTitle(resourceMap.getString("Form.title")); // NOI18N
      setName("Form"); // NOI18N

      jSplitPane1.setDividerLocation(350);
      jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jSplitPane1.setName("jSplitPane1"); // NOI18N

      jTabbedPane1.setName("jTabbedPane1"); // NOI18N

      jPanelArbeitsstunden.setName("jPanelArbeitsstunden"); // NOI18N

      arbeitsstundenTabelle1.setName("arbeitsstundenTabelle1"); // NOI18N

      javax.swing.GroupLayout jPanelArbeitsstundenLayout = new javax.swing.GroupLayout(jPanelArbeitsstunden);
      jPanelArbeitsstunden.setLayout(jPanelArbeitsstundenLayout);
      jPanelArbeitsstundenLayout.setHorizontalGroup(jPanelArbeitsstundenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(arbeitsstundenTabelle1,
            javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE));
      jPanelArbeitsstundenLayout.setVerticalGroup(jPanelArbeitsstundenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(arbeitsstundenTabelle1,
            javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE));

      jTabbedPane1.addTab(resourceMap.getString("jPanelArbeitsstunden.TabConstraints.tabTitle"), jPanelArbeitsstunden); // NOI18N

      jPanelRechnungen.setName("jPanelRechnungen"); // NOI18N

      formRechnungen1.setName("formRechnungen1"); // NOI18N

      javax.swing.GroupLayout jPanelRechnungenLayout = new javax.swing.GroupLayout(jPanelRechnungen);
      jPanelRechnungen.setLayout(jPanelRechnungenLayout);
      jPanelRechnungenLayout.setHorizontalGroup(jPanelRechnungenLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 605, Short.MAX_VALUE)
            .addGroup(
                  jPanelRechnungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        jPanelRechnungenLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
                              .addComponent(formRechnungen1, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE))));
      jPanelRechnungenLayout.setVerticalGroup(jPanelRechnungenLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
            .addGroup(
                  jPanelRechnungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        jPanelRechnungenLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
                              .addComponent(formRechnungen1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE))));

      jTabbedPane1.addTab(resourceMap.getString("jPanelRechnungen.TabConstraints.tabTitle"), jPanelRechnungen); // NOI18N

      jPanelAngebote.setName("jPanelAngebote"); // NOI18N

      jScrollPane2.setName("jScrollPane2"); // NOI18N

      jTableAngebote.setModel(new LabledStringValueNoneditableTableModel(new String[] { "Inhalt", "Preis", "Beschreibung" }));

      jTableAngebote.setName("jTableAngebote"); // NOI18N
      jTableAngebote.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      jTableAngebote.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseClicked(final MouseEvent evt) {
            jTableAngeboteMouseClicked(evt);
         }
      });
      jScrollPane2.setViewportView(jTableAngebote);
      jTableAngebote.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableAngebote.columnModel.title0")); // NOI18N
      jTableAngebote.getColumnModel().getColumn(1).setMaxWidth(211);
      jTableAngebote.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableAngebote.columnModel.title1")); // NOI18N
      jTableAngebote.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableAngebote.columnModel.title2")); // NOI18N

      jButtonaddAngebot.setText(resourceMap.getString("jButtonaddAngebot.text")); // NOI18N
      jButtonaddAngebot.setName("jButtonaddAngebot"); // NOI18N
      jButtonaddAngebot.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonaddAngebotActionPerformed(evt);
         }
      });

      jButtonEditAngebot.setText(resourceMap.getString("jButtonEditAngebot.text")); // NOI18N
      jButtonEditAngebot.setName("jButtonEditAngebot"); // NOI18N
      jButtonEditAngebot.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonEditAngebotActionPerformed(evt);
         }
      });

      jButtonDeleteAngebot.setText(resourceMap.getString("jButtonDeleteAngebot.text")); // NOI18N
      jButtonDeleteAngebot.setName("jButtonDeleteAngebot"); // NOI18N
      jButtonDeleteAngebot.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonDeleteAngebotActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout jPanelAngeboteLayout = new javax.swing.GroupLayout(jPanelAngebote);
      jPanelAngebote.setLayout(jPanelAngeboteLayout);
      jPanelAngeboteLayout.setHorizontalGroup(jPanelAngeboteLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                  javax.swing.GroupLayout.Alignment.TRAILING,
                  jPanelAngeboteLayout.createSequentialGroup().addContainerGap(278, Short.MAX_VALUE).addComponent(jButtonaddAngebot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jButtonEditAngebot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jButtonDeleteAngebot).addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE));
      jPanelAngeboteLayout.setVerticalGroup(jPanelAngeboteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            javax.swing.GroupLayout.Alignment.TRAILING,
            jPanelAngeboteLayout
                  .createSequentialGroup()
                  .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(
                        jPanelAngeboteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButtonaddAngebot).addComponent(jButtonEditAngebot)
                              .addComponent(jButtonDeleteAngebot)).addContainerGap()));

      jTabbedPane1.addTab(resourceMap.getString("jPanelAngebote.TabConstraints.tabTitle"), jPanelAngebote); // NOI18N

      jPanelEinstellungen.setName("jPanelEinstellungen"); // NOI18N

      jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
      jLabel16.setName("jLabel16"); // NOI18N

      jCheckBoxZusatz1.setText(resourceMap.getString("Zusatz1.text")); // NOI18N
      jCheckBoxZusatz1.setName("Zusatz1"); // NOI18N

      jCheckBoxZusatz2.setText(resourceMap.getString("Zusatz2.text")); // NOI18N
      jCheckBoxZusatz2.setName("Zusatz2"); // NOI18N

      jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
      jLabel17.setName("jLabel17"); // NOI18N

      jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
      jLabel18.setName("jLabel18"); // NOI18N

      jTextFieldZusatzBezeichnung1.setText(resourceMap.getString("Zusatz1_Name.text")); // NOI18N
      jTextFieldZusatzBezeichnung1.setName("Zusatz1_Name"); // NOI18N

      jTextFieldZusatzBezeichnung2.setText(resourceMap.getString("Zusatz2_Name.text")); // NOI18N
      jTextFieldZusatzBezeichnung2.setName("Zusatz2_Name"); // NOI18N

      jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
      jLabel19.setName("jLabel19"); // NOI18N

      jTextFieldRechnungBezeichnung.setText(resourceMap.getString("rechnungnummer_bezeichnung.text")); // NOI18N
      jTextFieldRechnungBezeichnung.setName("rechnungnummer_bezeichnung"); // NOI18N

      javax.swing.GroupLayout jPanelEinstellungenLayout = new javax.swing.GroupLayout(jPanelEinstellungen);
      jPanelEinstellungen.setLayout(jPanelEinstellungenLayout);
      jPanelEinstellungenLayout.setHorizontalGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanelEinstellungenLayout
                  .createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        jPanelEinstellungenLayout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(
                                    jPanelEinstellungenLayout
                                          .createSequentialGroup()
                                          .addGroup(
                                                jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jCheckBoxZusatz2)
                                                      .addComponent(jCheckBoxZusatz1))
                                          .addGap(18, 18, 18)
                                          .addGroup(
                                                jPanelEinstellungenLayout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                      .addGroup(
                                                            jPanelEinstellungenLayout
                                                                  .createSequentialGroup()
                                                                  .addComponent(jLabel17)
                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                  .addComponent(jTextFieldZusatzBezeichnung1, javax.swing.GroupLayout.PREFERRED_SIZE, 127,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                      .addGroup(
                                                            jPanelEinstellungenLayout.createSequentialGroup().addComponent(jLabel18).addGap(57, 57, 57)
                                                                  .addComponent(jTextFieldZusatzBezeichnung2))))
                              .addComponent(jLabel16)
                              .addGroup(
                                    jPanelEinstellungenLayout.createSequentialGroup().addComponent(jLabel19).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(jTextFieldRechnungBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                  .addContainerGap(213, Short.MAX_VALUE)));
      jPanelEinstellungenLayout.setVerticalGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanelEinstellungenLayout
                  .createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        jPanelEinstellungenLayout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                              .addGroup(
                                    jPanelEinstellungenLayout
                                          .createSequentialGroup()
                                          .addGroup(
                                                jPanelEinstellungenLayout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jLabel17)
                                                      .addComponent(jTextFieldZusatzBezeichnung1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanelEinstellungenLayout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jLabel18)
                                                      .addComponent(jTextFieldZusatzBezeichnung2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE)))
                              .addGroup(
                                    jPanelEinstellungenLayout.createSequentialGroup().addComponent(jLabel16).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addComponent(jCheckBoxZusatz1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jCheckBoxZusatz2)))
                  .addGap(18, 18, 18)
                  .addGroup(
                        jPanelEinstellungenLayout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                              .addComponent(jLabel19)
                              .addComponent(jTextFieldRechnungBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(107, Short.MAX_VALUE)));

      jTabbedPane1.addTab(resourceMap.getString("jPanelEinstellungen.TabConstraints.tabTitle"), jPanelEinstellungen); // NOI18N

      jSplitPane1.setBottomComponent(jTabbedPane1);

      jPanel3.setFocusCycleRoot(true);
      jPanel3.setFocusTraversalPolicy(policy);
      jPanel3.setName("jPanel3"); // NOI18N

      jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
      jLabel6.setFocusable(false);
      jLabel6.setName("jLabel6"); // NOI18N
      new LabelComponentBinding(jLabel6, jTextFieldKTelefon);

      jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
      jLabel7.setFocusable(false);
      jLabel7.setName("jLabel7"); // NOI18N
      new LabelComponentBinding(jLabel7, jTextFieldKEmail);

      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setFocusable(false);
      jLabel1.setName("jLabel1"); // NOI18N
      new LabelComponentBinding(jLabel1, jTextFieldKunde);

      jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
      jLabel2.setFocusable(false);
      jLabel2.setName("jLabel2"); // NOI18N
      new LabelComponentBinding(jLabel2, jTextFieldKAdress1);

      jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
      jLabel3.setFocusable(false);
      jLabel3.setName("jLabel3"); // NOI18N
      new LabelComponentBinding(jLabel3, jTextFieldKAdress2);

      jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
      jLabel4.setFocusable(false);
      jLabel4.setName("jLabel4"); // NOI18N
      new LabelComponentBinding(jLabel4, jTextFieldKPlz);

      jTextFieldKPlz.setColumns(5);
      jTextFieldKPlz.setName("KPLZ"); // NOI18N

      jTextFieldKOrt.setName("KOrt"); // NOI18N

      jTextFieldKAdress2.setName("KAdresse2"); // NOI18N
      jTextFieldKAdress2.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldKAdress1.setName("KAdresse1"); // NOI18N
      jTextFieldKAdress1.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldKunde.setName("Kunde"); // NOI18N
      jTextFieldKunde.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldKTelefon.setName("KTelefon"); // NOI18N
      jTextFieldKTelefon.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldKEmail.setName("KEmail"); // NOI18N
      jTextFieldKEmail.setPreferredSize(new java.awt.Dimension(4, 20));

      jLabel13.setFont(Font.getFont(resourceMap.getString("jLabel13.font"))); // NOI18N
      jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
      jLabel13.setName("jLabel13"); // NOI18N

      jButtonNewKlient.setText(resourceMap.getString("jButtonNewKlient.text")); // NOI18N
      jButtonNewKlient.setName("jButtonNewKlient"); // NOI18N
      jButtonNewKlient.setPreferredSize(new java.awt.Dimension(110, 25));
      jButtonNewKlient.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonNewKlientActionPerformed(evt);
         }
      });

      jButtonDelKlient.setText(resourceMap.getString("jButtonDelKlient.text")); // NOI18N
      jButtonDelKlient.setName("jButtonDelKlient"); // NOI18N
      jButtonDelKlient.setPreferredSize(new java.awt.Dimension(110, 25));
      jButtonDelKlient.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonDelKlientActionPerformed(evt);
         }
      });

      jButtonBeenden.setText(resourceMap.getString("jButtonBeenden.text")); // NOI18N
      jButtonBeenden.setName("jButtonBeenden"); // NOI18N
      jButtonBeenden.setPreferredSize(new java.awt.Dimension(110, 25));
      jButtonBeenden.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonBeendenActionPerformed(evt);
         }
      });

      jLabel14.setFont(Font.getFont(resourceMap.getString("jLabel14.font"))); // NOI18N
      jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
      jLabel14.setName("jLabel14"); // NOI18N

      jButtonZumAnfang.setIcon(getIcon(resourceMap.getString("jButtonZumAnfang.icon"))); // NOI18N
      jButtonZumAnfang.setText(resourceMap.getString("jButtonZumAnfang.text")); // NOI18N
      jButtonZumAnfang.setName("jButtonZumAnfang"); // NOI18N
      jButtonZumAnfang.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonZumAnfangActionPerformed(evt);
         }
      });

      jButtonZurueck.setIcon(getIcon(resourceMap.getString("jButtonZurueck.icon"))); // NOI18N
      jButtonZurueck.setText(resourceMap.getString("jButtonZurueck.text")); // NOI18N
      jButtonZurueck.setName("jButtonZurueck"); // NOI18N
      jButtonZurueck.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonZurueckActionPerformed(evt);
         }
      });

      jButtonVor.setIcon(getIcon(resourceMap.getString("jButtonVor.icon"))); // NOI18N
      jButtonVor.setText(resourceMap.getString("jButtonVor.text")); // NOI18N
      jButtonVor.setName("jButtonVor"); // NOI18N
      jButtonVor.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonVorActionPerformed(evt);
         }
      });

      jButtonZumEnde.setIcon(getIcon(resourceMap.getString("jButtonZumEnde.icon"))); // NOI18N
      jButtonZumEnde.setText(resourceMap.getString("jButtonZumEnde.text")); // NOI18N
      jButtonZumEnde.setName("jButtonZumEnde"); // NOI18N
      jButtonZumEnde.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonZumEndeActionPerformed(evt);
         }
      });

      jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
      jLabel5.setFocusable(false);
      jLabel5.setName("jLabel5"); // NOI18N

      jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
      jLabel8.setFocusable(false);
      jLabel8.setName("jLabel8"); // NOI18N

      jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
      jLabel9.setFocusable(false);
      jLabel9.setName("jLabel9"); // NOI18N

      jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
      jLabel10.setFocusable(false);
      jLabel10.setName("jLabel10"); // NOI18N

      jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
      jLabel11.setFocusable(false);
      jLabel11.setName("jLabel11"); // NOI18N

      jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
      jLabel12.setFocusable(false);
      jLabel12.setName("jLabel12"); // NOI18N

      jTextFieldAPlz.setColumns(5);
      jTextFieldAPlz.setFocusCycleRoot(true);
      jTextFieldAPlz.setName("APLZ"); // NOI18N

      jTextFieldAOrt.setFocusCycleRoot(true);
      jTextFieldAOrt.setName("AOrt"); // NOI18N

      jTextFieldAuftraggeber.setFocusCycleRoot(true);
      jTextFieldAuftraggeber.setName("Auftraggeber"); // NOI18N
      jTextFieldAuftraggeber.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldAAdress1.setFocusCycleRoot(true);
      jTextFieldAAdress1.setName("AAdresse1"); // NOI18N
      jTextFieldAAdress1.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldAAdress2.setFocusCycleRoot(true);
      jTextFieldAAdress2.setName("AAdresse2"); // NOI18N
      jTextFieldAAdress2.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldATelefon.setFocusCycleRoot(true);
      jTextFieldATelefon.setName("ATelefon"); // NOI18N
      jTextFieldATelefon.setPreferredSize(new java.awt.Dimension(4, 20));

      jTextFieldAEmail.setFocusCycleRoot(true);
      jTextFieldAEmail.setName("AEmail"); // NOI18N
      jTextFieldAEmail.setPreferredSize(new java.awt.Dimension(4, 20));

      jScrollPane1.setName("jScrollPane1"); // NOI18N

      jTextAreaBemerkungen.setColumns(20);
      jTextAreaBemerkungen.setRows(5);
      jTextAreaBemerkungen.setName("Bemerkungen"); // NOI18N
      jScrollPane1.setViewportView(jTextAreaBemerkungen);

      jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
      jLabel15.setName("jLabel15"); // NOI18N

      jTextFieldTex_datei.setText(resourceMap.getString("tex_datei.text")); // NOI18N
      jTextFieldTex_datei.setName("tex_datei"); // NOI18N

      jButtonfindeTexDatei.setText(resourceMap.getString("jButtonfindeTexDatei.text")); // NOI18N
      jButtonfindeTexDatei.setName("jButtonfindeTexDatei"); // NOI18N
      jButtonfindeTexDatei.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent evt) {
            jButtonfindeTexDateiActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                  javax.swing.GroupLayout.Alignment.TRAILING,
                  jPanel3Layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                              jPanel3Layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                                    .addGroup(
                                          javax.swing.GroupLayout.Alignment.LEADING,
                                          jPanel3Layout
                                                .createSequentialGroup()
                                                .addGap(13, 13, 13)
                                                .addGroup(
                                                      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel6).addComponent(jLabel7)
                                                            .addComponent(jLabel1).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(
                                                      jPanel3Layout
                                                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addGroup(
                                                                  jPanel3Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(jTextFieldKPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(2, 2, 2)
                                                                        .addComponent(jTextFieldKOrt, javax.swing.GroupLayout.PREFERRED_SIZE, 112,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addComponent(jTextFieldKAdress2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE)
                                                            .addComponent(jTextFieldKAdress1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE)
                                                            .addComponent(jTextFieldKunde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE)
                                                            .addComponent(jTextFieldKTelefon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE)
                                                            .addComponent(jTextFieldKEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(
                                                      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel5).addComponent(jLabel8)
                                                            .addComponent(jLabel9).addComponent(jLabel10).addComponent(jLabel11).addComponent(jLabel12))
                                                .addGap(6, 6, 6)
                                                .addGroup(
                                                      jPanel3Layout
                                                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(
                                                                  jPanel3Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(jTextFieldAPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                              javax.swing.GroupLayout.PREFERRED_SIZE).addGap(2, 2, 2)
                                                                        .addComponent(jTextFieldAOrt, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                                                            .addComponent(jTextFieldAuftraggeber, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                                            .addComponent(jTextFieldAAdress1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                                            .addComponent(jTextFieldAAdress2, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                                            .addComponent(jTextFieldATelefon, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                                            .addComponent(jTextFieldAEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)))
                                    .addGroup(
                                          jPanel3Layout
                                                .createSequentialGroup()
                                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonNewKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonDelKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                                                .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                          jPanel3Layout.createSequentialGroup().addGap(279, 279, 279).addComponent(jLabel14).addGap(225, 225, 225))
                                    .addGroup(
                                          jPanel3Layout.createSequentialGroup().addComponent(jButtonZumAnfang).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonZurueck, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(6, 6, 6)
                                                .addComponent(jButtonVor, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonZumEnde).addGap(105, 105, 105))
                                    .addGroup(
                                          javax.swing.GroupLayout.Alignment.LEADING,
                                          jPanel3Layout.createSequentialGroup().addComponent(jLabel15).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldTex_datei, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonfindeTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap()));
      jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanel3Layout
                  .createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        jPanel3Layout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addGroup(
                                    jPanel3Layout
                                          .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                          .addComponent(jButtonDelKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addComponent(jButtonNewKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jButtonZumEnde).addComponent(jButtonVor)
                              .addComponent(jButtonZumAnfang).addComponent(jButtonZurueck))
                  .addGroup(
                        jPanel3Layout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(
                                    jPanel3Layout
                                          .createSequentialGroup()
                                          .addGap(3, 3, 3)
                                          .addComponent(jLabel14)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldAuftraggeber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel5))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldAAdress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel8))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldAAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel9))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jLabel10)
                                                      .addComponent(jTextFieldAOrt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jTextFieldAPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldATelefon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel11))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldAEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel12)))
                              .addGroup(
                                    jPanel3Layout
                                          .createSequentialGroup()
                                          .addGap(18, 18, 18)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldKunde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel1))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldKAdress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel2))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldKAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel3))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jLabel4)
                                                      .addComponent(jTextFieldKOrt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                                      .addComponent(jTextFieldKPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldKTelefon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel6))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                jPanel3Layout
                                                      .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                      .addComponent(jTextFieldKEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel7))))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanel3Layout
                              .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(
                                    jPanel3Layout
                                          .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                          .addComponent(jLabel15)
                                          .addComponent(jTextFieldTex_datei, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                              .addComponent(jButtonfindeTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                  .addContainerGap()));

      jSplitPane1.setLeftComponent(jPanel3);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addContainerGap().addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addContainerGap().addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE).addContainerGap()));

      pack();
   }

   private Icon getIcon(final String fileName) {
      URL resource = getClass().getResource("/" + fileName);
      return new ImageIcon(resource);
   }

   /**
    * jButtonVor wurde ausgelöst. Wenn möglich wird der nächste Datensatz im
    * Recordset klientendaten angesprungen
    * 
    * @param evt
    */
   private void jButtonVorActionPerformed(final ActionEvent evt) {
      toNextKlientIfPossible();
   }

   private void toNextKlientIfPossible() {
      if (currentIndex + 1 < allKlienten.size()) {
         currentIndex++;
         currentKlient = allKlienten.get(currentIndex);
         updateComponents();
      }
   }

   /**
    * jButtonZurueck wurde ausgelöst. Wenn möglich wird der vorherige Datensatz
    * im Recordset klientendaten angesprungen
    * 
    * @param evt
    */
   private void jButtonZurueckActionPerformed(final ActionEvent evt) {
      toPreviourKlientIfPossible();
   }

   private void toPreviourKlientIfPossible() {
      if (currentIndex >= 0) {
         currentIndex--;
         currentKlient = allKlienten.get(currentIndex);
         updateComponents();
      }
   }

   /**
    * Panel für Rechnungen wird unsichtbar oder sichtbar, sobald Rechnungen
    * existieren und wird dann aktualisiert
    */
   private void updateRechnungenPanel() {

      KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
      this.jTabbedPane1.remove(jPanelRechnungen);
      int anzahl = persister.getRechnungenAnzahl(currentKlient);

      if (anzahl > 0) {
         this.jTabbedPane1.insertTab("Rechnungen", null, jPanelRechnungen, null, 1);
         this.formRechnungen1.update(currentKlient.getKlienten_id());
      }
      persister.close();
   }

   /**
    * Tabelle mit den Angeboten für diesen Klienten wird aktualisiert
    */
   private void updateAngeboteTabelle() {

      int klientId = 0;
      java.text.NumberFormat zf;
      zf = NumberFormat.getCurrencyInstance(Locale.GERMANY);

      if (currentKlient != null)
         klientId = currentKlient.getKlienten_id();

      KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
      angebote = persister.getAngeboteForKlient(klientId);
      persister.close();

      angeboteTableModel.setRowCount(0);

      for (Angebot angebot : angebote) {

         Vector<String> einVektor = new Vector<String>();
         einVektor.addElement(angebot.getInhalt());
         einVektor.addElement(zf.format(angebot.getPreis()));
         einVektor.addElement(angebot.getBeschreibung());

         angeboteTableModel.addRow(einVektor);
      }
   }

   /**
    * jButtonNewKlient wurde ausgelöst: Neuer Klient wird in der Datenbank
    * gespeichert und dann aufgerufen
    * 
    * @param evt
    */
   private void jButtonNewKlientActionPerformed(final ActionEvent evt) {

      KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
      currentKlient = persister.createNewAuftraggeber();
      allKlienten = persister.getAllKlienten();
      persister.close();
      updateComponents();
   }

   /**
    * jButtonDelKlient wurde ausgelöst: Der Klient wird in der Datenbank
    * gelöscht!
    * Sämtliche Angebote und Einheiten werden gelöscht!
    * 
    * @param evt
    */
   private void jButtonDelKlientActionPerformed(final ActionEvent evt) {

      int ergebnis = JOptionPane.showConfirmDialog(this, "Wollen Sie den Datensatz \"" + currentKlient.getAuftraggeber() + "\" endgültig löschen?", "Löschen?",
            JOptionPane.YES_NO_OPTION);

      if (ergebnis == JOptionPane.YES_OPTION) {
         // Zuerst den Datensatz identifizieren, der als nächstes gezeigt wird.
         Klient toDelete = currentKlient;

         if (currentIslast()) {
            toPreviourKlientIfPossible();
         } else {
            toNextKlientIfPossible();
         }

         KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
         persister.delete(toDelete);
         persister.close();
         
         updateComponents();
      }
   }

   private boolean currentIslast() {
      if (currentKlient != null) {
         return currentKlient.equals(allKlienten.get(allKlienten.size() - 1));
      }
      return false;
   }

   /**
    * Beenden-Knopf gedrückt: Fenster wird geschlossen. Wenn selbststaendig =
    * true, wird System.exit(0) ausgeführt.
    * 
    * @param evt
    */
   private void jButtonBeendenActionPerformed(final ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
   }

   /**
    * jButtonZumEnde wurde ausgelöst: Der letzte Datensatz des Recordsetzs
    * klientendaten wird angesprungen und die Anzeige wird aktualisiert.
    * 
    * @param evt
    */
   private void jButtonZumEndeActionPerformed(final ActionEvent evt) {
      toLast();
   }

   private void toLast() {
      if (!allKlienten.isEmpty()) {
         currentIndex = allKlienten.size() - 1;
         currentKlient = allKlienten.get(currentIndex);
      }
   }

   /**
    * jButtonZumAnfang wurde ausgelöst: Der erste Datensatz des Recordsetzs
    * klientendaten wird angesprungen und die Anzeige wird aktualisiert.
    * 
    * @param evt
    */
   private void jButtonZumAnfangActionPerformed(final ActionEvent evt) {
      toFirst();
   }

   private void toFirst() {
      if (!allKlienten.isEmpty()) {
         currentIndex = 0;
         currentKlient = allKlienten.get(currentIndex);
      }
   }

   /**
    * jButtonaddAngebot wurde ausgelöst: Ein Angebot für diesen Klienten wird
    * hinzugefügt.
    * 
    * @param evt
    */
   private void jButtonaddAngebotActionPerformed(final ActionEvent evt) {
      // Konstruktor mit 3 Parametern wird für neue Angebote benutzt -1 steht
      // für unbekannte Angebot_ID
      AngebotDialog eingabefenster = new AngebotDialog(this.getOwner(), currentKlient.getKlienten_id());
      eingabefenster.setVisible(true);
      updateAngeboteTabelle(); // Mit geändertem Datensatz anzeigen
   }

   /**
    * JButtonEditAngebot wurde ausgelöst: Das in der Tabelle selectierte Angebot
    * wird in AngebotDialog bearbeitet
    * 
    * @param evt
    */
   private void jButtonEditAngebotActionPerformed(final ActionEvent evt) {

      if (this.jTableAngebote.getSelectedRow() == -1) {
         JOptionPane.showMessageDialog(this, "Bearbeitung abgebrochen!\nEin Eintrag muss ausgewählt sein!");
      } else {
         openAngebotDialogForSelected();
      }
   }

   private void openAngebotDialogForSelected() {

      try {
         Angebot angebot = this.angebote.get(this.jTableAngebote.getSelectedRow());
         AngebotDialog eingabefenster = new AngebotDialog(this, angebot);
         eingabefenster.setVisible(true);
         this.updateAngeboteTabelle();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * jButtonDeleteAngebot wurde ausgelöst: Gewähltes Angebot wird gelöscht
    * 
    * @param evt
    */
   private void jButtonDeleteAngebotActionPerformed(final ActionEvent evt) {
      if (this.jTableAngebote.getSelectedRow() == -1) {
         JOptionPane.showMessageDialog(this, "Bearbeitung abgebrochen!\nEin Eintrag muss ausgewählt sein!");
      } else {
         try {
            Angebot angebot = this.angebote.get(this.jTableAngebote.getSelectedRow());

            int ergebnis = JOptionPane.showConfirmDialog(this, "Soll der Datensatz " + angebot.getInhalt() + " wirklich endgültig gelöscht werden?", "Löschen?",
                  JOptionPane.YES_NO_OPTION);

            if (ergebnis == JOptionPane.YES_OPTION) {
               KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
               persister.deleteAngebot(angebot);
               persister.close();
            }

            this.updateAngeboteTabelle();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Maus auf Angebot-Tabelle geklickt. Bei Doppelklick wird markierter
    * Datensatz bearbeitet.
    * 
    * @param evt
    */
   private void jTableAngeboteMouseClicked(final MouseEvent evt) {
      if (evt.getClickCount() > 1) { // Bei mehr als Einfach-Klick
         if (this.jTableAngebote.getSelectedRow() == -1) { // Wenn keine Zeile
                                                           // markiert ist
            this.jTable1SetSelection(evt); // Wird die unter dem Mausklick
                                           // markiert und dann bearbeitet
         }

         openAngebotDialogForSelected();
      }
   }

   private void jButtonfindeTexDateiActionPerformed(final ActionEvent evt) {

      java.awt.FileDialog dateiname = new java.awt.FileDialog(this, "Tex-Datei", java.awt.FileDialog.LOAD);
      int endindex = 0;
      if (currentKlient.getTex_datei() == null) {
         endindex = 0;
      } else {
         endindex = currentKlient.getTex_datei().lastIndexOf("/");
      }

      if (endindex > 0) {
         dateiname.setDirectory(currentKlient.getTex_datei().substring(0, endindex));
         dateiname.setFile(currentKlient.getTex_datei().substring(endindex, currentKlient.getTex_datei().length()));
      } else {
         if (currentKlient.getTex_datei() != null) {
            dateiname.setFile(currentKlient.getTex_datei());
         }
      }
      dateiname.setFilenameFilter(new java.io.FilenameFilter() {

         @Override
         public boolean accept(final File dir, final String name) {
            if (name.endsWith(".tex")) {
               return true;
            } else {
               return false;
            }
         }
      });

      dateiname.setVisible(true);
      if (dateiname.getFile() != null) {

         KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
         persister.speicherWert(currentKlient.getKlienten_id(), "tex_datei", "\"" + dateiname.getDirectory() + dateiname.getFile() + "\"");
         persister.close();
         this.jTextFieldTex_datei.setText(currentKlient.getTex_datei());
      }
      dateiname.dispose();
   }

   /**
    * In der Tabelle wird die Zeile auf die geklickt wurde ausgwählt.
    * 
    * @param evt
    */
   private void jTable1SetSelection(final MouseEvent evt) {
      // System.out.println("Zeile wird selectiert.");
      java.awt.Point p = evt.getPoint();
      int zeile = this.jTableAngebote.rowAtPoint(p);
      this.jTableAngebote.getSelectionModel().setSelectionInterval(zeile, zeile);
   }

   /**
    * Eins der Textfelder wurde bearbeitet. Der neue Wert wird gespeichert
    * 
    * @param event
    */
   private void TextFieldActionPerformed(final ActionEvent event) {

      KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
      if (event.getSource() instanceof JTextField) {
         JTextField tf = (JTextField) event.getSource();
         System.out.println("Text: " + tf.getText() + "\nName: " + tf.getName() + "\n");
         persister.speicherWert(currentKlient.getKlienten_id(), tf.getName(), "\"" + tf.getText() + "\"");
         allKlienten = persister.getAllKlienten();
      }
      if (event.getSource() instanceof JCheckBox) {
         JCheckBox cb = (JCheckBox) event.getSource();
         if (cb.isSelected()) {
            persister.speicherWert(currentKlient.getKlienten_id(), cb.getName(), Integer.valueOf(1));
         } else {
            persister.speicherWert(currentKlient.getKlienten_id(), cb.getName(), Integer.valueOf(0));
         }
         allKlienten = persister.getAllKlienten();
      }
      persister.close();
   }

   /**
    * Eins der Textfelder bekam den Focus. Der enthaltene Text wird markiert
    * 
    * @param event
    */
   private void TextFieldFocusGained(final FocusEvent event) {
      if (event.getSource() instanceof JTextField) {
         JTextField tf = (JTextField) event.getSource();
         tf.setSelectionStart(0);
         tf.setSelectionEnd(tf.getText().length());
      }
   }

   /**
    * Eins der Textfelder oder TextArea hat den Focus verloren - der Enthaltene
    * Wert wird gespeichert.
    * 
    * @param event
    */
   private void TextFieldFocusLost(final FocusEvent event) {

	   if (currentKlient == null) {
		   return;
	   }

      KlientenEditorPersister persister = factory.getPersister(KlientenEditorPersister.class);
      if (event.getSource() instanceof JTextField) {
         JTextField tf = (JTextField) event.getSource();
         persister.speicherWert(currentKlient.getKlienten_id(), tf.getName(), "\"" + tf.getText() + "\"");
         allKlienten = persister.getAllKlienten();
      }

      if (event.getSource() instanceof JTextArea) {
         JTextArea ta = (JTextArea) event.getSource();
         persister.speicherWert(currentKlient.getKlienten_id(), ta.getName(), "\"" + ta.getText() + "\"");
         allKlienten = persister.getAllKlienten();
      }
      persister.close();
   }

   /**
    * KLienten aktualisieren, Tabelle wird neu geladen.
    * @param klientenID
    */
   public void setCurrentKlientenId(final int klientenID) {
      for (int i = 0; i < allKlienten.size(); i++) {
         Klient k = allKlienten.get(i);
         if (k.getKlienten_id() == klientenID) {
            currentIndex = i;
            currentKlient = k;
            break;
         }
      }
      updateComponents();
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   public ArbeitsstundenTabelle arbeitsstundenTabelle1;
   private FormRechnungen formRechnungen1;
   private JButton jButtonBeenden;
   private JButton jButtonDelKlient;
   private JButton jButtonDeleteAngebot;
   private JButton jButtonEditAngebot;
   private JButton jButtonNewKlient;
   private JButton jButtonVor;
   private JButton jButtonZumAnfang;
   private JButton jButtonZumEnde;
   private JButton jButtonZurueck;
   private JButton jButtonaddAngebot;
   private JButton jButtonfindeTexDatei;
   private JCheckBox jCheckBoxZusatz1;
   private JCheckBox jCheckBoxZusatz2;
   private JLabel jLabel1;
   private JLabel jLabel10;
   private JLabel jLabel11;
   private JLabel jLabel12;
   private JLabel jLabel13;
   private JLabel jLabel14;
   private JLabel jLabel15;
   private JLabel jLabel16;
   private JLabel jLabel17;
   private JLabel jLabel18;
   private JLabel jLabel19;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JPanel jPanel3;
   private JPanel jPanelAngebote;
   private JPanel jPanelArbeitsstunden;
   private JPanel jPanelEinstellungen;
   private JPanel jPanelRechnungen;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JSplitPane jSplitPane1;
   private JTabbedPane jTabbedPane1;
   private JTable jTableAngebote;
   private JTextArea jTextAreaBemerkungen;
   private JTextField jTextFieldAAdress1;
   private JTextField jTextFieldAAdress2;
   private JTextField jTextFieldAEmail;
   private JTextField jTextFieldAOrt;
   private JFormattedTextField jTextFieldAPlz;
   private JTextField jTextFieldATelefon;
   private JTextField jTextFieldAuftraggeber;
   private JTextField jTextFieldKAdress1;
   private JTextField jTextFieldKAdress2;
   private JTextField jTextFieldKEmail;
   private JTextField jTextFieldKOrt;
   private JFormattedTextField jTextFieldKPlz;
   private JTextField jTextFieldKTelefon;
   private JTextField jTextFieldKunde;
   private JTextField jTextFieldRechnungBezeichnung;
   private JTextField jTextFieldTex_datei;
   private JTextField jTextFieldZusatzBezeichnung1;
   private JTextField jTextFieldZusatzBezeichnung2;

   // End of variables declaration//GEN-END:variables

   private class FocusTraversalPolicyImpl extends FocusTraversalPolicy {

      public FocusTraversalPolicyImpl() {}

      List<JComponent> list = java.util.Arrays.asList(order);

      @Override
      public Component getFirstComponent(final java.awt.Container focusCycleRoot) {
         return order[0];
      }

      @Override
      public Component getLastComponent(final java.awt.Container focusCycleRoot) {
         return order[order.length - 1];
      }

      @Override
      public Component getComponentAfter(final java.awt.Container focusCycleRoot, final Component aComponent) {
         int index = list.indexOf(aComponent);
         return order[(index + 1) % order.length];
      }

      @Override
      public Component getComponentBefore(final java.awt.Container focusCycleRoot, final Component aComponent) {
         int index = list.indexOf(aComponent);
         return order[(index - 1 + order.length) % order.length];
      }

      @Override
      public Component getDefaultComponent(final java.awt.Container focusCycleRoot) {
         return order[0];
      }

      @Override
      public Component getInitialComponent(final java.awt.Window window) {
         return order[0];
      }
   }

}
