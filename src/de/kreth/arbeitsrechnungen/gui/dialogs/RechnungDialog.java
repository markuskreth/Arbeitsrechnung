/*
 * RechnungDialog.java
 * Created on 02.09.2009, 11:52:54
 */

package de.kreth.arbeitsrechnungen.gui.dialogs;

/**
 * @author markus
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kreth.arbeitsrechnungen.*;
import de.kreth.arbeitsrechnungen.business.RechnungSystemExecutionService;
import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;
import de.kreth.arbeitsrechnungen.report.ShowJasperRechnung;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public class RechnungDialog extends JDialog implements PropertyChangeListener, DocumentListener {

   private static final long serialVersionUID = 3906049054488142992L;
   private Logger logger = LogManager.getLogger(getClass());

   public static final String ERSTELLT = "Rechnung erstellt";

   private RechnungDialogPersister persister;
   private KlientenEditorPersister klientenPersister;

   /**
    * Wird in Konstruktor 2 geändert - true: daten aus Datenbank gesammelt -
    * Rechnung per INSERT gespeichert false: Rechnung aus Datenbank geladen -
    * Rechnung per UPDATE gespeichert
    */
   private boolean useCreateinsteadUpdate = true;
   private Calendar heute;
   boolean stunden_vorhanden = false;
   boolean zusammenfassungen_erlauben;

   private PropertyChangeSupport pchListeners = new PropertyChangeSupport(this);

   private Component prevComponent = null;
   private Rechnung rechnung = null;
   private Vector<Arbeitsstunde> einheiten;
   private Klient klient;

   private RechnungDialog(Options optionen, Window parent) {
      super(parent);
      setModal(true);

      initComponents();
      klientenPersister = (KlientenEditorPersister) ArbeitRechnungFactory.getInstance().getPersister(KlientenEditorPersister.class, optionen);
      persister = (RechnungDialogPersister) ArbeitRechnungFactory.getInstance().getPersister(RechnungDialogPersister.class, optionen);

      heute = new GregorianCalendar();
   }

   /**
    * Erstellt einen neuen RechnungsDialog für die übergebene RechnungsId. Die
    * alte Rechnung wird angezeigt.
    * 
    * @param optionen
    * @param parent
    * @param rechnungId
    */
   public RechnungDialog(Options optionen, Window parent, int rechnungId) {
      this(optionen, parent);

      logger.info("Öffne Rechnungdialog mit RechnungId " + rechnungId);
      this.jToggleButtonDetails.setSelected(false);
      toggleDetails();

      this.useCreateinsteadUpdate = false;

      int klienten_id = persister.getKlientenIdForRechnungId(rechnungId);

      klient = klientenPersister.getKlientById(klienten_id);
      logger.debug("found klient: " + klient);
      String z1 = klient.getZusatz1_Name();
      String z2 = klient.getZusatz2_Name();
      rechnung = persister.getRechnungById(rechnungId).zusatz1Name(z1).zusatz2Name(z2).build();
      logger.debug("rechnung geladen: " + rechnung);
      einheiten = persister.getEinheiten(rechnungId);
      logger.trace("Einheiten geladen: " + einheiten);

      this.jCheckBoxStundenzahl.setSelected(this.stunden_vorhanden);
      this.jCheckBoxStundenzahl.setVisible(this.stunden_vorhanden);

      werteVonRechnungInFormularEintragen();

      // Zusatz-Checkboxen konfigurieren
      if (rechnung.hasZusatz1()) {
         this.jCheckBoxZusatz1.setText(klient.getZusatz1_Name());
      } else {
         this.jCheckBoxZusatz1.setVisible(false);
      }
      if (rechnung.hasZusatz2()) {
         this.jCheckBoxZusatz2.setText(klient.getZusatz2_Name());
      } else {
         this.jCheckBoxZusatz2.setVisible(false);
      }
      makeTable();
      setListeners();
   }

   private void werteVonRechnungInFormularEintragen() {

      this.jTextKlient.setText(rechnung.getAdresse());
      this.jTextTexDatei.setText(rechnung.getTexdatei());
      this.jDateRechnungsdatum.setCalendar(rechnung.getDatum());
      this.jDateZahlDatum.setCalendar(rechnung.getZahldatum());
      this.jTextRechnungsnummer.setText(rechnung.getRechnungnr());

      this.jCheckBoxStundenzahl.setSelected(this.stunden_vorhanden);
      this.jCheckBoxStundenzahl.setVisible(this.stunden_vorhanden);

      this.jTextKlient.setText(rechnung.getAdresse());
      this.jTextTexDatei.setText(rechnung.getTexdatei());

      this.jToggleButtonDetails.setSelected(false);
      toggleDetails();

      if (this.klient.hasZusatz1()) {
         this.jCheckBoxZusatz1.setText(this.klient.getZusatz1_Name());
      } else {
         this.jCheckBoxZusatz1.setVisible(false);
      }
      if (this.klient.hasZusatz2()) {
         this.jCheckBoxZusatz2.setText(this.klient.getZusatz2_Name());
      } else {
         this.jCheckBoxZusatz2.setVisible(false);
      }

   }

   /**
    * Erstellt eine neue Rechnung für die übergebenen Einheiten.
    * 
    * @param optionen
    * @param parent
    * @param einheiten
    */
   public RechnungDialog(Options optionen, Window parent, Vector<Integer> einheiten) {
      this(optionen, parent);

      zusammenfassungen_erlauben = this.jToggleButtonZusammenfassungen.isSelected();

      logger.debug("Lade Rechnungen durch Einheiten");
      this.einheiten = persister.getEinheitenByIds(einheiten);

      if (this.einheiten.size() > 0)
         this.klient = klientenPersister.getKlientById(this.einheiten.get(0).getKlientenID());
      else
         throw new IllegalArgumentException("Einheiten dürfen nicht leer sein");

      Calendar zahldatum = new GregorianCalendar();
      zahldatum.setTime(heute.getTime());
      zahldatum.add(Calendar.MONTH, 1);

      Builder reBuilder = new Rechnung.Builder().texdatei(optionen.getStdTexFile()).datum(heute).zahldatum(zahldatum).einheiten(this.einheiten)
            .rechnungnr(generateRechnungsnr(heute)).klienten_id(this.klient.getKlienten_id()).zusatz1(klient.hasZusatz1()).zusatz2(klient.hasZusatz2())
            .zusatz1Name(klient.getZusatz1_Name()).zusatz2Name(klient.getZusatz2_Name());

      setAdresse(reBuilder);

      if (klient.getTex_datei() != null && !klient.getTex_datei().isEmpty()) {
         reBuilder.texdatei(klient.getTex_datei());
      }

      rechnung = reBuilder.build();

      logger.trace("Rechnungsobjekt instanziiert: " + rechnung);
      werteVonRechnungInFormularEintragen();
      makeTable();
      setListeners();
   }

   /**
    * Versieht Formular-Felder mit EventListenern
    **/
   private void setListeners() {
      this.jDateRechnungsdatum.addPropertyChangeListener(this);
      this.jDateZahlDatum.addPropertyChangeListener(this);
      this.jTextTexDatei.getDocument().addDocumentListener(this);
      this.jTextKlient.getDocument().addDocumentListener(this);
      this.jTextRechnungsnummer.getDocument().addDocumentListener(this);
   }

   @Override
   public void propertyChange(PropertyChangeEvent e) {
      JComponent quelle = (JComponent) e.getSource();

      logger.debug("Neuer Wert bei " + quelle.getName() + ": " + e.getNewValue());
      if (quelle.getName().matches(jDateRechnungsdatum.getName())) {
         rechnung.setDatum(this.jDateRechnungsdatum.getCalendar());
      }
      if (quelle.getName().matches(jDateZahlDatum.getName())) {
         rechnung.setZahldatum(this.jDateZahlDatum.getCalendar());
      }
   }

   @Override
   public void insertUpdate(DocumentEvent e) {
      changeText(e);
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
      changeText(e);
   }

   @Override
   public void changedUpdate(DocumentEvent e) {
      changeText(e);
   }

   private void changeText(DocumentEvent e) {
      logger.debug(e.getDocument().getProperty("name"));
      logger.debug(" wurde geändert!");
      logger.debug("DefaultRootElement: " + e.getDocument().getDefaultRootElement());
      if (e.getDocument().equals(this.jTextKlient.getDocument())) {
         logger.debug("Adresse geändert!");
         this.rechnung.setAdresse(this.jTextKlient.getText());
      }
      if (e.getDocument().equals(this.jTextTexDatei.getDocument())) {
         logger.debug("Tex-Datei geändert!");
         this.rechnung.setTexdatei(this.jTextTexDatei.getText());
      }
      if (e.getDocument().equals(this.jTextRechnungsnummer.getDocument())) {
         logger.debug("Rechnungsnummer geändert!");
         this.rechnung.setRechnungnr(this.jTextRechnungsnummer.getText());
      }
   }

   private void makeTable() {
      // Ja/Nein, Datum, Inhalt, Preis
      List<ArbeitsstundenSpalten> spalten = new ArrayList<>();
      spalten.add(ArbeitsstundenSpalten.Checkbox);
      spalten.add(ArbeitsstundenSpalten.Datum);
      spalten.add(ArbeitsstundenSpalten.Inhalt);

      // TableModel instanzieren mit Titeln und 0 Zeilen

      if (this.jCheckBoxStundenzahl.isSelected()) {
         spalten.add(ArbeitsstundenSpalten.Stunden);
      }
      if (this.jCheckBoxZusatz1.isSelected() && rechnung.hasZusatz1()) {
         spalten.add(ArbeitsstundenSpalten.Zusatz1);
      }
      if (this.jCheckBoxZusatz2.isSelected() && rechnung.hasZusatz2()) {
         spalten.add(ArbeitsstundenSpalten.Zusatz2);
      }
      spalten.add(ArbeitsstundenSpalten.Preis);

      String[] spaltentitel = new String[spalten.size()];
      spaltentitel[0] = "";

      for (int i = 1; i < spalten.size(); i++) {
         ArbeitsstundenSpalten tableColumn = spalten.get(i);
         if (tableColumn == ArbeitsstundenSpalten.Zusatz1) {
            spaltentitel[i] = this.klient.getZusatz1_Name();
         } else if (tableColumn == ArbeitsstundenSpalten.Zusatz2) {
            spaltentitel[i] = this.klient.getZusatz2_Name();
         } else {
            spaltentitel[i] = tableColumn.toString();
         }

      }

      DefaultTableModel mymodel = new DefaultTableModel(spaltentitel, 0) {

         /**
          * 
          */
         private static final long serialVersionUID = -5855242848142616562L;

         // Methoden überschreiben
         @Override
         public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
               return Boolean.class;
            return String.class;
         }

         @Override
         public boolean isCellEditable(int row, int column) {
            if (column != 0)
               return false;
            else
               return true;
         }
      };

      for (int i = 0; i < einheiten.size(); i++) {
         Vector<Object> tabellenzeile = new Vector<Object>();

         // ist die Tabelle noch nicht eingerichtet?
         if (jTable1.getModel().getColumnName(0).contains("Title"))
            tabellenzeile.add(new Boolean(true));
         else {
            // Wenn sie schon eingerichtet ist wird der Booleanwert
            // übernommen
            Boolean tmp_bool = Boolean.valueOf(jTable1.getValueAt(i, 0).toString());
            tabellenzeile.add(tmp_bool);
         }

         tabellenzeile.add(einheiten.elementAt(i).getDatum());
         tabellenzeile.add(einheiten.elementAt(i).getInhalt());
         if (this.jCheckBoxStundenzahl.isSelected())
            tabellenzeile.add(Integer.valueOf(einheiten.elementAt(i).getDauerInMinutes() / 60)); // Minuten
         // zu
         // Stunden
         if (this.jCheckBoxZusatz1.isSelected())
            tabellenzeile.add(einheiten.elementAt(i).getZusatz1());
         if (this.jCheckBoxZusatz2.isSelected())
            tabellenzeile.add(einheiten.elementAt(i).getZusatz2());
         tabellenzeile.add(einheiten.elementAt(i).getPreis());
         mymodel.addRow(tabellenzeile);
      }

      jTable1.setModel(mymodel);
      jTable1.getColumnModel().getColumn(0).setMinWidth(15);
      jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
      jTable1.getColumnModel().getColumn(0).setMaxWidth(35);
   }

   /**
    * Ermittelnt aufgrund der Adresse und Rechnngsdatum automatisch die
    * Rechnungsnummer
    * 
    * @return
    */
   private String generateRechnungsnr(Calendar rechnungsdatum) {

      String rechnungsnr = persister.getRechnungsnummer(rechnung, klient);

      logger.debug("rechnungsnr: " + rechnungsnr);

      rechnungsnr += "-" + rechnungsdatum.get(Calendar.YEAR);
      int monat = rechnungsdatum.get(Calendar.MONTH) + 1;

      if (monat < 10)
         rechnungsnr += "-" + 0 + monat;
      else
         rechnungsnr += "-" + monat;

      logger.debug("rechnungsnr: " + rechnungsnr);

      rechnungsnr = persister.checkAndUpdateRechnungNr(rechnungsnr);

      logger.debug("rechnungsnr: " + rechnungsnr);
      return rechnungsnr;
   }

   /**
    * this.adresse wird mit Daten aus der Datenbank gefüllt. Zwingend:
    * this.klienten_id muss gegeben sein
    */
   private void setAdresse(Rechnung.Builder reBuilder) {

      String adresse = klient.getAuftraggeber() + "\n" + "\n" + klient.getAAdress1() + "\n"
            + ((klient.getAAdress2() == null || klient.getAAdress2().isEmpty()) ? "" : klient.getAAdress2() + "\n") + klient.getAPlz() + " " + klient.getAOrt();

      reBuilder.adresse(adresse);
   }

   /**
    * Der untere Teil des Fensters (Tex-Datei, Einheiten-Tabelle) wird ein- und
    * ausgeschaltet
    */
   private void toggleDetails() {
      int hoehe = this.jPanelunten.getHeight();
      int driverlocation = this.jSplitPane1.getDividerLocation();
      // logger.debug("DriverLocation: " + driverlocation);
      if (this.jSplitPane1.getRightComponent() == null) {
         // Details einschalten
         this.setSize(this.getWidth(), this.getHeight() + hoehe);
         this.jPanelunten.setSize(this.jPaneloben.getWidth(), this.jPaneloben.getHeight());
         this.jSplitPane1.setRightComponent(this.jPanelunten);
         this.jSplitPane1.setDividerLocation(driverlocation);
      } else {
         // Details ausschalten
         this.jSplitPane1.setRightComponent(null);
         this.setSize(this.getWidth(), this.getHeight() - hoehe);
      }
      // logger.debug("Endgültige Position: " +
      // this.jSplitPane1.getDividerLocation());
   }

   /**
    * This method is called from within the constructor to initialize the form.
    */
   private void initComponents() {

      jLabel1 = new JLabel();
      jSplitPane1 = new JSplitPane();
      jPanelunten = new JPanel();
      jTabbedPane1 = new JTabbedPane();
      jScrollPane2 = new JScrollPane();
      jTable1 = new JTable();
      jPanel1 = new JPanel();
      jTextTexDatei = new JTextField();
      jLabel6 = new JLabel();
      jButton3 = new JButton();
      jCheckBoxVorschau = new JCheckBox();
      jToggleButtonZusammenfassungen = new JToggleButton();
      jLabel7 = new JLabel();
      jPaneloben = new JPanel();
      jLabel2 = new JLabel();
      jScrollPane1 = new JScrollPane();
      jTextKlient = new JTextArea();
      jLabel3 = new JLabel();
      jDateRechnungsdatum = new com.toedter.calendar.JDateChooser();
      jLabel4 = new JLabel();
      jLabel5 = new JLabel();
      jTextRechnungsnummer = new JTextField();
      jDateZahlDatum = new com.toedter.calendar.JDateChooser();
      jToggleButtonDetails = new JToggleButton();
      jCheckBoxZusatz1 = new JCheckBox();
      jCheckBoxZusatz2 = new JCheckBox();
      jCheckBoxStundenzahl = new JCheckBox();
      jCheckBoxUnterschrift = new JCheckBox();
      jButton1 = new JButton();
      jButtonErstellen = new JButton();

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());

      setTitle(resourceMap.getString("Form.title")); // NOI18N
      setMinimumSize(new Dimension(446, 322));
      setModal(true);
      setName("Form"); // NOI18N

      jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
      jLabel1.setName("jLabel1"); // NOI18N

      jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jSplitPane1.setResizeWeight(0.5);
      jSplitPane1.setName("jSplitPane1"); // NOI18N

      jPanelunten.setMinimumSize(new Dimension(300, 200));
      jPanelunten.setName("jPanelunten"); // NOI18N
      jPanelunten.setPreferredSize(new Dimension(388, 200));

      jTabbedPane1.setName("jTabbedPane1"); // NOI18N

      jScrollPane2.setMinimumSize(new Dimension(22, 0));
      jScrollPane2.setName("jScrollPane2"); // NOI18N

      jTable1.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null }, { null, null, null, null }, { null, null, null, null }, { null, null, null, null } },
            new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
      jTable1.setMinimumSize(new Dimension(60, 0));
      jTable1.setName("jTable1"); // NOI18N
      jScrollPane2.setViewportView(jTable1);
      jTable1.getColumnModel().getColumn(0).setMinWidth(20);
      jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
      jTable1.getColumnModel().getColumn(0).setMaxWidth(20);
      jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
      jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N
      jTable1.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
      jTable1.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N

      jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

      jPanel1.setName("jPanel1"); // NOI18N

      jTextTexDatei.setText(resourceMap.getString("jTextTexDatei.text")); // NOI18N
      jTextTexDatei.setToolTipText(resourceMap.getString("jTextTexDatei.toolTipText")); // NOI18N
      jTextTexDatei.setName("jTextTexDatei"); // NOI18N

      jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
      jLabel6.setName("jLabel6"); // NOI18N

      jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
      jButton3.setName("jButton3"); // NOI18N

      jCheckBoxVorschau.setText(resourceMap.getString("jCheckBoxVorschau.text")); // NOI18N
      jCheckBoxVorschau.setName("jCheckBoxVorschau"); // NOI18N

      jToggleButtonZusammenfassungen.setSelected(true);
      jToggleButtonZusammenfassungen.setText(resourceMap.getString("jToggleButtonZusammenfassungen.text")); // NOI18N
      jToggleButtonZusammenfassungen.setToolTipText(resourceMap.getString("jToggleButtonZusammenfassungen.toolTipText")); // NOI18N
      jToggleButtonZusammenfassungen.setName("jToggleButtonZusammenfassungen"); // NOI18N
      jToggleButtonZusammenfassungen.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jToggleButtonZusammenfassungenActionPerformed(evt);
         }
      });

      jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
      jLabel7.setName("jLabel7"); // NOI18N

      GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout
            .setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel6).addGroup(GroupLayout.Alignment.TRAILING,
                              jPanel1Layout.createSequentialGroup().addComponent(jTextTexDatei, GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
                              .addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(jCheckBoxVorschau)).addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap().addComponent(jToggleButtonZusammenfassungen).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel7)))
                        .addContainerGap()));
      jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextTexDatei, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jButton3,
                              GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jCheckBoxVorschau).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jToggleButtonZusammenfassungen).addComponent(jLabel7))
                  .addContainerGap(70, Short.MAX_VALUE)));

      jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

      GroupLayout jPaneluntenLayout = new GroupLayout(jPanelunten);
      jPanelunten.setLayout(jPaneluntenLayout);
      jPaneluntenLayout
            .setHorizontalGroup(jPaneluntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE));
      jPaneluntenLayout.setVerticalGroup(jPaneluntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jTabbedPane1, GroupLayout.Alignment.TRAILING,
            GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE));

      jSplitPane1.setRightComponent(jPanelunten);

      jPaneloben.setMinimumSize(new Dimension(414, 200));
      jPaneloben.setName("jPaneloben"); // NOI18N

      jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
      jLabel2.setName("jLabel2"); // NOI18N

      jScrollPane1.setName("jScrollPane1"); // NOI18N

      jTextKlient.setColumns(20);
      jTextKlient.setRows(3);
      jTextKlient.setMinimumSize(new Dimension(0, 5));
      jTextKlient.setName("jTextKlient"); // NOI18N
      jTextKlient.setPreferredSize(new Dimension(120, 45));
      jScrollPane1.setViewportView(jTextKlient);

      jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
      jLabel3.setMaximumSize(new Dimension(2147483647, 30));
      jLabel3.setMinimumSize(new Dimension(15, 19));
      jLabel3.setName("jLabel3"); // NOI18N
      jLabel3.setPreferredSize(new Dimension(130, 19));

      jDateRechnungsdatum.setMaximumSize(new Dimension(2147483647, 30));
      jDateRechnungsdatum.setMinimumSize(new Dimension(15, 19));
      jDateRechnungsdatum.setName("rechnungsdatum"); // NOI18N
      jDateRechnungsdatum.setPreferredSize(new Dimension(130, 19));
      jDateRechnungsdatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

         @Override
         public void propertyChange(java.beans.PropertyChangeEvent evt) {
            jDateRechnungsdatumPropertyChange(evt);
         }
      });

      jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
      jLabel4.setMaximumSize(new Dimension(2147483647, 30));
      jLabel4.setMinimumSize(new Dimension(15, 19));
      jLabel4.setName("jLabel4"); // NOI18N
      jLabel4.setPreferredSize(new Dimension(130, 19));

      jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
      jLabel5.setMaximumSize(new Dimension(2147483647, 30));
      jLabel5.setMinimumSize(new Dimension(15, 19));
      jLabel5.setName("jLabel5"); // NOI18N
      jLabel5.setPreferredSize(new Dimension(130, 19));

      jTextRechnungsnummer.setText(resourceMap.getString("rechnungsnummer.text")); // NOI18N
      jTextRechnungsnummer.setEnabled(false);
      jTextRechnungsnummer.setMaximumSize(new Dimension(2147483647, 30));
      jTextRechnungsnummer.setMinimumSize(new Dimension(15, 19));
      jTextRechnungsnummer.setName("rechnungsnummer"); // NOI18N
      jTextRechnungsnummer.setPreferredSize(new Dimension(130, 19));
      jTextRechnungsnummer.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseClicked(MouseEvent evt) {
            jTextRechnungsnummerMouseClicked(evt);
         }
      });
      jTextRechnungsnummer.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jTextRechnungsnummerActionPerformed(evt);
         }
      });
      jTextRechnungsnummer.addFocusListener(new FocusAdapter() {

         @Override
         public void focusGained(FocusEvent evt) {
            jTextRechnungsnummerFocusGained(evt);
         }

         @Override
         public void focusLost(FocusEvent evt) {
            jTextRechnungsnummerFocusLost(evt);
         }
      });

      jDateZahlDatum.setMaximumSize(new Dimension(2147483647, 30));
      jDateZahlDatum.setMinimumSize(new Dimension(15, 19));
      jDateZahlDatum.setName("zahlungsdatum"); // NOI18N
      jDateZahlDatum.setPreferredSize(new Dimension(130, 19));

      jToggleButtonDetails.setIcon(getIcon(resourceMap.getString("jToggleDetails.icon"))); // NOI18N
      jToggleButtonDetails.setSelected(true);
      jToggleButtonDetails.setText(resourceMap.getString("jToggleDetails.text")); // NOI18N
      jToggleButtonDetails.setToolTipText(resourceMap.getString("jToggleDetails.toolTipText")); // NOI18N
      jToggleButtonDetails.setDisabledIcon(getIcon(resourceMap.getString("jToggleDetails.disabledIcon"))); // NOI18N
      jToggleButtonDetails.setName("jToggleDetails"); // NOI18N
      jToggleButtonDetails.setSelectedIcon(getIcon(resourceMap.getString("jToggleDetails.selectedIcon"))); // NOI18N
      jToggleButtonDetails.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jToggleButtonDetailsActionPerformed(evt);
         }
      });

      jCheckBoxZusatz1.setSelected(true);
      jCheckBoxZusatz1.setText(resourceMap.getString("jCheckBoxZusatz1.text")); // NOI18N
      jCheckBoxZusatz1.setName("jCheckBoxZusatz1"); // NOI18N
      jCheckBoxZusatz1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jCheckBoxZusatz1ActionPerformed(evt);
         }
      });

      jCheckBoxZusatz2.setSelected(true);
      jCheckBoxZusatz2.setText(resourceMap.getString("jCheckBoxZusatz2.text")); // NOI18N
      jCheckBoxZusatz2.setName("jCheckBoxZusatz2"); // NOI18N
      jCheckBoxZusatz2.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jCheckBoxZusatz2ActionPerformed(evt);
         }
      });

      jCheckBoxStundenzahl.setText(resourceMap.getString("jCheckBoxStundenzahl.text")); // NOI18N
      jCheckBoxStundenzahl.setName("jCheckBoxStundenzahl"); // NOI18N
      jCheckBoxStundenzahl.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jCheckBoxStundenzahlActionPerformed(evt);
         }
      });

      jCheckBoxUnterschrift.setText(resourceMap.getString("jCheckBoxUnterschrift.text")); // NOI18N
      jCheckBoxUnterschrift.setActionCommand(resourceMap.getString("jCheckBoxUnterschrift.actionCommand")); // NOI18N
      jCheckBoxUnterschrift.setName("jCheckBoxUnterschrift"); // NOI18N

      GroupLayout jPanelobenLayout = new GroupLayout(jPaneloben);
      jPaneloben.setLayout(jPanelobenLayout);
      jPanelobenLayout
            .setHorizontalGroup(
                  jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelobenLayout.createSequentialGroup().addGroup(jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                              .addGroup(jPanelobenLayout.createSequentialGroup()
                                    .addComponent(jCheckBoxZusatz1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jCheckBoxZusatz2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jCheckBoxStundenzahl)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jCheckBoxUnterschrift))
                              .addGroup(GroupLayout.Alignment.TRAILING, jPanelobenLayout.createSequentialGroup().addGroup(jPanelobenLayout
                                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelobenLayout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE))
                                    .addGroup(jPanelobenLayout.createSequentialGroup()
                                          .addComponent(jToggleButtonDetails, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE).addGap(241, 241, 241)))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                          .addGroup(jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jDateZahlDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel4, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                      GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jTextRechnungsnummer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                          .addComponent(jDateRechnungsdatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                              .addGroup(jPanelobenLayout.createSequentialGroup().addContainerGap().addComponent(jLabel2))).addContainerGap()));
      jPanelobenLayout.setVerticalGroup(jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanelobenLayout.createSequentialGroup().addContainerGap()
            .addComponent(jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanelobenLayout.createSequentialGroup()
                  .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jDateRechnungsdatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(12, 12, 12)
                  .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jDateZahlDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(jTextRechnungsnummer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap())
                  .addGroup(jPanelobenLayout.createSequentialGroup().addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelobenLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jCheckBoxZusatz1).addComponent(jCheckBoxZusatz2)
                              .addComponent(jCheckBoxStundenzahl).addComponent(jCheckBoxUnterschrift))
                        .addGap(18, 18, 18).addComponent(jToggleButtonDetails, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)))));

      jSplitPane1.setLeftComponent(jPaneloben);

      jButton1.setText(resourceMap.getString("jButtonAbbrechen.text")); // NOI18N
      jButton1.setName("jButtonAbbrechen"); // NOI18N
      jButton1.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      jButtonErstellen.setText(resourceMap.getString("jButtonErstellen.text")); // NOI18N
      jButtonErstellen.setName("jButtonErstellen"); // NOI18N
      jButtonErstellen.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent evt) {
            jButtonErstellenActionPerformed(evt);
         }
      });

      GroupLayout layout = new GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jSplitPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE).addGroup(GroupLayout.Alignment.LEADING,
                              layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel1).addGroup(GroupLayout.Alignment.TRAILING,
                                    layout.createSequentialGroup().addComponent(jButtonErstellen).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton1))))
                  .addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE).addGap(18, 18, 18)
                  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButton1).addComponent(jButtonErstellen)).addContainerGap()));

      pack();
   }

   private Icon getIcon(String fileName) {
      return new ImageIcon("icons/" + fileName);
   }

   private void jToggleButtonDetailsActionPerformed(ActionEvent evt) {
      toggleDetails();
   }

   private void jDateRechnungsdatumPropertyChange(java.beans.PropertyChangeEvent evt) {
      if (evt.getPropertyName().matches("date")) {
         Calendar datum;
         datum = new GregorianCalendar();
         com.toedter.calendar.JDateChooser tmp = (com.toedter.calendar.JDateChooser) evt.getSource();
         if (tmp.getName().matches("jDateRechnungsdatum")) {
            datum.setTime(this.jDateRechnungsdatum.getDate());
            this.rechnung.setDatum(datum);
            String generateRechnungsnr = generateRechnungsnr(datum);
            this.jTextRechnungsnummer.setText(generateRechnungsnr);

            if (!this.useCreateinsteadUpdate)
               JOptionPane.showMessageDialog(this, "Die Rechnungsnummer wurde auf den Standard-Wert zurückgesetzt!", "Achtung!", JOptionPane.WARNING_MESSAGE);
         }
         if (tmp.getName().matches("jDateZahlDatum")) {
            datum.setTime(this.jDateZahlDatum.getDate());
            this.rechnung.setZahldatum(datum);
         }
      }
   }

   private void jTextRechnungsnummerMouseClicked(MouseEvent evt) {

      if (evt.getClickCount() > 1) {
         jTextRechnungsnummer.setEnabled(true);
         jTextRechnungsnummer.requestFocusInWindow();
         jTextRechnungsnummer.setCaretPosition(jTextRechnungsnummer.getText().length());
      }
   }

   /**
    * Abbrechen gedrückt - Fenster wird ohne Aktion geschlossen.
    * 
    * @param evt
    */
   private void jButton1ActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
   }

   private void jCheckBoxZusatz1ActionPerformed(ActionEvent evt) {
      makeTable();
   }

   private void jCheckBoxZusatz2ActionPerformed(ActionEvent evt) {
      makeTable();
   }

   /**
    * OK-Button gedrückt - Rechnung wird erstellt
    * 
    * @param evt
    */
   private void jButtonErstellenActionPerformed(ActionEvent evt) {
      Options einstellungen = Einstellungen.getInstance().getEinstellungen();

      this.rechnung.setZusatz1(this.jCheckBoxZusatz1.isSelected());
      this.rechnung.setZusatz2(this.jCheckBoxZusatz2.isSelected());

      // Nur arbetisstunden übernehmen, die auch gewält sind.
      Vector<Arbeitsstunde> tmp_einheiten = new Vector<Arbeitsstunde>();

      for (int i = 0; i < einheiten.size(); i++) {

         Boolean tmp_bool = (Boolean) jTable1.getValueAt(i, 0);

         if (tmp_bool.booleanValue())
            tmp_einheiten.add(einheiten.elementAt(i));
      }

      this.rechnung.setEinheiten(tmp_einheiten);

      try {
         ShowJasperRechnung rechn = new ShowJasperRechnung();
         JRDataSource source = rechn.createSource(this.rechnung);
         JasperPrint repo = rechn.compileAndShowReport(source);

         if (JOptionPane.showConfirmDialog(null, "Soll diese Rechnung so gespeichert werden?", "Speichern?", JOptionPane.OK_CANCEL_OPTION,
               JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {

            OutputStream outStream = new FileOutputStream(this.rechnung.getPdfdatei());
            rechn.store(repo, outStream);

            speichern(einstellungen);

            File pdf_datei = new File(rechnung.getPdfdatei());
            String pdfProg = einstellungen.getPdfProg();
            if (pdfProg != null) {
               String befehl = "";
               befehl = pdfProg + " " + rechnung.getPdfdatei();

               if (pdf_datei.canRead()) {
                  logger.info("showPdf: " + befehl);
                  try {
                     // Runtime.getRuntime().exec("sh -c " + befehl);
                     Runtime.getRuntime().exec(befehl);
                  } catch (Exception e) {
                     logger.debug("showPdf Runtime error:", e);
                  }
               } else {
                  System.err.println("Pdfdatei existiert nicht: " + pdf_datei.getAbsolutePath());
               }
            } else {
               logger.debug("Kein pdf-Programm angegeben. Ausgabe nicht möglich.");
            }

            pchListeners.firePropertyChange(ERSTELLT, 0, rechnung.getRechnungen_id());
         }

      } catch (FileNotFoundException e) {
         JOptionPane.showMessageDialog(this, "Datei " + rechnung.getTexdatei() + "\nkonnte nicht gefunden werden!\nAbbruch!", "Datei nicht gefunden", JOptionPane.ERROR_MESSAGE);
      } catch (JRException e1) {
         logger.error("Error creating PDF", e1);
         JOptionPane.showMessageDialog(this, "Rechnung vom " + rechnung.getDatum() + "\nkonnte nicht erstellt werden!\nAbbruch!\n" + e1.getMessage(), "PDF nicht erstellt",
               JOptionPane.ERROR_MESSAGE);
      }

      this.setVisible(false);
      this.dispose();
   }

   Rechnung getRechnung() {
      return rechnung;
   }
   
   /**
    * Speichert die Rechnung und Details in der Datenbank
    * 
    * @param optionen
    */
   public int speichern(Options optionen) {

      String dateiname = "";

      if (klient == null) {
         klient = klientenPersister.getKlientById(rechnung.getKlienten_id());
      }
      if (klient != null) {
         dateiname = klient.getAuftraggeber() + "_" + rechnung.getRechnungnr();
         dateiname = dateiname.replace(" ", "_");
      } else {
         // sql nicht erfolgreich
         dateiname = rechnung.getRechnungnr();
         dateiname = dateiname.replace(" ", "_");
      }

      RechnungSystemExecutionService fileService = new RechnungSystemExecutionService(optionen);

      int ergebnis = fileService.movePdf(rechnung, dateiname);

      if (ergebnis == 0) {
         ergebnis = fileService.moveTex(rechnung, dateiname);
      }

      if (ergebnis == 0) {
         persister.insertOrUpdateRechnung(rechnung);
      }

      return ergebnis;
   }

   private void jTextRechnungsnummerActionPerformed(ActionEvent evt) {
      this.rechnung.setRechnungnr(jTextRechnungsnummer.getText());
   }

   private void jTextRechnungsnummerFocusLost(FocusEvent evt) {
      this.prevComponent.requestFocusInWindow();
      this.rechnung.setRechnungnr(jTextRechnungsnummer.getText());
      jTextRechnungsnummer.setEnabled(false);
      prevComponent = null;
   }

   private void jTextRechnungsnummerFocusGained(FocusEvent evt) {
      this.prevComponent = evt.getOppositeComponent();
   }

   private void jToggleButtonZusammenfassungenActionPerformed(ActionEvent evt) {
      // Zusammenfassungen geändert!
      this.zusammenfassungen_erlauben = jToggleButtonZusammenfassungen.isSelected();
      if (this.zusammenfassungen_erlauben) {
         this.jLabel7.setText("erlauben!");
      } else {
         this.jLabel7.setText("nicht erlauben!");
      }
   }

   private void jCheckBoxStundenzahlActionPerformed(ActionEvent evt) {
      this.stunden_vorhanden = this.jCheckBoxStundenzahl.isSelected();
      makeTable();
   }

   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      pchListeners.addPropertyChangeListener(listener);
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      pchListeners.removePropertyChangeListener(listener);
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private JButton jButton1;
   private JButton jButton3;
   private JButton jButtonErstellen;
   private JCheckBox jCheckBoxStundenzahl;
   private JCheckBox jCheckBoxUnterschrift;
   private JCheckBox jCheckBoxVorschau;
   private JCheckBox jCheckBoxZusatz1;
   private JCheckBox jCheckBoxZusatz2;
   private com.toedter.calendar.JDateChooser jDateRechnungsdatum;
   private com.toedter.calendar.JDateChooser jDateZahlDatum;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JPanel jPanel1;
   private JPanel jPaneloben;
   private JPanel jPanelunten;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JSplitPane jSplitPane1;
   private JTabbedPane jTabbedPane1;
   private JTable jTable1;
   private JTextArea jTextKlient;
   private JTextField jTextRechnungsnummer;
   private JTextField jTextTexDatei;
   private JToggleButton jToggleButtonDetails;
   private JToggleButton jToggleButtonZusammenfassungen;
   // End of variables declaration//GEN-END:variables

}
