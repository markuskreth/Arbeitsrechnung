package de.kreth.arbeitsrechnungen.report;

import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class ShowJasperRechnung {

   Logger log = LoggerFactory.getLogger(getClass());
   
   public static void main(String[] args) throws JRException {
      ShowJasperRechnung showJasperRechnung = new ShowJasperRechnung();

      URL resource = showJasperRechnung.getClass().getResource("mtv_gross_buchholz.jasper");
      showJasperRechnung.log.debug(resource==null?"null":resource.toString());

      JRDataSource res = showJasperRechnung.createSource(16);
      showJasperRechnung.compileAndShowReport(res);
   }

   public JRDataSource createSource(int rechnungId) {

      Options opts = Einstellungen.getInstance().getEinstellungen();
      RechnungDialogPersister p = new RechnungDialogPersister(opts);
      Builder rechn = p.getRechnungById(rechnungId);
      rechn.einheiten(p.getEinheiten(rechnungId));

      int klienten_id = p.getKlientenIdForRechnungId(rechnungId);
      Klient klient = new KlientenEditorPersister(opts).getKlientById(klienten_id);
      rechn.zusatz1Name(klient.getZusatz1_Name());
      rechn.zusatz2Name(klient.getZusatz2_Name());
      return createSource(rechn.build());
      
   }

   public JRDataSource createSource(Rechnung rechnung) {
      return new RechnungDataSource(rechnung);
   }
   
   public JasperPrint compileAndShowReport(JRDataSource source) throws JRException {
      JasperReport report = JasperCompileManager.compileReport(getClass().getResourceAsStream("mtv_gross_buchholz.jrxml"));
      JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), source);
      JasperViewer.viewReport(print, false);
      
      return print;
   }

   public boolean store(JasperPrint repo, OutputStream outputStream) throws JRException {
      JasperExportManager.exportReportToPdfStream(repo, outputStream);
      return false;
   }


}
