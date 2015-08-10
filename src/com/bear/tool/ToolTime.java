package com.bear.tool;

import java.util.Calendar;

public class ToolTime {
	
	public static Calendar fillCalendar(Calendar time, int hour, int minute, int seconde, int millisecond){
		time.set(Calendar.HOUR_OF_DAY, hour);
		time.set(Calendar.MINUTE, minute);
		time.set(Calendar.SECOND, seconde);
		time.set(Calendar.MILLISECOND, millisecond);
		return time;
	}
	
}
