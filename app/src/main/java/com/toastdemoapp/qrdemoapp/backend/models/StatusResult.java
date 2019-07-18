package com.toastdemoapp.qrdemoapp.backend.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusResult implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("User")
    @Expose
    private User user;

    @SerializedName("message")
    @Expose
    private String message;

    public StatusResult(boolean status, User user , String message) {
        this.status = status;
        this.user = user;
        this.message = message;
    }

    public StatusResult() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}