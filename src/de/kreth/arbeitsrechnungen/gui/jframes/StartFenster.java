/*
 * StartFenster.java
 * Created on 27.04.2009, 10:10:09
 */
package de.kreth.arbeitsrechnungen.gui.jframes;

import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import mouseoverhintmanager.MouseOverHintManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.StartFensterTableCellRenderer;
import de.kreth.arbeitsrechnungen.gui.dialogs.OptionenDialog;
import de.kreth.arbeitsrechnungen.gui.jframes.starttablemodels.LabledStringValueNoneditableTableModel;
import de.kreth.arbeitsrechnungen.persister.DatenPersister;
import de.kreth.arbeitsrechnungen.persister.DatenPersister.Einheit;
import de.kreth.arbeitsrechnungen.persister.DatenPersister.Forderung;

public class StartFenster extends JFrame implements PropertyChangeListener {

   public enum StartTable {
      FORDERUNGEN, EINHEITEN
   }

   private static final long serialVersionUID = -1175489292478287196L;
   private Logger logger;

   DecimalFormat df = new DecimalFormat("0.00");

   private Vector<Integer> forderungen_ids = new Vector<Integer>();
   private Vector<Integer> Einheiten_ids = new Vector<Integer>();
   private Options optionen;
   private MouseOverHintManager hintman;
   private DefaultTableModel forderungenTableModel;
   private DefaultTableModel einheitenTableModel;
   private DatenPersister persister;

   /** Creates new form StartFenster */
   public StartFenster() {
      logger = Logger.getLogger(getClass());
      logger.setLevel(Level.DEBUG);
      logger.info("current Loglevel: " + logger.getLevel());

      loadOrCreateOptions();

      persister = new DatenPersister(optionen);

      // Model mit Überschriften erstellen
      einheitenTableModel = new LabledStringValueNoneditableTableModel(new String[] { "Firma", "Einsätze", "Summe" });
      forderungenTableModel = new LabledStringValueNoneditableTableModel(new String[] { "Firma", "Rechnungsdatum", "Forderung" });

      initComponents();
      initForderungen();
      initEinheiten();
      initHintman();
      this.jButtonArtenEinheiten.setEnabled(false);

   }

   private void initHintman() {

      hintman = new MouseOverHintManager(this.Status_links);
      hintman.addHintFor(this.jButtonKlientenEditor, "Startet den Klienteneditor, einen umfassenden Werkzeug für die meisten Programmfunktionen");
      hintman.addHintFor(this.jTableEinheiten, "Geleistete Arbeitsstunden, noch nicht abgerechnet");
      hintman.addHintFor(this.jTableForderungen, "Abgerechnete Arbeitsstunden, noch nicht bezahlt");
      hintman.addHintFor(this.jButtonBeenden, "Beendet das Programm und schließt alle zugehörigen Fenster");
      hintman.addHintFor(this.jButtonArtenEinheiten, "TODO: Einheitenarten");
      hintman.addHintFor(this.jButton1, "Einfacher Editor für Arbeitsstunden");
      hintman.addHintFor(this.jMenuItemBeenden, "Beendet das Programm und schließt alle zugehörigen Fenster");
      hintman.enableHints(this);
   }

   private void loadOrCreateOptions() {
      // Testen ob das arbeitsverzeichnis im home-verzeichnis existiert
      File homeverzeichnis;
      Properties sysprops = System.getProperties();
      String homedir = sysprops.getProperty("user.home");
      homeverzeichnis = new File(homedir + sysprops.getProperty("file.separator") + ".arbeitrechnungen");

      if (!homeverzeichnis.exists()) {
         // Verzeichnis anlegen
         logger.info(homeverzeichnis.getAbsolutePath() + " existiert nicht!\nwird angelegt...");
         homeverzeichnis.mkdirs();
      }

      File optionfile = new File(homedir + sysprops.getProperty("file.separator") + ".arbeitrechnungen" + sysprops.getProperty("file.separator") + "optionen.ini");

      createOptionsfileIfNotExisting(optionfile);

      loadOptions(optionfile);

      if (optionen == null || optionen.getDbHost() == null) {
         // Property sqlserver nicht gefunden: optionen nicht gespeichert!
         OptionenDialog optionwindow = new OptionenDialog(null, true);
         optionwindow.setVisible(true);
      }
   }

