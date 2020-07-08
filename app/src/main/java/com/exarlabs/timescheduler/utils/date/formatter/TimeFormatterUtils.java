package com.exarlabs.timescheduler.utils.date.formatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;

import com.exarlabs.timescheduler.R;


/**
 * Utility class for formatting time.
 * Created by Arnold on 25.08.2016.
 */
public class TimeFormatterUtils {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    public enum TimeFormatter {
        NUMBER_OF_MILLISECONDS_IN_A_SECOND(1000L), NUMBER_OF_MILLISECONDS_IN_A_MINUTE(60L * 1000L), NUMBER_OF_MILLISECONDS_IN_AN_HOUR(
                        60L * 60L * 1000L), NUMBER_OF_MILLISECONDS_IN_AN_DAY(24L * 60L * 60L * 1000L), NUMBER_OF_MILLISECONDS_IN_A_WEEK(
                        7L * 24L * 60L * 60L * 1000L), NUMBER_OF_MILLISECONDS_IN_AN_YEAR(
                        365L * 24L * 60L * 60L * 1000L), NUMBER_OF_SECONDS_IN_A_MINUTE(60L), NUMBER_OF_MINUTES_IN_AN_HOUR(
                        60L), NUMBER_OF_SECONDS_IN_AN_HOUR(60L * 60L), NUMBER_OF_DAYS_IN_A_WEEK(7L);

        private Long value;

        TimeFormatter(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public static TimeFormatter fromLong(Long value) {
            if (value != null) {
                for (TimeFormatter dateFormatter : TimeFormatter.values()) {
                    if (value.equals(dateFormatter.getValue())) {
                        return dateFormatter;
                    }
                }
            }
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    public static final String DURATION_FROMAT_HHh_MMm_SSs = "%02dh:%02dm:%02ds";
    public static final String DURATION_FROMAT_MM_SS = "%02d:%02d";

    // ------------------------------------------------------------------------
    // CONSTRUCTORS

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * @param context
     * @param timeStamp the timestamp which should be converted
     *
     * @return the relative timespan string
     */
    public static String getSecondsAgo(Context context, long timeStamp) {
        long now = System.currentTimeMillis();
        //@formatter:off
        String lastSyncTime = now - timeStamp < DateUtils.DAY_IN_MILLIS ?
                        DateUtils.getRelativeTimeSpanString(timeStamp, now, DateUtils.SECOND_IN_MILLIS, 0).toString():
                        DateUtils.getRelativeDateTimeString(context, timeStamp, DateUtils.SECOND_IN_MILLIS,
                                                                          DateUtils.HOUR_IN_MILLIS,
                                                                          DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_TIME).toString();



        String zeroMinutes = context.getString(R.string.zero_minutes_ago);
        if (lastSyncTime.equals(zeroMinutes)) {
            lastSyncTime = lastSyncTime.replace(zeroMinutes, context.getString(R.string.just_now));
        }
        return lastSyncTime;
    }

    /**
     * @param context
     * @param timeStamp the timestamp which should be converted
     *
     * @return the relative timespan string
     */
    public static String getTimeAgo(Context context, long timeStamp) {
        long now = System.currentTimeMillis();
        //@formatter:off
        String lastSyncTime = now - timeStamp < DateUtils.DAY_IN_MILLIS ?
                        DateUtils.getRelativeTimeSpanString(timeStamp, now, DateUtils.MINUTE_IN_MILLIS, 0).toString():
                        DateUtils.getRelativeDateTimeString(context, timeStamp, DateUtils.MINUTE_IN_MILLIS,
                                                                          DateUtils.WEEK_IN_MILLIS,
                                                                          DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_TIME).toString();



        String zeroMinutes = context.getString(R.string.zero_minutes_ago);
        if (lastSyncTime.equals(zeroMinutes)) {
            lastSyncTime = lastSyncTime.replace(zeroMinutes, context.getString(R.string.just_now));
        }
        return lastSyncTime;
    }

    /**
     * @param context
     * @param timeStamp the timestamp which should be converted
     *
     * @return the relative timespan string
     */
    public static String getDateAgo(Context context, long timeStamp) {
        long now = System.currentTimeMillis();
        //@formatter:off
        String lastSyncTime = now - timeStamp < DateUtils.DAY_IN_MILLIS ?
                        DateUtils.getRelativeTimeSpanString(timeStamp, now, DateUtils.MINUTE_IN_MILLIS, 0).toString():
                        DateUtils.getRelativeDateTimeString(context, timeStamp, DateUtils.DAY_IN_MILLIS,
                                                                          DateUtils.WEEK_IN_MILLIS,
                                                                          DateUtils.FORMAT_ABBREV_MONTH ).toString();



        String zeroMinutes = context.getString(R.string.zero_minutes_ago);
        if (lastSyncTime.equals(zeroMinutes)) {
            lastSyncTime = lastSyncTime.replace(zeroMinutes, context.getString(R.string.just_now));
        }
        return lastSyncTime;
    }

    /**
     * Format the duration in mm:ss:ms format
     *
     * @param duration in seconds
     *
     * @return Formatted String
     */
    @SuppressLint ("DefaultLocale") // intended using the format without locale
    public static String format(long duration) {
        //@formatter:off
        return String.format(DURATION_FROMAT_HHh_MMm_SSs,
                        duration / TimeFormatter.NUMBER_OF_SECONDS_IN_AN_HOUR.getValue(),
                        (duration / TimeFormatter.NUMBER_OF_SECONDS_IN_A_MINUTE.getValue()) % TimeFormatter.NUMBER_OF_SECONDS_IN_A_MINUTE.getValue(),
                        duration % TimeFormatter.NUMBER_OF_SECONDS_IN_A_MINUTE.getValue());
        //@formatter:on
    }

    /**
     * @param duration the duration in milliseconds
     *
     * @return Formats duration like 00:21
     */
    public static String formatDurationMM_SS(long duration) {
        try {
            //@formatter:off
        return String.format(DURATION_FROMAT_MM_SS,
                        (duration / TimeFormatter.NUMBER_OF_SECONDS_IN_A_MINUTE.getValue()) % TimeFormatter.NUMBER_OF_SECONDS_IN_A_MINUTE.getValue(),
                        duration % TimeFormatter.NUMBER_OF_SECONDS_IN_A_MINUTE.getValue());
        //@formatter:on
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
