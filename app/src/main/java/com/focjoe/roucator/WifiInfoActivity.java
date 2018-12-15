package com.focjoe.roucator;

import android.app.Dialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.model.SavedWifiEntry;
import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;
import com.focjoe.roucator.util.WifiDbOpenHelper;

import java.util.List;

import static com.focjoe.roucator.util.MyApplication.CHANNEL_ID;


public class WifiInfoActivity extends AppCompatActivity {

    private static final String TAG = "wifi info activity";
    //views
    private Toolbar toolbar;
    private TextView textViewSsid;
    private TextView textViewMacAddress;
    private ImageView configured;
    private ImageView signalBar;

    private ImageView freqIcon;
    private ImageView savedIcon;

    private TextView textViewManufacture;
    private TextView textViewLinkChannel;
    private TextView textViewDistance;
    private TextView textViewCapility;
    private TextView textViewFrequencyBand;


    private Button buttonLocator;
    private Button buttonManage;
    private Button buttonConnect;
    private FloatingActionButton fabRefresh;

    //local variables
    private int wifiItemIndex;
    private WifiItem wifiItem;
    private WifiDbOpenHelper dbOpenHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    private WifiManager wifiManager;
    private Scanner scanner;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_info);

        //use info_toolbar replace actionbar
        initToolbar();

        wifiItemIndex = getIntent().getIntExtra("item_index", 0);
        wifiItem = MyApplication.getWifiItemList().get(wifiItemIndex);

        //init views
        textViewSsid = findViewById(R.id.info_wifi_ssid);
        textViewMacAddress = findViewById(R.id.info_wifi_mac);

        freqIcon = findViewById(R.id.info_wifi_token);
        signalBar = findViewById(R.id.info_wifi_signal_bar);
        configured = findViewById(R.id.info_wifi_configured);
        savedIcon = findViewById(R.id.info_wifi_saved);

        textViewManufacture = findViewById(R.id.info_manufacture);
        textViewLinkChannel = findViewById(R.id.info_link_channel);
        textViewDistance = findViewById(R.id.info_distance);
        textViewCapility = findViewById(R.id.info_capability);
        textViewFrequencyBand = findViewById(R.id.info_frequency_band);

        buttonLocator = findViewById(R.id.btn_test_locator);
        buttonManage = findViewById(R.id.btn_manage);
        buttonConnect = findViewById(R.id.btn_connect);
        fabRefresh = findViewById(R.id.btn_refresh_info);

        //set views
        if (wifiItem.isConfigured()) {
            configured.setImageResource(R.drawable.round_lock_open_black_36);
        }

        if (wifiItem.isSaved()) {
            savedIcon.setImageResource(R.drawable.round_save_black_36);
        }

        if (wifiItem.getInfoFrequencyType().equals("5G")) {
            freqIcon.setImageResource(R.drawable.m5g_token);
        }

        int percentage = wifiItem.getPercentage();
        if (percentage < 25) {
            signalBar.setImageResource(R.drawable.round_signal_wifi_1_bar_black_48);
        } else if (percentage < 50) {
            signalBar.setImageResource(R.drawable.round_signal_wifi_2_bar_black_48);
        } else if (percentage < 75) {
            signalBar.setImageResource(R.drawable.round_signal_wifi_3_bar_black_48);
        } else {
            signalBar.setImageResource(R.drawable.round_signal_wifi_4_bar_black_48);
        }


        textViewSsid.setText(wifiItem.getSsid());
        textViewMacAddress.setText(wifiItem.getBSSID());
        textViewManufacture.setText(wifiItem.getInfoManufacture());
        textViewLinkChannel.setText(String.valueOf(wifiItem.getChannel()));
        textViewDistance.setText(wifiItem.getInfoDistance());
        textViewCapility.setText(wifiItem.getInfoCapility());
        textViewFrequencyBand.setText(wifiItem.getInfoFrequency());

        //get db
        dbOpenHelper = new WifiDbOpenHelper(this);
        database = dbOpenHelper.getReadableDatabase();

        //set connect button's visibility
        //set manage button's visibility
        if (wifiItem.isConnected()) {
            buttonConnect.setVisibility(View.INVISIBLE);
        } else {
            buttonManage.setVisibility(View.INVISIBLE);
        }


        buttonLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiInfoActivity.this, LocatorActivity.class);
                intent.putExtra("item_index", wifiItemIndex);
                startActivity(intent);
            }
        });

        cursor = database.query(SavedWifiEntry.TABLE_NAME, null, SavedWifiEntry.COLUMN_NAME_SSID + " = '" + wifiItem.getSsid() + "'", null, null, null, null);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if already saved password to local
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    final String ssid = cursor.getString(1);
                    String type = cursor.getString(2);
                    String pass = cursor.getString(3);

                    Log.d(TAG, "onClick: saved:" + cursor.getString(4));
                    // if successful configured network
                    if (configSucceed(type, ssid, pass)) {
                        sendNotification(ssid);
                        MyApplication.getWifiItemList().get(wifiItemIndex).setConnected(true);
                        int curCon = MyApplication.currentConnectedWifiIndex[0];
                        MyApplication.getWifiItemList().get(curCon).setConnected(false);
                    } else {
                        // local record is not match.
                        AlertDialog.Builder builder = new AlertDialog.Builder(WifiInfoActivity.this);
                        builder.setTitle(R.string.connect_fail)
                                .setMessage("此无线网络的认证方式可能已发生改变，是否删除本地已保存的WiFi记录")
                                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        database.execSQL("DELETE FROM wifi WHERE ssid=?", new Object[]{ssid});
                                        // TODO: 2018/12/12 使用 SnackBar 代替 Toast 
                                        Toast.makeText(WifiInfoActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    }

                } else { // if haven't save password
                    Dialog dialog = buildInputDialog();
                    dialog.show();
                }
            }
        });


        // a simple implement, need to establish a database to store all vendors manage url.
        buttonManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(wifiItem.getManageUrl()));
                startActivity(intent);
            }
        });

        //set up scanner
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanner = new WifiInfoActivity.Scanner();
        registerReceiver(scanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiManager.startScan();
            }
        });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.info_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * This dialog shows up when there is no information saved locally for the wifi connected to.
     *
     * @return
     */
    private Dialog buildInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = inflater.inflate(R.layout.dialog_input, null);
        final EditText editTextSSID = view.findViewById(R.id.et_ssid);
        final EditText editTextPass = view.findViewById(R.id.et_password);
        final TextView textViewType = view.findViewById(R.id.et_capability);
        final ImageButton dropdown = view.findViewById(R.id.btn_capability_dropdown);
        LinearLayout layout = view.findViewById(R.id.layout_capability);
        final CheckBox checkBox = view.findViewById(R.id.checkBox_show_password);

        editTextSSID.setText(wifiItem.getSsid());
        editTextSSID.setEnabled(false);

        final String[] selectedType = {"nopass"};

        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(WifiInfoActivity.this, dropdown);
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
                        String type = selectedType[0];

                        if (configSucceed(type, ssid, pass)) {
                            sendNotification(ssid);
                            showSaveToLocalDialog(type, ssid, pass);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WifiInfoActivity.this);
                            builder.setTitle(R.string.connect_fail)
                                    .setMessage(R.string.please_retry)
                                    .setPositiveButton(R.string.confirm, null)
                                    .show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setTitle(R.string.input_information);
        return builder.create();
    }

    /**
     * show a dialog to confirm whether save the wifi information to local data base.
     * @param type
     * @param ssid
     * @param pass
     */
    private void showSaveToLocalDialog(final String type, final String ssid, final String pass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_to_local)
                .setMessage(R.string.would_you_save_to_local)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.execSQL("INSERT INTO wifi VALUES(NULL, ?,?,?)", new Object[]{ssid, type, pass});
                        Toast.makeText(WifiInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * configure a wifi and return result
     * @param type
     * @param networkSSID
     * @param networkPass
     * @return true when success
     */
    private boolean configSucceed(String type, String networkSSID, String networkPass) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quo
        if (type.equals("WEP")) {
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (type.equals("WPA")) {
            conf.preSharedKey = "\"" + networkPass + "\"";
        } else if (type.equals("nopass")) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int networkId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        if (wifiManager.enableNetwork(networkId, true)) {
            wifiManager.reconnect();
            return true;
        } else {
            wifiManager.removeNetwork(networkId);
            return false;
        }
    }


    /**
     * Send notification when successful connected to ssid
     * @param ssid
     */
    private void sendNotification(String ssid) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notifyBuilder.setContentTitle("Roucator")
                .setContentText("已连接到 " + ssid + "")
                .setSmallIcon(R.drawable.round_signal_wifi_4_bar_black_18);

        notifyBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        notifyBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

        Notification notification = notifyBuilder.build();

        MyApplication.getNotificationManager().notify(0, notification);
    }


    //重写下面的两个方法来使用自定义 main_toolbar 上的菜单，定义菜单的的点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.toolbar_menu_upload:
                Toast.makeText(this, "You clicked upload", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.toolbar_menu_qr_code:
//                intent = new Intent(WifiInfoActivity.this, QRCodeGenerateActivity.class);
//                startActivity(intent);
//                break;
            default:
                break;
        }
        return false;
    }

    private class Scanner extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            int size = scanResultList.size();
            Log.d(TAG, "onReceive: size of result" + size);

            //find target wifi
            for (ScanResult result :
                    scanResultList) {
                if (result.BSSID.equals(wifiItem.getBSSID())) {
                    wifiItem = new WifiItem(result.SSID, result.BSSID, result.capabilities,
                            result.frequency, result.centerFreq0, result.centerFreq1, result.channelWidth, result.level);

                    int percentage = wifiItem.getPercentage();
                    if (percentage < 25) {
                        signalBar.setImageResource(R.drawable.round_signal_wifi_1_bar_black_48);
                    } else if (percentage < 50) {
                        signalBar.setImageResource(R.drawable.round_signal_wifi_2_bar_black_48);
                    } else if (percentage < 75) {
                        signalBar.setImageResource(R.drawable.round_signal_wifi_3_bar_black_48);
                    } else {
                        signalBar.setImageResource(R.drawable.round_signal_wifi_4_bar_black_48);
                    }

                    textViewLinkChannel.setText(String.valueOf(wifiItem.getChannel()));
                    textViewDistance.setText(String.format("%.2fm", LocatorActivity.calculateDistance(wifiItem)));
                    textViewFrequencyBand.setText(wifiItem.getInfoFrequency());
                    Toast.makeText(context, "刷新完毕", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}
