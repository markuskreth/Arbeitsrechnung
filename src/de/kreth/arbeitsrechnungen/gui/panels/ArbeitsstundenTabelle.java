
/*
 * ArbeitsstundenTabelle.java
 *
 * Created on 14.05.2009, 23:13:56
 */
package de.kreth.arbeitsrechnungen.gui.panels;

/**
 *
 * @author markus
 */
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.mySqlDate;
import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.ArbeitsstundeImpl;
import de.kreth.arbeitsrechnungen.gui.dialogs.Kalenderauswahl;
import de.kreth.arbeitsrechnungen.gui.dialogs.RechnungDialog;
import de.kreth.arbeitsrechnungen.gui.jframes.Einheit_einzel;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_mysql;

public class ArbeitsstundenTabelle extends javax.swing.JPanel implements WindowListener{

	private static final long serialVersionUID = 8161115991876323549L;
	private Vector<Arbeitsstunde> Arbeitsstunden;
    java.util.Properties optionen;
    private Window parent = null;
    private int anzahl = 0;
    private Double summe = 0.00;
    private Double stundenzahl = null;
    private int klient;
    private boolean zusatz1=false;
    private boolean zusatz2=false;
    private String zusatz1_name = "";
    private String zusatz2_name = "";
    private javax.swing.table.TableColumn spalte5;
    private javax.swing.table.TableColumn spalte6;
    private Integer[] geloeschte_spalten = new Integer[2];

    private String filter = "(ISNULL(Bezahlt) OR ISNULL(Rechnung_verschickt))";
	private Verbindung verbindung;
    public static final String TEXUMBRUCH = "\\\\\\\\";
    public static final String TEXLINE = "\\\\hline";
    public static final String TEXEURO = "\\\\officialeuro";
    public static final int EINGEREICHTE=1;
    public static final int NICHTEINGEREICHTE=2;
    public static final int OFFENE=3;


    public void setParent(javax.swing.JFrame parent){
        this.parent = parent;
    }

    public ArbeitsstundenTabelle(javax.swing.JFrame parent) {
        // Füllt die Tabelle mit den Feuerwehr-Arbeitsstunden
        this(parent, 1);
        System.out.println("ArbeitsstundenTabelle(javax.swing.JFrame parent)");
    }

    public ArbeitsstundenTabelle() {
        // Füllt die Tabelle mit den Feuerwehr-Arbeitsstunden
        this(null,1);
    }

    /** Creates new form ArbeitsstundenTabelle */
    public ArbeitsstundenTabelle(Window parent, int klienten_id) {
        // Optionen mit Datenbankeinstellungen laden
        optionen = new Einstellungen().getEinstellungen();

        verbindung = new Verbindung_mysql(optionen);

        geloeschte_spalten[0] = null;
        geloeschte_spalten[1] = null;

        if(optionen == null){
            //Abbruch, wenn keine Optionen geladen werden konnten!
            parent.setVisible(false);
            parent.dispose();
        }else{
            initComponents();
            this.jTextFieldStundenzahl.setVisible(false);
            this.jLabel6.setVisible(false);
            this.parent = parent;
            setFilter();
            update(klienten_id);
        }
    }

