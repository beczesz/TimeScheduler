package com.exarlabs.timescheduler.business.rx.tasks;

import java.util.Arrays;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Runs the tasks in parallel
 * Created by becze on 11/30/2015.
 */
public class ParallelTask<T extends Task> extends MultiTask<T> {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static final String TAG = ParallelTask.class.getSimpleName();

    public static final boolean LOG = true;


    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * Creates a task which executes a sequence of tasks
     *
     * @param tasks
     *
     * @return
     */
    public static <T extends Task> ParallelTask create(T... tasks) {
        return new ParallelTask<>(Arrays.asList(tasks));
    }


    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public ParallelTask(String name, Scheduler executionThread, Scheduler subscriptionThread) {
        super(name, executionThread, subscriptionThread);
    }

    public ParallelTask(String name, List<T> tasks, Scheduler executionThread, Scheduler subscriptionThread) {
        super(name, tasks, executionThread, subscriptionThread);
    }

    public ParallelTask(String name, List<T> tasks) {
        super(name, tasks);
    }

    public ParallelTask(List<T> tasks) {
        super(tasks);
    }

    public ParallelTask(String name) {
        super(name);
    }

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    protected void doExecute() {
        setNumberOfExecutedTasks(0);

        // We can complete since we don't have any tasks to handle
        if (getTasks().size() == 0) {
            onCompleted();
            return;
        }

        for (T task : getTasks()) {
            if (LOG) {
                Timber.w( toString() + " ==== START EX =====> " + task);
            }
            task.setSuffix("    " + getSuffix());
            task.execute(new Subscriber() {
                @Override
                public void onCompleted() {
                    incrementNumberOfExecutedTasks();

                    updateProgress();

                    if (LOG) {
                        Timber.w( ParallelTask.this.toString() + " <==== END EX ===== " + task + " took " + task.elapsedTimeToString());
                    }

                    ParallelTask.this.onNext(task);
                    checkIsFinished();
                }

                @Override
                public void onError(Throwable e) {
                    if (LOG) {
                        Timber.e( ParallelTask.this.toString() + " onError for " + task + " with message " + e);
                    }


                    if (!isFinishOnError()) {
                        incrementNumberOfExecutedTasks();
                        getFailedTasks().add(task);
                        updateProgress();
                        checkIsFinished();
                        if (LOG) {
                            Timber.w( ParallelTask.this.toString() + " continue on error");
                        }
                    } else {
                        ParallelTask.this.onError(e);
                    }
                }

                @Override
                public void onNext(Object o) {
                    if (LOG) {
                        Timber.d( "  " + ParallelTask.this.toString() + " pending: " + task);
                    }
                }
            });
        }

    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);

        // cancel every other tasks
        cancel();
    }

    private void checkIsFinished() {
        if (getNumberOfExecutedTasks() == getTasks().size()) {
            if (LOG) {
                Timber.i( toString() + " ========= FINISH ========= " + " took " + elapsedTimeToString());
            }
            onCompleted();
        }
    }

    @Override
    public void cancel() {
        Timber.d("cancel() called");
        super.cancel();
        for (T task : getTasks()) {
            task.cancel();
        }
    }


    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------
}
