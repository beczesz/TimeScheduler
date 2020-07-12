package com.exarlabs.timescheduler.model.event;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Break event in every 50 mins we will do a break
 */
public class TestEvent extends Event {

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

    public TestEvent() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
        setEndTimestamp(cal.getTimeInMillis());

        addMarker(5, Marker.MARKER);
        addMarker(3, Marker.BREAK_3_2_1);
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    public String getEventName() {
        return "Test event";
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
