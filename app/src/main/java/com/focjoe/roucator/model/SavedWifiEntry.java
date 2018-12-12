package com.focjoe.roucator.model;

import android.provider.BaseColumns;

public class SavedWifiEntry implements BaseColumns {
    public static final String TABLE_NAME = "wifi";
    public static final String _ID = "_id";
    public static final String COLUMN_NAME_SSID = "ssid";
    public static final String COLUMN_NAME_CAPABILITY = "capability";
    public static final String COLUMN_NAME_PASSWORD = "password";
}
