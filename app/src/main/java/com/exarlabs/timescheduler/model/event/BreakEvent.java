package com.exarlabs.timescheduler.model.event;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Break event in every 50 mins we will do a break
 */
public class BreakEvent extends Event {

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

    public BreakEvent() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        setEndTimestamp(cal.getTimeInMillis());

        addMarker((int) TimeUnit.MINUTES.toSeconds(1), Marker.ONE_MINUTES_REMINDER);
        addMarker(3, Marker.GO_3_2_1);
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    public String getEventName() {
        return "Work starts ";
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
