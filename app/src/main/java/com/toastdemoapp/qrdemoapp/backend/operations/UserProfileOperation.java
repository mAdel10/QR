package com.toastdemoapp.qrdemoapp.backend.operations;//package com.toastdemoapp.myapplication.backend.operations;
//
//import android.content.Context;
//
//import com.lmsllcdrdapp.lms.backend.api.OperationsManager;
//import com.lmsllcdrdapp.lms.backend.models.User;
//import com.lmsllcdrdapp.lms.managers.UserManager;
//
//
//public class UserProfileOperation extends BaseOperation<User> {
//
//
//    public UserProfileOperation(Object requestID, boolean isShowLoadingDialog, Context activity) {
//        super(requestID, isShowLoadingDialog, activity);
//    }
//
//    @Override
//    public User doMain() throws Throwable {
//        User user = OperationsManager.getInstance().doUserProfile();
//        UserManager.getInstance().saveUser(user);
//        return user;
//    }
//}
