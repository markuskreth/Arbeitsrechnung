package de.kreth.arbeitsrechnungen.report;

import java.text.DateFormat;

import de.kreth.arbeitsrechnungen.data.Klient;
import de.kreth.arbeitsrechnungen.data.Rechnung;

public class KlientRechnung {

	private final Klient k;
	private final Rechnung r;
	
	public KlientRechnung(Klient k, Rechnung r) {
		this.k = k;
		this.r = r;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + r.getRechnungen_id();
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
		KlientRechnung other = (KlientRechnung) obj;
		if (r.getRechnungen_id() != other.r.getRechnungen_id())
			return false;
		return true;
	}

	public Rechnung getRechnung() {
		return r;
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder()
				.append(k.getAuftraggeber())
				.append(": ")
				.append(r.getRechnungnr())
				.append(" vom ").append(DateFormat.getDateInstance().format(r.getDatum().getTime()));
		return text.toString();
	}
}
