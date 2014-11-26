package com.pinthecloud.item.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.pinthecloud.item.exception.ItException;

// 20141113014345 --> 2014-11-13 01:43:45
public class ItDateTime {
	private String dateTime;
	
	public ItDateTime(String rawDateTime) {
		if (rawDateTime.length() != 14) {
			throw new ItException(ItException.TYPE.FORMATE_ERROR);
		}
		this.dateTime = rawDateTime;
	}
	
	public int getYear() {
		return Integer.parseInt(dateTime.substring(0, 4));
	}
	public int getMonth() {
		return Integer.parseInt(dateTime.substring(4, 6));
	}
	public int getDate() {
		return Integer.parseInt(dateTime.substring(6, 8));
	}
	public int getHours() {
		return Integer.parseInt(dateTime.substring(8, 10));
	}
	public int getMinutes() {
		return Integer.parseInt(dateTime.substring(10, 12));
	}
	public int getSeconds() {
		return Integer.parseInt(dateTime.substring(12, 14));
	}
	public String toString() {
		return dateTime;
	}
	public static ItDateTime getToday() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd000000", Locale.KOREA);
        Calendar cal = Calendar.getInstance();
        return new ItDateTime(dateFormat.format(cal.getTime()));
	}
	public ItDateTime getYesterday() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd000000", Locale.KOREA);
        Calendar cal = Calendar.getInstance();
        cal.set(this.getYear(), this.getMonth()-1, this.getDate());
        cal.add(Calendar.DATE, -1);    
        return new ItDateTime(dateFormat.format(cal.getTime()));
	}
	public String toPrettyDate() {
		return String.format(Locale.KOREA, "%d-%d-%d", this.getYear(), this.getMonth(), this.getDate());
	}
	public String toDate() {
		return String.format(Locale.KOREA, "%d%d%d", this.getYear(), this.getMonth(), this.getDate());
	}
	public String toPrettyDateTime() {
		return String.format(Locale.KOREA, "%d-%d-%d(%d:%d:%d)", this.getYear(), this.getMonth(), this.getDate(), 
				this.getHours(), this.getMinutes(), this.getSeconds());
	}
}
