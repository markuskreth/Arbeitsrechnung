package de.kreth.arbeitsrechnungen.report;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.arbeitsrechnungen.*;
import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.data.Rechnung;
import de.kreth.arbeitsrechnungen.data.Rechnung.Builder;
import de.kreth.arbeitsrechnungen.persister.KlientenEditorPersister;
import de.kreth.arbeitsrechnungen.persister.RechnungDialogPersister;
import de.kreth.arbeitsrechnungen.persister.RechnungPersister;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

public class ShowJasperRechnung {

	private static final String MTV_JRXML = "mtv_gross_buchholz.jrxml";
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	

	public static void main(String[] args) throws JRException {

	   ArbeitRechnungFactoryProductiv.init();
		ShowJasperRechnung showJasperRechnung = new ShowJasperRechnung();

		int rechnungId;
		if(args.length>0 && StringUtils.isNumeric(args[0])) {
			rechnungId = Integer.valueOf(args[0]).intValue();
		}else {
			rechnungId = showJasperRechnung.chooseRechnung();
		}
		URL resource = showJasperRechnung.getClass().getResource(MTV_JRXML);
		showJasperRechnung.log.debug(resource == null ? "null" : resource.toString());

		JRDataSource res = showJasperRechnung.createSource(rechnungId);
		showJasperRechnung.compileAndShowReport(res);
	}

	private int chooseRechnung() {

      KlientenEditorPersister klientenEditorPersister = ArbeitRechnungFactory.getInstance().getPersister(KlientenEditorPersister.class);
		List<Klient> klienten = klientenEditorPersister.getAllKlienten();
		RechnungPersister pers = ArbeitRechnungFactory.getInstance().getPersister(RechnungPersister.class);
		List<KlientRechnung> rechnungen = new ArrayList<>();
		for (Klient k: klienten) {
			List<Rechnung> tmp = pers.getRechnungenForKlient(k.getKlienten_id());
			for (Rechnung r: tmp) {
				rechnungen.add(new KlientRechnung(k, r));
			}
		}
		
		pers.close();
		klientenEditorPersister.close();
		
		KlientRechnung result = (KlientRechnung) JOptionPane.showInputDialog(null, "Wählen Sie eine Rechnung", "Rechnung anzeigen"
				, JOptionPane.QUESTION_MESSAGE, null
				, rechnungen.toArray(), rechnungen.get(0));
		
		if(null == result) {
			return 16;
		} else {
			return result.getRechnung().getRechnungen_id();
		}
	}

	public JRDataSource createSource(int rechnungId) {

      KlientenEditorPersister klientenEditorPersister = ArbeitRechnungFactory.getInstance().getPersister(KlientenEditorPersister.class);
	   RechnungDialogPersister p = ArbeitRechnungFactory.getInstance().getPersister(RechnungDialogPersister.class);
		Builder rechn = p.getRechnungById(rechnungId);
		rechn.einheiten(p.getEinheiten(rechnungId));
		int klienten_id = p.getKlientenIdForRechnungId(rechnungId);
		Klient klient = klientenEditorPersister.getKlientById(klienten_id);
		rechn.zusatz1Name(klient.getZusatz1_Name());
		rechn.zusatz2Name(klient.getZusatz2_Name());
		p.close();
		klientenEditorPersister.close();
		return createSource(rechn.build());

	}

	public JRDataSource createSource(Rechnung rechnung) {
		return new RechnungDataSource(rechnung);
	}

	public JasperPrint compileAndShowReport(JRDataSource source) throws JRException {
		JasperReport report = JasperCompileManager
				.compileReport(getClass().getResourceAsStream(MTV_JRXML));
		JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), source);
		JasperViewer.viewReport(print, false);

		return print;
	}

	public boolean store(JasperPrint repo, OutputStream outputStream) throws JRException {
		JasperExportManager.exportReportToPdfStream(repo, outputStream);
		return false;
	}

}
