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
import com.exarlabs.timescheduler.model.event.BreakEvent;
import com.exarlabs.timescheduler.model.event.Event;
import com.exarlabs.timescheduler.model.event.Marker;
import com.exarlabs.timescheduler.model.event.OffworkEvent;
import com.exarlabs.timescheduler.model.event.WorkEvent;

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
        mMarkerMap.put(Marker.ONE_MINUTES_REMINDER, Arrays.asList(R.raw.one_more_minute));
        mMarkerMap.put(Marker.GO_3_2_1, Arrays.asList(R.raw.go_3_2_1));
        mMarkerMap.put(Marker.END, Arrays.asList(R.raw.end));
    }

    /**
     * @return the next upcommingevent
     */
    private Event getNextEvent() {
        /*
         * If it is not weekend then we can continue
         */
        if (OffworkEvent.isWeekend() || !WorkEvent.isWorkHour()) {
            return new OffworkEvent();
        } else if (WorkEvent.isWorkHour()) {

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            return cal.get(Calendar.MINUTE) >= 50 ? new BreakEvent() : new WorkEvent();
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

            mUpcommingEvent = getNextEvent();
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
