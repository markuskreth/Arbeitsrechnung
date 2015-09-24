package de.kreth.arbeitsrechnungen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

import de.kreth.arbeitsrechnungen.data.Arbeitsstunde;
import de.kreth.arbeitsrechnungen.data.ArbeitsstundeImpl;
import de.kreth.arbeitsrechnungen.data.Rechnung;


public class RechnungDataTest {

   private PdfCreator rechnung;

   private Options optionen;

   @Before
   public void setUp() throws Exception {
      optionen = new Options.Build()
                     .dbHost("localhost")
                     .dbUser("markus")
                     .dbPassword("0773")
                     .dbDatabaseName("arbeitrechnungen")
                     .stdTexFile("Rechnung_Allgemein.tex")
                     .texTemplatesDir("Tex-Vorlagen")
                     .targetDir("targetDir")
                     .tmpDir("tmpDir")
                     .pdfProg("/usr/bin/okular")
                     .build();
      
   }

   @Test
   public void testOptionExistence(){
      File f = optionen.getTargetDir();
      assertTrue(f.exists());
      assertTrue(f.isDirectory());
      f = optionen.getTexTemplatesDir();
      assertTrue(f.exists());
      assertTrue(f.isDirectory());
      f = optionen.getTmpDir();
      assertTrue(f.exists());
      assertTrue(f.isDirectory());
      

      f = new File(optionen.getPdfProg());
      assertTrue(f.exists());
      assertTrue(f.canExecute());
   }
   
   @Test
   public void testMakePdf_buchholz_abrechnung() throws FileNotFoundException {
      boolean zusammenfassungen_erlauben = false;
      boolean stunden = true;
      String zusatz2_name = null;
      String zusatz1_name = "Teilnehmerzahl";
      String rechnungsnr = "Rechnung 1";
      Calendar zahlungsdatum = new GregorianCalendar(2013, Calendar.SEPTEMBER, 21);
      Calendar rechnungsdatum = new GregorianCalendar(2013, Calendar.AUGUST, 21);

      Vector<Arbeitsstunde> einheiten = new Vector<>();
      makeFakeEinheitenBuchholz(einheiten);
      File texFile = new File("./Tex-Vorlagen/buchholz-abrechnung.tex");
      String tex_datei = texFile.getAbsolutePath();
      String adresse = "Markus Kreth\nLister Kirchweg 7\n30163 Hannover";
      int klienten_id = 1;

      Rechnung rech = new Rechnung.Builder()
                           .klienten_id(klienten_id)
                           .adresse(adresse)
                           .texdatei(tex_datei)
                           .einheiten(einheiten)
                           .datum(rechnungsdatum)
                           .zahldatum(zahlungsdatum)
                           .rechnungnr(rechnungsnr)
                           .zusatz1Name(zusatz1_name)
                           .zusatz2Name(zusatz2_name)
                           .zusatz1(false)
                           .zusatz2(false)
                           .stunden(stunden)
                           .zusammenfassungenErlauben(zusammenfassungen_erlauben)
                           .build();

      rechnung = new PdfCreator(rech);
      int makePdf = rechnung.makePdf(optionen);
      assertEquals(0, makePdf);
      assertEquals(expectedLatex_buchholz_abrechnung, rechnung.latexcode);
   }

