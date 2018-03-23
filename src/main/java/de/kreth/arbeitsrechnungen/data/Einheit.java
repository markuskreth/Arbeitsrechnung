package de.kreth.arbeitsrechnungen.data;

import java.util.Calendar;
import java.util.Date;

import com.ibm.icu.util.GregorianCalendar;

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
	private Angebot angebot;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + angebotId;
		result = prime * result + anzahl;
		result = prime * result + ((auftraggeber == null) ? 0 : auftraggeber.hashCode());
		result = prime * result + ((beginn == null) ? 0 : beginn.hashCode());
		result = prime * result + ((bezahltDatum == null) ? 0 : bezahltDatum.hashCode());
		result = prime * result + ((datum == null) ? 0 : datum.hashCode());
		result = prime * result + ((ende == null) ? 0 : ende.hashCode());
		result = prime * result + id;
		result = prime * result + klientenId;
		long temp;
		temp = Double.doubleToLongBits(klientenpreis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(preisAenderung);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((rechnungDatum == null) ? 0 : rechnungDatum.hashCode());
		result = prime * result + ((zusatz1 == null) ? 0 : zusatz1.hashCode());
		result = prime * result + ((zusatz2 == null) ? 0 : zusatz2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Einheit other = (Einheit) obj;
		if (angebotId != other.angebotId)
			return false;
		if (anzahl != other.anzahl)
			return false;
		if (auftraggeber == null) {
			if (other.auftraggeber != null)
				return false;
		} else if (!auftraggeber.equals(other.auftraggeber))
			return false;
		if (beginn == null) {
			if (other.beginn != null)
				return false;
		} else if (!beginn.equals(other.beginn))
			return false;
		if (bezahltDatum == null) {
			if (other.bezahltDatum != null)
				return false;
		} else if (!bezahltDatum.equals(other.bezahltDatum))
			return false;
		if (datum == null) {
			if (other.datum != null)
				return false;
		} else if (!datum.equals(other.datum))
			return false;
		if (ende == null) {
			if (other.ende != null)
				return false;
		} else if (!ende.equals(other.ende))
			return false;
		if (id != other.id)
			return false;
		if (klientenId != other.klientenId)
			return false;
		if (Double.doubleToLongBits(klientenpreis) != Double.doubleToLongBits(other.klientenpreis))
			return false;
		if (Double.doubleToLongBits(preisAenderung) != Double.doubleToLongBits(other.preisAenderung))
			return false;
		if (rechnungDatum == null) {
			if (other.rechnungDatum != null)
				return false;
		} else if (!rechnungDatum.equals(other.rechnungDatum))
			return false;
		if (zusatz1 == null) {
			if (other.zusatz1 != null)
				return false;
		} else if (!zusatz1.equals(other.zusatz1))
			return false;
		if (zusatz2 == null) {
			if (other.zusatz2 != null)
				return false;
		} else if (!zusatz2.equals(other.zusatz2))
			return false;
		return true;
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
		private String zusatz1 = "";
		private String zusatz2 = "";
		private Date bezahltDatum;
		private Angebot angebot;

		public Builder() {
		}
		
		public Builder(Einheit einheit) {
			this.angebotId = einheit.angebotId;
			this.anzahl = einheit.anzahl;
			this.auftraggeber = einheit.auftraggeber;
			this.beginn = einheit.beginn;
			this.bezahltDatum = einheit.bezahltDatum;
			this.datum = einheit.datum;
			this.ende = einheit.ende;
			this.id = einheit.id;
			this.klientenId = einheit.klientenId;
			this.klientenpreis = einheit.klientenpreis;
			this.preisAenderung = einheit.preisAenderung;
			this.rechnungDatum = einheit.rechnungDatum;
			this.zusatz1 = einheit.zusatz1;
			this.zusatz2 = einheit.zusatz2;
			this.angebot = einheit.angebot;
		}

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
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(datum);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			this.datum = cal.getTime();
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

		public Builder angebot(Angebot a) {
			this.angebot = a;
			return this;
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
		this.angebot = builder.angebot;
	}

	public double getPreis() {
		double preis = -1;
		if(angebot != null) {
			preis = Math.round((angebot.getPreis() + preisAenderung) * 100);
			preis = preis / 100;
			if(angebot.isPreis_pro_stunde()) {

				preis = Math.round(((double) getDauerInMinutes() / 60 * preis) * 100);
				preis = preis / 100;
			}
		}
		return preis;
	}

	public long getDauerInMinutes() {
		return Math.round(
				((double) ende.getTime() - beginn.getTime()) 
				/ (60. * 1000.));
	}
	
}
