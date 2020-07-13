package com.exarlabs.timescheduler.model.event;

import java.util.HashMap;
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
    private long mStartTimestamp = 1;
    private long mEndTimestamp = 1;
    private Map<Integer, Marker> mMarkerMap;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public Event() {
        mMarkerMap = new HashMap<>();
        mStartTimestamp = System.currentTimeMillis();
        mEndTimestamp = System.currentTimeMillis();
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
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
                if (integer == getRemainingSeconds()) {
                    return mMarkerMap.remove(integer);
                }
            }
        }

        return Marker.NO_EVENT;
    }

    int getDuration() {
        return (int) ((getEndTimestamp() - getStartTimestamp()) / 1000);
    }

    int getRemainingSeconds() {
        return (int) ((getEndTimestamp() - System.currentTimeMillis()) / 1000);
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------


    public long getEndTimestamp() {
        return mEndTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        mEndTimestamp = endTimestamp;
    }

    public long getStartTimestamp() {
        return mStartTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        mStartTimestamp = startTimestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > getEndTimestamp();
    }


    Marker addMarker(Integer integer, Marker marker) {
        return mMarkerMap.put(integer, marker);
    }

    protected Map<Integer, Marker> getMarkerMap() {
        return mMarkerMap;
    }

    public abstract Marker getFinishMarker();
}
