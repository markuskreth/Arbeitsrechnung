
package arbeitsrechnungen.data;

/**
 *
 * @author markus
 */
import java.io.*;
import java.beans.*;
import java.util.Vector;

public interface Arbeitsstunde extends Serializable
{
    public java.lang.Integer getID();

    public java.lang.Integer getKlientenID();

    public java.lang.Integer getAngeboteID();

    public java.util.Date getDatum();

    public void setDatum(java.util.Date datum);

    public java.util.Date getBeginn();

    public void setBeginn(java.util.Date beginn);

    public java.util.Date getEnde();

    public void setEnde(java.util.Date ende);

	/**
	 * Preis für eine einzelne Einheit oder Stunde
	 * @return
	 */
	public double getEinzelPreis();

	public void setEinzelPreis(double preis);

	/**
	 * Gesamtpreis für diese Einheit
	 * @return
	 */
    public double getPreis();

    public void setPreis(double preis);

    public String getZusatz2();

    public void setZusatz2(String zusatz2);

    public String getZusatz1();

    public void setZusatz1(String zusatz1);

    public java.lang.Double getPreisAenderung();

    public void setPreisAenderung(java.lang.Double aenderung);

    public java.lang.Boolean isVerschickt();

    public java.util.Date getVerschicktDatum();

    public void setVerschicktDatum(java.util.Date datum);

    public java.lang.Boolean isBezahlt();

    public java.util.Date getBezahltDatum();

    public void setBezahltDatum(java.util.Date datum);

    public Vector<Object> toVector();

    public double getDauer();

    public void setDauer(double dauer);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);
	public String getInhalt();

    public static final String PROP_KLIENTENID = "klientenid";
    public static final String PROP_ANGEBOTEID = "angeboteid";
    public static final String PROP_DATUM = "datum";
    public static final String PROP_BEGINN = "beginn";
    public static final String PROP_ENDE = "ende";
    public static final String PROP_PREISAENDERUNG = "preisaenderung";
    public static final String PROP_VERSCHICKT = "verschickt";
    public static final String PROP_BEZAHLT = "bezahlt";
    public static final String PROP_AUFTRAGGEBER = "auftraggeber";
    public static final String PROP_INHALT = "inhalt";
    public static final String PROP_PREIS = "preis";
    public static final String PROP_ZUSATZ1 = "zusatz1";
    public static final String PROP_ZUSATZ2 = "zusatz2";
    public static final String PROP_DAUER = "dauer";
    public static final String PROP_EINZELPREIS = "einzelPreis";
}