   @Test
   public void testMakePdf_Rechnung_Allgemein() throws FileNotFoundException {
      boolean zusammenfassungen_erlauben = false;
      boolean stunden = true;
      String rechnungsnr = "Rechnung 1";
      Calendar zahlungsdatum = new GregorianCalendar(2013, Calendar.SEPTEMBER, 21);
      Calendar rechnungsdatum = new GregorianCalendar(2013, Calendar.AUGUST, 21);

      Vector<Arbeitsstunde> einheiten = new Vector<>();
      makeFakeEinheiten(einheiten);
      File texFile = new File("./Tex-Vorlagen/Rechnung_Allgemein.tex");
      String tex_datei = texFile.getAbsolutePath();
      String adresse = "Markus Kreth\nLister Kirchweg 7\n30163 Hannover";
      int klienten_id = 1;

      Rechnung rech = new Rechnung.Builder()
                           .klienten_id(klienten_id)
                           .adresse(adresse)
                           .texdatei(tex_datei)
                           .einheiten(einheiten)
                           .datum(rechnungsdatum)
                           .zahldatum(zahlungsdatum)
                           .rechnungnr(rechnungsnr)
                           .zusatz1(false)
                           .zusatz2(false)
                           .stunden(stunden)
                           .zusammenfassungenErlauben(zusammenfassungen_erlauben)
                           .build();

      rechnung = new PdfCreator(rech);
      int makePdf = rechnung.makePdf(optionen);
      assertEquals(0, makePdf);
      assertEquals(expectedLatex_Rechnung_Allgemein, rechnung.latexcode);
   }

   private void makeFakeEinheitenBuchholz(Vector<Arbeitsstunde> einheiten) {
      einheiten.add(new ArbeitsstundeImpl.Builder(1, 1, 1).datum(new Date(1386856800000L)).inhalt("Sport1").dauerInMinuten(120).einzelPreis(10.5).beginn(new Date(1386856800000L)).ende(new Date(1386856810000L)).zusatz1(String.valueOf(13)).preis(15.6).build());
      einheiten.add(new ArbeitsstundeImpl.Builder(2, 1, 1).datum(new Date(1386943200000L)).inhalt("Sport2").dauerInMinuten(180).einzelPreis(11.5).beginn(new Date(1386943200000L)).ende(new Date(1386943210000L)).zusatz1(String.valueOf(15)).preis(16.7).build());
      einheiten.add(new ArbeitsstundeImpl.Builder(3, 1, 1).datum(new Date(1387029600000L)).inhalt("Sport3").dauerInMinuten(90).einzelPreis(12.5).beginn(new Date(1387029600000L)).ende(new Date(1387029610000L)).zusatz1(String.valueOf(17)).preis(17.8).build());
      einheiten.add(new ArbeitsstundeImpl.Builder(3, 1, 2).datum(new Date(1387202400000L)).inhalt("Sport4").dauerInMinuten(45).einzelPreis(13.5).beginn(new Date(1387202400000L)).ende(new Date(1387202410000L)).zusatz1(String.valueOf(21)).preis(18.9).build());
   }

   private void makeFakeEinheiten(Vector<Arbeitsstunde> einheiten) {
      einheiten.add(new ArbeitsstundeImpl.Builder(1, 1, 1).datum(new Date(1386856800000L)).inhalt("Sport1").dauerInMinuten(120).einzelPreis(10.5).preis(15.6).build());
      einheiten.add(new ArbeitsstundeImpl.Builder(2, 1, 1).datum(new Date(1386943200000L)).inhalt("Sport2").dauerInMinuten(180).einzelPreis(11.5).preis(16.7).build());
      einheiten.add(new ArbeitsstundeImpl.Builder(3, 1, 1).datum(new Date(1387029600000L)).inhalt("Sport3").dauerInMinuten(90).einzelPreis(12.5).preis(17.8).build());
      einheiten.add(new ArbeitsstundeImpl.Builder(3, 1, 2).datum(new Date(1387202400000L)).inhalt("Sport4").dauerInMinuten(45).einzelPreis(13.5).preis(18.9).build());
   }

