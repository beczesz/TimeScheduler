package com.bluerisc.eprivo.business.rx;

import rx.Subscriber;

/**
 * This Observer just adds dummy implementation for the traditional observer callbacks
 * and forwards the result to one method
 * Created by becze on 11/27/2015.
 */
public abstract class CallbackSubscriber<T> extends Subscriber<T> {

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

    /**
     * Flag marking whether this subscruber is already completed
     */
    private boolean mIsCompleted;
    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Returns the result if it was successful, otherwise an error.
     *
     * @param result the result of the call or null if there was an error.
     * @param e On success it will be null, otherwise the error.
     */
    abstract public void onResult(T result, Throwable e);

    @Override
    public void onCompleted() {
        doCallOnResult(null, null);
    }

    private void doCallOnResult(T result, Throwable e) {
        if (!mIsCompleted) {
            onResult(result, e);
            mIsCompleted = true;
        }
    }

    @Override
    public void onError(Throwable e) {
        onResult(null, e);
    }

    @Override
    public void onNext(T o) {
        doCallOnResult(o, null);
        onCompleted();
    }


    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------
}
