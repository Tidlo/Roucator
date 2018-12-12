package com.focjoe.roucator;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;

import static com.focjoe.roucator.util.MyApplication.CHANNEL_ID;


public class WifiInfoActivity extends AppCompatActivity {

    private static final String TAG = "wifi info activity";
    //views
    private Toolbar toolbar;
    private TextView textViewSsid;
    private TextView textViewMacAddress;
    private TextView textViewFrequencyType;

    private TextView textViewManufacture;
    private TextView textViewLinkChannel;
    private TextView textViewDistance;
    private TextView textViewCapility;
    private TextView textViewFrequencyBand;


    private Button buttonLocator;
    private Button buttonManage;
    private Button buttonConnect;

    //local variables
    private int wifiItemIndex;
    private WifiItem wifiItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_info);

        //使用 info_toolbar 取代 actionbar
        toolbar = findViewById(R.id.info_tool_bar);
        setSupportActionBar(toolbar);

        wifiItemIndex = getIntent().getIntExtra("item_index", 0);
        wifiItem = MyApplication.getWifiItemList().get(wifiItemIndex);

        //init views
        textViewSsid = findViewById(R.id.info_ssid);
        textViewMacAddress = findViewById(R.id.info_mac_address);
        textViewFrequencyType = findViewById(R.id.info_frequency_type);
        textViewManufacture = findViewById(R.id.info_manufacture);
        textViewLinkChannel = findViewById(R.id.info_link_channel);
        textViewDistance = findViewById(R.id.info_distance);
        textViewCapility = findViewById(R.id.info_capability);
        textViewFrequencyBand = findViewById(R.id.info_frequency_band);

        buttonLocator = findViewById(R.id.btn_test_locator);
        buttonManage = findViewById(R.id.btn_manage);
        buttonConnect = findViewById(R.id.btn_connect);

        //set views
        textViewSsid.setText(wifiItem.getSsid());
        textViewMacAddress.setText(wifiItem.getBSSID());
        textViewFrequencyType.setText(wifiItem.getInfoFrequencyType());
        textViewManufacture.setText(wifiItem.getInfoManufacture());
        textViewLinkChannel.setText(String.valueOf(wifiItem.getChannel()));
        textViewDistance.setText(wifiItem.getInfoDistance());
        textViewCapility.setText(wifiItem.getInfoCapility());
        textViewFrequencyBand.setText(wifiItem.getInfoFrequency());

        buttonLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiInfoActivity.this, LocatorActivity.class);
                intent.putExtra("item_index", wifiItemIndex);
                startActivity(intent);
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if already saved password to local
//                if(!wifiItem.getPassword().equals("")){
//                    // TODO: 2018/12/12 查本地数据库找得到数据的情况，找到数据后直接用
//                }else { // if haven't save password
//                    Dialog dialog = buildInputDialog();
//                    dialog.show();
//                }
            }
        });


//        if(wifiItem.getManageUrl() != null && !wifiItem.getManageUrl().equals("")){
//
//        }else {
//            buttonManage.setVisibility(View.INVISIBLE);
//        }

        buttonManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.baidu.com"));
                startActivity(intent);
            }
        });
    }

    private Dialog buildInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = inflater.inflate(R.layout.dialog_input, null);
        final EditText editTextSSID = view.findViewById(R.id.et_ssid);
        final EditText editTextPass = view.findViewById(R.id.et_password);
        final TextView textViewType = view.findViewById(R.id.et_capability);
        final ImageButton dropdown = view.findViewById(R.id.btn_capability_dropdown);
        LinearLayout layout = view.findViewById(R.id.layout_capability);

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
                            showSaveToLocalDialog();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WifiInfoActivity.this);
                            builder.setTitle(R.string.connect_fail)
                                    .setMessage(R.string.please_retry)
                                    .setPositiveButton(R.string.confirm, null)
                                    .show();
                        }


                        Intent intent = new Intent(WifiInfoActivity.this, QRCodeGenerateActivity.class);
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

    private void showSaveToLocalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_to_local)
                .setMessage(R.string.would_you_save_to_local)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

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
            case R.id.toolbar_menu_qr_code:
                intent = new Intent(WifiInfoActivity.this, QRCodeGenerateActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
