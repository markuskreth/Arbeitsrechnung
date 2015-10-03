/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Optionen.java
 * Created on 23.08.2009, 17:12:27
 */

package de.kreth.arbeitsrechnungen.gui.dialogs;

/**
 * @author markus
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class OptionenDialog extends JDialog {

   private static final long serialVersionUID = -527076543127705929L;

   private Logger logger = Logger.getLogger(getClass());

   private Properties einstellungen = new Properties();

   private final String programmverzeichnis = ".arbeitrechnungen";

   private File optiondatei;
   private boolean firststart;

   private JLabel jLabelLogLevel;

   private JPanel jPanelSettings;

   private JTextField jTextFieldLogLevel;

   /**
    * Launch the Dialog.
    */
   public static void main(String[] args) {
      try {
         OptionenDialog dialog = new OptionenDialog();
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.setVisible(true);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public OptionenDialog() {
      super();

      this.firststart = false;
      initComponents();
      Properties sysprops = System.getProperties();
      String homedir = sysprops.getProperty("user.home");
      optiondatei = new File(homedir + sysprops.getProperty("file.separator") + programmverzeichnis + sysprops.getProperty("file.separator") + "optionen.ini");
      try {
         einstellungen.load(new FileInputStream(optiondatei));
      } catch (Exception e) {
         logger.error("Optionen.java: Options-Datei konnte nicht geladen werden.", e);
      }
      if (!firststart) {
         loadoptions();
      }
   }

   /** Creates new form Optionen */
   public OptionenDialog(JFrame parent, boolean firststart) {
      super(parent, true);
      this.firststart = firststart;
      initComponents();
      Properties sysprops = System.getProperties();
      String homedir = sysprops.getProperty("user.home");
      optiondatei = new File(homedir + sysprops.getProperty("file.separator") + programmverzeichnis + sysprops.getProperty("file.separator") + "optionen.ini");
      try {
         einstellungen.load(new FileInputStream(optiondatei));
      } catch (Exception e) {
         logger.error("Optionen.java: Options-Datei konnte nicht geladen werden.", e);
      }
      if (!firststart) {
         loadoptions();
      }
   }

   private void loadoptions() {
      logger.debug("Optionen werden geladen...");

      Container contentPane = this.getContentPane();
      Map<String, JTextField> components = new HashMap<>();

      fillComponentMap(components, contentPane);

      Enumeration<?> propnames = einstellungen.propertyNames();

      // für jedes Property zugehöriges Textfeld
      while (propnames.hasMoreElements()) {
         String propname = (String) propnames.nextElement();

         Component comp = components.get(propname);

         if (comp instanceof JTextField) {
            if (((JTextField) comp).getName().matches(propname)) {
               ((JTextField) comp).setText(einstellungen.getProperty(propname));
            }
         }
      }
      
      Level level = logger.getLevel();
      if(level == null)
         level = logger.getEffectiveLevel();
      
      jTextFieldLogLevel.setText(level.toString());
   }

   private void fillComponentMap(Map<String, JTextField> components, Container contentPane) {

      for (int i = 0, count = contentPane.getComponentCount(); i < count; i++) {

         Component comp = contentPane.getComponent(i);

         if (comp instanceof JPanel || comp instanceof JTabbedPane) {
            fillComponentMap(components, (Container) comp);
         } else if (comp instanceof JTextField)
            components.put(comp.getName(), (JTextField) comp);
      }
   }

   private void getTexts(JComponent component) {
      for (int i = 0; i < component.getComponentCount(); i++) {
         if (component.getComponent(i) instanceof JComponent) {
            // logger.debug(component.getComponent(i).getClass() +
            // "--> setTexts");
            getTexts((JComponent) component.getComponent(i));
         }
         if (component.getComponent(i) instanceof JTextField) {
            einstellungen.setProperty(((JTextField) component.getComponent(i)).getName(), ((JTextField) component.getComponent(i)).getText());
            logger.debug(((JTextField) component.getComponent(i)).getName());
         }
      }
   }

   private void saveoptions() {
      getTexts((JComponent) this.getContentPane());
      try {
         einstellungen.store(new FileOutputStream(optiondatei), "Eigene Optionen");
      } catch (Exception e) {
         System.err.println("Optionen.java: Options-Datei konnte nicht gespeichert werden.");
         e.printStackTrace();
      }
   }

   /**
    * This method is called from within the constructor to
    * initialize the form.
    */
   private void initComponents() {

      jTabbedPane1 = new JTabbedPane();
      jPanelDatenbank = new JPanel();
      jTextFieldSqlServer = new JTextField();
      jTextFieldUser = new JTextField();
      jTextFieldDatenbank = new JTextField();
      jLabel3 = new JLabel();
      jLabel4 = new JLabel();
      jLabel5 = new JLabel();
      jLabel6 = new JLabel();
      jPasswordFieldPassword = new JPasswordField();
      jPanelPfade = new JPanel();
      jTextFieldArbeitsverzeichnis = new JTextField();
      jLabel1 = new JLabel();
      jTextFieldVerzeichnisTexDateien = new JTextField();
      jLabel2 = new JLabel();
      jButtonArbVerz = new JButton();
      jButtonTexVerz = new JButton();
      jLabel7 = new JLabel();
      jTextFieldStdTexDatei = new JTextField();
      jButtonTexDatei = new JButton();
      jLabel8 = new JLabel();
      jTextFieldPdfProg = new JTextField();
      jButtonDialogPdfViewer = new JButton();
      jLabel9 = new JLabel();
      jTextFieldPdfFiles = new JTextField();
      jButtonDialogPdfVerz = new JButton();
      jPanelEinheitenArten = new JPanel();
      jButton1 = new JButton();
      jButton2 = new JButton();

      jPanelSettings = new JPanel();
      jLabelLogLevel = new JLabel();
      jTextFieldLogLevel = new JTextField();
      jTextFieldLogLevel.setEditable(false);
      jTextFieldLogLevel.addMouseListener(new MouseAdapter() {
         
         @Override
         public void mouseClicked(MouseEvent e) {

            logger.info("Neue Logger haben Level: " + Logger.getLogger("TestLoger").getEffectiveLevel());
            Level[] alle = {Level.ALL, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF};
            Level selected = (Level) JOptionPane.showInputDialog(null, "Level zur Auswahl:", "Setzen des Loglevels", JOptionPane.QUESTION_MESSAGE, null, alle, logger.getEffectiveLevel());
            Logger.getRootLogger().setLevel(selected);
            logger.setLevel(selected);
            jTextFieldLogLevel.setText(selected.toString());
            logger.info("Neue Logger haben nun Level: " + Logger.getLogger("TestLoger").getEffectiveLevel());
         }
      });
      
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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

      jLabelLogLevel.setText(resourceMap.getString("jLabelLogLevel.text"));
      jLabelLogLevel.setName("jLabelLogLevel");
      
      GroupLayout jPanelDatenbankLayout = new GroupLayout(jPanelDatenbank);
      jPanelDatenbank.setLayout(jPanelDatenbankLayout);
      jPanelDatenbankLayout.setHorizontalGroup(jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanelDatenbankLayout
                  .createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jLabel3).addComponent(jLabel4).addComponent(jLabel5)
                              .addComponent(jLabel6))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(jTextFieldDatenbank).addComponent(jPasswordFieldPassword)
                              .addComponent(jTextFieldUser).addComponent(jTextFieldSqlServer, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
                  .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
      jPanelDatenbankLayout.setVerticalGroup(jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanelDatenbankLayout
                  .createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldSqlServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel3))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel4))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel5)
                              .addComponent(jPasswordFieldPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelDatenbankLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldDatenbank, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel6))
                  .addContainerGap(119, Short.MAX_VALUE)));

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

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonArbVerzActionPerformed(evt);
         }
      });

      jButtonTexVerz.setText(resourceMap.getString("jButtonTexVerz.text")); // NOI18N
      jButtonTexVerz.setName("jButtonTexVerz"); // NOI18N
      jButtonTexVerz.addActionListener(new java.awt.event.ActionListener() {

         @Override
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

         @Override
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

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonDialogPdfVerzActionPerformed(evt);
         }
      });

      GroupLayout jPanelPfadeLayout = new GroupLayout(jPanelPfade);
      jPanelPfade.setLayout(jPanelPfadeLayout);
      jPanelPfadeLayout.setHorizontalGroup(jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(
                  jPanelPfadeLayout
                        .createSequentialGroup()
                        .addGroup(
                              jPanelPfadeLayout
                                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(
                                          jPanelPfadeLayout.createSequentialGroup().addContainerGap().addComponent(jLabel8).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldPdfProg, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonDialogPdfViewer, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(
                                          jPanelPfadeLayout
                                                .createSequentialGroup()
                                                .addGap(30, 30, 30)
                                                .addGroup(
                                                      jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jLabel1).addComponent(jLabel2)
                                                            .addComponent(jLabel7).addComponent(jLabel9))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(
                                                      jPanelPfadeLayout
                                                            .createParallelGroup(GroupLayout.Alignment.LEADING)
                                                            .addGroup(
                                                                  jPanelPfadeLayout.createSequentialGroup()
                                                                        .addComponent(jTextFieldPdfFiles, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jButtonDialogPdfVerz, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                                                            .addGroup(
                                                                  jPanelPfadeLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                              jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                    .addComponent(jTextFieldVerzeichnisTexDateien, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                                                    .addComponent(jTextFieldArbeitsverzeichnis, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                                                                    .addComponent(jTextFieldStdTexDatei, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                              jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                                    .addComponent(jButtonTexDatei, GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE)
                                                                                    .addComponent(jButtonArbVerz, GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE)
                                                                                    .addComponent(jButtonTexVerz, GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE))))))
                        .addContainerGap()));
      jPanelPfadeLayout.setVerticalGroup(jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            jPanelPfadeLayout
                  .createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldArbeitsverzeichnis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel1)
                              .addComponent(jButtonArbVerz, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldVerzeichnisTexDateien, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jLabel2).addComponent(jButtonTexVerz, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jButtonTexDatei, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jTextFieldStdTexDatei, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel7))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                        jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldPdfFiles, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jButtonDialogPdfVerz, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE).addComponent(jLabel9))
                  .addGap(18, 18, 18)
                  .addGroup(
                        jPanelPfadeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(jTextFieldPdfProg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(jButtonDialogPdfViewer, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE).addComponent(jLabel8))
                  .addContainerGap(82, Short.MAX_VALUE)));

      jTabbedPane1.addTab(resourceMap.getString("jPanelPfade.TabConstraints.tabTitle"), jPanelPfade); // NOI18N

      jPanelEinheitenArten.setName("jPanelEinheitenArten"); // NOI18N

      GroupLayout jPanelEinheitenArtenLayout = new GroupLayout(jPanelEinheitenArten);
      jPanelEinheitenArten.setLayout(jPanelEinheitenArtenLayout);
      jPanelEinheitenArtenLayout.setHorizontalGroup(jPanelEinheitenArtenLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 371, Short.MAX_VALUE));
      jPanelEinheitenArtenLayout.setVerticalGroup(jPanelEinheitenArtenLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 225, Short.MAX_VALUE));

      jTabbedPane1.addTab(resourceMap.getString("jPanelEinheitenArten.TabConstraints.tabTitle"), jPanelEinheitenArten); // NOI18N

      jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
      jButton1.setName("jButton1"); // NOI18N
      jButton1.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
      jButton2.setName("jButton2"); // NOI18N
      jButton2.addActionListener(new java.awt.event.ActionListener() {

         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
         }
      });

      GroupLayout layout = new GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                              .addGroup(
                                    GroupLayout.Alignment.TRAILING,
                                    layout.createSequentialGroup().addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                              .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
            GroupLayout.Alignment.TRAILING,
            layout.createSequentialGroup().addContainerGap().addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButton1).addComponent(jButton2)).addContainerGap()));

      pack();
      
      jPanelSettings.setLayout(new GridLayout(1,2,15,15));
      jPanelSettings.add(jLabelLogLevel);
      jPanelSettings.add(jTextFieldLogLevel);
      
      jTabbedPane1.addTab(resourceMap.getString("jPanelSettings.TabConstraints.tabTitle"), jPanelSettings);
   }

   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
      if (firststart) {
         logger.debug("Progamm wird beendet. Optionen nicht gespeichert.");
         System.exit(-1);
      }
   }

   private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
      saveoptions();
      this.setVisible(false);
      this.dispose();
   }

   private void jButtonArbVerzActionPerformed(java.awt.event.ActionEvent evt) {
      String arb_verz = this.jTextFieldArbeitsverzeichnis.getText();

      File file = openFileChooserForDirectory(arb_verz, "Arbeitsverzeichnis");
      if (file != null)
         this.jTextFieldArbeitsverzeichnis.setText(file.getAbsolutePath());
      else
         this.jTextFieldArbeitsverzeichnis.setText("");

   }

   private void jButtonTexVerzActionPerformed(java.awt.event.ActionEvent evt) {

      String tex_verz = this.jTextFieldVerzeichnisTexDateien.getText();
      File file = openFileChooserForDirectory(tex_verz, "Tex-Verzeichnis");
      if (file != null)
         this.jTextFieldVerzeichnisTexDateien.setText(file.getAbsolutePath());
      else
         this.jTextFieldVerzeichnisTexDateien.setText("");
   }

   private void jButtonTexDateiActionPerformed(java.awt.event.ActionEvent evt) {
      String tex_datei = jTextFieldStdTexDatei.getText();
      java.awt.FileDialog dateiname = new java.awt.FileDialog(this, "Tex-Datei", java.awt.FileDialog.LOAD);
      int endindex = tex_datei.lastIndexOf("/");
      if (endindex > 0) {
         dateiname.setDirectory(tex_datei.substring(0, endindex));
         dateiname.setFile(tex_datei.substring(endindex, tex_datei.length()));
      } else {
         if (tex_datei.length() > 0) {
            dateiname.setFile(tex_datei);
         }
      }

      dateiname.setFilenameFilter(new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            if (name.endsWith(".tex"))
               return true;
            else {
               return false;
               // throw new UnsupportedOperationException("Not supported yet.");
            }
         }
      });

      dateiname.setVisible(true);
      if (dateiname.getFile() != null) {
         tex_datei = dateiname.getDirectory() + dateiname.getFile();
         this.jTextFieldStdTexDatei.setText(tex_datei);
      }
      dateiname.dispose();

   }

   private void jButtonDialogPdfVerzActionPerformed(java.awt.event.ActionEvent evt) {

      String pdfVerzName = this.jTextFieldPdfFiles.getText();
      File selectedFile = openFileChooserForDirectory(pdfVerzName, "pdf-Verzeichnis");

      if (selectedFile != null)
         this.jTextFieldPdfFiles.setText(selectedFile.getAbsolutePath());
      else
         this.jTextFieldPdfFiles.setText("");
   }

   private File openFileChooserForDirectory(String preSelectedFileName, String titel) {

      File pdf_verz = new File(preSelectedFileName);

      if (preSelectedFileName.length() <= 0 || !pdf_verz.exists())
         pdf_verz = new File(".");

      JFileChooser verzFileChooser = new JFileChooser();
      verzFileChooser.setName(titel);
      FileFilter filter = new DirectoryFileFilter();
      verzFileChooser.setFileFilter(filter);
      verzFileChooser.setAcceptAllFileFilterUsed(false);
      verzFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      verzFileChooser.setCurrentDirectory(pdf_verz);

      // java.awt.FileDialog verz_name = new java.awt.FileDialog(this,
      // "Verzeichnis für Pdf-Dateien");
      // if(pdf_verz.length()>0) verz_name.setDirectory(pdf_verz);
      // verz_name.setVisible(true);

      int showOpenDialog = verzFileChooser.showDialog(this, titel);
      File selectedFile;
      if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
         selectedFile = verzFileChooser.getSelectedFile();
      } else {
         selectedFile = null;
      }
      return selectedFile;
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private JButton jButton1;
   private JButton jButton2;
   private JButton jButtonArbVerz;
   private JButton jButtonDialogPdfVerz;
   private JButton jButtonDialogPdfViewer;
   private JButton jButtonTexDatei;
   private JButton jButtonTexVerz;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JPanel jPanelDatenbank;
   private JPanel jPanelEinheitenArten;
   private JPanel jPanelPfade;
   private JPasswordField jPasswordFieldPassword;
   private JTabbedPane jTabbedPane1;
   private JTextField jTextFieldArbeitsverzeichnis;
   private JTextField jTextFieldDatenbank;
   private JTextField jTextFieldPdfFiles;
   private JTextField jTextFieldPdfProg;
   private JTextField jTextFieldSqlServer;
   private JTextField jTextFieldStdTexDatei;
   private JTextField jTextFieldUser;
   private JTextField jTextFieldVerzeichnisTexDateien;
   // End of variables declaration//GEN-END:variables

}
