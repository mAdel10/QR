package com.toastdemoapp.qrdemoapp.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import  com.toastdemoapp.qrdemoapp.helpers.Constants;
import com.toastdemoapp.qrdemoapp.utilities.AppConfiguration;
import com.toastdemoapp.qrdemoapp.utilities.Engine;

import java.util.Locale;

public class UIEngine {

    public static void initialize(Context context) {
        Fonts.APP_FONT_LIGHT = Typeface.createFromAsset(context.getAssets(), Constants.FONT_POPPINS_LIGHT);
        Fonts.APP_FONT_REGULAR = Typeface.createFromAsset(context.getAssets(), Constants.FONT_POPPINS_REGULAR);
        Fonts.APP_FONT_MEDIUM = Typeface.createFromAsset(context.getAssets(), Constants.FONT_POPPINS_MEDIUM);
        Fonts.APP_FONT_SEMI_BOLD = Typeface.createFromAsset(context.getAssets(), Constants.FONT_POPPINS_SEMI_BOLD);
        Fonts.APP_FONT_BOLD = Typeface.createFromAsset(context.getAssets(), Constants.FONT_POPPINS_BOLD);
    }

    public static class Fonts {

        public static Typeface APP_FONT_LIGHT;
        public static Typeface APP_FONT_REGULAR;
        public static Typeface APP_FONT_MEDIUM;
        public static Typeface APP_FONT_SEMI_BOLD;
        public static Typeface APP_FONT_BOLD;
    }

    /**
     * Return color represents the value to be used in progress wheel , bar chart
     *
     * @param mval int
     * @return int color
     */
    public static int getValueColor(int mval) {
        if (mval >= 0 && mval < 35) {
            return Color.parseColor("#ff0000");
        } else if (mval >= 35 && mval < 75) {
            return Color.parseColor("#ffaa00");
        } else if (mval >= 75 && mval <= 100) {
            return Color.parseColor("#57de57");
        }

        return 0;
    }

    /**
     * Get Current App Locale
     *
     * @return Locale
     */
    public static Locale getCurrentAppLocale() {
        AppConfiguration appConfig = Engine.getAppConfiguration();
        if (appConfig != null) {
            return appConfig.getLocale();
        }
        return new Locale(Engine.getAppConfiguration().getLanguage());
    }

    /**
     * Change font type of Navigation.
     *
     * @param mi       is MenuItem
     * @param typeface Typeface
     */
    public static void applyFontToMenuItem(MenuItem mi, Typeface typeface) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new AppTypefaceSpan("", typeface), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    /**
     * Apply Custom Font To TextView
     *
     * @param textView TextView
     * @param typeface Typeface
     */
    public static void applyCustomFont(TextView textView, Typeface typeface) {
        if (textView != null)
            textView.setTypeface(typeface);
    }

    /**
     * Apply Custom Font To EditText
     *
     * @param editText EditText
     * @param typeface Typeface
     */
    public static void applyCustomFont(EditText editText, Typeface typeface) {
        if (editText != null)
            editText.setTypeface(typeface);
    }

    /**
     * Apply Custom Font To Button
     *
     * @param button   Button
     * @param typeface Typeface
     */
    public static void applyCustomFont(Button button, Typeface typeface) {
        if (button != null)
            button.setTypeface(typeface);
    }

    /**
     * Apply Custom Font To RadioButton
     *
     * @param button   RadioButton
     * @param typeface Typeface
     */
    public static void applyCustomFont(RadioButton button, Typeface typeface) {
        if (button != null)
            button.setTypeface(typeface);
    }

    /**
     * Apply Custom Font
     *
     * @param checkBox CheckBox
     * @param typeface Typeface
     */
    public static void applyCustomFont(CheckBox checkBox, Typeface typeface) {
        if (checkBox != null)
            checkBox.setTypeface(typeface);
    }

    /**
     * Apply Custom Font
     *
     * @param topView  View
     * @param typeface Typeface
     */
    public static void applyCustomFont(View topView, Typeface typeface) {
        if (topView instanceof ViewGroup) {
            final int len = ((ViewGroup) topView).getChildCount();
            processViewGroup(((ViewGroup) topView), len, typeface);
        } else if (topView instanceof TextView) {
            applyCustomFont((TextView) topView, typeface);
        }
    }

    /**
     * Apply Custom Font To Process View Group
     *
     * @param v        ViewGroup
     * @param len      int
     * @param typeface Typeface
     */
    private static void processViewGroup(ViewGroup v, final int len, Typeface typeface) {
        for (int i = 0; i < len; i++) {
            final View c = v.getChildAt(i);
            if (c instanceof TextView) {
                applyCustomFont((TextView) c, typeface);
            } else if (c instanceof ViewGroup) {
                applyCustomFont(c, typeface);
            }
        }
    }

    /**
     * Create Spannable String with Font Type
     *
     * @param text     String
     * @param typeface Typeface
     * @return SpannableString
     */
    public static SpannableString createSpannableString(String text, Typeface typeface) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new RelativeSizeSpan(1.0f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AppTypefaceSpan("", typeface), 0, spannableString.length(), 0);
        return spannableString;
    }

    /**
     * Check if App Language Arabic
     *
     * @return boolean
     */
    public static boolean isAppLanguageArabic() {
        return Engine.isCurrentLanguageArabic();
    }

    /**
     * Get Current App Language
     *
     * @param context Context
     * @return String
     */
    public static String getCurrentAppLanguage(Context context) {
        return Engine.getAppConfiguration().getLanguage();
    }


    /**
     * Is Device Language Arabic
     *
     * @param context Context
     * @return boolean
     */
    public static boolean isDeviceLanguageArabic(Context context) {
        return getCurrentDeviceLanguage(context).equalsIgnoreCase("ar");
    }

    /**
     * Get Current Device Language
     *
     * @param context Context
     * @return String
     */
    public static String getCurrentDeviceLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * Apply Fonts For All Views
     *
     * @param v        View
     * @param typeface Typeface
     */
    public static void applyFontsForAll(final View v, Typeface typeface) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFontsForAll(child, typeface);
                }
            } else if (v instanceof TextView || v instanceof EditText || v instanceof RadioButton ||
                    v instanceof CheckBox || v instanceof TextView || v instanceof Button) {
                applyCustomFont(v, typeface);
            }
        } catch (Exception e) {
            // ...
        }
    }

    /**
     * enable/disable view and change bg to grey if disabled or to enableColor if enabled
     *
     * @param isEnabled  boolean
     * @param _view      View
     * @param enabledBg  int
     * @param disabledBg int
     */
    public static void enableView(boolean isEnabled, View _view, int enabledBg, int disabledBg) {
        if (_view == null)
            return;
        _view.setEnabled(isEnabled);
        _view.setBackgroundResource(isEnabled ? enabledBg : disabledBg);
    }
}