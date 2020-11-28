package com.exarlabs.timescheduler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.exarlabs.timescheduler.business.EventManager;
import com.exarlabs.timescheduler.business.SessionManager;
import com.exarlabs.timescheduler.model.event.Event;
import com.exarlabs.timescheduler.ui.CustomViewGroup;
import com.exarlabs.timescheduler.ui.base.BaseActivity;
import com.exarlabs.timescheduler.utils.date.formatter.DateFormatterUtils;
import com.exarlabs.timescheduler.utils.date.formatter.TimeFormatterUtils;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.OnLongClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private int[] mFunBackgrounds;

    @BindView (R.id.cl_main_container)
    ConstraintLayout mMainContainer;

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

    @BindView (R.id.sw_fun_mode)
    Switch mFunMode;


    @BindView (R.id.ic_logo)
    ImageView mLogo;

    @BindView (R.id.ic_fun_logo)
    ImageView mFunLogo;


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

        // Full screen mode, no notification bar
        makeFullScreen();
    }

    private void initUI() {

        mFunBackgrounds = new int[] { R.color.material_amber_900, R.color.material_blue_900, R.color.material_brown_900, R.color.material_cyan_900,
                                      R.color.material_indigo_900, R.color.material_lime_900, R.color.material_orange_900, R.color.material_pink_900,
                                      R.color.material_purple_900, R.color.material_red_900, R.color.material_deep_orange_900,
                                      R.color.material_deep_purple_900, R.color.material_deep_purple_900, R.color.material_teal_900,
                                      R.color.material_yellow_900, R.color.material_green_900, R.color.material_light_green_900,
                                      R.color.material_light_blue_900, };

        mMuteIcon.setText(R.string.mute);
        mMuteIcon.setOnClickListener(view -> {
            mIsMuted = !mIsMuted;
            mMuteIcon.setText(mIsMuted ? R.string.un_mute : R.string.mute);
            mEventManager.setMuted(mIsMuted);
        });

        mFunMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mEventManager.setMode(isChecked ? EventManager.Mode.FUN : EventManager.Mode.WORK);

                // Add a random backgroun in fun mode
                int bkgRes = R.color.md_black_1000;

                if (isChecked) {
                    bkgRes = mFunBackgrounds[(int) (mFunBackgrounds.length * Math.random())];
                }

                mLogo.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                mFunLogo.setVisibility(!isChecked ? View.GONE : View.VISIBLE);

                mMainContainer.setBackgroundColor(getResources().getColor(bkgRes));
            }
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

    @OnLongClick (R.id.ic_mute)
    boolean onTestVoice() {
        mEventManager.testLong();
        return true;
    }


    /**
     * Enables the full screen mode on the activity
     */
    protected void makeFullScreen() {

        // This work only for android 4.4+
        hideSystemUI();

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                hideSystemUI();
            }
        });

        // Disable the keyguard
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();

        try {
            disableStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint ("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //@formatter:off
            getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            //@formatter:on


        }
    }

    /**
     * Hides the system UI after some delay
     */
    public void hideSystemUIAsync() {
        //@formatter:off
        Observable.timer(500, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnCompleted(() -> hideSystemUI())
                        .doOnError(throwable -> throwable.printStackTrace())
                        .subscribe();
        //@formatter:on
    }

    /**
     * Disables the status bar expansion (pull down menu from Android)
     */
    private void disableStatusBarExpansion() {
        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                        // this is to enable the notification to recieve touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (24 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(this);

        manager.addView(view, localLayoutParams);
    }

    @OnLongClick (R.id.ic_logo)
    boolean onLogoClick() {
        Toast.makeText(this, String.format("Version %s (#%d)", BuildConfig.VERSION_NAME, BuildConfig.BUILD_NUMBER), Toast.LENGTH_SHORT).show();
        return true;
    }


    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
