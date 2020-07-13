package com.exarlabs.timescheduler.model.event;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Break event in every 50 mins we will do a break
 */
public class OffworkEvent extends Event {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * @return true if this is a weekend
     */
    public static boolean isWeekend() {
        Calendar c1 = Calendar.getInstance();
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------


    private int mReminder10Mins;
    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public OffworkEvent() {
        Calendar cal = Calendar.getInstance();
        if (isWeekend()) {
            cal.setTimeInMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        } else {
            cal.setTimeInMillis(System.currentTimeMillis());
            if (cal.get(Calendar.HOUR) > 8) {
                cal.setTimeInMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
            }
        }

        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        setEndTimestamp(cal.getTimeInMillis());
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    public String getEventName() {
        return "Work starts";
    }

    @Override
    public Marker getFinishMarker() {
        return Marker.NO_EVENT;
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
