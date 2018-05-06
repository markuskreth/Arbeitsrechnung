package de.kreth.arbeitsrechnungen;

/**
 * @author markus
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.Rechnung;

public class PdfCreator {

   private Logger logger = LoggerFactory.getLogger(getClass());

   public static final String TEXUMBRUCH = "\\\\\\\\";
   public static final String TEXLINE = "\\\\hline ";
   public static final String TEXEURO = "\\\\officialeuro\\\\ ";

   private boolean std_ersetzung = true;

   /**
    * Name der Pdf-Datei
    */
   private String pdf;

   private int latexergebnis = -1;

   private Rechnung rechnung;

   /**
    * IDs der Dublikate, die in einheiten gelöscht wurden
    */
   private Vector<Integer> dublikate = new Vector<Integer>();

   // private BigDecimal summe = BigDecimal.ZERO;
   private BigDecimal summe_stunden = BigDecimal.ZERO;
   String latexcode = null;

   private boolean unterschrift = false;

   private File texFile;

   /**
    * Get the value of unterschrift
    * 
    * @return the value of unterschrift
    */
   public boolean isUnterschrift() {
      return unterschrift;
   }

   /**
    * Soll die Unterschrift als Grafik eingefügt werden? Set the value of
    * unterschrift
    * 
    * @param unterschrift
    *           new value of unterschrift
    */
   public void setUnterschrift(boolean unterschrift) {
      this.unterschrift = unterschrift;
   }

   /**
    * Konstruktor
    * 
    * @throws FileNotFoundException
    */
   public PdfCreator(Rechnung rechnung) throws FileNotFoundException {
      
      this.rechnung = rechnung;

      openTexFile(); // Texfile wird geöffnet und in Variable
                     // latexcode gespeichert
                     // this.summe = BigDecimal.ZERO;
   }

   /**
    * Tex-Datei öffnen und in String 'latex' speichern
    * 
    * @return true bei Erfolg
    */
   private boolean openTexFile() throws FileNotFoundException {

      java.io.FileReader latexdatei;
      StringBuilder latex = new StringBuilder();

      latexdatei = new FileReader(rechnung.getTexdatei());
      int c;
      try {
         while ((c = latexdatei.read()) != -1) {
            // System.out.print((char)c);
            latex.append(java.lang.String.valueOf((char) c));
         }
         this.latexcode = latex.toString();

         if (this.latexcode.contains("%Zeileanfang\n") && this.latexcode.contains("%Zeileende\n"))
            this.std_ersetzung = false;

         return true;
      } catch (java.io.IOException e) {
         logger.error("RechnungData:openTexFile: read Error", e);
      } finally {
         if (latexdatei != null) {
            try {
               latexdatei.close();
            } catch (IOException e) {
               logger.error("Fehler beim Schließen der Tex-datei in " + rechnung.getTexdatei(), e);
            }
         }

      }
      return false;
   }

   /**
    * Erstellt aus der Vorlage und den Daten eine Tex-Datei und führt pdflatex
    * aus um eine pdf-Datei zu erstellen.
    * 
    * @param optionen
    * @return Rückgabe des System Commandos, 0 = Erfolg
    */
   public int makePdf(Options optionen) {
      // Formatierungen
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
      DecimalFormat zf = new DecimalFormat("0.00");
      // Verzeichnisse und Dateien festlegen
      int ergebnis = 0;

      checkValidity();

      logger.debug("Arbeitsverzeichnis: " + optionen.getTmpDir());

      texFile = new File(optionen.getTmpDir().getPath() + File.separator + new Date().getTime() + ".tex");

      // Ersetzungen vornehmen

      latexcode = latexcode.replaceAll("%%adresse%%\n", trimAdressAndTexify(rechnung.getAdresse()));

      String rexp = "..setkomavar.date..%%datum%%.";
      String repl = "\\\\setkomavar{date}{" + df.format(rechnung.getDatum().getTime()) + "}";
      latexcode = latexcode.replaceAll(rexp, repl);
      latexcode = latexcode.replaceAll("%%opening%%\n", "Sehr geehrte Damen und Herren,");
      latexcode = latexcode.replaceAll("%%subject%%\n", "Rechnung Kreth");
      String closing = "Mit freundlichen Grüßen";
      if (this.unterschrift) {
         closing += TEXUMBRUCH + "\\\\includegraphics[width=3.5cm]{" + optionen.getTexTemplatesDir() + File.separator + "Sign.jpg}";
         String rep = PdfCreator.TEXUMBRUCH + "\\\\includegraphics[width=3.5cm]{" + optionen.getTexTemplatesDir() + File.separator + "Sign.jpg}";
         latexcode = latexcode.replaceAll("%Unterschrift", rep);
      }
      latexcode = latexcode.replaceAll("%%closing%%\n", closing);

      // Rechnungnr setzten
      latexcode = latexcode.replaceAll("%%invoice%%", rechnung.getRechnungnr());

      String stundentabelle = createTable();
      if (std_ersetzung) {
         latexcode = latexcode.replaceAll("%%tabelle%%", TEXUMBRUCH + stundentabelle + TEXUMBRUCH + "\n");
         if (rechnung.isStunden())
            latexcode = latexcode.replaceAll("%%summe%%", " für " + zf.format(this.summe_stunden) + " Std. insgesamt " + zf.format(rechnung.getBetrag()) + TEXEURO);
         else
            latexcode = latexcode.replaceAll("%%summe%%", "insgesamt " + zf.format(rechnung.getBetrag()) + TEXEURO);
         latexcode = latexcode.replaceAll("%%zahlfrist%%", df.format(rechnung.getZahldatum().getTime()));
      } else {
         logger.debug("Ersetzungen beginnen");

         String ersetzung = "\"Stunden\"";
         logger.debug(ersetzung);
         latexcode = latexcode.replaceAll(ersetzung, zf.format(summe_stunden));
         ersetzung = "\"Stundenlohn\"";
         logger.debug(ersetzung);
         BigDecimal einzelPreis = rechnung.getEinheiten().elementAt(0).getEinzelPreis();
         einzelPreis = einzelPreis.setScale(2, RoundingMode.HALF_UP);

         latexcode = latexcode.replaceAll(ersetzung, einzelPreis.toPlainString());
         ersetzung = "\"Summe\"";
         logger.debug(ersetzung);

         latexcode = latexcode.replaceAll(ersetzung, zf.format(rechnung.getBetrag()) + " " + TEXEURO);
         ersetzung = "\"Monat\"";
         Calendar einheit_dat = new GregorianCalendar();
         einheit_dat.setTime(rechnung.getEinheiten().elementAt(0).getDatum());
         String new_monat = einheit_dat.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMANY) + " " + einheit_dat.get(Calendar.YEAR);

         logger.debug(ersetzung);
         latexcode = latexcode.replaceAll(ersetzung, new_monat);
         logger.debug("Ersetzungen beendet. Stunden, Stundenlohn, Monat und Summe ersetzt.");

         latexcode = latexcode.replaceAll("%%tabelle%%", stundentabelle + "\n");

      }
      latexcode = latexcode.replaceAll(TEXUMBRUCH + TEXUMBRUCH, TEXUMBRUCH);
      latexcode = latexcode.replaceAll("\n" + TEXUMBRUCH, "\n");
      // Tex-Datei speichern
      try {
         java.io.FileWriter out = new java.io.FileWriter(texFile);
         out.write(this.latexcode);
         out.close();
      } catch (java.io.IOException e) {
         logger.error("File out", e);
      }

      // pdflatex ausführen
      Process proc;

      try {
         File tmpDir = optionen.getTmpDir();

         if (!tmpDir.exists() || !tmpDir.isDirectory()) {
            throw new IllegalStateException("TmpDir exists=" + tmpDir.exists() + "; isDir=" + tmpDir.isDirectory());
         }

         if (!texFile.exists() || !texFile.isFile()) {
            throw new IllegalStateException("TexFile " + texFile + " exists=" + texFile.exists() + "; isDir=" + texFile.isFile());
         }

         String command = "pdflatex -halt-on-error -output-directory " + optionen.getTmpDir() + " " + texFile;
         logger.debug("pdflatex wird ausgeführt: ");
         logger.debug(command);

         proc = Runtime.getRuntime().exec(command);

         try {
            proc.waitFor();
            this.latexergebnis = proc.exitValue();
            logger.debug("pdf Exit-Value: " + this.latexergebnis);
            ergebnis = this.latexergebnis;
            if (ergebnis != 0) {
               StringBuilder output = new StringBuilder();

               streamToString(output, proc.getErrorStream());

               if (output.length() > 0)
                  output.append("\n\n");

               streamToString(output, proc.getInputStream());

               logger.error("Nicht erfolgreicht, exit-Wert=" + ergebnis + " in " + command + "\n\n" + output.toString());
            } else {
               this.pdf = texFile.getAbsolutePath().replace(".tex", ".pdf");
            }
         } catch (InterruptedException e2) {
            logger.debug("waitfor pdflatex", e2);
         }
      } catch (Exception e) {
         logger.error("Error executing pdflatex", e);
      }
      logger.debug("Pdflatex beendet!");
      return ergebnis; // Ergebnis von pdflatex wird zurückgegeben
   }

   private String trimAdressAndTexify(String adresse) {
      String retval = adresse;
      if (retval.endsWith("\n"))
         retval = retval.substring(0, retval.lastIndexOf("\n"));

      retval = retval.replaceAll("\n\n", TEXUMBRUCH + TEXUMBRUCH + "\n");
      retval = retval.replaceAll("\n", TEXUMBRUCH + TEXUMBRUCH + "\n");

      return retval;
   }

   public File getPdfFile() {
      return new File(pdf);
   }

   public File getTexFile() {
      return texFile;
   }

   private void checkValidity() {
      if (rechnung.getAdresse() == null || rechnung.getAdresse().length() <= 0)
         throw new IllegalStateException("Adresse ist ungültig: " + rechnung.getAdresse());
   }

   private void streamToString(StringBuilder output, InputStream stream) throws IOException {
      InputStreamReader in = new InputStreamReader(stream);
      BufferedReader bin = new BufferedReader(in);
      String line;
      while ((line = bin.readLine()) != null) {
         output.append(line).append("\n");
      }
   }

   private String createTable() {
      DateFormat df = DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, Locale.GERMAN);
      DecimalFormat zf = new DecimalFormat("0.00");
      StringBuilder tabelle = new StringBuilder();

      int fact = 0;
      int spalten = 3; // Mind 3 Spalten: Datum, Inhalt und Honorar
      if (rechnung.isStunden()) {
         spalten += 1;
         fact += 1;
      }
      if (rechnung.getZusatz1_name() != null) {
         spalten += 1;
         fact += 2;
      }
      if (rechnung.getZusatz2_name() != null) {
         spalten += 1;
         fact += 4;
      }

      if (std_ersetzung) {
         // Tabellenkopf
         makeLatexTableHead(tabelle, spalten);

         // Tabelleninhelt
         for (int i = 0; i < rechnung.getEinheiten().size(); i++) {
            if (rechnung.isZusammenfassungenErlauben())
               checkEinheitenGleichheit(i); // Einheiten zusammenfassen

            tabelle.append(df.format(rechnung.getEinheiten().elementAt(i).getDatum()) + " & " + rechnung.getEinheiten().elementAt(i).getInhalt() + " & ");

            BigDecimal dauer = BigDecimal.ZERO;
            if (rechnung.isStunden()) {
               dauer = BigDecimal.valueOf(rechnung.getEinheiten().elementAt(i).getDauerInMinutes()).divide(BigDecimal.valueOf(60)).setScale(2, RoundingMode.HALF_UP)
                     .stripTrailingZeros();
               tabelle.append(zf.format(dauer) + " & "); // Minuten in Stunden
                                                         // umrechnen
            }

            if (rechnung.getZusatz1_name() != null)
               tabelle.append(rechnung.getEinheiten().elementAt(i).getZusatz1() + " & ");
            if (rechnung.getZusatz2_name() != null)
               tabelle.append(rechnung.getEinheiten().elementAt(i).getZusatz2() + " & ");
            tabelle.append(zf.format(rechnung.getEinheiten().elementAt(i).getPreis()) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n");

            summe_stunden = summe_stunden.add(BigDecimal.valueOf(dauer.doubleValue())); // Minuten
                                                                                        // in
                                                                                        // Stunden
         }

         logger.debug("Anzahl der Elemente in dublikate: " + dublikate.size() + "\n" + dublikate);

         // Tabellenfuß
         double summe = rechnung.getBetrag().doubleValue();
         switch (fact) {
            case 0: // Datum, Inhalt, Preis
               tabelle.append(TEXLINE + "& & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 1: // Datum, Inhalt, Std, Preis
               tabelle.append(TEXLINE + "& & " + zf.format(summe_stunden) + " & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 2: // Datum, Inhalt, Ein Zusatz, Honorar
               tabelle.append(TEXLINE + "& & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 4: // Datum, Inhalt, Ein Zusatz, Honorar
               tabelle.append(TEXLINE + "& & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 3: // Datum, Inhalt, Std, Zusatz, Preis
               tabelle.append(TEXLINE + "& & " + zf.format(summe_stunden) + " & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 5: // Datum, Inhalt, Std, Zusatz, Preis
               tabelle.append(TEXLINE + "& & " + zf.format(summe_stunden) + " & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 6:
               tabelle.append(TEXLINE + "& & & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
            case 7:
               tabelle.append(TEXLINE + "& & " + zf.format(summe_stunden) + " & & & " + zf.format(summe) + " " + TEXEURO + TEXUMBRUCH + TEXLINE + "\n" + "\\\\end{tabular}");
               break;
         }
      } else {
         // Felder, die in Tex-Vorlage pro spalte eingesetzt werden können:
         // "Datum" "vonbis" "Teilnehmerzahl" "Dauer"

         String zeilencode = this.latexcode.substring(this.latexcode.indexOf("%Zeileanfang"), this.latexcode.indexOf("%Zeileende"));
         zeilencode = zeilencode.replace("%Zeileanfang", "");
         zeilencode = zeilencode.replace("%Zeileende", "");

         // Zeilen erstellen
         for (int i = 0; i < rechnung.getEinheiten().size(); i++) {
            String zeile = zeilencode;

            zeile = zeile.replaceAll("\"Datum\"", df.format(rechnung.getEinheiten().elementAt(i).getDatum()));

            if (rechnung.getZusatz1_name().matches("Teilnehmerzahl"))
               zeile = zeile.replaceAll("\"Teilnehmerzahl\"", rechnung.getEinheiten().elementAt(i).getZusatz1());

            zeile = zeile.replaceAll("\"Dauer\"", Double.valueOf(rechnung.getEinheiten().elementAt(i).getDauerInMinutes() / 60.).toString());

            String zeiten = "17.00 - 19.00";
            GregorianCalendar kalender = new GregorianCalendar();
            kalender.setTime(rechnung.getEinheiten().elementAt(i).getBeginn());
            String minute = "";
            int iminute = kalender.get(java.util.GregorianCalendar.MINUTE);
            if (iminute < 10)
               minute = "0" + iminute;
            else
               minute = "" + iminute;
            zeiten = kalender.get(java.util.GregorianCalendar.HOUR_OF_DAY) + ":" + minute;
            zeiten += " - ";
            kalender.setTime(rechnung.getEinheiten().elementAt(i).getEnde());
            iminute = kalender.get(java.util.GregorianCalendar.MINUTE);
            if (iminute < 10)
               minute = "0" + iminute;
            else
               minute = "" + iminute;
            zeiten += kalender.get(java.util.GregorianCalendar.HOUR_OF_DAY) + ":" + minute;

            zeile = zeile.replaceAll("\"vonbis\"", zeiten);

            this.summe_stunden = summe_stunden.add(BigDecimal.valueOf(rechnung.getEinheiten().elementAt(i).getDauerInMinutes() / 60));
            // Minuten in Stunden

            zeile = zeile.replace("\\hline", TEXUMBRUCH + TEXUMBRUCH + TEXUMBRUCH + "hline");
            zeile += "\n";
            tabelle.append(zeile);
         }

         String regexp = "(%Zeileanfang)(?s)(.*)(%Zeileende)";

         this.latexcode = this.latexcode.replaceAll(regexp, "%%tabelle%%");
      }
      return tabelle.toString();
   }

   private void makeLatexTableHead(StringBuilder tabelle, int spalten) {

      tabelle.append("\\\\begin{tabular}{");
      for (int i = 0; i < spalten - 1; i++) {
         tabelle.append("|l");
      }
      tabelle.append("|r|}" + TEXLINE + "\n" + "Datum & Inhalt");
      if (rechnung.isStunden())
         tabelle.append(" & Std.");
      if (rechnung.getZusatz1_name() != null)
         tabelle.append(" & " + rechnung.getZusatz1_name());
      if (rechnung.getZusatz2_name() != null)
         tabelle.append(" & " + rechnung.getZusatz2_name());

      tabelle.append(" & Honorar" + TEXUMBRUCH + TEXLINE + TEXLINE + "\n");

   }

   private void checkEinheitenGleichheit(int i) {
      // wird rekursiv wiederholt, bis keine Dublikate mehr gefunden werden.
      if (i + 1 < rechnung.getEinheiten().size()) {
         Arbeitsstunde element1 = rechnung.getEinheiten().elementAt(i);
         Arbeitsstunde element2 = rechnung.getEinheiten().elementAt(i + 1);
         if (element1.getDatum().equals(element2.getDatum()) && element1.getAngeboteID() == element2.getAngeboteID()) {
            // Wenn Datum, Angebot_id übereinstimmen dann preis und zusätze
            // checken
            markAsDoubleIfPreisAndZusaetzeMacht(i, element1, element2);
         }
      }
   }

   private void markAsDoubleIfPreisAndZusaetzeMacht(int i, Arbeitsstunde element1, Arbeitsstunde element2) {

      BigDecimal preis = element1.getPreis();
      if (preis.equals(element2.getPreis())) {
         // Zusätze checken
         String z1 = element2.getZusatz1();
         String z2 = element2.getZusatz2();
         if ((rechnung.getZusatz1_name() == null || element1.getZusatz1().equals(z1)) && (rechnung.getZusatz2_name() == null || element1.getZusatz2().equals(z2))) {

            element1.setPreis(preis.add(element2.getPreis()).setScale(2, RoundingMode.HALF_UP).doubleValue());

            dublikate.add(Integer.valueOf(element2.getID()));

            logger.info("Doppeltes Element wurde gefunden:" + "\ni = " + i + "\n ID von i: " + element1.getID() + "\n ID von i + 1: " + element2.getID());

            rechnung.getEinheiten().remove(i + 1);
            checkEinheitenGleichheit(i);
         }
      }
   }

}
