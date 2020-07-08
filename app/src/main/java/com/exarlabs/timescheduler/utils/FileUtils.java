package com.exarlabs.timescheduler.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.exarlabs.timescheduler.AppConstant;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * Contains utility methods related with file operations.
 */

public class FileUtils {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    /**
     * File Detail.
     * <p>
     * 1. Model used to hold file details.
     */
    public static class FileDetail {

        // fileSize.
        public String fileName;

        // fileSize in bytes.
        public long fileSize;

        /**
         * Constructor.
         */
        public FileDetail() {

        }
    }

    /**
     * File type
     */
    public enum FileType {

        //@formatter:off
        EPRIVO("application/eprivo"),
        IMAGE("image/"),
        AUDIO("audio/"),
        VIDEO("video/"),
        POWER_POINT("application/vnd.ms-powerpoint"),
        EXCEL("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        WORD("application/msword"),
        PDF("application/pdf"),
        CODE("text/html", "text/css", "text/csv", "json","js"),
        TEXT("text/"),
        ARCHIVE("application/zip", "application/x-tar"),
        BINARY(""),
        ;
        //@formatter:on

        /**
         * @param mimeType
         *
         * @return the inferred file type based on the mime type
         */
        public static FileType fromMime(String mimeType) {
            if (!TextUtils.isEmpty(mimeType)) {
                for (FileType fileType : values()) {
                    for (String rule : fileType.getMimeMatchRules()) {
                        if (mimeType.contains(rule)) {
                            return fileType;
                        }
                    }
                }
                return FileType.BINARY;
            }

            return null;
        }


        FileType(String... mimeMatchRule) {
            mMimeMatchRules = mimeMatchRule;
        }

        /**
         * The MIME match rule for this file type
         */
        private String[] mMimeMatchRules;

        public String[] getMimeMatchRules() {
            return mMimeMatchRules;
        }

        public String getRule() {
            return mMimeMatchRules[0];
        }
    }

