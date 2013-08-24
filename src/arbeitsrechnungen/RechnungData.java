
package arbeitsrechnungen;

/**
 *
 * @author markus
 */

import org.apache.log4j.Logger;
import ArbeitsstundeModel.*;
import java.util.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import arbeitsabrechnungendataclass.Verbindung;
import java.io.*;
import org.apache.log4j.Level;

public class RechnungData {
    private static Logger logger = Logger.getLogger("arbeitsrechnungen.RechnungData");
    protected static final String TEXUMBRUCH = "\\\\\\\\";
    protected static final String TEXLINE = "\\\\hline ";
    protected static final String TEXEURO = "\\\\officialeuro\\\\ ";
    public static final int TEXERROR = 103;
	private static final String[] MONATE = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
	private boolean std_ersetzung = true;
    String adresse;
    /**
     * Name der Tex-Vorlage
     */
    String tex_datei;
    /**
     * Name der erstellten tex-Datei
     */
    String tex_out;
    /**
     * Name der Pdf-Datei
     */
    String pdf;

    Integer latexergebnis = null;
    Vector<ArbeitsstundeImpl> einheiten;
	/**
	 * IDs der Dublikate, die in einheiten gelöscht wurden
	 */
	Vector<Integer> dublikate = new Vector<Integer>();
    GregorianCalendar heute;
    GregorianCalendar rechnungsdatum;
    GregorianCalendar zahlungsdatum;
    int klienten_id;
    String rechnungsnr;
    String zusatz1_name;
    String zusatz2_name;
    boolean stunden;
    boolean zusammenfassungen_erlauben;
    Double summe = 0.0;
    Double summe_stunden = 0.0;
    java.util.Properties optionen = new java.util.Properties();
    String latexcode;
    /** Neue oder bearbeitete Rechnung - wird durch setRechnung_id belegt
     * true: Beim Speichern wird INSERT ausgeführt
     * false: Beim Speichern wird UPDATE ausgeführt
     */
    protected boolean neu = true;
    private int rechnung_id;
    private boolean unterschrift = false;

	/**
	 * Get the value of unterschrift
	 *
	 * @return the value of unterschrift
	 */
	public boolean isUnterschrift() {
		return unterschrift;
	}

	/**
	 * Soll die Unterschrift als Grafik eingefügt werden?
	 * Set the value of unterschrift
	 *
	 * @param unterschrift new value of unterschrift
	 */
	public void setUnterschrift(boolean unterschrift) {
		this.unterschrift = unterschrift;
	}

    /**
     * Get the value of rechnung_id
     *
     * @return the value of rechnung_id
     */
    public int getRechnung_id() {
        return rechnung_id;
    }

    /**
     * Set the value of rechnung_id
     *
     * @param rechnung_id new value of rechnung_id
     */
    public void setRechnung_id(int rechnung_id) {
        this.neu = false;
        this.rechnung_id = rechnung_id;
    }


    /**
     * Get the value of neu
     *
     * @return the value of neu
     */
    public boolean isNeu() {
        return neu;
    }

    /**
     * Konstruktor
     */
    public void RechnungData(){
	logger.setLevel(Level.DEBUG);
logger.debug("Konstruktor RechnungenData wurde ausgeführt! Sonst nicht - wieso... bitte forschen");
        this.summe = 0.0;
        this.optionen = getEinstellungen();
    }

    private Properties getEinstellungen(){
        java.util.Properties sysprops = System.getProperties();
        java.util.Properties opt = new java.util.Properties();
        java.io.File optionfile  = new java.io.File(sysprops.getProperty("user.home") + sysprops.getProperty("file.separator") + ".arbeitrechnungen"
                + sysprops.getProperty("file.separator") + "optionen.ini");
        try{
            opt.load(new java.io.FileInputStream(optionfile));
//logger.debug("testopt: " + opt.getProperty("sqlserver"));
            return opt;
        }catch(Exception e){
            System.err.println("ArbeitsstundenTabelle.java: Options-Datei konnte nicht geladen werden.");
            return null;
        }
    }

