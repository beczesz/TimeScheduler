package com.exarlabs.timescheduler.business.rx.tasks;

import java.util.concurrent.TimeUnit;

import com.exarlabs.timescheduler.utils.HexUtils;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A task is an operation which can be executed and it returns a the result.
 * A task can be configured what to do when an error occurs,
 * if it should run on UI or on a background thread, and provides integration to handle progress reports.
 * Created by becze on 11/28/2015.
 */
public abstract class Task<T> implements ITask<T> {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    /**
     * The possible states of a task.
     */
    public enum State {
        UNINITIALIZED, INITIALIZING, STARTED, SUCCESS, FAILED, CANCELLED
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static final String TAG = Task.class.getSimpleName();

    public static final boolean LOG = true;

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /**
     * If the task fails with an error then it simply completes without an error.
     */
    protected boolean mFinishOnError = false;

    /**
     * On error it will reexecute the function this number of times
     */
    protected int mRetryCount = 0;

    protected int mTimeout = 0;
    protected TimeUnit mTimeUnit = TimeUnit.SECONDS;

    protected Subscriber<? super T> mSubscriber;

    private State mState = State.UNINITIALIZED;

    /**
     * The object which is the subject of the task
     */
    private T mTaskObject;

    /**
     * The scheduler use for the execution
     */
    private Scheduler mWorkingScheduler;

    /**
     * Scheduler used when the task is done and the user should be notified
     */
    private Scheduler mNotifyScheduler;

    private String mName;
    private String mOriginalName;
    /**
     * Suffix used for log indentation
     */
    private String mSuffix;

    private long mStartTime;
    private long mRunTime;

    private Throwable mLastException;


    private Progress mProgress = new Progress(0, 1);
    private String mProgressNote;

    private Progress.ProgressUpdateListener mProgressUpdateListener;


    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    /**
     * Constructor for a named task
     *
     * @param name
     */
    public Task(String name) {
        this(name, null, Schedulers.computation(), null);
    }

    /**
     * Empty constructor
     */
    public Task() {
        this("", null, Schedulers.computation(), null);
    }

    public Task(String name, T taskObject) {
        this(name, taskObject, Schedulers.computation(), null);
    }

    public Task(T taskObject) {
        this("", taskObject, Schedulers.computation(), null);
    }


    public Task(T taskObject, Scheduler workingScheduler, Scheduler notifyScheduler) {
        this("", taskObject, workingScheduler, notifyScheduler);
    }

    public Task(String name, T taskObject, Scheduler workingScheduler, Scheduler notifyScheduler) {
        mSuffix = "";
        mOriginalName = name;
        mName = "T -> " + name + " of type " + ((!this.getClass().getSimpleName().equals("")) ? this.getClass().getSimpleName() : "Inner class ");
        mTaskObject = taskObject;
        mWorkingScheduler = workingScheduler != null ? workingScheduler : Schedulers.computation();
        mNotifyScheduler = notifyScheduler != null ? notifyScheduler : AndroidSchedulers.mainThread();
    }
    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Method which will be executed in a background thread. Implement here the work done by the task.
     * When the work is done call the onCompleted() method.
     */
    protected abstract void doExecute();


    @Override
    public Subscription execute(Subscriber<T> taskSubscriber) {
        if (taskSubscriber == null) {
            throw new IllegalArgumentException("subscriber must be non null");
        }

        // the Task is started
        start();

        Scheduler workingScheduler = mWorkingScheduler != null ? mWorkingScheduler : Schedulers.computation();

        if (LOG) {
            Timber.d(toString() + " ==== START EX ===== on " + mWorkingScheduler);
        }


        //@formatter:off
        Observable<T> observable = Observable.create((Observable.OnSubscribe<T>) subscriber -> {
            mSubscriber = subscriber;
            try {
                if (LOG) {
                    if (!Thread.currentThread().getName().contains("main")) {
                        Thread.currentThread().setName(this.toString() + "->" + workingScheduler.toString());}
                    }
                doExecute();
            } catch (Exception ex) {
                ex.printStackTrace();
                onError(ex);
            }
        })
        .subscribeOn(workingScheduler)
        .observeOn(mNotifyScheduler != null ? mNotifyScheduler : workingScheduler);
        //@formatter:on

        if (mTimeout > 0) {
            observable = observable.timeout(mTimeout, mTimeUnit, mNotifyScheduler);
        }

        return observable.subscribe(taskSubscriber);
    }

    /**
     * Can be called when the task execution should be cancelled
     */
    public void cancel() {
        markCancelled();
        onCanceled();
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
            mSubscriber.unsubscribe();
        }
    }

