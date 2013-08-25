/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * KlientenEditor.java
 * Umfassende Bearbeitung von Klientendaten, Angeboten und Zugriff auf Arbeitsstunden
 *
 * Created on 17.04.2009, 22:43:52
 */
package arbeitsrechnungen.gui.jframes;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jdesktop.beansbinding.AutoBinding;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsrechnungen.Einstellungen;
import arbeitsrechnungen.data.Angebot;
import arbeitsrechnungen.data.Klient;
import arbeitsrechnungen.gui.dialogs.AngebotDialog;
import arbeitsrechnungen.gui.jframes.starttablemodels.LabledStringValueNoneditableTableModel;
import arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle;
import arbeitsrechnungen.persister.KlientenEditorPersister;

/**
 * Umfassende Bearbeitung von Klientendaten, Angeboten und Zugriff auf Arbeitsstunden
 * 
 * @author markus
 */
public class KlientenEditor extends javax.swing.JFrame {

	private static final long serialVersionUID = 2229715761785683028L;
	
	Verbindung verbindung;
    Verbindung verbindung_angebote;
    List<Klient> allKlienten;
    Klient currentKlient = null;
    int currentIndex = -1;
    
    ResultSet angebote;
    java.util.Properties optionen;

    public static final int EINGEREICHTE = 1;
    public static final int NICHTEINGEREICHTE = 2;
    public static final int OFFENE = 3;

    /**
     * Focusklasse anonym: Soll die Reihenfolge der Komponenten festgelgen und widergeben.
     */
    final JComponent[] order = new JComponent[]{
        this.jTextFieldAuftraggeber,
        this.jTextFieldAAdress1,
        this.jTextFieldAAdress2,
        this.jTextFieldAPlz,
        this.jTextFieldAOrt,
        this.jTextFieldATelefon,
        this.jTextFieldAEmail,
        this.jTextAreaBemerkungen
    };
    java.awt.FocusTraversalPolicy policy = new FocusTraversalPolicyImpl();
	private KlientenEditorPersister persister;