    /**
     * Füllt anhand der Klienten_id und des gesetzten Filters die Tabelle mit Daten.
     * @param klienten_id
     */
    private void readList(int klienten_id) {
        // TODO Einheitentypen unterscheiden (Accord, Stunden, Einheiten) und danach Tabelle, Einheit_einzel und Rechnung anpassen
        this.klient = klienten_id;
		anzahl = 0;
		summe = 0.00;
		stundenzahl = null;

        String sqltext = "SELECT DISTINCT einheiten.einheiten_id, einheiten.klienten_id, " +
                "einheiten.angebote_id, Datum, Beginn, Ende, einheiten.zusatz1, einheiten.zusatz2, Preisänderung, Rechnung_verschickt, " +
                "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.preis_pro_stunde, " +
                "klienten.Zusatz1 AS bool1, klienten.Zusatz2 AS bool2, klienten.Zusatz1_Name, klienten.Zusatz2_Name FROM einheiten, " +
                "angebote, klienten WHERE einheiten.klienten_id=" + klienten_id +
                " AND einheiten.angebote_id=angebote.angebote_id" +
                " AND einheiten.klienten_id=klienten.klienten_id" +
                " AND " + filter +
                " ORDER BY Datum, Preis;";
        System.out.println("readList: " + sqltext);

        Arbeitsstunden = new Vector<de.kreth.arbeitsrechnungen.data.Arbeitsstunde>();
        try {
        	ResultSet daten = verbindung.query(sqltext);
            while (daten.next()) {
                if( (daten.getBoolean("preis_pro_stunde")) && (stundenzahl==null) ){
                    stundenzahl = 0.0;
                }
                this.zusatz1 = daten.getBoolean("bool1");
                if (this.zusatz1) this.zusatz1_name = daten.getString("Zusatz1_Name");
                this.zusatz2 = daten.getBoolean("bool2");
                if (this.zusatz2) this.zusatz2_name = daten.getString("Zusatz2_Name");

                int id = daten.getInt("einheiten_id");
                int angebote_id = daten.getInt("angebote_id");

                anzahl = anzahl + 1;
                summe = summe + daten.getDouble("Preis");
                if(daten.getBoolean("preis_pro_stunde")){
                    stundenzahl += daten.getDouble("Dauer");
                }

                ArbeitsstundeImpl stunde = new ArbeitsstundeImpl(id, klienten_id, angebote_id);
                stunde.setDatum(daten.getDate("Datum"));
                stunde.setInhalt(daten.getString("Inhalt"));
                stunde.setBeginn(daten.getTimestamp("Beginn"));
                stunde.setEnde(daten.getTimestamp("Ende"));
                stunde.setPreis(daten.getDouble("Preis"));
                stunde.setZusatz1(daten.getString("zusatz1"));
                stunde.setZusatz2(daten.getString("zusatz2"));
                stunde.setPreisAenderung(daten.getDouble("Preisänderung"));
                stunde.setDauer(daten.getDouble("Dauer"));
                try{
                    stunde.setVerschicktDatum(daten.getDate("Rechnung_Datum"));
                }catch (Exception e) {
                    System.err.println(daten.getInt("einheiten.einheiten_id") + ": Rechnung Datum nicht gesetzt!");
//                    e.printStackTrace();
                    stunde.setVerschicktDatum(null);
                }
                try{
                    stunde.setBezahltDatum(daten.getDate("Bezahlt_Datum"));
                }catch (Exception e) {
                    System.err.println(daten.getInt("einheiten.einheiten_id") + ": Bezahlt Datum nicht gesetzt!");
//                    e.printStackTrace();
                    stunde.setBezahltDatum(null);
                }
                try{
                    this.Arbeitsstunden.addElement(stunde);
                }catch (Exception e) {
                    System.err.println("ArbeitsstundenTabelle::readList: AddElement misslungen!");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(stundenzahl != null) stundenzahl = stundenzahl / 60; // Minuten in Stunden umrechnen
    }

    public void update(int klienten_id){
        this.readList(klienten_id);
        this.updateTable();

        DecimalFormat df = new DecimalFormat("0.00");
        this.jTextFieldAnzahl.setText(String.valueOf(this.anzahl));
        this.jTextFieldSumme.setText(df.format(summe) + " €");
        
        if(stundenzahl != null) {
            df = new DecimalFormat("0.0");
            this.jTextFieldStundenzahl.setVisible(true);
            this.jLabel6.setVisible(true);
            System.out.println("Stundenanzahl gesetzt");
            this.jTextFieldStundenzahl.setText(df.format(stundenzahl));
        }else{
            this.jTextFieldStundenzahl.setText("");
            this.jTextFieldStundenzahl.setVisible(false);
            this.jLabel6.setVisible(false);
        }
    }

    private void updateTable() {
        //int[] selected = this.jTable1.getSelectedRows();
        int[] breitenMax = new int[10];
        int[] breite = new int[10];
        int[] breitenOpt = new int[10];
        int[] breitenMin = new int[10];

        int j = 0;      // Wenn Spalten eingefügt werden müssen, dann werden die Einschübe addiert.
        for (int i = 0; i < this.jTable1.getColumnModel().getColumnCount(); i++) {
            breite[i+j]     = this.jTable1.getColumnModel().getColumn(i).getWidth();
            breitenMax[i+j] = this.jTable1.getColumnModel().getColumn(i).getMaxWidth();
            breitenOpt[i+j] = this.jTable1.getColumnModel().getColumn(i).getPreferredWidth();
            breitenMin[i+j] = this.jTable1.getColumnModel().getColumn(i).getMinWidth();
            if(i == 4){
                if(this.geloeschte_spalten[0] != null){
                    j += 1;
                    breite[i+j]     = this.spalte5.getWidth();
                    breitenMax[i+j] = this.spalte5.getMaxWidth();
                    breitenOpt[i+j] = this.spalte5.getPreferredWidth();
                    breitenMin[i+j] = this.spalte5.getMinWidth();
                }
                if(this.geloeschte_spalten[1] != null){
                    j += 1;
                    breite[i+j]     = this.spalte6.getWidth();
                    breitenMax[i+j] = this.spalte6.getMaxWidth();
                    breitenOpt[i+j] = this.spalte6.getPreferredWidth();
                    breitenMin[i+j] = this.spalte6.getMinWidth();
                }
            }
        }
        // Model mit Überschriften erstellen
        javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Datum", "Inhalt", "Start", "Ende", "Preis", this.zusatz1_name, this.zusatz2_name, "Preisänderung", "Eingereicht", "Bezahlt"
                }
        ){
            /**
			 * 
			 */
			private static final long serialVersionUID = 1913170267962749520L;

			@Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        // Vektor aus Daten erstellen und einzeln zum Model hinzufügen
        DecimalFormat df = new DecimalFormat("0.00");

        for (int i=0; i<Arbeitsstunden.size();i++){
            try{
            	Arbeitsstunde elementAt = this.Arbeitsstunden.elementAt(i);
                Vector<Object> daten = elementAt.toVector();
                daten.removeElementAt(0);
                daten.removeElementAt(0);
                daten.removeElementAt(0);
                String preis = df.format(daten.elementAt(4)) + " €";
                daten.removeElementAt(4);
                daten.add(4, preis);
                mymodel.addRow(daten);            
            } catch (Exception e) {
                System.err.println("ArbeitsstundenTabelle::updateTable: Vektorfunktion fehlgeschlagen");
                e.printStackTrace();
            }
        }

        jTable1.setModel(mymodel);
        for (int i= 0; i< breitenMax.length; i++){
            jTable1.getColumnModel().getColumn(i).setWidth(breite[i]);
            jTable1.getColumnModel().getColumn(i).setMaxWidth(breitenMax[i]);
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(breitenOpt[i]);
            jTable1.getColumnModel().getColumn(i).setMinWidth(breitenMin[i]);
        }
        if (!this.zusatz2) {
            this.geloeschte_spalten[1] = 6;
            this.spalte6 = jTable1.getColumnModel().getColumn(6);
            jTable1.removeColumn(jTable1.getColumnModel().getColumn(6));
/*            jTable1.getColumnModel().getColumn(6).setWidth(0);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0); */
        }else{
            this.geloeschte_spalten[1] = null;
        }
        if (!this.zusatz1) {
            this.geloeschte_spalten[0] = 5;
            this.spalte5 = jTable1.getColumnModel().getColumn(5);
            jTable1.removeColumn(jTable1.getColumnModel().getColumn(5));
/*            jTable1.getColumnModel().getColumn(5).setWidth(0);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(5).setMinWidth(0); */
        }else{
            this.geloeschte_spalten[0] = null;
        }
/*        this.jLabel7.setText(
                "Breite 5: " + jTable1.getColumnModel().getColumn(5).getWidth() +
                ", Max: " + jTable1.getColumnModel().getColumn(5).getMaxWidth() +
                "  ;  Breite 6: " + jTable1.getColumnModel().getColumn(6).getWidth() +
                ", Max: " + jTable1.getColumnModel().getColumn(6).getMaxWidth()
                ); */
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
    	
        buttonGroupFilter = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItembearbeiten = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemEditAll = new javax.swing.JMenuItem();
        jMenuItemRechnungDatum = new javax.swing.JMenuItem();
        jMenuItemBezahltDatum = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonDelete = new javax.swing.JButton();
        jButtonBearbeiten = new javax.swing.JButton();
        jButtonNeuerDatensatz = new javax.swing.JButton();
        jRadioButtonAbgeschlossene = new javax.swing.JRadioButton();
        jRadioButtonNichtBezahlte = new javax.swing.JRadioButton();
        jRadioButtonNichtEingereichte = new javax.swing.JRadioButton();
        jRadioButtonAlle = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonRechnung = new javax.swing.JButton();
        jRadioButtonOffene = new javax.swing.JRadioButton();
        jTextFieldSumme = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldAnzahl = new javax.swing.JTextField();
        jDateChooserVonDatum = new com.toedter.calendar.JDateChooser();
        jDateChooserBisDatum = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldStundenzahl = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        jPopupMenu1.setInvoker(jTable1);
        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
        
        jMenuItembearbeiten.setText(resourceMap.getString("jMenuItembearbeiten.text")); // NOI18N
        jMenuItembearbeiten.setFocusPainted(true);
        jMenuItembearbeiten.setName("jMenuItembearbeiten"); // NOI18N
        jMenuItembearbeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItembearbeitenActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItembearbeiten);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jPopupMenu1.add(jSeparator1);

        jMenuItemEditAll.setText(resourceMap.getString("jMenuItemEditAll.text")); // NOI18N
        jMenuItemEditAll.setToolTipText(resourceMap.getString("jMenuItemEditAll.toolTipText")); // NOI18N
        jMenuItemEditAll.setName("jMenuItemEditAll"); // NOI18N
        jPopupMenu1.add(jMenuItemEditAll);

        jMenuItemRechnungDatum.setText(resourceMap.getString("jMenuItemRechnungDatum.text")); // NOI18N
        jMenuItemRechnungDatum.setName("jMenuItemRechnungDatum"); // NOI18N
        jMenuItemRechnungDatum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRechnungDatumActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemRechnungDatum);

        jMenuItemBezahltDatum.setText(resourceMap.getString("jMenuItemBezahltDatum.text")); // NOI18N
        jMenuItemBezahltDatum.setName("jMenuItemBezahltDatum"); // NOI18N
        jMenuItemBezahltDatum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBezahltDatumActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItemBezahltDatum);

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setActionCommand(resourceMap.getString("jMenuItem1.actionCommand")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"13.12.09", null, "13:50", "14:50", "25,50", null, null, "", "25.12.09", "30.12.09"}
            },
            new String [] {
                "Datum", "Inhalt", "Start", "Ende", "Preis", "Zusatz1", "Zusatz2", "Preisänderung", "Eingereicht", "Bezahlt"
            }
        ) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 516135999448412054L;
			Class<?>[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class<?> getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setEditingColumn(0);
        jTable1.setEditingRow(0);
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setMinWidth(80);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(120);
        jTable1.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable1.columnModel.title0")); // NOI18N
        jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title8")); // NOI18N
        jTable1.getColumnModel().getColumn(2).setMinWidth(30);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(55);
        jTable1.getColumnModel().getColumn(2).setMaxWidth(75);
        jTable1.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1")); // NOI18N
        jTable1.getColumnModel().getColumn(3).setMinWidth(30);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(55);
        jTable1.getColumnModel().getColumn(3).setMaxWidth(75);
        jTable1.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2")); // NOI18N
        jTable1.getColumnModel().getColumn(4).setMinWidth(30);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(4).setMaxWidth(70);
        jTable1.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTable1.columnModel.title3")); // NOI18N
        jTable1.getColumnModel().getColumn(5).setMinWidth(30);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jTable1.columnModel.title8")); // NOI18N
        jTable1.getColumnModel().getColumn(6).setMinWidth(30);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("jTable1.columnModel.title9")); // NOI18N
        jTable1.getColumnModel().getColumn(7).setMinWidth(30);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(7).setMaxWidth(70);
        jTable1.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("jTable1.columnModel.title4")); // NOI18N
        jTable1.getColumnModel().getColumn(8).setMinWidth(80);
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(8).setMaxWidth(120);
        jTable1.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("jTable1.columnModel.title5")); // NOI18N
        jTable1.getColumnModel().getColumn(9).setMinWidth(80);
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(9).setMaxWidth(120);
        jTable1.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("jTable1.columnModel.title6")); // NOI18N

        jButtonDelete.setText(resourceMap.getString("jButtonDelete.text")); // NOI18N
        jButtonDelete.setName("jButtonDelete"); // NOI18N
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonBearbeiten.setText(resourceMap.getString("jButtonBearbeiten.text")); // NOI18N
        jButtonBearbeiten.setName("jButtonBearbeiten"); // NOI18N
        jButtonBearbeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBearbeitenActionPerformed(evt);
            }
        });

        jButtonNeuerDatensatz.setText(resourceMap.getString("jButtonNeuerDatensatz.text")); // NOI18N
        jButtonNeuerDatensatz.setName("jButtonNeuerDatensatz"); // NOI18N
        jButtonNeuerDatensatz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNeuerDatensatzActionPerformed(evt);
            }
        });

        buttonGroupFilter.add(jRadioButtonAbgeschlossene);
        jRadioButtonAbgeschlossene.setText(resourceMap.getString("jRadioButtonAbgeschlossene.text")); // NOI18N
        jRadioButtonAbgeschlossene.setActionCommand(resourceMap.getString("jRadioButtonAbgeschlossene.actionCommand")); // NOI18N
        jRadioButtonAbgeschlossene.setName("jRadioButtonAbgeschlossene"); // NOI18N
        jRadioButtonAbgeschlossene.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAbgeschlosseneItemStateChanged(evt);
            }
        });

        buttonGroupFilter.add(jRadioButtonNichtBezahlte);
        jRadioButtonNichtBezahlte.setText(resourceMap.getString("jRadioButtonNichtBezahlte.text")); // NOI18N
        jRadioButtonNichtBezahlte.setActionCommand(resourceMap.getString("jRadioButtonNichtBezahlte.actionCommand")); // NOI18N
        jRadioButtonNichtBezahlte.setName("jRadioButtonNichtBezahlte"); // NOI18N
        jRadioButtonNichtBezahlte.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonNichtBezahlteItemStateChanged(evt);
            }
        });

        buttonGroupFilter.add(jRadioButtonNichtEingereichte);
        jRadioButtonNichtEingereichte.setText(resourceMap.getString("jRadioButtonNichtEingereichte.text")); // NOI18N
        jRadioButtonNichtEingereichte.setActionCommand(resourceMap.getString("jRadioButtonNichtEingereichte.actionCommand")); // NOI18N
        jRadioButtonNichtEingereichte.setName("jRadioButtonNichtEingereichte"); // NOI18N
        jRadioButtonNichtEingereichte.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonNichtEingereichteItemStateChanged(evt);
            }
        });

        buttonGroupFilter.add(jRadioButtonAlle);
        jRadioButtonAlle.setText(resourceMap.getString("jRadioButtonAlle.text")); // NOI18N
        jRadioButtonAlle.setActionCommand(resourceMap.getString("jRadioButtonAlle.actionCommand")); // NOI18N
        jRadioButtonAlle.setName("jRadioButtonAlle"); // NOI18N
        jRadioButtonAlle.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAlleItemStateChanged(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jButtonRechnung.setText(resourceMap.getString("jButtonRechnung.text")); // NOI18N
        jButtonRechnung.setName("jButtonRechnung"); // NOI18N
        jButtonRechnung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRechnungActionPerformed(evt);
            }
        });

        buttonGroupFilter.add(jRadioButtonOffene);
        jRadioButtonOffene.setSelected(true);
        jRadioButtonOffene.setText(resourceMap.getString("jRadioButtonOffene.text")); // NOI18N
        jRadioButtonOffene.setActionCommand(resourceMap.getString("jRadioButtonOffene.actionCommand")); // NOI18N
        jRadioButtonOffene.setName("jRadioButtonOffene"); // NOI18N
        jRadioButtonOffene.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonOffeneItemStateChanged(evt);
            }
        });

        jTextFieldSumme.setEditable(false);
        jTextFieldSumme.setText(resourceMap.getString("jTextFieldSumme.text")); // NOI18N
        jTextFieldSumme.setFocusable(false);
        jTextFieldSumme.setName("jTextFieldSumme"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jTextFieldAnzahl.setEditable(false);
        jTextFieldAnzahl.setText(resourceMap.getString("jTextFieldAnzahl.text")); // NOI18N
        jTextFieldAnzahl.setFocusable(false);
        jTextFieldAnzahl.setName("jTextFieldAnzahl"); // NOI18N

        jDateChooserVonDatum.setName("jDateChooserVonDatum"); // NOI18N
        jDateChooserVonDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserVonDatumPropertyChange(evt);
            }
        });

        jDateChooserBisDatum.setName("jDateChooserBisDatum"); // NOI18N
        jDateChooserBisDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserBisDatumPropertyChange(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jTextFieldStundenzahl.setEditable(false);
        jTextFieldStundenzahl.setText(resourceMap.getString("jTextFieldStundenzahl.text")); // NOI18N
        jTextFieldStundenzahl.setFocusable(false);
        jTextFieldStundenzahl.setName("jTextFieldStundenzahl"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonRechnung)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNeuerDatensatz, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBearbeiten, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(162, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonNichtEingereichte)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButtonAbgeschlossene))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButtonAlle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButtonNichtBezahlte)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonOffene)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooserVonDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jDateChooserBisDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addContainerGap(89, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(217, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldStundenzahl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldAnzahl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldSumme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonBearbeiten, jButtonDelete, jButtonNeuerDatensatz});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButtonAbgeschlossene, jRadioButtonAlle, jRadioButtonNichtBezahlte, jRadioButtonNichtEingereichte});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSumme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldAnzahl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldStundenzahl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButtonAlle)
                            .addComponent(jLabel1)
                            .addComponent(jRadioButtonNichtBezahlte)
                            .addComponent(jRadioButtonOffene))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButtonNichtEingereichte)
                            .addComponent(jRadioButtonAbgeschlossene))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jDateChooserVonDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jDateChooserBisDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRechnung)
                    .addComponent(jButtonNeuerDatensatz)
                    .addComponent(jButtonBearbeiten)
                    .addComponent(jButtonDelete))
                .addGap(12, 12, 12)
                .addComponent(jLabel7))
        );

    }

    private void jButtonNeuerDatensatzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNeuerDatensatzActionPerformed
        Einheit_einzel fenster = new Einheit_einzel(this.klient);
        fenster.addWindowListener(this);
        fenster.setVisible(true);
    }//GEN-LAST:event_jButtonNeuerDatensatzActionPerformed

    private void jButtonBearbeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBearbeitenActionPerformed
        // Datensatz bearbeiten
        editEinheit();
    }//GEN-LAST:event_jButtonBearbeitenActionPerformed

    private void editEinheit(){
        int einheit_id = this.jTable1.getSelectedRow();
        if (einheit_id == -1){
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Edieren!",
                        "Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
        }else{
            einheit_id = this.Arbeitsstunden.elementAt(einheit_id).getID();
            Einheit_einzel fenster = new Einheit_einzel(this.klient,einheit_id);
            fenster.addWindowListener(this);
            fenster.setVisible(true);
        }
    }

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        // Datensatz löschen

    int einheit_id[] = this.jTable1.getSelectedRows();

    int selection = -10;
    
    for (int i=0; i<einheit_id.length;i++){
        einheit_id[i] = this.Arbeitsstunden.elementAt(einheit_id[i]).getID();
    }
    String frageText="";

    String sqltext = "";
        if (this.jTable1.getSelectedRowCount() == 0){
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Löschen!",
                        "Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
        }else{
            sqltext = "SELECT * FROM einheiten WHERE einheiten_id in (" + einheit_id[0];
                    
            for (int i=1; i<einheit_id.length;i++){
                sqltext = sqltext + ", " + einheit_id[i];
            }
            sqltext = sqltext + ");";
            System.out.println("Arbeitsrechnungen::JButton1ActionPerformed: ");
            System.out.println(sqltext);
            Date datum[] = new Date[einheit_id.length];
            try{
                ResultSet einheit = verbindung.query(sqltext);
                int i = 0;
                while (einheit.next()){
                    datum[i] = einheit.getDate("Datum");
                    i++;
                }

                if(einheit_id.length == 1){
                    frageText = "Soll der Datensatz Nr. \""  + einheit_id[0];
                }else{
                    frageText = "Sollen die Datensätze Nr. \""  + einheit_id[0];
                }
                for (i=1; i<einheit_id.length;i++){
                    frageText += ", " + einheit_id[i];
                }
                frageText += "\" vom " + DateFormat.getDateInstance().format(datum[0]);
                for (i=1; i<datum.length;i++){
                    frageText += ", " + DateFormat.getDateInstance().format(datum[i]);
                }
                frageText += " wirklich gelöscht werden?";

                if (datum.length!=einheit_id.length){
                    frageText += "\nAchtung! Anzahl der Id's und der Daten stimmt nicht überein!";
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            //JOptionPane.showOptionDialog(this, frageText, "tool-text - einsetzen", JOptionPane.YES_NO_OPTION, WIDTH, icon, options, jLabel1);

            selection = JOptionPane.showConfirmDialog(this, frageText);
            if (selection == JOptionPane.YES_OPTION){
                for (int i=0; i<einheit_id.length;i++){
                    sqltext = "DELETE FROM einheiten WHERE einheiten_id=" + einheit_id[i] + ";";
                    System.out.println(sqltext);
                    try {
						verbindung.sql(sqltext);
					} catch (SQLException e) {
	                    System.err.println("Sqltext nicht erfolgreich: " + sqltext);
						e.printStackTrace();
					}

                }
                this.update(klient);
                this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
            }
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

//	private String rechnungStart(){
//        String inputfile = "/home/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/Rechnung_Allgemein.tex";
//        String auftraggeber="";
//
//        //Adresse festlegen und in 'adresse' speichern
//        String adresse = "";
//        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
//                optionen.getProperty("user"), optionen.getProperty("password"));
//        String sqltext = "SELECT * FROM klienten WHERE  klienten_id=" + this.klient + ";";
//        System.out.println("ArbeitsstundenTabelle::rechnungStart: " + sqltext);
//        ResultSet angebote = verbindung.query(sqltext);
//        try{
//            angebote.first();
//            System.out.println(angebote.getString("Auftraggeber"));
//            System.out.println(angebote.getString("AAdresse1"));
//            System.out.println(angebote.getString("APLZ"));
//            System.out.println(angebote.getString("AOrt"));
//            auftraggeber = angebote.getString("Auftraggeber");
//            adresse = angebote.getString("Auftraggeber");
//            adresse = adresse + TEXUMBRUCH + angebote.getString("AAdresse1");
//            if(angebote.getString("AAdresse2").length()>2) {
//                adresse = adresse + TEXUMBRUCH + angebote.getString("AAdresse2");
//            }
//            adresse = adresse + TEXUMBRUCH + angebote.getString("APLZ") + " " + angebote.getString("AOrt");
//
//            try{
//                    if(!angebote.getString("tex_datei").isEmpty()) {
//                    inputfile = angebote.getString("tex_datei");
//                }
//            }catch (Exception e){
//                System.err.println("tex-datei ist NULL");
//            }
//        }catch (Exception e){
//            System.err.println("Angebote-Exeption");
//            //e.printStackTrace();
//        }
//
//System.err.println("Inputfile: " + inputfile);
//
//        // Tex-Datei öffnen und in String 'latex' speichern
//        java.io.FileReader latexdatei;
//        String latex = "";
//        try{
//            latexdatei = new java.io.FileReader(inputfile);
//            int c;
//            try{
//                while ((c = latexdatei.read()) != -1) {
////                    System.out.print((char)c);
//                    latex = latex.concat(java.lang.String.valueOf((char)c));
//                }
//                latexdatei.close();
//            }catch (java.io.IOException e){
//                System.err.println("Tex-Datei auslesen und in Variable \"latex\" speichern.");
//                e.printStackTrace();
//            }
//        }catch(java.io.FileNotFoundException e){
//            e.printStackTrace();
//            System.err.println("Tex-Datei öffnen und in String 'latex' speichern");
//        }
//        java.text.DateFormat df;
//        df = java.text.DateFormat.getDateInstance(java.text.DateFormat.DATE_FIELD, Locale.US);
//        String datum = df.format(new java.util.GregorianCalendar().getTime());
//        datum = datum.replace("/", "-");
//        datum = datum.replace(" ", "_");
//
//        if (this.klient==1){
//            latex = this.rechnungFeuerwehr(latex, adresse);
//            outputfile_pdf = outputfile_pdf + "Feuerwehr_" + datum + ".pdf";
//        }else{
//            latex = this.rechnungAllgemein(latex, adresse);
//            outputfile_pdf = outputfile_pdf + auftraggeber.replace(" ", "_").replace("/", "-") + "_" + datum + ".pdf";
//        }
//        return latex;
//    }

//    private String rechnungFeuerwehr(String latex, String adresse){
//
//        // Tabellenkopf
//        String tabelle =
//                "\\\\begin{tabular}{|l|l|l|l|l|}\n" +
//                TEXLINE + " Datum/ Tag & Wache/ Abteilung & Teilnehmer & Ausgefallen & Betrag ";
//        // Daten in Tabelle schreiben und Summe sammeln
//        double rechnung_summe = 0;
//
//        String zeilenumbruch = TEXUMBRUCH + TEXLINE + "\n "; //"\\\\\\\\\\\\hline\n ";
//        for (int i = 0; i<Arbeitsstunden.size(); i++){
//            if (i==0){
//                zeilenumbruch = "\\\\\\\\\\\\hline\\\\hline\n ";
//            }else {
//                zeilenumbruch = "\\\\\\\\\\\\hline\n ";
//            }
//            tabelle = tabelle + zeilenumbruch +
//                    Arbeitsstunden.elementAt(i).getDatum() + " & " +
//                    Arbeitsstunden.elementAt(i).getZusatz1() + " & ";
//            try{
//                Integer.parseInt(Arbeitsstunden.elementAt(i).getZusatz2());
//                tabelle = tabelle + Arbeitsstunden.elementAt(i).getZusatz2() + " &  & ";
//            }catch (NumberFormatException e){
//                tabelle = tabelle + " & Ja & ";
//            }
//            // tabelle = tabelle + Arbeitsstunden.elementAt(i).getZusatz2() + " &  & ";
//            tabelle = tabelle + (Arbeitsstunden.elementAt(i).getPreis() ) +" \\\\officialeuro";
//            rechnung_summe = rechnung_summe + Arbeitsstunden.elementAt(i).getPreis(); //+ Arbeitsstunden.elementAt(i).getPreisAenderung()
//        }
//
//        // Zeile mit Summe hinzufügen
//        tabelle = tabelle + TEXUMBRUCH + TEXLINE + TEXLINE + "\n\\\\textbf{Summe} & & & & " + rechnung_summe + " " + TEXEURO;
//        // Tabellenfuß
//        tabelle = tabelle + TEXUMBRUCH + TEXLINE + "\n\\\\end{tabular}\n";
//        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
//        cal.setTimeInMillis(System.currentTimeMillis());
//        String betreff = " " + cal.get(Calendar.DAY_OF_WEEK) + "." + cal.get(Calendar.YEAR);
//        // Text in latex ersetzen
//        // JOptionPane.showMessageDialog(this, latex);
//        adresse = "-";
//        System.out.println("Adresse: "+ adresse);
//        latex = latex.replaceAll("%%Betreff%%\n", betreff);
//        latex = latex.replaceAll("%%Adresse%%\n", adresse);
//        latex = latex.replaceAll("%%Anrede%%\n", "-");
//        latex = latex.replaceAll("%%Text%%\n",
//                "Rechnungstext:" + TEXUMBRUCH +
////                "\\\\\\\\\n------tab------------\\\\\\\\\n" +
//                tabelle +
//  //              "\\\\\\\\\n------tab------------\\\\\\\\\n" +
//                TEXUMBRUCH + "[.5cm]Ich erlaube mir für " + Arbeitsstunden.size() + " Stunden insgesamt " + rechnung_summe + " " + TEXEURO + " zu berechnen. " +
//                "Bitte überweisen Sie die Summe auf das unten angegebene Konto."
//                );
//        latex = latex.replaceAll("%%Closing%%\n", "Mit freundlichen Grüßen");
//        latex = latex.replaceAll("\n}", "}");
//        return latex;
//    }
//
//    private String rechnungAllgemein(String latex, String adresse){
//        double summe_rechnung = 0;
//        String tabelle;
//
//// Tabellenkopf
//        if(Arbeitsstunden.elementAt(0).getDauer()==0){
//            tabelle =
//                "\\\\begin{tabular}{|l|l|}\n" +
//                TEXLINE + " Datum & Betrag ";
//        }else{
//            tabelle =
//                "\\\\begin{tabular}{|l|l|l|l|}\n" +
//                TEXLINE + " Datum & Tätigkeit & Dauer & Betrag ";
//        }
//// Daten in Tabelle schreiben
//        for (int i = 0; i<Arbeitsstunden.size(); i++){
//            String zeilenumbruch = "\\\\\\\\\\\\hline\n ";
//            if (i==0){
//                zeilenumbruch = "\\\\\\\\\\\\hline\\\\hline\n ";
//            }
//            if(Arbeitsstunden.elementAt(0).getDauer()==0){
//                tabelle = tabelle + zeilenumbruch +
//                        DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.GERMAN).format(
//                    Arbeitsstunden.elementAt(i).getDatum()) + " & ";
//                tabelle = tabelle + (Arbeitsstunden.elementAt(i).getPreis()) +" \\\\officialeuro";
//            }else{
//                tabelle = tabelle + zeilenumbruch +
//                        DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.GERMAN).format(
//                    Arbeitsstunden.elementAt(i).getDatum()) + " & ";
//                tabelle = tabelle + Arbeitsstunden.elementAt(i).getInhalt() + " & ";
//                tabelle = tabelle + Arbeitsstunden.elementAt(i).getDauer()/60 + " Std. & ";
//                tabelle = tabelle + (Arbeitsstunden.elementAt(i).getPreis()) +" \\\\officialeuro";
//            }
//            summe_rechnung = summe_rechnung + Arbeitsstunden.elementAt(i).getPreis(); // + Arbeitsstunden.elementAt(i).getPreisAenderung()
//        }
//
//// Tabellenfuß
//        tabelle = tabelle + "\\\\\\\\\\\\hline\n\\\\end{tabular}\n";
//
//        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
//        cal.setTimeInMillis(System.currentTimeMillis());
//        String betreff = "Rechnung " + cal.get(Calendar.DAY_OF_WEEK) + "." + cal.get(Calendar.YEAR);
//
//// Text in latex ersetzen
//        System.out.println("Adresse: "+ adresse);
//        latex = latex.replaceAll("%%Betreff%%\n", betreff);
//        latex = latex.replaceAll("%%Adresse%%\n", adresse);
//        latex = latex.replaceAll("%%Anrede%%\n", "Sehr geehrte Damen und Herren");
//        latex = latex.replaceAll("%%Text%%\n",
//                "für die folgenden Leistungen erlaube ich mir " + summe_rechnung + " " + TEXEURO + 
//                "\\\\ zu berechnen.\\\\\\\\" + TEXUMBRUCH +
////                "\\\\\\\\\n------tab------------\\\\\\\\\n" +
//                tabelle +
//  //              "\\\\\\\\\n------tab------------\\\\\\\\\n" +
//                "\nBitte überweisen Sie dem genannten Betrag auf dieses Konto:" + TEXUMBRUCH + TEXUMBRUCH +
//                "\n\\\\begin{tabular}[c]{ll}" +
//                "\nBank: & SK Hannover" + TEXUMBRUCH +
//                "\nBLZ: & 25050180" + TEXUMBRUCH +
//                "\nKonto: & 37793160" +
//                "\n\\\\end{tabular}" + "\n"
//                );
//        latex = latex.replaceAll("%%Closing%%\n", "Mit freundlichen Grüßen");
//        latex = latex.replaceAll("\n}", "}");
//        return latex;
//    }

    private void jButtonRechnungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRechnungActionPerformed
        boolean isAnySubmitted = false;

        Vector<Integer> einheitenIDs = new Vector<Integer>();
        for (int i=0; i<this.Arbeitsstunden.size();i++){
            if((!this.Arbeitsstunden.elementAt(i).isVerschickt()) && (!this.Arbeitsstunden.elementAt(i).isBezahlt()))
                einheitenIDs.add(this.Arbeitsstunden.elementAt(i).getID());
            else{
                String nachricht = "Die Einheit vom " + this.Arbeitsstunden.elementAt(i).getDatum() + " ist bereits verschickt oder bezahlt!\n" +
                        "Erstellung der Rechnung abgebrochen.";
                isAnySubmitted = true;
                einheitenIDs.removeAllElements();
                i = this.Arbeitsstunden.size();
                javax.swing.JOptionPane.showMessageDialog(parent, nachricht, "Einheit bereits abgerechnet", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }

        if(!isAnySubmitted){
            RechnungDialog dialog = new RechnungDialog(optionen, this.parent, einheitenIDs);
			dialog.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().matches(RechnungDialog.ERSTELLT)){
						neue_rechnung();
					}
				}
			});
            dialog.setVisible(true);
        }
		this.updateTable();
    }//GEN-LAST:event_jButtonRechnungActionPerformed

	private void neue_rechnung(){
		this.firePropertyChange(RechnungDialog.ERSTELLT, 0, 1);
	}

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        System.out.println("Mouse geklickt! Anzahl: " + evt.getClickCount());
        if(this.jTable1.getSelectedRowCount()==0) jTable1SetSelection(evt);
        if (this.jPopupMenu1.isVisible()) this.jPopupMenu1.setVisible(false);
        if (evt.getClickCount()>1 && !evt.isPopupTrigger()) editEinheit();
        if (evt.isPopupTrigger()) {
            this.jPopupMenu1.show(jTable1, evt.getX(), evt.getY());
            System.out.println("Rechtsklick!");
        }
    }//GEN-LAST:event_jTable1MouseClicked

    /**
     * In der Tabelle wird die Zeile auf die geklickt wurde ausgwählt.
     * @param evt
     */
    private void jTable1SetSelection(java.awt.event.MouseEvent evt){
        System.out.println("Zeile wird selectiert.");
        java.awt.Point p = evt.getPoint();
        int zeile = this.jTable1.rowAtPoint(p);
        this.jTable1.getSelectionModel().setSelectionInterval(zeile, zeile);
    }
    
    private void jRadioButtonAlleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAlleItemStateChanged
        setFilter();
        update(this.klient);
    }//GEN-LAST:event_jRadioButtonAlleItemStateChanged

    private void jRadioButtonNichtEingereichteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonNichtEingereichteItemStateChanged
        setFilter();
        update(this.klient);
    }//GEN-LAST:event_jRadioButtonNichtEingereichteItemStateChanged

    private void jRadioButtonNichtBezahlteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonNichtBezahlteItemStateChanged
        setFilter();
        update(this.klient);
    }//GEN-LAST:event_jRadioButtonNichtBezahlteItemStateChanged

    private void jRadioButtonAbgeschlosseneItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAbgeschlosseneItemStateChanged
        setFilter();
        update(this.klient);
    }//GEN-LAST:event_jRadioButtonAbgeschlosseneItemStateChanged

    private void jRadioButtonOffeneItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonOffeneItemStateChanged
        setFilter();
        update(this.klient);
}//GEN-LAST:event_jRadioButtonOffeneItemStateChanged

    private void setFilter(){
        if(buttonGroupFilter.getSelection() != null){
            filter = buttonGroupFilter.getSelection().getActionCommand();

            if (this.jDateChooserVonDatum.getDate() != null){
                mySqlDate tmpdate = new mySqlDate(this.jDateChooserVonDatum.getDate());
                filter = filter + " AND Datum>=\"" + tmpdate.getSqlDate() + "\"";
            }
            
            if (this.jDateChooserBisDatum.getDate() != null){
                mySqlDate tmpdate = new mySqlDate(this.jDateChooserBisDatum.getDate());
                filter = filter + " AND Datum<=\"" + tmpdate.getSqlDate() + "\"";
            }
            System.out.println("filter: " +  filter);
//            update(klient);
        }
    }

    public void setFilter(int Filter){
        switch(Filter){
            case EINGEREICHTE: {
                jRadioButtonNichtBezahlte.setSelected(true);
                break;
            }
            case NICHTEINGEREICHTE: {
                jRadioButtonNichtEingereichte.setSelected(true);
                break;
            }
            case OFFENE: {
                jRadioButtonOffene.setSelected(true);
                break;
            }
        }
    }

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        if(this.jTable1.getSelectedRowCount()==0) jTable1SetSelection(evt);
        if (evt.isPopupTrigger()) {
            this.jPopupMenu1.show(jTable1, evt.getX(), evt.getY());
            System.out.println("Rechtsklick! (Pressed)");
        }
    }//GEN-LAST:event_jTable1MousePressed

    private void jMenuItembearbeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItembearbeitenActionPerformed
        editEinheit();
        this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
    }//GEN-LAST:event_jMenuItembearbeitenActionPerformed

    private void jMenuItemBezahltDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBezahltDatumActionPerformed
        // Setzt das Bezahlt_Datum
        setDatumOnField("Bezahlt_Datum","Bezahlt");
        update(klient);
        this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
    }//GEN-LAST:event_jMenuItemBezahltDatumActionPerformed

    private boolean setDatumOnField(String feld, String feld2){
        // Sowohl das Datum-Feld (feld) als auch das Boolean-Feld (feld2) müssen übergeben und gesetzt werden

        // Datum auswählen
        java.util.Date datum = null;
        Kalenderauswahl dialog = new Kalenderauswahl(this.parent);
        if(dialog.run()){
            System.out.println("run abgeschlossen...");
            datum = dialog.getDatum(); // Wenn Dialog nicht abgebrochen wurde, setzte Datum (auch null)
            dialog.dispose();
            System.out.println("datum übergeben...");
            System.out.print("Ausgewähltes Datum: ");
            if (datum == null) System.out.println("null");
            else System.out.println(datum.toString());

            // ID der Datensätze herausfinden
            int einheit_id[] = this.jTable1.getSelectedRows();
            for (int i=0; i<einheit_id.length;i++){
                einheit_id[i] = this.Arbeitsstunden.elementAt(einheit_id[i]).getID();
            }

            // SQL-Text erstellen
            String sqltext = "null";
            int wahr = 0;
            if (datum != null) {
                System.out.println("text und int wird geändert...");
                mySqlDate tmpdate = new mySqlDate(datum);
                sqltext = "\"" + tmpdate.getSqlDate() + "\"";
                wahr = 1;
            }
            System.out.println("Text und int geändert. sqltext: " + sqltext);

            sqltext ="UPDATE einheiten SET " + feld + "=" + sqltext +
                    ", "+ feld2 + "=" + wahr +
                    " WHERE einheiten_id IN (";
            for(int i=0;i<einheit_id.length;i++){
                sqltext = sqltext + einheit_id[i] + ",";
                if(i==einheit_id.length-1) sqltext = sqltext.substring(0, sqltext.length()-1) + ");";
            }
            System.out.println("SQL-Text: " + sqltext);

            // SQL ausführen
            try {
				verbindung.sql(sqltext);
			} catch (SQLException e) {
	            System.err.println("Sqltext nicht erfolgreich: " + sqltext);
				e.printStackTrace();
			}
            return true;
        }else return false;
    }

    private void jDateChooserVonDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooserVonDatumPropertyChange
        // Läd Liste mit neuem Filter
        setFilter();
