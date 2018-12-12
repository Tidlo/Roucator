package com.focjoe.roucator;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focjoe.roucator.adapter.WifiItemAdapter;
import com.focjoe.roucator.model.VendorService;
import com.focjoe.roucator.model.VendorServiceFactory;
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
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,

    };
    private boolean mHasPermission;
    //    models
    private WifiManager wifiManager;
    private List<WifiItem> nearbyWifiList = new ArrayList<>();
    private List<ScanResult> scanResultList;
    private List<WifiConfiguration> wifiConfigurationList;
    private Scanner scanner;

    //    views
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton btnRefresh;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    // notifications
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //使用 main_toolbar 取代 actionbar
        toolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);

        //检查权限和申请权限
        mHasPermission = checkPermission();
        if (!mHasPermission) {
            requestPermission();
        }

        //recyclerView setup
        recyclerView = findViewById(R.id.listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);


        //swipeRefresh setup
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initScanner();
            }
        };
        swipeRefresh.setOnRefreshListener(listener);

        //refresh floating button setup
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
                //initScanner();
                wifiManager.startScan();
            }
        });

        //set up navigation view
        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_channel_rating:
                        intent = new Intent(MainActivity.this, ChannelRatingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_scan_qr_code:
                        intent = new Intent(MainActivity.this, QRCodeScanActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_generate_qr_code:
                        generateQRCode();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        //set up notification manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(MyApplication.CHANNEL_ID,
                    "Channel1", NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);
        }
        MyApplication.setNotificationManager(notificationManager);

        //initial refresh
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
            }
        });
        listener.onRefresh();
    }

    private void generateQRCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_data_source)
                .setCancelable(true)
                .setPositiveButton(R.string.manual_input, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog = buildInputDialog();
                        ((Dialog) dialog).show();
                    }
                })
                .setNegativeButton(R.string.select_from_exist, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog = buildSelectDialog();
                        ((Dialog) dialog).show();

                    }
                }).show();
    }

    private Dialog buildSelectDialog() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = inflater.inflate(R.layout.dialog_input, null);
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2018/12/12 show stored items
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setTitle(R.string.select);
        return builder.create();
    }

    private Dialog buildInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = inflater.inflate(R.layout.dialog_input, null);
        final EditText editTextSSID = view.findViewById(R.id.et_ssid);
        final EditText editTextPass = view.findViewById(R.id.et_password);
        final TextView textViewType = view.findViewById(R.id.et_capability);
        final ImageButton dropdown = view.findViewById(R.id.btn_capability_dropdown);
        final CheckBox checkBox = view.findViewById(R.id.checkBox_show_password);
        LinearLayout layout = view.findViewById(R.id.layout_capability);

        final String[] selectedType = {"nopass"};

        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, dropdown);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.capablity_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.capability_none:
                                textViewType.setText(R.string.none);
                                selectedType[0] = "nopass";
                                break;
                            case R.id.capability_wep:
                                textViewType.setText(R.string.wep);
                                selectedType[0] = "WEP";
                                break;
                            case R.id.capability_wpa:
                                textViewType.setText(R.string.wpa);
                                selectedType[0] = "WPA";
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        //set onclick listener for the whole line to popup a menu.
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdown.callOnClick();
            }
        });

        //set check listener for check box
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        //set onclick listener for buttons
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ssid = editTextSSID.getText().toString();
                        String pass = editTextPass.getText().toString();
                        Intent intent = new Intent(MainActivity.this, QRCodeGenerateActivity.class);
                        intent.putExtra("type", selectedType[0]);
                        intent.putExtra("ssid", ssid);
                        intent.putExtra("password", pass);
                        startActivity(intent);

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setTitle(R.string.input_information);
        return builder.create();
    }

    public void initScanner() {
        Log.d(TAG, "initScanner: ");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanner = new Scanner();
        wifiManager.startScan();
        registerReceiver(scanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    //重写下面的两个方法来使用自定义 main_toolbar 上的菜单，定义菜单的的点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private boolean checkConfigured(WifiItem item) {
        String ssid = item.getSsid();
        for (WifiConfiguration i : wifiConfigurationList) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                return true;
            }
        }
        return false;
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    private class Scanner extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            scanResultList = wifiManager.getScanResults();
            wifiConfigurationList = wifiManager.getConfiguredNetworks();

            int size = scanResultList.size();
            nearbyWifiList.clear();
            VendorService vendorService = VendorServiceFactory.makeVendorService(MyApplication.getContext().getResources());

            ScanResult result;
            String currentSsid = getCurrentSsid(MainActivity.this);
            boolean flag = true;
            for (int i = 0; i < size; i++) {

                result = scanResultList.get(i);

                WifiItem item = new WifiItem(result.SSID, result.BSSID, result.capabilities,
                        result.frequency, result.centerFreq0, result.centerFreq1, result.channelWidth, result.level);

                item.setInfoManufacture(vendorService.findVendorName(item.getBSSID()));
                item.setInfoDistance(String.format("%.2fm", LocatorActivity.calculateDistance(item)));
                item.setConfigured(checkConfigured(item));
                if (currentSsid != null && flag && currentSsid.equals("\"" + item.getSsid() + "\"")) {
                    MyApplication.currentConnectedWifiIndex[0] = i;
                    item.setConnected(true);
                    flag = false;
                }
                nearbyWifiList.add(item);
            }

            MyApplication.setWifiItemList(nearbyWifiList);
            WifiItemAdapter wifiItemAdapter = new WifiItemAdapter(nearbyWifiList);
            recyclerView.setAdapter(wifiItemAdapter);
            wifiItemAdapter.notifyDataSetChanged();

            //recyclerView 加载完毕后关闭刷新动画
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(false);
                }
            });
        }
    }

}
