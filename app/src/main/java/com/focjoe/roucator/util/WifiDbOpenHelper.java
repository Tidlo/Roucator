package com.focjoe.roucator.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.focjoe.roucator.model.SavedWifiEntry;

public class WifiDbOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wifi_db.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SavedWifiEntry.TABLE_NAME + " (" +
                    SavedWifiEntry._ID + " INTEGER PRIMARY KEY, " +
                    SavedWifiEntry.COLUMN_NAME_SSID + " VARCHAR(30), " +
                    SavedWifiEntry.COLUMN_NAME_CAPABILITY + " VARCHAR(10), " +
                    SavedWifiEntry.COLUMN_NAME_PASSWORD + " VARCHAR(20)" +
                    ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SavedWifiEntry.TABLE_NAME;

    public WifiDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        initDb(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    private void initDb(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(SavedWifiEntry.COLUMN_NAME_SSID, "Johnny");
        values.put(SavedWifiEntry.COLUMN_NAME_CAPABILITY, "13939932213");
        values.put(SavedWifiEntry.COLUMN_NAME_PASSWORD, 21);

        db.insert(SavedWifiEntry.TABLE_NAME, null, values);

        db.execSQL("INSERT INTO person VALUES(NULL, ?,?,?)", new Object[]{"Fancy", "13322143324", 22});
    }
}
