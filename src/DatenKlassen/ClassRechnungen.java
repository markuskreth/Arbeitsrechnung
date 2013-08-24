
package DatenKlassen;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.GregorianCalendar;

/**
 *
 * @author markus
 */
public class ClassRechnungen {

    protected double betrag;
    private int rechnungen_id;
    protected int klienten_id;
    protected GregorianCalendar datum;
    protected String rechnungnr;
    protected String adresse;
    protected String texdatei;
    protected String pdfdatei;
    protected boolean zusatz1;
    protected boolean zusatz2;
    protected boolean zusammenfassungen;
    protected GregorianCalendar geldeingang = null;
    protected GregorianCalendar zahldatum;

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

    public ClassRechnungen(int rechnungen_id) {
        this.rechnungen_id = rechnungen_id;
    }

    /**
     * Get the value of geldeingang
     *
     * @return the value of geldeingang
     */
    public GregorianCalendar getGeldeingang() {
        return geldeingang;
    }

    /**
     * Set the value of geldeingang
     *
     * @param geldeingang new value of geldeingang
     */
    public void setGeldeingang(GregorianCalendar geldeingang) {
        GregorianCalendar oldGeldeingang = this.geldeingang;
        this.geldeingang = geldeingang;
        propertyChangeSupport.firePropertyChange(PROP_GELDEINGANG, oldGeldeingang, geldeingang);
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
    public GregorianCalendar getDatum() {
        return datum;
    }

    /**
     * Set the value of datum
     *
     * @param datum new value of datum
     */
    public void setDatum(GregorianCalendar datum) {
        GregorianCalendar oldDatum = this.datum;
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
    public boolean isZusatz1() {
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
    public boolean isZusatz2() {
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
    public GregorianCalendar getZahldatum() {
        return zahldatum;
    }

    /**
     * Set the value of zahldatum
     *
     * @param zahldatum new value of zahldatum
     */
    public void setZahldatum(GregorianCalendar zahldatum) {
        GregorianCalendar oldZahldatum = this.zahldatum;
        this.zahldatum = zahldatum;
        propertyChangeSupport.firePropertyChange(PROP_ZAHLDATUM, oldZahldatum, zahldatum);
    }

}
