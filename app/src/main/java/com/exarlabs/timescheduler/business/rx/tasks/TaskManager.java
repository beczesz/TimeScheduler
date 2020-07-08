package com.exarlabs.timescheduler.business.rx.tasks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;

/**
 * Task manager is a helper factory class to create and run a series of tasks.
 * Created by becze on 11/28/2015.
 */
public class TaskManager {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * Executes a sequence of tasks.
     *
     * @param tasks
     * @param callback
     */
    public static <T> SequenceTask execute(final List<? extends Task<T>> tasks, final Subscriber<Task<T>> callback) {
        SequenceTask executor = new SequenceTask(tasks);
        executor.execute(callback);
        return executor;
    }

    public static <T> void execute(Task<T> task, final Subscriber<Task<T>> callback) {
        execute(Collections.singletonList(task), callback);
    }

    public static <T> void execute(Task<T> task0, Task<T> task1, final Subscriber<Task<T>> callback) {
        execute(Arrays.asList(task0, task1), callback);
    }

    public static <T> void execute(Task<T> task0, Task<T> task1, Task<T> task2, final Subscriber<Task<T>> callback) {
        execute(Arrays.asList(task0, task1, task2), callback);
    }

    public static <T> void execute(Task<T> task0, Task<T> task1, Task<T> task2, Task<T> task3, final Subscriber<Task<T>> callback) {
        execute(Arrays.asList(task0, task1, task2, task3), callback);
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

    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------
}
