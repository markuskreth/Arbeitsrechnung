
/*
 * RechnungDialog.java
 *
 * Created on 02.09.2009, 11:52:54
 */

package arbeitsrechnungen.gui.dialogs;

/**
 *
 * @author markus
 */
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsrechnungen.RechnungData;
import arbeitsrechnungen.data.ArbeitsstundeImpl;

public class RechnungDialog extends javax.swing.JDialog implements PropertyChangeListener,DocumentListener{

	private static final long serialVersionUID = 3906049054488142992L;
	Logger logger = Logger.getLogger("arbeitsrechnungen.RechnungDialog");
    public static final String ERSTELLT = "Rechnung erstellt";
    Verbindung verbindung;
    String adresse;
    String tex_datei;
    Vector<ArbeitsstundeImpl> einheiten = new Vector<ArbeitsstundeImpl>();

    /** Wird in Konstruktor 2 geändert - 
     * true: daten aus Datenbank gesammelt - Rechnung per INSERT gespeichert
     * false: Rechnung aus Datenbank geladen - Rechnung per UPDATE gespeichert
     */
    boolean neu = true;
    int rechnungs_id = -1;
    Integer klienten_id = null;
    GregorianCalendar heute;
    GregorianCalendar rechnungsdatum;
    GregorianCalendar zahlungsdatum;
    String rechnungsnr;
    boolean zusatz1;
    boolean zusatz2;
    boolean stunden_vorhanden = false;
    boolean zusammenfassungen_erlauben;
    String zusatz1_name;
    String zusatz2_name;
    java.util.Properties optionen = new java.util.Properties();
	private PropertyChangeSupport pchListeners = new PropertyChangeSupport(this);
//    Document klientDocument;
//    Document texDateiDocument;
    java.awt.Component prevComponent=null;

    /** Creates new form RechnungDialog */
    public RechnungDialog(java.awt.Frame parent, Vector<Integer> einheiten) {
        super(parent, true);
	logger.setLevel(Level.DEBUG);
        initComponents();

        zusammenfassungen_erlauben = this.jToggleButtonZusammenfassungen.isSelected();
        this.jToggleButtonDetails.setSelected(false);
        toggleDetails();
        optionen = getEinstellungen();
        verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        heute = new GregorianCalendar();
        rechnungsdatum = new GregorianCalendar();
        zahlungsdatum = new GregorianCalendar();
        zahlungsdatum.add(GregorianCalendar.MONTH, 1); // Standard-Zahlfrist: 1 Monat
        getEinheiten(einheiten);

        this.jCheckBoxStundenzahl.setSelected(this.stunden_vorhanden);
        this.jCheckBoxStundenzahl.setVisible(this.stunden_vorhanden);
        
        getAdresse();

        this.jTextKlient.setText(adresse);
        this.jTextTexDatei.setText(tex_datei);
        this.jDateRechnungsdatum.setCalendar(rechnungsdatum);
        this.jDateZahlDatum.setCalendar(zahlungsdatum);
        if (this.zusatz1) {
            this.jCheckBoxZusatz1.setText(this.zusatz1_name);
        }else {
            this.jCheckBoxZusatz1.setVisible(false);
        }
        if (this.zusatz2){
            this.jCheckBoxZusatz2.setText(this.zusatz2_name);
        }else{
            this.jCheckBoxZusatz2.setVisible(false);
        }
        
        setRechnungsnr();
        makeTable();
        setListeners();
    }