    /**
     * Rechnung mit nötigen Daten füllen
     * gibt True zurück, wenn die Tex-Datei geladen werden konnte.
     *
     * @param adresse
     * @param tex_datei
     * @param einheiten
     * @param heute
     * @param rechnungsdatum
     * @param zahlungsdatum
     * @param rechnungsnr
     * @param zusatz1_name
     * @param zusatz2_name
     * @param zusammenfassungen_erlauben
     * @return
     */
    public boolean initRechnung(
            int klienten_id,
            String adresse,
            String tex_datei,
            Vector<ArbeitsstundeImpl> einheiten,
            GregorianCalendar heute,
            GregorianCalendar rechnungsdatum,
            GregorianCalendar zahlungsdatum,
            String rechnungsnr,
            String zusatz1_name,
            String zusatz2_name,
            boolean stunden,
            boolean zusammenfassungen_erlauben
        ){
        boolean texfileSuccess;
        while (adresse.endsWith("\n"))
            adresse = adresse.substring(0, adresse.lastIndexOf("\n"));
        
        adresse = adresse.replaceAll("\n\n", TEXUMBRUCH + TEXUMBRUCH + "\n");
        this.adresse = adresse.replaceAll("\n", TEXUMBRUCH + TEXUMBRUCH + "\n");

        this.klienten_id = klienten_id;
        this.tex_datei = tex_datei;
        this.einheiten = einheiten;
        this.heute = heute;
        this.rechnungsdatum = rechnungsdatum;
        this.zahlungsdatum = zahlungsdatum;
        this.rechnungsnr = rechnungsnr;
        this.zusatz1_name = zusatz1_name;
        this.zusatz2_name = zusatz2_name;
        this.stunden = stunden;
logger.debug("Stundenübernahme: " + stunden);
        this.zusammenfassungen_erlauben = zusammenfassungen_erlauben;
        texfileSuccess = openTexFile(); // Texfile wird geöffnet und in Variable latexcode gespeichert

        // Bekanntes wird ersetzt
//        latexcode = latexcode.replaceAll("%\\setkomavar{invoice}{%%invoice%%}", "\\setkomavar{invoice}{" +
//                this.rechnungsnr + "}");
        latexcode = latexcode.replaceAll("%%adresse%%\n", this.adresse);

        return texfileSuccess;
    }

    private boolean openTexFile(){
        // Tex-Datei öffnen und in String 'latex' speichern
        java.io.FileReader latexdatei;
        String latex = "";
        try{
            latexdatei = new java.io.FileReader(this.tex_datei);
            int c;
            try{
                while ((c = latexdatei.read()) != -1) {
//                    System.out.print((char)c);
                    latex = latex.concat(java.lang.String.valueOf((char)c));
                }
                latexdatei.close();
                this.latexcode = latex;
				if (latex.contains("%Zeileanfang\n") && latex.contains("%Zeileende\n"))
					this.std_ersetzung = false;
                return true;
            }catch (java.io.IOException e){
                System.err.println("RechnungData:openTexFile: read Error");
                e.printStackTrace();
            }
        }catch(java.io.FileNotFoundException e){
            System.err.println("RechnungData:openTexFile: open error");
            e.printStackTrace();
        }
        return false;
    }

	/**
	 * Erstellt aus der Vorlage und den Daten eine Tex-Datei und führt pdflatex aus um eine pdf-Datei zu erstellen.
	 * @return
	 */
    public int makePdf(){
        // Formatierungen 
        DateFormat df = DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.GERMAN);
        DecimalFormat zf = new DecimalFormat("0.00");
        // Verzeichnisse und Dateien festlegen
        optionen = getEinstellungen();
        int ergebnis =0;

        String outputfile_tex = optionen.getProperty("arbeitsverzeichnis");
        logger.debug("Arbeitsverzeichnis: " + outputfile_tex);
        outputfile_tex = outputfile_tex + this.heute.getTimeInMillis() + ".tex";
        this.tex_out = outputfile_tex;

