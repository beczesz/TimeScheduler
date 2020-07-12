package com.exarlabs.timescheduler.business;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.media.MediaPlayer;

import com.exarlabs.timescheduler.R;
import com.exarlabs.timescheduler.TimeSchedulerApplication;
import com.exarlabs.timescheduler.model.event.Event;
import com.exarlabs.timescheduler.model.event.Marker;
import com.exarlabs.timescheduler.model.event.TestEvent;
import com.exarlabs.timescheduler.model.event.WeekedEvent;

import timber.log.Timber;

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

    private Map<Marker, List<Integer>> mMarkerMap;

    private Event mUpcommingEvent;

    @Inject
    SessionManager mSessionManager;

    @Inject
    Context mContext;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public EventManager() {
        TimeSchedulerApplication.component().inject(this);
        mSessionManager.addSessionEventListener(this);

        mMarkerMap = new HashMap<>();
        mMarkerMap.put(Marker.MARKER, Arrays.asList(R.raw.marker));
        mMarkerMap.put(Marker.BREAK_3_2_1, Arrays.asList(R.raw.break_3_2_1));
        mMarkerMap.put(Marker.TEN_MINUTES_REMINDER, Arrays.asList(R.raw.ten_minutes_left));
        mMarkerMap.put(Marker.END, Arrays.asList(R.raw.end));
    }

    /**
     * @return the next upcommingevent
     */
    private Event getNextEvent() {
        /*
         * If it is not weekend then we can continue
         */
        Calendar c1 = Calendar.getInstance();
        if (c1.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c1.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

        } else {
            return new WeekedEvent();
        }

        return null;
    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionTimerTick(long totalTime) {
        if (mUpcommingEvent == null || mUpcommingEvent.isExpired()) {

            if (mUpcommingEvent != null) {
                // The event must be expired
                playAudio(mUpcommingEvent.getFinishMarker());
            }

            //            mUpcommingEvent = getNextEvent();
            mUpcommingEvent = new TestEvent();
            Timber.d("onSessionTimerTick() new event: " + mUpcommingEvent.getClass().getSimpleName());
        } else {
            Marker marker = mUpcommingEvent.onSessionTick(totalTime);
            playAudio(marker);
        }
    }

    /**
     * Plays a random audio with the given marker
     *
     * @param marker
     */
    private void playAudio(Marker marker) {
        Timber.d("playAudio() called with: marker = [" + marker + "]");
        if (mMarkerMap.containsKey(marker)) {
            List<Integer> resources = mMarkerMap.get(marker);
            int randomIndex = (int) (Math.random() * resources.size());
            Integer resId = resources.get(randomIndex);

            MediaPlayer mPlayer = MediaPlayer.create(mContext, resId);
            mPlayer.start();
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
