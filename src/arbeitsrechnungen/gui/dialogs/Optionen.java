/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Optionen.java
 *
 * Created on 23.08.2009, 17:12:27
 */

package arbeitsrechnungen.gui.dialogs;

/**
 *
 * @author markus
 */

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class Optionen extends javax.swing.JDialog {

	private static final long serialVersionUID = -527076543127705929L;

	Properties einstellungen = new Properties();
    
    final String programmverzeichnis = ".arbeitrechnungen";
    File optiondatei;
    boolean firststart;

    /** Creates new form Optionen */
    public Optionen(javax.swing.JFrame parent, boolean firststart) {
        super(parent, true);
        this.firststart = firststart;
        initComponents();
        java.util.Properties sysprops = System.getProperties();
        String homedir = sysprops.getProperty("user.home");
        optiondatei = new java.io.File(homedir + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");
        try{
            einstellungen.load(new java.io.FileInputStream(optiondatei));
        }catch(Exception e){
            System.err.println("Optionen.java: Options-Datei konnte nicht geladen werden.");
        }
        if(!firststart){
            loadoptions();
        }
    }

    private void setTexts(javax.swing.JComponent component, String propname){
        for(int i=0;i<component.getComponentCount();i++){
            if(component.getComponent(i) instanceof javax.swing.JPanel || component.getComponent(i) instanceof javax.swing.JTabbedPane){
                System.out.println(propname + ": " + component.getComponent(i).getClass() +
                        "(" + component.getComponent(i).getName() + ") " + "--> setTexts");
                setTexts((javax.swing.JComponent)component.getComponent(i), propname);
            }
            if(component.getComponent(i) instanceof javax.swing.JTextField){
                System.out.println(((javax.swing.JTextField)component.getComponent(i)).getName());
                if(((javax.swing.JTextField)component.getComponent(i)).getName().matches(propname)){
                    ((javax.swing.JTextField)component.getComponent(i)).setText(einstellungen.getProperty(propname));
                    i=component.getComponentCount();
                }
            }
        }
    }

    private void loadoptions(){
        System.out.println("Optionen werden geladen...");
        Enumeration<?> propnames = einstellungen.propertyNames();

        // für jedes Property zugehöriges Textfeld 
        while (propnames.hasMoreElements()) {
            String propname = (String)propnames.nextElement();
            setTexts((javax.swing.JComponent)this.getContentPane(),propname);
        }
    }

    private void getTexts(javax.swing.JComponent component){
        for(int i=0;i<component.getComponentCount();i++){
            if(component.getComponent(i) instanceof javax.swing.JComponent){
//                System.out.println(component.getComponent(i).getClass() + "--> setTexts");
                getTexts((javax.swing.JComponent)component.getComponent(i));
            }
            if(component.getComponent(i) instanceof javax.swing.JTextField){
                einstellungen.setProperty(((javax.swing.JTextField)component.getComponent(i)).getName(),
                        ((javax.swing.JTextField)component.getComponent(i)).getText());
                System.out.println(((javax.swing.JTextField)component.getComponent(i)).getName());
            }
        }
    }

    private void saveoptions(){
        getTexts((javax.swing.JComponent)this.getContentPane());
        try{
            einstellungen.store(new java.io.FileOutputStream(optiondatei),"Eigene Optionen");
        }catch(Exception e){
            System.err.println("Optionen.java: Options-Datei konnte nicht gespeichert werden.");
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelDatenbank = new javax.swing.JPanel();
        jTextFieldSqlServer = new javax.swing.JTextField();
        jTextFieldUser = new javax.swing.JTextField();
        jTextFieldDatenbank = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPasswordFieldPassword = new javax.swing.JPasswordField();
        jPanelPfade = new javax.swing.JPanel();
        jTextFieldArbeitsverzeichnis = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldVerzeichnisTexDateien = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButtonArbVerz = new javax.swing.JButton();
        jButtonTexVerz = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldStdTexDatei = new javax.swing.JTextField();
        jButtonTexDatei = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldPdfProg = new javax.swing.JTextField();
        jButtonDialogPdfViewer = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldPdfFiles = new javax.swing.JTextField();
        jButtonDialogPdfVerz = new javax.swing.JButton();
        jPanelEinheitenArten = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        ResourceBundle resourceMap = ResourceBundle.getBundle(getClass().getSimpleName());
        
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanelDatenbank.setName("jPanelDatenbank"); // NOI18N

        jTextFieldSqlServer.setText(resourceMap.getString("sqlserver.text")); // NOI18N
        jTextFieldSqlServer.setName("sqlserver"); // NOI18N

        jTextFieldUser.setText(resourceMap.getString("user.text")); // NOI18N
        jTextFieldUser.setName("user"); // NOI18N

        jTextFieldDatenbank.setText(resourceMap.getString("datenbank.text")); // NOI18N
        jTextFieldDatenbank.setName("datenbank"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jPasswordFieldPassword.setText(resourceMap.getString("password.text")); // NOI18N
        jPasswordFieldPassword.setName("password"); // NOI18N

        javax.swing.GroupLayout jPanelDatenbankLayout = new javax.swing.GroupLayout(jPanelDatenbank);
        jPanelDatenbank.setLayout(jPanelDatenbankLayout);
        jPanelDatenbankLayout.setHorizontalGroup(
            jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDatenbankLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldDatenbank)
                    .addComponent(jPasswordFieldPassword)
                    .addComponent(jTextFieldUser)
                    .addComponent(jTextFieldSqlServer, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelDatenbankLayout.setVerticalGroup(
            jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDatenbankLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSqlServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDatenbankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDatenbank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(119, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelDatenbank.TabConstraints.tabTitle"), jPanelDatenbank); // NOI18N

        jPanelPfade.setName("jPanelPfade"); // NOI18N

        jTextFieldArbeitsverzeichnis.setText(resourceMap.getString("arbeitsverzeichnis.text")); // NOI18N
        jTextFieldArbeitsverzeichnis.setName("arbeitsverzeichnis"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N


        jTextFieldVerzeichnisTexDateien.setText(resourceMap.getString("verzeichnistexdateien.text")); // NOI18N
        jTextFieldVerzeichnisTexDateien.setName("verzeichnistexdateien"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        
        jButtonArbVerz.setText(resourceMap.getString("jButtonArbVerz.text")); // NOI18N
        jButtonArbVerz.setName("jButtonArbVerz"); // NOI18N
        jButtonArbVerz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonArbVerzActionPerformed(evt);
            }
        });

        jButtonTexVerz.setText(resourceMap.getString("jButtonTexVerz.text")); // NOI18N
        jButtonTexVerz.setName("jButtonTexVerz"); // NOI18N
        jButtonTexVerz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTexVerzActionPerformed(evt);
            }
        });

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jTextFieldStdTexDatei.setText(resourceMap.getString("stdtexdatei.text")); // NOI18N
        jTextFieldStdTexDatei.setName("stdtexdatei"); // NOI18N

        jButtonTexDatei.setText(resourceMap.getString("jButtonTexDatei.text")); // NOI18N
        jButtonTexDatei.setName("jButtonTexDatei"); // NOI18N
        jButtonTexDatei.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTexDateiActionPerformed(evt);
            }
        });

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jTextFieldPdfProg.setText(resourceMap.getString("pdfprogramm.text")); // NOI18N
        jTextFieldPdfProg.setName("pdfprogramm"); // NOI18N

        jButtonDialogPdfViewer.setText(resourceMap.getString("jButtonDialogPdfViewer.text")); // NOI18N
        jButtonDialogPdfViewer.setName("jButtonDialogPdfViewer"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jTextFieldPdfFiles.setName("verzPdfFiles"); // NOI18N

        jButtonDialogPdfVerz.setText(resourceMap.getString("jButtonDialogPdfVerz.text")); // NOI18N
        jButtonDialogPdfVerz.setName("jButtonDialogPdfVerz"); // NOI18N
        jButtonDialogPdfVerz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDialogPdfVerzActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPfadeLayout = new javax.swing.GroupLayout(jPanelPfade);
        jPanelPfade.setLayout(jPanelPfadeLayout);
        jPanelPfadeLayout.setHorizontalGroup(
            jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPfadeLayout.createSequentialGroup()
                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPfadeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPdfProg, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDialogPdfViewer, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPfadeLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelPfadeLayout.createSequentialGroup()
                                .addComponent(jTextFieldPdfFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDialogPdfVerz, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelPfadeLayout.createSequentialGroup()
                                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldVerzeichnisTexDateien, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                    .addComponent(jTextFieldArbeitsverzeichnis, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                    .addComponent(jTextFieldStdTexDatei, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButtonTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE)
                                    .addComponent(jButtonArbVerz, javax.swing.GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE)
                                    .addComponent(jButtonTexVerz, javax.swing.GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        jPanelPfadeLayout.setVerticalGroup(
            jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPfadeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldArbeitsverzeichnis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonArbVerz, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldVerzeichnisTexDateien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonTexVerz, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldStdTexDatei, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPdfFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDialogPdfVerz, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanelPfadeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPdfProg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDialogPdfViewer, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelPfade.TabConstraints.tabTitle"), jPanelPfade); // NOI18N

        jPanelEinheitenArten.setName("jPanelEinheitenArten"); // NOI18N

        javax.swing.GroupLayout jPanelEinheitenArtenLayout = new javax.swing.GroupLayout(jPanelEinheitenArten);
        jPanelEinheitenArten.setLayout(jPanelEinheitenArtenLayout);
        jPanelEinheitenArtenLayout.setHorizontalGroup(
            jPanelEinheitenArtenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );
        jPanelEinheitenArtenLayout.setVerticalGroup(
            jPanelEinheitenArtenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelEinheitenArten.TabConstraints.tabTitle"), jPanelEinheitenArten); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        this.dispose();
        if(firststart){
            System.out.println("Progamm wird beendet. Optionen nicht gespeichert.");
            System.exit(-1);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        saveoptions();
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonArbVerzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonArbVerzActionPerformed
        String arb_verz = this.jTextFieldArbeitsverzeichnis.getText();
        java.awt.FileDialog verz_name = new java.awt.FileDialog(this, "Tex-Verzeichnis");
        if(arb_verz.length()>0) verz_name.setDirectory(arb_verz);
        verz_name.setVisible(true);
        this.jTextFieldArbeitsverzeichnis.setText(verz_name.getDirectory());
        verz_name.dispose();
    }//GEN-LAST:event_jButtonArbVerzActionPerformed

    private void jButtonTexVerzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTexVerzActionPerformed
        // 
        String tex_verz = this.jTextFieldVerzeichnisTexDateien.getText();
        java.awt.FileDialog verz_name = new java.awt.FileDialog(this, "Tex-Verzeichnis");
        if(tex_verz.length()>0) verz_name.setDirectory(tex_verz);
        verz_name.setVisible(true);
        this.jTextFieldVerzeichnisTexDateien.setText(verz_name.getDirectory());
        verz_name.dispose();
    }//GEN-LAST:event_jButtonTexVerzActionPerformed

    private void jButtonTexDateiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTexDateiActionPerformed
        String tex_datei = jTextFieldStdTexDatei.getText();
        java.awt.FileDialog dateiname = new java.awt.FileDialog(this, "Tex-Datei", java.awt.FileDialog.LOAD);
        int endindex = tex_datei.lastIndexOf("/");
        if(endindex>0){
            dateiname.setDirectory(tex_datei.substring(0, endindex));
            dateiname.setFile(tex_datei.substring(endindex, tex_datei.length()));
        }else{
            if(tex_datei.length()>0){
                dateiname.setFile(tex_datei);
            }
        }

        dateiname.setFilenameFilter(new java.io.FilenameFilter() {

            public boolean accept(File dir, String name) {
                if(name.endsWith(".tex")) return true;
                else {
                    return false;
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        });

        dateiname.setVisible(true);
        if(dateiname.getFile()!= null){
            tex_datei = dateiname.getDirectory() + dateiname.getFile();
            this.jTextFieldStdTexDatei.setText(tex_datei);
        }
        dateiname.dispose();

    }//GEN-LAST:event_jButtonTexDateiActionPerformed

    private void jButtonDialogPdfVerzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDialogPdfVerzActionPerformed
        // TODO JFileChooser einrichten! - Verzeichnis akzeptieren, Dateien nicht anzeigen
        java.io.File pdf_verz = new java.io.File(this.jTextFieldPdfFiles.getText());
        javax.swing.JFileChooser verz_name = new javax.swing.JFileChooser();
        if(pdf_verz.length()>0) verz_name.setCurrentDirectory(pdf_verz);

//        java.awt.FileDialog verz_name = new java.awt.FileDialog(this, "Verzeichnis für Pdf-Dateien");
//        if(pdf_verz.length()>0) verz_name.setDirectory(pdf_verz);
//        verz_name.setVisible(true);
        verz_name.showOpenDialog(this);

        this.jTextFieldPdfFiles.setText("");
    }//GEN-LAST:event_jButtonDialogPdfVerzActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Optionen(new javax.swing.JFrame(),false).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonArbVerz;
    private javax.swing.JButton jButtonDialogPdfVerz;
    private javax.swing.JButton jButtonDialogPdfViewer;
    private javax.swing.JButton jButtonTexDatei;
    private javax.swing.JButton jButtonTexVerz;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelDatenbank;
    private javax.swing.JPanel jPanelEinheitenArten;
    private javax.swing.JPanel jPanelPfade;
    private javax.swing.JPasswordField jPasswordFieldPassword;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldArbeitsverzeichnis;
    private javax.swing.JTextField jTextFieldDatenbank;
    private javax.swing.JTextField jTextFieldPdfFiles;
    private javax.swing.JTextField jTextFieldPdfProg;
    private javax.swing.JTextField jTextFieldSqlServer;
    private javax.swing.JTextField jTextFieldStdTexDatei;
    private javax.swing.JTextField jTextFieldUser;
    private javax.swing.JTextField jTextFieldVerzeichnisTexDateien;
    // End of variables declaration//GEN-END:variables

}