   @Test
   public void testMakePdfEmpty() throws FileNotFoundException {
      boolean zusammenfassungen_erlauben = false;
      boolean stunden = true;
      String rechnungsnr = "";
      Calendar zahlungsdatum = new GregorianCalendar(2013, Calendar.SEPTEMBER, 21);
      Calendar rechnungsdatum = new GregorianCalendar(2013, Calendar.AUGUST, 21);

      Vector<Arbeitsstunde> einheiten = new Vector<>();
      File texFile = new File("./Tex-Vorlagen/Rechnung_Allgemein.tex");
      String tex_datei = texFile.getAbsolutePath();
      String adresse = "Markus Kreth\nLister Kirchweg 7\n30163 Hannover";
      int klienten_id = 1;

      Rechnung rech = new Rechnung.Builder()
                           .klienten_id(klienten_id)
                           .adresse(adresse)
                           .texdatei(tex_datei)
                           .einheiten(einheiten)
                           .datum(rechnungsdatum)
                           .zahldatum(zahlungsdatum)
                           .rechnungnr(rechnungsnr)
                           .zusatz1(false)
                           .zusatz2(false)
                           .stunden(true)
                           .stunden(stunden)
                           .zusammenfassungenErlauben(zusammenfassungen_erlauben)
                           .build();

      rechnung = new PdfCreator(rech);
      int makePdf = rechnung.makePdf(optionen);
      assertEquals(0, makePdf);
      assertEquals(expectedLatexKeineEinheiten_Rechnung_Allgemein, rechnung.latexcode);
   }

