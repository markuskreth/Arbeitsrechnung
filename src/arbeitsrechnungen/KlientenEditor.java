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
package arbeitsrechnungen;

import java.awt.FocusTraversalPolicy;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.sql.ResultSet;
import arbeitsabrechnungendataclass.Verbindung;
import DialogBox.*;
import java.util.*;
import java.awt.Component;
import javax.swing.JComponent;

/**
 * Umfassende Bearbeitung von Klientendaten, Angeboten und Zugriff auf Arbeitsstunden
 * 
 * @author markus
 */
public class KlientenEditor extends javax.swing.JFrame {

    Verbindung verbindung;
    Verbindung verbindung_angebote;
    private boolean selbststaendig = true;
    protected ResultSet klientendaten;
    ResultSet angebote;
    protected String Kunde;
    protected String AEmail;
    protected String ATelefon;
    protected String AOrt;
    protected String APlz;
    protected String AAdress2;
    protected String AAdress1;
    protected String Auftraggeber;
    protected String KEmail;
    protected String KTelefon;
    protected String KOrt;
    protected String KPlz;
    protected String KAdress2;
    protected String KAdress1;
    protected int klienten_id;
    protected String Bemerkungen;
    protected String tex_datei;
    protected boolean Zusatz1;
    protected String Zusatz1_Name;
    protected boolean Zusatz2;
    protected String Zusatz2_Name;
    protected String rechnungnummer_bezeichnung;
    java.util.Properties optionen = new java.util.Properties();

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


