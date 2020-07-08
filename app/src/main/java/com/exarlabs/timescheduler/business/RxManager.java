package com.exarlabs.timescheduler.business;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A manager which provides helper methods for business logic managers using Rx components.
 * Created by becze on 12/2/2015.
 */
public class RxManager {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static final String TAG = RxManager.class.getSimpleName();

    public static final boolean LOG = true;

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * Executes a certain task in the future
     *
     * @param time
     * @param unit
     * @param callback
     *
     * @return
     */
    public static Subscription executeLaterOnMain(int time, TimeUnit unit, Action1<Long> callback) {
        return Observable.timer(time, unit, AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(callback);
    }

    public static void sRunOnUIThread(final Action0 action) {
        new RxManager().runOnUIThread(action);
    }

    public static <T> void sDoInBackground(final Action1<Subscriber<? super T>> action) {
        new RxManager().doInBackground(action);
    }

    public static <T> void sDoInBackground(final Action1<Subscriber<? super T>> action, Subscriber callback) {
        new RxManager().doInBackground(action, callback);
    }

    public static <T> void sDoInBackground(final Action1<Subscriber<? super T>> action, Subscriber callback, Scheduler workingScheduler) {
        new RxManager().doInBackground(action, workingScheduler, callback);
    }
    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------


    /**
     * Executes the function notifying the callback about the result. Everything will be done on a background thread
     * but the notification is done on the UI thread
     *
     * @param <T>
     * @param action   the action which should be executed and takes the subscriber as the input
     * @param callback callback which will be used to notify the caller after the result
     */
    public <T> Subscription doInBackground(final Action1<Subscriber<? super T>> action, Scheduler scheduler, Subscriber<T> callback) {
        return execute(action, scheduler, AndroidSchedulers.mainThread(), callback);
    }

    public <T> Subscription doInBackground(final Action1<Subscriber<? super T>> action, Scheduler scheduler, Scheduler subscribeOn,
                                           Subscriber<T> callback) {
        return execute(action, scheduler, subscribeOn, callback);
    }

    public <T> Subscription doInBackground(final Action1<Subscriber<? super T>> action, Scheduler scheduler) {
        return doInBackground(action, scheduler, null);
    }

    /**
     * @param f
     * @param callback
     * @param <T>
     *
     * @return executes the function and notifies back the subscriber about the results
     */
    protected <T> Subscription doInBackground(final Func0<T> f, Subscriber<T> callback) {
        return doInBackground(subscriber -> {
            subscriber.onNext(f.call());
            subscriber.onCompleted();
        }, Schedulers.computation(), callback);
    }

    public <T> Subscription doInBackground(final Action1<Subscriber<? super T>> action, Subscriber<T> callback) {
        return doInBackground(action, Schedulers.computation(), callback);
    }


    /**
     * Executes a function in the backgrounf thread without a callback
     * <p>
     * Note: By default it executes the function on the {@link Schedulers}
     *
     * @param action
     * @param <T>
     */
    public <T> void doInBackground(final Action1<Subscriber<? super T>> action) {
        doInBackground(action, ((Subscriber<T>) null));
    }


    /**
     * Executes the action on the UI thread
     *
     * @param action
     */
    public void runOnUIThread(final Action0 action) {
        execute(subscriber -> action.call(), AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread(), null);
    }

    /**
     * Executes the action on the UI thread
     *
     * @param action
     */
    public <T> Subscription runOnUIThread(final Action1<Subscriber<? super T>> action, Subscriber<T> subscriber) {
        return execute(action, AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread(), subscriber);
    }

    /**
     * executes the given function on the given scheduler notifying the caller through the callback
     *
     * @param action            function
     * @param executeOn         the scheduler on which the function will be executed
     * @param callbackScheduler the scheduler on which the callback will be notified
     * @param callback          the callback object which will be notified
     * @param <T>
     *
     * @return the subscription to the Observable function executor
     */
    public <T> Subscription execute(Action1<Subscriber<? super T>> action, Scheduler executeOn, Scheduler callbackScheduler, Subscriber<T> callback) {

        if (callback == null) {
            // Just create a subscriber
            callback = new TestSubscriber<T>();
        }

        return Observable.create((Observable.OnSubscribe<T>) subscriber -> {
            try {
                long time = 0;
                if (LOG) {
                    time = System.currentTimeMillis();
                    Timber.d("Execution started: " + action);
                    if (!Thread.currentThread().getName().contains("main")) {
                        Thread.currentThread().setName(executeOn.toString() + "->" + action.toString());
                    }
                }

                // Execute the action
                action.call(subscriber);

                if (LOG) {
                    Timber.w("Execution done in: " + (System.currentTimeMillis() - time) + " ms for" + action);
                }

                // Make sure that the on completed and the unsubscribe are called
                subscriber.onCompleted();
                subscriber.unsubscribe();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        })
                        //@formatter:off
                        .subscribeOn(executeOn)
                        .observeOn(callbackScheduler)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe(callback);
        //@formatter:on


    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------
}
