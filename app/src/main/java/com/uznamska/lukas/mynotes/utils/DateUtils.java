package com.uznamska.lukas.mynotes.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anna on 2016-04-01.
 */
public class DateUtils {
    private static final String TAG = "MyNotes:DateUtils";
    public static String DATE_SEPARATOR = "-";
    public static final String TIME_SEPARATOR = ":";
    public static String DATE_FORMAT = "yyyy-MM-dd";

    public static final double MIN = 60 * 1000.0;
    public static final double HOUR = 60 * MIN;
    public static final double DAY = 24 * HOUR;
    public static final double MONTH = 30 * DAY;
    public static final double YEAR = 365 * DAY;

    public static final StringBuilder sb = new StringBuilder();
    public static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);


    public static String getStringDate(int year, int month, int day ) {
        sb.setLength(0);
        sb.append(year).append(DATE_SEPARATOR)
                .append(month+1).append(DATE_SEPARATOR)
                .append(day);
        return sb.toString();
    }

    public static final Date getDateFromString(String date) throws ParseException {
        Log.d(TAG, "Format this date " + date);
        if(sdf != null) {
            return sdf.parse(date); //yyyy-M-d
        }
        else {
            Log.d(TAG, "Date is null");
        }
        return null;
    }

    public static  final String getTimeString(long hour, long minute) {
        sb.setLength(0);
        sb.append(hour).append(TIME_SEPARATOR).append(minute > 9 ? "" : "0").append(minute);
        return sb.toString();
    }

    public static final Calendar getCalendarWithTime(String time, Calendar cal) {
        if(cal == null) {
            cal = Calendar.getInstance();
        }
        String [] tokens = time.split(TIME_SEPARATOR);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(tokens[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private void  restOfStuff() {
//        cal.setTime(fromDate);
//
//        //at
//        String[] tokens = c.getString(4).split(":"); //hh:mm
//        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[0]));
//        cal.set(Calendar.MINUTE, Integer.parseInt(tokens[1]));
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
    }
}
