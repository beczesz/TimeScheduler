package com.bluerisc.eprivo.utils.date;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Calendar utility class
 * <p>
 * Created by atiyka on 2016.09.28..
 */
public class CalendarUtils {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static Calendar instance1 = Calendar.getInstance();
    private static Calendar instance2 = Calendar.getInstance();

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Calculates and returns a long, containing the millis only of the time
     *
     * @param dateTime date time
     *
     * @return the time in millis
     */
    public static long getTimeMillis(Calendar dateTime) {
        long inMillis = dateTime.getTimeInMillis();
        Calendar time = (Calendar) dateTime.clone();
        // go back to 00:00:00 today
        time.set(Calendar.HOUR_OF_DAY, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        // calculate the time in millis
        inMillis -= time.getTimeInMillis();
        return inMillis;
    }

    /**
     * Calculates and returns a long, containing the millis only of the time
     *
     * @param hours   hours
     * @param minutes minutes
     *
     * @return the time in millis
     */
    public static long getTimeMillis(int hours, int minutes) {
        Calendar time = instance1;
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, hours);
        time.set(Calendar.MINUTE, minutes);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        return getTimeMillis(time);
    }

    /**
     * Creates a calendar object based on millis calculated by the getTimeMillis(..) methods
     *
     * @param millis millis since 00:00
     *
     * @return the calendar
     */
    public static Calendar getCalendarFromMillis(long millis) {
        int seconds = (int) (millis / 1000);
        int hour = seconds / 3600;
        int minute = (seconds % 3600) / 60;

        Calendar time = instance1;
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);

        return time;
    }

    /**
     * @param date1
     * @param date2
     *
     * @return true if the dates are on the same date
     */
    public static boolean isOnSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        Calendar cal1 = instance1;
        Calendar cal2 = instance2;
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isInThisYear(Date date) {
        Calendar calNow = instance1;
        calNow.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = instance2;
        cal.setTime(date);

        return calNow.get(Calendar.YEAR) == cal.get(Calendar.YEAR);
    }

    /**
     * @param startTS start timestamps
     * @param endTS   end timestamps
     *
     * @return Returns the number of days between two timestamps
     */
    public static long getDayCount(long startTS, long endTS) {
        return Math.round((endTS - startTS) / (double) TimeUnit.DAYS.toMillis(1));
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
