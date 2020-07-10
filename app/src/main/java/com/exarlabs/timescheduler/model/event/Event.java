package com.exarlabs.timescheduler.model.event;

import java.util.Map;

import timber.log.Timber;

/**
 * Abstarct implementation of an event
 */
public abstract class Event {

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

    /**
     * The total duration of the event in seconds
     */
    private int mTotalDuration = 1;
    private Map<Integer, Marker> mMarkerMap;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     *
     * @return the Event name which can be displayed
     */
    public abstract String getEventName();

    /**
     * Checks if one of the markers are hit
     *
     * @param sessionCounter
     * @return
     */
    public Marker onSessionTick(long sessionCounter) {
        Timber.d("onSessionTick() called with: sessionCounter = [" + sessionCounter + "]");
        if (mMarkerMap != null) {
            for (Integer integer : mMarkerMap.keySet()) {
                if (integer == sessionCounter % mTotalDuration) {
                    return mMarkerMap.get(integer);
                }
            }
        }

        return Marker.NO_EVENT;
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------


    public int getTotalDuration() {
        return mTotalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        mTotalDuration = totalDuration;
    }

    public  boolean isExpired() {
        return true;
    }
}
