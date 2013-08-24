/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StartFenster.java
 *
 * Created on 27.04.2009, 10:10:09
 */

package arbeitsrechnungen;

/**
 *
 * @author markus
 */

import java.sql.ResultSet;
import arbeitsabrechnungendataclass.Verbindung;
import java.util.Vector;
//import javax.swing.event.TableModelListener;
//import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import mouseoverhintmanager.MouseOverHintManager;


public class StartFenster extends javax.swing.JFrame implements PropertyChangeListener {

    Vector<Integer> Forderungen_ids = new Vector<Integer>();
    Vector<Integer> Einheiten_ids = new Vector<Integer>();
    java.util.Properties optionen = new java.util.Properties();
    public static final int FORDERUNGEN = 1;
    public static final int EINHEITEN = 2;
    MouseOverHintManager hintman;

    /** Creates new form StartFenster */
    public StartFenster() {
        // Testen ob das arbeitsverzeichnis im home-verzeichnis existiert
        java.io.File homeverzeichnis;
        java.util.Properties sysprops = System.getProperties();
        String homedir = sysprops.getProperty("user.home");
        homeverzeichnis = new java.io.File(homedir + sysprops.getProperty("file.separator") + ".arbeitrechnungen");

        if(!homeverzeichnis.exists()){
            // Verzeichnis anlegen
            System.out.println(homeverzeichnis.getAbsolutePath() + " existiert nicht!");
            System.out.println("Wird angelegt...");
            homeverzeichnis.mkdirs();
        }
        // Existiert die optionsdatei?
        java.io.File optionfile  = new java.io.File(homedir + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");
        try{
            // Die Funktion legt die datei nur dann an, wenn sie nicht existiert.
            // Dann wird das Options-Fenster geöffnet
            if(optionfile.createNewFile()) {
                    System.out.println("Options-Datei erfolgreich angelegt!");
                    Optionen optionwindow = new Optionen(this, true);
                    optionwindow.setVisible(true);
            }
        }catch(Exception e){
            System.err.println("Options-Datei konnte nicht angelegt werden.");
        }

        try{
            optionen.load(new java.io.FileInputStream(optionfile));
        }catch(Exception e){
            System.err.println("Startfenster.java: Options-Datei konnte nicht geladen werden.");
        }
        if(optionen.getProperty("sqlserver")== null){
            // Property sqlserver nicht gefunden: optionen nicht gespeichert!
            Optionen optionwindow = new Optionen(this, true);
            optionwindow.setVisible(true);
        }

        initComponents();
        initForderungen();
        initEinheiten();
        hintman = new MouseOverHintManager(this.Status_links);
        hintman.addHintFor(this.jButtonKlientenEditor, "Startet den Klienteneditor, einen umfassenden Werkzeug für die meisten Programmfunktionen");
        hintman.addHintFor(this.jTableEinheiten, "Geleistete Arbeitsstunden, noch nicht abgerechnet");
        hintman.addHintFor(this.jTableForderungen, "Abgerechnete Arbeitsstunden, noch nicht bezahlt");
        hintman.addHintFor(this.jButtonBeenden, "Beendet das Programm und schließt alle zugehörigen Fenster");
        hintman.addHintFor(this.jButtonArtenEinheiten, "TODO: Einheitenarten");
        this.jButtonArtenEinheiten.setEnabled(false);
        hintman.addHintFor(this.jButton1, "Einfacher Editor für Arbeitsstunden");
        hintman.addHintFor(this.jMenuItemBeenden, "Beendet das Programm und schließt alle zugehörigen Fenster");
        hintman.enableHints(this);
    }

    /**
     * Tabelle mit Forderungen wird mit Daten aus der Datenbank gefüllt
     */
    private void initForderungen(){
        //Spaltenbreiten merken
        int spaltenzahl = this.jTableForderungen.getColumnModel().getColumnCount();
        //int[] selected = this.jTable1.getSelectedRows();
        int[] breitenMax = new int[spaltenzahl];
        int[] breitenOpt = new int[spaltenzahl];
        int[] breitenMin = new int[spaltenzahl];

        for (int i = 0; i < this.jTableForderungen.getColumnModel().getColumnCount(); i++) {
            breitenMax[i] = this.jTableForderungen.getColumnModel().getColumn(i).getMaxWidth();
            breitenOpt[i] = this.jTableForderungen.getColumnModel().getColumn(i).getPreferredWidth();
            breitenMin[i] = this.jTableForderungen.getColumnModel().getColumn(i).getMinWidth();
        }
        // Model mit Überschriften erstellen
        javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Firma", "Rechnungsdatum", "Forderung"
                }
        ){
            @Override
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        // Daten laden
        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, einheiten.Rechnung_Datum AS datum, SUM(einheiten.Preis) AS summe " +
                "FROM einheiten, klienten " +
                "WHERE einheiten.klienten_id = klienten.klienten_id " +
                "AND NOT (ISNULL( einheiten.Rechnung_verschickt )) " +
                "AND ISNULL( einheiten.Bezahlt ) " +
                "GROUP BY einheiten.Rechnung_Datum,einheiten.klienten_id " +
                "ORDER BY einheiten.Rechnung_Datum;";
        
        System.out.println(sqltext);
        ResultSet daten = null;
        if(verbindung.connected()) {
            System.out.println("Connected!");
            daten = verbindung.query(sqltext);
        }

        // Ergebnise einzeln zum Model hinzufügen
        double summe=0;
        try {
            while (daten.next()) {
                java.util.Vector<String> datensatz;
                datensatz = new Vector<String>();
                datensatz.add(daten.getString("auftraggeber"));
                datensatz.add(java.text.DateFormat.getDateInstance().format(daten.getDate("datum")));
                java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
                datensatz.add(df.format(daten.getDouble("summe")) + " €");
                summe = summe + daten.getDouble("summe");
                mymodel.addRow(datensatz);
                this.Forderungen_ids.add(daten.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // In der letzten Zeile die Summe ausgeben
        java.util.Vector<String> datensatz;
        datensatz = new Vector<String>();
        datensatz.add("Summe");
        datensatz.add("");
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        datensatz.add(df.format(summe) + " €");
        mymodel.addRow(datensatz);

        // eigenes Modell einsetzten und Vorgegebene Breiten speichern
        jTableForderungen.setModel(mymodel);
        for (int i= 0; i< breitenMax.length; i++){
            jTableForderungen.getColumnModel().getColumn(i).setMaxWidth(breitenMax[i]);
            jTableForderungen.getColumnModel().getColumn(i).setPreferredWidth(breitenOpt[i]);
            jTableForderungen.getColumnModel().getColumn(i).setMinWidth(breitenMin[i]);
        }
        jTableForderungen.setDefaultRenderer(String.class, new StartFensterTableCellRenderer());
    }

    /**
     * Tabelle mit gearbeiteten Einheiten wird mit Daten aus der Datenbank gefüllt
     */
    private void initEinheiten(){
        //Spaltenbreiten merken
        int spaltenzahl = this.jTableEinheiten.getColumnModel().getColumnCount();
        //int[] selected = this.jTable1.getSelectedRows();
        int[] breitenMax = new int[spaltenzahl];
        int[] breitenOpt = new int[spaltenzahl];
        int[] breitenMin = new int[spaltenzahl];

        for (int i = 0; i < this.jTableEinheiten.getColumnModel().getColumnCount(); i++) {
            breitenMax[i] = this.jTableEinheiten.getColumnModel().getColumn(i).getMaxWidth();
            breitenOpt[i] = this.jTableEinheiten.getColumnModel().getColumn(i).getPreferredWidth();
            breitenMin[i] = this.jTableEinheiten.getColumnModel().getColumn(i).getMinWidth();
        }
        // Model mit Überschriften erstellen
        javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Firma", "Einsätze", "Summe"
                }
        ){
            @Override
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        // Daten laden
        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        String sqltext = "SELECT klienten.klienten_id AS id, klienten.Auftraggeber AS auftraggeber, COUNT(einheiten.Preis) AS anzahl, SUM(einheiten.Preis) AS klientpreis " +
                "FROM einheiten, klienten " +
                "WHERE einheiten.klienten_id = klienten.klienten_id " +
                "AND ISNULL( einheiten.Rechnung_verschickt ) " +
                "AND ISNULL( einheiten.Bezahlt ) " +
                "GROUP BY einheiten.klienten_id " +
                "ORDER BY klientpreis;";
        
        System.out.println(sqltext);
        ResultSet daten = null;
        if(verbindung.connected()) {
            System.out.println("Connected!");
            daten = verbindung.query(sqltext);
        }

        // Ergebnise einzeln zum Model hinzufügen
        double summe=0;
        try {
            while (daten.next()) {
                java.util.Vector<String> datensatz;
                datensatz = new Vector<String>();
                datensatz.add(daten.getString("auftraggeber"));
                datensatz.add(String.valueOf(daten.getInt("anzahl")));
                java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
                datensatz.add(df.format(daten.getDouble("klientpreis")) + " €");
                summe = summe + daten.getDouble("klientpreis"); // summe ist gesamtsumme, in daten nur die des einzelnen Klienten
                mymodel.addRow(datensatz);
                this.Einheiten_ids.add(daten.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // In der letzten Zeile die Summe ausgeben
        java.util.Vector<String> datensatz;
        datensatz = new Vector<String>();
        datensatz.add("Summe");
        datensatz.add("");
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        datensatz.add(df.format(summe) + " €");
        mymodel.addRow(datensatz);

        // eigenes Modell einsetzten und Vorgegebene Breiten speichern
        jTableEinheiten.setModel(mymodel);
        for (int i= 0; i< breitenMax.length; i++){
            jTableEinheiten.getColumnModel().getColumn(i).setMaxWidth(breitenMax[i]);
            jTableEinheiten.getColumnModel().getColumn(i).setPreferredWidth(breitenOpt[i]);
            jTableEinheiten.getColumnModel().getColumn(i).setMinWidth(breitenMin[i]);
        }
        jTableEinheiten.setDefaultRenderer(String.class, new StartFensterTableCellRenderer());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(arbeitsrechnungen.ArbeitsrechnungenApp.class).getContext().getResourceMap(StartFenster.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jButtonKlientenEditor.setText(resourceMap.getString("jButtonKlientenEditor.text")); // NOI18N
        jButtonKlientenEditor.setMaximumSize(new java.awt.Dimension(500, 25));
        jButtonKlientenEditor.setMinimumSize(new java.awt.Dimension(120, 25));
        jButtonKlientenEditor.setName("jButtonKlientenEditor"); // NOI18N
        jButtonKlientenEditor.setPreferredSize(new java.awt.Dimension(151, 25));
        jButtonKlientenEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKlientenEditorActionPerformed(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(arbeitsrechnungen.ArbeitsrechnungenApp.class).getContext().getActionMap(StartFenster.class, this);
        jButtonBeenden.setAction(actionMap.get("quit")); // NOI18N
        jButtonBeenden.setText(resourceMap.getString("jButtonBeenden.text")); // NOI18N
        jButtonBeenden.setMaximumSize(new java.awt.Dimension(500, 25));
        jButtonBeenden.setMinimumSize(new java.awt.Dimension(120, 25));
        jButtonBeenden.setName("jButtonBeenden"); // NOI18N
        jButtonBeenden.setPreferredSize(new java.awt.Dimension(151, 25));

        jButtonArtenEinheiten.setText(resourceMap.getString("jButtonArtenEinheiten.text")); // NOI18N
        jButtonArtenEinheiten.setMaximumSize(new java.awt.Dimension(500, 25));
        jButtonArtenEinheiten.setMinimumSize(new java.awt.Dimension(120, 25));
        jButtonArtenEinheiten.setName("jButtonArtenEinheiten"); // NOI18N
        jButtonArtenEinheiten.setPreferredSize(new java.awt.Dimension(151, 25));
        jButtonArtenEinheiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonArtenEinheitenActionPerformed(evt);
            }
        });

        jButton1.setText(resourceMap.getString("jButtonEinheiten.text")); // NOI18N
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMaximumSize(new java.awt.Dimension(500, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(120, 25));
        jButton1.setName("jButtonEinheiten"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setName("jPanel1"); // NOI18N

        Status_links.setFont(resourceMap.getFont("Status_links.font")); // NOI18N
        Status_links.setText(resourceMap.getString("Status_links.text")); // NOI18N
        Status_links.setName("Status_links"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(Status_links)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 595, Short.MAX_VALUE)
                .addComponent(jLabel3))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Status_links, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setDividerLocation(297);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane1PropertyChange(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTableForderungen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Firma", "Rechnungsdatum", "Forderung"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTableForderungen.setName("jTableForderungen"); // NOI18N
        jTableForderungen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
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
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTableEinheiten.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Firma", "Stundenanzahl", "Summe"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTableEinheiten.setName("jTableEinheiten"); // NOI18N
        jTableEinheiten.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
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
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel3);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenuDatei.setText(resourceMap.getString("jMenuDatei.text")); // NOI18N
        jMenuDatei.setName("jMenuDatei"); // NOI18N

        jMenuItemBeenden.setAction(actionMap.get("quit")); // NOI18N
        jMenuItemBeenden.setText(resourceMap.getString("jMenuItemBeenden.text")); // NOI18N
        jMenuItemBeenden.setName("jMenuItemBeenden"); // NOI18N
        jMenuDatei.add(jMenuItemBeenden);

        jMenuBar1.add(jMenuDatei);

        jMenuBearbeiten.setText(resourceMap.getString("jMenuBearbeiten.text")); // NOI18N
        jMenuBearbeiten.setName("jMenuBearbeiten"); // NOI18N

        jMenuItemOption.setText(resourceMap.getString("jMenuItemOption.text")); // NOI18N
        jMenuItemOption.setName("jMenuItemOption"); // NOI18N
        jMenuItemOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButtonKlientenEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonArtenEinheiten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addComponent(jButtonKlientenEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonArtenEinheiten, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addGap(24, 24, 24)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonKlientenEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKlientenEditorActionPerformed
        openKlientenEditor(null, null);
}//GEN-LAST:event_jButtonKlientenEditorActionPerformed

    private void openKlientenEditor(Integer KlientenID, Integer Tabelle){
        KlientenEditor klienteneditor;
        klienteneditor = new KlientenEditor();
        klienteneditor.arbeitsstundenTabelle1.addPropertyChangeListener("ArbeitsstundenTabelle.Tabellendaten",this);
        klienteneditor.setSelbststaendig(false);
        if(KlientenID != null){
            klienteneditor.findRow(KlientenID);
            klienteneditor.updateComponents();
            if(Tabelle==FORDERUNGEN)
                klienteneditor.setFilter(KlientenEditor.EINGEREICHTE);
            if(Tabelle==EINHEITEN)
                klienteneditor.setFilter(KlientenEditor.NICHTEINGEREICHTE);
        }
        klienteneditor.setVisible(true);
    }

    /**
     * Daten in einem registrierten Fenster wurden geändert und hier wird aktualisiert!
     * Bisher nur ArbeitsstundenTabelle.Tabellendaten
     * 
     * @param evt
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt){
        if(evt.getPropertyName().equals("ArbeitsstundenTabelle.Tabellendaten")){
            initForderungen();
            initEinheiten();
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Arbeitsstunden arbeitsstunden = new Arbeitsstunden();
        arbeitsstunden.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTableForderungenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableForderungenMouseClicked
        //System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
        if(this.jTableForderungen.getSelectedRowCount()==0) jTableForderungenSetSelection(evt);
        if (evt.getClickCount()>1 && !evt.isPopupTrigger()){
            // Doppelklick mit links -> Klienteneditor
            // SelectedRow wird in ID-Liste gesucht und gefundene ID an Klienteneditor übergeben.
            openKlientenEditor(this.Forderungen_ids.elementAt(this.jTableForderungen.getSelectedRow()), FORDERUNGEN);
        }
        // Irgendwann ein Menü?
/*        if (evt.isPopupTrigger()) {
            this.jPopupMenu1.show(jTable1, evt.getX(), evt.getY());
            System.out.println("Rechtsklick!");
        }*/
    }//GEN-LAST:event_jTableForderungenMouseClicked

    private void jTableEinheitenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEinheitenMouseClicked
        // System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
        if(this.jTableForderungen.getSelectedRowCount()==0) jTableEinheitenSetSelection(evt);
        if (evt.getClickCount()>1 && !evt.isPopupTrigger()){
            // Doppelklick mit links -> Klienteneditor
            // SelectedRow wird in ID-Liste gesucht und gefundene ID an Klienteneditor übergeben.
            openKlientenEditor(this.Einheiten_ids.elementAt(this.jTableEinheiten.getSelectedRow()), EINHEITEN);
        }
        // Irgendwann ein Menü?
/*        if (evt.isPopupTrigger()) {
            this.jPopupMenu1.show(jTable1, evt.getX(), evt.getY());
            System.out.println("Rechtsklick!");
        }*/

    }//GEN-LAST:event_jTableEinheitenMouseClicked

    private void jMenuItemOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOptionActionPerformed
        // Öffne Options-Fenster
        Optionen optionwindow = new Optionen(this, false);
        optionwindow.setVisible(true);
    }//GEN-LAST:event_jMenuItemOptionActionPerformed

    private void jButtonArtenEinheitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonArtenEinheitenActionPerformed
        //TODO Einheitenarten Fenster erstellen und in Datenbank anlegen zur Abrechnung
}//GEN-LAST:event_jButtonArtenEinheitenActionPerformed

    private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane1PropertyChange
        // TODO add your handling code here:
        this.jLabel3.setText("Divider: " + this.jSplitPane1.getDividerLocation());
    }//GEN-LAST:event_jSplitPane1PropertyChange

    private void jTableEinheitenSetSelection(java.awt.event.MouseEvent evt){
//        System.out.println("Zeile wird selectiert.");
        java.awt.Point p = evt.getPoint();
        int zeile = this.jTableEinheiten.rowAtPoint(p);
        this.jTableEinheiten.getSelectionModel().setSelectionInterval(zeile, zeile);
    }

    private void jTableForderungenSetSelection(java.awt.event.MouseEvent evt){
//        System.out.println("Zeile wird selectiert.");
        java.awt.Point p = evt.getPoint();
        int zeile = this.jTableForderungen.rowAtPoint(p);
        this.jTableForderungen.getSelectionModel().setSelectionInterval(zeile, zeile);
    }


    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartFenster().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Status_links;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonArtenEinheiten;
    private javax.swing.JButton jButtonBeenden;
    private javax.swing.JButton jButtonKlientenEditor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuBearbeiten;
    private javax.swing.JMenu jMenuDatei;
    private javax.swing.JMenu jMenuHilfe;
    private javax.swing.JMenuItem jMenuItemBeenden;
    private javax.swing.JMenuItem jMenuItemOption;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableEinheiten;
    private javax.swing.JTable jTableForderungen;
    // End of variables declaration//GEN-END:variables

}