    public enum ExecutableExtensions {
        //@formatter:off
        _0XE       ("0XE", "F-Secure Renamed Virus File"),
        _73K       ("73K", "TI-73 Application"),
        _89K       ("89K", "TI-89 Application"),
        _8CK       ("8CK", "TI-84 Plus C Silver Edition Application File"),
        A6P        ("A6P", "Authorware 6 Program"),
        A7R        ("A7R", "Authorware 7 Runtime File"),
        AC         ("AC", "Autoconf Script"),
        ACC        ("ACC", "GEM Accessory File"),
        ACR        ("ACR", "ACRobot Script"),
        ACTC       ("ACTC", "Action(s) Collection File"),
        ACTION     ("ACTION", "Automator Action"),
        ACTM       ("ACTM", "AutoCAD Action Macro File"),
        AHK        ("AHK", "AutoHotkey Script"),
        AIR        ("AIR", "Adobe AIR Installation Package"),
        APK        ("APK", "Android Package File"),
        APP        ("APP", "Symbian OS Application"),
        APPLESCRIPT("APPLESCRIPT", "AppleScript File"),
        ARSCRIPT   ("ARSCRIPT", "ArtRage Script"),
        ASB        ("ASB", "Alphacam Stone VB Macro File"),
        AZW2       ("AZW2", "Kindle Active Content App File"),
        BA_        ("BA_", "Renamed BAT File"),
        BAT        ("BAT", "DOS Batch File"),
        BEAM       ("BEAM", "Compiled Erlang File"),
        BIN        ("BIN", "Unix Executable File"),
        BTM        ("BTM", "4DOS Batch File"),
        CACTION    ("CACTION", "Automator Converter Action"),
        CEL        ("CEL", "Celestia Script File"),
        CELX       ("CELX", "Celestia Script"),
        CGI        ("CGI", "Common Gateway Interface Script"),
        CMD        ("CMD", "Windows Command File"),
        COF        ("COF", "MPLAB COFF File"),
        COFFEE     ("COFFEE", "CoffeeScript JavaScript File"),
        COM        ("COM", "DOS Command File"),
        COMMAND    ("COMMAND", "Terminal Command File"),
        CSH        ("CSH", "C Shell Script"),
        CYW        ("CYW", "Rbot.CYW Worm File"),
        DEK        ("DEK", "Eavesdropper Batch File"),
        DLD        ("DLD", "EdLog Compiled Program"),
        DMC        ("DMC", "Medical Manager Script"),
        DS         ("DS", "TWAIN Data Source"),
        DXL        ("DXL", "Rational DOORS Script"),
        E_E        ("E_E", "Renamed EXE File"),
        EAR        ("EAR", "Java Enterprise Archive File"),
        EBM        ("EBM", "EXTRA! Basic Macro"),
        EBS        ("EBS", "E-Run 1.x Script"),
        EBS2       ("EBS2", "E-Run 2.0 Script File"),
        ECF        ("ECF", "SageCRM Component File"),
        EHAM       ("EHAM", "ExtraHAM Executable File"),
        ELF        ("ELF", "Nintendo Wii Game File"),
        EPK        ("EPK", "LG Firmware Package"),
        ES         ("ES", "SageCRM Script File"),
        ESH        ("ESH", "Extended Shell Batch File"),
        EX4        ("EX4", "MetaTrader 4 Program File"),
        EX5        ("EX5", "MetaTrader 5 Program File"),
        EX_        ("EX_", "Compressed Executable File"),
        EXE        ("EXE", "PortableApps.com Application"),
        EXE1       ("EXE1", "Renamed EXE File"),
        EXOPC      ("EXOPC", "ExoPC Application"),
        EZS        ("EZS", "EZ-R Batch Script"),
        EZT        ("EZT", "EZT Malicious Worm File"),
        FAS        ("FAS", "Compiled Fast-Load AutoLISP File"),
        FKY        ("FKY", "FoxPro Macro"),
        FPI        ("FPI", "FPS Creator Intelligence Script"),
        FRS        ("FRS", "Flash Renamer Script"),
        FXP        ("FXP", "FoxPro Compiled Program"),
        GADGET     ("GADGET", "Windows Gadget"),
        GPE        ("GPE", "GP2X Video Game"),
        GPU        ("GPU", "GP2X Utility Program"),
        GS         ("GS", "Geosoft Script"),
        HAM        ("HAM", "HAM Executable File"),
        HMS        ("HMS", "HostMonitor android.renderscript.Script File"),
        HPF        ("HPF", "HP9100A Program File"),
        HTA        ("HTA", "HTML Application"),
        ICD        ("ICD", "SafeDisc Encrypted Program"),
        IIM        ("IIM", "iMacro Macro File"),
        IPA        ("IPA", "iOS Application"),
        IPF        ("IPF", "SMS Installer Script"),
        ISU        ("ISU", "InstallShield Uninstaller Script"),
        ITA        ("ITA", "VTech InnoTab Application File"),
        JAR        ("JAR", "Java Archive File"),
        JS         ("JS", "JScript Executable Script"),
        JSE        ("JSE", "JScript Encoded File"),
        JSF        ("JSF", "Java Script Command File"),
        JSX        ("JSX", "ExtendScript Script File"),
        KIX        ("KIX", "KiXtart Script File"),
        KSH        ("KSH", "Unix Korn Shell Script"),
        KX         ("KX", "KiXtart Tokenized Script File"),
        LO         ("LO", "Interleaf Compiled Lisp File"),
        LS         ("LS", "LightWave LScript File"),
        M3G        ("M3G", "Mobile 3D Graphics Program"),
        MAC        ("MAC", "Application Macro File"),
        MAM        ("MAM", "Microsoft Access Macro"),
        MCR        ("MCR", "3ds Max Macroscript File"),
        MEL        ("MEL", "Maya Embedded Language File"),
        MEM        ("MEM", "Macro Editor Macro"),
        MIO        ("MIO", "MioEngine Application File"),
        MLX        ("MLX", "MATLAB Live Script"),
        MM         ("MM", "NeXtMidas Macro File"),
        MPX        ("MPX", "FoxPro Compiled Menu Program"),
        MRC        ("MRC", "mIRC Script File"),
        MRP        ("MRP", "Mobile Application File"),
        MS         ("MS", "3ds Max Script File"),
        MSL        ("MSL", "Magick Scripting Language File"),
        MXE        ("MXE", "Macro Express Playable Macro"),
        N          ("N", "Neko Bytecode File"),
        NCL        ("NCL", "NirCmd Script File"),
        NEXE       ("NEXE", "Chrome Native Client Executable"),
        ORE        ("ORE", "Ore Executable File"),
        OSX        ("OSX", "PowerPC Executable File"),
        OTM        ("OTM", "Outlook Macro File"),
        OUT        ("OUT", "Compiled java.lang.reflect.Executable File"),
        PAF        ("PAF", "Portable Application Installer File"),
        PAF_EXE    ("PAF.EXE", "PortableApps.com Program File"),
        PEX        ("PEX", "ProBoard Executable File"),
        PHAR       ("PHAR", "PHP Archive"),
        PIF        ("PIF", "Program Information File"),
        PLSC       ("PLSC", "Messenger Plus! Live Script File"),
        PLX        ("PLX", "Perl Executable File"),
        PRC        ("PRC", "Palm Resource Code File"),
        PRG        ("PRG", "GEM Application"),
        PS1        ("PS1", "Windows PowerShell Cmdlet File"),
        PVD        ("PVD", "Instalit Script"),
        PWC        ("PWC", "PictureTaker File"),
        PYC        ("PYC", "Python Compiled File"),
        PYO        ("PYO", "Python Optimized Code"),
        QIT        ("QIT", "QIT Trojan Horse File"),
        QPX        ("QPX", "FoxPro Compiled Query Program"),
        RBF        ("RBF", "LEGO MINDSTORMS EV3 Robot Brick File"),
        RBX        ("RBX", "Rembo-C Compiled Script"),
        RFU        ("RFU", "Remote Firmware Update"),
        RGS        ("RGS", "Registry Script"),
        ROX        ("ROX", "Actuate Report Object Executable File"),
        RPJ        ("RPJ", "Real Pac Batch Job File"),
        RUN        ("RUN", "Linux Executable File"),
        RXE        ("RXE", "Lego Mindstorms NXT Executable Program"),
        S2A        ("S2A", "SEAL2 android.app.Application"),
        SBS        ("SBS", "SPSS Script"),
        SCA        ("SCA", "Scala Script File"),
        SCAR       ("SCAR", "SCAR Script"),
        SCB        ("SCB", "Scala Published Script"),
        SCPT       ("SCPT", "AppleScript Script File"),
        SCPTD      ("SCPTD", "AppleScript Script Bundle"),
        SCR        ("SCR", "Script File"),
        SCRIPT     ("SCRIPT", "Generic Script File"),
        SCT        ("SCT", "Windows Scriptlet"),
        SEED       ("SEED", "Linux Preseed File"),
        SERVER     ("SERVER", "MySQL Server Script"),
        SHB        ("SHB", "Windows Document Shortcut"),
        SMM        ("SMM", "Ami Pro Macro"),
        SPR        ("SPR", "FoxPro Generated Screen File"),
        TCP        ("TCP", "Tally Compiled Program File"),
        THM        ("THM", "Thermwood Macro File"),
        TIAPP      ("TIAPP", "TiTanium App"),
        TMS        ("TMS", "Telemate Script"),
        U3P        ("U3P", "U3 Smart Application"),
        UDF        ("UDF", "Excel User Defined Function"),
        UPX        ("UPX", "Ultimate Packer for eXecutables File"),
        VBE        ("VBE", "VBScript Encoded Script File"),
        VBS        ("VBS", "VBScript File"),
        VBSCRIPT   ("VBSCRIPT", "Visual Basic Script"),
        VDO        ("VDO", "Heathen Virus File"),
        VEXE       ("VEXE", "Virus Executable File"),
        VLX        ("VLX", "Compiled AutoLISP File"),
        VPM        ("VPM", "Vox Proxy Macro File"),
        VXP        ("VXP", "Mobile Application File"),
        WCM        ("WCM", "WordPerfect Macro"),
        WIDGET     ("WIDGET", "Yahoo! Widget"),
        WIZ        ("WIZ", "Microsoft Wizard File"),
        WORKFLOW   ("WORKFLOW", "Automator Workflow"),
        WPK        ("WPK", "WordPerfect Macro"),
        WPM        ("WPM", "WordPerfect Macro File"),
        WS         ("WS", "Windows Script"),
        WSF        ("WSF", "Windows Script File"),
        WSH        ("WSH", "Windows Script Host Settings"),
        X86        ("X86", "Linux java.lang.reflect.Executable File"),
        XAP        ("XAP", "Silverlight android.app.Application Package"),
        XBAP       ("XBAP", "XAML Browser Application File"),
        XLM        ("XLM", "Excel Macro"),
        XQT        ("XQT", "SuperCalc Macro File"),
        XYS        ("XYS", "XYplorer android.renderscript.Script File"),
        ZL9        ("ZL9", "ZoneAlarm Quarantined EXE File"),
        ;
        //@formatter:on

