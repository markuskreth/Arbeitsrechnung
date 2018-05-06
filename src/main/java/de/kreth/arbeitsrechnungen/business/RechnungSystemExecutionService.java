package de.kreth.arbeitsrechnungen.business;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Rechnung;

public class RechnungSystemExecutionService {

   private Options options;
   private Logger logger;

   public RechnungSystemExecutionService() {
      this.options = Einstellungen.getInstance().getEinstellungen();
      this.logger = LoggerFactory.getLogger(getClass());
   }

   public int movePdf(Rechnung rechnung, String dateiname) {

      int ergebnis = -1;

      String new_pdf = dateiname + ".pdf";

      File source = new File(rechnung.getPdfdatei());
      File target = new File(options.getTargetDir(), new_pdf);
      
      try {
         FileUtils.moveFile(source, target);
         ergebnis = 0;
      } catch (java.io.IOException exp) {
         exp.printStackTrace();
      }
      if (ergebnis == 0) {
         rechnung.setPdfdatei(target.getAbsolutePath());
      }
      return ergebnis;
   }

   public int moveTex(Rechnung rechnung, String dateiname) {

      int ergebnis = -1;

      File srcFile = new File(rechnung.getTexdatei());
      File destFile = new File(options.getTargetDir(), dateiname+".tex");
      
      try {
         FileUtils.moveFile(srcFile, destFile);
         ergebnis = 0;
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
