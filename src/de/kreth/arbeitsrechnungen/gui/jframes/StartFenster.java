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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.StartFensterTableCellRenderer;
import de.kreth.arbeitsrechnungen.data.Einheit;
import de.kreth.arbeitsrechnungen.gui.dialogs.OptionenDialog;
import de.kreth.arbeitsrechnungen.gui.jframes.starttablemodels.LabeledTableModel;
import de.kreth.arbeitsrechnungen.persister.DatenPersister;
import de.kreth.arbeitsrechnungen.persister.DatenPersister.Forderung;

public class StartFenster extends JFrame implements PropertyChangeListener {

   public enum StartTable {
      FORDERUNGEN, EINHEITEN
   }

   private static final long serialVersionUID = -1175489292478287196L;
   private Logger logger;

   private DecimalFormat df = new DecimalFormat("0.00");

   private Options optionen = null;
   private StartTableModel forderungenTableModel;
   private StartTableModel einheitenTableModel;
   private DatenPersister persister;

   public StartFenster() {
      this(null);
   }
   
   public StartFenster(Options options) {
      logger = LogManager.getLogger(getClass());

      if(options == null)
         loadOrCreateOptions();
      else
         this.optionen = options;

      persister = new DatenPersister(optionen);

      // Model mit Überschriften erstellen
//      einheitenTableModel = new LabledStringValueNoneditableTableModel(new String[] { "Firma", "Einsätze", "Summe" });
      einheitenTableModel = new StartTableModel(new String[] { "Firma", "Einsätze", "Summe" });
      forderungenTableModel = new StartTableModel(new String[] { "Firma", "Rechnungsdatum", "Forderung" });

      initComponents();
      
      jTableForderungen.setDefaultRenderer(String.class, new StartFensterTableCellRenderer());
      
      initForderungen();
      initEinheiten();
      initHintman();
      this.jButtonArtenEinheiten.setEnabled(false);

   }

   private void initHintman() {

      this.jButtonKlientenEditor.setToolTipText("Startet den Klienteneditor, einen umfassenden Werkzeug für die meisten Programmfunktionen");
      this.jTableEinheiten.setToolTipText("Geleistete Arbeitsstunden, noch nicht abgerechnet");
      this.jTableForderungen.setToolTipText("Abgerechnete Arbeitsstunden, noch nicht bezahlt");
      this.jButtonBeenden.setToolTipText("Beendet das Programm und schließt alle zugehörigen Fenster");
      this.jButtonArtenEinheiten.setToolTipText("TODO: Einheitenarten");
      this.jButton1.setToolTipText("Einfacher Editor für Arbeitsstunden");
      this.jMenuItemBeenden.setToolTipText("Beendet das Programm und schließt alle zugehörigen Fenster");
   }

