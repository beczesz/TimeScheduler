package com.exarlabs.timescheduler.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.DateUtils;

/**
 * Created by jordi on 19/02/17.
 */
public class TimeUtils {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
    @Deprecated
    public static String getTimeAgo(Date date) {
        if (date != null) {
            long now = System.currentTimeMillis();
            String agoString = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.MINUTE_IN_MILLIS));
            if (agoString.equalsIgnoreCase("0 minutes ago") || agoString.equalsIgnoreCase("In 0 minutes")) {
                agoString = "just now";
            }
            return agoString;
        }

        return "";
    }


    @Deprecated
    public static long getTimeStampFromTimeString(String timeString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
        Date convertedDate = new Date();
        try {
            convertedDate = simpleDateFormat.parse(timeString);
            return convertedDate.getTime() / 1000L;

        } catch (ParseException e) {
            return 0;
        }
    }

    @Deprecated
    public static int getTimeStampFromDate(Date date) {
        return (int) (date.getTime() / 1000L);
    }

    public static int getNrSecondsUntil (long timeStamp) {
        return (int) (timeStamp - System.currentTimeMillis());
    }

    @Deprecated
    public static String getTimeStringFromDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
        return simpleDateFormat.format(date);
    }


    @Deprecated
    public static Date getTimeFromTimeStamp(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm a");
            Date currenTimeZone = calendar.getTime();
            return currenTimeZone;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Selects a date from the input dates based on the returnGreater flag
     * Note: if one of the dates is null then the other date is returned
     *
     * @param dateA
     * @param dateB
     * @param returnGreater
     *
     * @return
     */
    public static Date selectDate(Date dateA, Date dateB, boolean returnGreater) {
        if (dateA == null) {
            return dateB;
        }
        if (dateB == null) {
            return dateA;
        }

        if (returnGreater) {
            return dateA.getTime() > dateB.getTime() ? dateA : dateB;
        } else {
            return dateA.getTime() > dateB.getTime() ? dateB : dateA;
        }
    }

}
