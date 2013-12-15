package de.kreth.arbeitsrechnungen.data;

/**
 *
 * @author markus
 */
import java.util.*;
import java.beans.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;


public class ArbeitsstundeImpl implements Arbeitsstunde {
	
	private static final long serialVersionUID = 7216748433407807648L;
	private int id;
	private int klientenid;
	private int angeboteid;
	private Date datum;
	private Date beginn;
	private Date ende;
	private BigDecimal preisaenderung = BigDecimal.ZERO;
	private Date verschickt = null;
	private Date bezahlt = null;
	private String auftraggeber;
	private String inhalt;
	private BigDecimal preis;
	protected String zusatz1;
	protected String zusatz2;
	private PropertyChangeSupport pss;
	protected int dauer;
	private BigDecimal einzelPreis;
	private boolean preisProStunde;

	/**
	 * Get the value of einzelPreis
	 * 
	 * @return the value of einzelPreis
	 */
	public BigDecimal getEinzelPreis() {
		return einzelPreis;
	}

	/**
	 * Set the value of einzelPreis
	 * 
	 * @param einzelPreis
	 *            new value of einzelPreis
	 */
	public void setEinzelPreis(double einzelPreis) {
		double oldEinzelPreis = this.einzelPreis.setScale(2, RoundingMode.HALF_UP).doubleValue();
		this.einzelPreis = new BigDecimal(einzelPreis);
		pss.firePropertyChange(PROP_EINZELPREIS, oldEinzelPreis, einzelPreis);
	}

	/**
	 * Get the value of dauer
	 * 
	 * @return the value of dauer
	 */
	public int getDauerInMinutes() {
		return dauer;
	}

	/**
	 * Set the value of dauer
	 * 
	 * @param dauer
	 *            new value of dauer
	 */
	public void setDauer(int dauer) {
		double oldDauer = this.dauer;
		this.dauer = dauer;
		pss.firePropertyChange(PROP_DAUER, oldDauer, dauer);
	}

	/**
	 * Get the value of zusatz2
	 * 
	 * @return the value of zusatz2
	 */
	public String getZusatz2() {
		return zusatz2;
	}

	/**
	 * Set the value of zusatz2
	 * 
	 * @param zusatz2
	 *            new value of zusatz2
	 */
	public void setZusatz2(String zusatz2) {
		String oldZusatz2 = this.zusatz2;
		this.zusatz2 = zusatz2;
		pss.firePropertyChange(PROP_ZUSATZ2, oldZusatz2, zusatz2);
	}

	/**
	 * Get the value of zusatz1
	 * 
	 * @return the value of zusatz1
	 */
	public String getZusatz1() {
		return zusatz1;
	}

	/**
	 * Set the value of zusatz1
	 * 
	 * @param zusatz1
	 *            new value of zusatz1
	 */
	public void setZusatz1(String zusatz1) {
		String oldZusatz1 = this.zusatz1;
		this.zusatz1 = zusatz1;
		pss.firePropertyChange(PROP_ZUSATZ1, oldZusatz1, zusatz1);
	}

	/**
	 * Get the value of preis
	 * 
	 * @return the value of preis
	 */
	public BigDecimal getPreis() {
		return preis;
	}

	/**
	 * Set the value of preis
	 * 
	 * @param preis
	 *            new value of preis
	 */
	public void setPreis(double preis) {
		double oldPreis = this.preis.setScale(2, RoundingMode.HALF_UP).doubleValue();
		this.preis = new BigDecimal(preis);
		pss.firePropertyChange(PROP_PREIS, oldPreis, preis);
	}

	/**
	 * Get the value of inhalt
	 * 
	 * @return the value of inhalt
	 */
	public String getInhalt() {
		return inhalt;
	}

	/**
	 * Set the value of inhalt
	 * 
	 * @param inhalt
	 *            new value of inhalt
	 */
	public void setInhalt(String inhalt) {
		String oldInhalt = this.inhalt;
		this.inhalt = inhalt;
		pss.firePropertyChange(PROP_INHALT, oldInhalt, inhalt);
	}

	/**
	 * Get the value of Auftraggeber
	 * 
	 * @return the value of Auftraggeber
	 */
	public String getAuftraggeber() {
		return auftraggeber;
	}

