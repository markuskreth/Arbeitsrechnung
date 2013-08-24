/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AngebotDialog.java
 *
 * Created on 29.04.2009, 15:15:15
 */

package arbeitsrechnungen;

/**
 *
 * @author markus
 */
import java.sql.ResultSet;
import arbeitsabrechnungendataclass.Verbindung;


public class AngebotDialog extends javax.swing.JDialog {

    int AngebotID;
    int KlientenID=0;
    Verbindung verbindung;
    java.util.Properties optionen = new java.util.Properties();

    /** Creates new form AngebotDialog 
     *  bei DatensatzID == -1 wird ein neuer erstellt.
     */
    public AngebotDialog(java.awt.Frame parent, int DatensatzID) {
        super(parent, true);
        initComponents();
        this.optionen = getEinstellungen();
        verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));

        // Bei -1 ist es ein neuer Datensatz
        this.AngebotID = DatensatzID;
        //Neuer Datensatz
        if(DatensatzID==-1){
            // Neuer Datensatz - Felder nur mit Probewerten füllen
            this.jTextFieldInhalt.setText("Pflichtfeld");
            this.jTextFieldPreis.setValue(0);
        }else{
        // Vorhandenen Datensatz laden und edieren
            ResultSet Angebot;
            String sqltext = "SELECT angebote_id, klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung  FROM angebote WHERE angebote_id=" + DatensatzID + ";";

            System.out.println("AngebotDialog: " + sqltext);
            Angebot = verbindung.query(sqltext);

            int i=0;
            try{
                Angebot.first();
                this.jTextFieldInhalt.setText(Angebot.getString("Inhalt"));
                this.jTextFieldPreis.setValue(Angebot.getFloat("Preis"));
                this.jTextFieldBeschreibung.setText(Angebot.getString("Beschreibung"));
                if(Angebot.getInt("preis_pro_stunde")==1){
                    this.jCheckBox1.setSelected(true);
                }else{
                    this.jCheckBox1.setSelected(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Creates new form AngebotDialog
     *  mit neuem Datensatz
     */
    public AngebotDialog(java.awt.Frame parent, int AngebotID, int KlientenID) {
        this(parent,-1);
        this.KlientenID = KlientenID;
    }

    private java.util.Properties getEinstellungen(){
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


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldInhalt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPreis = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldBeschreibung = new javax.swing.JTextField();
        jButtonSpeichern = new javax.swing.JButton();
        jButtonVerwerfen = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(arbeitsrechnungen.ArbeitsrechnungenApp.class).getContext().getResourceMap(AngebotDialog.class);
        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jTextFieldInhalt.setText(resourceMap.getString("jTextFieldInhalt.text")); // NOI18N
        jTextFieldInhalt.setName("jTextFieldInhalt"); // NOI18N
        jTextFieldInhalt.setSelectionEnd(jTextFieldInhalt.getText().length());
        jTextFieldInhalt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldInhaltFocusGained(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jTextFieldPreis.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        jTextFieldPreis.setName("jTextFieldPreis"); // NOI18N
        jTextFieldPreis.setSelectionEnd(jTextFieldPreis.getText().length());
        jTextFieldPreis.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldPreisFocusGained(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jTextFieldBeschreibung.setText(resourceMap.getString("jTextFieldBeschreibung.text")); // NOI18N
        jTextFieldBeschreibung.setName("jTextFieldBeschreibung"); // NOI18N
        jTextFieldBeschreibung.setSelectionEnd(jTextFieldBeschreibung.getText().length());
        jTextFieldBeschreibung.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBeschreibungFocusGained(evt);
            }
        });

        jButtonSpeichern.setText(resourceMap.getString("jButtonSpeichern.text")); // NOI18N
        jButtonSpeichern.setName("jButtonSpeichern"); // NOI18N
        jButtonSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSpeichernActionPerformed(evt);
            }
        });

        jButtonVerwerfen.setText(resourceMap.getString("jButtonVerwerfen.text")); // NOI18N
        jButtonVerwerfen.setName("jButtonVerwerfen"); // NOI18N
        jButtonVerwerfen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerwerfenActionPerformed(evt);
            }
        });

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSpeichern)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                        .addComponent(jButtonVerwerfen))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldInhalt)
                                .addComponent(jTextFieldPreis, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldBeschreibung, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBox1))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(54, 54, 54)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldInhalt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldPreis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldBeschreibung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSpeichern)
                    .addComponent(jButtonVerwerfen))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSpeichernActionPerformed
        // TODO Prüfen, ob es eine Zahl ist
        String preis; // SQL-Format für Preis
        preis = this.jTextFieldPreis.getText();
        // Falls ein leerzeichen gefunden wird, nur den vorderen Teil nuten (€)
        preis = preis.substring(0, preis.indexOf(" "));
        preis = preis.replace(",", ".");
        int stundenpreis=0;
        String sqltext;
        if (this.jCheckBox1.isSelected()){
            stundenpreis=1;
        }
        // Bei neuem Datensatz Insert ausführen
        if(AngebotID==-1){
            sqltext = "INSERT INTO angebote (klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung) " +
                    "VALUES (" +
                    this.KlientenID + ","+
                    "\"" + this.jTextFieldInhalt.getText() + "\"," +
                    preis + "," +
                    stundenpreis + ", " +
                    "\"" + this.jTextFieldBeschreibung.getText() + "\");";
        }
        // Bei vorhandenem Datensatz Update ausführen
        else{
        sqltext = "UPDATE angebote SET " +
                "Inhalt=\"" + this.jTextFieldInhalt.getText() + "\", " +
                "Preis=" + preis + ", "  +
                "Beschreibung=\"" + this.jTextFieldBeschreibung.getText() + "\", " +
                "preis_pro_stunde=" + stundenpreis +
                " WHERE angebote_id=" + this.AngebotID + ";";
        }
        System.out.println("AngebotDialog: " + sqltext);
        try{
            verbindung.sql(sqltext);
        }catch (Exception e) {
            System.err.print("verbindung.sql: ");
            e.printStackTrace();
        }
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonSpeichernActionPerformed

    private void jButtonVerwerfenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerwerfenActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonVerwerfenActionPerformed

    private void jTextFieldInhaltFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldInhaltFocusGained
        if (evt.getSource() instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) evt.getSource();
            tf.setSelectionStart(0);
            tf.setSelectionEnd(tf.getText().length());
        }
    }//GEN-LAST:event_jTextFieldInhaltFocusGained

    private void jTextFieldPreisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldPreisFocusGained
        if (evt.getSource() instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) evt.getSource();
            tf.setSelectionStart(0);
            tf.setSelectionEnd(tf.getText().length());
        }
    }//GEN-LAST:event_jTextFieldPreisFocusGained

    private void jTextFieldBeschreibungFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBeschreibungFocusGained
        if (evt.getSource() instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) evt.getSource();
            tf.setSelectionStart(0);
            tf.setSelectionEnd(tf.getText().length());
        }
    }//GEN-LAST:event_jTextFieldBeschreibungFocusGained

    /**
    * @param args the command line arguments
    */
/*    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AngebotDialog dialog = new AngebotDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSpeichern;
    private javax.swing.JButton jButtonVerwerfen;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextFieldBeschreibung;
    private javax.swing.JTextField jTextFieldInhalt;
    private javax.swing.JFormattedTextField jTextFieldPreis;
    // End of variables declaration//GEN-END:variables

}
