package com.focjoe.roucator.model;

public class SavedWifi {

    public SavedWifi(String ssid, String capability, String password) {
        this.ssid = ssid;
        this.capability = capability;
        this.password = password;
    }

    int _id;
    String ssid; // wifi name
    String capability; // wifi capability
    String password; // password

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
