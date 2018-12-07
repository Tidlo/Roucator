package com.focjoe.roucator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.focjoe.roucator.adapter.WifiItemAdapter;
import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private boolean mHasPermission;
    //    models
    private WifiManager wifiManager;
    private List<WifiItem> nearbyWifiList = new ArrayList<>();
    private List<ScanResult> scanResultList;
    private Scanner scanner;

    //    views
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton btnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        使用 toolbar 取代 actionbar
        toolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);

        //检查权限和申请权限
        mHasPermission = checkPermission();
        if (!mHasPermission) {
            requestPermission();
        }

//        recyclerView setup
        recyclerView = findViewById(R.id.listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

//        swipeRefresh setup
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initScanner();
            }
        };
        swipeRefresh.setOnRefreshListener(listener);

//        refresh floationg button setup
        btnRefresh = findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: refresh button");
                swipeRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(true);
                    }
                });
//                initScanner();
                wifiManager.startScan();
            }
        });


//        initial refresh
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
            }
        });
        listener.onRefresh();
    }

    public void initScanner() {
        Log.d(TAG, "initScanner: ");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanner = new Scanner();
        wifiManager.startScan();
        registerReceiver(scanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    //重写下面的两个方法来使用自定义 toolbar 上的菜单，定义菜单的的点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_refresh:
                Toast.makeText(this, "You clicked refresh", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 检查是否已经授予权限
     *
     * @return
     */
    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;
                    break;
                }
            }
        }
    }

    private class Scanner extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Start.");
            scanResultList = wifiManager.getScanResults();
            int size = scanResultList.size();
            Log.d(TAG, "onReceive: scanlist size " + size);
            nearbyWifiList.clear();
            for (int i = 0; i < size; i++) {
                WifiItem item = new WifiItem();
                item.setSsid(scanResultList.get(i).SSID);
                item.setFrequency(scanResultList.get(i).frequency);
                item.setSignalStrengthIndB(scanResultList.get(i).level);
                item.setCapabilities(scanResultList.get(i).capabilities);

//                Log.d(TAG, "onReceive: item's capabilities" + item.getCapabilities());
                nearbyWifiList.add(item);
            }

            MyApplication.setWifiItemList(nearbyWifiList);
            Log.d(TAG, "onReceive: Updated global wifi item list.");
            WifiItemAdapter wifiItemAdapter = new WifiItemAdapter(nearbyWifiList);
            recyclerView.setAdapter(wifiItemAdapter);
            wifiItemAdapter.notifyDataSetChanged();
//            recyclerView 加载完毕后关闭刷新动画
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(false);
                }
            });


        }
    }

}