   protected void loadOrCreateOptions() {
      // Testen ob das arbeitsverzeichnis im home-verzeichnis existiert
      File homeverzeichnis;
      Properties sysprops = System.getProperties();
      String homedir = sysprops.getProperty("user.home");
      homeverzeichnis = new File(homedir, Options.BENUTZERVERZEICHNIS);

      if (!homeverzeichnis.exists()) {
         // Verzeichnis anlegen
         logger.info(homeverzeichnis.getAbsolutePath() + " existiert nicht!\nwird angelegt...");
         homeverzeichnis.mkdirs();
      }

      File optionfile = new File(homedir + sysprops.getProperty("file.separator") + Options.BENUTZERVERZEICHNIS + sysprops.getProperty("file.separator") + "optionen.ini");

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
      if(optionen == null) {
         try {
            logger.debug("loading Option properties from "+ optionfile.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(new FileInputStream(optionfile));
            optionen = new Options.Build(prop).build();
         } catch (Exception e) {
            logger.error("Startfenster.java: Options-Datei konnte nicht geladen werden.", e);
         }
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
         Element e = new Element();
         e.name = forderung.getAuftraggeber();
         e.count = dateFormat.format(forderung.getDatum().getTime());
         e.price = df.format(forderung.getSumme()) + "€";
         e.id = forderung.getId();
         
         summe += forderung.getSumme();
         forderungenTableModel.add(e);
      }

      // In der letzten Zeile die Summe ausgeben
      Element e = new Element();
      e.name = "Summe";
      e.count = "";
      e.price = df.format(summe) + " €";
      e.id = -1;
      
      forderungenTableModel.add(e);

      restoreSpaltenBreiten(jTableForderungen, spaltenBreiten);

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

         summe += einheit.getKlientenpreis();

         Element e = new Element();
         e.id = einheit.getId();
         e.name = einheit.getAuftraggeber();
         e.count = String.valueOf(einheit.getAnzahl());
         e.price = df.format(einheit.getKlientenpreis()) + " €";
         einheitenTableModel.add(e);
      }

      // In der letzten Zeile die Summe ausgeben
      Element e = new Element();
      e.name = "Summe";
      e.count = "";
      e.price = df.format(summe) + " €";
      
      einheitenTableModel.add(e);

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

      jButtonKlientenEditor = new JButton();
      jButtonBeenden = new JButton();
      jButtonArtenEinheiten = new JButton();
      jButton1 = new JButton();
      jPanel1 = new JPanel();
      Status_links = new JLabel();
      jLabel3 = new JLabel();
      jSplitPane1 = new JSplitPane();
      jPanel2 = new JPanel();
      jScrollPane1 = new JScrollPane();
      jTableForderungen = new JTable();
      jLabel1 = new JLabel();
      jPanel3 = new JPanel();
      jLabel2 = new JLabel();
      jScrollPane2 = new JScrollPane();
      jTableEinheiten = new JTable();
      jMenuBar1 = new JMenuBar();
      jMenuDatei = new JMenu();
      jMenuItemBeenden = new JMenuItem();
      jMenuBearbeiten = new JMenu();
      jMenuItemOption = new JMenuItem();
      jMenuHilfe = new JMenu();

      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
      setTitle(resourceMap.getString("Form.title")); // NOI18N
      setName("Form"); // NOI18N

      addWindowListener(new WindowListener() {
         
         @Override
         public void windowOpened(WindowEvent e) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void windowIconified(WindowEvent e) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void windowDeiconified(WindowEvent e) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void windowDeactivated(WindowEvent e) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void windowClosing(WindowEvent e) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void windowClosed(WindowEvent e) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void windowActivated(WindowEvent e) {
            jFrameWindowActivated(e);
         }
      });
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
      jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
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

      GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanel1Layout.createSequentialGroup().addComponent(Status_links).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 595, Short.MAX_VALUE)
                  .addComponent(jLabel3)));
      jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanel1Layout
                  .createSequentialGroup()
                  .addGroup(
                        jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                              .addComponent(Status_links, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)).addContainerGap()));

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

      jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setName("jLabel1"); // NOI18N

      GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout
            .setHorizontalGroup(jPanel2Layout
                  .createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                  .addGroup(
                        jPanel2Layout.createSequentialGroup().addGap(12, 12, 12).addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                              .addContainerGap()));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanel2Layout.createSequentialGroup().addComponent(jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)));

      jSplitPane1.setLeftComponent(jPanel2);

      jPanel3.setName("jPanel3"); // NOI18N

      jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
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

      GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
      jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(jPanel3Layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING,
                  jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE).addContainerGap())
            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE));
      jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanel3Layout.createSequentialGroup().addComponent(jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)));

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

      GroupLayout layout = new GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(
                  layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                              layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(jSplitPane1, GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                                    .addGroup(
                                          GroupLayout.Alignment.LEADING,
                                          layout.createSequentialGroup()
                                                .addComponent(jButtonKlientenEditor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                      GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                      GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonArtenEinheiten, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                      GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonBeenden, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                      GroupLayout.PREFERRED_SIZE))).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            GroupLayout.Alignment.TRAILING,
            layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                              .addComponent(jButtonKlientenEditor, GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                              .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jButtonArtenEinheiten, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jButtonBeenden, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                  .addGap(24, 24, 24).addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)));

      pack();
   }// </editor-fold>//GEN-END:initComponents

   protected void jFrameWindowActivated(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   private void jButtonKlientenEditorActionPerformed(ActionEvent evt) {
      openKlientenEditor(-1, null);
   }

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

   private void jButton1ActionPerformed(ActionEvent evt) {
      Arbeitsstunden arbeitsstunden = new Arbeitsstunden(optionen);
      arbeitsstunden.setVisible(true);
   }

   private void jTableForderungenMouseClicked(MouseEvent evt) {
      // System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
      if (this.jTableForderungen.getSelectedRowCount() == 0)
         jTableForderungenSetSelection(evt);
      if (evt.getClickCount() > 1 && !evt.isPopupTrigger()) {
         // Doppelklick mit links -> Klienteneditor
         // SelectedRow wird in ID-Liste gesucht und gefundene ID an
         // Klienteneditor übergeben.
         Element e = forderungenTableModel.getItem(this.jTableForderungen.getSelectedRow());
         openKlientenEditor(e.id, StartTable.FORDERUNGEN);
      }
      // Irgendwann ein Menü?
      /*
       * if (evt.isPopupTrigger()) { this.jPopupMenu1.show(jTable1,
       * evt.getX(), evt.getY()); System.out.println("Rechtsklick!"); }
       */
   }

   private void jTableEinheitenMouseClicked(MouseEvent evt) {
      // System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
      if (this.jTableForderungen.getSelectedRowCount() == 0)
         jTableEinheitenSetSelection(evt);
      if (evt.getClickCount() > 1 && !evt.isPopupTrigger()) {

         int selectedRow = this.jTableEinheiten.getSelectedRow();
         Element selected = einheitenTableModel.getItem(selectedRow);
         openKlientenEditor(selected.id, StartTable.EINHEITEN);
      }

   }

   private void jMenuItemOptionActionPerformed(ActionEvent evt) {
      // Öffne Options-Fenster
      OptionenDialog optionwindow = new OptionenDialog(this, false);
      optionwindow.setVisible(true);
   }

   private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {

      this.jLabel3.setText("Divider: " + this.jSplitPane1.getDividerLocation());
   }

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

   private class Element {
      int id;
      String name;
      String count;
      String price;
   }
   
   private class StartTableModel extends LabeledTableModel<Element> {

      public StartTableModel(String[] titles) {
         super(titles);
      }

      @Override
      public Class<?> getColumnClass(int columnIndex) {
         return String.class;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
         Element item = getItem(rowIndex);
         switch (columnIndex) {
            case 0:
               return item.name;

            case 1:
               return item.count;

            case 2:
               return item.price;

            default:
               return null;
         }
      }
      
   }
}