        //Ersetzungen vornehmen
	String rexp = "..setkomavar.date..%%datum%%.";
	String repl = "\\\\setkomavar{date}{" + df.format(rechnungsdatum.getTime()) + "}";
	latexcode = latexcode.replaceAll(rexp, repl);
        latexcode = latexcode.replaceAll("%%opening%%\n", "Sehr geehrte Damen und Herren,");
        latexcode = latexcode.replaceAll("%%subject%%\n", "Rechnung Kreth");
		String closing =  "Mit freundlichen Grüßen";
		if (this.unterschrift) {
			closing += TEXUMBRUCH + "\\\\includegraphics[width=3.5cm]{" + optionen.getProperty("verzeichnistexdateien") + "Sign.jpg}";
			String rep = RechnungData.TEXUMBRUCH + "\\\\includegraphics[width=3.5cm]{" + optionen.getProperty("verzeichnistexdateien") + "Sign.jpg}";
			latexcode = latexcode.replaceAll("%Unterschrift", rep);
		}
        latexcode = latexcode.replaceAll("%%closing%%\n", closing);

		//Rechnungnr setzten
		latexcode = latexcode.replaceAll("%%invoice%%", this.rechnungsnr);

        String stundentabelle = createTable();
		if(std_ersetzung){
			latexcode = latexcode.replaceAll("%%tabelle%%", TEXUMBRUCH + stundentabelle + TEXUMBRUCH + "\n");
			if(this.stunden) latexcode = latexcode.replaceAll("%%summe%%", " für " + this.summe_stunden + " Std. insgesamt " + zf.format(this.summe) + TEXEURO);
				else latexcode = latexcode.replaceAll("%%summe%%", "insgesamt " + zf.format(this.summe) + TEXEURO);
			latexcode = latexcode.replaceAll("%%zahlfrist%%", df.format(this.zahlungsdatum.getTime()));
		}else{
			logger.debug("Ersetzungen beginnen");

			String ersetzung = "\"Stunden\"";
			logger.debug(ersetzung);
			latexcode = latexcode.replaceAll(ersetzung, this.summe_stunden.toString());
			ersetzung = "\"Stundenlohn\"";
			logger.debug(ersetzung);
			latexcode = latexcode.replaceAll(ersetzung, ((Double)(this.einheiten.elementAt(0).getEinzelPreis())).toString());
			ersetzung = "\"Summe\"";
			logger.debug(ersetzung);
                        
			latexcode = latexcode.replaceAll(ersetzung, zf.format(this.summe) + " " + TEXEURO);
			ersetzung = "\"Monat\"";
			java.util.GregorianCalendar einheit_dat = new java.util.GregorianCalendar();
			einheit_dat.setTime(this.einheiten.elementAt(0).getDatum());
			String new_monat = this.MONATE[einheit_dat.get(java.util.GregorianCalendar.MONTH)] + " " + einheit_dat.get(java.util.GregorianCalendar.YEAR);

			logger.debug(ersetzung);
			latexcode = latexcode.replaceAll(ersetzung,  new_monat);
			logger.debug("Ersetzungen beendet. Stunden, Stundenlohn, Monat und Summe ersetzt.");

			latexcode = latexcode.replaceAll("%%tabelle%%", stundentabelle + "\n");
			
		}
        latexcode = latexcode.replaceAll(TEXUMBRUCH + TEXUMBRUCH, TEXUMBRUCH);
        latexcode = latexcode.replaceAll("\n" + TEXUMBRUCH, "\n");
        //Tex-Datei speichern
        try{
            java.io.FileWriter out = new java.io.FileWriter(outputfile_tex);
            out.write(this.latexcode);
            out.close();
        }catch (java.io.IOException e){
            e.printStackTrace();
            System.err.println("File out");
        }

