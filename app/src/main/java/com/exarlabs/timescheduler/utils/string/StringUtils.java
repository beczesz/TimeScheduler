package com.exarlabs.timescheduler.utils.string;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import com.exarlabs.timescheduler.utils.HexUtils;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * String related utility methods
 * Created by becze on 11/1/2017.
 */
public class StringUtils {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static final Charset UTF8_CHARSET = Charset.defaultCharset();
    public static final String BASE_64_RULE = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    /**
     * @param message the message to be encoded
     *
     * @return the enooded from of the message
     */
    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes(UTF8_CHARSET);
            return toBase64FromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @param data the data which should be base 64 encoded
     *
     * @return the enooded from of the message
     */
    public static String toBase64FromByteArray(byte[] data) {
        try {
            String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
            return cleanupBase64(base64Sms);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @param message the encoded message
     *
     * @return the decoded message
     */
    public static String fromBase64(String message) {
        try {
            byte[] data = fromBase64ToArray(message);
            return new String(data, UTF8_CHARSET);
        } catch (Exception e) {
            Timber.e("fromBase64() called with error: message = [" + message + "]");
            e.printStackTrace();
        }

        return null;
    }

    @NonNull
    public static String cleanupBase64(String text) {
        return !TextUtils.isEmpty(text) ? text.replaceAll("\\r\\n|\\r|\\n", "").trim() : "";
    }

    /**
     * @param message the encoded message
     *
     * @return If the message is base64 then return teh decoded form otherwise just the original mesage
     */
    public static String base64ToDevString(String message) {
        try {
            if (isBase64(message)) {
                byte[] data = fromBase64ToArray(message);
                return "(Decoded)" + new String(data, UTF8_CHARSET);
            } else {
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] fromBase64ToArray(String message) {
        return Base64.decode(message, Base64.DEFAULT);
    }

    public static String base64Ofsha256(String text) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes());
        byte[] digest = md.digest();

        return Base64.encodeToString(digest, Base64.DEFAULT);
    }

    /**
     * @param text
     *
     * @return the hex encoded SHA256 has of the string
     *
     * @throws NoSuchAlgorithmException
     */
    public static String sha256(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes());
        byte[] digest = md.digest();

        return HexUtils.toHexString(digest);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param message
     *
     * @return true if a String is base64
     */
    public static boolean isBase64(String message) {
        return !TextUtils.isEmpty(message) && message.replace("\r", "").replace("\n", "").replace(" ", "").trim().matches(BASE_64_RULE);
    }


    public static String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    public static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     *
     * @return true if str is null or zero length
     */
    // TODO mock TextUtils
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    @NonNull
    public static String cleanup(String text) {
        return !TextUtils.isEmpty(text) ? text.replaceAll("\\r\\n|\\r|\\n", " ").trim() : "";
    }


    /**
     * Get the hashCode of a String, insensitive to case, without any new Strings
     * being created on the heap.
     *
     * @param s String input
     *
     * @return int hashCode of input String insensitive to case
     */
    public static int hashCodeIgnoreCase(String s) {
        if (s == null) {
            return 0;
        }
        int hash = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(s.charAt(i));
            hash = 31 * hash + c;
        }
        return hash;
    }

    public static String extractInitial(String text) {
        if (text != null && text.length() > 0) {
            String initial = text.substring(0, 1).toUpperCase(Locale.US);
            if (Pattern.matches("[a-zA-Z]+", initial)) {
                return initial;
            } else {
                return "";
            }
        }
        return "";
    }

    /**
     * Generates a random Alphanumaric string with the given lenght
     *
     * @param length
     *
     * @return
     */
    public static String generateRandom_a_z_A_Z_0_9_String(int length) {
        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        return getRandomString(length, chars);
    }

    /**
     * Generates a random Alphanumaric string with the given lenght
     *
     * @param length
     *
     * @return
     */
    public static String generateRandom_A_Z_0_9_String(int length) {
        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        return getRandomString(length, chars);
    }

    /**
     * Generates a random Alphanumaric string with the given lenght
     *
     * @param length
     *
     * @return
     */
    public static String generateRandom_0_9_String(int length) {
        final char[] chars = "0123456789".toCharArray();
        return getRandomString(length, chars);
    }

    /**
     * Generates a random Alphanumaric string with the given lenght
     *
     * @param length
     *
     * @return
     */
    public static String generateRandom_A_Z_String(int length) {
        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        return getRandomString(length, chars);
    }

    /**
     * @param length the length of the random string
     * @param chars  the allowed chars in the random string
     *
     * @return
     */
    public static String getRandomString(int length, char[] chars) {
        String randomString = "";

        final SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randomString = randomString + chars[random.nextInt(chars.length)];
        }
        return randomString;
    }

    /**
     * @param bundle
     * @param bundleName
     *
     * @return
     */
    public static String bundleToString(Bundle bundle, String bundleName, String padding) {
        if (bundle == null) {
            return bundleName + " is empty";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(padding).append(bundleName).append(":");

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            sb.append("\n    ").append(padding).append(key).append(" -> ");
            sb.append(value != null ? value.toString() : "null");
        }

        return sb.toString();
    }

    /**
     * @param s the string
     *
     * @return the camel case version of s
     */
    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
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
