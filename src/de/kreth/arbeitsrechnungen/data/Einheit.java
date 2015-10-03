package de.kreth.arbeitsrechnungen.data;

import java.util.Date;

public class Einheit {

   private String auftraggeber;
   private int anzahl;
   private double klientenpreis;
   private int id;
   private int angebotId;
   private Date beginn;
   private int klientenId;
   private Date ende;
   private Date datum;
   private double preisAenderung;
   private Date rechnungDatum;
   private String zusatz1;
   private String zusatz2;
   private Date bezahltDatum;

   public Date getBezahltDatum() {
      return bezahltDatum;
   }

   public String getZusatz2() {
      return zusatz2;
   }

   public String getZusatz1() {
      return zusatz1;
   }

   public Date getRechnungDatum() {
      return rechnungDatum;
   }

   public double getPreisAenderung() {
      return preisAenderung;
   }

   public Date getDatum() {
      return datum;
   }

   public Date getEnde() {
      return ende;
   }

   public int getKlientenId() {
      return klientenId;
   }

   public int getAngebotId() {
      return angebotId;
   }

   public Date getBeginn() {
      return beginn;
   }

   public String getAuftraggeber() {
      return auftraggeber;
   }

   public int getAnzahl() {
      return anzahl;
   }

   public double getKlientenpreis() {
      return klientenpreis;
   }

   public int getId() {
      return id;
   }

   @Override
   public String toString() {
      return auftraggeber + "(" + anzahl + "): " + klientenpreis + " €";
   }

   public static class Builder implements de.kreth.arbeitsrechnungen.Builder<Einheit> {

      private String auftraggeber;
      private int anzahl;
      private double klientenpreis;
      private int id;
      private int angebotId;
      private Date beginn;
      private int klientenId;
      private Date ende;
      private Date datum;
      private double preisAenderung;
      private Date rechnungDatum;
      private String zusatz1;
      private String zusatz2;
      private Date bezahltDatum;

      public Builder auftraggeber(String auftraggeber) {
         this.auftraggeber = auftraggeber;
         return this;
      }

      public Builder anzahl(int anzahl) {
         this.anzahl = anzahl;
         return this;
      }

      public Builder klientenpreis(double klientenpreis) {
         this.klientenpreis = klientenpreis;
         return this;
      }

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder angebotId(int angebotId) {
         this.angebotId = angebotId;
         return this;
      }

      public Builder beginn(Date beginn) {
         this.beginn = beginn;
         return this;
      }

      public Builder klientenId(int klientenId) {
         this.klientenId = klientenId;
         return this;
      }

      public Builder ende(Date ende) {
         this.ende = ende;
         return this;
      }

      public Builder datum(Date datum) {
         this.datum = datum;
         return this;
      }

      public Builder preisAenderung(double preisAenderung) {
         this.preisAenderung = preisAenderung;
         return this;
      }

      public Builder rechnungDatum(Date rechnungDatum) {
         this.rechnungDatum = rechnungDatum;
         return this;
      }

      public Builder zusatz1(String zusatz1) {
         this.zusatz1 = zusatz1;
         return this;
      }

      public Builder zusatz2(String zusatz2) {
         this.zusatz2 = zusatz2;
         return this;
      }

      public Builder bezahltDatum(Date bezahltDatum) {
         this.bezahltDatum = bezahltDatum;
         return this;
      }

      @Override
      public Einheit build() {
         return new Einheit(this);
      }
   }

   private Einheit(Builder builder) {
      this.auftraggeber = builder.auftraggeber;
      this.anzahl = builder.anzahl;
      this.klientenpreis = builder.klientenpreis;
      this.id = builder.id;
      this.angebotId = builder.angebotId;
      this.beginn = builder.beginn;
      this.klientenId = builder.klientenId;
      this.ende = builder.ende;
      this.datum = builder.datum;
      this.preisAenderung = builder.preisAenderung;
      this.rechnungDatum = builder.rechnungDatum;
      this.zusatz1 = builder.zusatz1;
      this.zusatz2 = builder.zusatz2;
      this.bezahltDatum = builder.bezahltDatum;
   }
}
