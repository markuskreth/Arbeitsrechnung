package de.kreth.arbeitsrechnungen;

/**
 *
 * @author markus
 */

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class MySqlDate {

   private static DateFormat sdf = 
     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Calendar cal;

	public MySqlDate(java.util.Date datum) {
		cal = new GregorianCalendar();
		cal.setTime(datum);
	}

	public MySqlDate(Calendar cal) {
		this.cal = cal;
	}

	public String getSqlDate() {
	   return sdf.format(cal.getTime());
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	/**
	 * januar = 1, August = 8, Dezember = 12
	 * @return Monat als menschenlesbare zahl
	 */
	public int getMonth() {
		return cal.get(Calendar.MONTH) + 1;
	}

	public int getDay() {
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return cal.get(Calendar.MINUTE);
	}

	public long getTimestamp() {
		return cal.getTimeInMillis();
	}

}
