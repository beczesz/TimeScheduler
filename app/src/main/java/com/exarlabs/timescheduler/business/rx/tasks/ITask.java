package com.exarlabs.timescheduler.business.rx.tasks;

import rx.Subscriber;
import rx.Subscription;

/**
 * Common interface for all the tasks
 * Created by becze on 8/3/2017.
 */

public interface ITask<T> {

    /**
     * Executes the task
     *
     * @param subscriber the subscriber to the task which will be notified about the progress
     *
     * @return a subscription to the task being executed
     */
    Subscription execute(Subscriber<T> subscriber);
}
