package com.exarlabs.timescheduler.business;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.net.Uri;

import com.exarlabs.timescheduler.R;
import com.exarlabs.timescheduler.TimeSchedulerApplication;
import com.exarlabs.timescheduler.model.event.BreakEvent;
import com.exarlabs.timescheduler.model.event.Event;
import com.exarlabs.timescheduler.model.event.Marker;
import com.exarlabs.timescheduler.model.event.OffworkEvent;
import com.exarlabs.timescheduler.model.event.WorkEvent;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import timber.log.Timber;


/**
 * Manages the events, generating the next event
 */
public class EventManager implements SessionManager.SessionEventListener {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    public enum Mode {
        WORK, FUN
    }

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

    private Mode mMode = Mode.WORK;

    @Inject
    SessionManager mSessionManager;

    @Inject
    Context mContext;
    private boolean mMuted;
    private SimpleExoPlayer mExoPlayer;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public EventManager() {
        TimeSchedulerApplication.component().inject(this);
        mSessionManager.addSessionEventListener(this);


        mExoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        mExoPlayer.setPlayWhenReady(true);

        initVoices();
    }

    private void initVoices() {
        mMarkerMap = new HashMap<>();

        switch (mMode) {

            case WORK:
                mMarkerMap.put(Marker.MARKER, Arrays.asList(R.raw.marker));
                mMarkerMap.put(Marker.BREAK_3_2_1, Arrays.asList(R.raw.break_3_2_1));
                mMarkerMap.put(Marker.TEN_MINUTES_REMINDER, Arrays.asList(R.raw.ten_minutes_left));
                mMarkerMap.put(Marker.ONE_MINUTES_REMINDER, Arrays.asList(R.raw.one_more_minute));
                mMarkerMap.put(Marker.GO_3_2_1, Arrays.asList(R.raw.go_3_2_1));
                mMarkerMap.put(Marker.END, Arrays.asList(R.raw.end));
                mMarkerMap.put(Marker.TEST_lONG, Arrays.asList(R.raw.lorem));
                mMarkerMap.put(Marker.TEST,
                               Arrays.asList(R.raw.voice_test, R.raw.voice_test1, R.raw.voice_test2, R.raw.voice_test3, R.raw.voice_test4,
                                             R.raw.voice_test5, R.raw.voice_test6, R.raw.voice_test7, R.raw.voice_test8, R.raw.voice_test9,
                                             R.raw.voice_test10, R.raw.voice_test11, R.raw.voice_test12, R.raw.voice_test13, R.raw.voice_test14,
                                             R.raw.voice_test15, R.raw.voice_test16, R.raw.voice_test17, R.raw.voice_test18, R.raw.voice_test19,
                                             R.raw.voice_test20, R.raw.voice_test21, R.raw.voice_test22, R.raw.voice_test23, R.raw.voice_test24,
                                             R.raw.voice_test25, R.raw.voice_test26, R.raw.voice_test27));
                break;

            case FUN:
                mMarkerMap.put(Marker.MARKER, Arrays.asList(R.raw.marker));
                mMarkerMap.put(Marker.BREAK_3_2_1,
                               Arrays.asList(R.raw.fun_break_1, R.raw.fun_break_2, R.raw.fun_break_3, R.raw.fun_break_4, R.raw.fun_break_5,
                                             R.raw.fun_break_6, R.raw.fun_break_7, R.raw.fun_break_8, R.raw.fun_break_9, R.raw.fun_break_10,
                                             R.raw.fun_break_11, R.raw.fun_break_12, R.raw.fun_break_13, R.raw.fun_break_14, R.raw.fun_break_15,
                                             R.raw.fun_break_16, R.raw.fun_break_17

                               ));
                mMarkerMap.put(Marker.TEN_MINUTES_REMINDER,
                               Arrays.asList(R.raw.fun_10_mins_1, R.raw.fun_10_mins_2, R.raw.fun_10_mins_3, R.raw.fun_10_mins_4, R.raw.fun_10_mins_5,
                                             R.raw.fun_10_mins_6, R.raw.fun_10_mins_7, R.raw.fun_10_mins_8, R.raw.fun_10_mins_9, R.raw.fun_10_mins_10,
                                             R.raw.fun_10_mins_11, R.raw.fun_10_mins_12, R.raw.fun_10_mins_13, R.raw.fun_10_mins_14,
                                             R.raw.fun_10_mins_15


                               ));
                mMarkerMap.put(Marker.ONE_MINUTES_REMINDER, Arrays.asList(R.raw.fun_1_mins_1));

                mMarkerMap.put(Marker.GO_3_2_1,
                               Arrays.asList(R.raw.fun_work_1, R.raw.fun_work_2, R.raw.fun_work_3, R.raw.fun_work_4, R.raw.fun_work_5,
                                             R.raw.fun_work_6, R.raw.fun_work_7, R.raw.fun_work_8, R.raw.fun_work_9, R.raw.fun_work_10,
                                             R.raw.fun_work_11, R.raw.fun_work_12, R.raw.fun_work_13, R.raw.fun_work_14, R.raw.fun_work_15,
                                             R.raw.fun_work_16


                               ));
                mMarkerMap.put(Marker.END, Arrays.asList(R.raw.end));
                mMarkerMap.put(Marker.TEST_lONG, Arrays.asList(R.raw.lorem));
                mMarkerMap.put(Marker.TEST, Arrays.asList(R.raw.fun_test_1, R.raw.fun_test_2, R.raw.fun_test_3, R.raw.fun_test_4, R.raw.fun_test_5,
                                                          R.raw.fun_test_6, R.raw.fun_test_7, R.raw.fun_test_8, R.raw.fun_test_9, R.raw.fun_test_10,
                                                          R.raw.fun_test_11, R.raw.fun_test_12, R.raw.fun_test_13, R.raw.fun_test_14,
                                                          R.raw.fun_test_15));
                break;
        }

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
        if (mMuted) {
            return;
        }

        if (mMarkerMap.containsKey(marker)) {
            Timber.d("playAudio() called with: marker = [" + marker + "]");
            List<Integer> resources = mMarkerMap.get(marker);
            int randomIndex = (int) (Math.random() * resources.size());
            Integer resId = resources.get(randomIndex);

            playMediaWithRes(resId);
        }
    }

    private void playMediaWithRes(Integer resId) {
        Uri uri = RawResourceDataSource.buildRawResourceUri(resId);

        ExtractorMediaSource audioSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(mContext, "MyExoplayer"),
                                                                    new DefaultExtractorsFactory(), null, null);

        mExoPlayer.prepare(audioSource);
    }

    @Override
    public void onSessionExpired() {

    }


    public void testLong() {
        playAudio(Marker.TEST);
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

    public void setMuted(boolean muted) {
        mMuted = muted;
    }

    public boolean getMuted() {
        return mMuted;
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        mMode = mode;
        initVoices();
    }
}
