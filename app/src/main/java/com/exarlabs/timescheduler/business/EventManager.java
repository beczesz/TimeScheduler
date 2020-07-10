package com.exarlabs.timescheduler.business;

import javax.inject.Inject;

import com.exarlabs.timescheduler.TimeSchedulerApplication;
import com.exarlabs.timescheduler.model.event.Event;

/**
 * Manages the events, generating the next event
 */
public class EventManager implements SessionManager.SessionEventListener {

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

    private Event mUpcommingEvent;

    @Inject
    SessionManager mSessionManager;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public EventManager() {
        TimeSchedulerApplication.component().inject(this);
        mSessionManager.addSessionEventListener(this);
    }

    /**
     * @return the next upcommingevent
     */
    private Event getNextEvent() {
        return null;
    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionTimerTick(long totalTime) {
        if (mUpcommingEvent == null || mUpcommingEvent.isExpired()) {
            mUpcommingEvent = getNextEvent();
        } else {
            mUpcommingEvent.onSessionTick(totalTime);
        }
    }

    @Override
    public void onSessionExpired() {

    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------


    public Event getUpcommingEvent() {
        return mUpcommingEvent;
    }
}