    public void onCanceled() {
        if (LOG) {
            Timber.w(toString() + " ==== CANCELED ===== ");
        }
    }


    public void onCompleted() {
        if (LOG) {
            Timber.w(toString() + " ==== END EX ===== ");
        }
        markSuccessful();
        if (mSubscriber != null) {
            mSubscriber.onCompleted();
            mSubscriber = null;
        }
        mProgressUpdateListener = null;

    }

    public void onError(Throwable e) {
        if (LOG) {
            Timber.e(toString() + " onError " + e);
            e.printStackTrace();
        }
        mLastException = e;
        markFailed();
        if (mSubscriber != null) {
            mSubscriber.onError(e);
        }
        mProgressUpdateListener = null;
    }

    public void onNext(T o) {
        if (LOG) {
            Timber.d(toString() + " pending");
        }
        if (mSubscriber != null) {
            mSubscriber.onNext(o);
        }
    }


    public void initialize() {
        mState = State.INITIALIZING;
    }


    protected void start() {
        mStartTime = System.currentTimeMillis();
        mState = State.STARTED;
    }

    protected void markSuccessful() {
        mState = State.SUCCESS;
        mRunTime = getElapsedTimeSinceStart();
        getProgress().setPercentage(1F);
    }

    protected void markCancelled() {
        if (mState == State.STARTED) {
            mState = State.CANCELLED;
            mRunTime = getElapsedTimeSinceStart();
        }
    }

    protected void markFailed() {
        mState = State.FAILED;
        mRunTime = getElapsedTimeSinceStart();
    }

    /**
     * @return true if the task is running
     */
    public boolean isRunning() {
        return mState == State.STARTED;
    }


    /**
     * Re-Execute the task increasing the retry count
     */
    protected void onRetry() {
        Timber.d("On retry: ");
        setRetryCount(getRetryCount() + 1);
        doExecute();
    }

    @Override
    public String toString() {
        String type = getSuffix() + getName() + (getTaskObject() != null ? "<" + getTaskObject().getClass().getSimpleName() + ">#" : "<Void>#");
        return type + HexUtils.toHEXString(this.hashCode(), 8) + (mTaskObject != null ? " Object: " + mTaskObject : "");
    }


    /**
     * Sets the timeout for the task
     *
     * @param timeout
     * @param timeUnit
     */
    public void setTimeout(int timeout, TimeUnit timeUnit) {
        mTimeout = timeout;
        mTimeUnit = timeUnit;
    }

    /**
     * @return the progress betwenen [0,100]
     */
    public float getProgressPercentage() {
        return mProgress.getPercentage();
    }


    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------


    public Scheduler getWorkingScheduler() {
        return mWorkingScheduler;
    }

    public void setWorkingScheduler(Scheduler workingScheduler) {
        mWorkingScheduler = workingScheduler;
    }

    public Scheduler getNotifyScheduler() {
        return mNotifyScheduler;
    }

    public void setNotifyScheduler(Scheduler notifyScheduler) {
        mNotifyScheduler = notifyScheduler;
    }

    public boolean isFinishOnError() {
        return mFinishOnError;
    }

    public void setFinishOnError(boolean finishOnError) {
        mFinishOnError = finishOnError;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    public void setRetryCount(int retryCount) {
        mRetryCount = retryCount;
    }

    public State getState() {
        return mState;
    }

    public String getName() {
        return mName;
    }

    public String getOriginalName() {
        return mOriginalName;
    }

    public void setName(String name) {
        mName = name;
    }

    public T getTaskObject() {
        return mTaskObject;
    }

    public void setSuffix(String suffix) {
        mSuffix = suffix;
    }

    public String getSuffix() {
        return mSuffix;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getElapsedTimeSinceStart() {
        return System.currentTimeMillis() - getStartTime();
    }

    public String elapsedTimeToString() {
        return getElapsedTimeSinceStart() + "ms";
    }

    public long getRunTime() {
        return mRunTime;
    }

    public Throwable getLastException() {
        return mLastException;
    }

    public void setProgress(Progress progress) {
        mProgress = progress;

        if (getProgressUpdateListener() != null) {
            getProgressUpdateListener().onProgressUpdate(progress, getProgressNote());
        }
    }

    public Progress getProgress() {
        return mProgress;
    }

    public String getProgressNote() {
        return mProgressNote;
    }

    public void setProgressNote(String progressNote) {
        mProgressNote = progressNote;
    }

    public Progress.ProgressUpdateListener getProgressUpdateListener() {
        return mProgressUpdateListener;
    }

    public void setProgressUpdateListener(Progress.ProgressUpdateListener progressUpdateListener) {
        mProgressUpdateListener = progressUpdateListener;
    }
}
