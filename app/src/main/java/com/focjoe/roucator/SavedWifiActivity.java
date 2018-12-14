package com.focjoe.roucator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.focjoe.roucator.adapter.SavedWfiAdapter;
import com.focjoe.roucator.model.SavedWifi;
import com.focjoe.roucator.model.SavedWifiEntry;
import com.focjoe.roucator.util.WifiDbOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SavedWifiActivity extends AppCompatActivity {

    private static final String TAG = "SavedWifiActivity";
    SQLiteDatabase db;
    List<SavedWifi> savedWifiList;
    SavedWfiAdapter adapter;
    RecyclerView recyclerView;
    private WifiDbOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_wifi);
        recyclerView = findViewById(R.id.sved_wifi_recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

        dbOpenHelper = new WifiDbOpenHelper(this);
        db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(SavedWifiEntry.TABLE_NAME, null, null, null, null, null, null);
        savedWifiList = new ArrayList<>();
        // use queried data to create a list
        Log.d(TAG, "onCreate: cursor size:" + cursor.getCount());
        while (cursor.moveToNext()) {
            String ssid = cursor.getString(1);
            String capability = cursor.getString(2);
            String password = cursor.getString(3);
            savedWifiList.add(new SavedWifi(ssid, capability, password));
        }
        cursor.close();

        adapter = new SavedWfiAdapter(savedWifiList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {

        dbOpenHelper.close();
        super.onDestroy();
    }

}
