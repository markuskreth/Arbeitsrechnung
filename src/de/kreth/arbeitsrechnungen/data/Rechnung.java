
package de.kreth.arbeitsrechnungen.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author markus
 */
public class Rechnung {

    protected double betrag;
    private int rechnungen_id;
    protected int klienten_id;
    protected Calendar datum;
    protected String rechnungnr;
    protected String adresse;
    protected String texdatei;
    protected String pdfdatei;
    protected boolean zusatz1;
    protected boolean zusatz2;
    protected boolean zusammenfassungen;
    protected Calendar geldeingang = null;
    protected Calendar zahldatum;

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
       this.zusammenfassungen = builder.zusammenfassungen;
       this.geldeingang = builder.geldeingang;
       this.zahldatum = builder.zahldatum;
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
    public double getBetrag() {
        return betrag;
    }

    /**
     * Set the value of betrag
     *
     * @param betrag new value of betrag
     */
    public void setBetrag(double betrag) {
        double oldBetrag = this.betrag;
        this.betrag = betrag;
        propertyChangeSupport.firePropertyChange(PROP_BETRAG, oldBetrag, betrag);
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
     * Get the value of zusammenfassungen
     *
     * @return the value of zusammenfassungen
     */
    public boolean isZusammenfassungen() {
        return zusammenfassungen;
    }

    /**
     * Set the value of zusammenfassungen
     *
     * @param zusammenfassungen new value of zusammenfassungen
     */
    public void setZusammenfassungen(boolean zusammenfassungen) {
        boolean oldZusammenfassungen = this.zusammenfassungen;
        this.zusammenfassungen = zusammenfassungen;
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

    public static class Builder implements de.kreth.arbeitsrechnungen.Builder<Rechnung>{

       private double betrag = 0;
       private int rechnungen_id = -1;
       private int klienten_id = -1;
       private Calendar datum = null;
       private String rechnungnr = "";
       private String adresse = "";
       private String texdatei ="";
       private String pdfdatei ="";
       private boolean zusatz1 = false;
       private boolean zusatz2 = false;
       private boolean zusammenfassungen;
       private Calendar geldeingang = null;
       private Calendar zahldatum = null;
       
      public Builder() {
         datum = new GregorianCalendar();
      }

      public Builder setBetrag(double betrag) {
         this.betrag = betrag;
         return this;
      }

      
      public Builder setRechnungen_id(int rechnungen_id) {
         this.rechnungen_id = rechnungen_id;
         return this;
      }

      
      public Builder setKlienten_id(int klienten_id) {
         this.klienten_id = klienten_id;
         return this;
      }

      
      public Builder setDatum(Calendar datum) {
         this.datum = datum;
         return this;
      }

      
      public Builder setRechnungnr(String rechnungnr) {
         this.rechnungnr = rechnungnr;
         return this;
      }

      
      public Builder setAdresse(String adresse) {
         this.adresse = adresse;
         return this;
      }

      
      public Builder setTexdatei(String texdatei) {
         this.texdatei = texdatei;
         return this;
      }

      
      public Builder setPdfdatei(String pdfdatei) {
         this.pdfdatei = pdfdatei;
         return this;
      }

      
      public Builder setZusatz1(boolean zusatz1) {
         this.zusatz1 = zusatz1;
         return this;
      }

      
      public Builder setZusatz2(boolean zusatz2) {
         this.zusatz2 = zusatz2;
         return this;
      }

      
      public Builder setZusammenfassungen(boolean zusammenfassungen) {
         this.zusammenfassungen = zusammenfassungen;
         return this;
      }

      
      public Builder setGeldeingang(Calendar geldeingang) {
         this.geldeingang = geldeingang;
         return this;
      }

      
      public Builder setZahldatum(Calendar zahldatum) {
         this.zahldatum = zahldatum;
         return this;
      }

      @Override
      public Rechnung build() {
         return new Rechnung(this);
      }
       
    }
}
