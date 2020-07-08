package com.exarlabs.timescheduler.business.rx.tasks;

import androidx.annotation.NonNull;

/**
 * Describes the progress of certain task
 */
public class Progress {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    /**
     *  Listsner interface for progress updates
     */
    public interface ProgressUpdateListener {

        /**
         * Callback when the progress is updated
         *
         * @param progress
         */
        void onProgressUpdate(Progress progress, String note);
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * Combines the two progresses taking into the consideration the weights resulting in a new progress
     *
     * @param p1
     * @param p2
     *
     * @return
     */
    public static Progress combine(Progress p1, Progress p2) {
        Progress progress = new Progress();
        progress.mWeight = p1.mWeight + p2.mWeight;
        progress.mPercentage = progress.mWeight > 0 ? (p1.mPercentage * p1.mWeight + p2.mPercentage * p2.mWeight) / progress.mWeight : 0;

        return progress;
    }


    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /**
     * The percentage of the progress
     */
    private float mPercentage;

    /**
     * Weight represents the lenght of the progress. The more weight we attribute to a given progress the more likely is that it will be slower
     * to complete
     */
    private int mWeight;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    public Progress() {
        mPercentage = 0;
        mWeight = 0;
    }

    public Progress(float percentage, int weight) {
        this();
        mPercentage = percentage;
        mWeight = weight;
    }

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    public void combine(Progress progress) {
        Progress combine = combine(this, progress);
        setWeight(combine.getWeight());
        setPercentage(combine.getPercentage());
    }

    public Progress cloneProgress() {
        return new Progress(getPercentage(), getWeight());
    }
    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------



    @Override
    public String toString() {
        return "w: " + getWeight() + " " + getFormattedPercentage();
    }

    @NonNull
    public String getFormattedPercentage() {
        return String.format("%.4f", getPercentage() * 100) + "%";
    }

    @NonNull
    public String getEstimatedTimeLeft() {
        long durationInMillis = (long) (getWeight() * (1 - getPercentage()));

        long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
        return time;
    }

    public float getPercentage() {
        return mPercentage;
    }

    public void setPercentage(float percentage) {
        mPercentage = percentage;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }
}
