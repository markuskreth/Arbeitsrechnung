package de.kreth.arbeitsrechnungen.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * @author markus
 */
public class Rechnung {

   private BigDecimal betrag;
   private int rechnungen_id;
   private int klienten_id;
   private Calendar datum;
   private String rechnungnr;
   private String adresse;
   private String texdatei;
   private String pdfdatei;
   private boolean zusatz1;
   private boolean zusatz2;
   private String zusatz1_name;
   private String zusatz2_name;
   private boolean zusammenfassungenErlauben;
   private boolean stunden;
   private boolean isNew;

   private Calendar geldeingang = null;
   private Calendar zahldatum;
   private Vector<Arbeitsstunde> einheiten;

   private Rechnung(Builder builder) {
      this.betrag = builder.betrag;
      this.rechnungen_id = builder.rechnungen_id;
      this.klienten_id = builder.klienten_id;
      this.datum = builder.datum;
      this.rechnungnr = builder.rechnungnr;
      this.adresse = builder.adresse;
      this.texdatei = builder.texdatei;
      this.pdfdatei = builder.pdfdatei;
      this.zusatz1 = builder.zusatz1;
      this.zusatz2 = builder.zusatz2;
      this.zusammenfassungenErlauben = builder.zusammenfassungen;
      this.geldeingang = builder.geldeingang;
      this.zusatz1_name = builder.zusatz1_name;
      this.zusatz2_name = builder.zusatz2_name;
      this.zahldatum = builder.zahldatum;
      this.stunden = builder.stunden;
      this.zahldatum = builder.zahldatum;
      this.einheiten = builder.einheiten;
      this.isNew = builder.isNew;
   }

   public static final String PROP_ZAHLDATUM = "zahldatum";
   public static final String PROP_ZUSAMMENFASSUNGEN = "zusammenfassungen";
   public static final String PROP_ZUSATZ1 = "zusatz1";
   public static final String PROP_PDFDATEI = "pdfdatei";
   public static final String PROP_RECHNUNGEN_ID = "rechnungen_id";
   public static final String PROP_KLIENTEN_ID = "klienten_id";
   public static final String PROP_RECHNUNGNR = "rechnungnr";
   public static final String PROP_TEXDATEI = "texdatei";
   public static final String PROP_DATUM = "datum";
   public static final String PROP_BETRAG = "betrag";
   public static final String PROP_ZUSATZ2 = "zusatz2";
   public static final String PROP_ADRESSE = "adresse";
   public static final String PROP_GELDEINGANG = "geldeingang";

   public boolean isZusatz1() {
      return zusatz1;
   }

   public boolean isZusatz2() {
      return zusatz2;
   }

   public String getZusatz1_name() {
      return zusatz1_name;
   }

   public String getZusatz2_name() {
      return zusatz2_name;
   }

   public boolean isZusammenfassungenErlauben() {
      return zusammenfassungenErlauben;
   }

   public boolean isStunden() {
      return stunden;
   }

   public Vector<Arbeitsstunde> getEinheiten() {
      return einheiten;
   }

   public PropertyChangeSupport getPropertyChangeSupport() {
      return propertyChangeSupport;
   }

   /**
    * Get the value of geldeingang
    * 
    * @return the value of geldeingang
    */
   public Calendar getGeldeingang() {
      return geldeingang;
   }

   /**
    * Set the value of geldeingang
    * 
    * @param geldeingang2 new value of geldeingang
    */
   public void setGeldeingang(Calendar geldeingang2) {
      Calendar oldGeldeingang = this.geldeingang;
      this.geldeingang = geldeingang2;
      propertyChangeSupport.firePropertyChange(PROP_GELDEINGANG, oldGeldeingang, geldeingang2);
   }

   /**
    * Get the value of betrag
    * 
    * @return the value of betrag
    */
   public BigDecimal getBetrag() {
      return betrag;
   }

   /**
    * Get the value of rechnungen_id
    * 
    * @return the value of rechnungen_id
    */
   public int getRechnungen_id() {
      return rechnungen_id;
   }

   private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