   private String expectedLatex_buchholz_abrechnung = "\\documentclass[a4paper,10pt,BCOR=0mm]{scrreprt}\n"
   +"\\usepackage[utf8]{inputenc}\n"
   +"\\usepackage[ngerman]{babel}\n"
   +"\\usepackage{eurosym}\n"
   +"%\\usepackage[DIV60]{typearea}\n"
   +"\\usepackage{graphicx}\n"
   +"\\usepackage[left=1cm, right=1cm,\n"
   +"  top=0.5cm, bottom=0.5cm, a4paper]{geometry}\n"
   +"\n"
   +"\\begin{document}\n"
   +"\\begin{flushright}\n"
   +"\\parbox{6cm}{Lfd Nr. Rechnungsbuch: \\hrulefill \\\\\n"
   +"Datum Rechnungsbuch: \\hrulefill }\n"
   +"\\end{flushright}\n"
   +"\\begin{center}\n"
   +"\\begin{Large}\\textbf{MTV} Groß-Buchholz von 1898 e.V.                                        \\end{Large}\\\\\n"
   +"Rotekreuzstraße 25 \\textperiodcentered\\ 30627 Hannover\\\\[.4cm]\n"
   +"\\begin{huge}\\underline{Monatsabrechnung Übungsleitervergütung}\\end{huge}\\\\\n"
   +"\\begin{scriptsize}zur Vorlage bei der Landeshauptstadt Hannover\\\\\n"
   +"zur Inanspruchnahme eines städtischen Zuschusses zu den Personalkosten von Sportübungsleitern\\\\[.7cm]\n"
   +"\\end{scriptsize}\n"
   +"\n"
   +"\\parbox{0,49\\textwidth}{\n"
   +"\\begin{center}\n"
   +"\\hrulefill\\underline{Markus Kreth}\\hrulefill\\\\\n"
   +"Vor- u. Zuname des Übungsleiters/ der Übungsleiterin\n"
   +"\\end{center}\n"
   +"}\n"
   +"\\hfill\n"
   +"\\parbox{0,49\\textwidth}{\\begin{center}\n"
   +"\\hrulefill\\underline{Lister Kirchweg 7; 30163 Hannover}\\hrulefill\\\\\n"
   +"Anschrift\\end{center}}\\\\[.7cm]\n"
   +"\\parbox{0,32\\textwidth}{\n"
   +"\\begin{center}\n"
   +"\\hrulefill\\underline{SK Hannover}\\hrulefill\\\\\n"
   +"Bankverbindung\n"
   +"\\end{center}\n"
   +"}\n"
   +"\\hfill\n"
   +"\\parbox{0,32\\textwidth}{\n"
   +"\\begin{center}\n"
   +"\\hrulefill\\underline{37793160}\\hrulefill\\\\\n"
   +"Konto-Nr.\n"
   +"\\end{center}\n"
   +"}\n"
   +"\\hfill\n"
   +"\\parbox{0,32\\textwidth}{\n"
   +"\\begin{center}\n"
   +"\\hrulefill\\underline{25050180}\\hrulefill\\\\\n"
   +"BLZ\n"
   +"\\end{center}\n"
   +"}\n"
   +"\\parbox{12cm}{\\begin{large}\\underline{\\textbf{Abrechnung für Monat: Dezember 2013}}\\end{large}}\\\\\n"
   +"\\parbox{9cm}{Die Vergütung je Stunde beträgt: \\underline{10.50 EUR}}\n"
   +"\\end{center}\\begin{flushright}\n"
   +"\n"
   +"\\begin{tabular}{|c|c|c|c|c|c|}\\hline\n"
   +" \\textbf{Tag}\n"
   +"&\n"
   +" \\textbf{Uhrzeit}\n"
   +"&\n"
   +" \\textbf{Art des Unterrichts}\n"
   +"&\n"
   +"\\textbf{Ort}\n"
   +"&\n"
   +"\\textbf{Teilnehmerzahl}\n"
   +"&\n"
   +" \\textbf{Stundenzahl}\\\\\\hline\\hline\n"
   +"\n"
   +"12.12.2013\n"
   +"&\n"
   +"15:00 - 15:00\n"
   +"&\n"
   +"Trampolin\n"
   +"&\n"
   +"IGS Roderbruch\n"
   +"&\n"
   +"13\n"
   +"&\n"
   +"2.0\n"
   +"\\\\\\hline\n"
   +"\n"
   +"\n"
   +"13.12.2013\n"
   +"&\n"
   +"15:00 - 15:00\n"
   +"&\n"
   +"Trampolin\n"
   +"&\n"
   +"IGS Roderbruch\n"
   +"&\n"
   +"15\n"
   +"&\n"
   +"3.0\n"
   +"\\\\\\hline\n"
   +"\n"
   +"\n"
   +"14.12.2013\n"
   +"&\n"
   +"15:00 - 15:00\n"
   +"&\n"
   +"Trampolin\n"
   +"&\n"
   +"IGS Roderbruch\n"
   +"&\n"
   +"17\n"
   +"&\n"
   +"1.5\n"
   +"\\\\\\hline\n"
   +"\n"
   +"\n"
   +"16.12.2013\n"
   +"&\n"
   +"15:00 - 15:00\n"
   +"&\n"
   +"Trampolin\n"
   +"&\n"
   +"IGS Roderbruch\n"
   +"&\n"
   +"21\n"
   +"&\n"
   +"0.75\n"
   +"\\\\\\hline\n"
   +"\n"
   +"\n"
   +"\n"
   +"\\end{tabular} \n"
   +"\\begin{flushright}\n"
   +"\\parbox{5cm}{\\textbf{Total STD.} 6,00}\\end{flushright}\n"
   +"\\hfill\\hfill \\textbf{Vergütung:} 6,00 Stunden x 10.50 EUR = 69,00 \\officialeuro\\  brutto EUR \\hspace*{2cm}\\\\\n"
   +"\\end{flushright}\n"
   +"\\vfill\n"
   +"Die Richtigkeit der vorstehenden Angaben wird hiermit bestätigt.\\\\\n"
   +"\\begin{center}\n"
   +"\\parbox{6cm}{\n"
   +"\\vspace{9mm}\n"
   +"%\\hline\n"
   +"\\begin{center}\n"
   +"Unterschrift Abt.Leiter/In\n"
   +"\\end{center}\n"
   +"\\vspace{9mm}\n"
   +"%Unterschrift\n"
   +"\\begin{center}\n"
   +"Unterschrift Übungsleiter/In\n"
   +"\\end{center}\n"
   +"}\n"
   +"\\hfill\n"
   +"\\parbox{0.45\\textwidth }{\n"
   +"\\begin{small}\\textbf{Abzüge}\\hfill \\hspace*{2cm}\\\\\n"
   +"(Unter Berücksichtigung des\\hfill \\hspace*{2cm}\\\\\n"
   +"Freibetrages \\S 3 Nr. 26 EStG)\\hfill \\hspace*{2cm}\\\\\n"
   +"\\hspace*{2cm} Sozialversicherung \\hfill \\hspace*{2cm}\\\\\n"
   +"\\hspace*{2cm} \\% KV \\hfill  EUR \\hspace*{2cm} \\\\\n"
   +"\\hspace*{2cm} \\% PV \\hfill  EUR \\hspace*{2cm} \\\\\n"
   +"\\hspace*{2cm} \\% RV \\hfill  EUR \\hspace*{2cm} \\\\\n"
   +"Lohnsteuer  \\hfill \\hfill EUR \\hspace*{2cm} \\\\\n"
   +"Sol.-Steuer  \\hfill \\hfill EUR \\hspace*{2cm} \\\\\n"
   +"LKiSt ev. \\hfill \\hfill EUR \\hspace*{2cm} \\\\\n"
   +"LKiSt kath. \\hfill \\hfill EUR \\hspace*{2cm} \\\\\n"
   +"\\hfill*\\textbf{zu überweisender Betrag: netto EUR} \\hspace*{2cm}\\\\\n"
   +"\\end{small}}\n"
   +"\\end{center}\n"
   +"%\\vfill\n"
   +"\\end{document}          \n";
   