        //pdflatex ausführen
        java.lang.Process proc;
		java.io.BufferedInputStream proc_stream;
// TODO Ausgabe von pdflatex abfangen!
        try {
            String command = "pdflatex -halt-on-error -output-directory " + optionen.getProperty("arbeitsverzeichnis") + " " + outputfile_tex;
            logger.debug("pdflatex wird ausgeführt: ");
            logger.debug(command);

            proc = Runtime.getRuntime().exec(command);

			proc_stream = new java.io.BufferedInputStream(proc.getInputStream());
            try{
                proc.waitFor();
                this.latexergebnis =  proc.exitValue();
                logger.debug("pdf Exit-Value: " + this.latexergebnis);
                ergebnis =  this.latexergebnis;
                if (ergebnis!=0){
					System.err.println();
                }else{
                    this.pdf = outputfile_tex.substring(0, outputfile_tex.lastIndexOf(".tex")) + ".pdf";
                }
            } catch (InterruptedException e2) {
                logger.debug("waitfor pdflatex");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        logger.debug("Pdflatex beendet!");
        return ergebnis;    //Ergebnis von pdflatex wird zurückgegeben
    }

    private String createTable(){
        DateFormat df = DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.GERMAN);
        DecimalFormat zf = new DecimalFormat("0.00");
        String tabelle ="";
        int fact = 0;
        int spalten = 3; // Mind 3 Spalten: Datum, Inhalt und Honorar
        if (this.stunden){
            spalten += 1;
            fact += 1;
        }
        if (this.zusatz1_name != null){
            spalten += 1;
            fact += 2;
        }
        if (this.zusatz2_name != null){
            spalten += 1;
            fact += 4;
        }

		if (std_ersetzung){
			// Tabellenkopf
			tabelle = "\\\\begin{tabular}{";
			for (int i = 0; i < spalten - 1; i++){
				tabelle += "|l";
			}
			tabelle += "|r|}" + TEXLINE + "\n" +
					"Datum & Inhalt";
			if (this.stunden) tabelle += " & Std.";
			if (this.zusatz1_name != null) tabelle += " & " + this.zusatz1_name;
			if (this.zusatz2_name != null) tabelle += " & " + this.zusatz2_name;

			tabelle += " & Honorar" + TEXUMBRUCH + TEXLINE + TEXLINE + "\n";

			// Tabelleninhelt
			for (int i=0; i<einheiten.size(); i++){
				if(this.zusammenfassungen_erlauben) checkEinheitenGleichheit(i);  // Einheiten zusammenfassen

				tabelle += df.format(einheiten.elementAt(i).getDatum()) + " & " +
						einheiten.elementAt(i).getInhalt() + " & ";
				if (this.stunden) tabelle += einheiten.elementAt(i).getDauer()/60 + " & ";  // Minuten in Stunden
				if (zusatz1_name != null) tabelle += einheiten.elementAt(i).getZusatz1() + " & ";
				if (zusatz2_name != null) tabelle += einheiten.elementAt(i).getZusatz2() + " & ";
				tabelle += zf.format(einheiten.elementAt(i).getPreis()) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n";
				this.summe += einheiten.elementAt(i).getPreis();
				this.summe_stunden += einheiten.elementAt(i).getDauer()/60;             // Minuten in Stunden
			}

			// Tabellenfuß
			switch (fact){
				case 0:     // Datum, Inhalt, Preis
							tabelle += TEXLINE + "& & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
								"\\\\end{tabular}";
							break;
				case 1:     // Datum, Inhalt, Std, Preis
							tabelle += TEXLINE + "& & " + summe_stunden + " & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
									"\\\\end{tabular}";
							break;
				case 2:     // Datum, Inhalt, Ein Zusatz, Honorar
							tabelle += TEXLINE + "& & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
									"\\\\end{tabular}";
							break;
				case 4:     // Datum, Inhalt, Ein Zusatz, Honorar
							tabelle += TEXLINE + "& & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
									"\\\\end{tabular}";
							break;
				case 3:     // Datum, Inhalt, Std, Zusatz, Preis
							tabelle += TEXLINE + "& & " + summe_stunden + " & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
									"\\\\end{tabular}";
							break;
				case 5:     // Datum, Inhalt, Std, Zusatz, Preis
							tabelle += TEXLINE + "& & " + summe_stunden + " & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
									"\\\\end{tabular}";
							break;
				case 6:     tabelle += TEXLINE + "& & & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
							"\\\\end{tabular}";
							break;
				case 7:     tabelle += TEXLINE + "& & " + summe_stunden + " & & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" +
							"\\\\end{tabular}";
							break;
			}
		}else{
			// Felder, die in Tex-Vorlage pro spalte eingesetzt werden können: "Datum" "vonbis" "Teilnehmerzahl" "Dauer"
		
			String zeilencode = this.latexcode.substring(
					this.latexcode.indexOf("%Zeileanfang"),
					this.latexcode.indexOf("%Zeileende"));
			zeilencode = zeilencode.replace("%Zeileanfang", "");
			zeilencode = zeilencode.replace("%Zeileende", "");
			// javax.swing.JOptionPane.showMessageDialog(null, zeilencode);

			// Zeilen erstellen
			for (int i=0; i<einheiten.size(); i++){
				String zeile = zeilencode;
				zeile = zeile.replaceAll("\"Datum\"", df.format(einheiten.elementAt(i).getDatum()));
				if (this.zusatz1_name.matches("Teilnehmerzahl"))
					zeile = zeile.replaceAll("\"Teilnehmerzahl\"", einheiten.elementAt(i).getZusatz1());
				zeile = zeile.replaceAll("\"Dauer\"", ((Double)(einheiten.elementAt(i).getDauer()/60.)).toString() );
				// TODO Zugriff auf Datenbank-Zeiten...
				String zeiten = "17.00 - 19.00";
				GregorianCalendar kalender = new GregorianCalendar();
				kalender.setTime(einheiten.elementAt(i).getBeginn());
				String minute = "";
				int iminute = kalender.get(java.util.GregorianCalendar.MINUTE);
				if (iminute<10) minute = "0" + iminute;
				else minute = "" + iminute;
				zeiten = kalender.get(java.util.GregorianCalendar.HOUR_OF_DAY) + ":" + minute;
				zeiten += " - ";
				kalender.setTime(einheiten.elementAt(i).getEnde());
				iminute = kalender.get(java.util.GregorianCalendar.MINUTE);
				if (iminute<10) minute = "0" + iminute;
				else minute = "" + iminute;
				zeiten += kalender.get(java.util.GregorianCalendar.HOUR_OF_DAY) + ":" + minute;

				zeile = zeile.replaceAll("\"vonbis\"", zeiten);

				this.summe += einheiten.elementAt(i).getPreis();
				this.summe_stunden += einheiten.elementAt(i).getDauer()/60;             // Minuten in Stunden

				zeile = zeile.replace("\\hline", TEXUMBRUCH + TEXUMBRUCH + TEXUMBRUCH + "hline");
				zeile += "\n";
				tabelle += zeile;
			}

			String regexp = "(%Zeileanfang)(?s)(.*)(%Zeileende)";

			this.latexcode = this.latexcode.replaceAll(regexp, "%%tabelle%%");
logger.debug("Tabllencode:: " + tabelle);
			// TODO Einzelne Felder ersetzten pro Tag
		}
        return tabelle;
    }