   private void createOptionsfileIfNotExisting(File optionfile) {
      boolean wasCreatedNew = false;
      try {
         wasCreatedNew = optionfile.createNewFile();
         if (wasCreatedNew) {
            logger.info("Options-Datei erfolgreich angelegt! Öffne " + OptionenDialog.class.getSimpleName());
            OptionenDialog optionwindow = new OptionenDialog(null, true);
            optionwindow.setVisible(true);
         }
      } catch (Exception e) {
         logger.error("Options-Datei konnte nicht angelegt werden. CreatedNew=" + wasCreatedNew, e);
      }
   }

   private void loadOptions(File optionfile) {

      try {
         Properties prop = new Properties();
         prop.load(new FileInputStream(optionfile));
         optionen = new Options.Build(prop).build();
      } catch (Exception e) {
         logger.error("Startfenster.java: Options-Datei konnte nicht geladen werden.", e);
      }
   }

   /**
    * Tabelle mit Forderungen wird mit Daten aus der Datenbank gefüllt
    */
   private void initForderungen() {
      // Spaltenbreiten merken
      int[][] spaltenBreiten = getSpaltenBreiten(this.jTableForderungen);

      List<Forderung> forderungen = persister.getForderungen();
      // Ergebnise einzeln zum Model hinzufügen
      double summe = 0;
      DateFormat dateFormat = DateFormat.getDateInstance();

      for (Iterator<Forderung> iterator = forderungen.iterator(); iterator.hasNext();) {
         Forderung forderung = iterator.next();
         Vector<String> datensatz;
         datensatz = new Vector<String>();
         datensatz.add(forderung.getAuftraggeber());
         datensatz.add(dateFormat.format(forderung.getDatum().getTime()));
         datensatz.add(df.format(forderung.getSumme()) + "€");
         summe += forderung.getSumme();
         forderungenTableModel.addRow(datensatz);
         forderungen_ids.add(Integer.valueOf(forderung.getId()));
      }

      // In der letzten Zeile die Summe ausgeben
      Vector<String> datensatz;
      datensatz = new Vector<String>();
      datensatz.add("Summe");
      datensatz.add("");
      datensatz.add(df.format(summe) + " €");
      forderungenTableModel.addRow(datensatz);

      restoreSpaltenBreiten(jTableForderungen, spaltenBreiten);

      jTableForderungen.setDefaultRenderer(String.class, new StartFensterTableCellRenderer());
   }

   private void restoreSpaltenBreiten(JTable tab, int[][] spaltenBreiten) {

      int[] breitenMax = spaltenBreiten[0];
      int[] breitenOpt = spaltenBreiten[1];
      int[] breitenMin = spaltenBreiten[2];

      for (int i = 0; i < breitenMax.length; i++) {
         tab.getColumnModel().getColumn(i).setMaxWidth(breitenMax[i]);
         tab.getColumnModel().getColumn(i).setPreferredWidth(breitenOpt[i]);
         tab.getColumnModel().getColumn(i).setMinWidth(breitenMin[i]);
      }
   }

   private int[][] getSpaltenBreiten(JTable tab) {
      int spaltenzahl = tab.getColumnModel().getColumnCount();

      int[] breitenMax = new int[spaltenzahl];
      int[] breitenOpt = new int[spaltenzahl];
      int[] breitenMin = new int[spaltenzahl];

      for (int i = 0; i < tab.getColumnModel().getColumnCount(); i++) {
         breitenMax[i] = tab.getColumnModel().getColumn(i).getMaxWidth();
         breitenOpt[i] = tab.getColumnModel().getColumn(i).getPreferredWidth();
         breitenMin[i] = tab.getColumnModel().getColumn(i).getMinWidth();
      }
      int[][] res = { breitenMax, breitenOpt, breitenMin };
      return res;
   }