   String expectedLatex_Rechnung_Allgemein = "\\documentclass[a4paper,11pt]{scrlttr2}\n"
   +"\n"
   +"\\usepackage[utf8x]{inputenc}\n"
   +"\\usepackage[ngerman]{babel}\n"
   +"\\usepackage{eurosym}\n"
   +"\\usepackage{graphicx}\n"
   +"\n"
   +"\\setkomavar{backaddress}{M. Kreth, Lister Kirchweg 7, 30163 Hannover}\n"
   +"\\setkomavar{fromaddress}{\\centering \\textbf{Markus Kreth}\\\\Lister Kirchweg 7, 30163 Hannover\\\\Telefon: 0511 - 26 18 291, Email: markus.kreth@web.de}\n"
   +"%\\setkomavar{yourref}{%%yourref%%}\n"
   +"%\\setkomavar{yourmail}{21. Februar 2007}\n"
   +"\\setkomavar{signature}{Markus Kreth}\n"
   +"\\setkomavar{invoice}{Rechnung 1}\n"
   +"\\setkomavar{frombank}{\\begin{footnotesize}\\textbf{Bankverbindung:} Inhaber: Markus Kreth; BLZ: 25050180; Konto: 37793160\\end{footnotesize}}\n"
   +"%\\setkomavar{customer}{%%customer%%}\n"
   +"\\setkomavar{date}{21.08.2013}\n"
   +"\\setkomavar{location}{\\raggedright\n"
   +"Steuernummer: 25/124/04832\\\\\n"
   +"Kein gesonderter Ausweis der Umsatzsteuer \\mbox{(§ 19 UStG)}\n"
   +"}\n"
   +"\\setkomavar{subject}{Rechnung Kreth}\n"
   +"\\begin{document}\n"
   +"\\firstfoot{%\n"
   +"\\parbox[b]{\\linewidth}{%\n"
   +"\\centering\\def\\\\{, }\\usekomavar{frombank}%\n"
   +"}%\n"
   +"}\n"
   +"\\begin{letter}{Markus Kreth\\\\\n"
   +"Lister Kirchweg 7\\\\\n"
   +"30163 Hannover}\n"
   +"\\opening{Sehr geehrte Damen und Herren,}\n"
   +"für die folgenden von mir erbrachten Leistungen erlaube ich mir  für 7,25 Std. insgesamt 69,00\\officialeuro\\ \n"
   +" zu berechnen.\n"
   +"{\\scriptsize\n"
   +"\\begin{tabular}{|l|l|l|r|}\\hline \n"
   +"Datum & Inhalt & Std. & Honorar\\\\\\hline \\hline \n"
   +"12.12.2013 & Sport1 & 2,00 & 15,60 \\officialeuro\\ \\\\\\hline \n"
   +"13.12.2013 & Sport2 & 3,00 & 16,70 \\officialeuro\\ \\\\\\hline \n"
   +"14.12.2013 & Sport3 & 1,50 & 17,80 \\officialeuro\\ \\\\\\hline \n"
   +"16.12.2013 & Sport4 & 0,75 & 18,90 \\officialeuro\\ \\\\\\hline \n"
   +"\\hline & & 7,25 & 69,00 \\officialeuro\\ \\\\\\hline \n"
   +"\\end{tabular}\\\\\n"
   +"}\n"
   +"Bitte überweisen Sie den Betrag bis zum 21.09.2013\n"
   +" auf das unten genannte Konto.\n"
   +"\\closing{Mit freundlichen Grüßen}\n"
   +"\n"
   +"%enclosure listing\n"
   +"%\\encl{%%encl%%}\n"
   +"\n"
   +"\\end{letter}\n"
   +"\\end{document}\n"
   +"";
   