    private void checkEinheitenGleichheit(int i){
		// wird rekursiv wiederholt, bis keine Dublikate mehr gefunden werden.
            if(i+1 < einheiten.size()){
                if(einheiten.elementAt(i).getDatum().equals(     einheiten.elementAt(i+1).getDatum()) &&
                   einheiten.elementAt(i).getAngeboteID().equals(einheiten.elementAt(i+1).getAngeboteID())
                        ){
                    // Wenn Datum, Angebot_id übereinstimmen dann preis und zusätze checken
                    Double preis = einheiten.elementAt(i).getPreis();
                    if (preis.equals(einheiten.elementAt(i + 1).getPreis())){
                        // Zusätze checken
                        String z1 = einheiten.elementAt(i + 1).getZusatz1();
                        String z2 = einheiten.elementAt(i + 1).getZusatz2();
                        if ((this.zusatz1_name == null || einheiten.elementAt(i).getZusatz1().equals(z1)) &&
                            (this.zusatz2_name == null || einheiten.elementAt(i).getZusatz2().equals(z2))){
                            einheiten.elementAt(i).setPreis(preis + einheiten.elementAt(i + 1).getPreis());
							dublikate.add(einheiten.elementAt(i+1).getID());
logger.info(
		"Doppeltes Element wurde gefunden:" +
		"\ni = " + i +
		"\n ID von i: " + einheiten.elementAt(i).getID() +
		"\n ID von i + 1: " + einheiten.elementAt(i + 1).getID()
		);
logger.debug("Anzahl der Elemente in dublikate: " + dublikate.size());
System.out.print("Enthaltene Elemente: " + dublikate.elementAt(0));
for (int k=1; k<dublikate.size();k++){
	System.out.print(", " + dublikate.elementAt(k));
}
                            einheiten.remove(i + 1);
                            checkEinheitenGleichheit(i);
                        }
                    }
                }
            }
    }

