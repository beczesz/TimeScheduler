package com.exarlabs.timescheduler;

import javax.inject.Inject;

import android.os.Bundle;
import android.widget.TextView;

import com.exarlabs.timescheduler.business.EventManager;
import com.exarlabs.timescheduler.business.SessionManager;
import com.exarlabs.timescheduler.model.event.Event;
import com.exarlabs.timescheduler.ui.base.BaseActivity;
import com.exarlabs.timescheduler.utils.date.formatter.DateFormatterUtils;
import com.exarlabs.timescheduler.utils.date.formatter.TimeFormatterUtils;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements SessionManager.SessionEventListener {


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
    @BindView (R.id.tv_date)
    TextView mDateTv;

    @BindView (R.id.tv_time)
    TextView mTimeTv;

    @BindView (R.id.tv_event_name)
    TextView mEventName;

    @BindView (R.id.tv_remaining_time)
    TextView mRemainingTime;

    @Inject
    SessionManager mSessionManager;

    @Inject
    EventManager mEventManager;


    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showActionBar(false);

        TimeSchedulerApplication.component().inject(this);

        startSession();
    }

    private void updateUI(long totalTime) {
        mDateTv.setText(DateFormatterUtils.format(System.currentTimeMillis(), DateFormatterUtils.DateFormatter.EEE_MMM_DD_YYYY));
        mTimeTv.setText(DateFormatterUtils.format(System.currentTimeMillis(), DateFormatterUtils.DateFormatter.H_MM_AA));

        Event upcommingEvent = mEventManager.getUpcommingEvent();
        long duration = 0;
        String nextEvent = "";

        if (upcommingEvent != null) {
            int totalDuration = upcommingEvent.getTotalDuration();
            duration = totalDuration - totalTime % totalDuration;
            nextEvent = upcommingEvent.getEventName();
        }

        mRemainingTime.setText(TimeFormatterUtils.format(duration));
        mEventName.setText(nextEvent);
    }

    private void startSession() {
        mSessionManager.startSessionTimer();
        mSessionManager.addSessionEventListener(this);
    }

    @Override
    public void onSessionStarted() {

    }

    @Override
    public void onSessionTimerTick(long totalTime) {
        updateUI(totalTime);
    }


    @Override
    public void onSessionExpired() {

    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
