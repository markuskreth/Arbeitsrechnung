package de.kreth.arbeitsrechnungen.data;

public class Angebot {

   private int angebote_id;
   private String inhalt;
   private double preis;
   private String beschreibung;
   private boolean preis_pro_stunde;

   private Angebot(Builder build) {
      angebote_id = build.angebote_id;
      inhalt = build.inhalt;
      preis = build.preis;
      beschreibung = build.beschreibung;
      preis_pro_stunde = build.preis_pro_stunde;
   }

   public static class Builder implements de.kreth.arbeitsrechnungen.Builder<Angebot> {

      private int angebote_id = -1;
      private String inhalt;
      private double preis;
      private String beschreibung = "";
      private boolean preis_pro_stunde = false;

      public Builder(String inhalt, double preis) {
         this.inhalt = inhalt;
         this.preis = preis;
      }

      public Builder angebotId(int angebotId) {
         this.angebote_id = angebotId;
         return this;
      }

      public Builder beschreibung(String beschreibung) {
         this.beschreibung = beschreibung;
         return this;
      }

      public Builder preis_pro_stunde(boolean preis_pro_stunde) {
         this.preis_pro_stunde = preis_pro_stunde;
         return this;
      }

      @Override
      public Angebot build() {
         return new Angebot(this);
      }

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
