package de.kreth.arbeitsrechnungen.persister;

import java.util.Properties;

import org.apache.logging.log4j.LoggerFactory;
import org.slf4j.Logger;

import arbeitsabrechnungendataclass.Verbindung;
import arbeitsabrechnungendataclass.Verbindung_mysql;

/**
 * Stellt die Db-Verbindung her und stellt logger-Objekt zur Verfügung.
 * 
 * @author markus
 */
public final class DatabaseConnector {

   private final Logger logger = LoggerFactory.getLogger(getClass());
   private static Verbindung verbindung = null;

   public DatabaseConnector(Properties optionen) {
      super();
      this.options = optionen;
   }

//   private void checkVersion() {
//      String sql = "\n" + 
//            "CREATE TABLE klienten (\n" + 
//            "  klienten_id int IDENTITY NOT NULL ,\n" + 
//            "  AAdresse1 varchar(50) NOT NULL,\n" + 
//            "  AAdresse2 varchar(50) DEFAULT NULL,\n" + 
//            "  AEmail varchar(30) DEFAULT NULL,\n" + 
//            "  AOrt varchar(50) NOT NULL,\n" + 
//            "  APLZ varchar(5) NOT NULL,\n" + 
//            "  ATelefon varchar(30) DEFAULT NULL,\n" + 
//            "  Auftraggeber varchar(50) NOT NULL,\n" + 
//            "  Bemerkungen varchar(255),\n" + 
//            "  KAdresse1 varchar(50) DEFAULT NULL,\n" + 
//            "  KAdresse2 varchar(50) DEFAULT NULL,\n" + 
//            "  KEmail varchar(30) DEFAULT NULL,\n" + 
//            "  KOrt varchar(50) DEFAULT NULL,\n" + 
//            "  KPLZ varchar(5) DEFAULT NULL,\n" + 
//            "  KTelefon varchar(30) DEFAULT NULL,\n" + 
//            "  Kunde varchar(50) DEFAULT NULL,\n" + 
//            "  rechnungnummer_bezeichnung varchar(255) DEFAULT NULL,\n" + 
//            "  tex_datei varchar(255) DEFAULT NULL,\n" + 
//            "  Zusatz1 bit(1) DEFAULT NULL,\n" + 
//            "  Zusatz1_Name varchar(100) DEFAULT NULL,\n" + 
//            "  Zusatz2 bit(1) DEFAULT NULL,\n" + 
//            "  Zusatz2_Name varchar(100) DEFAULT NULL\n" + 
//            ")\n" + 
//            "\n" + 
//            "CREATE TABLE angebote (\n" + 
//            "  angebote_id int IDENTITY NOT NULL,\n" + 
//            "  Beschreibung varchar(255) DEFAULT NULL,\n" + 
//            "  Inhalt varchar(60) NOT NULL,\n" + 
//            "  Preis float NOT NULL,\n" + 
//            "  preis_pro_stunde bit(1) NOT NULL,\n" + 
//            "  klienten_id int NOT NULL,\n" + 
//            "  FOREIGN KEY (klienten_id) REFERENCES klienten (klienten_id)\n" + 
//            ") \n" + 
//            "\n" + 
//            "CREATE TABLE rechnungen (\n" + 
//            "  rechnungen_id int IDENTITY NOT NULL,\n" + 
//            "  adresse varchar(1240) NOT NULL,\n" + 
//            "  betrag float NOT NULL,\n" + 
//            "  datum datetime NOT NULL,\n" + 
//            "  geldeingang date DEFAULT NULL,\n" + 
//            "  pdfdatei varchar(255) NOT NULL,\n" + 
//            "  rechnungnr varchar(255) NOT NULL,\n" + 
//            "  texdatei varchar(255) NOT NULL,\n" + 
//            "  timestamp datetime NOT NULL,\n" + 
//            "  zahldatum date NOT NULL,\n" + 
//            "  zusammenfassungen bit(1) NOT NULL,\n" + 
//            "  zusatz1 bit(1) NOT NULL,\n" + 
//            "  zusatz2 bit(1) NOT NULL,\n" + 
//            "  klienten_id int NOT NULL,\n" + 
//            "  FOREIGN KEY (klienten_id) REFERENCES klienten (klienten_id)\n" + 
//            ")\n" + 
//            "\n" + 
//            "CREATE TABLE einheiten (\n" + 
//            "  einheiten_id int IDENTITY NOT NULL ,\n" + 
//            "  angebote_id int NOT NULL,\n" + 
//            "  Beginn datetime DEFAULT NULL,\n" + 
//            "  Bezahlt bit(1) DEFAULT NULL,\n" + 
//            "  Bezahlt_Datum datetime DEFAULT NULL,\n" + 
//            "  Datum date NOT NULL,\n" + 
//            "  Dauer float NOT NULL,\n" + 
//            "  Ende datetime DEFAULT NULL,\n" + 
//            "  Preis float NOT NULL,\n" + 
//            "  Preisänderung float NOT NULL,\n" + 
//            "  Rechnung_Datum datetime DEFAULT NULL,\n" + 
//            "  rechnung_id int DEFAULT NULL,\n" + 
//            "  Rechnung_verschickt bit(1) DEFAULT NULL,\n" + 
//            "  zusatz1 varchar(255) DEFAULT NULL,\n" + 
//            "  zusatz2 varchar(255) DEFAULT NULL,\n" + 
//            "  klienten_id int DEFAULT NULL,\n" + 
//            "  FOREIGN KEY (rechnung_id) REFERENCES rechnungen (rechnungen_id),\n" + 
//            "  FOREIGN KEY (klienten_id) REFERENCES klienten (klienten_id)\n" + 
//            ")\n" + 
//            "\n" + 
//            "CREATE TABLE klienten_angebote (\n" + 
//            "  klienten_angebote_id int IDENTITY NOT NULL,\n" + 
//            "  angebote_id int NOT NULL,\n" + 
//            "  klienten_id int NOT NULL,\n" + 
//            "  CONSTRAINT UK_4j35cpa5s03lfok8xvppta8wo UNIQUE (klienten_id,angebote_id)\n" + 
//            ")\n" + 
//            "\n" + 
//            "CREATE TABLE rechnungen_einheiten (\n" + 
//            "  rechnungen_einheiten_id int IDENTITY NOT NULL,\n" + 
//            "  einheiten_id int NOT NULL,\n" + 
//            "  rechnungen_id int NOT NULL\n" + 
//            ")\n" + 
//            "";
//   }

   public Verbindung getVerbindung() {
      return verbindung;
   }

}
