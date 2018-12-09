package com.focjoe.roucator.model;

public class ChannelInfo {
    String name;
    int number;
    int devices;
    int rating;
    boolean is2400;
    boolean is5000;
    int frequency;

    public ChannelInfo() {
    }


    public ChannelInfo(int number) {
        this.name = "Ch." + number;
        this.devices = 0;
        this.rating = 10;
        this.is2400 = number < 5000;
        this.is5000 = !this.is2400;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCount() {
        this.devices++;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDevices() {
        return devices;
    }

    public void setDevices(int devices) {
        this.devices = devices;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isIs2400() {
        return is2400;
    }

    public void setIs2400(boolean is2400) {
        this.is2400 = is2400;
    }

    public boolean isIs5000() {
        return is5000;
    }

    public void setIs5000(boolean is5000) {
        this.is5000 = is5000;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