        /** Creates new form RechnungDialog */
    public RechnungDialog(java.awt.Frame parent, int rechnungs_id) {
        super(parent, true);
	logger.setLevel(Level.DEBUG);
        initComponents();

        this.jToggleButtonDetails.setSelected(false);
        toggleDetails();
        optionen = getEinstellungen();
        verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));

        heute = new GregorianCalendar();
        this.neu = false;
        this.rechnungs_id = rechnungs_id;
        getEinheiten(rechnungs_id);

        this.jCheckBoxStundenzahl.setSelected(this.stunden_vorhanden);
        this.jCheckBoxStundenzahl.setVisible(this.stunden_vorhanden);

        String sql = "SELECT rechnungen_id, klienten_id, datum, rechnungnr, " +
                "adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum " +
                "FROM rechnungen WHERE rechnungen_id=" + rechnungs_id + ";";
        logger.debug("RechnungDialog: Konstruktor (2): " + sql);
        ResultSet daten = verbindung.query(sql);

        // Daten aus rechnungen-Tabelle speichern
        rechnungsdatum = new GregorianCalendar();
        zahlungsdatum = new GregorianCalendar();
        try{
            if(daten.next()){
                this.klienten_id = daten.getInt("klienten_id");
//logger.debug("dat wird angelegt");
                java.util.Date dat = new java.util.Date();
//logger.debug("angelegt! getDate.getTime wird ausgeführt");
                dat.setTime(daten.getDate("datum").getTime());
//logger.debug("ausgeführt! datum: " + dat.toString() +
//        " rechnungsdatum.setTime wird aufgerufen");
                this.rechnungsdatum.setTime(dat);
//logger.debug("aufgerufen!\n");
                this.rechnungsnr = daten.getString("rechnungnr");
                this.adresse = daten.getString("adresse");
//logger.debug("dat wird neu belegt mit getTime");
                dat.setTime(daten.getDate("zahldatum").getTime());
//logger.debug("neu belegt! datum: " + dat.toString() +
//        "zahlungsdatum.setTime wird ausgeführt");
                this.zahlungsdatum.setTime(dat);
// logger.debug("ausgeführt!");
                this.jCheckBoxZusatz1.setSelected(daten.getBoolean("zusatz1"));
                this.jCheckBoxZusatz2.setSelected(daten.getBoolean("zusatz2"));
                this.zusammenfassungen_erlauben = daten.getBoolean("zusammenfassungen");
            }
        }catch(SQLException e){
            System.err.println("Rechnung_id nicht gefunden!");
        }

        // Daten aus kliententabelle speichern
        sql = "SELECT tex_datei, Zusatz1, Zusatz1_Name, Zusatz2, Zusatz2_Name FROM klienten WHERE klienten_id= " + this.klienten_id + ";";
        logger.debug("RechnungDialog: Konstruktor (2): " + sql);
        daten = verbindung.query(sql);

        try{
            if(daten.next()){
                // TEX-DATEI
                this.tex_datei = optionen.getProperty("stdtexdatei"); // Zuerst Voreinstellung laden, wird ggf. geändert
                try{
                    if(!daten.getString("tex_datei").isEmpty()) {
                        this.tex_datei = daten.getString("tex_datei");   // Wenn leer, wird die Voreinstellung beibehalten
                    }
                }catch (Exception e){
                    System.err.println("tex-datei ist NULL");
                }
                // Zusätze
                this.zusatz1 = daten.getBoolean("Zusatz1");
                this.zusatz1_name = daten.getString("Zusatz1_Name");
                this.zusatz2 = daten.getBoolean("Zusatz2");
                this.zusatz2_name = daten.getString("Zusatz2_Name");

            }
        }catch(SQLException e){
            System.err.println("Rechnung_id nicht gefunden!");
        }

        // Werte in Formular eintragen
        this.jTextKlient.setText(adresse);
        this.jTextTexDatei.setText(tex_datei);
        this.jDateRechnungsdatum.setCalendar(rechnungsdatum);
        this.jDateZahlDatum.setCalendar(zahlungsdatum);
        this.jTextRechnungsnummer.setText(rechnungsnr);

        // Zusatz-Checkboxen konfigurieren
        if (this.zusatz1) {
            this.jCheckBoxZusatz1.setText(this.zusatz1_name);
        }else {
            this.jCheckBoxZusatz1.setVisible(false);
        }
        if (this.zusatz2){
            this.jCheckBoxZusatz2.setText(this.zusatz2_name);
        }else{
            this.jCheckBoxZusatz2.setVisible(false);
        }
        makeTable();
        setListeners();
    }