   String expectedLatexKeineEinheiten_Rechnung_Allgemein = "\\documentclass[a4paper,11pt]{scrlttr2}\n"
   +"\n"
   +"\\usepackage[utf8x]{inputenc}\n"
   +"\\usepackage[ngerman]{babel}\n"
   +"\\usepackage{eurosym}\n"
   +"\\usepackage{graphicx}\n"
   +"\n"
   +"\\setkomavar{backaddress}{M. Kreth, Lister Kirchweg 7, 30163 Hannover}\n"
   +"\\setkomavar{fromaddress}{\\centering \\textbf{Markus Kreth}\\\\Lister Kirchweg 7, 30163 Hannover\\\\Telefon: 0511 - 26 18 291, Email: markus.kreth@web.de}\n"
   +"%\\setkomavar{yourref}{%%yourref%%}\n"
   +"%\\setkomavar{yourmail}{21. Februar 2007}\n"
   +"\\setkomavar{signature}{Markus Kreth}\n"
   +"\\setkomavar{invoice}{}\n"
   +"\\setkomavar{frombank}{\\begin{footnotesize}\\textbf{Bankverbindung:} Inhaber: Markus Kreth; BLZ: 25050180; Konto: 37793160\\end{footnotesize}}\n"
   +"%\\setkomavar{customer}{%%customer%%}\n"
   +"\\setkomavar{date}{21.08.2013}\n"
   +"\\setkomavar{location}{\\raggedright\n"
   +"Steuernummer: 25/124/04832\\\\\n"
   +"Kein gesonderter Ausweis der Umsatzsteuer \\mbox{(§ 19 UStG)}\n"
   +"}\n"
   +"\\setkomavar{subject}{Rechnung Kreth}\n"
   +"\\begin{document}\n"
   +"\\firstfoot{%\n"
   +"\\parbox[b]{\\linewidth}{%\n"
   +"\\centering\\def\\\\{, }\\usekomavar{frombank}%\n"
   +"}%\n"
   +"}\n"
   +"\\begin{letter}{Markus Kreth\\\\\n"
   +"Lister Kirchweg 7\\\\\n"
   +"30163 Hannover}\n"
   +"\\opening{Sehr geehrte Damen und Herren,}\n"
   +"für die folgenden von mir erbrachten Leistungen erlaube ich mir  für 0,00 Std. insgesamt 0,00\\officialeuro\\ \n"
   +" zu berechnen.\n"
   +"{\\scriptsize\n"
   +"\\begin{tabular}{|l|l|l|r|}\\hline \n"
   +"Datum & Inhalt & Std. & Honorar\\\\\\hline \\hline \n"
   +"\\hline & & 0,00 & 0,00 \\officialeuro\\ \\\\\\hline \n"
   +"\\end{tabular}\\\\\n"
   +"}\n"
   +"Bitte überweisen Sie den Betrag bis zum 21.09.2013\n"
   +" auf das unten genannte Konto.\n"
   +"\\closing{Mit freundlichen Grüßen}\n"
   +"\n"
   +"%enclosure listing\n"
   +"%\\encl{%%encl%%}\n"
   +"\n"
   +"\\end{letter}\n"
   +"\\end{document}\n"
   +"";
}