   /**
    * Tabelle mit gearbeiteten Einheiten wird mit Daten aus der Datenbank
    * gefüllt
    */
   private void initEinheiten() {
      int[][] spaltenBreiten = getSpaltenBreiten(jTableEinheiten);

      double summe = 0;

      List<Einheit> einheiten = persister.getEinheiten();

      for (Iterator<Einheit> iterator = einheiten.iterator(); iterator.hasNext();) {
         Einheit einheit = iterator.next();
         Einheiten_ids.add(Integer.valueOf(einheit.getId()));

         summe += einheit.getKlientenpreis();

         Vector<String> datensatz;
         datensatz = new Vector<String>();

         datensatz.add(einheit.getAuftraggeber());
         datensatz.add(String.valueOf(einheit.getAnzahl()));
         datensatz.add(df.format(einheit.getKlientenpreis()) + " €");
         einheitenTableModel.addRow(datensatz);
      }

      // In der letzten Zeile die Summe ausgeben
      Vector<String> datensatz;
      datensatz = new Vector<String>();
      datensatz.add("Summe");
      datensatz.add("");

      datensatz.add(df.format(summe) + " €");
      einheitenTableModel.addRow(datensatz);

      restoreSpaltenBreiten(jTableEinheiten, spaltenBreiten);

      jTableEinheiten.setDefaultRenderer(String.class, new StartFensterTableCellRenderer());
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed"
   // desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jButtonKlientenEditor = new javax.swing.JButton();
      jButtonBeenden = new javax.swing.JButton();
      jButtonArtenEinheiten = new javax.swing.JButton();
      jButton1 = new javax.swing.JButton();
      jPanel1 = new javax.swing.JPanel();
      Status_links = new javax.swing.JLabel();
      jLabel3 = new javax.swing.JLabel();
      jSplitPane1 = new javax.swing.JSplitPane();
      jPanel2 = new javax.swing.JPanel();
      jScrollPane1 = new javax.swing.JScrollPane();
      jTableForderungen = new javax.swing.JTable();
      jLabel1 = new javax.swing.JLabel();
      jPanel3 = new javax.swing.JPanel();
      jLabel2 = new javax.swing.JLabel();
      jScrollPane2 = new javax.swing.JScrollPane();
      jTableEinheiten = new javax.swing.JTable();
      jMenuBar1 = new javax.swing.JMenuBar();
      jMenuDatei = new javax.swing.JMenu();
      jMenuItemBeenden = new javax.swing.JMenuItem();
      jMenuBearbeiten = new javax.swing.JMenu();
      jMenuItemOption = new javax.swing.JMenuItem();
      jMenuHilfe = new javax.swing.JMenu();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
      setTitle(resourceMap.getString("Form.title")); // NOI18N
      setName("Form"); // NOI18N

      jButtonKlientenEditor.setText(resourceMap.getString("jButtonKlientenEditor.text")); // NOI18N
      jButtonKlientenEditor.setMaximumSize(new java.awt.Dimension(500, 25));
      jButtonKlientenEditor.setMinimumSize(new java.awt.Dimension(120, 25));
      jButtonKlientenEditor.setName("jButtonKlientenEditor"); // NOI18N
      jButtonKlientenEditor.setPreferredSize(new java.awt.Dimension(151, 25));
      jButtonKlientenEditor.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButtonKlientenEditorActionPerformed(evt);
         }
      });

      jButtonBeenden.setText(resourceMap.getString("jButtonBeenden.text")); // NOI18N
      jButtonBeenden.setMaximumSize(new java.awt.Dimension(500, 25));
      jButtonBeenden.setMinimumSize(new java.awt.Dimension(120, 25));
      jButtonBeenden.setName("jButtonBeenden"); // NOI18N
      jButtonBeenden.setPreferredSize(new java.awt.Dimension(151, 25));
      jButtonBeenden.setAction(quitAction);

      jButtonArtenEinheiten.setText(resourceMap.getString("jButtonArtenEinheiten.text")); // NOI18N
      jButtonArtenEinheiten.setMaximumSize(new java.awt.Dimension(500, 25));
      jButtonArtenEinheiten.setMinimumSize(new java.awt.Dimension(120, 25));
      jButtonArtenEinheiten.setName("jButtonArtenEinheiten"); // NOI18N
      jButtonArtenEinheiten.setPreferredSize(new java.awt.Dimension(151, 25));

      jButton1.setText(resourceMap.getString("jButtonEinheiten.text")); // NOI18N
      jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      jButton1.setMaximumSize(new java.awt.Dimension(500, 25));
      jButton1.setMinimumSize(new java.awt.Dimension(120, 25));
      jButton1.setName("jButtonEinheiten"); // NOI18N
      jButton1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
      jPanel1.setName("jPanel1"); // NOI18N

      Status_links.setFont(Font.getFont(resourceMap.getString("Status_links.font"))); // NOI18N
      Status_links.setText(resourceMap.getString("Status_links.text")); // NOI18N
      Status_links.setName("Status_links"); // NOI18N

      jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
      jLabel3.setName("jLabel3"); // NOI18N

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanel1Layout.createSequentialGroup().addComponent(Status_links).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 595, Short.MAX_VALUE)
                  .addComponent(jLabel3)));
      jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanel1Layout
                  .createSequentialGroup()
                  .addGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addComponent(Status_links, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

      jSplitPane1.setDividerLocation(297);
      jSplitPane1.setResizeWeight(0.5);
      jSplitPane1.setName("jSplitPane1"); // NOI18N
      jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

         @Override
         public void propertyChange(java.beans.PropertyChangeEvent evt) {
            jSplitPane1PropertyChange(evt);
         }
      });

      jPanel2.setName("jPanel2"); // NOI18N

      jScrollPane1.setName("jScrollPane1"); // NOI18N

      // jTableForderungen.setModel(new
      // LabledStringValueNoneditableTableModel(new String[] {
      // "Firma", "Rechnungsdatum", "Forderung" }));

      jTableEinheiten.setModel(einheitenTableModel);
      jTableForderungen.setModel(forderungenTableModel);

      jTableForderungen.setName("jTableForderungen"); // NOI18N
      jTableForderungen.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseClicked(MouseEvent evt) {
            jTableForderungenMouseClicked(evt);
         }
      });
      jScrollPane1.setViewportView(jTableForderungen);
      jTableForderungen.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableForderungen.columnModel.title0")); // NOI18N
      jTableForderungen.getColumnModel().getColumn(1).setMinWidth(50);
      jTableForderungen.getColumnModel().getColumn(1).setPreferredWidth(80);
      jTableForderungen.getColumnModel().getColumn(1).setMaxWidth(95);
      jTableForderungen.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableForderungen.columnModel.title1")); // NOI18N
      jTableForderungen.getColumnModel().getColumn(2).setMinWidth(60);
      jTableForderungen.getColumnModel().getColumn(2).setPreferredWidth(60);
      jTableForderungen.getColumnModel().getColumn(2).setMaxWidth(80);
      jTableForderungen.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableForderungen.columnModel.title2")); // NOI18N

      jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setName("jLabel1"); // NOI18N

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout
            .setHorizontalGroup(jPanel2Layout
                  .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                  .addGroup(
                        jPanel2Layout.createSequentialGroup().addGap(12, 12, 12).addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                              .addContainerGap()));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanel2Layout.createSequentialGroup().addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)));

      jSplitPane1.setLeftComponent(jPanel2);

      jPanel3.setName("jPanel3"); // NOI18N

      jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
      jLabel2.setName("jLabel2"); // NOI18N

      jScrollPane2.setName("jScrollPane2"); // NOI18N

      // jTableEinheiten.setModel(new LabledStringValueNoneditableTableModel(
      // new String[] {"Firma", "Stundenanzahl", "Summe" }));

      jTableEinheiten.setName("jTableEinheiten"); // NOI18N
      jTableEinheiten.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseClicked(MouseEvent evt) {
            jTableEinheitenMouseClicked(evt);
         }
      });
      jScrollPane2.setViewportView(jTableEinheiten);
      jTableEinheiten.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableEinheiten.columnModel.title0")); // NOI18N
      jTableEinheiten.getColumnModel().getColumn(1).setMinWidth(50);
      jTableEinheiten.getColumnModel().getColumn(1).setPreferredWidth(80);
      jTableEinheiten.getColumnModel().getColumn(1).setMaxWidth(95);
      jTableEinheiten.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableEinheiten.columnModel.title1")); // NOI18N
      jTableEinheiten.getColumnModel().getColumn(2).setMinWidth(60);
      jTableEinheiten.getColumnModel().getColumn(2).setPreferredWidth(60);
      jTableEinheiten.getColumnModel().getColumn(2).setMaxWidth(80);
      jTableEinheiten.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableEinheiten.columnModel.title2")); // NOI18N

      javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                  jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE).addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE));
      jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            jPanel3Layout.createSequentialGroup().addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)));

      jSplitPane1.setRightComponent(jPanel3);

      jMenuBar1.setName("jMenuBar1"); // NOI18N

      jMenuDatei.setText(resourceMap.getString("jMenuDatei.text")); // NOI18N
      jMenuDatei.setName("jMenuDatei"); // NOI18N

      jMenuItemBeenden.setAction(quitAction); // NOI18N
      jMenuItemBeenden.setText(resourceMap.getString("jMenuItemBeenden.text")); // NOI18N
      jMenuItemBeenden.setName("jMenuItemBeenden"); // NOI18N
      jMenuDatei.add(jMenuItemBeenden);

      jMenuBar1.add(jMenuDatei);

      jMenuBearbeiten.setText(resourceMap.getString("jMenuBearbeiten.text")); // NOI18N
      jMenuBearbeiten.setName("jMenuBearbeiten"); // NOI18N

      jMenuItemOption.setText(resourceMap.getString("jMenuItemOption.text")); // NOI18N
      jMenuItemOption.setName("jMenuItemOption"); // NOI18N
      jMenuItemOption.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jMenuItemOptionActionPerformed(evt);
         }
      });
      jMenuBearbeiten.add(jMenuItemOption);

      jMenuBar1.add(jMenuBearbeiten);

      jMenuHilfe.setText(resourceMap.getString("jMenuHilfe.text")); // NOI18N
      jMenuHilfe.setName("jMenuHilfe"); // NOI18N
      jMenuBar1.add(jMenuHilfe);

      setJMenuBar(jMenuBar1);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(
                  layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                              layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                                    .addGroup(
                                          javax.swing.GroupLayout.Alignment.LEADING,
                                          layout.createSequentialGroup()
                                                .addComponent(jButtonKlientenEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonArtenEinheiten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            javax.swing.GroupLayout.Alignment.TRAILING,
            layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                              .addComponent(jButtonKlientenEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                              .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addComponent(jButtonArtenEinheiten, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                  .addGap(24, 24, 24).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)));

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void jButtonKlientenEditorActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonKlientenEditorActionPerformed
      openKlientenEditor(-1, null);
   }// GEN-LAST:event_jButtonKlientenEditorActionPerformed

   private void openKlientenEditor(int KlientenID, StartTable Tabelle) {
      
      KlientenEditor klienteneditor = new KlientenEditor(this);
      klienteneditor.arbeitsstundenTabelle1.addPropertyChangeListener("ArbeitsstundenTabelle.Tabellendaten", this);

      if (Tabelle == StartTable.FORDERUNGEN)
         klienteneditor.setFilter(KlientenEditor.EINGEREICHTE);
      if (Tabelle == StartTable.EINHEITEN)
         klienteneditor.setFilter(KlientenEditor.NICHTEINGEREICHTE);

      klienteneditor.setVisible(true);

      if (KlientenID != -1) {
         klienteneditor.setCurrentKlientenId(KlientenID);
      }
   }

   /**
    * Daten in einem registrierten Fenster wurden geändert und hier wird
    * aktualisiert! Bisher nur ArbeitsstundenTabelle.Tabellendaten
    * 
    * @param evt
    */
   @Override
   public void propertyChange(java.beans.PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("ArbeitsstundenTabelle.Tabellendaten")) {
         initForderungen();
         initEinheiten();
      }
   }

   private void jButton1ActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
      Arbeitsstunden arbeitsstunden = new Arbeitsstunden(optionen);
      arbeitsstunden.setVisible(true);
   }// GEN-LAST:event_jButton1ActionPerformed

   private void jTableForderungenMouseClicked(MouseEvent evt) {// GEN-FIRST:event_jTableForderungenMouseClicked
      // System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
      if (this.jTableForderungen.getSelectedRowCount() == 0)
         jTableForderungenSetSelection(evt);
      if (evt.getClickCount() > 1 && !evt.isPopupTrigger()) {
         // Doppelklick mit links -> Klienteneditor
         // SelectedRow wird in ID-Liste gesucht und gefundene ID an
         // Klienteneditor übergeben.
         Integer elementAt = this.forderungen_ids.elementAt(this.jTableForderungen.getSelectedRow());
         openKlientenEditor(elementAt.intValue(), StartTable.FORDERUNGEN);
      }
      // Irgendwann ein Menü?
      /*
       * if (evt.isPopupTrigger()) { this.jPopupMenu1.show(jTable1,
       * evt.getX(), evt.getY()); System.out.println("Rechtsklick!"); }
       */
   }// GEN-LAST:event_jTableForderungenMouseClicked

   private void jTableEinheitenMouseClicked(MouseEvent evt) {// GEN-FIRST:event_jTableEinheitenMouseClicked
      // System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
      if (this.jTableForderungen.getSelectedRowCount() == 0)
         jTableEinheitenSetSelection(evt);
      if (evt.getClickCount() > 1 && !evt.isPopupTrigger()) {

         int selectedRow = this.jTableEinheiten.getSelectedRow();
         Integer elementAt = this.Einheiten_ids.elementAt(selectedRow);
         openKlientenEditor(elementAt.intValue(), StartTable.EINHEITEN);
      }

   }// GEN-LAST:event_jTableEinheitenMouseClicked

   private void jMenuItemOptionActionPerformed(ActionEvent evt) {
      // Öffne Options-Fenster
      OptionenDialog optionwindow = new OptionenDialog(this, false);
      optionwindow.setVisible(true);
   }

   private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {// GEN-FIRST:event_jSplitPane1PropertyChange

      this.jLabel3.setText("Divider: " + this.jSplitPane1.getDividerLocation());
   }// GEN-LAST:event_jSplitPane1PropertyChange

   private void jTableEinheitenSetSelection(MouseEvent evt) {
      // System.out.println("Zeile wird selectiert.");
      java.awt.Point p = evt.getPoint();
      int zeile = this.jTableEinheiten.rowAtPoint(p);
      this.jTableEinheiten.getSelectionModel().setSelectionInterval(zeile, zeile);
   }

   private void jTableForderungenSetSelection(MouseEvent evt) {
      java.awt.Point p = evt.getPoint();
      int zeile = this.jTableForderungen.rowAtPoint(p);
      this.jTableForderungen.getSelectionModel().setSelectionInterval(zeile, zeile);
   }

   private void exit() {
      setVisible(false);
      dispose();
   }

   Action quitAction = new AbstractAction("Anwendung beenden") {

      private static final long serialVersionUID = -7037463710032412611L;

      @Override
      public void actionPerformed(ActionEvent e) {
         exit();
      }

   };

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private JLabel Status_links;
   private JButton jButton1;
   private JButton jButtonArtenEinheiten;
   private JButton jButtonBeenden;
   private JButton jButtonKlientenEditor;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JMenuBar jMenuBar1;
   private JMenu jMenuBearbeiten;
   private JMenu jMenuDatei;
   private JMenu jMenuHilfe;
   private JMenuItem jMenuItemBeenden;
   private JMenuItem jMenuItemOption;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JSplitPane jSplitPane1;
   private JTable jTableEinheiten;
   private JTable jTableForderungen;
   // End of variables declaration//GEN-END:variables

}
