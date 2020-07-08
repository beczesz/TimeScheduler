package com.exarlabs.timescheduler.business;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SessionManager {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    /**
     * Session event listener interface
     */
    public interface SessionEventListener {

        /**
         * Callback when the session is started
         */
        void onSessionStarted();

        /**
         * Callback when the session timer ticks
         *
         * @param totalTime the total time in seconds
         */
        void onSessionTimerTick(long totalTime);

        /**
         * Callback when the session expires
         */
        void onSessionExpired();
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

    private List<SessionEventListener> mSessionEventListeners;


    private Subscription mSessionSubscription;

    private int mRemainingTime;

    /**
     * Marks the timestamp of last activity when the session was reset. Note: the difference between this field and mLastUserActivity is that the
     * former one used to decide if the session is still valid.
     * <p>
     * The latter one used to decide what type of user authentication is required
     */
    private long mLastSessionActivity;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public SessionManager() {
        mSessionEventListeners = new ArrayList<>();
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Starts / restarts a new session timer
     */
    public void startSessionTimer() {
        Timber.d("startSessionTimer: ");

        for (SessionEventListener sessionEventListener : mSessionEventListeners) {
            sessionEventListener.onSessionStarted();
        }

        //@formatter:off
        mSessionSubscription = Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                        .onBackpressureDrop(timeout -> Timber.w( "drop timer : " + timeout))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(time -> onSecureSessionTimerTick(time));
        //@formatter:on
    }

    /**
     * @param totalTime the session tick count
     */
    private void onSecureSessionTimerTick(long totalTime) {

        for (SessionEventListener sessionEventListener : mSessionEventListeners) {
            sessionEventListener.onSessionTimerTick(totalTime);
        }
    }

    /**
     * Adds a new {@link SessionEventListener}
     *
     * @param sessionEventListener
     */
    public void addSessionEventListener(SessionEventListener sessionEventListener) {
        if (!mSessionEventListeners.contains(sessionEventListener)) {
            mSessionEventListeners.add(sessionEventListener);
        }
    }

    /**
     * Removes the given listener
     *
     * @param listener
     */
    public void removeSessionEventListener(SessionEventListener listener) {
        mSessionEventListeners.remove(listener);
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