    public void showPdf(){
        String pdfProg = optionen.getProperty("pdfprogramm");
        if (pdfProg != null){
            if(this.latexergebnis == 0 && this.pdf != null){
                String befehl = "";
                befehl = pdfProg + " " + this.pdf;
                java.io.File pdf_datei = new java.io.File(this.pdf);
                if(pdf_datei.canRead()){
		    logger.setLevel(Level.INFO);
                    logger.info("showPdf: " + befehl);
                    try{
//                        Runtime.getRuntime().exec("sh -c " + befehl);
                        Runtime.getRuntime().exec( befehl);
                    }catch(Exception e){
                        logger.debug("showPdf Runtime error:");
                        e.printStackTrace();
                    }
                }else{
                    System.err.println("Pdfdatei existiert nicht: " + this.pdf );
                    System.err.println("Pdfdatei existiert nicht: " + pdf_datei.getAbsolutePath() );
                }
            }
        }else{
            logger.debug("Kein pdf-Programm angegeben. Ausgabe nicht möglich.");
        }
    }

    /**
     * Speichert die Rechnung und Details in der Datenbank
     */
    public int speichern(){
        Verbindung verbindung = new Verbindung(optionen.getProperty("sqlserver"), optionen.getProperty("datenbank") ,
                optionen.getProperty("user"), optionen.getProperty("password"));

        java.sql.Date sql_rechnungsdatum = new java.sql.Date(this.rechnungsdatum.getTimeInMillis());
        java.sql.Date sql_zahlungsdatum = new java.sql.Date(this.zahlungsdatum.getTimeInMillis());
        // zuerst die tex und pdf-Datei ins andere Verzeichnis kopieren und umbenennen
        String dateiname = "";
        String sql = "SELECT Auftraggeber FROM klienten WHERE klienten_id=" + this.klienten_id + ";";
        logger.debug("RechnungenData:speichern(546): " + sql);
        java.sql.ResultSet auftraggeber = verbindung.query(sql);
        try{
            if(auftraggeber.first()){
                dateiname = auftraggeber.getString("Auftraggeber") + "_" + this.rechnungsnr;
				dateiname = dateiname.replace(" ", "_");
            }else{
				// sql nicht erfolgreich
				dateiname = this.rechnungsnr;
				dateiname = dateiname.replace(" ", "_");
			}
        }catch(Exception e){
            e.printStackTrace();
        }
        java.lang.Process proc;
        int ergebnis=0;
        String new_pdf = dateiname + ".pdf";

        String befehl = "mv " + this.pdf + " " + optionen.getProperty("verzPdfFiles") + new_pdf;
        logger.debug("RechnungenData:speichern(565): " + befehl);
        try{
            //proc = Runtime.getRuntime().exec(befehl);
            proc = new ProcessBuilder("sh", "-c",befehl).start();
            try{
                BufferedReader procout = new BufferedReader(
                        new InputStreamReader(proc.getErrorStream())
                        );
                String line;
                while ((line = procout.readLine()) != null) {
                    logger.debug("  ERROR> " + line);
                }
                proc.waitFor();
                logger.debug("mv pdf Exit-Value(570): " + proc.exitValue());
                ergebnis =  proc.exitValue();
                if (proc.exitValue()==0){
                    this.pdf = optionen.getProperty("verzPdfFiles") + new_pdf;                  
                }

            } catch (InterruptedException e2) {
                logger.debug("waitfor pdflatex");
            }
        }catch(java.io.IOException exp){
            exp.printStackTrace();
        }
        // Texdatei auch kopieren, wenn pdf erfolgreich kopiert wurde.
        if(ergebnis==0){
            befehl = "mv " + this.tex_out + " " + optionen.getProperty("verzPdfFiles") + dateiname + ".tex";
            System.out.print("RechnungenData:speichern(584): " + befehl);
            try{
                //proc = Runtime.getRuntime().exec(befehl);
                proc = new ProcessBuilder("sh", "-c",befehl).start();
                try{
                    proc.waitFor();
                    logger.debug("mv tex Exit-Value(589): " + proc.exitValue());
                    ergebnis =  proc.exitValue();
                    if (proc.exitValue()==0){
                        this.tex_out = optionen.getProperty("verzPdfFiles") + dateiname + ".tex";
                    }
                } catch (InterruptedException e2) {
                    logger.debug("waitfor pdflatex");
                }
            }catch(java.io.IOException exp){
            }
        }
        if(ergebnis==0){
            // dann die Rechnung in die Datenbank
            if (this.neu){ // Bei neuer Rechnung INSERT
                sql = "INSERT INTO rechnungen (klienten_id, datum, rechnungnr, betrag, texdatei, pdfdatei, adresse, zusatz1, zusatz2, zusammenfassungen, zahldatum)" +
                        "VALUES (" + this.klienten_id +
                        ", \"" + sql_rechnungsdatum +
                        "\", \"" + this.rechnungsnr +
                        "\", " + this.summe +
                        ", \"" + dateiname + ".tex" +
                        "\", \"" +  dateiname + ".pdf" +
                        "\", \"" + this.adresse +
                        "\", ";
                if(this.zusatz1_name == null) sql += "0";
                else sql += "1";
                if(this.zusatz2_name == null) sql += ", 0";
                else sql += ", 1";
                if(this.zusammenfassungen_erlauben) sql += ", 1";
                else sql += ", 0";
                sql += ", \"" + sql_zahlungsdatum +
                        "\");";
            }else{
                // Bei vorhandener Rechnung UPDATE
                sql = "UPDATE rechnungen SET" +
                        " datum=\"" + sql_rechnungsdatum +
                        "\", rechnungnr=\"" + this.rechnungsnr +
                        "\", betrag=" + this.summe +
                        ", texdatei=\"" + dateiname + ".tex" +
                        "\", pdfdatei=\"" +  dateiname + ".pdf" +
                        "\", adresse=\"" + this.adresse;
                if(this.zusatz1_name == null) sql += "\", zusatz1=\"0";
                else sql += "\", zusatz1=\"1";
                if(this.zusatz2_name == null) sql += "\", zusatz2=\"0";
                else sql += "\", zusatz2=\"1";
                if(this.zusammenfassungen_erlauben) sql += "\", zusammenfassungen=\"1";
                else sql += "\", zusammenfassungen=\"0";
                sql += "\", zahldatum=\"" + sql_zahlungsdatum +
                        "\" WHERE rechnungen_id 	=" + this.rechnung_id + ";";
            }
            logger.debug("Rechnung speichern: " + sql);
            if(verbindung.sql(sql)){
				// Elemente in "einheiten" werden in  WHERE-bedinung aufgenommen
                String in_bedingung = "(" + einheiten.elementAt(0).getID();

                for (int i = 1 ; i < einheiten.size(); i++){
                    in_bedingung += ", " + einheiten.elementAt(i).getID();
                }

				// Elemente, die bei dublikatsuche aussortiert wurden, werden in Liste aufgenommen.
				for (int i = 0; i < dublikate.size() ; i++){
					in_bedingung += ", " + dublikate.elementAt(i);
				}
                in_bedingung += ")";

                // Rechnung_verschickt, Rechnungsdatum und rechnung_id bei einheiten ändern
                if(this.neu){
					logger.debug("LAST_INSERT_ID wird benutzt...");
					sql = "UPDATE einheiten SET Rechnung_verschickt=1, " +
                                           "Rechnung_Datum=\"" + sql_rechnungsdatum.toString() + "\", " +
                                           "rechnung_id=LAST_INSERT_ID()" +
                                           " WHERE einheiten_id IN ";
				}else {
					sql = "UPDATE einheiten SET Rechnung_verschickt=1, " +
                                           "Rechnung_Datum=\"" + sql_rechnungsdatum.toString() + "\", " +
                                           "rechnung_id=" + rechnung_id +
                                           " WHERE einheiten_id IN ";
				}
                sql += in_bedingung + ";";
                logger.debug("UPDATE einheiten: " + sql);
                verbindung.sql(sql);
            }
        }
        return ergebnis;
    }
}
