package com.focjoe.roucator;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.focjoe.roucator.util.MyApplication;
import com.google.zxing.Result;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.focjoe.roucator.util.MyApplication.CHANNEL_ID;


public class QRCodeScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    String TAG = "ZXING";

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);


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
                        if (connectToWifi(type, ssid, pass)) {
                            sendNotification(ssid);
                        } else {
                            Toast.makeText(QRCodeScanActivity.this, R.string.connect_fail, Toast.LENGTH_SHORT).show();
                        }

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

    public boolean connectToWifi(String type, String networkSSID, String networkPass) {

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

        MyApplication.getNotificationManager().notify(0, notification);
    }
}
