package com.bluerisc.eprivo.utils.date.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bluerisc.eprivo.utils.date.CalendarUtils;

/**
 * Date format utils: formatting Date, convert string in Date
 * Created by Arnold on 25.08.2016.
 */
public class DateFormatterUtils {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    public enum DateFormatter {
        //@formatter:off
        H_MM_AA("h:mm aa"),
        HH_MM_SS("HH:mm:ss"),
        HH24_MM("HH:mm"),
        dM("d. MMM"),
        MMMd("MMM d"),
        MMM_DD_YYYY("MMM dd, yyyy"),
        MM_DD_YYYY("MM/dd/yyyy"),
        EEE_DD_MMM("EEE dd/MMM"),
        YYYY_MM_DD("yyyy-MM-dd"),
        DD_MMM_YYYY("dd-MMM-yyyy"),
        YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
        YYYY_MM_DD_HH_MM_SS_SSSS("yyyy-MM-dd HH:mm:ss.SSS"),
        HH_MM_SS_SSS("HH:mm:ss.SSS "),
        MM_DD__YYYY_H_MM_AA("MMM dd, yyyy h:mm aa"),
        MM_DD__YYYY_H_MM_AA_Z("MMM dd, yyyy h:mm aa z");
        //@formatter:on

        private String value;

        DateFormatter(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static DateFormatter fromString(String text) {
            if (text != null) {
                for (DateFormatter element : DateFormatter.values()) {
                    if (text.equalsIgnoreCase(element.toString())) {
                        return element;
                    }
                }
            }
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("", Locale.US);

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Formats the given date using the given format.
     *
     * @param date    Date
     * @param formats Formats
     *
     * @return formatted date string.
     */
    public static String format(Date date, String formats) {
        try {
            SIMPLE_DATE_FORMAT.applyPattern(formats);
            return SIMPLE_DATE_FORMAT.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String format(Date date, DateFormatter dateFormatter) {
        try {
            SIMPLE_DATE_FORMAT.applyPattern(dateFormatter.toString());
            return SIMPLE_DATE_FORMAT.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String format(long timestamp, DateFormatter dateFormatter) {
        try {
            SIMPLE_DATE_FORMAT.applyPattern(dateFormatter.toString());
            return SIMPLE_DATE_FORMAT.format(new Date(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * @param date
     *
     * @return the date when the message is recived
     */
    public static String formatReciveDate(Date date) {

        if (date != null) {
            Date now = new Date();
            String pattern;
            if (CalendarUtils.isOnSameDay(now, date)) {
                pattern = DateFormatter.H_MM_AA.toString();
            } else if (CalendarUtils.isInThisYear(date)){
                pattern = DateFormatter.MMMd.toString();
            } else {
                pattern = DateFormatter.MMM_DD_YYYY.toString();
            }
            SIMPLE_DATE_FORMAT.applyPattern(pattern);
            return SIMPLE_DATE_FORMAT.format(date);
        } else {
            return "";
        }
    }

    /**
     * Parses a text by a formatter and returns a Date
     *
     * @param format   Formatter
     * @param dateText Text which contains the date
     *
     * @return Date by the date string
     */
    public static Date parse(String format, String dateText) {

        Date result = null;

        try {
            SIMPLE_DATE_FORMAT.applyPattern(format);
            result = SIMPLE_DATE_FORMAT.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String formatToYYYY_MM_DD_HH_MM(Date date) {
        return format(date, DateFormatter.YYYY_MM_DD_HH_MM.toString());
    }

    public static String formatToYYYY_MM_DD(Date date) {
        return format(date, DateFormatter.YYYY_MM_DD.toString());
    }

    public static String formatTo_D_MMM(Date date) {
        return format(date, DateFormatter.dM.toString());
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
