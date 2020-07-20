package com.exarlabs.timescheduler.utils.device;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.PowerManager;

import timber.log.Timber;

import static android.content.Context.POWER_SERVICE;

/**
 * Handling the system UI
 * <p>
 * Created by atiyka on 2016.06.29..
 */
public class SystemCommands {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    /**
     * Class for showing the navigation bar asynchronously
     */
    private static class ShowSystemNavBar extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object[] objects) {
            try {
                Process su = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

                // android shell command for starting the system UI service
                outputStream.writeBytes("am startservice --user 0 -n com.android.systemui/.SystemUIService\n");
                outputStream.flush();

                outputStream.writeBytes("exit\n");
                outputStream.flush();
                su.waitFor();
            } catch (IOException | InterruptedException e) {
                Timber.e(TAG, e.toString());
                Thread.interrupted();
            }
            return true;
        }
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------
    private static final String TAG = SystemCommands.class.getSimpleName();
    private static long mLastUnlockCommandTime = 0;

    private static final String POCKET_LIB = "libpocketsphinx_jni.so";
    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Copies the running application apk file to the /system/app folder - so makes it system app
     *
     * @param pm          package manager
     * @param packageName package name
     */
    public static void makeItselfSystemApp(PackageManager pm, String packageName) {
        PackageInfo p;
        try {
            p = pm.getPackageInfo(packageName, 0);
            String sourceDir = p.applicationInfo.publicSourceDir;
            Timber.w(TAG, "makeItselfSystemApp: " + sourceDir);
            // if it'sourceDir not a system app
            if (!sourceDir.contains("/system/app")) {
                try {
                    Timber.d(TAG, "Try to make it system app");

                    Process su = Runtime.getRuntime().exec("su");
                    DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

                    // make the system folder writable
                    outputStream.writeBytes("mount -o rw,remount /system\n");
                    outputStream.flush();

                    // copies the lib
                    copyLib(outputStream, packageName);

                    // check if exists in system folder - the running instance is an update
                    outputStream.writeBytes("ls /system/app/Brightswitch.apk\n");
                    outputStream.flush();

                    byte[] data = new byte[28];
                    readInputStreamWithTimeout(su.getInputStream(), data, 3000);

                    String result = new String(data, "UTF-8");

                    // if the app wasn't found in system folder
                    if (!result.contains("Brightswitch")) {
                        // copy to system folder
                        outputStream.writeBytes("cp " + sourceDir + " /system/app/Brightswitch.apk\n");
                        outputStream.flush();

                        // give permission
                        outputStream.writeBytes("chmod 644 /system/app/Brightswitch.apk\n");
                        outputStream.flush();

                        // reboot the device
                        outputStream.writeBytes("reboot\n");
                        outputStream.flush();
                        // here the device will reboot
                    }

                    // exit from shell
                    outputStream.writeBytes("exit\n");
                    outputStream.flush();
                    su.waitFor();
                } catch (IOException | InterruptedException ex) {
                    Timber.e(TAG, ex.toString());
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(TAG, "makeItselfSystemApp: " + e);
        }
    }

    /**
     * Copies the pocketsphinx library to the system libs folder
     *
     * @param packageName package name
     */
    public static void copyPocketsphinxLibraryToSystemLib(String packageName) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            // make the system folder writable
            outputStream.writeBytes("mount -o rw,remount /system\n");
            outputStream.flush();

            copyLib(outputStream, packageName);

            // exit from shell
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();

        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * The actual copy command
     *
     * @param outputStream output stream
     * @param packageName  package name
     */
    private static void copyLib(DataOutputStream outputStream, String packageName) {
        try {
            // copy to system libs folder the pocketsphinx lib
            outputStream.writeBytes("cp /data/data/" + packageName + "/lib/" + POCKET_LIB + " /system/lib/" + POCKET_LIB + "\n");
            outputStream.flush();

            // give permission
            outputStream.writeBytes("chmod 644 /system/lib/" + POCKET_LIB + "\n");
            outputStream.flush();
        } catch (IOException ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * Creates the /data/local.prop with the corresponding content
     */
    public static void createLocalProp() {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            // make the data folder writable
            outputStream.writeBytes("mount -o rw,remount /data\n");
            outputStream.flush();

            // keep in memory the app
            outputStream.writeBytes("print sys.keep_app_1=com.bellatrix.brightswitch > /data/local.prop\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * Reads from an input stream with timeout
     *
     * @param is            input stream
     * @param b             bytes
     * @param timeoutMillis timeout
     *
     * @return the buffer offset
     *
     * @throws IOException
     */
    private static int readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis) throws IOException {
        int bufferOffset = 0;
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < b.length) {
            int readLength = java.lang.Math.min(is.available(), b.length - bufferOffset);
            // can alternatively use bufferedReader, guarded by isReady():
            int readResult = is.read(b, bufferOffset, readLength);
            if (readResult == -1) {
                break;
            }
            bufferOffset += readResult;
        }
        return bufferOffset;
    }

    /**
     * Installs an apk file
     *
     * @param filePath file
     *
     * @return true on success
     */
    public synchronized static boolean installApp(String filePath) {
        try {
            Timber.d(TAG, "installApp");

            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("pm install -r " + filePath + "\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
            return false;
        }
        return true;
    }



    /**
     * Hides the system's navigation bar
     */
    public static void hideSystemUI() {
        try {
            Timber.d(TAG, "hideSystemUI");

            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            // a shell command, which kills the system UI (pid: 42)
            outputStream.writeBytes("service call activity 42 s16 com.android.systemui\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * Shows the system's navigation bar
     */
    public static void showSystemUI() {
        Timber.d(TAG, "showSystemUI");
        new ShowSystemNavBar().execute("");
    }

    /**
     * Reboots the android
     */
    public static void rebootAndroid() {
        Timber.d(TAG, "Starting reboot");
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
            process.waitFor();
        } catch (Exception ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * @return true if the device is rooted
     */
    public static boolean isDeviceRooted() {
        boolean ret = true;
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
            ret = false;
        }
        return ret;
    }

    /**
     * Lock the screen if it's not locked
     */
    public static void lockScreen() {
        Timber.d(TAG, "lockScreen");
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            // RES variable will contain the row with the given value - screen is on
            outputStream.writeBytes("RES=`dumpsys input_method | grep mScreenOn=true`\n");
            // if the string length is > 0 then screen is on
            outputStream.writeBytes("if [ ${#RES} -gt 0 ]; then\n");
            // a shell command, which locks the screen
            outputStream.writeBytes("input keyevent 26\n");
            outputStream.writeBytes("fi\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * Unlocks the screen is it's locked
     */
    public static synchronized void unlockScreen() {
        Timber.d(TAG, "unlockScreen");
        if (System.currentTimeMillis() - mLastUnlockCommandTime < 5000) {
            Timber.w(TAG, "unlockScreen: prevent.");
            return;
        }
        mLastUnlockCommandTime = System.currentTimeMillis();

        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            // RES variable will contain the row with the given value - screen is off
            outputStream.writeBytes("RES=`dumpsys input_method | grep mScreenOn=false`\n");
            // if the string length is > 0 then screen is off
            outputStream.writeBytes("if [ ${#RES} -gt 0 ]; then\n");
            // so we can give the unlock command
            outputStream.writeBytes("input keyevent 26\n");
            outputStream.writeBytes("fi\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        } catch (IOException | InterruptedException ex) {
            Timber.e(TAG, ex.toString());
        }
    }

    /**
     * Checks from the power manager if the screen is locked and then unlocks
     *
     * @param context context
     */
    public static void unlockScreenWithInstantCheck(Context context) {
        Timber.w(TAG, "unlockScreenWithInstantCheck");
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (!powerManager.isScreenOn()) {
            unlockScreen();
        }
    }

    /**
     * Disables showing the recents
     *
     * @param context context
     * @param taskId  task id
     */
    @SuppressWarnings ("unused - reserved for later development")
    public static void disableRecents(Context context, int taskId) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(taskId, 0);
    }
    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
