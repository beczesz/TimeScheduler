package com.exarlabs.timescheduler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
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

    private boolean mIsMuted;

    @BindView (R.id.tv_date)
    TextView mDateTv;

    @BindView (R.id.tv_time)
    TextView mTimeTv;

    @BindView (R.id.tv_event_name)
    TextView mEventName;

    @BindView (R.id.tv_remaining_time)
    TextView mRemainingTime;

    @BindView (R.id.ic_mute)
    TextView mMuteIcon;

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

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        TimeSchedulerApplication.component().inject(this);

        initUI();
        startSession();
    }

    private void initUI() {
        mMuteIcon.setText(R.string.mute);
        mMuteIcon.setOnClickListener(view -> {
            mIsMuted = !mIsMuted;
            mMuteIcon.setText(mIsMuted ? R.string.un_mute : R.string.mute);
            mEventManager.setMuted(mIsMuted);
        });
    }

    private void updateUI() {
        mDateTv.setText(DateFormatterUtils.format(System.currentTimeMillis(), DateFormatterUtils.DateFormatter.EEE_MMM_DD_YYYY));
        mTimeTv.setText(DateFormatterUtils.format(System.currentTimeMillis(), DateFormatterUtils.DateFormatter.H_MM_AA));

        Event upcommingEvent = mEventManager.getUpcommingEvent();
        String eventDateFormat = "";
        String nextEvent = "";

        if (upcommingEvent != null) {
            long endTimestamp = upcommingEvent.getEndTimestamp();
            if (endTimestamp - System.currentTimeMillis() < TimeUnit.DAYS.toMillis(1)) {
                int secondsUntilEnd = (int) ((endTimestamp - System.currentTimeMillis()) / 1000);
                nextEvent = upcommingEvent.getEventName() + " in";
                eventDateFormat = TimeFormatterUtils.format(secondsUntilEnd);
            } else {
                nextEvent = upcommingEvent.getEventName() + " at";
                eventDateFormat = DateFormatterUtils.format(new Date(endTimestamp), DateFormatterUtils.DateFormatter.EEE_DD_MMM_H_MM_AA);
            }
        }

        mRemainingTime.setText(eventDateFormat);
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
        updateUI();
    }


    @Override
    public void onSessionExpired() {

    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
