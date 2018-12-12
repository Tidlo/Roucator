package com.focjoe.roucator.model;

public class SavedWifi {
    int _id;
    String ssid;
    String password;
    String capability;

    public SavedWifi(String ssid, String password, String capability) {
        this.ssid = ssid;
        this.password = password;
        this.capability = capability;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }
}
