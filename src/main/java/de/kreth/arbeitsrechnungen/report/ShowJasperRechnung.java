package de.kreth.arbeitsrechnungen.report;

import java.io.OutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import arbeitsabrechnungendataclass.Verbindung_mysql;
import de.kreth.arbeitsrechnungen.Einstellungen;
import de.kreth.arbeitsrechnungen.Options;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

public class ShowJasperRechnung {

   Logger log = LogManager.getLogger(getClass());
   
   public static void main(String[] args) throws JRException {
      ShowJasperRechnung showJasperRechnung = new ShowJasperRechnung();

      URL resource = showJasperRechnung.getClass().getResource("mtv_gross_buchholz.jasper");
      showJasperRechnung.log.debug(resource);

      JRDataSource res = showJasperRechnung.createSource(16);
      showJasperRechnung.compileAndShowReport(res);
   }

   public JRDataSource createSource(int rechnungId) {

      Options opts = Einstellungen.getInstance().getEinstellungen();
      
      Verbindung_mysql verb = new Verbindung_mysql(opts.getProperties());
      ResultSet rs = verb.query("SELECT rechnungen_id, betrag, rechnungen.datum, geldeingang, pdfdatei, rechnungnr, \n" + 
            "   zahldatum, zusammenfassungen, rechnungen.zusatz1, rechnungen.zusatz2,\n" + 
            "    klienten.Zusatz1, klienten.Zusatz1_Name, klienten.Zusatz2, klienten.Zusatz2_Name, \n" + 
            "    einheiten.Beginn, einheiten.Ende, einheiten.Dauer, einheiten.Preis, \n" + 
            "    einheiten.Preisänderung, einheiten.zusatz1 zusatz1Val, einheiten.zusatz2 zusatz2Val, angebote.Beschreibung, \n" + 
            "    angebote.Preis preisProStunde\n" + 
            "FROM Arbeitrechnungen.rechnungen \n" + 
            "    inner join Arbeitrechnungen.klienten on rechnungen.klienten_id=klienten.klienten_id\n" + 
            "    inner join einheiten on rechnungen.rechnungen_id=einheiten.rechnung_id\n" + 
            "    inner join angebote on einheiten.angebote_id=angebote.angebote_id\n" + 
            "where rechnungen_id=" + rechnungId);
      
      JRResultSetDataSource source = new JRResultSetDataSource(rs);
      return source;
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
