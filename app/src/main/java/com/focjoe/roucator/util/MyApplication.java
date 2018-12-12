package com.focjoe.roucator.util;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.focjoe.roucator.model.WifiItem;

import java.util.List;

public class MyApplication extends Application {
    private static Context context;
    private static List<WifiItem> wifiItemList;
    public static final String CHANNEL_ID = "channel_0";
    private static NotificationManager notificationManager;


    public static Context getContext() {
        return context;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static void setNotificationManager(NotificationManager notificationManager) {
        MyApplication.notificationManager = notificationManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static List<WifiItem> getWifiItemList() {
        return wifiItemList;
    }

    public static void setWifiItemList(List<WifiItem> wifiItemList) {
        MyApplication.wifiItemList = wifiItemList;
    }
}
