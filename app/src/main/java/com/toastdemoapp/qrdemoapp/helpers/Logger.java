package com.toastdemoapp.qrdemoapp.helpers;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import  com.toastdemoapp.qrdemoapp.utilities.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

    private static Logger _instance = null;
    private String loggerFileName = "Base_Logger.txt";
    private BufferedWriter write = null;
    private boolean canWrite = false;
    private int limitedFileSize = 500000;
    private File root;
    private File toWriteFile;

    static public Logger instance() {
        if (_instance == null) _instance = new Logger();
        return _instance;
    }

    /**
     * Constructor
     */
    private Logger() {
        root = Environment.getExternalStorageDirectory();
        if (root.canRead() && root.canWrite()) {
            toWriteFile = new File(root, loggerFileName);
            try {
                write = new BufferedWriter(new FileWriter(toWriteFile, true));
                canWrite = true;
            } catch (IOException e) {
                Log.v("Create Logger File", e.getMessage());
            }
        }
    }

    /**
     * Get Logging Time
     *
     * @return String
     */
    public String getLoggingTime() {
        String currentTime;
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        currentTime = sdf.format(cal.getTime());
        return currentTime;
    }

    /**
     * This v
     *
     * @param tag String
     * @param msg Object
     */
    public void v(String tag, Object msg) {
        v(tag, msg, false);
    }

    /**
     * Log.v
     *
     * @param tag         String
     * @param msg         Object
     * @param writeToFile boolean
     */
    public void v(String tag, Object msg, boolean writeToFile) {
        Log.v("MyApp " + tag, msg + "");
        if (canWrite && writeToFile) {
            try {
                // updateLogFile(toWriteFile,limitedFileSize);
                write.write(tag + "\t" + getLoggingTime() + "\t\t" + msg);
                write.newLine();
                write.flush();
            } catch (IOException e) {
                Log.v("verbose log", e.getMessage());
            }
        }
    }

    /**
     * Log.e
     *
     * @param tag         String
     * @param msg         Object
     * @param writeToFile boolean
     */
    public void e(String tag, Object msg, boolean writeToFile) {
        Log.e(tag, msg + "");
    }

    /**
     * Update Log File
     *
     * @param fileName  File
     * @param newLength int
     */
    public static void updateLogFile(File fileName, int newLength) {
        BufferedWriter write = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(fileName, "rw");
            long length = randomAccessFile.length();
            if (length > (newLength)) {
                System.out.println(getFileSize(fileName) + " " + length);
                // randomAccessFile.setLength(length - 50 );
                randomAccessFile.seek(newLength);
                // Declare a buffer with the same length as the second line
                byte[] buffer = new byte[((int) length - newLength)];
                // Read data from the file
                randomAccessFile.read(buffer);

                // Print out the buffer contents
                String newStr = new String(buffer);
                System.out.println(newStr);
                randomAccessFile.close();

                String[] temp = newStr.split("\n");
                System.out.println("temp " + temp.length);

                write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
                write.write(newStr.replaceFirst(temp[0], ""));
                write.flush();
                write.close();
            }
            randomAccessFile.close();
        } catch (IOException ex) {
            Log.v("UpdateFile", ex.getMessage());
        }
    }

    /**
     * Get File Size
     *
     * @param file File
     * @return long file length
     */
    public static long getFileSize(File file) {
        if (!file.exists() || !file.isFile()) {
            System.out.println("File doesn\'t exist");
            return -1;
        }
        // Here we get the actual size
        return file.length();
    }

    /**
     * Log Full Message
     *
     * @param tag     String
     * @param message Object
     */
    public void logFullMessage(String tag, Object message) {
        if (!Utilities.isNullString(message.toString()) && message.toString().length() > 4000) {
            int chunkCount = message.toString().length() / 4000; // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= message.toString().length()) {
                    v(tag, message.toString().substring(4000 * i), false);
                } else {
                    v(tag, message.toString().substring(4000 * i, max), false);
                }
            }
            return;
        }
        v(tag, message + "", false);
    }
}
