package de.kreth.arbeitsrechnungen;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.gui.dialogs.OptionenDialog;
import de.kreth.arbeitsrechnungen.persister.Persister;

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
	private Options optionen;

	protected ArbeitRechnungFactoryProductiv() {

		logger = LoggerFactory.getLogger(getClass());
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

		if (Boolean.parseBoolean(useTest)) {
			optionen = new Options.Build()
					.dbHost("localhost").dbUser("markus").dbPassword("0773").dbDatabaseName("ArbeitrechnungenBak")
					.stdTexFile("Rechnung_Allgemein.tex").texTemplatesDir("Tex-Vorlagen")
					.targetDir("targetDir").tmpDir("tmpDir").pdfProg("/usr/bin/okular")
					.build();
		} else {
			File optionfile = new File(homedir + sysprops.getProperty("file.separator") + Options.BENUTZERVERZEICHNIS
					+ sysprops.getProperty("file.separator") + "optionen.ini");

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
	public <T extends Persister> T getPersister(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getConstructor(optionen.getClass());
			return constructor.newInstance(optionen);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("Klasse " + clazz.getSimpleName() + " nicht unterstützt...", e);
		}
	}
	
}
