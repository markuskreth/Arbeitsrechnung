package de.kreth.arbeitsrechnungen.data;

public class Angebot {
	private int angebote_id;
	private String inhalt;
	private double preis;
	private String beschreibung;
	private boolean preis_pro_stunde;
	
	public static Angebot createAngebot(int angebote_id, String inhalt, double preis,
			String beschreibung, boolean preis_pro_stunde) {
		Angebot a = new Angebot();
		a.angebote_id = angebote_id;
		a.inhalt = inhalt;
		a.preis = preis;
		a.beschreibung = beschreibung;
		a.preis_pro_stunde = preis_pro_stunde;
		return a;
	}
	
	public boolean isPreis_pro_stunde() {
		return preis_pro_stunde;
	}

	public int getAngebote_id() {
		return angebote_id;
	}
	public String getInhalt() {
		return inhalt;
	}
	public double getPreis() {
		return preis;
	}
	public String getBeschreibung() {
		return beschreibung;
	}
	
	
}
