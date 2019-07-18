package com.toastdemoapp.qrdemoapp.backend.operations;

import android.content.Context;


import com.toastdemoapp.qrdemoapp.backend.api.OperationsManager;
import com.toastdemoapp.qrdemoapp.backend.models.SignForm;
import com.toastdemoapp.qrdemoapp.backend.models.StatusResult;
import com.toastdemoapp.qrdemoapp.managers.UserManager;


public class UserSignInOperation extends BaseOperation<StatusResult> {

    private SignForm signForm;

    public UserSignInOperation(SignForm signForm, Object requestID, boolean isShowLoadingDialog, Context activity) {
        super(requestID, isShowLoadingDialog, activity);
        this.signForm = signForm;
    }

    @Override
    public StatusResult doMain() throws Throwable {
        StatusResult status = OperationsManager.getInstance().doUserSignIn(signForm);
        UserManager.getInstance().saveUser(status.getUser());
        return status;
    }
}
