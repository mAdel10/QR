package com.toastdemoapp.qrdemoapp.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.toastdemoapp.qrdemoapp.R;
import com.toastdemoapp.qrdemoapp.backend.models.Qr;
import com.toastdemoapp.qrdemoapp.backend.models.QrForm;
import com.toastdemoapp.qrdemoapp.backend.models.SignForm;
import com.toastdemoapp.qrdemoapp.backend.models.User;
import com.toastdemoapp.qrdemoapp.backend.observers.CTHttpError;
import com.toastdemoapp.qrdemoapp.backend.observers.RequestObserver;
import com.toastdemoapp.qrdemoapp.backend.operations.ScanQrOperation;
import com.toastdemoapp.qrdemoapp.backend.operations.UserSignInOperation;
import com.toastdemoapp.qrdemoapp.dialogs.ErrorDialog;
import com.toastdemoapp.qrdemoapp.dialogs.PopupDialog;
import com.toastdemoapp.qrdemoapp.managers.UserManager;
import com.toastdemoapp.qrdemoapp.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class QrScanActivity extends BaseActivity implements QRCodeReaderView.OnQRCodeReadListener, PopupDialog.ErrorDialogListener, RequestObserver {

    private QRCodeReaderView qrCodeReaderView;
    private Button cancelButton;
    private TextView resultText;
    private static final int REQUEST_QR_SCAN = 1;


    private static final String KEY_USERNAME = "username";
    private static final String KEY_QRCODE = "qrcode";
    private boolean showDialoge = true;
    String scan_url = "http://newgizaqr.com/checkqr.php?check";

    public QrScanActivity() {
        super(R.layout.activity_qr_scan, false);
    }

    @Override
    protected void doOnCreate(Bundle bundle) {
        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        cancelButton = findViewById(R.id.qr_cancel_button);
        resultText = findViewById(R.id.result_text);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setTorchEnabled(true);
        qrCodeReaderView.setBackCamera();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });
    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        //resultText.setVisibility(View.VISIBLE);
        //resultText.setText(text);

       // Toast.makeText(this, "Scan", Toast.LENGTH_SHORT).show();
        if (showDialoge){
            scanQr(UserManager.getInstance().getCurrentUser().getUserName(), text);
            showDialoge = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onOkClick() {
        finish();
    }

    @Override
    public void onCancelClick() {

    }


//    private void scanQr(String username, String qrScan) {
//        QrForm qrForm = new QrForm(username, qrScan);
//        ScanQrOperation operation = new ScanQrOperation(qrForm, REQUEST_QR_SCAN, true, this);
//        operation.addRequestObserver(this);
//        operation.execute();
//    }

    @Override
    public void handleRequestFinished(Object requestId, Throwable error, Object resultObject) {

        if (error != null) {
            if (error instanceof CTHttpError) {
                int code = ((CTHttpError) error).getStatusCode();
                String errorMsg = ((CTHttpError) error).getErrorMsg();
                if (code == -1 || Utilities.isNullString(errorMsg)) {
                    ErrorDialog.showMessageDialog(getString(R.string.invalid_request), getString(R.string.request_server_error), this);
                } else {
                    ErrorDialog.showMessageDialog(getString(R.string.invalid_request), errorMsg, this);
                }
            }
        } else if (requestId.equals(REQUEST_QR_SCAN)) {
            Qr qr = (Qr) resultObject;
            Toast.makeText(this, qr.getIsValid() + " " + qr.getGateNum(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void requestCanceled(Integer requestId, Throwable error) {

    }

    @Override
    public void updateStatus(Integer requestId, String statusMsg) {

    }

    public static class MySingleton
    {
        private static MySingleton mInstance;
        private RequestQueue mRequestQueue;
        private static Context mCtx;

        private MySingleton(Context context)
        {
            mCtx = context;
            mRequestQueue = getRequestQueue();
        }

        public static synchronized QrScanActivity.MySingleton getInstance(Context context)
        {
            if (mInstance == null)
            {
                mInstance = new MySingleton(context);
            }
            return mInstance;
        }

        private RequestQueue getRequestQueue()
        {
            if (mRequestQueue == null)
            {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            }
            return mRequestQueue;
        }

        <T> void addToRequestQueue(Request<T> req)
        {
            getRequestQueue().add(req);
        }
    }

    private void scanQr(String username, String qrcode)
    {
        JSONObject request = new JSONObject();
        try
        {
            request.put(KEY_USERNAME, username);
            request.put(KEY_QRCODE, qrcode);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }



        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, scan_url, request, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            if (response.getBoolean("status"))
                            {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(QrScanActivity.this);
                                dialog.setTitle("Result");
                                dialog.setMessage(response.getString("message"));
                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        finish();
                                    }
                                });
                                dialog.show();

                                //Toast.makeText(QrScanActivity.this, "QR is Valid", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(QrScanActivity.this);
                                dialog.setTitle("Result");
                                dialog.setMessage(response.getString("message"));
                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                dialog.show();

                                //Toast.makeText(getApplicationContext(), "QR not valid", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        LoginActivity.MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}