//        System.out.println("Von-Datum geändert!");
    }//GEN-LAST:event_jDateChooserVonDatumPropertyChange

    private void jDateChooserBisDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooserBisDatumPropertyChange
        // Läd Liste mit neuem Filter
        setFilter();
        update(this.klient);
  //      System.out.println("Bis-Datum geändert!");
    }//GEN-LAST:event_jDateChooserBisDatumPropertyChange

    private void jMenuItemRechnungDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRechnungDatumActionPerformed
        // Setzt das Rechnung_Datum
        if(setDatumOnField("Rechnung_Datum","Rechnung_verschickt")){
            update(klient);
            this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
        }
    }//GEN-LAST:event_jMenuItemRechnungDatumActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.jTable1.clearSelection();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public void windowActivated(WindowEvent evt){}
    public void windowClosed(WindowEvent evt){
//        Ein Fenster, dass Daten modifiziert hat wurde geschlossen, Tabelle wird neu geladen
        update(klient);
        this.firePropertyChange("ArbeitsstundenTabelle.Tabellendaten", true, false);
    }
    public void windowClosing(WindowEvent evt){}
    public void windowDeactivated(WindowEvent evt){}
    public void windowDeiconified(WindowEvent evt){}
    public void windowIconified(WindowEvent evt){}
    public void windowOpened(WindowEvent evt){}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupFilter;
    private javax.swing.JButton jButtonBearbeiten;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonNeuerDatensatz;
    private javax.swing.JButton jButtonRechnung;
    private com.toedter.calendar.JDateChooser jDateChooserBisDatum;
    private com.toedter.calendar.JDateChooser jDateChooserVonDatum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemBezahltDatum;
    private javax.swing.JMenuItem jMenuItemEditAll;
    private javax.swing.JMenuItem jMenuItemRechnungDatum;
    private javax.swing.JMenuItem jMenuItembearbeiten;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButton jRadioButtonAbgeschlossene;
    private javax.swing.JRadioButton jRadioButtonAlle;
    private javax.swing.JRadioButton jRadioButtonNichtBezahlte;
    private javax.swing.JRadioButton jRadioButtonNichtEingereichte;
    private javax.swing.JRadioButton jRadioButtonOffene;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldAnzahl;
    private javax.swing.JTextField jTextFieldStundenzahl;
    private javax.swing.JTextField jTextFieldSumme;
    // End of variables declaration//GEN-END:variables
}