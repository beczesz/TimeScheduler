package com.exarlabs.timescheduler.business.rx.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.exarlabs.timescheduler.utils.HexUtils;

import androidx.annotation.NonNull;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A task whose duty is to run multiple other tasks
 * Created by becze on 11/28/2015.
 */
public abstract class MultiTask<T> extends Task<T> implements Progress.ProgressUpdateListener {

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

    private List<T> mTasks;
    private List<T> mFailedTasks;
    private int mNumberOfExecutedTasks = 0;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public MultiTask(String name, Scheduler executionThread, Scheduler subscriptionThread) {
        this(name, new ArrayList<>(), executionThread, subscriptionThread);
    }

    public MultiTask(String name, List<T> tasks, Scheduler executionThread, Scheduler subscriptionThread) {
        super(name, null, executionThread, subscriptionThread);
        mTasks = tasks;
        mFailedTasks = new ArrayList<>();
    }

    public MultiTask(String name, List<T> tasks) {
        this(name, tasks, Schedulers.computation(), AndroidSchedulers.mainThread());
    }

    public MultiTask(List<T> tasks) {
        this("", tasks);
    }

    public MultiTask(String name) {
        this(name, new ArrayList<>());
    }


    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------


    @Override
    protected void doExecute() {

    }

    @Override
    public void start() {
        setProgress(new Progress());

        for (T task : mTasks) {
            if (task instanceof Task) {
                ((Task) task).setProgressUpdateListener(this);
            }
        }

        super.start();
    }

    @Override
    public String toString() {
        String type = getSuffix() + getName() + (getTaskObject() != null ? "<" + getTaskObject().getClass().getSimpleName() + ">#" : "<Void>#");
        return type + HexUtils.toHEXString(this.hashCode(), 8) + " Progress: " + getProgress().getFormattedPercentage();
    }


    /**
     * Combines and updates the progress based on the other progresses
     */
    protected void updateProgress() {

        Progress progress = null;
        for (T task : mTasks) {
            if (task instanceof Task) {
                if (progress == null) {
                    progress = ((Task) task).getProgress().cloneProgress();
                } else {
                    progress.combine(((Task) task).getProgress());
                }
            }
        }

        // Update the current progress
        setProgress(progress);
    }

    @Override
    public void onProgressUpdate(Progress progress, String note) {
        setProgressNote(note);
        updateProgress();
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------

    /**
     * Adds a new task to th sequence task
     *
     * @param task
     *
     * @return true if the task has been added successfully
     */
    public boolean add(T task) {
        return mTasks.add(task);
    }

    /**
     * Adds the list of tasks
     *
     * @param collection
     *
     * @return true if it could be added
     */
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return mTasks.addAll(collection);
    }

    public int getNumberOfExecutedTasks() {
        return mNumberOfExecutedTasks;
    }

    void setNumberOfExecutedTasks(int numberOfExecutedTasks) {
        mNumberOfExecutedTasks = numberOfExecutedTasks;
    }

    void incrementNumberOfExecutedTasks() {
        mNumberOfExecutedTasks++;
    }


    public List<T> getTasks() {
        return mTasks;
    }

    List<T> getFailedTasks() {
        return mFailedTasks;
    }


}