        public static boolean isExecutableExtansion(String extension) {
            return fromExtansion(extension) != null;
        }

        public static ExecutableExtensions fromExtansion(String extension) {
            if (TextUtils.isEmpty(extension)) {
                return null;
            }

            for (ExecutableExtensions executableExtension : values()) {

                if (extension.toUpperCase().equals(executableExtension.mExtension)) {
                    return executableExtension;
                }
            }

            return null;
        }

        private final String mExtension;
        private final String mDescription;

        ExecutableExtensions(String extension, String Description) {
            mExtension = extension;
            mDescription = Description;
        }

        public String getExtension() {
            return mExtension;
        }

        public String getDescription() {
            return mDescription;
        }
    }

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The file copy buffer size (30 MB)
     */
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    /**
     * An empty array of type <code>File</code>.
     */
    public static final File[] EMPTY_FILE_ARRAY = new File[0];

    /**
     * The UTF-8 character set, used to decode octets in URLs.
     */
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String TAG = FileUtils.class.getSimpleName();
    public static final String EPRIVO_TEMP_FILE_SUFFIX = ".eptemp";

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------


    /**
     * Convert the specified string to an input stream, encoded as bytes
     * using the default character encoding of the platform.
     *
     * @param input the string to convert
     *
     * @return an input stream
     *
     * @since 1.1
     */
    public static InputStream toInputStream(String input) {
        return toInputStream(input, UTF8);
    }

