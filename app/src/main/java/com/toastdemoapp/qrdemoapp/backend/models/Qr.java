package com.toastdemoapp.qrdemoapp.backend.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Qr implements Serializable {

    @SerializedName("isValid")
    @Expose
    private String isValid;

    @SerializedName("gateNum")
    @Expose
    private int gateNum;


    public Qr(String userName, int password) {
        this.isValid = userName;
        this.gateNum = password;
    }


    public Qr() {

    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String userName) {
        this.isValid = userName;
    }

    public int getGateNum() {
        return gateNum;
    }

    public void setGateNum(int password) {
        this.gateNum = password;
    }
}
