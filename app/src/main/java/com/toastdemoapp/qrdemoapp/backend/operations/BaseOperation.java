package com.toastdemoapp.qrdemoapp.backend.operations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import  com.toastdemoapp.qrdemoapp.R;
import  com.toastdemoapp.qrdemoapp.backend.observers.CTHttpError;
import  com.toastdemoapp.qrdemoapp.backend.observers.CTOperationResponse;
import  com.toastdemoapp.qrdemoapp.backend.observers.RequestObserver;
import  com.toastdemoapp.qrdemoapp.dialogs.ProgressViewDialog;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseOperation<T> extends AsyncTask<Void, Object, CTOperationResponse> {

    private static HashMap<String, BaseOperation<?>> activeOperations = new HashMap<>();
    private static HashMap<Object, BaseOperation<?>> activeOperationsMapByRequestId = new HashMap<>();

    public boolean isShowLoadingDialog = true;
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public ProgressViewDialog dialog;
    public Object requestID = 0;
    public long operationUniqueID = 0;
    public boolean isOperationRunning = false;
    public ArrayList<RequestObserver> observersList;

    private String loadingMessage;

    public BaseOperation() {
        operationUniqueID = System.currentTimeMillis();
    }

    public BaseOperation(Object requestID, boolean isShowLoadingDialog, Context context) {
        this.isShowLoadingDialog = isShowLoadingDialog;
        this.context = context;
        this.requestID = requestID;
        observersList = new ArrayList<>();
    }

    public BaseOperation(Object requestID, boolean isShowLoadingDialog, Context context, String message) {
        this.isShowLoadingDialog = isShowLoadingDialog;
        this.context = context;
        this.requestID = requestID;
        this.loadingMessage = message;
        observersList = new ArrayList<>();
    }

    public static BaseOperation<?> getActiveOperation(Class<? extends BaseOperation<?>> operationClass) {
        return activeOperations.get(operationClass.getName());
    }

    public static BaseOperation<?> getActiveOperationByRequestId(Object requestId) {
        if (requestId != null)
            return activeOperationsMapByRequestId.get(requestId);
        return null;
    }

    /**
     * Do/Execute the operation itself
     *
     * @return T the object
     * @throws Exception
     */
    public abstract T doMain() throws Throwable;

    private void showWaitingDialog() {
        if (dialog == null)
            dialog = new ProgressViewDialog(context);

        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.v("Dialog", "Canceled");
                cancelConnection();
                // Wake observers with the result
                for (RequestObserver observer : observersList) {
                    observer.requestCanceled((int) requestID, null);
                }

                BaseOperation.this.cancel(true);
            }
        });

        try {
            if (!dialog.isShowing())
                if (loadingMessage != null) {
                    dialog.showProgressDialog(loadingMessage);
                } else {
                    dialog.showProgressDialog(context.getString(R.string.please_wait_while_processing_your_request));
                }

        } catch (Exception e) {
            Log.v("Dialog", "Error");
        }
    }

    private void cancelConnection() {

    }

    @Override
    protected void onPreExecute() {
        isOperationRunning = true;
        activeOperations.put(this.getClass().getName(), this);
        if (requestID != null)
            activeOperationsMapByRequestId.put(requestID, this);
        super.onPreExecute();

        if (isShowLoadingDialog) {
            showWaitingDialog();
        }
    }

    @Override
    protected CTOperationResponse doInBackground(Void... params) {
        CTOperationResponse response = new CTOperationResponse();
        try {
            response.response = doMain();
        } catch (SocketTimeoutException t) {
            response.error = new CTHttpError("Request Time Out.", 504);

        } catch (Throwable t) {
            if (!(t instanceof CTHttpError)) t.printStackTrace();
            response.error = t;
        }

        return response;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (isShowLoadingDialog && dialog.isShowing()) dialog.hideDialog();
    }

    @Override
    protected void onPostExecute(CTOperationResponse result) {
        isOperationRunning = false;
        activeOperations.remove(this.getClass().getName());
        if (requestID != null)
            activeOperationsMapByRequestId.remove(requestID);

        super.onPostExecute(result);
        try {
            if (isShowLoadingDialog && dialog != null && dialog.isShowing())
                dialog.hideDialog();
        } catch (Exception ex) {
            // ignore exception, as this happens sometimes
            ex.printStackTrace();

        }

        doOnPostExecute(result);
        // Wake observers with the result
        for (RequestObserver observer : observersList) {
            observer.handleRequestFinished(requestID, result.error, result.response);
        }
    }

    protected void doOnPostExecute(CTOperationResponse result) {

    }

    /**
     * Add Request Observer to List
     */
    public BaseOperation<T> addRequestObserver(RequestObserver requestObserver) {
        // remove the observer if it was already added here
        removeRequestObserver(requestObserver);
        // add to observers List
        observersList.add(requestObserver);
        return this;
    }

    /**
     * Remove Request Observer from the list
     */
    public void removeRequestObserver(RequestObserver requestObserver) {
        observersList.remove(requestObserver);
    }

    public boolean isShowLoadingDialog() {
        return isShowLoadingDialog;
    }

    /**
     * Setters & Getters
     */
    public void setShowLoadingDialog(boolean isShowLoadingDialog) {
        this.isShowLoadingDialog = isShowLoadingDialog;
    }

}