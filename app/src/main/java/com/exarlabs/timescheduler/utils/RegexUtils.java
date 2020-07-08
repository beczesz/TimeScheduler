package com.exarlabs.timescheduler.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;


import androidx.core.util.PatternsCompat;

public class RegexUtils {
    private static final String TAG = RegexUtils.class.getCanonicalName();

    public static final String REGEX_EXTENSION = "\\.([0-9a-z]+)(?:[\\?#]|$)";
    public static final String REGEX_EMAIL_DOMAIN = "(?<=@)[^.]+(?=\\.)";
    public static final String REGEX_EMAIL= "([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})";
    public static final String REGEX_A_Z_0_9 = "[A-Z0-9]+";
    public static final String REGEXa_z_A_Z_0_9 = "[a-zA-Z0-9]+";
    public static final String REGEX_LINK = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
    public static final String REGEX_LINK_HHTP_HTTPS = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,})";
    public static final String REGEX_LINK_WWW = "(www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";

    public static final Pattern LINK_PATTERN = Pattern.compile(REGEX_LINK);
    public static final Pattern LINK_PATTERN_HTTPS = Pattern.compile(REGEX_LINK);
    public static final Pattern LINK_PATTERN_WWW = Pattern.compile(REGEX_LINK);

    /**
     * @param emailTextToCheck
     *
     * @return true if the supplied email address is valid
     */
    public static boolean validateEmail(String emailTextToCheck) {
        return !TextUtils.isEmpty(emailTextToCheck) && PatternsCompat.EMAIL_ADDRESS.matcher(emailTextToCheck).matches();
    }

    /**
     * @param emailTextToCheck
     *
     * @return true if the supplied email address is valid
     */
    public static boolean validateLink(String emailTextToCheck) {
        return !TextUtils.isEmpty(emailTextToCheck) && LINK_PATTERN.matcher(emailTextToCheck).matches();
    }

    /**
     * @return true if the supplied email address is valid
     */
    public static String getDomain(String email) {
        Pattern pattern = Pattern.compile(REGEX_EMAIL_DOMAIN);
        Matcher matcher = pattern.matcher(email);
        return validateEmail(email) && matcher.find() ? matcher.group() : email;
    }

    /**
     * @param phoneNumber
     *
     * @return true if the given number is a valid phone number
     */
    public static boolean validatePhone(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }


    /**
     * @param string
     *
     * @return true if the string comtains A-Z nd 0-9 characters only
     */
    public static boolean validateWithPattern(String patternString, String string) {
        Pattern pattern = Pattern.compile(patternString);
        return !TextUtils.isEmpty(string) && pattern.matcher(string).matches();
    }

    /**
     * @param string
     *
     * @return true if the string comtains A-Z nd 0-9 characters only
     */
    public static boolean validateWithPattern(Pattern pattern, String string) {
        return !TextUtils.isEmpty(string) && pattern.matcher(string).matches();
    }

    /**
     * @param string
     *
     * @return true if the string comtains A-Z nd 0-9 characters only
     */
    public static boolean validateA_Z_0_9(String string) {
        Pattern pattern = Pattern.compile(REGEX_A_Z_0_9);
        return !TextUtils.isEmpty(string) && pattern.matcher(string).matches();
    }

    /**
     * @param string
     *
     * @return true if the string comtains A-Z nd 0-9 characters only
     */
    public static boolean validatea_zA_Z_0_9(String string) {
        Pattern pattern = Pattern.compile(REGEXa_z_A_Z_0_9);
        return !TextUtils.isEmpty(string) && pattern.matcher(string).matches();
    }

    /**
     * @param fileName
     *
     * @return true if the given number is a valid phone number
     */
    public static String getFileExtension(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            Pattern pattern = Pattern.compile(REGEX_EXTENSION);
            Matcher matcher = pattern.matcher(fileName.toLowerCase());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return "";
    }


    /**
     * @param email
     *
     * @return the username extarcted from the email
     */
    public static String extractUsername(String email) {
        int endIndex = email.lastIndexOf("@");
        return endIndex > 0 ? email.substring(0, endIndex) : email;
    }


    public static String maskString(String strText) {
        try {
            if (!TextUtils.isEmpty(strText)) {
                if (strText.length() > 4) {
                    return maskString(strText, 2, strText.length() - 2, '*', -1);
                } else {
                    return maskString(strText, 0, strText.length(), '*', -1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String maskString(String strText, int start, int end, char maskChar, int maxLength) throws Exception {

        if (strText == null || strText.equals("")) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }

        if (end > strText.length()) {
            end = strText.length();
        }

        if (start > end) {
            throw new Exception("End index cannot be greater than start index");
        }

        int maskableLength = end - start;
        int maskLength = maxLength == -1 ? maskableLength : maxLength;

        if (maskLength == 0) {
            return strText;
        }

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskableLength);
    }


    /**
     * Transforms the email address such that after the first 2 letter from the id it replaces every character with a * symbol.
     * Ex: example@example.com will be exa**le@example.com
     *
     * @param email the email address
     *
     * @return the hidden form of the email or empty string
     */
    public static String emailToHiddenForm(String email) {
        return emailToHiddenForm(email, -1, true);
    }

    /**
     * Transforms the email address such that after the first 2 letter from the id it replaces every character with a * symbol.
     * Ex: example@example.com will be exa**le@example.com
     *
     * @param email         the email address
     * @param maxMaskLength the maxumim number of masked characters. If -1 then it will be no restrictions in the maximum characters
     *
     * @return the hidden form of the email or empty string
     */
    public static String emailToHiddenForm(String email, int maxMaskLength, boolean requireValidEmail) {
        if (validateEmail(email) || !requireValidEmail) {
            String[] items = email.split("@");
            try {
                if (items.length == 2 && items[0].length() > 4) {
                    return maskString(items[0], 2, items[0].length() - 2, '*', maxMaskLength) + "@" + items[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return !TextUtils.isEmpty(email) ? email : "";
    }


    /**
     * Transforms the phone number such that everything will be hidden except the last 4 digits.
     * Ex: 123456789 will be *****6789
     *
     * @param phoneNumber
     *
     * @return the hidden form of the phone number
     */
    public static String phoneToHiddenForm(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.length() > 4) {
                String encryptString = "";
                for (int i = 0; i < phoneNumber.length() - 4; i++) {
                    encryptString = encryptString + "*";
                }
                encryptString = encryptString + phoneNumber.substring(phoneNumber.length() - 4);
                return encryptString;
            } else {
                return phoneNumber;
            }
        }

        return "";
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}
