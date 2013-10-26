package de.kreth.arbeitsrechnungen.persister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import de.kreth.arbeitsrechnungen.data.Angebot;


public class AngebotPersister extends AbstractPersister {

	public AngebotPersister(Properties optionen) {
		super(optionen);
	}

	public Angebot getAngebot(int angebotId){
		ResultSet rs;
        String sqltext = "SELECT angebote_id, klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung  FROM angebote WHERE angebote_id=" + angebotId + ";";

        System.out.println("AngebotDialog: " + sqltext);
    
    	Angebot angebot = null;
    	
        try{
        	rs = verbindung.query(sqltext);
        
            rs.first();
            String inhalt = rs.getString("Inhalt");
            float preis = rs.getFloat("Preis");
            String beschr = rs.getString("Beschreibung");
            boolean preisPHour = rs.getInt("preis_pro_stunde")==1;
            
            angebot = new Angebot.Builder(inhalt, preis).angebotId(angebotId).beschreibung(beschr).preis_pro_stunde(preisPHour).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return angebot;
	}
	
	public void insertOrUpdateAngebot(int klientId, Angebot angebot){
		String sql;
		if(angebot.getAngebote_id() <0)
			sql = insertAngebot(klientId, angebot);
		else 
			sql = updateAngebot(klientId, angebot);

		logger.info(sql);
		try {
			verbindung.sql(sql);
		} catch (SQLException e) {
			logger.error("insertOrUpdateAngebot", e);
		}
	}
	
	private String insertAngebot(int klientId, Angebot angebot){
		String sqltext;

		sqltext = "INSERT INTO angebote (klienten_id, Inhalt, Preis, preis_pro_stunde, Beschreibung) "
				+ "VALUES ("
				+ klientId
				+ ","
				+ "\""
				+ angebot.getInhalt()
				+ "\","
				+ angebot.getPreis()
				+ ","
				+ (angebot.isPreis_pro_stunde()?1:0)
				+ ", "
				+ "\""
				+ angebot.getBeschreibung() + "\");";

		return sqltext;
	}
	
	private String updateAngebot(int klientId, Angebot angebot){

		String sqltext;

		sqltext = "UPDATE angebote SET " + "Inhalt=\""
				+ angebot.getInhalt() + "\", " + "Preis="
				+ angebot.getPreis() + ", " + "Beschreibung=\""
				+ angebot.getBeschreibung() + "\", "
				+ "preis_pro_stunde=" + (angebot.isPreis_pro_stunde()?1:0)
				+ " WHERE angebote_id=" + angebot.getAngebote_id() + ";";
		return sqltext;
	}
}
