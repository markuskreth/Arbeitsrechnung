package de.kreth.arbeitsrechnungen;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kreth.arbeitsrechnungen.gui.dialogs.OptionenDialog;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.Persister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;

/**
 * Stellt die Produktive ArbeitRechnungFactory zur Verfügung.
 * <P>
 * {@link #init()} initialisiert {@link ArbeitRechnungFactory} nur, wenn es
 * nicht bereits initialisiert ist!
 * 
 * @author markus
 */
public class ArbeitRechnungFactoryProductiv extends ArbeitRechnungFactory {

   private Logger logger;

   protected ArbeitRechnungFactoryProductiv() {

      logger = LogManager.getLogger(getClass());
      // Testen ob das arbeitsverzeichnis im home-verzeichnis existiert
      File homeverzeichnis;
      Properties sysprops = System.getProperties();
      String homedir = sysprops.getProperty("user.home");
      homeverzeichnis = new File(homedir, Options.BENUTZERVERZEICHNIS);

      if (!homeverzeichnis.exists()) {
         // Verzeichnis anlegen
         logger.info(homeverzeichnis.getAbsolutePath() + " existiert nicht!\nwird angelegt...");
         homeverzeichnis.mkdirs();
      }

      String useTest = System.getProperty("useTestDb", "false");

      Options optionen;
      if (Boolean.parseBoolean(useTest)) {
         optionen = new Options.Build().dbHost("localhost").dbUser("markus").dbPassword("0773").dbDatabaseName("ArbeitrechnungenBak").stdTexFile("Rechnung_Allgemein.tex")
               .texTemplatesDir("Tex-Vorlagen").targetDir("targetDir").tmpDir("tmpDir").pdfProg("/usr/bin/okular").build();
      } else {
         File optionfile = new File(homedir + sysprops.getProperty("file.separator") + Options.BENUTZERVERZEICHNIS + sysprops.getProperty("file.separator") + "optionen.ini");

         createOptionsfileIfNotExisting(optionfile);

         optionen = loadOptions(optionfile);
      }
      if (optionen != null && optionen.getDbHost() != null) {
         Einstellungen.setOptions(optionen);
      }
   }

   public static void init() {
      if (instance == null)
         instance = new ArbeitRechnungFactoryProductiv();
   }

   private void createOptionsfileIfNotExisting(File optionfile) {
      boolean wasCreatedNew = false;
      try {
         wasCreatedNew = optionfile.createNewFile();
         if (wasCreatedNew) {
            logger.info("Options-Datei erfolgreich angelegt! Öffne " + OptionenDialog.class.getSimpleName());
            OptionenDialog optionwindow = new OptionenDialog(null, true);
            optionwindow.setVisible(true);
         }
      } catch (Exception e) {
         logger.error("Options-Datei konnte nicht angelegt werden. CreatedNew=" + wasCreatedNew, e);
      }
   }

   private Options loadOptions(File optionfile) {

      try {
         logger.debug("loading Option properties from " + optionfile.getAbsolutePath());
         Properties prop = new Properties();
         prop.load(new FileInputStream(optionfile));
         return new Options.Build(prop).build();
      } catch (Exception e) {
         logger.error("Startfenster.java: Options-Datei konnte nicht geladen werden.", e);
      }
      return null;
   }

   @Override
   public Persister getPersister(Class<? extends Persister> clazz, Options optionen) {
      if (clazz == KlientenEditorPersister.class)
         return new KlientenEditorPersister(optionen);
      if (clazz == RechnungDialogPersister.class)
         return new RechnungDialogPersister(optionen);
      throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...");
   }

}
