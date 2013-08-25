/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Einheit_einzel.java
 *
 * Created on 21.05.2009, 16:18:44
 */

package arbeitsrechnungen.gui.jframes;

/**
 *
 * @author markus
 */

import java.awt.Font;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsrechnungen.mySqlDate;
//import javax.swing.JOptionPane;

public class Einheit_einzel extends javax.swing.JFrame {

	private static final long serialVersionUID = 3963303174102985288L;
	java.util.Properties optionen = new java.util.Properties();
    int klient;
    int einheit = -1;
    private boolean zusatz1 = false;
    private boolean zusatz2 = false;
    private String zusatz1_name = "";
    private String zusatz2_name = "";
    Vector<Integer> angeboteliste;

    /** Creates new form Einheit_einzel */
    public Einheit_einzel() {
        // Sollte nicht benutzt werden!
        // Oder nur in Kombination mit  setKlient()
        this(1,2);
    }

    /** Creates new form Einheit_einzel */
    public Einheit_einzel(int klient) {
        // Neuen Datensatz anlegen
        this(klient,-1);
    }

    /** Creates new form Einheit_einzel */
    public Einheit_einzel(int klient, int einheit) {
        // Bestehenden Datensatz edieren
        optionen = getEinstellungen();
        this.klient = klient;
        initComponents();
        MaskFormatter startmask;
        MaskFormatter endemask;
        try{
            startmask = new MaskFormatter("##:##");
            startmask.setPlaceholderCharacter('_');
            startmask.install(jFormattedTextFieldStart);
            endemask = new MaskFormatter("##:##");
            endemask.setPlaceholderCharacter('_');
            endemask.install(jFormattedTextFieldEnde);
        }catch (Exception e){
            e.printStackTrace();
        }
        initAngebote();
        setAuftraggeber();
        setZusaetze();
        if(einheit>-1){
            this.einheit = einheit;
            setEinheit();
        }
    }