    /** Creates new form KlientenEditor */
    public KlientenEditor() {
        super("KlientenEditor");

        optionen = getEinstellungen();
        verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        verbindung_angebote = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        klientendaten = verbindung.query("SELECT * FROM klienten;");
        try {
            if (klientendaten.first()) {
                wertespeichern();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
     * Speichert die Werte des aktuellen Datensatzes im Recordset klientendaten in den lokalen Feldvariablen
     * Datensatzbewegung muss vorher erfolgt sein! (z.B. jButtonVorActionPerformed oder findKlient
     */
    private void wertespeichern() {
        try {
            setKlienten_id(klientendaten.getInt("klienten_id"));
            setAuftraggeber(klientendaten.getString("Auftraggeber"));
            setAAdress1(klientendaten.getString("AAdresse1"));
            setAAdress2(klientendaten.getString("AAdresse2"));
            setAEmail(klientendaten.getString("AEmail"));
            setAOrt(klientendaten.getString("AOrt"));
            setAPlz(klientendaten.getString("APLZ"));
            setATelefon(klientendaten.getString("ATelefon"));

            setKunde(klientendaten.getString("Kunde"));
            setKAdress1(klientendaten.getString("KAdresse1"));
            setKAdress2(klientendaten.getString("KAdresse2"));
            setKEmail(klientendaten.getString("KEmail"));
            setKOrt(klientendaten.getString("KOrt"));
            setKPlz(klientendaten.getString("KPLZ"));
            setKTelefon(klientendaten.getString("KTelefon"));
            setBemerkungen(klientendaten.getString("Bemerkungen"));
            setTex_datei(klientendaten.getString("tex_datei"));
            setZusatz1(klientendaten.getBoolean("Zusatz1"));
            setZusatz1_Name(klientendaten.getString("Zusatz1_Name"));
            setZusatz2(klientendaten.getBoolean("Zusatz2"));
            setZusatz2_Name(klientendaten.getString("Zusatz2_Name"));
	    setRechnungnummer_bezeichnung(klientendaten.getString("rechnungnummer_bezeichnung"));
        } catch (Exception e) {
            System.out.println("wertespeichern");
            e.printStackTrace();
        }
    }

    /**
     * Speichert einen einzelnen Wert in der Datenbank. Parameter sind der Name des zu änderndes Feldes und der neue Wert
     *
     * @param Feld
     * @param Wert
     */
    private void speicherWert(String Feld, String Wert) {
        /**
         * Einen neuen Wert im zugehörigen Feld speichern.
         */
        try {
            String sql = "UPDATE klienten SET " + Feld + "=\"" + Wert + "\" WHERE klienten_id=" + this.getKlienten_id() + ";";
            verbindung.sql(sql);
            System.out.println(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Hier werden alle Teile des Fensters aktualisiert,
     * wenn sich an der Ansicht etwas geändert hat.
     */
    public void updateComponents() {
        updateKlient();
        updateTables();
    }

    /**
     * Aktualisiert den Klienten und die Buttons
     */
    private void updateKlient(){
        jTextFieldAuftraggeber.setText(getAuftraggeber());
        jTextFieldAAdress1.setText(getAAdress1());
        jTextFieldAAdress2.setText(getAAdress2());
        jTextFieldAPlz.setText(getAPlz());
        jTextFieldAOrt.setText(getAOrt());
        jTextFieldATelefon.setText(getATelefon());
        jTextFieldAEmail.setText(getAEmail());
        jTextFieldKPlz.setText(getKPlz());
        jTextFieldKOrt.setText(getKOrt());
        jTextFieldKunde.setText(getKunde());
        jTextFieldKAdress1.setText(getKAdress1());
        jTextFieldKAdress2.setText(getKAdress2());
        jTextFieldKTelefon.setText(getKTelefon());
        jTextFieldKEmail.setText(getKEmail());
        jTextAreaBemerkungen.setText(getBemerkungen());
        jTextFieldTex_datei.setText(getTex_datei());
        jButtonVor.setEnabled(true);
        jButtonZurueck.setEnabled(true);
        jButtonZumEnde.setEnabled(true);
        jButtonZumAnfang.setEnabled(true);
        jCheckBoxZusatz1.setSelected(isZusatz1());
        jCheckBoxZusatz2.setSelected(isZusatz2());
        jTextFieldZusatzBezeichnung1.setText(this.getZusatz1_Name());
        jTextFieldZusatzBezeichnung2.setText(this.getZusatz2_Name());
	this.jTextFieldRechnungBezeichnung.setText(this.getRechnungnummer_bezeichnung());
	try {
            if (klientendaten.isFirst()) {
                jButtonZurueck.setEnabled(false);
                jButtonZumAnfang.setEnabled(false);
            }
            if (klientendaten.isLast()) {
                jButtonVor.setEnabled(false);
                jButtonZumEnde.setEnabled(false);
            }
        } catch (Exception e) {
            System.out.println("updateComponents");
            e.printStackTrace();
        }
        
    }

    /**
     * Bringt alle Tabellen auf den neuesten Stand (z.B. bei neuer Klientenwahl)
     */
    private void updateTables() {
        updateAngeboteTabelle();
        updateRechnungenPanel();
        this.arbeitsstundenTabelle1.update(this.getKlienten_id());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelArbeitsstunden = new javax.swing.JPanel();
        arbeitsstundenTabelle1 = new arbeitsrechnungen.ArbeitsstundenTabelle(this, this.getKlienten_id());
        jPanelRechnungen = new javax.swing.JPanel();
        formRechnungen1 = new arbeitsrechnungen.FormRechnungen(this.getKlienten_id());
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
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(arbeitsrechnungen.ArbeitsrechnungenApp.class).getContext().getResourceMap(KlientenEditor.class);
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

        jTableAngebote.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Inhalt", "Preis", "Beschreibung"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTextFieldZusatzBezeichnung1, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jCheckBoxZusatz1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
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

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
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

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jButtonZumAnfang.setIcon(resourceMap.getIcon("jButtonZumAnfang.icon")); // NOI18N
        jButtonZumAnfang.setText(resourceMap.getString("jButtonZumAnfang.text")); // NOI18N
        jButtonZumAnfang.setName("jButtonZumAnfang"); // NOI18N
        jButtonZumAnfang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZumAnfangActionPerformed(evt);
            }
        });

        jButtonZurueck.setIcon(resourceMap.getIcon("jButtonZurueck.icon")); // NOI18N
        jButtonZurueck.setText(resourceMap.getString("jButtonZurueck.text")); // NOI18N
        jButtonZurueck.setName("jButtonZurueck"); // NOI18N
        jButtonZurueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZurueckActionPerformed(evt);
            }
        });

        jButtonVor.setIcon(resourceMap.getIcon("jButtonVor.icon")); // NOI18N
        jButtonVor.setText(resourceMap.getString("jButtonVor.text")); // NOI18N
        jButtonVor.setName("jButtonVor"); // NOI18N
        jButtonVor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVorActionPerformed(evt);
            }
        });

        jButtonZumEnde.setIcon(resourceMap.getIcon("jButtonZumEnde.icon")); // NOI18N
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
    }// </editor-fold>//GEN-END:initComponents

    /**
     *
     * jButtonVor wurde ausgelöst. Wenn möglich wird der nächste Datensatz im Recordset klientendaten angesprungen
     *
     * @param evt
     */
    private void jButtonVorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVorActionPerformed
        try {
            if (klientendaten.next()) {
                wertespeichern();
                updateComponents();
            }
        } catch (Exception e) {
            System.out.println("jButtonVorActionPerformed");
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonVorActionPerformed

    /**
     *
     * jButtonZurueck wurde ausgelöst. Wenn möglich wird der vorherige Datensatz im Recordset klientendaten angesprungen
     *
     * @param evt
     */
    private void jButtonZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZurueckActionPerformed
        try {
            if (klientendaten.previous()) {
//                jButtonVor.setEnabled(true);
                wertespeichern();
                updateComponents();
//                boolean wahrheit = klientendaten.isFirst();
//                if(klientendaten.isFirst()){
//                    jButtonZurueck.setEnabled(false);
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonZurueckActionPerformed

    /**
     * Panel für Rechnungen wird unsichtbar oder sichtbar, sobald Rechnungen existieren und wird dann aktualisiert
     */
    private void updateRechnungenPanel(){
        Verbindung verb = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        String sql = "SELECT COUNT(rechnungen_id) AS Anzahl FROM rechnungen WHERE klienten_id=" + getKlienten_id() + ";";
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
                    this.formRechnungen1.update(klienten_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/** Arbeitsstundentabelle wird aktualisiert - ginge auch so - nur ne abkürzung */
	private void updateArbeitsstundenTabelle(){
		this.arbeitsstundenTabelle1.update(klienten_id);
	}

    /**
     * Tabelle mit den Angeboten für diesen Klienten wird aktualisiert
     */
    private void updateAngeboteTabelle() {

        int klient = 0;
		java.text.NumberFormat zf;
        zf = java.text.DecimalFormat.getCurrencyInstance(Locale.GERMANY);

        int maxwidth = jTableAngebote.getColumnModel().getColumn(1).getMaxWidth();
        klient = this.getKlienten_id();

        String sqltext = "SELECT angebote_id, klienten_id, Inhalt, Preis, Beschreibung FROM angebote WHERE klienten_id=" + klient;
        System.out.println("updateKlientenTabelle: " + sqltext);
        angebote = verbindung_angebote.query(sqltext);

        javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Inhalt", "Preis", "Beschreibung"
                }) {

            @Override
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        try {
            int i = 0;
            while (angebote.next()) {
                Vector<Object> einVektor = new Vector<Object>();
                einVektor.addElement(angebote.getString("Inhalt"));
                einVektor.addElement(zf.format(angebote.getDouble("Preis")));
                einVektor.addElement(angebote.getString("Beschreibung"));

                mymodel.addRow(einVektor);
                i++;
            }
        } catch (Exception e) {
            System.out.println("updateTabelle");
            e.printStackTrace();
        }
        jTableAngebote.setModel(mymodel);
        jTableAngebote.getColumnModel().getColumn(1).setMaxWidth(maxwidth);
    }

    /**
     * Sucht im Recordset klientendaten nach id oder dem letzten Datensatz, falls id nicht gefunden wird.
     * @param id
     */
    public void findRow(int id) {
        // Setzt den aktuellen Datensatz auf die gesuche Klienten_ID
        try {
            klientendaten.first();
            while (klientendaten.getInt("klienten_id") != id && klientendaten.next()) {
//                System.out.println("id = " + id + ", klienten_id= " + klientendaten.getInt("klienten_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wertespeichern();
    }

    /**
     * jButtonNewKlient wurde ausgelöst: Neuer Klient wird in der Datenbank gespeichert und dann aufgerufen
     *
     * @param evt
     */
    private void jButtonNewKlientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewKlientActionPerformed
        ResultSet tmp_record;
        int newrecordID;
        try {
            verbindung.sql("INSERT INTO klienten (Auftraggeber, AAdresse1, APLZ, AOrt) " +
                    "VALUES (\"Auftraggeber eingeben\", \"Strasse eingeben\", \"00000\", \"Ort eingeben\");");
            tmp_record = verbindung.query("SELECT LAST_INSERT_ID()");
            tmp_record.first();
            newrecordID = tmp_record.getInt(1);
            klientendaten = verbindung.query("SELECT * FROM klienten;");
            findRow(newrecordID);
            wertespeichern();
            updateComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonNewKlientActionPerformed

    /**
     * jButtonDelKlient wurde ausgelöst: Der Klient wird in der Datenbank gelöscht!
     * Sämtliche Angebote und Einheiten werden gelöscht!
     * 
     * @param evt
     */
    private void jButtonDelKlientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelKlientActionPerformed
        if (ModalDialog.YesNoDialog(this, "Wollen Sie den Datensatz \"" + this.getAuftraggeber() +
                "\" endgültig löschen?").equals("Ja")) {
            try {
                String sql ="";
                // Zuerst den Datensatz identifizieren, der als nächstes gezeigt wird.
                if (klientendaten.isLast()) {
                    klientendaten.previous();
                } else {
                    klientendaten.next();
                }
                final int neuerSatz = klientendaten.getInt("klienten_id");

                // Zuerst alle zugehörigen Angebote und Einheiten in der Datenbank löschen
                final int alterSatz = this.getKlienten_id();
                sql = "DELETE FROM angebote WHERE klienten_id=" + alterSatz;
                System.out.println(sql);
                verbindung.sql(sql);

                // Zugehörige Einheiten löschen
                sql = "DELETE FROM einheiten WHERE klienten_id=" + alterSatz;
                System.out.println(sql);
                verbindung.sql(sql);

                // Zugehörige Rechnungen  löschen
                sql = "DELETE FROM rechnungen WHERE klienten_id=" + alterSatz;
                System.out.println(sql);
                verbindung.sql(sql);

                // Dann den Datensatz in der Datenbank löschen
                sql = "DELETE FROM klienten WHERE klienten_id=" + alterSatz;
                System.out.println(sql);
                verbindung.sql(sql);
                // Dann das Formular aktualisieren
                klientendaten = verbindung.query("SELECT * FROM klienten;");
                findRow(neuerSatz);
                wertespeichern();
                updateComponents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButtonDelKlientActionPerformed

    /**
     * Beenden-Knopf gedrückt: Fenster wird geschlossen. Wenn selbststaendig = true, wird System.exit(0) ausgeführt.
     * @param evt
     */
    private void jButtonBeendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBeendenActionPerformed
        this.setVisible(false);
        this.dispose();
        if (selbststaendig) {
            System.out.println("Fenster ist eigenständig. Anwendung wird beendet!");
            System.exit(0);
        }
    }//GEN-LAST:event_jButtonBeendenActionPerformed

    /**
     * jButtonZumEnde wurde ausgelöst: Der letzte Datensatz des Recordsetzs klientendaten wird angesprungen und die Anzeige wird aktualisiert.
     * @param evt
     */
    private void jButtonZumEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZumEndeActionPerformed
        try {
            if (klientendaten.last()) {
                wertespeichern();
                updateComponents();
            }
        } catch (Exception e) {
            System.out.println("jButtonZumEndeActionPerformed");
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonZumEndeActionPerformed

    /**
     * jButtonZumAnfang wurde ausgelöst: Der erste Datensatz des Recordsetzs klientendaten wird angesprungen und die Anzeige wird aktualisiert.
     * @param evt
     */
    private void jButtonZumAnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZumAnfangActionPerformed
        try {
            if (klientendaten.first()) {
                wertespeichern();
                updateComponents();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonZumAnfangActionPerformed

    /**
     * jButtonaddAngebot wurde ausgelöst: Ein Angebot für diesen Klienten wird hinzugefügt.
     * @param evt
     */
    private void jButtonaddAngebotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonaddAngebotActionPerformed
        // Konstruktor mit 3 Parametern wird für neue Angebote benutzt -1 steht für unbekannte Angebot_ID
        AngebotDialog eingabefenster = new AngebotDialog(this, -1, this.klienten_id);
        eingabefenster.setVisible(true);
        updateAngeboteTabelle();    // Mit geändertem Datensatz anzeigen
    }//GEN-LAST:event_jButtonaddAngebotActionPerformed

    /**
     * JButtonEditAngebot wurde ausgelöst: Das in der Tabelle selectierte Angebot wird in AngebotDialog bearbeitet
     *
     * @param evt
     */
    private void jButtonEditAngebotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditAngebotActionPerformed
        if (this.jTableAngebote.getSelectedRow() == -1) {
            DialogBox.ModalDialog.OKDialog(this, "Bearbeitung abgebrochen!\nEin Eintrag muss ausgewählt sein!");
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
            DialogBox.ModalDialog.OKDialog(this, "Bearbeitung abgebrochen!\nEin Eintrag muss ausgewählt sein!");
        } else {
            try {
                this.angebote.first();
                this.angebote.relative(this.jTableAngebote.getSelectedRow());
                int DatensatzID = this.angebote.getInt("angebote_id");

                String Datensatzname = this.angebote.getString("Inhalt");
                String Ergebnis = DialogBox.ModalDialog.YesNoDialog(this, "Soll der Datensatz " + Datensatzname +
                        " wirklich endgültig gelöscht werden?");
                if (Ergebnis.equals("Ja")) {
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

    private void jButtonfindeTexDateiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonfindeTexDateiActionPerformed

        java.awt.FileDialog dateiname = new java.awt.FileDialog(this, "Tex-Datei", java.awt.FileDialog.LOAD);
		int endindex = 0;
		if(this.tex_datei == null){
			endindex = 0;
		}else {
			endindex = this.tex_datei.lastIndexOf("/");
		}
		
        if (endindex > 0) {
            dateiname.setDirectory(this.tex_datei.substring(0, endindex));
            dateiname.setFile(this.tex_datei.substring(endindex, tex_datei.length()));
        } else {
            if (tex_datei != null) {
                dateiname.setFile(this.tex_datei);
            }
        }
//        System.out.println("Verzeichnis: " + dateiname.getDirectory());
//        System.out.println("Datei: " + dateiname.getFile());
        dateiname.setFilenameFilter(new java.io.FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.endsWith(".tex")) {
                    return true;
                } else {
                    return false;
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        });

        dateiname.setVisible(true);
        if (dateiname.getFile() != null) {
            this.tex_datei = dateiname.getDirectory() + dateiname.getFile();
            this.jTextFieldTex_datei.setText(this.tex_datei);
        }
        dateiname.dispose();
        this.speicherWert("tex_datei", this.tex_datei);
    }//GEN-LAST:event_jButtonfindeTexDateiActionPerformed

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
            this.speicherWert(tf.getName(), tf.getText());
            klientendaten = verbindung.query("SELECT * FROM klienten;");
            findRow(this.getKlienten_id());
        }
        if (event.getSource() instanceof javax.swing.JCheckBox) {
            javax.swing.JCheckBox cb = (javax.swing.JCheckBox) event.getSource();
            if (cb.isSelected()) {
                speicherWert(cb.getName(), "1");
            } else {
                speicherWert(cb.getName(), "0");
            }
            klientendaten = verbindung.query("SELECT * FROM klienten;");
            findRow(this.getKlienten_id());
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
            this.speicherWert(tf.getName(), tf.getText());
            klientendaten = verbindung.query("SELECT * FROM klienten;");
            findRow(this.getKlienten_id());
        }
        if (event.getSource() instanceof javax.swing.JTextArea) {
            javax.swing.JTextArea ta = (javax.swing.JTextArea) event.getSource();
            this.speicherWert(ta.getName(), ta.getText());
            klientendaten = verbindung.query("SELECT * FROM klienten;");
            findRow(this.getKlienten_id());
        }
    }

	/**
	 * Setzt eine spezielle bezeichnung, die für die Rechnungsnummer verwendet wird.
	 * @param rechnungnummer_bezeichnung
	 */
	public void setRechnungnummer_bezeichnung(String rechnungnummer_bezeichnung){
		this.rechnungnummer_bezeichnung = rechnungnummer_bezeichnung;
	}

	/**
	 * Gibt die spezielle bezeichnung, die für die Rechnungsnummer verwendet wird, zurück.
	 * @return
	 */
	public String getRechnungnummer_bezeichnung(){
		return this.rechnungnummer_bezeichnung;
	}

    /**
     * Setzt den Wert selbststaendig, bei false wird verhindert, dass System.exit(0) aufgerufen wird.
     * (standardmäßig wird System.exit(0) aufgerufen!)
     *
     * Wenn das Fenster eigenständig läuft, soll Programm beendet werden anderenfalls, soll nur das Fenster geschlossen werden
     *
     * @param selbststaendig
     */
    public void setSelbststaendig(boolean selbststaendig) {
        this.selbststaendig = selbststaendig;
    }

    /**
     * Get the value of tex_datei
     *
     * @return the value of tex_datei
     */
    public String getTex_datei() {
        return tex_datei;
    }

    /**
     * Set the value of tex_datei
     *
     * @param tex_datei new value of tex_datei
     */
    public void setTex_datei(String tex_datei) {
        this.tex_datei = tex_datei;
    }

    /**
     * Set the value of Bemerkungen
     *
     * @param Bemerkungen new value of Bemerkungen
     */
    public void setBemerkungen(String Bemerkungen) {
        this.Bemerkungen = Bemerkungen;
    }

    /**
     * Set the value of klienten_id
     *
     * @param klienten_id new value of klienten_id
     */
    public void setKlienten_id(int klienten_id) {
        this.klienten_id = klienten_id;
    }

    public void setAAdress1(String AAdress1) {
        this.AAdress1 = AAdress1;
    }

    public void setAAdress2(String AAdress2) {
        this.AAdress2 = AAdress2;
    }

    public void setAEmail(String AEmail) {
        this.AEmail = AEmail;
    }

    public void setAOrt(String AOrt) {
        this.AOrt = AOrt;
    }

    public void setAPlz(String APlz) {
        this.APlz = APlz;
    }

    public void setATelefon(String ATelefon) {
        this.ATelefon = ATelefon;
    }

    public void setAuftraggeber(String Auftraggeber) {
        this.Auftraggeber = Auftraggeber;
    }

    public void setKAdress1(String KAdress1) {
        this.KAdress1 = KAdress1;
    }

    public void setKAdress2(String KAdress2) {
        this.KAdress2 = KAdress2;
    }

    public void setKEmail(String KEmail) {
        this.KEmail = KEmail;
    }

    public void setKOrt(String KOrt) {
        this.KOrt = KOrt;
    }

    public void setKPlz(String KPlz) {
        this.KPlz = KPlz;
    }

    public void setKTelefon(String KTelefon) {
        this.KTelefon = KTelefon;
    }

    public void setKunde(String Kunde) {
        this.Kunde = Kunde;
    }

    /**
     * Get the value of Bemerkungen
     *
     * @return the value of Bemerkungen
     */
    public String getBemerkungen() {
        return Bemerkungen;
    }

    /**
     * Get the value of klienten_id
     *
     * @return the value of klienten_id
     */
    public int getKlienten_id() {
        return klienten_id;
    }

    /**
     * Get the value of Kunde
     *
     * @return the value of Kunde
     */
    public String getKunde() {
        return Kunde;
    }

    /**
     * Get the value of KAdress1
     *
     * @return the value of KAdress1
     */
    public String getKAdress1() {
        return KAdress1;
    }

    /**
     * Get the value of KAdress2
     *
     * @return the value of KAdress2
     */
    public String getKAdress2() {
        return KAdress2;
    }

    /**
     * Get the value of KPlz
     *
     * @return the value of KPlz
     */
    public String getKPlz() {
        return KPlz;
    }

    /**
     * Get the value of KOrt
     *
     * @return the value of KOrt
     */
    public String getKOrt() {
        return KOrt;
    }

    /**
     * Get the value of KTelefon
     *
     * @return the value of KTelefon
     */
    public String getKTelefon() {
        return KTelefon;
    }

    /**
     * Get the value of KEmail
     *
     * @return the value of KEmail
     */
    public String getKEmail() {
        return KEmail;
    }

    /**
     * Get the value of Auftraggeber
     *
     * @return the value of Auftraggeber
     */
    public String getAuftraggeber() {
        return Auftraggeber;
    }

    /**
     * Get the value of KAdress1
     *
     * @return the value of KAdress1
     */
    public String getAAdress1() {
        return AAdress1;
    }

    /**
     * Get the value of KAdress2
     *
     * @return the value of KAdress2
     */
    public String getAAdress2() {
        return AAdress2;
    }

    /**
     * Get the value of KPlz
     *
     * @return the value of KPlz
     */
    public String getAPlz() {
        return APlz;
    }

    /**
     * Get the value of KOrt
     *
     * @return the value of KOrt
     */
    public String getAOrt() {
        return AOrt;
    }

    /**
     * Get the value of KTelefon
     *
     * @return the value of KTelefon
     */
    public String getATelefon() {
        return ATelefon;
    }

    /**
     * Get the value of KEmail
     *
     * @return the value of KEmail
     */
    public String getAEmail() {
        return AEmail;
    }


    /**
     * Get the value of Zusatz2_Name
     *
     * @return the value of Zusatz2_Name
     */
    public String getZusatz2_Name() {
        return Zusatz2_Name;
    }

    /**
     * Set the value of Zusatz2_Name
     *
     * @param Zusatz2_Name new value of Zusatz2_Name
     */
    public void setZusatz2_Name(String Zusatz2_Name) {
        this.Zusatz2_Name = Zusatz2_Name;
    }

    /**
     * Get the value of Zusatz2
     *
     * @return the value of Zusatz2
     */
    public boolean isZusatz2() {
        return Zusatz2;
    }

    /**
     * Set the value of Zusatz2
     *
     * @param Zusatz2 new value of Zusatz2
     */
    public void setZusatz2(boolean Zusatz2) {
        this.Zusatz2 = Zusatz2;
    }

    /**
     * Get the value of Zusatz1_Name
     *
     * @return the value of Zusatz1_Name
     */
    public String getZusatz1_Name() {
        return Zusatz1_Name;
    }

    /**
     * Set the value of Zusatz1_Name
     *
     * @param Zusatz1_Name new value of Zusatz1_Name
     */
    public void setZusatz1_Name(String Zusatz1_Name) {
        this.Zusatz1_Name = Zusatz1_Name;
    }

    /**
     * Get the value of Zusatz1
     *
     * @return the value of Zusatz1
     */
    public boolean isZusatz1() {
        return Zusatz1;
    }

    /**
     * Set the value of Zusatz1
     *
     * @param Zusatz1 new value of Zusatz1
     */
    public void setZusatz1(boolean Zusatz1) {
        this.Zusatz1 = Zusatz1;
    }

    public Properties getEinstellungen(){
        java.util.Properties sysprops = System.getProperties();
        java.io.File optionfile  = new java.io.File(sysprops.getProperty("user.home") + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");

        try{
            optionen.load(new java.io.FileInputStream(optionfile));
            return optionen;
        }catch(Exception e){
            System.err.println("RechnungenDialog.java: Options-Datei konnte nicht geladen werden.");
            return null;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                KlientenEditor dialog = new KlientenEditor();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public arbeitsrechnungen.ArbeitsstundenTabelle arbeitsstundenTabelle1;
    private arbeitsrechnungen.FormRechnungen formRechnungen1;
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
