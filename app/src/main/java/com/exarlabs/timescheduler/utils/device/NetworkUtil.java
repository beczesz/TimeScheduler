package com.bluerisc.eprivo.utils.device;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

public class NetworkUtil {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    public static class Ping {
        public boolean isConnected = false;
        public String net = "NO_CONNECTION";
        public String host = "";
        public String ip = "";
        public int dnsResolvetime = Integer.MAX_VALUE;
        public int pingTime = Integer.MAX_VALUE;
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    public static Ping ping(URL url, Context ctx) {
        Ping r = new Ping();
        if (isNetworkConnected(ctx)) {
            r.net = getNetworkType(ctx);
            try {
                String hostAddress;
                long start = System.currentTimeMillis();
                hostAddress = InetAddress.getByName(url.getHost()).getHostAddress();
                long dnsResolved = System.currentTimeMillis();
                Socket socket = new Socket(hostAddress, url.getPort());
                socket.close();
                long probeFinish = System.currentTimeMillis();
                r.dnsResolvetime = (int) (dnsResolved - start);
                r.pingTime = (int) (probeFinish - dnsResolved);
                r.host = url.getHost();
                r.ip = hostAddress;
                r.isConnected = true;
            }
            catch (Exception ex) {
                Timber.e("Unable to ping");
            }
        }
        return r;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
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
