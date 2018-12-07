package com.focjoe.roucator.model;

public class WifiItem {
    private String ssid;
    private int signalStrengthIndB;
    private int freequency;
    private String capabilities;
    private int channel;
    private int percentage;

    public WifiItem() {
    }

    public WifiItem(String ssid, int signalStrengthIndB, int frequency) {
        this.freequency = frequency;
        this.signalStrengthIndB = signalStrengthIndB;
        this.ssid = ssid;
    }

    public int getFrequency() {
        return freequency;
    }

    public void setFrequency(int frequency) {
        this.freequency = frequency;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getSignalStrengthIndB() {
        return signalStrengthIndB;
    }

    public void setSignalStrengthIndB(int signalStrengthIndB) {
        this.signalStrengthIndB = signalStrengthIndB;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getChannel() {
        channel = frequencyToChannel(String.format("%d", this.freequency));
        return channel;
    }

    public int getPercentage() {
        return calculatePercentage(this.signalStrengthIndB);
    }


    public int calculatePercentage(int level) {
        int lev = 0;
        if (level > -30)
            lev = -30;
        else if (level < -90)
            lev = -89;
        else
            lev = level;

        int percentge;
        percentge = (int) (Math.abs((float) ((lev + 90) * 99 / (-60))));
        return percentge;
    }

    int frequencyToChannel(String frequency) {
        switch (frequency) {
            case "2412":
                return 1;
            case "2417":
                return 2;
            case "2422":
                return 3;
            case "2427":
                return 4;
            case "2432":
                return 5;
            case "2437":
                return 6;
            case "2442":
                return 7;
            case "2447":
                return 8;
            case "2452":
                return 9;
            case "2457":
                return 10;
            case "2462":
                return 11;
            case "2467":
                return 12;
            case "2472":
                return 13;
            case "2484":
                return 14;
            case "5035":
                return 7;
            case "5040":
                return 8;
            case "5045":
                return 9;
            case "5055":
                return 11;
            case "5060":
                return 12;
            case "5080":
                return 16;
            case "5170":
                return 34;
            case "5180":
                return 36;
            case "5190":
                return 38;
            case "5200":
                return 40;
            case "5210":
                return 42;
            case "5220":
                return 44;
            case "5230":
                return 46;
            case "5240":
                return 48;
            case "5260":
                return 52;
            case "5280":
                return 56;
            case "5300":
                return 60;
            case "5320":
                return 64;
            case "5500":
                return 100;
            case "5520":
                return 104;
            case "5540":
                return 108;
            case "5560":
                return 112;
            case "5580":
                return 116;
            case "5600":
                return 120;
            case "5620":
                return 124;
            case "5640":
                return 128;
            case "5660":
                return 132;
            case "5680":
                return 136;
            case "5700":
                return 140;
            case "5720":
                return 144;
            case "5745":
                return 149;
            case "5765":
                return 153;
            case "5785":
                return 157;
            case "5805":
                return 161;
            case "5825":
                return 165;
            case "4915":
                return 183;
            case "4920":
                return 184;
            case "4925":
                return 185;
            case "4935":
                return 187;
            case "4940":
                return 188;
            case "4945":
                return 189;
            case "4960":
                return 192;
            case "4980":
                return 196;
            default:
                return -1;
        }
    }

}
