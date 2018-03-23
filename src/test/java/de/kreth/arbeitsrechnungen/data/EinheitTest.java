package de.kreth.arbeitsrechnungen.data;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;

import de.kreth.arbeitsrechnungen.data.Einheit.Builder;

public class EinheitTest {

	AtomicLong dateValue;
	AtomicInteger intValue;
	
	@Before
	public void initEinheitCreator() {
		dateValue = new AtomicLong(1L);
		intValue = new AtomicInteger();
	}
	
	@Test
	public void testClone() {
		Einheit original = createTestEinheit().build();
		assertEquals(original, new Einheit.Builder(original).build());
	}

	private Builder createTestEinheit() {
		Builder original = new Einheit.Builder()
				.angebotId(intValue.incrementAndGet())
				.anzahl(intValue.incrementAndGet())
				.auftraggeber("Der Auftraggeber")
				.beginn(new Date(dateValue.addAndGet(96400000L)))
				.bezahltDatum(new Date(dateValue.addAndGet(96400000L)))
				.datum(new Date(dateValue.addAndGet(96400000L)))
				.ende(new Date(dateValue.addAndGet(96400000L)))
				.id(intValue.incrementAndGet())
				.klientenId(intValue.incrementAndGet())
				.klientenpreis(12.3)
				.preisAenderung(13.3)
				.rechnungDatum(new Date(dateValue.addAndGet(96400000L)))
				.zusatz1("Zusatz 1")
				.zusatz2("zusatz2");
		return original;
	}

	@Test
	public void testPreisBerechnung() {
		BigDecimal angebotPreis = BigDecimal.valueOf(11.73);
		Angebot a = new Angebot.Builder("Testangebot", angebotPreis.doubleValue())
				.angebotId(intValue.incrementAndGet())
				.preis_pro_stunde(false)
				.build();
		Builder builder = createTestEinheit()
				.angebot(a)
				.angebotId(a.getAngebote_id())
				.preisAenderung(0);

		Einheit e = builder.build();
		BigDecimal preis = BigDecimal.valueOf(e.getPreis());
		assertEquals(angebotPreis, preis);

		BigDecimal preisAenderung = BigDecimal.valueOf(5.39);
		BigDecimal expected = angebotPreis.add(preisAenderung);
		e = builder.preisAenderung(preisAenderung.doubleValue()).build();
		preis = BigDecimal.valueOf(e.getPreis());
		assertEquals(expected, preis);
		
	}

	@Test
	public void testPreisBerechnungProStunde() {
		Date beginn = new GregorianCalendar(2000, Calendar.MARCH, 5, 17, 0, 0).getTime();
		Date end    = new GregorianCalendar(2000, Calendar.MARCH, 5, 19, 30, 0).getTime();
		
		BigDecimal angebotPreis = BigDecimal.valueOf(11.73);
		
		Angebot a = new Angebot.Builder("Testangebot", angebotPreis.doubleValue())
				.angebotId(intValue.incrementAndGet())
				.preis_pro_stunde(true)
				.build();
		Builder builder = createTestEinheit()
				.angebot(a)
				.angebotId(a.getAngebote_id())
				.beginn(beginn)
				.ende(end)
				.preisAenderung(0);

		Einheit e = builder.build();
		BigDecimal preis = BigDecimal.valueOf(e.getPreis());
		BigDecimal expected = angebotPreis.multiply(BigDecimal.valueOf(2.5)).setScale(2, RoundingMode.HALF_UP);
		assertEquals(expected, preis);
		
	}
	
	@Test
	public void testDauer() {
		Date beginn = new GregorianCalendar(2000, Calendar.MARCH, 5, 17, 0, 0).getTime();
		Date end = new GregorianCalendar(2000, Calendar.MARCH, 5, 19, 30, 0).getTime();
		Einheit einheit = createTestEinheit().beginn(beginn).ende(end).build();
		assertEquals(150, einheit.getDauerInMinutes());
	}
	
}
