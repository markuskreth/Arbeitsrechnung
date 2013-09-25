/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.kreth.arbeitsrechnungen;

/**
 *
 * @author markus
 */

import java.util.GregorianCalendar;
import java.util.Calendar;

public class mySqlDate {

	Calendar cal;

	public mySqlDate(java.util.Date datum) {
		cal = new GregorianCalendar();
		cal.setTime(datum);
	}

	public mySqlDate(GregorianCalendar cal) {
		this.cal = cal;
	}

	public String getSqlDate() {
		java.sql.Timestamp sqldate = new java.sql.Timestamp(cal.getTimeInMillis());
		return (sqldate.toString());
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	/**
	 * januar = 1, August = 8, Dezember = 12
	 * @return
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
