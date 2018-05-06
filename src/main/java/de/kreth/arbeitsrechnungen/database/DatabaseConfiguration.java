package de.kreth.arbeitsrechnungen.database;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfiguration {

	private Logger log = LoggerFactory.getLogger(getClass());
	private Statement stm;
	private String primaryType;
	
	public static boolean initDatabase(Statement stm, String primaryType) throws SQLException {
		DatabaseConfiguration conf = new DatabaseConfiguration();
		conf.stm = stm;
		conf.primaryType = primaryType;
		boolean exists = conf.checkTables();
		if(conf.log.isDebugEnabled()) {
		   conf.log.debug("Database exists=" + exists);
		}
		if (exists == false) {
			conf.createTables();
		}
		return exists;
	}

	List<String> SYSTEM_TABLES = Arrays.asList("INFORMATION_SCHEMA", "SYSTEM_LOBS");
	private boolean checkTables() throws SQLException {
		DatabaseMetaData metaData = stm.getConnection().getMetaData();
		ResultSet tables = metaData.getTables(null, null, null, null);
		boolean exists = false;
		while (tables.next()) {
		   String schema = tables.getString("TABLE_SCHEM");
         if(log.isTraceEnabled()) {
   			String msg = "TABLE_SCHEM=" + schema + "; TABLE_NAME=" + tables.getString("TABLE_NAME");
            log.trace(msg);
		   }
			if(SYSTEM_TABLES.contains(schema) == false) {
				exists = true;
				break;
			}
		}
		tables.close();
		return exists;
	}

	private void createTables() throws SQLException {
		log.info("Creating tables");

		log.trace("creating tables klienten, angebote, einheiten, klienten_angebote, rechnungen, rechnungen_einheiten");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE klienten ( ");
		stringBuilder.append("klienten_id " + primaryType + " NOT NULL, ");
		stringBuilder.append("Kunde varchar(50) DEFAULT NULL, ");
		stringBuilder.append("KAdresse1 varchar(50) DEFAULT NULL, ");
		stringBuilder.append("KAdresse2 varchar(50) DEFAULT NULL, ");
		stringBuilder.append("KPLZ varchar(5) DEFAULT NULL, ");
		stringBuilder.append("KOrt varchar(50) DEFAULT NULL, ");
		stringBuilder.append("KTelefon varchar(30) DEFAULT NULL, ");
		stringBuilder.append("KEmail varchar(30) DEFAULT NULL, ");
		stringBuilder.append("Auftraggeber varchar(50) NOT NULL, ");
		stringBuilder.append("AAdresse1 varchar(50) NOT NULL, ");
		stringBuilder.append("AAdresse2 varchar(50) DEFAULT NULL, ");
		stringBuilder.append("APLZ varchar(5) NOT NULL, ");
		stringBuilder.append("AOrt varchar(50) NOT NULL, ");
		stringBuilder.append("ATelefon varchar(30) DEFAULT NULL, ");
		stringBuilder.append("AEmail varchar(30) DEFAULT NULL, ");
		stringBuilder.append("Bemerkungen varchar(250), ");
		stringBuilder.append("Zusatz1 tinyint DEFAULT NULL, ");
		stringBuilder.append("Zusatz1_Name varchar(100) DEFAULT NULL, ");
		stringBuilder.append("Zusatz2 tinyint DEFAULT NULL, ");
		stringBuilder.append("Zusatz2_Name varchar(100) DEFAULT NULL, ");
		stringBuilder.append("tex_datei varchar(255) DEFAULT NULL, ");
		stringBuilder.append("rechnungnummer_bezeichnung varchar(255) DEFAULT NULL");
		stringBuilder.append(");");
		stm.executeQuery(stringBuilder.toString());
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE angebote (");
		stringBuilder.append("angebote_id ").append(primaryType).append(" NOT NULL, ");
		stringBuilder.append("klienten_id int  DEFAULT NULL, ");
		stringBuilder.append("Inhalt varchar(60) NOT NULL, ");
		stringBuilder.append("Preis float NOT NULL, ");
		stringBuilder.append("preis_pro_stunde tinyint NOT NULL, ");
		stringBuilder.append("Beschreibung varchar(250) ");
		stringBuilder.append(");");
		stm.executeQuery(stringBuilder.toString());
	    
		stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE einheiten (");
		stringBuilder.append("einheiten_id ").append(primaryType).append(" NOT NULL, ");
		stringBuilder.append("klienten_id int NOT NULL, ");
		stringBuilder.append("angebote_id int NOT NULL, ");
		stringBuilder.append("Datum date NOT NULL, ");
		stringBuilder.append("Beginn datetime DEFAULT NULL , ");
		stringBuilder.append("Ende datetime DEFAULT NULL , ");
		stringBuilder.append("Dauer float NOT NULL , ");
		stringBuilder.append("zusatz1 varchar(255) DEFAULT NULL, ");
		stringBuilder.append("zusatz2 varchar(255) DEFAULT NULL, ");
		stringBuilder.append("Preis float NOT NULL , ");
		stringBuilder.append("Preisänderung float NOT NULL , ");
		stringBuilder.append("Rechnung_verschickt boolean DEFAULT NULL, ");
		stringBuilder.append("Rechnung_Datum datetime DEFAULT NULL, ");
		stringBuilder.append("rechnung_id int DEFAULT NULL, ");
		stringBuilder.append("Bezahlt boolean DEFAULT NULL, ");
		stringBuilder.append("Bezahlt_Datum timestamp NULL, ");
		stringBuilder.append("rechnungen_id int DEFAULT NULL ");
		stringBuilder.append(");");
		stm.executeQuery(stringBuilder.toString());
	       
		stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE klienten_angebote (");
		stringBuilder.append("klienten_angebote_id ").append(primaryType).append(" NOT NULL, ");
		stringBuilder.append("klienten_id int NOT NULL, ");
		stringBuilder.append("angebote_id int NOT NULL, ");
		stringBuilder.append("UNIQUE (klienten_id,angebote_id));");
		stm.executeQuery(stringBuilder.toString());
	      
		stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE rechnungen ( ");
		stringBuilder.append("rechnungen_id ").append(primaryType).append(" NOT NULL, ");
		stringBuilder.append("klienten_id int NOT NULL, ");
		stringBuilder.append("datum datetime NOT NULL, ");
		stringBuilder.append("rechnungnr varchar(255) NOT NULL, ");
		stringBuilder.append("betrag float NOT NULL, ");
		stringBuilder.append("texdatei varchar(255) NOT NULL, ");
		stringBuilder.append("pdfdatei varchar(255) NOT NULL, ");
		stringBuilder.append("adresse varchar(255) NOT NULL, ");
		stringBuilder.append("zusatz1 boolean NOT NULL, ");
		stringBuilder.append("zusatz2 boolean NOT NULL, ");
		stringBuilder.append("zusammenfassungen boolean NOT NULL, ");
		stringBuilder.append("zahldatum date NOT NULL, ");
		stringBuilder.append("geldeingang date DEFAULT NULL, ");
		stringBuilder.append("timestamp timestamp NOT NULL ");
		stringBuilder.append(");");
		stm.executeQuery(stringBuilder.toString());
	      
		stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE TABLE rechnungen_einheiten ( ");
		stringBuilder.append("rechnungen_einheiten_id ").append(primaryType).append(" NOT NULL, ");
		stringBuilder.append("rechnungen_id int NOT NULL, ");
		stringBuilder.append("einheiten_id int NOT NULL ");
		stringBuilder.append(");");
		stm.executeQuery(stringBuilder.toString());
	}
}