    /**
     * Convert the specified string to an input stream, encoded as bytes
     * using the specified character encoding.
     *
     * @param input    the string to convert
     * @param encoding the encoding to use, null means platform default
     *
     * @return an input stream
     *
     * @since 2.3
     */
    public static InputStream toInputStream(String input, Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(encoding));
    }

    /**
     * Saves the raw data in a given file on the external storage.
     *
     * @param filePath  the file path on the storage
     * @param fileName  the file name
     * @param fileBytes the file content
     *
     * @return the full path to the file after a successful save. Empty string otherwise
     */
    public static String saveFileDataToExternal(Context context, String filePath, String fileName, byte[] fileBytes) {
        String dir = createFolder(getExternalFilesDir(context, filePath).getPath());
        return saveFileDataToExternal(dir + "/" + fileName, fileBytes);
    }

    /**
     * Reads and returns the rest of the given input stream as a byte array,
     * closing the input stream afterwards.
     *
     * @param is the input stream.
     *
     * @return the rest of the given input stream.
     */
    public static byte[] toByteArray(InputStream is) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            final byte[] b = new byte[BUFFER_SIZE];
            int n = 0;
            while ((n = is.read(b)) != -1) {
                output.write(b, 0, n);
            }
            return output.toByteArray();
        } finally {
            output.close();
        }
    }

    /**
     * Saves the raw data in a given file on the external storage.
     *
     * @param filePath  the file path on the storage
     * @param fileName  the file name
     * @param fileBytes the file content
     *
     * @return the full path to the file after a successful save. Empty string otherwise
     */
    public static String saveExternalFileData(Context context, String filePath, String fileName, byte[] fileBytes) {
        String folder = getFilesDirPath(context, filePath);
        return saveFileDataToExternal(folder + "/" + fileName, fileBytes);
    }

    public static String getFilesDirPath(Context context, String filePath) {
        return createFolder(getFilesDir(context).getPath() + "/" + filePath);
    }

    public static File getExternalFilesDir(Context context, String filePath) {
        return context.getExternalFilesDir(filePath);
    }


    public static File getFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * Saves the data to the given file output
     *
     * @param path
     * @param fileBytes
     *
     * @return the path to the given file
     */
    @NonNull
    public static String saveFileDataToExternal(String path, byte[] fileBytes) {
        Timber.d("saveFileDataToExternal() called with: path = [" + path + "], fileBytes = [" + fileBytes + "]");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bos.write(fileBytes != null ? fileBytes : new byte[0]);
            bos.flush();
            bos.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    /**
     * Creates a folder identified with the given path
     *
     * @param path
     */
    public static String createFolder(String path) {
        final File dir = new File(path);
        //create folders where write files
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path;
    }

    /**
     * @param filePath - path to the file
     *
     * @return true if the file is an existing one.
     */
    public static boolean isExistingFile(String filePath) {
        final File dir = new File(filePath);
        return dir.exists();
    }


    /**
     * @param path the path pointing to the file
     *
     * @return the file content as a byte[] if the file exists. Otherwise it is null
     */
    public static byte[] getRawFileData(String path) {

        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return bytes;
    }

    /**
     * @param is the input stream
     *
     * @return the raw content of a file
     */
    public static byte[] getRawFileData(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }


    /**
     * @param mimeType
     *
     * @return the type of the file based on the mime type
     */
    public static FileType getFiletype(String mimeType) {
        return FileType.fromMime(mimeType);
    }

    public static File createTempFile(Context context, String fileName, byte[] fileBytes) {
        return createTempFile(context, fileName, true, fileBytes);
    }


    /**
     * Creates a temporary file with the raw data given. this temporary file will be stored in the internal cache dir which self manages when to
     * delete it's content.
     * Read more here: https://developer.android.com/guide/topics/data/data-storage.html#InternalCache
     *
     * @param context   the context which will be used to access the file storage
     * @param fileName  the name of the file
     * @param addSuffix flag indicating if the file should have the suffix EPRIVO_TEMP_FILE_SUFFIX automatically added
     * @param fileBytes the raw file data
     *
     * @return the file
     */
    public static File createTempFile(Context context, String fileName, boolean addSuffix, byte[] fileBytes) {
        try {
            File outputFile = File.createTempFile(sanitizeFilePath(fileName), addSuffix ? EPRIVO_TEMP_FILE_SUFFIX : "", getEprivoCacheDir(context));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            bos.write(fileBytes);
            bos.flush();
            bos.close();
            return outputFile;
        } catch (IOException e) {
            return null;
        }
    }

    public static String sanitizeFilePath(String objectId) {
        if (!TextUtils.isEmpty(objectId)) {
            return objectId.replaceAll("\\.", "-").replaceAll("\\\\", "_").replaceAll("/", "_");
        }
        return null;
    }

    /**
     * Returns the temp file
     *
     * @param context
     * @param fileName
     *
     * @return the path to the temp file.
     */
    public static String getTempFilePath(Context context, String fileName) {
        File tempFile = getTempFile(context, fileName);
        return tempFile != null ? tempFile.getAbsolutePath() : "";
    }

    /**
     * @param context
     * @param fileName
     *
     * @return the tem file with the given name
     */
    public static File getTempFile(Context context, String fileName) {
        try {
            File outputFile = File.createTempFile(fileName, EPRIVO_TEMP_FILE_SUFFIX, getEprivoCacheDir(context));
            return outputFile;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Replaces the content of the destination file with the source file
     *
     * @param destinationFile
     * @param sourceFile
     */
    public static void replaceContent(String destinationFile, String sourceFile) {
        // delete the destination file
        try {
            if (!destinationFile.equals(sourceFile)) {
                deleteFile(destinationFile);
                byte[] newContent = getRawFileData(sourceFile);
                saveFileDataToExternal(destinationFile, newContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long folderSize(File directory) {
        long length = directory.isFile() ? directory.length() : 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += folderSize(file);
                }
            }
        }
        return length;
    }

    /**
     * @param size the number of bytes
     *
     * @return returns a file size in a readable formmat
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static byte[] getByteDataFromFileFullPath(String fullPath) {
        File file = new File(fullPath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return bytes;
    }


    /**
     * Deletes the temp file
     *
     * @param context
     * @param filePath
     *
     * @return true if the deletion was successful
     */
    public static boolean deleteTempFile(Context context, String filePath) {
        Timber.d("deleteTempFile() called with: context = [" + context + "], filePath = [" + filePath + "]");
        if (!TextUtils.isEmpty(filePath)) {
            File outputDir = getEprivoCacheDir(context);
            try {
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                File outputFile = File.createTempFile(fileName, EPRIVO_TEMP_FILE_SUFFIX, outputDir);
                if (outputFile.exists()) {
                    boolean deleted = outputFile.delete();
                    return deleted;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public static boolean deleteFile(String filePath) {
        Timber.d("deleteFile() called with: filePath = [" + filePath + "]");
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    // This creates a file in context.getFilesDir() with the same name as asset
    public static Boolean createFileForAsset(String asset, Boolean overwriteIfExists, Context ctx) {
        try {
            String filePath = ctx.getFilesDir().getPath() + File.separator + asset;
            File f = new File(filePath);
            if (!f.exists()) {
                Timber.d("File does not exist for asset " + asset + " at path " + filePath + "\nCreating file...");
                if (!f.createNewFile()) {
                    Timber.e("FAILED to create new file at location " + filePath);
                    return false;
                }
            } else if (!overwriteIfExists) {
                Timber.d("Found existing file at location " + filePath + " - overwrite flag was false. Skipping file creation...");
                return true;
            }

            FileOutputStream fos = new FileOutputStream(f);
            InputStream is = ctx.getAssets().open(asset);
            byte[] buf = new byte[1024];
            int count;
            while ((count = is.read(buf)) >= 0) {
                fos.write(buf, 0, count);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Creates a fileName using the raw resource content
     *
     * @param context  the application context
     * @param resId    the resource which should be copied
     * @param fileName the fileName name where the raw content should be copied
     */
    public static void createFileFromRawResource(Context context, int resId, String fileName) {
        try {
            File file = new File(fileName);

            // If the file does not exists then
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = context.getResources().openRawResource(resId);
            byte[] buf = new byte[1024];
            int count;
            while ((count = is.read(buf)) >= 0) {
                fos.write(buf, 0, count);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param resId
     *
     * @return the file content as a String or null in case of any erro
     */
    public static String readRawFilecontent(Context context, int resId) {
        try {
            byte[] b = getBytesFromRawResource(context, resId);
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getBytesFromRawResource(Context context, int resId) throws IOException {
        Resources res = context.getResources();
        InputStream inputStream = res.openRawResource(resId);

        byte[] b = new byte[inputStream.available()];
        inputStream.read(b);

        return b;
    }

    public static File getPublicDownloadDir() {
        // Get the directory for the user's public pictures directory.
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.mkdirs()) {
            Timber.e("Directory not created");
        }
        return file;
    }

    /**
     * @param context
     *
     * @return the Eprivo cache directory
     */
    public static File getEprivoCacheDir(Context context) {
        File file = new File(context.getCacheDir().getAbsolutePath() + AppConstant.CACHE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }





    /**
     * Zips a file at a location and places the resulting zip file at the toLocation
     *
     * @param inputFolderPath
     * @param outZipPath
     */
    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Timber.d("Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Timber.d("Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            Timber.e(ioe.getMessage(), "");
        }
    }

    /**
     * Used to get file detail from uri.
     * <p>
     * 1. Used to get file detail (name & size) from uri.
     * 2. Getting file details from uri is different for different uri scheme,
     * 2.a. For "File Uri Scheme" - We will get file from uri & then get its details.
     * 2.b. For "Content Uri Scheme" - We will get the file details by querying content resolver.
     *
     * @param uri Uri.
     *
     * @return file detail.
     */
    public static FileDetail getFileDetailFromUri(final Context context, final Uri uri) {
        FileDetail fileDetail = null;
        if (uri != null) {
            fileDetail = new FileDetail();
            // File Scheme.
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                File file = new File(uri.getPath());
                fileDetail.fileName = file.getName();
                fileDetail.fileSize = file.length();
            }
            // Content Scheme.
            else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    fileDetail.fileName = returnCursor.getString(nameIndex);
                    fileDetail.fileSize = returnCursor.getLong(sizeIndex);
                    returnCursor.close();
                }
            }
        }
        return fileDetail;
    }

    /**
     * Tries to fetch the size of the file from the URI using content resolver
     *
     * @param contentResolver
     * @param uri
     *
     * @return
     */
    public static long getFileSizeFromURI(ContentResolver contentResolver, Uri uri) {
        long size = 0;

        try {
            Cursor query = contentResolver.query(uri, null, null, null, null);

            if (query != null) {
                int columnIndex = query.getColumnIndex(OpenableColumns.SIZE);
                query.moveToFirst();
                size = query.getLong(columnIndex);
                query.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }

    /**
     * Tries to fetch the data from the Uri
     *
     * @param context the app context
     * @param uri     the Uri to get
     *
     * @return the byte[]
     */
    public static byte[] getDataFromUri(Context context, Uri uri) {

        byte[] data = null;

        try {
            InputStream iStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = iStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            data = byteBuffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
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
