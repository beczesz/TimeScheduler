package com.exarlabs.timescheduler.utils;

import java.util.List;

/**
 * Contains utility function to work with hex numebrs and arrays
 * Created by becze on 11/7/2016.
 */
public class HexUtils {

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
     * Pretty prints a byte array with multiple options
     *
     * @param array
     *
     * @return
     */
    public static String prettyPrint(byte[] array, String title, boolean includeTitle, boolean includeIndexes, boolean includeHex,
                                     boolean includeBinary, boolean includeDec) {

        int minimumLenght = 2;
        if (includeHex) {
            minimumLenght = 2;
        }
        if (includeDec) {
            minimumLenght = 3;
        }
        if (includeBinary) {
            minimumLenght = 8;
        }


        // Start block
        String arrayToString = "--------------------------------------------------------------------------------";

        if (includeTitle) {
            arrayToString += "\n  " + title;
        }

        if (includeIndexes) {
            arrayToString += "\n IND: ";
            for (int i = 0; i < array.length; i++) {
                String index = String.format("%" + minimumLenght + "d", i) + close();
                arrayToString += index;
            }
        }

        if (includeHex) {
            arrayToString += "\n HEX: ";
            for (int i = 0; i < array.length; i++) {
                String index = toHEXString(array[i], minimumLenght) + close();
                arrayToString += index;
            }
        }

        if (includeDec) {
            arrayToString += "\n DEC: ";
            for (int i = 0; i < array.length; i++) {
                String index = String.format("%" + minimumLenght + "d", array[i]) + close();
                arrayToString += index;
            }
        }

        if (includeBinary) {
            arrayToString += "\n BIN: ";
            for (int i = 0; i < array.length; i++) {
                String index = String.format("%" + minimumLenght + "s", Integer.toBinaryString(array[i] & 0xFF)).replace(' ', '0') + close();
                arrayToString += index;
            }
        }

        // end block
        arrayToString += "\n--------------------------------------------------------------------------------";
        return arrayToString;
    }

    /**
     * Pretty prints a byte array with multiple options
     *
     * @param array
     *
     * @return
     */
    public static String prettyPrint(List<byte[]> array, String title, boolean includeTitle, boolean includeIndexes, boolean includeHex) {

        int minimumLenght = 2;

        // Start block
        String arrayToString = "--------------------------------------------------------------------------------";

        if (includeTitle) {
            arrayToString += "\n  " + title;
        }

        if (includeIndexes) {
            arrayToString += "\n IND: ";
            for (int i = 0; i < 20; i++) {
                String index = String.format("%" + minimumLenght + "d", i) + close();
                arrayToString += index;
            }
        }

        for (byte[] packet : array) {
            if (includeHex) {
                arrayToString += "\n HEX: ";
                for (int i = 0; i < packet.length; i++) {
                    String index = toHEXString(packet[i], minimumLenght) + close();
                    arrayToString += index;
                }
            }
        }


        // end block
        arrayToString += "\n--------------------------------------------------------------------------------";
        return arrayToString;
    }


    public static String toHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }


    /**
     * @param value
     * @param length
     *
     * @return the HEX value of the byte to String
     */
    public static String toHEXString(byte value, int length) {
        return String.format("%" + length + "x", value);
    }

    /**
     * @param value
     * @param length
     *
     * @return the HEX value of the byte to String
     */
    public static String toHEXString(int value, int length) {
        return String.format("%" + length + "x", value);
    }


    /**
     * Helper method to close a section inside a row
     *
     * @return
     */
    private static String close() {
        return " | ";
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
