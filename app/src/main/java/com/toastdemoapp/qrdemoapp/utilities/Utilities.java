package com.toastdemoapp.qrdemoapp.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import  com.toastdemoapp.qrdemoapp.R;
import  com.toastdemoapp.qrdemoapp.dialogs.ProgressViewDialog;
import  com.toastdemoapp.qrdemoapp.helpers.Logger;
import  com.toastdemoapp.qrdemoapp.helpers.SharedPreferenceData;
import  com.toastdemoapp.qrdemoapp.managers.LocaleManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utilities {

    private static Utilities _instance = null;
    private ProgressViewDialog progressDialog;
    private SharedPreferenceData shared;
    private AlertDialog alertDialog;

    /**
     * instance Singleton
     *
     * @return Utilities
     */
    public static Utilities instance() {
        if (_instance == null) _instance = new Utilities();
        return _instance;
    }

    /**
     * Constructor Utilities
     */
    public Utilities() {
        shared = new SharedPreferenceData(MyApplication.getContext());
        enlargeHeapSize();
    }

    // ---------------------------------------------------------------------------------------------
    // ERRORS

    /**
     * Convert error to string text
     *
     * @param t Throwable
     * @return String error
     */
    public static String errorToString(Throwable t) {
        StringBuffer errorMessage = new StringBuffer();
        if (errorMessage.length() == 0 && t.getCause() != null && t.getCause().getLocalizedMessage() != null)
            errorMessage.append(t.getCause().getLocalizedMessage());

        if (errorMessage.length() == 0 && t.getCause() != null && t.getCause().getMessage() != null)
            errorMessage.append(t.getCause().getMessage());

        if (errorMessage.length() == 0 && t.getLocalizedMessage() != null)
            errorMessage.append(t.getLocalizedMessage());

        if (errorMessage.length() == 0 && t.getMessage() != null)
            errorMessage.append(t.getMessage());

        if (errorMessage.length() == 0)
            errorMessage.append(t.getClass().getSimpleName());

        return errorMessage.toString();
    }

    /**
     * md5Hash
     *
     * @param key String
     * @return String cacheKey
     */
    public static String md5Hash(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * Get SHA Hash
     *
     * @return String key
     */
    public static String shaHash() {
        PackageInfo packageInfo;
        Context context = BaseApplication.getContext();
        String key = null;
        try {
            // Getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            // Retrieving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Logger.instance().v("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Logger.instance().v("Key Hash=", key);
            }
        } catch (NameNotFoundException e1) {
            Logger.instance().v("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Logger.instance().v("No such an algorithm", e.toString());
        } catch (Exception e) {
            Logger.instance().v("Exception", e.toString());
        }

        return key;
    }

    /**
     * Convert bytes to Hex String
     *
     * @param bytes byte[]
     * @return String
     */
    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------------------------------------
    // IMAGE ENCODING

    /**
     * Encode Images To Base64
     *
     * @param image Bitmap Bitmap
     * @return String imageEncoded
     */
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * Get Bitmap From Base64
     *
     * @param imageBase64 String imageBase64
     * @return Bitmap to put in ImageView "imageView.setImageBitmap(decodedByte)"
     */
    public static Bitmap getBitmapFromBase64(String imageBase64) {
        byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Resize Bitmap Image
     *
     * @param bm        Bitmap
     * @param newWidth  int
     * @param newHeight int
     * @return Bitmap
     */
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        return resizedBitmap;
    }

    /**
     * Resize Bitmap Image
     *
     * @param bm        Bitmap
     * @param newWidth  int
     * @param newHeight int
     * @return String
     */
    public static String getResizedBase64(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * Capitalize First letter of text
     *
     * @param word String
     * @return String
     */
    public static String getFirstLetterCapitalized(String word) {
        if (!isNullString(word))
            return word.substring(0, 1).toUpperCase(Locale.getDefault()) + word.substring(1);
        return word;
    }

    // ---------------------------------------------------------------------------------------------
    // HEIGHT & WIDTH

    /**
     * Get Screen height
     *
     * @param activity Activity
     * @return int
     */
    @SuppressLint("ObsoleteSdkInt")
    public static int getScreenHeight(Activity activity) {
        int measuredHeight = 0;
        Point size = new Point();
        WindowManager w = activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredHeight = d.getHeight();
        }
        return measuredHeight;
    }

    /**
     * Get Screen Width
     *
     * @param activity Activity
     * @return int
     */
    @SuppressLint("ObsoleteSdkInt")
    public static int getScreenWidth(Activity activity) {
        int measuredWidth = 0;
        Point size = new Point();
        WindowManager w = activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
        }
        return measuredWidth;
    }

    /**
     * Check if the device CHDPI and has a "Navigation Bar"
     *
     * @param activity Activity
     * @return boolean
     */
    public static boolean isDeviceXHDPIWithNavBar(Activity activity) {
        int screenHeight = getScreenHeight(activity);
        return (screenHeight > 1100 && screenHeight < 1280);
    }

    /**
     * Get current app language
     *
     * @return String language
     */
    public static String getLanguage() {
        // Language of mobile
        LocaleManager localeManager = new LocaleManager(MyApplication.getContext());
        return localeManager.getAppLanguage();
        // return Locale.getDefault().getLanguage();
        // Language of app
        // return getContext().getResources().getConfiguration().locale.toString();
    }

    /**
     * Set Locale language of App
     *
     * @param lang String {"en": English, "ar": Arabic}
     */
    public static void setLocale(String lang, Activity activity) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, null);
    }

    /**
     * Check if process exit
     */
    public static void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /**
     * Get Decoded String
     *
     * @param text String
     * @return String text
     */
    public static String getDecodedString(String text) {
        if (!isNullString(text)) {
            try {
                text = URLDecoder.decode(text, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    /**
     * Enlarge Memory Heap Size
     */
    public static void enlargeHeapSize() {
        // long oldHeapSize = VMRuntime.getRuntime().setMinimumHeapSize(64 * 1024 * 1024);
        // Logger.instance().v("oldHeapSize", oldHeapSize, false);
    }

    /**
     * Print Memory State
     */
    public static void printMemState() {
        ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        Logger.instance().v("Memory", " memoryInfo.availMem " + memoryInfo.availMem + "\n", false);
        Logger.instance().v("Memory", " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n", false);
        Logger.instance().v("Memory", " memoryInfo.threshold " + memoryInfo.threshold + "\n", false);
        int memoryClass = activityManager.getMemoryClass();

        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
        // Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase
        // After garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        Logger.instance().v("Memory", "memoryClass:" + Integer.toString(memoryClass) + " " + Runtime.getRuntime().maxMemory(), false);
        Logger.instance().v("Memory", "Heap: heapSize= " + (heapSize / 1024 / 1024) + " MB heapMaxSize= " + (heapMaxSize / 1024 / 1024) + " MB   heapFreeSize= " + (heapFreeSize / 1024 / 1024) + " MB", false);

    }

    /**
     * Det Device Id
     *
     * @return String deviceId
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceId() {
        String deviceId = "";
        try {
            deviceId = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception e) {
        }

        if (MyApplication.getContext() != null && isNullString(deviceId)) {
            TelephonyManager tel = (TelephonyManager) MyApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            assert tel != null;
            Logger.instance().v("Device Id - Tel", tel.getDeviceId(), false);
            return tel.getDeviceId();
        }
        return deviceId;
    }

    /**
     * Add UI Service Observer to the list
     *
     * @param uiObserver      Object
     * @param uiObserversList ArrayList<Object>
     */
    public static void addUiObserver(Object uiObserver, ArrayList<Object> uiObserversList) {
        // Remove the observer if it was already added here
        removeUiObserver(uiObserver, uiObserversList);

        // Add to observers List
        uiObserversList.add(uiObserver);
    }

    /**
     * Remove UI Service Observer to the list
     *
     * @param uiObserver      Object
     * @param uiObserversList ArrayList of any
     */
    public static void removeUiObserver(Object uiObserver, ArrayList<?> uiObserversList) {
        // Remove the observer if it was already added here
        try {
            ArrayList<Object> uiObservers = new ArrayList<>();
            uiObservers.addAll(uiObserversList);

            for (Object uiObserver_ : uiObservers)
                if (uiObserver_.getClass().equals(uiObserver.getClass())) {
                    Logger.instance().v("Removed", uiObserver.getClass(), false);
                    uiObserversList.remove(uiObserver);
                }
            uiObservers = null;
        } catch (Exception e) {
            // ...
        }
    }

    /**
     * Get Html From Url
     *
     * @param urlString String
     * @return String inputLine
     */
    public static String getHtmlFromUrl(String urlString) {
        URL url;
        String inputLine = "";
        try {
            url = new URL(urlString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = "";
            while ((line = in.readLine()) != null)
                inputLine += line;
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputLine;
    }

    /**
     * Check is Agency in ContactList
     *
     * @param agencyName String
     * @return boolean
     */
    public static boolean isContactAddedBefore(String agencyName) {
        // Number is the phone number
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]
                {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = MyApplication.getContext().getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();

        try {
            do {
                String name = people.getString(indexName);
                String number = people.getString(indexNumber);
                Logger.instance().v("Name", name + " " + agencyName, false);
                // Do work...
                if (name.compareTo(agencyName) == 0) return true;
            } while (people.moveToNext());
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * Get Double from String
     *
     * @param str String
     * @return double value
     */
    public static double getDouble(String str) {
        double value = 0.0;
        try {
            str = str.replaceAll(",", ".");
            value = Double.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get Double from BigDecimal
     *
     * @param bigDecimal BigDecimal
     * @return double value
     */
    public static double getDouble(BigDecimal bigDecimal) {
        double value = 0.0;
        try {
            value = bigDecimal.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Check if String is Null/empty or not
     *
     * @param str String
     * @return boolean
     */
    public static boolean isNullString(String str) {
        if (str != null && str.compareToIgnoreCase("null") != 0 && str.trim().length() > 0)
            return false;
        return true;
    }

    /**
     * Check if List is Null/empty or not
     *
     * @param list List
     * @return boolean
     */
    public static boolean isNullList(List<?> list) {
        if (list != null && list.size() > 0)
            return false;
        return true;
    }

    /**
     * Display Toast message
     *
     * @param msg      String
     * @param duration int
     */
    public static void showToastMsg(String msg, int duration) {
        Toast toastMsg = Toast.makeText(getContext(), msg, duration);
        toastMsg.setGravity(Gravity.CENTER, 0, 0);
        toastMsg.show();
    }

    /**
     * Display Toast message in Runnable
     *
     * @param activity Activity
     * @param msg      String
     * @param duration int
     */
    public static void showToastMsg(Activity activity, final String msg, final int duration) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastMsg(msg, duration);
            }
        });
    }

    /**
     * Show Error message in Toast
     *
     * @param activity Activity
     * @param msg      String
     * @param duration int
     */
    public static void showErrorMsg(Activity activity, final String msg, final int duration) {
        showToastMsg(activity, msg, duration);
    }


    /**
     * Is ProgressViewDialog Showing
     *
     * @return boolean
     */
    public boolean isProgressDialogShowing() {
        if (progressDialog != null) return progressDialog.isShowing();
        return false;
    }

    /**
     * Show ProgressViewDialog
     *
     * @param activity Activity
     * @param msg      String
     */
    public void showProgressDialog(final Activity activity, String msg) {
        showProgressDialog(activity, msg, null);
    }

    /**
     * Show ProgressViewDialog
     *
     * @param activity       Activity
     * @param msg            String
     * @param cancelListener DialogInterface.OnCancelListener
     */
    public void showProgressDialog(final Activity activity, String msg, DialogInterface.OnCancelListener cancelListener) {
        dismissProgressDialog();

        progressDialog = new ProgressViewDialog(activity);
        if (msg == null) msg = "Want a moment";
        progressDialog.setOnCancelListener(cancelListener);
        progressDialog.showProgressDialog(msg);
    }

    /**
     * Dismiss ProgressViewDialog
     */
    public void dismissProgressDialog() {
        if (progressDialog != null) progressDialog.hideDialog();
    }

    /**
     * Show ErrorDialog
     *
     * @param activity Activity
     * @param title    String
     * @param msg      String
     */
    public void showErrorDialog(final Activity activity, String title, String msg) {
        if (alertDialog != null)
            alertDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null)
            builder.setTitle(title);

        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Dismiss AlertDialog
     */
    public void dismissAlertDialog() {
        if (alertDialog != null) alertDialog.dismiss();
    }

    /**
     * Ellipsize TextView to end with ...
     *
     * @param snippet  TextView
     * @param maxLines int
     */
    public static void ellipsizeTextView(final TextView snippet, final int maxLines) {
        ViewTreeObserver vto = snippet.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = snippet.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (snippet.getLineCount() > maxLines) {
                    int lineEndIndex = snippet.getLayout().getLineEnd(maxLines - 1);
                    String text = snippet.getText().subSequence(0, lineEndIndex - maxLines) + "...";
                    snippet.setText(text);
                }
            }
        });
    }

    /**
     * blur fast
     *
     * @param bitmap Bitmap
     * @param radius int
     * @return Bitmap
     */
    public static Bitmap blurfast(Bitmap bitmap, int radius) {
        if (bitmap == null) return null;
        // Create a bitmap of the same size
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.RGB_565);
        // Create a canvas for new bitmap
        Canvas c = new Canvas(bmp);
        // Draw your old bitmap on it.
        c.drawBitmap(bitmap, 0, 0, new Paint());

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pix = new int[w * h];
        bmp.getPixels(pix, 0, w, 0, 0, w, h);

        for (int r = radius; r >= 1; r /= 2) {
            for (int i = r; i < h - r; i++) {
                for (int j = r; j < w - r; j++) {
                    int tl = pix[(i - r) * w + j - r];
                    int tr = pix[(i - r) * w + j + r];
                    int tc = pix[(i - r) * w + j];
                    int bl = pix[(i + r) * w + j - r];
                    int br = pix[(i + r) * w + j + r];
                    int bc = pix[(i + r) * w + j];
                    int cl = pix[i * w + j - r];
                    int cr = pix[i * w + j + r];

                    pix[(i * w) + j] = 0xFF000000
                            | (((tl & 0xFF) + (tr & 0xFF) + (tc & 0xFF) + (bl & 0xFF) + (br & 0xFF) + (bc & 0xFF) + (cl & 0xFF) + (cr & 0xFF)) >> 3)
                            & 0xFF
                            | (((tl & 0xFF00) + (tr & 0xFF00) + (tc & 0xFF00) + (bl & 0xFF00) + (br & 0xFF00) + (bc & 0xFF00)
                            + (cl & 0xFF00) + (cr & 0xFF00)) >> 3)
                            & 0xFF00
                            | (((tl & 0xFF0000) + (tr & 0xFF0000) + (tc & 0xFF0000) + (bl & 0xFF0000) + (br & 0xFF0000)
                            + (bc & 0xFF0000) + (cl & 0xFF0000) + (cr & 0xFF0000)) >> 3) & 0xFF0000;
                }
            }
        }
        bmp.setPixels(pix, 0, w, 0, 0, w, h);
        return bmp;
    }

    /**
     * Copy InputStream method
     *
     * @param input  InputStream
     * @param output OutputStream
     * @throws IOException buffer
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];

        int n = 0;

        while (-1 != (n = input.read(buffer))) {

            output.write(buffer, 0, n);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // SETTER & GETTER

    /**
     * Get SharedPreference Data
     *
     * @return SharedPreferenceData
     */
    public SharedPreferenceData getShared() {
        return shared;
    }

    /**
     * Set SharedPreference Data
     *
     * @param shared SharedPreferenceData
     */
    public void setShared(SharedPreferenceData shared) {
        this.shared = shared;
    }

    /**
     * Get Context
     *
     * @return Context
     */
    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * Get rounded bitmap
     *
     * @param bmp    Bitmap
     * @param radius int
     * @return Bitmap
     */
    public static Bitmap getRoundedShape(Bitmap bmp, int radius) {
        if (bmp == null)
            return null;

        int width, height;
        if (bmp.getWidth() > bmp.getHeight()) {
            height = radius;
            width = radius * bmp.getWidth() / bmp.getHeight();
        } else {
            height = radius * bmp.getHeight() / bmp.getWidth();
            width = radius;
        }

        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, width, height, false);
        else
            sbmp = bmp;

        Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);

		/*Bitmap output =  bmp.isMutable()? bmp :Bitmap.createBitmap(radius, radius,
				Config.ARGB_8888) ;*/

        Canvas canvas = new Canvas(output);

        // final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        paint.setColor(Color.parseColor("#BAB399"));
        paint.setColor(Color.parseColor("#000000"));

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius / 2 + 0.7f,
                radius / 2 + 0.7f, radius / 2 + 0.1f, paint);

        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    // ---------------------------------------------------------------------------------------------
    // KEYBOARD

    /**
     * Show & hide soft keyboard
     *
     * @param context  Context
     * @param editText EditText
     */
    public static void showSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.requestFocus();
    }

    /**
     * Hide Soft Keyboard
     *
     * @param context Context
     * @param view    View
     */
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // INTERNET CONNECTION

    /**
     * Simple network connection check.
     *
     * @param context context
     */
    public static boolean isConnected(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    // ---------------------------------------------------------------------------------------------
    // DATE

    /**
     * Check if is Date In Past
     *
     * @param date Date
     * @return boolean
     */
    public static boolean isDateInPast(Date date) {
        if (date == null)
            return true;
        Date nowDate = Calendar.getInstance().getTime();
        return date.before(nowDate);
    }

    /**
     * Get Days Between Dates
     *
     * @param date1 Date
     * @param date2 Date
     * @return int value
     */
    public static int getDaysBetweenDates(Date date1, Date date2) {
        // NOTES:-
        // if date2 is more in the future than date1 then the result will be negative
        // if date1 is more in the future than date2 then the result will be positive.
        float days = ((float) (date2.getTime() - date1.getTime()) / (1000.0f * 60.0f * 60.0f * 24.0f));
        return (int) Math.ceil(days);
    }

    /**
     * Compare 2 days without comparing their time
     *
     * @param date1 Date
     * @param date2 Date
     * @return boolean
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        boolean sameYear = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
        boolean sameMonth = calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
        boolean sameDay = calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
        return (sameDay && sameMonth && sameYear);
    }

    /**
     * Get Date only from format "2018-09-23 02:12:00.0"
     *
     * @param dateInString String
     * @return String date
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateOnly(String dateInString) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if (dateInString != null) date = formatter.parse(dateInString);
            else return "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
        return formatter2.format(date);
    }

    /**
     * Get Hours only from format "2018-09-23 02:12:00.0"
     *
     * @param dateInString String
     * @return String date
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHoursOnly(String dateInString) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm");
        return formatter2.format(date);
    }

    /**
     * Convert Hours base 24 to base 12 AM or PM.
     *
     * @param dateInString String
     * @return String date
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHoursBase12(String dateInString) {
        Date date = null;
        String extension;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatterHours = new SimpleDateFormat("hh");
        SimpleDateFormat formatterMinutes = new SimpleDateFormat("mm");
        int minutes = Integer.parseInt(formatterMinutes.format(date));
        int hours = Integer.parseInt(formatterHours.format(date));
        if (hours >= 12) {
            extension = " AM";
            hours -= 12;
        } else extension = " PM";
        return hours + ":" + minutes + extension;
    }

    /**
     * Get End Hours after duration.
     *
     * @param dateInString String
     * @param duration     int
     * @return String date
     */
    @SuppressLint("SimpleDateFormat")
    public static String getEndHours(String dateInString, int duration) {
        Date date = null;
        String extension;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatterHours = new SimpleDateFormat("hh");
        SimpleDateFormat formatterMinutes = new SimpleDateFormat("mm");
        int minutes = Integer.parseInt(formatterMinutes.format(date));
        int hours = Integer.parseInt(formatterHours.format(date));
        int hoursAdd = 0;
        while (duration > 60) {
            duration = duration - 60;
            hoursAdd += 1;
        }
        hours += hoursAdd;
        if (hours >= 12) {
            extension = " AM";
            hours -= 12;
        } else extension = " PM";
        return hours + ":" + (minutes + duration) + extension;
    }

    // ---------------------------------------------------------------------------------------------
    // SEND ACTION INTENT

    /**
     * Send SMS Message
     *
     * @param context Context
     * @param tel     String Phone number
     * @param body    String message
     */
    public static void sendSMS(Context context, String tel, String body) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", tel);
        if (!Utilities.isNullString(body)) smsIntent.putExtra("sms_body", body);
        smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(smsIntent, "Select SMS App"));
    }

    /**
     * Open Email
     *
     * @param context      Context
     * @param emailAddress String
     * @param subject      String
     * @param body         String
     */
    public static void openEmail(Context context, String emailAddress, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        Intent chooser = Intent.createChooser(emailIntent, "");
        context.startActivity(chooser);
    }

    /**
     * Open Url in Browser
     *
     * @param context Context
     * @param url     String
     */
    public static void openUrlInBrowser(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(url));
        Intent chooser = Intent.createChooser(browserIntent, "");
        context.startActivity(chooser);
    }

    /**
     * Call Phone Number
     *
     * @param context     Context
     * @param phoneNumber String
     */
    public static void callPhoneNumber(Context context, String phoneNumber) {
        if (context == null || Utilities.isNullString(phoneNumber))
            return;
        Uri number = Uri.parse("tel:" + phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);

        // Create intent to show chooser
        Intent chooser = Intent.createChooser(callIntent, "");
        context.startActivity(chooser);

    }

    /**
     * Share APP info
     *
     * @param activity Activity
     * @param quote    String
     */
    public static void share(Activity activity, String quote /*,String credit*/) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, quote);
        try {
            shareIntent.putExtra("android.intent.extra.TEXT", quote);
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.app_name)));

    }

    // ---------------------------------------------------------------------------------------------
    // DEVICE HASH KEY

    /**
     * Print the hash key
     */
    public static void printHashKey() {
        try {
            PackageInfo info = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashkey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Logger.instance().v("KeyHash:", hashkey, false);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restart Activity
     *
     * @param activity Activity
     */
    public static void restartActivity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    // ---------------------------------------------------------------------------------------------
    // VALIDATION

    /**
     * Email address validation
     *
     * @param target CharSequence
     * @return boolean
     */
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target.toString().trim()).matches();
        }
    }

    /**
     * Is Valid Phone Number
     *
     * @param phoneNumber CharSequence
     * @return boolean
     */
    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        // Cause phone number is optional
        String regex = "^[+]?[0-9]{10,13}$";
        return phoneNumber.toString().matches(regex);
    }

    /**
     * Check if Confirm Password Match Password
     *
     * @param target1 CharSequence
     * @param target2 CharSequence
     * @return
     */
    public static boolean isConfirmPassMatchPass(CharSequence target1, CharSequence target2) {
        return target1.equals(target2);
    }

    /**
     * Check if GooglePlay Services Installed
     *
     * @param context Context
     * @return boolean
     */
    public static boolean isGooglePlayServicesInstalled(Context context) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo("com.img_google_logo.android.gms", 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi != null;
    }

    // ---------------------------------------------------------------------------------------------
    // APP LANGUAGE

    /**
     * Force to get the string from values-languageTag even if it's not the current language.
     *
     * @param context     Context
     * @param languageTag String
     * @param stringResId int
     * @return String Language
     */
    public static String getStringFromLanguage(Context context, String languageTag, int stringResId) {
        String value = "";
        if (!isNullString(languageTag) && context != null && stringResId > 0) {
            try {
                Resources standardResources = context.getResources();
                AssetManager assets = standardResources.getAssets();
                DisplayMetrics metrics = standardResources.getDisplayMetrics();
                Configuration config = new Configuration(standardResources.getConfiguration());
                // Save old locale
                Locale oldLocale = config.locale;

                config.locale = new Locale(languageTag);
                Resources mResources = new Resources(assets, metrics, config);
                value = mResources.getString(stringResId);

                // Restore old locale
                config.locale = oldLocale;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }


    // ---------------------------------------------------------------------------------------------
    // TEXT FORMATTING

    /**
     * Fixed part must contain %s, so that the method can replace %s with the value
     *
     * @param fixedPart String
     * @param value     String
     * @return String
     */
    public static String injectValueInString(String fixedPart, String value) {
        String formatted = "";
        if (!Utilities.isNullString(fixedPart)) {
            if (Utilities.isNullString(value))
                value = "";
            formatted = String.format(fixedPart, value);
        }
        return formatted;
    }

    /**
     * Fixed part must contain %s, so that the method can replace %s with the value
     *
     * @param fixedPart String
     * @param values    List
     * @return String
     */
    public static String injectValueInString(String fixedPart /*, String value*/, List<?> values) {
        String formatted = "";
        if (!Utilities.isNullString(fixedPart) /*&& !Utilities.isNullString(value)*/ && !isNullList(values)) {
            // formatted = String.format(fixedPart, value);
            Object ob[] = new Object[values.size()];
            for (int i = 0; i < values.size(); i++) {
                ob[i] = values.get(i);
            }
            formatted = String.format(fixedPart, ob);

        }
        return formatted;
    }

    /**
     * Takes String and the part of it and apply style (bold, color, text size) on that part
     *
     * @param strMain  String
     * @param strPart  String
     * @param color    int
     * @param isBold   boolean
     * @param textSize float
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder applyStyleOnPartOfString(String strMain, String strPart, int color, boolean isBold, float textSize) {
        if (Utilities.isNullString(strMain) ||
                Utilities.isNullString(strPart) ||
                (color == 0 && !isBold) ||    // Don't apply neither color  nor bold
                !strMain.contains(strPart)    // The part to apply isn't existing in the main string
                ) {
            if (!Utilities.isNullString(strMain))
                return new SpannableStringBuilder(strMain);
            else
                return new SpannableStringBuilder("");
        } else {
            final SpannableStringBuilder sb = new SpannableStringBuilder(strMain);
            int startIndex = strMain.indexOf(strPart);
            int endIndex = startIndex + strPart.length();

            // Apply Settings
            if (color != 0) { // Span to set text color to some RGB value
                final ForegroundColorSpan fcs = new ForegroundColorSpan(color);
                sb.setSpan(fcs, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            // Apply Bold
            if (isBold) { // Span to make text bold
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                sb.setSpan(bss, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (textSize > 0) { //span to change text size
                final RelativeSizeSpan rss = new RelativeSizeSpan(textSize);
                sb.setSpan(rss, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            return sb;
        }
    }

    /**
     * Reformat date
     *
     * @param oldDate      String
     * @param sdfOldFormat SimpleDateFormat
     * @param sdfNewFormat SimpleDateFormat
     * @return String
     */
    public static String reFormatDate(String oldDate, SimpleDateFormat sdfOldFormat, SimpleDateFormat sdfNewFormat) {
        String newString = "";
        try {
            Date date = sdfOldFormat.parse(oldDate);
            newString = sdfNewFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newString;
    }

    /**
     * Read json file from the assets folder and returns a string of it's content
     *
     * @param fileName String
     * @return String
     */
    public static String readJsonFromAssetFile(String fileName) {
        String jsonResponse = null;
        try {
            jsonResponse = "";
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = BaseApplication.getContext().getResources().getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            if (inputStream != null) {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            jsonResponse = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    /**
     * Det Dimen From Resources
     *
     * @param resId int
     * @return int
     */
    public static int getDimenFromRes(int resId) {
        // int dp = (int) (BaseApplication.getContext().getResources().getDimension(resId)
        // BaseApplication.getContext().getResources().getDisplayMetrics().density);
        int dp = (BaseApplication.getContext().getResources().getDimensionPixelSize(resId));
        return dp;

    }

    /**
     * Check if X large Screen
     *
     * @return boolean
     */
    public static boolean isXlargeScreen() {
        if ((MyApplication.getContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return true;
        }
        return false;
    }
}