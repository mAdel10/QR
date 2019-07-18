package com.toastdemoapp.qrdemoapp.managers;

import  com.toastdemoapp.qrdemoapp.backend.models.User;
import  com.toastdemoapp.qrdemoapp.utilities.CachingManager;

public class UserManager {

    private static UserManager self;
    private final static String TAG = "UserManager";
    private User currentUser;

    public static UserManager getInstance() {
        if (self == null) {
            self = new UserManager();
        }
        return self;
    }

    private UserManager() {

        currentUser = CachingManager.getInstance().loadUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void saveUser(User currentUser) {
        if (currentUser == null)
            return;
        this.currentUser = currentUser;
        CachingManager.getInstance().saveUser(currentUser);
    }

    public void logout() {
        CachingManager.getInstance().deleteUser();
        currentUser = null;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
}