    /** Creates new form KlientenEditor */
    public KlientenEditor() {
        super("KlientenEditor");

        optionen = new Einstellungen().getEinstellungen();
        persister = new KlientenEditorPersister(optionen);
        
        verbindung_angebote = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));

        allKlienten = persister.getAllKlienten();
        
        if(allKlienten.size()>0){
        	currentKlient = allKlienten.get(0);
        	currentIndex = 0;
        }

        initComponents();
        try {
            this.jTextFieldAPlz.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####")));
            this.jTextFieldKPlz.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setEvents();
        updateRechnungenPanel();
        updateKlient();
        updateAngeboteTabelle();
    }

    /**
     * Setzt Filtern in der ArbeitsstundenTabelle
     */
    public void setFilter(int Filter) {
        switch (Filter) {
            case EINGEREICHTE: {
                this.arbeitsstundenTabelle1.setFilter(ArbeitsstundenTabelle.EINGEREICHTE);
                break;
            }
            case NICHTEINGEREICHTE: {
                this.arbeitsstundenTabelle1.setFilter(ArbeitsstundenTabelle.NICHTEINGEREICHTE);
                break;
            }
            case OFFENE: {
                this.arbeitsstundenTabelle1.setFilter(ArbeitsstundenTabelle.OFFENE);
                break;
            }
        }
    }

    /**
     * Setzt für die Textfelder das Standard-Event TextFieldActionPerformed(ActionEvent evt)
     */
    private void setEvents() {
        jTextFieldKunde.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKunde.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldKAdress1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKAdress1.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldKAdress2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKAdress2.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldKPlz.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKPlz.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldKOrt.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKOrt.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldKTelefon.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKTelefon.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldKEmail.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldKEmail.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldAuftraggeber.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldAuftraggeber.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldAAdress1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldAAdress1.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldAAdress2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldAAdress2.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldAPlz.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldAPlz.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldAOrt.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldAOrt.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldATelefon.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldATelefon.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldAEmail.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldAEmail.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldTex_datei.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        this.jTextAreaBemerkungen.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldZusatzBezeichnung1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldZusatzBezeichnung1.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jTextFieldZusatzBezeichnung2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jTextFieldZusatzBezeichnung2.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
		jTextFieldRechnungBezeichnung.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
		jTextFieldRechnungBezeichnung.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                TextFieldFocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                TextFieldFocusLost(evt);
            }
        });
        jCheckBoxZusatz1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
        jCheckBoxZusatz2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldActionPerformed(evt);
            }
        });
		
		this.formRechnungen1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				formChange(evt);
			}
		});
		this.arbeitsstundenTabelle1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				formChange(evt);
			}
		});
    }

	private void formChange(PropertyChangeEvent evt){
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
    private void updateKlient(){
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
        
        jCheckBoxZusatz1.setSelected(currentKlient.isZusatz1());
        jCheckBoxZusatz2.setSelected(currentKlient.isZusatz2());
        jTextFieldZusatzBezeichnung1.setText(currentKlient.getZusatz1_Name());
        jTextFieldZusatzBezeichnung2.setText(currentKlient.getZusatz2_Name());
        this.jTextFieldRechnungBezeichnung.setText(currentKlient.getRechnungnummer_bezeichnung());

        setVisibilityOfButtons();
    }

    private void setVisibilityOfButtons() {

        jButtonVor.setEnabled(true);
        jButtonZurueck.setEnabled(true);
        jButtonZumEnde.setEnabled(true);
        jButtonZumAnfang.setEnabled(true);
        
        if(currentKlient == null){
            jButtonZurueck.setEnabled(false);
            jButtonZumAnfang.setEnabled(false);
            jButtonVor.setEnabled(false);
            jButtonZumEnde.setEnabled(false);        	
        } else {
        	if(currentKlient.equals(allKlienten.get(0))){
                jButtonZurueck.setEnabled(false);
                jButtonZumAnfang.setEnabled(false);
        	} else if (currentKlient.equals(allKlienten.get(allKlienten.size()-1))){

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
        this.arbeitsstundenTabelle1.update(currentKlient.getKlienten_id());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelArbeitsstunden = new javax.swing.JPanel();
        arbeitsstundenTabelle1 = new arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle(this, currentKlient.getKlienten_id());
        jPanelRechnungen = new javax.swing.JPanel();
        formRechnungen1 = new arbeitsrechnungen.gui.panels.FormRechnungen(currentKlient.getKlienten_id());
        jPanelAngebote = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAngebote = new javax.swing.JTable();
        jButtonaddAngebot = new javax.swing.JButton();
        jButtonEditAngebot = new javax.swing.JButton();
        jButtonDeleteAngebot = new javax.swing.JButton();
        jPanelEinstellungen = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jCheckBoxZusatz1 = new javax.swing.JCheckBox();
        jCheckBoxZusatz2 = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldZusatzBezeichnung1 = new javax.swing.JTextField();
        jTextFieldZusatzBezeichnung2 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldRechnungBezeichnung = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldKPlz = new javax.swing.JFormattedTextField();
        jTextFieldKOrt = new javax.swing.JTextField();
        jTextFieldKAdress2 = new javax.swing.JTextField();
        jTextFieldKAdress1 = new javax.swing.JTextField();
        jTextFieldKunde = new javax.swing.JTextField();
        jTextFieldKTelefon = new javax.swing.JTextField();
        jTextFieldKEmail = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButtonNewKlient = new javax.swing.JButton();
        jButtonDelKlient = new javax.swing.JButton();
        jButtonBeenden = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jButtonZumAnfang = new javax.swing.JButton();
        jButtonZurueck = new javax.swing.JButton();
        jButtonVor = new javax.swing.JButton();
        jButtonZumEnde = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldAPlz = new javax.swing.JFormattedTextField();
        jTextFieldAOrt = new javax.swing.JTextField();
        jTextFieldAuftraggeber = new javax.swing.JTextField();
        jTextFieldAAdress1 = new javax.swing.JTextField();
        jTextFieldAAdress2 = new javax.swing.JTextField();
        jTextFieldATelefon = new javax.swing.JTextField();
        jTextFieldAEmail = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaBemerkungen = new javax.swing.JTextArea();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldTex_datei = new javax.swing.JTextField();
        jButtonfindeTexDatei = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanelArbeitsstunden.setName("jPanelArbeitsstunden"); // NOI18N

        arbeitsstundenTabelle1.setName("arbeitsstundenTabelle1"); // NOI18N

        javax.swing.GroupLayout jPanelArbeitsstundenLayout = new javax.swing.GroupLayout(jPanelArbeitsstunden);
        jPanelArbeitsstunden.setLayout(jPanelArbeitsstundenLayout);
        jPanelArbeitsstundenLayout.setHorizontalGroup(
            jPanelArbeitsstundenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(arbeitsstundenTabelle1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
        );
        jPanelArbeitsstundenLayout.setVerticalGroup(
            jPanelArbeitsstundenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(arbeitsstundenTabelle1, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelArbeitsstunden.TabConstraints.tabTitle"), jPanelArbeitsstunden); // NOI18N

        jPanelRechnungen.setName("jPanelRechnungen"); // NOI18N

        formRechnungen1.setName("formRechnungen1"); // NOI18N

        javax.swing.GroupLayout jPanelRechnungenLayout = new javax.swing.GroupLayout(jPanelRechnungen);
        jPanelRechnungen.setLayout(jPanelRechnungenLayout);
        jPanelRechnungenLayout.setHorizontalGroup(
            jPanelRechnungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 605, Short.MAX_VALUE)
            .addGroup(jPanelRechnungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelRechnungenLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(formRechnungen1, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanelRechnungenLayout.setVerticalGroup(
            jPanelRechnungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
            .addGroup(jPanelRechnungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelRechnungenLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(formRechnungen1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelRechnungen.TabConstraints.tabTitle"), jPanelRechnungen); // NOI18N

        jPanelAngebote.setName("jPanelAngebote"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTableAngebote.setModel(new LabledStringValueNoneditableTableModel(
        		new String [] {"Inhalt", "Preis", "Beschreibung"}
    		));
        
        jTableAngebote.setName("jTableAngebote"); // NOI18N
        jTableAngebote.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jTableAngebote.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
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
        jButtonaddAngebot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonaddAngebotActionPerformed(evt);
            }
        });

        jButtonEditAngebot.setText(resourceMap.getString("jButtonEditAngebot.text")); // NOI18N
        jButtonEditAngebot.setName("jButtonEditAngebot"); // NOI18N
        jButtonEditAngebot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditAngebotActionPerformed(evt);
            }
        });

        jButtonDeleteAngebot.setText(resourceMap.getString("jButtonDeleteAngebot.text")); // NOI18N
        jButtonDeleteAngebot.setName("jButtonDeleteAngebot"); // NOI18N
        jButtonDeleteAngebot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAngebotActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAngeboteLayout = new javax.swing.GroupLayout(jPanelAngebote);
        jPanelAngebote.setLayout(jPanelAngeboteLayout);
        jPanelAngeboteLayout.setHorizontalGroup(
            jPanelAngeboteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAngeboteLayout.createSequentialGroup()
                .addContainerGap(278, Short.MAX_VALUE)
                .addComponent(jButtonaddAngebot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonEditAngebot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonDeleteAngebot)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
        );
        jPanelAngeboteLayout.setVerticalGroup(
            jPanelAngeboteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAngeboteLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelAngeboteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonaddAngebot)
                    .addComponent(jButtonEditAngebot)
                    .addComponent(jButtonDeleteAngebot))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelAngebote.TabConstraints.tabTitle"), jPanelAngebote); // NOI18N

        jPanelEinstellungen.setName("jPanelEinstellungen"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jCheckBoxZusatz1.setText(resourceMap.getString("Zusatz1.text")); // NOI18N
        jCheckBoxZusatz1.setName("Zusatz1"); // NOI18N

        AutoBinding<Object, Object, Object, Object> binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTextFieldZusatzBezeichnung1, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jCheckBoxZusatz1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jCheckBoxZusatz2.setText(resourceMap.getString("Zusatz2.text")); // NOI18N
        jCheckBoxZusatz2.setName("Zusatz2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTextFieldZusatzBezeichnung2, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jCheckBoxZusatz2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTextFieldRechnungBezeichnung, org.jdesktop.beansbinding.ObjectProperty.create(), jLabel19, org.jdesktop.beansbinding.BeanProperty.create("labelFor"));
        bindingGroup.addBinding(binding);

        jTextFieldRechnungBezeichnung.setText(resourceMap.getString("rechnungnummer_bezeichnung.text")); // NOI18N
        jTextFieldRechnungBezeichnung.setName("rechnungnummer_bezeichnung"); // NOI18N

        javax.swing.GroupLayout jPanelEinstellungenLayout = new javax.swing.GroupLayout(jPanelEinstellungen);
        jPanelEinstellungen.setLayout(jPanelEinstellungenLayout);
        jPanelEinstellungenLayout.setHorizontalGroup(
            jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                        .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxZusatz2)
                            .addComponent(jCheckBoxZusatz1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldZusatzBezeichnung1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(57, 57, 57)
                                .addComponent(jTextFieldZusatzBezeichnung2))))
                    .addComponent(jLabel16)
                    .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldRechnungBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(213, Short.MAX_VALUE))
        );
        jPanelEinstellungenLayout.setVerticalGroup(
            jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                        .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jTextFieldZusatzBezeichnung1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jTextFieldZusatzBezeichnung2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelEinstellungenLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxZusatz1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxZusatz2)))
                .addGap(18, 18, 18)
                .addGroup(jPanelEinstellungenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jTextFieldRechnungBezeichnung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(107, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelEinstellungen.TabConstraints.tabTitle"), jPanelEinstellungen); // NOI18N

        jSplitPane1.setBottomComponent(jTabbedPane1);

        jPanel3.setFocusCycleRoot(true);
        jPanel3.setFocusTraversalPolicy(policy);
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setFocusable(false);
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setFocusable(false);
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setFocusable(false);
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setFocusable(false);
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setFocusable(false);
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setFocusable(false);
        jLabel4.setName("jLabel4"); // NOI18N

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
        jButtonNewKlient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewKlientActionPerformed(evt);
            }
        });

        jButtonDelKlient.setText(resourceMap.getString("jButtonDelKlient.text")); // NOI18N
        jButtonDelKlient.setName("jButtonDelKlient"); // NOI18N
        jButtonDelKlient.setPreferredSize(new java.awt.Dimension(110, 25));
        jButtonDelKlient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelKlientActionPerformed(evt);
            }
        });

        jButtonBeenden.setText(resourceMap.getString("jButtonBeenden.text")); // NOI18N
        jButtonBeenden.setName("jButtonBeenden"); // NOI18N
        jButtonBeenden.setPreferredSize(new java.awt.Dimension(110, 25));
        jButtonBeenden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBeendenActionPerformed(evt);
            }
        });

        jLabel14.setFont(Font.getFont(resourceMap.getString("jLabel14.font"))); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        
        jButtonZumAnfang.setIcon(getIcon(resourceMap.getString("jButtonZumAnfang.icon"))); // NOI18N
        jButtonZumAnfang.setText(resourceMap.getString("jButtonZumAnfang.text")); // NOI18N
        jButtonZumAnfang.setName("jButtonZumAnfang"); // NOI18N
        jButtonZumAnfang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZumAnfangActionPerformed(evt);
            }
        });
        
        jButtonZurueck.setIcon(getIcon(resourceMap.getString("jButtonZurueck.icon"))); // NOI18N
        jButtonZurueck.setText(resourceMap.getString("jButtonZurueck.text")); // NOI18N
        jButtonZurueck.setName("jButtonZurueck"); // NOI18N
        jButtonZurueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZurueckActionPerformed(evt);
            }
        });

        jButtonVor.setIcon(getIcon(resourceMap.getString("jButtonVor.icon"))); // NOI18N
        jButtonVor.setText(resourceMap.getString("jButtonVor.text")); // NOI18N
        jButtonVor.setName("jButtonVor"); // NOI18N
        jButtonVor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVorActionPerformed(evt);
            }
        });

        jButtonZumEnde.setIcon(getIcon(resourceMap.getString("jButtonZumEnde.icon"))); // NOI18N
        jButtonZumEnde.setText(resourceMap.getString("jButtonZumEnde.text")); // NOI18N
        jButtonZumEnde.setName("jButtonZumEnde"); // NOI18N
        jButtonZumEnde.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        jButtonfindeTexDatei.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonfindeTexDateiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextFieldKPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jTextFieldKOrt, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldKAdress2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldKAdress1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldKunde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldKTelefon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldKEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextFieldAPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jTextFieldAOrt, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                            .addComponent(jTextFieldAuftraggeber, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                            .addComponent(jTextFieldAAdress1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                            .addComponent(jTextFieldAAdress2, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                            .addComponent(jTextFieldATelefon, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                            .addComponent(jTextFieldAEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNewKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDelKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 149, Short.MAX_VALUE)
                        .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(279, 279, 279)
                        .addComponent(jLabel14)
                        .addGap(225, 225, 225))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButtonZumAnfang)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonZurueck, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jButtonVor, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonZumEnde)
                        .addGap(105, 105, 105))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTex_datei, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonfindeTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonDelKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonNewKlient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonZumEnde)
                    .addComponent(jButtonVor)
                    .addComponent(jButtonZumAnfang)
                    .addComponent(jButtonZurueck))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldAuftraggeber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldAAdress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldAAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jTextFieldAOrt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldAPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldATelefon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldAEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldKunde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldKAdress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldKAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextFieldKOrt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldKPlz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldKTelefon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldKEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(jTextFieldTex_datei, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonfindeTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }

    private Icon getIcon(String fileName) {
    	URL resource = getClass().getResource("/"+fileName);
    	return new ImageIcon(resource);
	}

	/**
     *
     * jButtonVor wurde ausgelöst. Wenn möglich wird der nächste Datensatz im Recordset klientendaten angesprungen
     *
     * @param evt
     */
    private void jButtonVorActionPerformed(java.awt.event.ActionEvent evt) {
        toNextKlientIfPossible();
    }

    private void toNextKlientIfPossible() {
    	if(currentIndex<allKlienten.size()){
        	currentIndex++;
        	currentKlient = allKlienten.get(currentIndex);
            updateComponents();
        }
	}

	/**
     *
     * jButtonZurueck wurde ausgelöst. Wenn möglich wird der vorherige Datensatz im Recordset klientendaten angesprungen
     *
     * @param evt
     */
    private void jButtonZurueckActionPerformed(java.awt.event.ActionEvent evt) {
    	toPreviourKlientIfPossible();
    }

    private void toPreviourKlientIfPossible() {
    	if(currentIndex>=0){
        	currentIndex--;
        	currentKlient = allKlienten.get(currentIndex);
            updateComponents();
        }
	}

	/**
     * Panel für Rechnungen wird unsichtbar oder sichtbar, sobald Rechnungen existieren und wird dann aktualisiert
     */
    private void updateRechnungenPanel(){
        Verbindung verb = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        String sql = "SELECT COUNT(rechnungen_id) AS Anzahl FROM rechnungen WHERE klienten_id=" + currentKlient.getKlienten_id() + ";";
        System.out.println("updateRechnugnenPanel: " + sql);
        ResultSet rechnungen = verb.query(sql);
        
        try{
            this.jTabbedPane1.remove(jPanelRechnungen);
            if (rechnungen.first()){
                System.out.println("Anzahl der Rechnungen: " + rechnungen.getInt(1));
                if (rechnungen.getInt(1) > 0){
//                    this.jTabbedPane1.addTab("Rechnungen", this.jPanelRechnungen);
//                    this.jTabbedPane1.setSelectedComponent(this.jPanelRechnungen);
                    this.jTabbedPane1.insertTab("Rechnungen", null, jPanelRechnungen, null, 1);
                    this.formRechnungen1.update(currentKlient.getKlienten_id());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tabelle mit den Angeboten für diesen Klienten wird aktualisiert
     */
    private void updateAngeboteTabelle() {

        int klient = 0;
		java.text.NumberFormat zf;
        zf = java.text.DecimalFormat.getCurrencyInstance(Locale.GERMANY);

        int maxwidth = jTableAngebote.getColumnModel().getColumn(1).getMaxWidth();
        klient = currentKlient.getKlienten_id();

        List<Angebot> angebote = persister.getAngeboteForKlient(klient);
        
        javax.swing.table.DefaultTableModel mymodel = 
        		new LabledStringValueNoneditableTableModel(
    				new String[]{"Inhalt", "Preis", "Beschreibung"}
				);

        for (Iterator<Angebot> iterator = angebote.iterator(); iterator.hasNext();) {
			Angebot angebot = iterator.next();

            Vector<String> einVektor = new Vector<String>();
            einVektor.addElement(angebot.getInhalt());
            einVektor.addElement(zf.format(angebot.getPreis()));
            einVektor.addElement(angebot.getBeschreibung());

            mymodel.addRow(einVektor);
		}
        jTableAngebote.setModel(mymodel);
        jTableAngebote.getColumnModel().getColumn(1).setMaxWidth(maxwidth);
    }

    /**
     * jButtonNewKlient wurde ausgelöst: Neuer Klient wird in der Datenbank gespeichert und dann aufgerufen
     *
     * @param evt
     */
    private void jButtonNewKlientActionPerformed(java.awt.event.ActionEvent evt) {
    	currentKlient = persister.createNewAuftraggeber();
    	allKlienten = persister.getAllKlienten();
        updateComponents();
    }

    /**
     * jButtonDelKlient wurde ausgelöst: Der Klient wird in der Datenbank gelöscht!
     * Sämtliche Angebote und Einheiten werden gelöscht!
     * 
     * @param evt
     */
    private void jButtonDelKlientActionPerformed(java.awt.event.ActionEvent evt) {
        
        int ergebnis = JOptionPane.showConfirmDialog(this, "Wollen Sie den Datensatz \"" + currentKlient.getAuftraggeber() +
                "\" endgültig löschen?","Löschen?", JOptionPane.YES_NO_OPTION);

    	if (ergebnis == JOptionPane.YES_OPTION) {
            // Zuerst den Datensatz identifizieren, der als nächstes gezeigt wird.
            Klient toDelete = currentKlient;
            
            if (currentIslast()) {
                toPreviourKlientIfPossible();
            } else {
                toNextKlientIfPossible();
            }

            persister.delete(toDelete);
            
            updateComponents();
        }
    }

    private boolean currentIslast(){
    	if(currentKlient != null){
    		return currentKlient.equals(allKlienten.get(allKlienten.size()-1));
    	}
    	return false;
    }
    /**
     * Beenden-Knopf gedrückt: Fenster wird geschlossen. Wenn selbststaendig = true, wird System.exit(0) ausgeführt.
     * @param evt
     */
    private void jButtonBeendenActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * jButtonZumEnde wurde ausgelöst: Der letzte Datensatz des Recordsetzs klientendaten wird angesprungen und die Anzeige wird aktualisiert.
     * @param evt
     */
    private void jButtonZumEndeActionPerformed(java.awt.event.ActionEvent evt) {
    	toLast();
    }

    private void toLast() {
    	if( ! allKlienten.isEmpty()){
	    	currentIndex = allKlienten.size()-1;
	    	currentKlient = allKlienten.get(currentIndex);
    	}
    }

	/**
     * jButtonZumAnfang wurde ausgelöst: Der erste Datensatz des Recordsetzs klientendaten wird angesprungen und die Anzeige wird aktualisiert.
     * @param evt
     */
    private void jButtonZumAnfangActionPerformed(java.awt.event.ActionEvent evt) {
        toFirst();
    }

    private void toFirst() {
    	if( ! allKlienten.isEmpty()){
	    	currentIndex = 0;
	    	currentKlient = allKlienten.get(currentIndex);
    	}
	}

	/**
     * jButtonaddAngebot wurde ausgelöst: Ein Angebot für diesen Klienten wird hinzugefügt.
     * @param evt
     */
    private void jButtonaddAngebotActionPerformed(java.awt.event.ActionEvent evt) {
        // Konstruktor mit 3 Parametern wird für neue Angebote benutzt -1 steht für unbekannte Angebot_ID
        AngebotDialog eingabefenster = new AngebotDialog(this, -1, currentKlient.getKlienten_id());
        eingabefenster.setVisible(true);
        updateAngeboteTabelle();    // Mit geändertem Datensatz anzeigen
    }

    /**
     * JButtonEditAngebot wurde ausgelöst: Das in der Tabelle selectierte Angebot wird in AngebotDialog bearbeitet
     *
     * @param evt
     */
    private void jButtonEditAngebotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditAngebotActionPerformed
        
    	if (this.jTableAngebote.getSelectedRow() == -1) {
        	JOptionPane.showMessageDialog(this, "Bearbeitung abgebrochen!\nEin Eintrag muss ausgewählt sein!");
        } else {
            int datensatz;
            try {
                this.angebote.first();
                this.angebote.relative(this.jTableAngebote.getSelectedRow());
                datensatz = this.angebote.getInt("angebote_id");
                AngebotDialog eingabefenster = new AngebotDialog(this, datensatz);
                eingabefenster.setVisible(true);
                this.updateAngeboteTabelle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButtonEditAngebotActionPerformed

    /**
     * jButtonDeleteAngebot wurde ausgelöst: Gewähltes Angebot wird gelöscht
     * @param evt
     */
    private void jButtonDeleteAngebotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAngebotActionPerformed
        if (this.jTableAngebote.getSelectedRow() == -1) {
        	JOptionPane.showMessageDialog(this, "Bearbeitung abgebrochen!\nEin Eintrag muss ausgewählt sein!");
        } else {
            try {
                this.angebote.first();
                this.angebote.relative(this.jTableAngebote.getSelectedRow());
                int DatensatzID = this.angebote.getInt("angebote_id");

                String Datensatzname = this.angebote.getString("Inhalt");
                
                int ergebnis = JOptionPane.showConfirmDialog(this, "Soll der Datensatz " + Datensatzname +
                        " wirklich endgültig gelöscht werden?","Löschen?", JOptionPane.YES_NO_OPTION);
                
                if (ergebnis == JOptionPane.YES_OPTION) {
                    String sqltext = "DELETE FROM angebote WHERE angebote_id=" + DatensatzID + ";";
                    System.out.println(sqltext);
                    verbindung_angebote.sql(sqltext);
                }
                
                this.updateAngeboteTabelle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButtonDeleteAngebotActionPerformed

    /**
     * Maus auf Angebot-Tabelle geklickt. Bei Doppelklick wird markierter Datensatz bearbeitet.
     * @param evt
     */
    private void jTableAngeboteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableAngeboteMouseClicked
        if (evt.getClickCount() > 1) { // Bei mehr als Einfach-Klick
            if (this.jTableAngebote.getSelectedRow() == -1) { // Wenn keine Zeile markiert ist
                this.jTable1SetSelection(evt);              // Wird die unter dem Mausklick markiert und dann bearbeitet
            }
            int datensatz;
            try {
                this.angebote.first();
                this.angebote.relative(this.jTableAngebote.getSelectedRow());
                datensatz = this.angebote.getInt("angebote_id");
                AngebotDialog eingabefenster = new AngebotDialog(this, datensatz);
                eingabefenster.setVisible(true);
                this.updateAngeboteTabelle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jTableAngeboteMouseClicked

    private void jButtonfindeTexDateiActionPerformed(java.awt.event.ActionEvent evt) {

    	
        java.awt.FileDialog dateiname = new java.awt.FileDialog(this, "Tex-Datei", java.awt.FileDialog.LOAD);
		int endindex = 0;
		if(currentKlient.getTex_datei() == null){
			endindex = 0;
		}else {
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

            public boolean accept(File dir, String name) {
                if (name.endsWith(".tex")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        dateiname.setVisible(true);
        if (dateiname.getFile() != null) {
        	persister.speicherWert(currentKlient.getKlienten_id(), "tex_datei", dateiname.getDirectory() + dateiname.getFile());
            this.jTextFieldTex_datei.setText(currentKlient.getTex_datei());
            
        }
        dateiname.dispose();
    }

    /**
     * In der Tabelle wird die Zeile auf die geklickt wurde ausgwählt.
     * @param evt
     */
    private void jTable1SetSelection(java.awt.event.MouseEvent evt) {
//        System.out.println("Zeile wird selectiert.");
        java.awt.Point p = evt.getPoint();
        int zeile = this.jTableAngebote.rowAtPoint(p);
        this.jTableAngebote.getSelectionModel().setSelectionInterval(zeile, zeile);
    }

    /**
     * Eins der Textfelder wurde bearbeitet. Der neue Wert wird gespeichert
     * @param event
     */
    private void TextFieldActionPerformed(java.awt.event.ActionEvent event) {
        if (event.getSource() instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) event.getSource();
            System.out.println("Text: " + tf.getText() +
                    "\nName: " + tf.getName() + "\n");
            persister.speicherWert(currentKlient.getKlienten_id(), tf.getName(), tf.getText());
            allKlienten = persister.getAllKlienten();
        }
        if (event.getSource() instanceof javax.swing.JCheckBox) {
            javax.swing.JCheckBox cb = (javax.swing.JCheckBox) event.getSource();
            if (cb.isSelected()) {
            	persister.speicherWert(currentKlient.getKlienten_id(), cb.getName(), "1");
            } else {
            	persister.speicherWert(currentKlient.getKlienten_id(), cb.getName(), "0");
            }
            allKlienten = persister.getAllKlienten();
        }

//        System.out.println("Event.getSource: " +  event.getSource());
    }

    /**
     * Eins der Textfelder bekam den Focus. Der enthaltene Text wird markiert
     * @param event
     */
    private void TextFieldFocusGained(java.awt.event.FocusEvent event) {
        if (event.getSource() instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) event.getSource();
            tf.setSelectionStart(0);
            tf.setSelectionEnd(tf.getText().length());
        }
    }

    /**
     * Eins der Textfelder oder TextArea hat den Focus verloren - der Enthaltene Wert wird gespeichert.
     * @param event
     */
    private void TextFieldFocusLost(java.awt.event.FocusEvent event) {
    	
        if (event.getSource() instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) event.getSource();
            persister.speicherWert(currentKlient.getKlienten_id(), tf.getName(), tf.getText());
            allKlienten = persister.getAllKlienten();
        }
        
        if (event.getSource() instanceof javax.swing.JTextArea) {
            javax.swing.JTextArea ta = (javax.swing.JTextArea) event.getSource();
            persister.speicherWert(currentKlient.getKlienten_id(), ta.getName(), ta.getText());
            allKlienten = persister.getAllKlienten();
        }
    }


	public void setCurrentKlientenId(Integer klientenID) {
		for (int i = 0; i < allKlienten.size(); i++) {
			Klient k = allKlienten.get(i);
			if(k.getKlienten_id() == klientenID){
				currentIndex = i;
				currentKlient = k;
				break;
			}
		}
		updateComponents();
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public arbeitsrechnungen.gui.panels.ArbeitsstundenTabelle arbeitsstundenTabelle1;
    private arbeitsrechnungen.gui.panels.FormRechnungen formRechnungen1;
    private javax.swing.JButton jButtonBeenden;
    private javax.swing.JButton jButtonDelKlient;
    private javax.swing.JButton jButtonDeleteAngebot;
    private javax.swing.JButton jButtonEditAngebot;
    private javax.swing.JButton jButtonNewKlient;
    private javax.swing.JButton jButtonVor;
    private javax.swing.JButton jButtonZumAnfang;
    private javax.swing.JButton jButtonZumEnde;
    private javax.swing.JButton jButtonZurueck;
    private javax.swing.JButton jButtonaddAngebot;
    private javax.swing.JButton jButtonfindeTexDatei;
    private javax.swing.JCheckBox jCheckBoxZusatz1;
    private javax.swing.JCheckBox jCheckBoxZusatz2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelAngebote;
    private javax.swing.JPanel jPanelArbeitsstunden;
    private javax.swing.JPanel jPanelEinstellungen;
    private javax.swing.JPanel jPanelRechnungen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableAngebote;
    private javax.swing.JTextArea jTextAreaBemerkungen;
    private javax.swing.JTextField jTextFieldAAdress1;
    private javax.swing.JTextField jTextFieldAAdress2;
    private javax.swing.JTextField jTextFieldAEmail;
    private javax.swing.JTextField jTextFieldAOrt;
    private javax.swing.JFormattedTextField jTextFieldAPlz;
    private javax.swing.JTextField jTextFieldATelefon;
    private javax.swing.JTextField jTextFieldAuftraggeber;
    private javax.swing.JTextField jTextFieldKAdress1;
    private javax.swing.JTextField jTextFieldKAdress2;
    private javax.swing.JTextField jTextFieldKEmail;
    private javax.swing.JTextField jTextFieldKOrt;
    private javax.swing.JFormattedTextField jTextFieldKPlz;
    private javax.swing.JTextField jTextFieldKTelefon;
    private javax.swing.JTextField jTextFieldKunde;
    private javax.swing.JTextField jTextFieldRechnungBezeichnung;
    private javax.swing.JTextField jTextFieldTex_datei;
    private javax.swing.JTextField jTextFieldZusatzBezeichnung1;
    private javax.swing.JTextField jTextFieldZusatzBezeichnung2;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private class FocusTraversalPolicyImpl extends FocusTraversalPolicy {

        public FocusTraversalPolicyImpl() {
        }
        List<JComponent> list = java.util.Arrays.asList(order);

        public Component getFirstComponent(java.awt.Container focusCycleRoot) {
            return order[0];
        }

        public Component getLastComponent(java.awt.Container focusCycleRoot) {
            return order[order.length - 1];
        }

        public Component getComponentAfter(java.awt.Container focusCycleRoot, Component aComponent) {
            int index = list.indexOf(aComponent);
            return order[(index + 1) % order.length];
        }

        public Component getComponentBefore(java.awt.Container focusCycleRoot, Component aComponent) {
            int index = list.indexOf(aComponent);
            return order[(index - 1 + order.length) % order.length];
        }

        public Component getDefaultComponent(java.awt.Container focusCycleRoot) {
            return order[0];
        }

        @Override
        public Component getInitialComponent(java.awt.Window window) {
            return order[0];
        }
    }
    
}