    private Properties getEinstellungen(){
        java.util.Properties sysprops = System.getProperties();
        java.util.Properties opt = new java.util.Properties();
        java.io.File optionfile  = new java.io.File(sysprops.getProperty("user.home") + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");
        try{
            opt.load(new java.io.FileInputStream(optionfile));
//System.out.println("testopt: " + opt.getProperty("sqlserver"));
            return opt;
        }catch(Exception e){
            System.err.println("ArbeitsstundenTabelle.java: Options-Datei konnte nicht geladen werden.");
            return null;
        }
    }

    private void setZusaetze(){
        if (this.zusatz1) this.jLabel8.setText(zusatz1_name);
        else {
            this.jLabel8.setVisible(false);
            this.jTextFieldZusatz1.setVisible(false);
        }
        if (this.zusatz2) this.jLabel9.setText(zusatz2_name);
        else {
            this.jLabel9.setVisible(false);
            this.jTextFieldZusatz2.setVisible(false);
        }
    }

    private void setAuftraggeber(){
        // Id und Name des übergebenen auftraggebers einfügen
        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        String sqltext = "SELECT Auftraggeber, Zusatz1, Zusatz2, Zusatz1_Name, Zusatz2_Name  FROM klienten WHERE klienten_id=" + this.klient + ";";
        System.out.println("Einheit_einzel::setAuftraggeber: " + sqltext);
        ResultSet daten = verbindung.query(sqltext);
        try{
            if (daten.first()){
                this.jTextField1.setText(String.valueOf(this.klient));
                this.jTextField6.setText(daten.getString("Auftraggeber"));
                this.zusatz1 = daten.getBoolean("Zusatz1");
                this.zusatz2 = daten.getBoolean("Zusatz2");
                this.zusatz1_name = daten.getString("Zusatz1_Name");
                this.zusatz2_name = daten.getString("Zusatz2_Name");
            }
        }catch (Exception e){
            System.err.println("Einheit_einzel::setAuftraggeber: ");
            e.printStackTrace();
        }
    }

    private void setEinheit(){
        // Füllt das Formular mit existierenden Feldern
        if (this.einheit>-1){
            Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
            String sqltext =
                    "SELECT einheiten_id,klienten_id,angebote_id,Datum,Beginn,Ende,Preisänderung,zusatz1,zusatz2,Rechnung_Datum,Bezahlt_Datum " +
                    "FROM einheiten " +
                    "WHERE einheiten_id=" + this.einheit +
                    ";";
            System.out.println("Einheit_einzel::setEinheit: " + sqltext);
            ResultSet daten = verbindung.query(sqltext);
            try{
                daten.first();
                if (daten.getInt("klienten_id") != this.klient){
                    JOptionPane.showMessageDialog(this, "Achtung!!!\n" +
                            "Klienten_id des Konstruktors stimmt nicht mit der des übegebenen" +
                            " Datensatzes überein!" +
                            "\nDatensatz-Klient: " + daten.getInt("klienten_id") +
                            "\nKonstruktor-Klient: " + this.klient);
                }
                //Set Angebot-Combobox
                this.jComboBoxAngebot.setSelectedIndex(this.angeboteliste.indexOf(daten.getInt("angebote_id")));
                //Set Uhrzeit Beginn
                java.util.Date zeit = daten.getTimestamp("Beginn");
                String stzeit = DateFormat.getTimeInstance().format(zeit);
//System.out.println(stzeit);
                stzeit = stzeit.substring(0, stzeit.length()-2);
//System.out.println(stzeit);
                this.jFormattedTextFieldStart.setText(stzeit);

                //Set Uhrzeit Ende
                zeit = daten.getTimestamp("Ende");
                stzeit = DateFormat.getTimeInstance().format(zeit);
//System.out.println(stzeit);
                stzeit = stzeit.substring(0, stzeit.length()-2);
//System.out.println(stzeit);
                this.jFormattedTextFieldEnde.setText(stzeit);
                this.jDateChooserDatum.setDate(daten.getDate("Datum"));
                this.jTextFieldPreisAenderung.setText(daten.getString("Preisänderung"));
                this.jTextFieldZusatz1.setText(daten.getString("zusatz1"));
                this.jTextFieldZusatz2.setText(daten.getString("zusatz2"));
                this.jDateChooserEingereicht.setDate(daten.getDate("Rechnung_Datum"));
                this.jDateChooserBezahlt.setDate(daten.getDate("Bezahlt_Datum"));
            }catch (Exception e){
                System.err.println("Einheit_einzel::setEinheit: ");
                e.printStackTrace();
            }
        }else{
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Datensatz aus der Tabelle zum Edieren!",
                        "Kein Datensatz ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
            this.setVisible(false);
            this.dispose();
        }
    }

    private void initAngebote(){
        this.jComboBoxAngebot.removeAllItems();
        this.angeboteliste = new Vector<Integer>();

        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));

