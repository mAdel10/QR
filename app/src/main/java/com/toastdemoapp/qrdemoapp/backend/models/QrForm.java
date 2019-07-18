package com.toastdemoapp.qrdemoapp.backend.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QrForm implements Serializable {

    @SerializedName("username")
    @Expose
    private String userName;

    @SerializedName("qrScan")
    @Expose
    private String qrScan;


    public QrForm(String userName, String password) {
        this.userName = userName;
        this.qrScan = password;
    }


    public QrForm() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQrScan() {
        return qrScan;
    }

    public void setQrScan(String password) {
        this.qrScan = password;
    }
}
