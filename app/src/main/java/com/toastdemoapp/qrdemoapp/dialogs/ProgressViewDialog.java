package com.toastdemoapp.qrdemoapp.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import  com.toastdemoapp.qrdemoapp.R;
import  com.toastdemoapp.qrdemoapp.views.UIEngine;

public class ProgressViewDialog {

    private Context context;
    private android.app.ProgressDialog dialog;
    private DialogInterface.OnCancelListener onCancelListener;

    public ProgressViewDialog(Context mContext) {
        this.context = mContext;
        dialog = new android.app.ProgressDialog(context);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        if (dialog != null)
            dialog.setOnCancelListener(onCancelListener);
    }

    /**
     * Show Progress Dialog with giving message and title "Loading..."
     *
     * @param msg String
     */
    public void showProgressDialog(String msg) {
        UIEngine.initialize(context);
        try {
            dialog.setTitle(UIEngine.createSpannableString(context.getString(R.string.loading), UIEngine.Fonts.APP_FONT_REGULAR));
            dialog.setMessage(UIEngine.createSpannableString(msg, UIEngine.Fonts.APP_FONT_LIGHT));
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            if (!dialog.isShowing()) dialog.show();

            if (onCancelListener != null)
                setOnCancelListener(onCancelListener);
            else {
                setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.v("Dialog", "Canceled");
                    }
                });
            }
        } catch (Exception e) {
            Log.v("showProgressDialog", e + "");
        }
    }

    public void hideDialog() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                setDialogCancelable(true);
            }
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
    }

    public void setDialogCancelable(boolean isCancelable) {
        if (dialog != null) {
            try {
                dialog.setCancelable(isCancelable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}