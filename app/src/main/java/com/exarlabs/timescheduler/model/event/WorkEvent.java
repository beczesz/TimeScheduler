package com.exarlabs.timescheduler.model.event;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Break event in every 50 mins we will do a break
 */
public class WorkEvent extends Event {

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

    public WorkEvent() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.MINUTE, 50);
        cal.set(Calendar.SECOND, 0);

        setEndTimestamp(cal.getTimeInMillis());


        addMarker((int) TimeUnit.MINUTES.toSeconds(10), Marker.TEN_MINUTES_REMINDER);
        addMarker((int) TimeUnit.MINUTES.toSeconds(1), Marker.ONE_MINUTES_REMINDER);
        addMarker(3, Marker.GO_3_2_1);
    }

    public static boolean isWorkHour() {
        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(System.currentTimeMillis());
        calStart.set(Calendar.HOUR_OF_DAY, 6);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(System.currentTimeMillis());
        calEnd.set(Calendar.HOUR_OF_DAY, 18);
        calEnd.set(Calendar.MINUTE, 0);
        calEnd.set(Calendar.SECOND, 0);

        return calStart.getTimeInMillis() < System.currentTimeMillis() && calEnd.getTimeInMillis() > System.currentTimeMillis();
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    public String getEventName() {
        return "Next break";
    }

    @Override
    public Marker getFinishMarker() {
        Marker marker = getMarkerMap().get(getDuration());
        return marker != null ? marker : Marker.NO_EVENT;
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
