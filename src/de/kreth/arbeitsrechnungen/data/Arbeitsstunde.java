package de.kreth.arbeitsrechnungen.data;

/**
 * @author markus
 */
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Vector;

public interface Arbeitsstunde extends Serializable {

   public int getID();

   public int getKlientenID();

   public int getAngeboteID();
   
   public Date getDatum();

   public void setDatum(Date datum);

   public Date getBeginn();

   public void setBeginn(Date beginn);

   public Date getEnde();

   public void setEnde(Date ende);

   /**
    * Preis für eine einzelne Einheit oder Stunde
    * 
    * @return Preis für eine Einheit oder Stunde
    */
   public BigDecimal getEinzelPreis();

   public void setEinzelPreis(double preis);

   /**
    * Gesamtpreis für diese Einheit
    * 
    * @return Preis für diese Einheit?
    */
   public BigDecimal getPreis();

   public void setPreis(double preis);

   public String getZusatz2();

   public void setZusatz2(String zusatz2);

   public String getZusatz1();

   public void setZusatz1(String zusatz1);

   public BigDecimal getPreisAenderung();

   public void setPreisAenderung(double aenderung);

   public boolean isVerschickt();

   public Date getVerschicktDatum();

   public void setVerschicktDatum(Date datum);

   public boolean isBezahlt();

   public Date getBezahltDatum();

   public void setBezahltDatum(Date datum);

   public Vector<Object> toVector();

   /**
    * Dauer der Trainingsstunde in Minuten
    * 
    * @return minuten
    */
   public int getDauerInMinutes();

   public void setDauer(int minutes);

   public void addPropertyChangeListener(PropertyChangeListener listener);

   public void removePropertyChangeListener(PropertyChangeListener listener);

   public String getInhalt();

   public boolean isPreisProStunde();

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
