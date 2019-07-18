package com.toastdemoapp.qrdemoapp.backend.operations;

import android.content.Context;

import com.toastdemoapp.qrdemoapp.backend.api.OperationsManager;
import com.toastdemoapp.qrdemoapp.backend.models.Qr;
import com.toastdemoapp.qrdemoapp.backend.models.QrForm;
import com.toastdemoapp.qrdemoapp.backend.models.SignForm;
import com.toastdemoapp.qrdemoapp.backend.models.User;
import com.toastdemoapp.qrdemoapp.managers.UserManager;

public class ScanQrOperation extends BaseOperation<Qr> {

    private QrForm qrForm;

    public ScanQrOperation(QrForm qrForm, Object requestID, boolean isShowLoadingDialog, Context activity) {
        super(requestID, isShowLoadingDialog, activity);
        this.qrForm = qrForm;
    }

    @Override
    public Qr doMain() throws Throwable {
        return OperationsManager.getInstance().doScanQr(qrForm);
    }
}
