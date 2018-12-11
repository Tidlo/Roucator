package com.focjoe.roucator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRCodeScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String CHANNEL_ID = "channel_0";
    private static final int NOTIFICATION_ID = 0;
    String TAG = "ZXING";
    NotificationManager notificationManager;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel1", NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        //stop camera scan while got result
        mScannerView.stopCamera();
        String wifiElement[] = rawResult.getText().replace("WIFI:", "").split(";");

        Log.d(TAG, "handleResult: wifi element 0:" + wifiElement[0]);
        Log.d(TAG, "handleResult: wifi element 1:" + wifiElement[1]);
        Log.d(TAG, "handleResult: wifi element 2:" + wifiElement[2]);
        final String ssid = getParam(wifiElement[0]);
        final String type = getParam(wifiElement[1]);
        final String pass = getParam(wifiElement[2]);

        //build a dialog to verify the connection
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.confirm_connection)
                .setMessage("确定要连接到\"" + ssid + "\"吗？")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectToWifi(type, ssid, pass);
                        // TODO: 2018/12/11 notification of connected wifi
                        sendNotification(ssid);

                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScannerView.startCamera();
                    }
                });
        dialog.show();

//        mScannerView.resumeCameraPreview(this);
    }

    public void connectToWifi(String type, String networkSSID, String networkPass) {

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

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        // check whether already have the same-name configuration
        for (WifiConfiguration i : list) {

            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                Log.d(TAG, "connectToWifi: Found_Exist_Wificonfig_ssid:" + i.SSID);
                wifiManager.removeNetwork(i.networkId);
                break;
            }
        }


        wifiManager.addNetwork(conf);
        list = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration i : list) {
            Log.d(TAG, "connectToWifi: Wificonfig_ssid:" + i.SSID);
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                Log.d(TAG, "connectToWifi: Found_Wificonfig_ssid:" + i.SSID);
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    private String getParam(String value) {
        String[] splited = value.split(":");
        //has password
        if (splited.length > 1) {
            return splited[1];
        }
        //no password
        return "";
    }

    private void sendNotification(String ssid) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notifyBuilder.setContentTitle("Roucator")
                .setContentText("已连接到 " + ssid + "")
                .setSmallIcon(R.drawable.round_signal_wifi_4_bar_black_18);

        notifyBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        notifyBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

        Notification notification = notifyBuilder.build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