   /**
    * Add PropertyChangeListener.
    * 
    * @param listener
    */
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.addPropertyChangeListener(listener);
   }

   /**
    * Remove PropertyChangeListener.
    * 
    * @param listener
    */
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.removePropertyChangeListener(listener);
   }

   /**
    * Get the value of klienten_id
    * 
    * @return the value of klienten_id
    */
   public int getKlienten_id() {
      return klienten_id;
   }

   /**
    * Set the value of klienten_id
    * 
    * @param klienten_id new value of klienten_id
    */
   public void setKlienten_id(int klienten_id) {
      int oldKlienten_id = this.klienten_id;
      this.klienten_id = klienten_id;
      propertyChangeSupport.firePropertyChange(PROP_KLIENTEN_ID, oldKlienten_id, klienten_id);
   }

   /**
    * Get the value of datum
    * 
    * @return the value of datum
    */
   public Calendar getDatum() {
      return datum;
   }

   /**
    * Set the value of datum
    * 
    * @param datum new value of datum
    */
   public void setDatum(Calendar datum) {
      Calendar oldDatum = this.datum;
      this.datum = datum;
      propertyChangeSupport.firePropertyChange(PROP_DATUM, oldDatum, datum);
   }

   /**
    * Get the value of rechnungnr
    * 
    * @return the value of rechnungnr
    */
   public String getRechnungnr() {
      return rechnungnr;
   }

   /**
    * Set the value of rechnungnr
    * 
    * @param rechnungnr new value of rechnungnr
    */
   public void setRechnungnr(String rechnungnr) {
      String oldRechnungnr = this.rechnungnr;
      this.rechnungnr = rechnungnr;
      propertyChangeSupport.firePropertyChange(PROP_RECHNUNGNR, oldRechnungnr, rechnungnr);
   }

   /**
    * Get the value of texdatei
    * 
    * @return the value of texdatei
    */
   public String getTexdatei() {
      return texdatei;
   }

   /**
    * Set the value of texdatei
    * 
    * @param texdatei new value of texdatei
    */
   public void setTexdatei(String texdatei) {
      String oldTexdatei = this.texdatei;
      this.texdatei = texdatei;
      propertyChangeSupport.firePropertyChange(PROP_TEXDATEI, oldTexdatei, texdatei);
   }

   /**
    * Get the value of pdfdatei
    * 
    * @return the value of pdfdatei
    */
   public String getPdfdatei() {
      return pdfdatei;
   }

   /**
    * Set the value of pdfdatei
    * 
    * @param pdfdatei new value of pdfdatei
    */
   public void setPdfdatei(String pdfdatei) {
      String oldPdfdatei = this.pdfdatei;
      this.pdfdatei = pdfdatei;
      propertyChangeSupport.firePropertyChange(PROP_PDFDATEI, oldPdfdatei, pdfdatei);
   }

   /**
    * Get the value of adresse
    * 
    * @return the value of adresse
    */
   public String getAdresse() {
      return adresse;
   }

   /**
    * Set the value of adresse
    * 
    * @param adresse new value of adresse
    */
   public void setAdresse(String adresse) {
      String oldAdresse = this.adresse;
      this.adresse = adresse;
      propertyChangeSupport.firePropertyChange(PROP_ADRESSE, oldAdresse, adresse);
   }

   /**
    * Get the value of zusatz1
    * 
    * @return the value of zusatz1
    */
   public boolean hasZusatz1() {
      return zusatz1;
   }

   /**
    * Set the value of zusatz1
    * 
    * @param zusatz1 new value of zusatz1
    */
   public void setZusatz1(boolean zusatz1) {
      boolean oldZusatz1 = this.zusatz1;
      this.zusatz1 = zusatz1;
      propertyChangeSupport.firePropertyChange(PROP_ZUSATZ1, oldZusatz1, zusatz1);
   }

   /**
    * Get the value of zusatz2
    * 
    * @return the value of zusatz2
    */
   public boolean hasZusatz2() {
      return zusatz2;
   }

   /**
    * Set the value of zusatz2
    * 
    * @param zusatz2 new value of zusatz2
    */
   public void setZusatz2(boolean zusatz2) {
      boolean oldZusatz2 = this.zusatz2;
      this.zusatz2 = zusatz2;
      propertyChangeSupport.firePropertyChange(PROP_ZUSATZ2, oldZusatz2, zusatz2);
   }

   /**
    * Set the value of zusammenfassungen
    * 
    * @param zusammenfassungen new value of zusammenfassungen
    */
   public void setZusammenfassungen(boolean zusammenfassungen) {
      boolean oldZusammenfassungen = this.zusammenfassungenErlauben;
      this.zusammenfassungenErlauben = zusammenfassungen;
      propertyChangeSupport.firePropertyChange(PROP_ZUSAMMENFASSUNGEN, oldZusammenfassungen, zusammenfassungen);
   }

   /**
    * Get the value of zahldatum
    * 
    * @return the value of zahldatum
    */
   public Calendar getZahldatum() {
      return zahldatum;
   }

   /**
    * Set the value of zahldatum
    * 
    * @param zahldatum new value of zahldatum
    */
   public void setZahldatum(Calendar zahldatum) {
      Calendar oldZahldatum = this.zahldatum;
      this.zahldatum = zahldatum;
      propertyChangeSupport.firePropertyChange(PROP_ZAHLDATUM, oldZahldatum, zahldatum);
   }

   public static class Builder implements de.kreth.arbeitsrechnungen.Builder<Rechnung> {

      private static final String BETRAG = "betrag";
      private boolean isNew;
      private BigDecimal betrag = null;
      private int rechnungen_id = -1;
      private int klienten_id = -1;
      private Calendar datum = null;
      private String rechnungnr = "";
      private String adresse = "";
      private String texdatei = "";
      private String pdfdatei = "";
      private boolean zusatz1 = false;
      private boolean zusatz2 = false;
      private boolean zusammenfassungen;
      private Calendar geldeingang = null;
      private Calendar zahldatum = null;

      private String zusatz1_name = null;
      private String zusatz2_name = null;
      private boolean stunden = false;
      private Vector<Arbeitsstunde> einheiten = new Vector<>();

      private Set<String> mustBeSet = new HashSet<>(Arrays.asList("klienten_id", "datum", "einheiten", "rechnungnr", "texdatei", "zahldatum", "adresse", "zusatz1", "zusatz2",
            BETRAG));

      public Builder() {
         datum = new GregorianCalendar();
         isNew = true;
         secureThatAllFieldsInMustBeSetExist();
      }

      private void secureThatAllFieldsInMustBeSetExist() {
         Field[] fields = getClass().getDeclaredFields();
         Collection<String> validate = new ArrayList<>(mustBeSet);
         for (Field f : fields)
            validate.remove(f.getName());
         assert validate.isEmpty();
      }

      public Builder betrag(BigDecimal bigDecimal) {
         this.betrag = bigDecimal;
         mustBeSet.remove(BETRAG);
         return this;
      }

      public Builder rechnungen_id(int rechnungen_id) {
         this.rechnungen_id = rechnungen_id;
         mustBeSet.remove("rechnungen_id");
         if (rechnungen_id != -1)
            isNew = false;
         return this;
      }

      public Builder klienten_id(int klienten_id) {
         this.klienten_id = klienten_id;
         mustBeSet.remove("klienten_id");
         return this;
      }

      public Builder datum(Calendar datum) {
         this.datum = datum;
         mustBeSet.remove("datum");
         return this;
      }

      public Builder rechnungnr(String rechnungnr) {
         this.rechnungnr = rechnungnr;
         mustBeSet.remove("rechnungnr");
         return this;
      }

      public Builder adresse(String adresse) {
         this.adresse = adresse;
         mustBeSet.remove("adresse");
         return this;
      }

      public Builder texdatei(String texdatei) {
         this.texdatei = texdatei;
         mustBeSet.remove("texdatei");
         return this;
      }

      public Builder pdfDatei(String pdfdatei) {
         this.pdfdatei = pdfdatei;
         mustBeSet.remove("pdfdatei");
         return this;
      }

      public Builder zusatz1(boolean zusatz1) {
         this.zusatz1 = zusatz1;
         mustBeSet.remove("zusatz1");
         return this;
      }

      public Builder zusatz2(boolean zusatz2) {
         this.zusatz2 = zusatz2;
         mustBeSet.remove("zusatz2");
         return this;
      }

      public Builder zusammenfassungenErlauben(boolean zusammenfassungen) {
         this.zusammenfassungen = zusammenfassungen;
         mustBeSet.remove("zusammenfassungen");
         return this;
      }

      public Builder geldeingang(Calendar geldeingang) {
         this.geldeingang = geldeingang;
         mustBeSet.remove("geldeingang");
         return this;
      }

      public Builder zahldatum(Calendar zahldatum) {
         this.zahldatum = zahldatum;
         mustBeSet.remove("zahldatum");
         return this;
      }

      public Builder zusatz1Name(String zusatz1_name) {
         this.zusatz1_name = zusatz1_name;
         mustBeSet.remove("zusatz1_name");
         return this;
      }

      public Builder zusatz2Name(String zusatz2_name) {
         this.zusatz2_name = zusatz2_name;
         mustBeSet.remove("zusatz2_name");
         return this;
      }

      public Builder stunden(boolean stunden) {
         this.stunden = stunden;
         mustBeSet.remove("stunden");
         return this;
      }

      public Builder einheiten(Vector<Arbeitsstunde> einheiten) {
         this.einheiten = einheiten;
         mustBeSet.remove("einheiten");
         return this;
      }

      @Override
      public Rechnung build() {
         if (mustBeSet.contains(BETRAG)) {

            this.betrag(getSummeFromEinheiten(einheiten));
         }
         if (hasUnsetFields()) {
            throw new IllegalStateException("Alle Werte müssen gesetzt werden! Offen: " + mustBeSet);
         }
         return new Rechnung(this);
      }

      private boolean hasUnsetFields() {
         return !mustBeSet.isEmpty();
      }

      public Builder betrag(double betragWert) {
         return betrag(BigDecimal.valueOf(betragWert));
      }
   }

   private static BigDecimal getSummeFromEinheiten(Vector<Arbeitsstunde> einheiten) {

      BigDecimal summe = BigDecimal.ZERO;
      for (Arbeitsstunde einheit : einheiten) {
         summe = summe.add(einheit.getPreis());
      }

      return summe;
   }

   public boolean isNew() {
      return isNew;
   }

   public void setEinheiten(Vector<Arbeitsstunde> einheiten) {
      this.einheiten = einheiten;
      this.betrag = getSummeFromEinheiten(einheiten);
   }

   public void setRechnungId(int rechnungId) {
      this.rechnungen_id = rechnungId;
   }
}
