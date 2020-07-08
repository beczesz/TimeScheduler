package com.exarlabs.timescheduler.business.rx.tasks;

import java.util.Arrays;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Executes a series of tasks in a sequence
 * Created by becze on 11/30/2015.
 *
 * @param <T> Describes the type of tasks which can be added to this Sequence
 */
public class SequenceTask<T extends Task> extends MultiTask<T> {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    public static final boolean LOG = false;

    private static final String TAG = SequenceTask.class.getSimpleName();


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
    public static <T extends Task> SequenceTask create(Task<T>... tasks) {
        return new SequenceTask<>(Arrays.asList(tasks));
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public SequenceTask(String name, Scheduler executionThread, Scheduler subscriptionThread) {
        super(name, executionThread, subscriptionThread);
    }

    public SequenceTask(String name, List<T> tasks, Scheduler executionThread, Scheduler subscriptionThread) {
        super(name, tasks, executionThread, subscriptionThread);
    }

    public SequenceTask(String name, List<T> tasks) {
        super(name, tasks);
    }

    public SequenceTask(List<T> tasks) {
        super(tasks);
    }

    public SequenceTask(String name) {
        super(name);
    }

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    protected void doExecute() {
        setNumberOfExecutedTasks(0);
        executeNext();
    }


    /**
     * Executes the next task
     */
    private void executeNext() {
        // The state was cancelled to we stop here
        if (getState() == State.CANCELLED) {
            if (LOG) {
                Timber.w( toString() + " ========= CANCELLED ========= " + " took " + elapsedTimeToString());
            }
            SequenceTask.this.onCompleted();
            return;
        }


        // execute the next task if there is next
        if (getNumberOfExecutedTasks() < getTasks().size()) {

            // Update the progress
            updateProgress();

            // get the next task
            T task = getTasks().get(getNumberOfExecutedTasks());

            if (LOG) {
                Timber.w( toString() + " ==== START EX =====> " + task);
            }

            if (task != null) {
                // initialize the task
                task.initialize();
                task.setSuffix("    " + getSuffix());

                // Start the task by subscribing on it
                task.execute(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        incrementNumberOfExecutedTasks();

                        if (LOG) {
                            Timber.w( SequenceTask.this.toString() + " <==== END EX ===== " + task + " took " + task.elapsedTimeToString());
                        }

                        // Send as the next task which is being executed
                        SequenceTask.this.onNext(task);

                        executeNext();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (LOG) {
                            Timber.e( SequenceTask.this.toString() + " onError: " + task + " with message " + e);
                            e.printStackTrace();
                        }

                        if (!SequenceTask.this.isFinishOnError()) {
                            if (LOG) {
                                Timber.w( SequenceTask.this.toString() + " continue on error");
                            }

                            incrementNumberOfExecutedTasks();
                            getFailedTasks().add(task);
                            SequenceTask.this.onNext(task);
                            executeNext();
                        } else {
                            SequenceTask.this.onError(e);
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        if (LOG) {
                            Timber.d( SequenceTask.this.toString() + " pending: " + task);
                        }
                    }
                });
            } else {
                incrementNumberOfExecutedTasks();
                executeNext();
            }

        } else {
            // we finished with all the tasks so we can complete and unsubscibe the subscriber
            if (LOG) {
                Timber.i( toString() + " ========= FINISH ========= " + " took " + elapsedTimeToString());
            }
            SequenceTask.this.onCompleted();
        }


    }


    @Override
    public void cancel() {
        super.cancel();
        for (T task : getTasks()) {
            task.cancel();
        }
    }



    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------

}
