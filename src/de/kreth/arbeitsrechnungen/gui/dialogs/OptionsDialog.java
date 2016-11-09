package de.kreth.arbeitsrechnungen.gui.dialogs;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import de.kreth.arbeitsrechnungen.Options;

public class OptionsDialog extends JDialog {

   private static final long serialVersionUID = -527076543127705929L;

   private Logger logger = Logger.getLogger(getClass());

   private Properties einstellungen = new Properties();

   private File optiondatei;

   private final JPanel contentPanel = new JPanel();
   private JTextField txtDbServer;
   private JLabel lblNewLabel;
   private JTextField txtDbBenutzer;
   private JTextField txtDbDatabase;
   private JPasswordField txtpassword;
   private JTextField txtArbeitsverzeichnis;
   private JTextField txtTexDir;
   private JTextField txtDefaultTexFile;
   private JTextField txtOutputDir;
   private JTextField txtPdfViewer;

   /**
    * Launch the application.
    */
   public static void main(String[] args) {
      try {
         OptionsDialog dialog = new OptionsDialog();
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.setVisible(true);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Create the dialog.
    */
   public OptionsDialog() {
      setTitle("Optionen");
      setBounds(100, 100, 450, 300);
      getContentPane().setLayout(new BorderLayout());
      contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

      getContentPane().add(contentPanel, BorderLayout.CENTER);
      contentPanel.setLayout(new BorderLayout(0, 0));
      {
         JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
         contentPanel.add(tabbedPane, BorderLayout.NORTH);
         {
            JPanel panel = new JPanel();
            tabbedPane.addTab("Datenbank", null, panel, null);
            panel.setLayout(new GridLayout(4, 2, 0, 0));
            {
               lblNewLabel = new JLabel("Server");
               panel.add(lblNewLabel);
            }
            {
               txtDbServer = new JTextField();
               txtDbServer.setName("sqlserver");
               lblNewLabel.setLabelFor(txtDbServer);
               panel.add(txtDbServer);
               txtDbServer.setColumns(10);
            }
            {
               JLabel lblBenutzer = new JLabel("Benutzer");
               panel.add(lblBenutzer);
            }
            {
               txtDbBenutzer = new JTextField();
               txtDbBenutzer.setName("user");
               panel.add(txtDbBenutzer);
               txtDbBenutzer.setColumns(10);
            }
            {
               JLabel lblNewLabel_1 = new JLabel("Password");
               panel.add(lblNewLabel_1);
            }
            {
               txtpassword = new JPasswordField();
               txtpassword.setName("password");
               panel.add(txtpassword);
            }
            {
               JLabel lblNewLabel_2 = new JLabel("DatenbankName");
               panel.add(lblNewLabel_2);
            }
            {
               txtDbDatabase = new JTextField();
               txtDbDatabase.setName("datenbank");
               panel.add(txtDbDatabase);
               txtDbDatabase.setColumns(10);
            }
         }
         {
            JPanel panel_1 = new JPanel();
            tabbedPane.addTab("Pfade", null, panel_1, null);
            panel_1.setLayout(new GridLayout(0, 2, 0, 0));
            {
               JLabel lblArbeitsverzeichnis = new JLabel("Arbeitsverzeichnis");
               panel_1.add(lblArbeitsverzeichnis);
            }
            {
               txtArbeitsverzeichnis = new JTextField();
               txtArbeitsverzeichnis.setName("arbeitsverzeichnis");
               panel_1.add(txtArbeitsverzeichnis);
               txtArbeitsverzeichnis.setColumns(10);
            }
            {
               JLabel lblVerzeichnisTexvorlagen = new JLabel("Verzeichnis Tex-Vorlagen");
               panel_1.add(lblVerzeichnisTexvorlagen);
            }
            {
               txtTexDir = new JTextField();
               txtTexDir.setName("verzeichnistexdateien");
               panel_1.add(txtTexDir);
               txtTexDir.setColumns(10);
            }
            {
               JLabel lblStandardTexdatei = new JLabel("Standard Tex-Datei");
               panel_1.add(lblStandardTexdatei);
            }
            {
               txtDefaultTexFile = new JTextField();
               txtDefaultTexFile.setName("stdtexdatei");
               panel_1.add(txtDefaultTexFile);
               txtDefaultTexFile.setColumns(10);
            }
            {
               JLabel lblAusgabeverzeichnis = new JLabel("Ausgabeverzeichnis");
               panel_1.add(lblAusgabeverzeichnis);
            }
            {
               txtOutputDir = new JTextField();
               panel_1.add(txtOutputDir);
               txtOutputDir.setColumns(10);
            }
            {
               JLabel lblPdfAnzeige = new JLabel("Pdf Anzeige");
               panel_1.add(lblPdfAnzeige);
            }
            {
               txtPdfViewer = new JTextField();
               txtPdfViewer.setName("pdfprogramm");
               panel_1.add(txtPdfViewer);
               txtPdfViewer.setColumns(10);
            }
         }
         {
            JPanel panel = new JPanel();
            tabbedPane.addTab("EinheitenArten", null, panel, null);
         }
      }

      {
         JPanel buttonPane = new JPanel();
         buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
         getContentPane().add(buttonPane, BorderLayout.SOUTH);
         {
            JButton okButton = new JButton("OK");
            okButton.setActionCommand("OK");
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);
         }
         {
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setActionCommand("Cancel");
            buttonPane.add(cancelButton);
         }
      }
      loadoptions();
   }

   private void loadoptions() {

      Properties sysprops = System.getProperties();
      String homedir = sysprops.getProperty("user.home");
      optiondatei = new File(homedir + sysprops.getProperty("file.separator") + Options.BENUTZERVERZEICHNIS + sysprops.getProperty("file.separator") + "optionen.ini");
      try {
         einstellungen.load(new FileInputStream(optiondatei));
      } catch (Exception e) {
         logger.error("Optionen.java: Options-Datei konnte nicht geladen werden.", e);
      }

      logger.debug("Optionen werden geladen...");
      Enumeration<?> propnames = einstellungen.propertyNames();

      // für jedes Property zugehöriges Textfeld
      while (propnames.hasMoreElements()) {
         String propname = (String) propnames.nextElement();
         setTexts((JComponent) this.getContentPane(), propname);
      }
   }

   private void setTexts(JComponent component, String propname) {

      for (int i = 0; i < component.getComponentCount(); i++) {

         Component comp = component.getComponent(i);

         if (comp instanceof JPanel || comp instanceof JTabbedPane) {
            // logger.debug(propname + ": " + comp.getClass() + "(" +
            // comp.getName() + ") " + "--> setTexts");
            setTexts((JComponent) comp, propname);
         }

         if (comp instanceof JTextField) {
            logger.debug(((JTextField) comp).getName());
            if (((JTextField) comp).getName() != null && ((JTextField) comp).getName().matches(propname)) {
               ((JTextField) comp).setText(einstellungen.getProperty(propname));
               return;
            }
         }
      }
   }

   @Override
   public void dispose() {

      super.dispose();
   }
}