	/**
	 * Set the value of Auftraggeber
	 * 
	 * @param Auftraggeber
	 *            new value of Auftraggeber
	 */
	public void setAuftraggeber(String Auftraggeber) {
		String oldAuftraggeber = this.auftraggeber;
		this.auftraggeber = Auftraggeber;
		pss.firePropertyChange(PROP_AUFTRAGGEBER, oldAuftraggeber, Auftraggeber);
	}

	private ArbeitsstundeImpl(Builder builder) {
		this.id = builder.id;
		this.klientenid = builder.klientenid;
		this.angeboteid = builder.angeboteid;
		this.auftraggeber = builder.auftraggeber;
		this.beginn = builder.beginn;
		this.bezahlt = builder.bezahlt;
		this.datum = builder.datum;
		this.dauer = builder.dauer;
		this.einzelPreis = builder.einzelPreis;
		this.ende = builder.ende;
		this.inhalt = builder.inhalt;
		this.klientenid = builder.klientenid;
		this.preis = builder.preis;
		this.preisaenderung = builder.preisaenderung;
		this.preisProStunde = builder.preisProStunde;
		this.verschickt = builder.verschickt;
		this.zusatz1 = builder.zusatz1;
		this.zusatz2 = builder.zusatz2;
		this.pss = new PropertyChangeSupport(this);
	}

	public static class Builder implements de.kreth.arbeitsrechnungen.Builder<ArbeitsstundeImpl> {

	   private int id;
	   private int klientenid;
	   private int angeboteid;
	   private Date datum;
	   private Date beginn;
	   private Date ende;
	   private BigDecimal preisaenderung = BigDecimal.ZERO;
	   private Date verschickt = null;
	   private Date bezahlt = null;
	   private String auftraggeber;
	   private String inhalt;
	   private BigDecimal preis;
	   protected String zusatz1;
	   protected String zusatz2;
	   protected int dauer;
	   private BigDecimal einzelPreis;
	   private boolean preisProStunde;

      public Builder(int id, int klientenid, int angeboteid) {
         this.id = id;
         this.klientenid = klientenid;
         this.angeboteid = angeboteid;
      }

      public Builder setId(int id) {
         this.id = id;
         return this;
      }
      
      public Builder klientenid(int klientenid) {
         this.klientenid = klientenid;
         return this;
      }
      
      public Builder angeboteid(int angeboteid) {
         this.angeboteid = angeboteid;
         return this;
      }
      
      public Builder datum(Date datum) {
         this.datum = datum;
         return this;
      }
      
      public Builder beginn(Date beginn) {
         this.beginn = beginn;
         return this;
      }
      
      public Builder ende(Date ende) {
         this.ende = ende;
         return this;
      }
      
      public Builder preisaenderung(double preisaenderung) {
         this.preisaenderung = BigDecimal.valueOf(preisaenderung);
         return this;
      }
      
      public Builder setVerschickt(Date verschickt) {
         this.verschickt = verschickt;
         return this;
      }
      
      public Builder bezahlt(Date bezahlt) {
         this.bezahlt = bezahlt;
         return this;
      }
      
      public Builder auftraggeber(String auftraggeber) {
         this.auftraggeber = auftraggeber;
         return this;
      }
      
      public Builder inhalt(String inhalt) {
         this.inhalt = inhalt;
         return this;
      }

      public Builder preis(double preis) {
         this.preis = BigDecimal.valueOf(preis);
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
      
      public Builder dauerInMinuten(int minuten) {
         this.dauer = minuten;
         return this;
      }

      public Builder einzelPreis(double einzelPreis) {
         this.einzelPreis = BigDecimal.valueOf(einzelPreis);
         return this;
      }

      public Builder preisProStunde(boolean preisProStunde) {
         this.preisProStunde = preisProStunde;
         return this;
      }

      @Override
      public ArbeitsstundeImpl build() {
         return new ArbeitsstundeImpl(this);
      }
      
	}
	public java.lang.Integer getID() {
		return id;
	}

	public java.lang.Integer getKlientenID() {
		return klientenid;
	}

	public java.lang.Integer getAngeboteID() {
		return angeboteid;
	}

	public java.util.Date getDatum() {
		return datum;
	}

	public void setDatum(java.util.Date datum) {
		java.util.Date old = this.datum;
		this.datum = datum;
		this.pss.firePropertyChange(PROP_DATUM, old, datum);
	}

