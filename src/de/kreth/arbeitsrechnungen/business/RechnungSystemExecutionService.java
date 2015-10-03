package de.kreth.arbeitsrechnungen.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Rechnung;

public class RechnungSystemExecutionService {

   private Options options;
   private Logger logger;

   public RechnungSystemExecutionService(Options optionen) {
      this.options = optionen;
      this.logger = Logger.getLogger(getClass());
   }

   public int movePdf(Rechnung rechnung, String dateiname) {

      int ergebnis = -1;

      String new_pdf = dateiname + ".pdf";

      final String pdfDateiName = "'" + options.getTargetDir() + File.separator + new_pdf + "'";
      String befehl = "mv '" + rechnung.getPdfdatei() + "' " + pdfDateiName;
      logger.debug("RechnungenData:speichern: " + befehl);

      try {
         // proc = Runtime.getRuntime().exec(befehl);
         Process proc = new ProcessBuilder("sh", "-c", befehl).start();
         try {
            BufferedReader procout = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line;
            while ((line = procout.readLine()) != null) {
               logger.debug("  ERROR> " + line);
            }
            proc.waitFor();
            logger.debug("mv pdf Exit-Value(570): " + proc.exitValue());
            ergebnis = proc.exitValue();

         } catch (InterruptedException e2) {
            logger.debug("waitfor pdflatex");
         }
      } catch (java.io.IOException exp) {
         exp.printStackTrace();
      }
      if (ergebnis == 0) {
         rechnung.setPdfdatei(pdfDateiName);
      }
      return ergebnis;
   }

   public int moveTex(Rechnung rechnung, String dateiname) {

      int ergebnis = -1;
      // Texdatei auch kopieren, wenn pdf erfolgreich kopiert wurde.
      final String texDateiName = "'" + options.getTargetDir() + File.separator + dateiname + ".tex'";
      String befehl = "mv '" + rechnung.getTexdatei() + "' " + texDateiName;

      logger.debug("RechnungenData:speichern: " + befehl);

      try {
         // proc = Runtime.getRuntime().exec(befehl);
         Process proc = new ProcessBuilder("sh", "-c", befehl).start();
         try {
            proc.waitFor();
            logger.debug("mv tex Exit-Value(589): " + proc.exitValue());
            ergebnis = proc.exitValue();
            if (ergebnis == 0)
               rechnung.setTexdatei(texDateiName);
         } catch (InterruptedException e2) {
            logger.debug("waitfor pdflatex");
         }
      } catch (java.io.IOException exp) {
         logger.warn("Fehler in ProcessBuilder", exp);
      }

      return ergebnis;
   }

   public void showRechnung(Rechnung rechnungToShow) {
      String befehl = this.options.getPdfProg();
      befehl += " " + rechnungToShow.getPdfdatei();
      logger.debug("FormRechnungen: " + befehl);

      try {
         new ProcessBuilder("sh", "-c", befehl).start();
      } catch (java.io.IOException e) {
         logger.error("FormRechnungen: PDFansehen: ", e);
      }

   }

}