        String sqltext = "SELECT * FROM angebote WHERE klienten_id=" + this.klient + ";";
        System.out.println("Einheit_einzel::initangebote: " + sqltext);
        ResultSet daten = verbindung.query(sqltext);
        try{
            while(daten.next()){
                String datensatz;
                datensatz = daten.getString("Inhalt") + "|" + daten.getString("Preis") + "€";
                this.jComboBoxAngebot.addItem(datensatz);
                this.angeboteliste.addElement(daten.getInt("angebote_id"));
            }
        }catch (Exception e){
            System.err.println("Einheit_einzel::initangebote: ");
            e.printStackTrace();
        }
    }

    private void saveData(){
        // Wenn this.einheit = -1 dann existiert der Datensatz noch nicht und
        // muss angelegt werden.
        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));
        String sqltext="";
        double preis = 0.0;
        long dauer = 0;

        mySqlDate tmpdate = new mySqlDate(this.jDateChooserDatum.getDate());
        String datum = tmpdate.getSqlDate();

        java.sql.Date sqldate2 = new java.sql.Date(0);
        String EingereichtDatum = "NULL";
        int isEingereicht =0;
        String BezahltDatum = "NULL";
        int isBezahlt = 0;
        
        if(this.jDateChooserEingereicht.getDate() != null){
            System.out.println(this.jDateChooserEingereicht.getDate());
            sqldate2.setTime(this.jDateChooserEingereicht.getDate().getTime());
            EingereichtDatum = sqldate2.toString();
            isEingereicht = 1;
        }
        
        if(this.jDateChooserBezahlt.getDate() != null){
            System.out.println(this.jDateChooserBezahlt.getDate());
            sqldate2.setTime(this.jDateChooserBezahlt.getDate().getTime());
            BezahltDatum = sqldate2.toString();
            isBezahlt = 1;
        }

        // Setzten der Angebot-Elemente für diese Einheit
        int angebot_id= this.angeboteliste.elementAt(this.jComboBoxAngebot.getSelectedIndex());
        sqltext = "SELECT Preis, preis_pro_stunde FROM angebote WHERE angebote_id=" + angebot_id + ";";
        ResultSet daten = verbindung.query(sqltext);

        try{
            daten.first();
            preis = Math.round((daten.getDouble("Preis") + Double.parseDouble(this.jTextFieldPreisAenderung.getText()))*100);
            preis = preis / 100;
            if (daten.getBoolean("preis_pro_stunde")){
                    GregorianCalendar startcal = new GregorianCalendar();
                    startcal.setTime(this.jDateChooserDatum.getDate());
                    String starttext = this.jFormattedTextFieldStart.getText();
                    String[] startfeld = starttext.split(":");

//                    System.out.println("StartDatum ohne Zeit: " + startcal.getTime());
                    startcal.add(GregorianCalendar.HOUR, Integer.parseInt(startfeld[0]));
                    startcal.add(GregorianCalendar.MINUTE, Integer.parseInt(startfeld[1]));

                    GregorianCalendar endecal = new GregorianCalendar();
                    endecal.setTime(this.jDateChooserDatum.getDate());
                    String endetext = this.jFormattedTextFieldEnde.getText();
                    String[] endefeld = endetext.split(":");

//                    System.out.println("Ende-Datum ohne Zeit: " + endecal.getTime());
                    endecal.add(GregorianCalendar.HOUR, Integer.parseInt(endefeld[0]));
                    endecal.add(GregorianCalendar.MINUTE, Integer.parseInt(endefeld[1]));

                    dauer = Math.round( ((double)endecal.getTime().getTime() - startcal.getTime().getTime())/( 60.*1000.));
                    System.out.println("Dauer: " + dauer + " Minuten");
                    System.out.println("Dauer: " + (double)dauer/60 + " Stunden");
                    preis = Math.round(((double)dauer/60 * preis) *100);
                    preis = preis / 100;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if (this.einheit == -1){
            if((isEingereicht != 0) && (isBezahlt != 0)){
                sqltext = "INSERT INTO einheiten "
                    + "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer," +
                    "Rechnung_verschickt,Rechnung_Datum,Bezahlt,Bezahlt_Datum,Preisänderung) VALUES "
                    + "(" + this.klient + "," + angebot_id
                    + ",\"" + datum
                    + "\",\"" + datum + " " + this.jFormattedTextFieldStart.getText() + ":00"
                    + "\",\"" + datum + " " + this.jFormattedTextFieldEnde.getText() + ":00"
                    + "\",\"" + this.jTextFieldZusatz1.getText()
                    + "\",\"" + this.jTextFieldZusatz2.getText()
                    + "\",\"" + preis
                    + "\",\"" + dauer
                    + "\",\"" + isEingereicht
                    + "\",\"" + EingereichtDatum
                    + "\",\"" + isBezahlt
                    + "\",\"" + BezahltDatum
                    + "\",\"" + this.jTextFieldPreisAenderung.getText() + "\");";
            }else if (isEingereicht != 0){
                sqltext = "INSERT INTO einheiten "
                    + "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer," +
                    "Rechnung_verschickt,Rechnung_Datum,Preisänderung) VALUES "
                    + "(" + this.klient + "," + angebot_id
                    + ",\"" + datum
                    + "\",\"" + datum + " " + this.jFormattedTextFieldStart.getText() + ":00"
                    + "\",\"" + datum + " " + this.jFormattedTextFieldEnde.getText() + ":00"
                    + "\",\"" + this.jTextFieldZusatz1.getText().trim()
                    + "\",\"" + this.jTextFieldZusatz2.getText().trim()
                    + "\",\"" + preis
                    + "\",\"" + dauer
                    + "\",\"" + isEingereicht
                    + "\",\"" + EingereichtDatum
                    + "\",\"" + this.jTextFieldPreisAenderung.getText().trim() + "\");";
            }else {
                sqltext = "INSERT INTO einheiten "
                    + "(klienten_id,angebote_id,Datum,Beginn,Ende,zusatz1,zusatz2,Preis,Dauer," +
                    "Preisänderung) VALUES "
                    + "(" + this.klient + "," + angebot_id
                    + ",\"" + datum
                    + "\",\"" + datum + " " + this.jFormattedTextFieldStart.getText() + ":00"
                    + "\",\"" + datum + " " + this.jFormattedTextFieldEnde.getText() + ":00"
                    + "\",\"" + this.jTextFieldZusatz1.getText().trim()
                    + "\",\"" + this.jTextFieldZusatz2.getText().trim()
                    + "\",\"" + preis
                    + "\",\"" + dauer
                    + "\",\"" + this.jTextFieldPreisAenderung.getText() + "\");";
            }
        }else{
            sqltext = "UPDATE einheiten set "
                    + "angebote_id=" +this.angeboteliste.elementAt(this.jComboBoxAngebot.getSelectedIndex())
                    + ",Datum=\"" + datum + "\""
                    + ",Beginn=\"" + datum + " " + this.jFormattedTextFieldStart.getText() + ":00\""
                    + ",Ende=\"" + datum + " " + this.jFormattedTextFieldEnde.getText() + ":00\""
                    + ",zusatz1=\"" + this.jTextFieldZusatz1.getText().trim() + "\""
                    + ",zusatz2=\"" + this.jTextFieldZusatz2.getText().trim() + "\""
                    + ",Preis=" + preis
                    + ",Dauer=" + dauer;
            if (isEingereicht != 0){
                sqltext = sqltext + ",Rechnung_verschickt=\"" + isEingereicht + "\""
                                    + ",Rechnung_Datum=\"" + EingereichtDatum + "\"";
            }else{
                sqltext = sqltext + ",Rechnung_verschickt=NULL"
                                    + ",Rechnung_Datum=NULL";
            }
            if((isEingereicht != 0) && (isBezahlt != 0)){
                    sqltext = sqltext + ",Bezahlt=\"" + isBezahlt + "\""
                            + ",Bezahlt_Datum=\"" + BezahltDatum + "\"";
            }else{
                sqltext = sqltext + ",Bezahlt=NULL"
                            + ",Bezahlt_Datum=NULL";
            }
            sqltext = sqltext + " ,Preisänderung=" + this.jTextFieldPreisAenderung.getText()
                    + " WHERE einheiten_id=" + this.einheit + ";";
        }
        System.out.println("Einheit_einzel.java::jButton2ActionPerformed: \n" + sqltext);
//        System.out.println("jComboBoxAngebot.getSelectedIndex(): " + this.jComboBoxAngebot.getSelectedIndex());
//        System.out.println("indexOF: " + this.angeboteliste.elementAt(this.jComboBoxAngebot.getSelectedIndex()));
        if (!verbindung.sql(sqltext)){
            System.out.println("Sqltext nicht erfolgreich: " + sqltext);
        }
        this.setVisible(false);
        this.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        jFormattedTextFieldStart.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        jFormattedTextFieldStart.setName("jFormattedTextFieldStart"); // NOI18N

        jFormattedTextFieldEnde.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        jFormattedTextFieldEnde.setName("jFormattedTextFieldEnde"); // NOI18N

        jDateChooserDatum.setName("jDateChooserDatum"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
                            .addComponent(jComboBoxAngebot, 0, 331, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldPreisAenderung, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(203, 203, 203))
                            .addComponent(jDateChooserDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jFormattedTextFieldEnde, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jFormattedTextFieldStart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9)))
                                    .addComponent(jDateChooserEingereicht, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldZusatz1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                    .addComponent(jTextFieldZusatz2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jDateChooserBezahlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(262, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxAngebot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooserDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel5)
                    .addComponent(jFormattedTextFieldStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldZusatz1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jFormattedTextFieldEnde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldZusatz2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jTextFieldPreisAenderung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jDateChooserEingereicht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel10)
                        .addComponent(jDateChooserBezahlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //TODO Kontrolle der Eingaben auf vollständigkeit und richtigkeit
        
        saveData();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Fenster schließen
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables

}
