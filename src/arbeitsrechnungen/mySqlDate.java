/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arbeitsrechnungen;

/**
 *
 * @author markus
 */

import java.util.GregorianCalendar;
import java.util.Calendar;


public class mySqlDate {

GregorianCalendar cal;

    public mySqlDate(java.util.Date datum) {
        cal = new GregorianCalendar();
        cal.setTime(datum);
    }

    public mySqlDate(GregorianCalendar cal) {
		this.cal = cal;
	}
    
    public String getSqlDate(){
        java.sql.Date sqldate = new java.sql.Date(cal.getTimeInMillis());
        return(sqldate.toString());
    }

    public int getYear(){
        return cal.get(Calendar.YEAR);
    }

    public int getMonth(){
        return cal.get(Calendar.MONTH)+1;
    }

    public int getDay(){
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour(){
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute(){
        return cal.get(Calendar.MINUTE);
    }
    
    public long getTimestamp(){
        return cal.getTimeInMillis();
    }
    
    public long getSqlTimestamp(){
        java.sql.Date sqldate = new java.sql.Date(cal.getTimeInMillis());
        return sqldate.getTime();
    }
}
