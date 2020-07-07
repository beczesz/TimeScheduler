package com.exarlabs.timescheduler.ui.base;

import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.exarlabs.timescheduler.R;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.LayoutInflaterCompat;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Jordi on 5/18/17.
 */
public class BaseActivity extends AppCompatActivity  {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    /**
     * Flag indicating id any of the base activities are created
     */
    private static boolean mIsVisible;


    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    public static boolean isVisible() {
        return mIsVisible;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    private ProgressDialog mProgressDialog;
    private boolean mInForeground;

    private Toolbar mToolbar;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Needed for iconics
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        Timber.d("onCreate: " + this.toString());
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Explode());
            getWindow().setEnterTransition(new Explode());
        }

    }

    @Override
    protected void onResume() {
        Timber.d("onResume: " + this.toString());
        mIsVisible = true;
        mInForeground = true;

        super.onResume();
    }


    /**
     * Callback when the user settings is updated
     */
    protected void onUserSettingsUpdated() {
        // do nothing
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.d("onNewIntent() called with: intent = [" + intent + "]");
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        Timber.d("onStart: " + this.toString());
        super.onStart();
    }

    @Override
    public void onPause() {
        Timber.d("onPause: ");
        super.onPause();
        mIsVisible = false;
        mInForeground = false;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        Timber.d("onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy: ");
    }


    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mToolbar != null) {
            mToolbar.setTitle(""); // Explicitly set this to be emoty we have an other title
            TextView titleTV = mToolbar.findViewById(R.id.title);
            titleTV.setText(getTitle());
        }
    }



    protected TextView getTitleTextView() {
        if (mToolbar != null) {
            return mToolbar.findViewById(R.id.title);
        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        // Make the butterknife binding and the typeface initialization
        ButterKnife.bind(this);
    }


    /**
     * Shows/ hides the actionbar
     *
     * @param show
     */
    protected void showActionBar(boolean show) {
        if (getSupportActionBar() != null) {
            if (show) {
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        }
    }

    /**
     * Shows the progress dialog
     *
     * @param message the message which should be shown
     */
    public void showLoading(String message) {
        try {
            runOnUiThread(() -> {
                Timber.d("showLoading: " + message + " on activity " + this);
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(new ContextThemeWrapper(this, R.style.Theme_TimeScheduler), "", message, true);
                } else {
                    mProgressDialog.setMessage(message);
                }

                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the progress dialog without any text
     */
    public void showLoading(int res) {
        showLoading(getString(res));
    }

    /**
     * Dismisses the progress dialog
     */
    public void dismissLoading() {
        Timber.d("dismissLoading: ");
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }





    /**
     * Release memory when the UI becomes hidden or when system resources become low.
     *
     * @param level the memory-related event that was raised.
     */
    public void onTrimMemory(int level) {

        Timber.d("onTrimMemory() called with: level = [" + level + "]");

        // Determine which lifecycle or system event was raised.
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    /**
     * Unlocks the screen orientation
     */
    public void unlockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * Locks the screen orientation
     */
    public void lockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }




    /**
     * Callback when the network is connected
     */
    public void onNetworkConnected() {
        Timber.d("onNetworkConnected: ");
    }


    /**
     * Displays a snackbar if there is not Internet connection
     */
    public void showNoInternetConnection() {
        // Do nothing on this level
    }

    /**
     * Displays a snackbar if there is not Internet connection
     */
    public void hideNoInternetConnection() {
        // Do nothing on this level
    }

    /**
     * Callback when the network is connected
     */
    public void onNetworkNotConnected() {
        Timber.d("onNetworkNotConnected: ");
        showNoInternetConnection();
    }





    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
