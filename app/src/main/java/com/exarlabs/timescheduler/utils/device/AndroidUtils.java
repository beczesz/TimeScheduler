package com.exarlabs.timescheduler.utils.device;

import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Implements some common telephony utility functions
 * Created by becze on 10/30/2017.
 */

public class AndroidUtils {

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
     * @param context
     *
     * @return true if the device has telephony feature: call, sms etc.
     */
    public static boolean hasTelephonyFeature(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }


    /**
     * Hides the soft keyboard no matter what :)
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Return the country code of the current device extracted from the locale
     *
     * @param context
     *
     * @return
     */
    public static String getCountryCode(Context context) {
        return context.getResources().getConfiguration().locale.getCountry();
    }

    /**
     * Return the country name of the current device extracted from the locale
     *
     * @param context
     *
     * @return
     */
    public static String getCountryName(Context context) {
        return context.getResources().getConfiguration().locale.getDisplayName();
    }

    public static String getTimezoneId() {
        return TimeZone.getDefault().getID();
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
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------

}
