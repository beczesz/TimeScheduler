package com.bluerisc.eprivo.utils.arrays;

import java.util.List;

import android.text.TextUtils;

/**
 * Created by becze on 5/9/2016.
 */
public class ArrayUtil {

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

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Checks if given array is null or has zero elements.
     */
    public static boolean isEmpty( byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Concatenates two arrays and returns the result.
     *
     * @param a
     * @param b
     *
     * @return the array [a|b]
     */
    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    /**
     * Concatanates multiple arrays into one array.
     *
     * @param arrays
     *
     * @return
     */
    public static byte[] concat(byte[]... arrays) {
        byte[] result = new byte[0];
        for (byte[] array : arrays) {
            result = concat(result, array);
        }

        return result;
    }

    /**
     * Replaces all the occurences of the byteToRemove with byteToReplaceWith in the source array
     *
     * @param souce
     * @param byteToRemove
     * @param byteToReplaceWith
     *
     * @return
     */
    public static byte[] replaceAllOccurences(byte[] souce, byte byteToRemove, byte byteToReplaceWith) {
        for (int i = 0; i < souce.length; i++) {
            if (souce[i] == byteToRemove) {
                souce[i] = byteToReplaceWith;
            }
        }

        return souce;
    }

    /**
     * Replaces the first occurence of the given byte with another byte in the source array.
     * Note: no modification is made when the byteToReplaced is not found.
     *
     * @param souce
     * @param byteToReplaced
     * @param byteToReplaceWith
     *
     * @return
     */
    public static byte[] replaceFirstOccurenceOf(byte[] souce, byte byteToReplaced, byte byteToReplaceWith) {
        for (int i = 0; i < souce.length; i++) {
            if (souce[i] == byteToReplaced) {
                souce[i] = byteToReplaceWith;
                break;
            }
        }

        return souce;
    }


    /**
     * Returns the index of the element in that array
     *
     * @param needle
     * @param haystack
     * @param <T>
     *
     * @return
     */
    public static <T> int indexOf(T needle, T[] haystack) {
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i] != null && haystack[i].equals(needle) || needle == null && haystack[i] == null) {
                return i;
            }
        }

        return -1;
    }


    /**
     * @param key
     * @param array
     *
     * @return
     */
    public static int indexOf(int key, int[] array) {
        int returnvalue = -1;
        for (int i = 0; i < array.length; ++i) {
            if (key == array[i]) {
                returnvalue = i;
                break;
            }
        }
        return returnvalue;
    }

    /**
     * @param key
     * @param array
     *
     * @return
     */
    public static int indexOf(String key, String[] array) {
        int returnvalue = -1;
        if (!TextUtils.isEmpty(key)) {
            for (int i = 0; i < array.length; ++i) {
                if (key.equals(array[i])) {
                    returnvalue = i;
                    break;
                }
            }
        }
        return returnvalue;
    }

    /**
     * Converts the arguments to an array of T
     *
     * @param values
     *
     * @return the array of integersD
     */
    public static <T> T[] toObjectArray(T... values) {
        return values;
    }

    public static String[] toArray(List<String> folders) {
        if (folders == null) {
            return null;
        }

        return folders.toArray(new String[folders.size()]);
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------
}