/**
 *  Versieht Formular-Felder mit EventListenern
 **/
    private void setListeners(){
        this.jDateRechnungsdatum.addPropertyChangeListener(this);
        this.jDateZahlDatum.addPropertyChangeListener(this);
        this.jTextTexDatei.getDocument().addDocumentListener(this);
        this.jTextKlient.getDocument().addDocumentListener(this);
        this.jTextRechnungsnummer.getDocument().addDocumentListener(this);
    }

    public void propertyChange(java.beans.PropertyChangeEvent e){
        javax.swing.JComponent quelle = (javax.swing.JComponent)e.getSource();

        logger.debug("Neuer Wert bei " + quelle.getName() + ": " + e.getNewValue());
        if(quelle.getName().matches(jDateRechnungsdatum.getName())){
            this.rechnungsdatum.setTime(this.jDateRechnungsdatum.getDate());
        }
        if(quelle.getName().matches(jDateZahlDatum.getName())){
            this.zahlungsdatum.setTime(this.jDateZahlDatum.getDate());
        }
    }

    public void insertUpdate(javax.swing.event.DocumentEvent e){
        changeText(e);
    }

    public void removeUpdate(javax.swing.event.DocumentEvent e){
        changeText(e);
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e){
        changeText(e);
    }

    private void changeText(javax.swing.event.DocumentEvent e){
        logger.debug(e.getDocument().getProperty("name"));
        logger.debug(" wurde geändert!");
        logger.debug("DefaultRootElement: " + e.getDocument().getDefaultRootElement());
        if(e.getDocument().equals(this.jTextKlient.getDocument())){
            logger.debug("Adresse geändert!");
            this.adresse = this.jTextKlient.getText();
        }
        if(e.getDocument().equals(this.jTextTexDatei.getDocument())){
            logger.debug("Tex-Datei geändert!");
            this.tex_datei = this.jTextTexDatei.getText();
        }
        if(e.getDocument().equals(this.jTextRechnungsnummer.getDocument())){
            logger.debug("Rechnungsnummer geändert!");
            this.rechnungsnr = this.jTextRechnungsnummer.getText();
        }
    }
    private void makeTable(){
        // TableModel instanzieren mit Titeln und 0 Zeilen
        int spaltenanzahl = 4; // Mindestanzahl : Ja/Nein, Datum, Inhalt und Preis
        int fact = 0;
        if ( this.jCheckBoxStundenzahl.isSelected()){
            spaltenanzahl += 1;
            fact += 1;
        }
        if ( this.jCheckBoxZusatz1.isSelected() && this.zusatz1){
            spaltenanzahl += 1;
            fact += 2;
        }
        if ( this.jCheckBoxZusatz2.isSelected() && this.zusatz2){
            spaltenanzahl += 1;
            fact += 4;
        }

            String[] spaltentitel = new String [spaltenanzahl];
            spaltentitel[0] = "";
            spaltentitel[1] = "Datum";
            spaltentitel[2] = "Inhalt";
            switch (fact){
                case 1:     spaltentitel[3] = "Stunden";
                            break;
                case 2:     spaltentitel[3] = this.zusatz1_name;
                            break;
                case 3:     spaltentitel[3] = "Stunden";
                            spaltentitel[4] = this.zusatz1_name;
                            break;
                case 4:     spaltentitel[3] = this.zusatz2_name;
                            break;
                case 5:     spaltentitel[3] = "Stunden";
                            spaltentitel[4] = this.zusatz2_name;
                            break;
                case 6:     spaltentitel[3] = this.zusatz1_name;
                            spaltentitel[4] = this.zusatz2_name;
                            break;
                case 7:     spaltentitel[3] = "Stunden";
                            spaltentitel[4] = this.zusatz1_name;
                            spaltentitel[5] = this.zusatz2_name;
                            break;
            }
/*            if ( this.jCheckBoxZusatz1.isSelected()  && this.zusatz1) spaltentitel[3] = this.zusatz1_name;
            if ( this.jCheckBoxZusatz2.isSelected()  && this.zusatz2) {
                if ( this.jCheckBoxZusatz1.isSelected()  && this.zusatz1)   spaltentitel[4] = this.zusatz2_name;
                else spaltentitel[3] = this.zusatz2_name;
            }*/
            spaltentitel[spaltenanzahl -1] = "Preis";

        javax.swing.table.DefaultTableModel mymodel =
            new javax.swing.table.DefaultTableModel(
                spaltentitel, 0
            )
        {
            /**
			 * 
			 */
			private static final long serialVersionUID = -5855242848142616562L;
			// Methoden überschreiben
            @Override
            public Class<?> getColumnClass(int columnIndex){
                        if (columnIndex == 0)
                                return Boolean.class;
//                        else if (columnIndex == SPALTE_MIT_ZAHLEN)
  //                              return Integer.class;
                        return String.class;
                }
            @Override
            public boolean isCellEditable(int row, int column){
                if(column != 0) return false;
                else return true;
            }
        };

        for (int i=0; i< einheiten.size();i++){
            Vector<Object> tabellenzeile = new Vector<Object>();

            // ist die Tabelle noch nicht eingerichtet?
            if(jTable1.getModel().getColumnName(0).contains("Title"))
                tabellenzeile.add(new Boolean(true));
            else{
                // Wenn sie schon eingerichtet ist wird der Booleanwert übernommen
                java.lang.Boolean tmp_bool = (java.lang.Boolean)jTable1.getValueAt(i, 0);
                tabellenzeile.add(new Boolean(tmp_bool.booleanValue()));
            }
            tabellenzeile.add(einheiten.elementAt(i).getDatum());
            tabellenzeile.add(einheiten.elementAt(i).getInhalt());
            if ( this.jCheckBoxStundenzahl.isSelected()) tabellenzeile.add(einheiten.elementAt(i).getDauer()/60);   //Minuten zu Stunden
            if ( this.jCheckBoxZusatz1.isSelected() ) tabellenzeile.add(einheiten.elementAt(i).getZusatz1());
            if ( this.jCheckBoxZusatz2.isSelected() ) tabellenzeile.add(einheiten.elementAt(i).getZusatz2());
            tabellenzeile.add(einheiten.elementAt(i).getPreis());
            mymodel.addRow(tabellenzeile);
        }

        jTable1.setModel(mymodel);
        jTable1.getColumnModel().getColumn(0).setMinWidth(15);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(35);
    }
    /**
     * Ermittelnt aufgrund der Adresse und Rechnngsdatum automatisch die Rechnungsnummer
     */
    private void setRechnungsnr(){
        // Die Rechnungsnummer setzt sich zusammen aus Auftraggebernamen, Jahr und Monat der Rechnung
		String sql = "SELECT Auftraggeber,rechnungnummer_bezeichnung FROM klienten WHERE klienten_id=" + this.klienten_id + ";";
		logger.debug("RechnungDialog:setRechnungsnr:" + sql);
		ResultSet auftraggeber = verbindung.query(sql);
		try{
			if(auftraggeber.first()){
				if(auftraggeber.getString("rechnungnummer_bezeichnung") == null||auftraggeber.getString("rechnungnummer_bezeichnung").isEmpty()){
					rechnungsnr = auftraggeber.getString("Auftraggeber");
				}else{
					rechnungsnr = auftraggeber.getString("rechnungnummer_bezeichnung");
				}
				
			}else{
				rechnungsnr = adresse.substring(0, adresse.indexOf("\n"));
			}
		}catch(SQLException e){
				System.err.println("Fehler in Abrfage!");
				System.err.println(e.getSQLState());
				System.err.println("____________________________");
				System.err.println(e.getMessage());
		}
logger.debug("rechnungsnr: " + rechnungsnr);
        // rechnungsnr = rechnungsnr.replace(" ", "_");
        rechnungsnr += "-" + rechnungsdatum.get(GregorianCalendar.YEAR);
        int monat = rechnungsdatum.get(GregorianCalendar.MONTH) + 1;
        if(monat < 10) rechnungsnr += "-" + 0 + monat;
        else rechnungsnr += "-" + monat;
logger.debug("rechnungsnr: " + rechnungsnr);
        // TODO Kontrollieren, ob die Rechnungsnummer in der Datenbank schon existiert
		sql = "SELECT rechnungnr FROM rechnungen WHERE rechnungnr LIKE \"" + rechnungsnr + "%\" ORDER BY rechnungnr;";
		logger.debug("RechnungDialog:setRechnungsnr:" + sql);
		auftraggeber = verbindung.query(sql);
		//Wenn rechnungsnr bereits existiert wird ein buchstabe angehängt.
		try{
			if(auftraggeber.last()){
				Character buchstabe = (char)('a'  + auftraggeber.getRow()-1);
				rechnungsnr += buchstabe;
			}
		}catch(SQLException e){
				System.err.println("Fehler in Abrfage!");
				System.err.println(e.getSQLState());
				System.err.println("____________________________");
				System.err.println(e.getMessage());
		}
logger.debug("rechnungsnr: " + rechnungsnr);
        this.jTextRechnungsnummer.setText(rechnungsnr);
    }

    private void getEinheiten(Vector<Integer> einheiten_int){
		String where = " einheiten.einheiten_id IN (" + einheiten_int.elementAt(0);
        for (int i =1; i<einheiten_int.size();i++){
            where += ", " + einheiten_int.elementAt(i);
        }
        where += ")" ;
        getEinheiten(where);
    }

    private void getEinheiten(int rechnungs_id){
        String where = " rechnung_id=" + rechnungs_id;
        getEinheiten(where);
    }

    private void getEinheiten(String where){
        // Vector einheiten wird mit zugehörigen Daten von Vector einheiten_int gefüllt
        String sql = "SELECT DISTINCT einheiten.einheiten_id AS einheiten_id, einheiten.klienten_id AS klienten_id, " +
                "einheiten.angebote_id AS angebote_id, Datum, Beginn, Ende, zusatz1, zusatz2, Preisänderung, Rechnung_verschickt, " +
                "Rechnung_Datum, Bezahlt,Bezahlt_Datum, Inhalt, einheiten.Preis, einheiten.Dauer, angebote.Preis AS StundenPreis, angebote.preis_pro_stunde FROM einheiten, " +
                "angebote  WHERE einheiten.angebote_id=angebote.angebote_id AND " + where + " ORDER BY Datum, Preis;";
        logger.debug(sql);
        ResultSet daten = verbindung.query(sql);
        try{
            while(daten.next()){
                // Wenn Erster Datensatz kann klienten_id gesetzt werden und fortgefahren
                // Wenn folgender Datensatz und Klientenid stimmt nicht überein -> Abbruch
                if((this.klienten_id == null) || (this.klienten_id == daten.getInt("klienten_id"))){
                    this.klienten_id = daten.getInt("klienten_id");
                    ArbeitsstundeImpl stunde = new ArbeitsstundeImpl(daten.getInt("einheiten_id"), this.klienten_id, daten.getInt("angebote_id"));
                    stunde.setDatum(daten.getDate("Datum"));
                    stunde.setInhalt(daten.getString("Inhalt"));
                    stunde.setBeginn(daten.getTimestamp("Beginn"));
                    stunde.setEnde(daten.getTimestamp("Ende"));
		    stunde.setEinzelPreis(daten.getDouble("StundenPreis"));
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
                    
                    this.stunden_vorhanden = daten.getBoolean("preis_pro_stunde");                    
                    this.einheiten.add(stunde);
                }else{
                    System.err.println("Fehler! Ungleiche Klienten gefunden! Unmöglich!!!");
                    System.err.println("Gespeicherte klienten_id: " + this.klienten_id + "neue klienten_id: " + daten.getInt("klienten_id"));
                    this.einheiten.clear();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * this.adresse wird mit Daten aus der Datenbank gefüllt.
     * Zwingend: this.klienten_id muss gegeben sein
     */
    private void getAdresse(){
        String sql = "SELECT * FROM klienten WHERE klienten_id=" + klienten_id + ";";
        logger.debug(sql);
        ResultSet daten = verbindung.query(sql);
        try{
            daten.next(); // nur ein Datensatz oder leer
            this.adresse = daten.getString("Auftraggeber") + "\n";
            this.adresse += daten.getString("AAdresse1") + "\n";
            this.adresse += daten.getString("AAdresse2") + "\n";
            this.adresse += daten.getString("APLZ") + " ";
            this.adresse += daten.getString("AOrt") + "\n";
            this.zusatz1 = daten.getBoolean("Zusatz1");
            this.zusatz2 = daten.getBoolean("Zusatz2");
            this.zusatz1_name = daten.getString("Zusatz1_Name");
            this.zusatz2_name = daten.getString("Zusatz2_Name");

            tex_datei = optionen.getProperty("stdtexdatei"); // Zuerst Voreinstellung laden, wird ggf. geändert
            try{
                if(!daten.getString("tex_datei").isEmpty()) {
                    tex_datei = daten.getString("tex_datei");   // Wenn leer, wird die Voreinstellung beibehalten
                }
            }catch (Exception e){
                System.err.println("tex-datei ist NULL");
            }
        }catch(SQLException e){
            System.err.println("Klient nicht in Datenbank gefunden! Abbruch!");
        }
    }

    /**
     * Der untere Teil des Fensters (Tex-Datei, Einheiten-Tabelle) wird ein- und ausgeschaltet
     */
    private void toggleDetails(){
        int hoehe = this.jPanelunten.getHeight();
        int driverlocation = this.jSplitPane1.getDividerLocation();
//        logger.debug("DriverLocation: " + driverlocation);
        if(this.jSplitPane1.getRightComponent()==null){
            //Details einschalten
            this.setSize(this.getWidth(), this.getHeight() + hoehe);
            this.jPanelunten.setSize(this.jPaneloben.getWidth(), this.jPaneloben.getHeight());
            this.jSplitPane1.setRightComponent(this.jPanelunten);
            this.jSplitPane1.setDividerLocation(driverlocation);
        }else{
            // Details ausschalten
            this.jSplitPane1.setRightComponent(null);
            this.setSize(this.getWidth(), this.getHeight() - hoehe);
        }
//        logger.debug("Endgültige Position: " + this.jSplitPane1.getDividerLocation());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelunten = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jTextTexDatei = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jCheckBoxVorschau = new javax.swing.JCheckBox();
        jToggleButtonZusammenfassungen = new javax.swing.JToggleButton();
        jLabel7 = new javax.swing.JLabel();
        jPaneloben = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextKlient = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jDateRechnungsdatum = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextRechnungsnummer = new javax.swing.JTextField();
        jDateZahlDatum = new com.toedter.calendar.JDateChooser();
        jToggleButtonDetails = new javax.swing.JToggleButton();
        jCheckBoxZusatz1 = new javax.swing.JCheckBox();
        jCheckBoxZusatz2 = new javax.swing.JCheckBox();
        jCheckBoxStundenzahl = new javax.swing.JCheckBox();
        jCheckBoxUnterschrift = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButtonErstellen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
        
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(446, 322));
        setModal(true);
        setName("Form"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanelunten.setMinimumSize(new java.awt.Dimension(300, 200));
        jPanelunten.setName("jPanelunten"); // NOI18N
        jPanelunten.setPreferredSize(new java.awt.Dimension(388, 200));

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane2.setMinimumSize(new java.awt.Dimension(22, 0));
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setMinimumSize(new java.awt.Dimension(60, 0));
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
        jToggleButtonZusammenfassungen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonZusammenfassungenActionPerformed(evt);
            }
        });

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextTexDatei, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBoxVorschau))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jToggleButtonZusammenfassungen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxVorschau)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButtonZusammenfassungen)
                    .addComponent(jLabel7))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        javax.swing.GroupLayout jPaneluntenLayout = new javax.swing.GroupLayout(jPanelunten);
        jPanelunten.setLayout(jPaneluntenLayout);
        jPaneluntenLayout.setHorizontalGroup(
            jPaneluntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        jPaneluntenLayout.setVerticalGroup(
            jPaneluntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanelunten);

        jPaneloben.setMinimumSize(new java.awt.Dimension(414, 200));
        jPaneloben.setName("jPaneloben"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        AutoBinding<Object, Object, Object, Object> binding = 
        		org.jdesktop.beansbinding.Bindings.createAutoBinding(
        				org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, 
        				jTextKlient, 
        				org.jdesktop.beansbinding.ObjectProperty.create(), 
        				jLabel2, 
        				org.jdesktop.beansbinding.BeanProperty.create("labelFor"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextKlient.setColumns(20);
        jTextKlient.setRows(3);
        jTextKlient.setMinimumSize(new java.awt.Dimension(0, 5));
        jTextKlient.setName("jTextKlient"); // NOI18N
        jTextKlient.setPreferredSize(new java.awt.Dimension(120, 45));
        jScrollPane1.setViewportView(jTextKlient);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        jLabel3.setMinimumSize(new java.awt.Dimension(15, 19));
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(130, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jDateRechnungsdatum, org.jdesktop.beansbinding.ObjectProperty.create(), jLabel3, org.jdesktop.beansbinding.BeanProperty.create("labelFor"));
        bindingGroup.addBinding(binding);

        jDateRechnungsdatum.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        jDateRechnungsdatum.setMinimumSize(new java.awt.Dimension(15, 19));
        jDateRechnungsdatum.setName("rechnungsdatum"); // NOI18N
        jDateRechnungsdatum.setPreferredSize(new java.awt.Dimension(130, 19));
        jDateRechnungsdatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateRechnungsdatumPropertyChange(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        jLabel4.setMinimumSize(new java.awt.Dimension(15, 19));
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(130, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jTextRechnungsnummer, org.jdesktop.beansbinding.ObjectProperty.create(), jLabel4, org.jdesktop.beansbinding.BeanProperty.create("labelFor"));
        bindingGroup.addBinding(binding);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        jLabel5.setMinimumSize(new java.awt.Dimension(15, 19));
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(130, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jDateZahlDatum, org.jdesktop.beansbinding.ObjectProperty.create(), jLabel5, org.jdesktop.beansbinding.BeanProperty.create("labelFor"));
        bindingGroup.addBinding(binding);

        jTextRechnungsnummer.setText(resourceMap.getString("rechnungsnummer.text")); // NOI18N
        jTextRechnungsnummer.setEnabled(false);
        jTextRechnungsnummer.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        jTextRechnungsnummer.setMinimumSize(new java.awt.Dimension(15, 19));
        jTextRechnungsnummer.setName("rechnungsnummer"); // NOI18N
        jTextRechnungsnummer.setPreferredSize(new java.awt.Dimension(130, 19));
        jTextRechnungsnummer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextRechnungsnummerMouseClicked(evt);
            }
        });
        jTextRechnungsnummer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextRechnungsnummerActionPerformed(evt);
            }
        });
        jTextRechnungsnummer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextRechnungsnummerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextRechnungsnummerFocusLost(evt);
            }
        });

        jDateZahlDatum.setMaximumSize(new java.awt.Dimension(2147483647, 30));
        jDateZahlDatum.setMinimumSize(new java.awt.Dimension(15, 19));
        jDateZahlDatum.setName("zahlungsdatum"); // NOI18N
        jDateZahlDatum.setPreferredSize(new java.awt.Dimension(130, 19));
        
        jToggleButtonDetails.setIcon(getIcon(resourceMap.getString("jToggleDetails.icon"))); // NOI18N
        jToggleButtonDetails.setSelected(true);
        jToggleButtonDetails.setText(resourceMap.getString("jToggleDetails.text")); // NOI18N
        jToggleButtonDetails.setToolTipText(resourceMap.getString("jToggleDetails.toolTipText")); // NOI18N
        jToggleButtonDetails.setDisabledIcon(getIcon(resourceMap.getString("jToggleDetails.disabledIcon"))); // NOI18N
        jToggleButtonDetails.setName("jToggleDetails"); // NOI18N
        jToggleButtonDetails.setSelectedIcon(getIcon(resourceMap.getString("jToggleDetails.selectedIcon"))); // NOI18N
        jToggleButtonDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonDetailsActionPerformed(evt);
            }
        });
                
        jCheckBoxZusatz1.setSelected(true);
        jCheckBoxZusatz1.setText(resourceMap.getString("jCheckBoxZusatz1.text")); // NOI18N
        jCheckBoxZusatz1.setName("jCheckBoxZusatz1"); // NOI18N
        jCheckBoxZusatz1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxZusatz1ActionPerformed(evt);
            }
        });

        jCheckBoxZusatz2.setSelected(true);
        jCheckBoxZusatz2.setText(resourceMap.getString("jCheckBoxZusatz2.text")); // NOI18N
        jCheckBoxZusatz2.setName("jCheckBoxZusatz2"); // NOI18N
        jCheckBoxZusatz2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxZusatz2ActionPerformed(evt);
            }
        });

        jCheckBoxStundenzahl.setText(resourceMap.getString("jCheckBoxStundenzahl.text")); // NOI18N
        jCheckBoxStundenzahl.setName("jCheckBoxStundenzahl"); // NOI18N
        jCheckBoxStundenzahl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxStundenzahlActionPerformed(evt);
            }
        });

        jCheckBoxUnterschrift.setText(resourceMap.getString("jCheckBoxUnterschrift.text")); // NOI18N
        jCheckBoxUnterschrift.setActionCommand(resourceMap.getString("jCheckBoxUnterschrift.actionCommand")); // NOI18N
        jCheckBoxUnterschrift.setName("jCheckBoxUnterschrift"); // NOI18N
        jCheckBoxUnterschrift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUnterschriftActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelobenLayout = new javax.swing.GroupLayout(jPaneloben);
        jPaneloben.setLayout(jPanelobenLayout);
        jPanelobenLayout.setHorizontalGroup(
            jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelobenLayout.createSequentialGroup()
                .addGroup(jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelobenLayout.createSequentialGroup()
                        .addComponent(jCheckBoxZusatz1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxZusatz2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxStundenzahl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxUnterschrift))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelobenLayout.createSequentialGroup()
                        .addGroup(jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelobenLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE))
                            .addGroup(jPanelobenLayout.createSequentialGroup()
                                .addComponent(jToggleButtonDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(241, 241, 241)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jDateZahlDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextRechnungsnummer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jDateRechnungsdatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelobenLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        jPanelobenLayout.setVerticalGroup(
            jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelobenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelobenLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateRechnungsdatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateZahlDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextRechnungsnummer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanelobenLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelobenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBoxZusatz1)
                            .addComponent(jCheckBoxZusatz2)
                            .addComponent(jCheckBoxStundenzahl)
                            .addComponent(jCheckBoxUnterschrift))
                        .addGap(18, 18, 18)
                        .addComponent(jToggleButtonDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jSplitPane1.setLeftComponent(jPaneloben);

        jButton1.setText(resourceMap.getString("jButtonAbbrechen.text")); // NOI18N
        jButton1.setName("jButtonAbbrechen"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButtonErstellen.setText(resourceMap.getString("jButtonErstellen.text")); // NOI18N
        jButtonErstellen.setName("jButtonErstellen"); // NOI18N
        jButtonErstellen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonErstellenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jButtonErstellen)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButtonErstellen))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Icon getIcon(String fileName) {
    	fileName = "icons/" + fileName;
    	return new ImageIcon(fileName);
	}
    
	private void jToggleButtonDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonDetailsActionPerformed
        toggleDetails();
    }//GEN-LAST:event_jToggleButtonDetailsActionPerformed

    private void jDateRechnungsdatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateRechnungsdatumPropertyChange
        if(evt.getPropertyName().matches("date")){
            com.toedter.calendar.JDateChooser tmp = (com.toedter.calendar.JDateChooser)evt.getSource();
            if (tmp.getName().matches("jDateRechnungsdatum")){
                this.rechnungsdatum.setTime(this.jDateRechnungsdatum.getDate());
                setRechnungsnr();
                if (!this.neu)
                    javax.swing.JOptionPane.showMessageDialog(this, "Die Rechnungsnummer wurde auf den Standard-Wert zurückgesetzt!", "Achtung!", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            if (tmp.getName().matches("jDateZahlDatum")) this.zahlungsdatum.setTime(this.jDateZahlDatum.getDate());
        }
    }//GEN-LAST:event_jDateRechnungsdatumPropertyChange

    private void jTextRechnungsnummerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextRechnungsnummerMouseClicked
        
        if(evt.getClickCount()>1){
            jTextRechnungsnummer.setEnabled(true);
            jTextRechnungsnummer.requestFocusInWindow();
            jTextRechnungsnummer.setCaretPosition(jTextRechnungsnummer.getText().length());
        }
    }//GEN-LAST:event_jTextRechnungsnummerMouseClicked

    /**
     * Abbrechen gedrückt - Fenster wird ohne Aktion geschlossen.
     * @param evt
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBoxZusatz1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxZusatz1ActionPerformed
        makeTable();
    }//GEN-LAST:event_jCheckBoxZusatz1ActionPerformed

    private void jCheckBoxZusatz2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxZusatz2ActionPerformed
        makeTable();
    }//GEN-LAST:event_jCheckBoxZusatz2ActionPerformed

    /**
     * OK-Button gedrückt - Rechnung wird erstellt
     * @param evt
     */
    private void jButtonErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonErstellenActionPerformed
        
        RechnungData rechnung = new RechnungData();

        String z1 = zusatz1_name;
        String z2 = zusatz2_name;
		rechnung.setUnterschrift(jCheckBoxUnterschrift.isSelected());

        if(!this.zusatz1 || !this.jCheckBoxZusatz1.isSelected()) z1 = null;
        if(!this.zusatz2 || !this.jCheckBoxZusatz2.isSelected()) z2 = null;

        // Nur arbetisstunden übernehmen, die auch gewält sind.
        java.util.Vector<arbeitsrechnungen.data.ArbeitsstundeImpl> tmp_einheiten = new java.util.Vector<arbeitsrechnungen.data.ArbeitsstundeImpl>();
        for (int i = 0; i< einheiten.size() ; i++){
            java.lang.Boolean tmp_bool = (java.lang.Boolean)jTable1.getValueAt(i, 0);
            if (tmp_bool.booleanValue())
                tmp_einheiten.add(einheiten.elementAt(i));
        }
        if(rechnung.initRechnung(this.klienten_id, adresse, tex_datei, tmp_einheiten , heute, rechnungsdatum, zahlungsdatum, rechnungsnr, z1, z2, this.stunden_vorhanden, this.zusammenfassungen_erlauben)){
            int pdf_ergebnis = rechnung.makePdf();
            if (pdf_ergebnis == 0) {
                if (
                        javax.swing.JOptionPane.showConfirmDialog(null, "Soll diese Rechnung so gespeichert werden?", "Speichern?",
                            javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE)
                            == javax.swing.JOptionPane.OK_OPTION)
                {
                    rechnung.speichern();
                    pchListeners.firePropertyChange(ERSTELLT, false, true);
                }
                rechnung.showPdf();
            }
        }else{
            logger.debug("initRechnung nicht erfolgreich!");
        }
		this.setVisible(false);
		this.dispose();
    }//GEN-LAST:event_jButtonErstellenActionPerformed

    private void jTextRechnungsnummerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextRechnungsnummerActionPerformed
        this.rechnungsnr = jTextRechnungsnummer.getText();
    }//GEN-LAST:event_jTextRechnungsnummerActionPerformed

    private void jTextRechnungsnummerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextRechnungsnummerFocusLost
        this.prevComponent.requestFocusInWindow();
        this.rechnungsnr = jTextRechnungsnummer.getText();
        jTextRechnungsnummer.setEnabled(false);
        prevComponent = null;
    }//GEN-LAST:event_jTextRechnungsnummerFocusLost

    private void jTextRechnungsnummerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextRechnungsnummerFocusGained
        this.prevComponent =  evt.getOppositeComponent();
    }//GEN-LAST:event_jTextRechnungsnummerFocusGained

    private void jToggleButtonZusammenfassungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonZusammenfassungenActionPerformed
        // Zusammenfassungen geändert!
        this.zusammenfassungen_erlauben = jToggleButtonZusammenfassungen.isSelected();
        if (this.zusammenfassungen_erlauben){
            this.jLabel7.setText("erlauben!");
        }else{
            this.jLabel7.setText("nicht erlauben!");
        }
    }//GEN-LAST:event_jToggleButtonZusammenfassungenActionPerformed

    private void jCheckBoxStundenzahlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxStundenzahlActionPerformed
        this.stunden_vorhanden = this.jCheckBoxStundenzahl.isSelected();
        makeTable();
    }//GEN-LAST:event_jCheckBoxStundenzahlActionPerformed

	private void jCheckBoxUnterschriftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxUnterschriftActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jCheckBoxUnterschriftActionPerformed

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

	@Override
	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener){
		logger.debug("Listener übergeben und wird hinzugefügt...");
		logger.debug(listener.toString());
		pchListeners.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener){
		pchListeners.removePropertyChangeListener(listener);
	}

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Vector<Integer> testzahlen = new Vector<Integer>();
                testzahlen.add(50);
                testzahlen.add(51);
                testzahlen.add(55);
                testzahlen.add(56);
                testzahlen.add(57);
                testzahlen.add(58);
                testzahlen.add(61);
                RechnungDialog dialog = new RechnungDialog(new javax.swing.JFrame(), testzahlen);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonErstellen;
    private javax.swing.JCheckBox jCheckBoxStundenzahl;
    private javax.swing.JCheckBox jCheckBoxUnterschrift;
    private javax.swing.JCheckBox jCheckBoxVorschau;
    private javax.swing.JCheckBox jCheckBoxZusatz1;
    private javax.swing.JCheckBox jCheckBoxZusatz2;
    private com.toedter.calendar.JDateChooser jDateRechnungsdatum;
    private com.toedter.calendar.JDateChooser jDateZahlDatum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPaneloben;
    private javax.swing.JPanel jPanelunten;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextKlient;
    private javax.swing.JTextField jTextRechnungsnummer;
    private javax.swing.JTextField jTextTexDatei;
    private javax.swing.JToggleButton jToggleButtonDetails;
    private javax.swing.JToggleButton jToggleButtonZusammenfassungen;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