	public java.util.Date getBeginn() {
		return beginn;
	}

	public void setBeginn(java.util.Date beginn) {
		java.util.Date old = this.beginn;
		this.beginn = beginn;
		this.pss.firePropertyChange(PROP_BEGINN, old, beginn);
	}

	public java.util.Date getEnde() {
		return ende;
	}

	public void setEnde(java.util.Date ende) {
		java.util.Date old = this.ende;
		this.ende = ende;
		this.pss.firePropertyChange(PROP_ENDE, old, ende);
	}

	public BigDecimal getPreisAenderung() {
		return preisaenderung;
	}

	public void setPreisAenderung(double aenderung) {
		java.lang.Double old = this.preisaenderung.setScale(2, RoundingMode.HALF_UP).doubleValue();
		this.preisaenderung = BigDecimal.valueOf(aenderung);
		this.pss.firePropertyChange(PROP_PREISAENDERUNG, old, aenderung);
	}

	public java.lang.Boolean isVerschickt() {
		if (this.verschickt != null) {
			return true;
		} else {
			return false;
		}

	}

	public java.util.Date getVerschicktDatum() {
		return verschickt;
	}

	public void setVerschicktDatum(java.util.Date datum) {
		java.util.Date old = this.verschickt;
		this.verschickt = datum;
		this.pss.firePropertyChange(PROP_VERSCHICKT, old, datum);
	}

	public java.lang.Boolean isBezahlt() {
		if (this.bezahlt != null) {
			return true;
		} else {
			return false;
		}
	}

	public java.util.Date getBezahltDatum() {
		return bezahlt;
	}

	public void setBezahltDatum(java.util.Date datum) {
		java.util.Date old = this.bezahlt;
		this.bezahlt = datum;
		this.pss.firePropertyChange(PROP_BEZAHLT, old, datum);
	}

	public Vector<Object> toVector() {
		Vector<Object> datensatz = new Vector<Object>();
		try {
			datensatz.add(this.id);
			datensatz.add(this.klientenid);
			datensatz.add(this.angeboteid);
			try {
				datensatz.add(DateFormat.getDateInstance(
						java.text.DateFormat.MEDIUM, Locale.GERMAN).format(
						this.datum));
			} catch (Exception e) {
				// System.err.println("ArbeitsstundenImpl::toVector: Vektorfunktion \"Datum\" fehlgeschlagen");
				// e.printStackTrace();
			}
			datensatz.add(this.inhalt);
			try {
				String temp = DateFormat.getTimeInstance().format(this.beginn);
				temp = temp.substring(0, 5);
				datensatz.add(temp);
			} catch (Exception e) {
				// System.err.println("ArbeitsstundenImpl::toVector: Vektorfunktion \"beginn\" fehlgeschlagen");
			}
			try {
				String temp = DateFormat.getTimeInstance().format(this.ende);
				temp = temp.substring(0, 5);
				datensatz.add(temp);
			} catch (Exception e) {
				System.err
						.println("ArbeitsstundenImpl::toVector: Vektorfunktion \"ende\" fehlgeschlagen");
			}
			datensatz.add(this.preis);
			datensatz.add(this.zusatz1);
			datensatz.add(this.zusatz2);
			datensatz.add(this.preisaenderung);
			try {
				datensatz.add(DateFormat.getDateInstance().format(
						this.verschickt));
			} catch (Exception e) {
				// System.err.println("ArbeitsstundenImpl::toVector: Vektorfunktion \"verschickt\" fehlgeschlagen");
				// e.printStackTrace();
			}
			try {
				datensatz
						.add(DateFormat.getDateInstance().format(this.bezahlt));
			} catch (Exception e) {
				// System.err.println("ArbeitsstundenImpl::toVector: Vektorfunktion \"bezahlt\" fehlgeschlagen");
				// e.printStackTrace();
			}
		} catch (Exception e) {
			System.err
					.println("ArbeitsstundenImpl::toVector: Vektorfunktion fehlgeschlagen");
			e.printStackTrace();
		}
		return datensatz;
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		this.pss.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		this.pss.removePropertyChangeListener(listener);
	}

	public void setPreisProStunde(boolean boolean1) {
		this.preisProStunde = boolean1;
	}
	
	public boolean isPreisProStunde(){
		return preisProStunde;
	}
}
