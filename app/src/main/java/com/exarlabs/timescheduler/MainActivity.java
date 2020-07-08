package com.exarlabs.timescheduler;

import javax.inject.Inject;

import android.os.Bundle;
import android.widget.TextView;

import com.exarlabs.timescheduler.business.SessionManager;
import com.exarlabs.timescheduler.ui.base.BaseActivity;
import com.exarlabs.timescheduler.utils.date.formatter.DateFormatterUtils;

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
    @Inject
    SessionManager mSessionManager;

    @BindView (R.id.tv_date)
    TextView mDateTv;

    @BindView (R.id.tv_time)
    TextView mTimeTv;

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

    private void updateUI() {
        mDateTv.setText(DateFormatterUtils.format(System.currentTimeMillis(), DateFormatterUtils.DateFormatter.EEE_MMM_DD_YYYY));
        mTimeTv.setText(DateFormatterUtils.format(System.currentTimeMillis(), DateFormatterUtils.DateFormatter.H_MM_AA));
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
