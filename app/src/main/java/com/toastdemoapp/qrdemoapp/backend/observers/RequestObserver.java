package com.toastdemoapp.qrdemoapp.backend.observers;

public interface RequestObserver {

    /**
     * This method is called by OperationExecuteQueue class only. It must not called by any Request implementor. Method
     * is called to send event to specific request observer telling that request has been completed. Method is called in
     * case of any exception (un-handled or business) Method not called If request is cancelled.
     *
     * @param requestId    Object
     * @param error        Throwable
     * @param resultObject Object
     */
    void handleRequestFinished(Object requestId, Throwable error, Object resultObject);

    void requestCanceled(Integer requestId, Throwable error);

    void updateStatus(Integer requestId, String statusMsg);
}
