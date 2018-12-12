package com.focjoe.roucator.model;

import static com.focjoe.roucator.util.Tools.calculatePercentage;
import static com.focjoe.roucator.util.Tools.frequencyToChannel;

public class WifiItem {
    private String ssid;
    private String BSSID;
    private String capabilities;
    private int frequency;
    private int channel;
    private int centerFreq0;
    private int centerFreq1;
    private int channelWidth;
    private int level;

    //showed in card
    private boolean configured;
    private boolean saved;
    private int percentage;
    private boolean connected;

    //attributes for info activity
    private String manageUrl;
    private String infoFrequencyType;
    private String infoManufacture;
    private String infoLinkSpeed;
    private String infoDistance;
    private String infoCapility;
    private String infoFrequency;


    public WifiItem() {
        this.manageUrl = "";
        this.BSSID = "BSSID need implement";
        this.infoFrequencyType = "infoFrequencyType need implement";
        this.infoManufacture = "infoManufacture need implement";
        this.infoLinkSpeed = "infoLinkSpeed need implement";
        this.infoDistance = "infoDistance need implement";
        this.infoCapility = "infoCapility need implement";
        this.infoFrequency = "infoFrequency need implement";
    }

    public WifiItem(String ssid, String BSSID, String capabilities, int frequency, int centerFreq0, int centerFreq1, int channelWidth, int level) {
        this.ssid = ssid;
        this.BSSID = BSSID;
        this.capabilities = capabilities;
        this.frequency = frequency;
        this.channel = frequencyToChannel(this.frequency);
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.level = level;
        this.saved = false;
        this.connected = false;

        this.manageUrl = "http://192.168.1.1";
        this.infoFrequencyType = frequency > 5000 ? "5G" : "2.4G";
        this.infoManufacture = "infoManufacture need implement";
        this.infoLinkSpeed = "infoLinkSpeed need implement";
        this.infoDistance = "infoDistance need implement";
        this.infoCapility = "infoCapility need implement";
        this.infoFrequency = "infoFrequency need implement";
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getSignalStrengthIndB() {
        return this.level;
    }

    public void setSignalStrengthIndB(int signalStrengthIndB) {
        this.level = signalStrengthIndB;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getManageUrl() {
        return manageUrl;
    }

    public void setManageUrl(String manageUrl) {
        this.manageUrl = manageUrl;
    }

    public int getPercentage() {
        return calculatePercentage(this.level);
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getInfoFrequencyType() {
        return infoFrequencyType;
    }

    public void setInfoFrequencyType(String infoFrequencyType) {
        this.infoFrequencyType = infoFrequencyType;
    }

    public String getInfoManufacture() {
        return infoManufacture;
    }

    public void setInfoManufacture(String infoManufacture) {
        this.infoManufacture = infoManufacture;
    }

    public String getInfoLinkSpeed() {
        return infoLinkSpeed;
    }

    public void setInfoLinkSpeed(String infoLinkSpeed) {
        this.infoLinkSpeed = infoLinkSpeed;
    }

    public String getInfoDistance() {
        return infoDistance;
    }

    public void setInfoDistance(String infoDistance) {
        this.infoDistance = infoDistance;
    }

    public String getInfoCapility() {
        return capabilities;
    }

    public void setInfoCapility(String infoCapility) {
        this.infoCapility = infoCapility;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getInfoFrequency() {
        return String.valueOf(frequency);
    }

    public void setInfoFrequency(String infoFrequency) {
        this.infoFrequency = infoFrequency;
    }

    public int getCenterFreq0() {
        return centerFreq0;
    }

    public void setCenterFreq0(int centerFreq0) {
        this.centerFreq0 = centerFreq0;
    }

    public int getCenterFreq1() {
        return centerFreq1;
    }

    public void setCenterFreq1(int centerFreq1) {
        this.centerFreq1 = centerFreq1;
    }

    public int getChannelWidth() {
        return channelWidth;
    }

    public void setChannelWidth(int channelWidth) {
        this.channelWidth = channelWidth;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
