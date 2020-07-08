package com.bluerisc.eprivo.utils.device;

import java.util.UUID;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


/**
 * Contains utility methods releted with the device security (retrieves unique ID or pseudo unitque ID)
 * Created by becze on 6/3/2016.
 */
public class DeviceSecurity {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static final String TAG = DeviceSecurity.class.getSimpleName();


    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    private static final Pattern MAC_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    private static final Pattern IP_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------


    @SuppressWarnings("deprecation")
    @SuppressLint ("HardwareIds")
    public static String generateDeviceIdentifier(Context context) {

        try {
            String pseudoId = "35" +
                            Build.BOARD.length() % 10 +
                            Build.BRAND.length() % 10 +
                            Build.CPU_ABI.length() % 10 +
                            Build.DEVICE.length() % 10 +
                            Build.DISPLAY.length() % 10 +
                            Build.HOST.length() % 10 +
                            Build.ID.length() % 10 +
                            Build.MANUFACTURER.length() % 10 +
                            Build.MODEL.length() % 10 +
                            Build.PRODUCT.length() % 10 +
                            Build.TAGS.length() % 10 +
                            Build.TYPE.length() % 10 +
                            Build.USER.length() % 10;

            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String longId = "RandomSalt:2190312839" + pseudoId + androidId;

            return UUID.nameUUIDFromBytes(longId.getBytes()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getUniquePseudoID();
    }

    /**
     * Return pseudo unique ID
     *
     * @return ID
     */
    public static String getUniquePseudoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use SWITCH_SETUP, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) +
                        (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) +
                        (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * @param mac
     *
     * @return true if the given string can be a valid MAC address
     */
    public static boolean validateMAC(final String mac) {
        if (TextUtils.isEmpty(mac)) {
            return false;
        }
        return MAC_PATTERN.matcher(mac).matches();
    }


    /**
     * @param ip String version of the IP address Ex 192.168.1.6
     *
     * @return true if the IP address has a valid format
     */
    public static boolean validateIPv4(final String ip) {
        if (TextUtils.isEmpty(ip)) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches();
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        //        String phrase = "";
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                //                phrase += Character.toUpperCase(c);
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            //            phrase += c;
            phrase.append(c);
        }

        return phrase.toString();
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
