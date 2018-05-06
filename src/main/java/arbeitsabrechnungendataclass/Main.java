
package arbeitsabrechnungendataclass;

/**
 *
 * @author markus
 */
import java.sql.ResultSet;
import java.util.Vector;

public class Main {

    Verbindung_hsqldb verbindung;
    ResultSet datenmenge;
    String akt_tabelle = null;

    int id = 0;
    Vector<String> tabellenliste = new Vector<String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Testprogramm
        Main app = new Main();
        app.gibTabellen();
//        app.testread();
//        app.testwrite();
//        app.testwrite();
//        app.testread();
//        app.testdelete();
        app.testread();
    }
/**
    public Vector VAbfrage(String sqltext) {
        /**
         * Eine Sql-Abfrage wird ausgeführt und jede Spalte wird in einem Vector abgelegt
         * Die Sql-Abfrage muss übergeben werden.
         * die Vectoren werden in einem Ergebnisvector gespeichert, der dann zurückgegeben wird.
         * Das Ergebnis besteht also aus einem Vector von Vectoren.
         * /
        Vector ergebnis = new Vector();
        try {
            datenmenge = verbindung.query(sqltext);
            while (datenmenge.next()) {
                Vector zeile = new Vector();
//                zeile.add(datenmenge.);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ergebnis; // dieses ergebnis ist leer, weil ein Fehler aufgetreten ist.
        }

        return ergebnis;
    }
*/
    public Main() {
        
        verbindung = new Verbindung_hsqldb("ArbeitrechnungenTestDaten");

        // Tabellenliste füllen
//        datenmenge = verbindung.query("SHOW TABLES;");

	verbindung.sql("CREATE TABLE klienten ( "
		+ "klienten_id int NOT NULL, "
		+ "Kunde varchar(50) DEFAULT NULL, "
		+ "KAdresse1 varchar(50) DEFAULT NULL, "
		+ "KAdresse2 varchar(50) DEFAULT NULL, "
		+ "KPLZ varchar(5) DEFAULT NULL, "
		+ "KOrt varchar(50) DEFAULT NULL, "
		+ "KTelefon varchar(30) DEFAULT NULL, "
		+ "KEmail varchar(30) DEFAULT NULL, "
		+ "Auftraggeber varchar(50) NOT NULL, "
		+ "AAdresse1 varchar(50) NOT NULL, "
		+ "AAdresse2 varchar(50) DEFAULT NULL, "
		+ "APLZ varchar(5) NOT NULL, "
		+ "AOrt varchar(50) NOT NULL, "
		+ "ATelefon varchar(30) DEFAULT NULL, "
		+ "AEmail varchar(30) DEFAULT NULL, "
		+ "Bemerkungen varchar(250), "
		+ "Zusatz1 tinyint DEFAULT NULL, "
		+ "Zusatz1_Name varchar(100) DEFAULT NULL, "
		+ "Zusatz2 tinyint DEFAULT NULL, "
		+ "Zusatz2_Name varchar(100) DEFAULT NULL, "
		+ "tex_datei varchar(255) DEFAULT NULL, "
		+ "rechnungnummer_bezeichnung varchar(255) DEFAULT NULL,"
		+ "PRIMARY KEY (klienten_id));");
	verbindung.sql("CREATE TABLE angebote (\n"
		+ "angebote_id int  NOT NULL, \n"
		+ "klienten_id int  DEFAULT NULL, \n"
		+ "Inhalt varchar(60) NOT NULL, \n"
		+ "Preis float NOT NULL, \n"
		+ "preis_pro_stunde tinyint NOT NULL, \n"
		+ "Beschreibung varchar(250), \n"
		+ "PRIMARY KEY (angebote_id));");
	verbindung.sql("CREATE TABLE einheiten (\n"
		+ "einheiten_id int NOT NULL, \n"
		+ "klienten_id int NOT NULL, \n"
		+ "angebote_id int NOT NULL, \n"
		+ "Datum date NOT NULL, \n"
		+ "Beginn datetime DEFAULT NULL , \n"
		+ "Ende datetime DEFAULT NULL , \n"
		+ "Dauer float NOT NULL , \n"
		+ "zusatz1 varchar(255) DEFAULT NULL, \n"
		+ "zusatz2 varchar(255) DEFAULT NULL, \n"
		+ "Preis float NOT NULL , \n"
		+ "Preisänderung float NOT NULL , \n"
		+ "Rechnung_verschickt boolean DEFAULT NULL, \n"
		+ "Rechnung_Datum datetime DEFAULT NULL, \n"
		+ "rechnung_id int DEFAULT NULL, \n"
		+ "Bezahlt boolean DEFAULT NULL, \n"
		+ "Bezahlt_Datum timestamp NULL, \n"
		+ "rechnungen_id int DEFAULT NULL, \n"
		+ "PRIMARY KEY (einheiten_id));");
	verbindung.sql("CREATE TABLE klienten_angebote (\n"
		+ "klienten_angebote_id int NOT NULL , \n"
		+ "klienten_id int NOT NULL, \n"
		+ "angebote_id int NOT NULL, \n"
		+ "PRIMARY KEY (klienten_angebote_id), \n"
		+ "UNIQUE (klienten_id,angebote_id));");
	verbindung.sql("CREATE TABLE rechnungen ( \n"
		+ "rechnungen_id int NOT NULL, \n"
		+ "klienten_id int NOT NULL, \n"
		+ "datum datetime NOT NULL, \n"
		+ "rechnungnr varchar(255) NOT NULL, \n"
		+ "betrag float NOT NULL, \n"
		+ "texdatei varchar(255) NOT NULL, \n"
		+ "pdfdatei varchar(255) NOT NULL, \n"
		+ "adresse varchar(255) NOT NULL, \n"
		+ "zusatz1 boolean NOT NULL, \n"
		+ "zusatz2 boolean NOT NULL, \n"
		+ "zusammenfassungen boolean NOT NULL, \n"
		+ "zahldatum date NOT NULL, \n"
		+ "geldeingang date DEFAULT NULL, \n"
		+ "timestamp timestamp NOT NULL , \n"
		+ "PRIMARY KEY (rechnungen_id));");
	verbindung.sql("CREATE TABLE rechnungen_einheiten ( "
		+ "rechnungen_einheiten_id bigint NOT NULL, "
		+ "rechnungen_id int NOT NULL, "
		+ "einheiten_id int NOT NULL, "
		+ "PRIMARY KEY (rechnungen_einheiten_id));");
	verbindung.sql("insert into rechnungen_einheiten values (1, 5, 5);");
	verbindung.sql("insert into angebote values (1, 5, 'ANgebot', 20.5, 1, 'Ein Test');");

        verbindung.sql("INSERT INTO klienten \n VALUES(1, \n'Berufsfeuerwehr Hannover', \n'', \n'', \n'     ', \n'', '', '', \n'Gesunde Karriere', \n'Mirja Massen', \n'Georgstr. 50 B', '30159', \n'Hannover', '', '', '', \n1, 'Wache/ Ausfall', \n1, 'Teilnehmerzahl', \n'/home/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/Rechnung_Allgemein.tex', 'Feuerwehr Hannover');");
	verbindung.sql("\nINSERT INTO klienten VALUES(2, 'Solfit', '', '', '     ', '', '', '', 'Solvay GmbH', 'z.Hd. Herrn Michael Busche', 'Hans-Böckler-Allee 20', '30173', 'Hannover', '', '', '', 1, 'Kursname', NULL, NULL, '/home/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/Rechnung_Solfit.tex', NULL);");
	verbindung.sql("\nINSERT INTO klienten \n(klienten_id, Kunde, KAdresse1, KAdresse2, KPLZ, KOrt, KTelefon, KEmail, Auftraggeber, AAdresse1, AAdresse2, APLZ, AOrt, ATelefon, AEmail, Bemerkungen, Zusatz1, Zusatz1_Name, Zusatz2, Zusatz2_Name, tex_datei, rechnungnummer_bezeichnung)\n"
                + " VALUES(26, '', '', '', '     ', '', '', \nNULL, \n'Hochschulsport Hannover', \n'Im Moritzwinkel 6', '', '30159', 'Hannover', \nNULL, NULL, NULL, 1, 'Anzahl Teilnehmer', 1, 'Kursnr.', NULL, '');");
/*	verbindung.sql("\nINSERT INTO klienten \n"
                + "(`klienten_id`, `Kunde`, `KAdresse1`, `KAdresse2`, `KPLZ`, `KOrt`, `KTelefon`, `KEmail`, `Auftraggeber`, `AAdresse1`, `AAdresse2`, `APLZ`, `AOrt`, `ATelefon`, `AEmail`, `Bemerkungen`, `Zusatz1`, `Zusatz1_Name`, `Zusatz2`, `Zusatz2_Name`, `tex_datei`, `rechnungnummer_bezeichnung`)\n"
                + " VALUES(28, '', \nNULL, '', '     ', '', '', \nNULL, \n'MTV Groß-Buchholz', \n'Rotekreuzstr. 25', '', \n'30627', 'Hannover', NULL, NULL, NULL, \n"
                + "1, 'Teilnehmerzahl', 0, \n'leer', \n"
                + "'/home/markus/programming/NetBeansProjects/Arbeitsrechnungen/test/buchholz-abrechnung.tex'\n"
                + ", NULL);");
 *
 */
//	verbindung.sql("\nINSERT INTO `klienten` (`klienten_id`, `Kunde`, `KAdresse1`, `KAdresse2`, `KPLZ`, `KOrt`, `KTelefon`, `KEmail`, `Auftraggeber`, `AAdresse1`, `AAdresse2`, `APLZ`, `AOrt`, `ATelefon`, `AEmail`, `Bemerkungen`, `Zusatz1`, `Zusatz1_Name`, `Zusatz2`, `Zusatz2_Name`, `tex_datei`, `rechnungnummer_bezeichnung`) VALUES(35, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Jugendzentrum Emplelde', 'Auf dem Rade 3', NULL, '30952', 'Ronnenberg', '0511 - 4383359-0', 'juzempelde@gmx.de', 'Öffnungszeiten: Mo - Do 14 - 19 Uhr (Offene Tür)\nMo - Do 15 - 18 Uhr (Netzbutze)', NULL, NULL, NULL, NULL, '/home/data/markus/programming/NetBeansProjects/Arbeitsrechnungen/Tex-Vorlagen/Rechnung_Allgemein.tex', NULL);");
//	verbindung.sql("\nINSERT INTO `klienten` (`klienten_id`, `Kunde`, `KAdresse1`, `KAdresse2`, `KPLZ`, `KOrt`, `KTelefon`, `KEmail`, `Auftraggeber`, `AAdresse1`, `AAdresse2`, `APLZ`, `AOrt`, `ATelefon`, `AEmail`, `Bemerkungen`, `Zusatz1`, `Zusatz1_Name`, `Zusatz2`, `Zusatz2_Name`, `tex_datei`, `rechnungnummer_bezeichnung`) VALUES(36, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'sonja', 'vertretung', NULL, '00000', 'Ort eingeben', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);");
//	verbindung.sql("\nINSERT INTO `klienten` (`klienten_id`, `Kunde`, `KAdresse1`, `KAdresse2`, `KPLZ`, `KOrt`, `KTelefon`, `KEmail`, `Auftraggeber`, `AAdresse1`, `AAdresse2`, `APLZ`, `AOrt`, `ATelefon`, `AEmail`, `Bemerkungen`, `Zusatz1`, `Zusatz1_Name`, `Zusatz2`, `Zusatz2_Name`, `tex_datei`, `rechnungnummer_bezeichnung`) VALUES(37, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Opexx', 'Bayernstr. 21', NULL, '30855', 'Langenhagen', '0511-63 28 39 ', 'Nickart@OPExx-Hannover.de', 'Jürgen Nickart, Geschäftsführer', NULL, NULL, NULL, NULL, NULL, NULL);");
	verbindung.sql("INSERT INTO PUBLIC.einheiten VALUES(49, 1, 3, '2009-06-24', '2009-06-24 08:00:00', '2009-06-24 09:00:00', 0, 'Wache 1', 'ausgefallen', 13, -5, 1, '2009-07-08 00:00:00', NULL, 1, '2009-07-24 00:00:00', NULL);");

	/**        try {
            while (datenmenge.next()) {
                tabellenliste.add(datenmenge.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 */
  }

    public void testwrite(){
        /**
         * INSERT INTO klienten (Kunde, Auftraggeber, AAdresse1, APLZ, AOrt) VALUES ("TestKunde", "TestAuftraggeber");
         * soll ausgeführt werden.
         */

        verbindung.sql("INSERT INTO disziplinen VALUES (" + id++ + ", 'Testwettkampf', 15, 10, 1, 3,1);");
        verbindung.sql("INSERT INTO disziplinen VALUES (" + id++ + ", 'Testwettkampf2', 15, 10, 1, 3,1);");
        verbindung.sql("INSERT INTO disziplinen VALUES (" + id++ + ", 'Testwettkampf3', 15, 10, 1, 3,1);");
    }

    public void testdelete(){
        /**
         * Datensätze mit Kunde = TestKunde werden gelöscht
         */
        verbindung.sql("DELETE FROM klienten WHERE kunde=\"TestKunde\"");
    }
    public void testread() {
        /**
         * "SELECT klienten_id, Kunde, Auftraggeber FROM klienten;
         * wird am Bildschirm ausgegeben.
         */
        try {
            SqlText sqltext;
            sqltext = new SqlText("klienten","*");
            sqltext.setWhere("");
            System.out.println(sqltext.select());

            datenmenge = verbindung.query(sqltext.select());
            // Überschriften ausgeben
            System.out.println("");
            System.out.println("ID\tKunde\t\t\t\tAuftraggeber");
            System.out.println("");

            while (datenmenge.next()) {
//                id = datenmenge.getString(0);
//                kunde = datenmenge.getString("Kunde");
//                auftraggeber = datenmenge.getString("Auftraggeber");
                System.out.println(datenmenge.getString(1) + "\t" + datenmenge.getString(2) + "\t\t\t\t" + datenmenge.getString(3));
//                tabellenliste.add(kunde);
            }
//            verbindung.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gibTabellen() {
        // Gibt die Namen der vorhandenen Tabellen aus
        // Wenn die tabellenliste nicht leer ist
        java.sql.DatabaseMetaData metaData = verbindung.getMetaData();

        System.out.println("Ausgabe der Tabellen: ");
                try{
            java.sql.ResultSet tableResult = metaData.getTables(null, null, "DISZIPLINEN", null);

            while (tableResult.next()) {
                System.out.println(
//                        tableResult.getString(1) + "\t" +
//                        tableResult.getString(2) + "\t" +
                        tableResult.getString(3)
                );
                tabellenliste.add(tableResult.getString(3));
            }
        }catch(java.sql.SQLException ex)
        {
           System.err.println(ex);   
        }

        if (!tabellenliste.isEmpty()) {
            System.out.println("Tabellenname");
            System.out.println("");
            for (int i = 0; i < tabellenliste.size(); i++) {
                System.out.println(tabellenliste.elementAt(i));
            }
            System.out.println("");
        } else {
            System.out.println("Keine Tabellen gespeichert!");
            System.out.println("");
        }
    }
}
