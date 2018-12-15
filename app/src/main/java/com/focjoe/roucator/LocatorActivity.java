package com.focjoe.roucator;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.draw.DrawView;
import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;

import java.util.List;

public class LocatorActivity extends AppCompatActivity {

    private static final String TAG = "Locator";
    Color targetColor = Color.valueOf(-13335291);
    int xTarget = 0;
    int yTarget = 0;
    private List<ScanResult> scanResultList;
    private WifiManager wifiManager;
    private String BSSID;
    private WifiItem targetWifi;
    private WifiItem wifiItem;
    private Toolbar toolbar;
    private TextView test_ssid;
    private TextView test_strength;
    private TextView test_distance;
    private Button btn_measure;
    private DrawView mDrawView;
    private EditText editText;
    private Scanner scanner;
    private AlertDialog.Builder dialog;
    private int size;
    private double distance = 0.0;
    private int measureCount = 1;
    private int xPos = 300;
    private int yPos = 600;
    private int xOffset = 0;
    private int yOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        toolbar = findViewById(R.id.locator_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        test_ssid = findViewById(R.id.locator_ssid);
        test_strength = findViewById(R.id.locator_strength);
        test_distance = findViewById(R.id.distance);
        btn_measure = findViewById(R.id.btn_calc_distance);
        editText = findViewById(R.id.text_input_distance);
        mDrawView = findViewById(R.id.draw_view);

        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.setVisibility(View.INVISIBLE);

        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Oops!");
        dialog.setMessage("您输入的数据有误，请重新开始测试");
        dialog.setCancelable(false);
        dialog.setPositiveButton("重新选择测试Wi-Fi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LocatorActivity.this, LocatorActivity.class);
                LocatorActivity.this.startActivity(intent);
            }
        });

        measureCount = 1;


        targetWifi = new WifiItem();


        wifiItem = MyApplication
                .getWifiItemList()
                .get(getIntent().getIntExtra("item_index", 0));
        targetWifi.setBSSID(wifiItem.getBSSID());
        targetWifi.setSsid(wifiItem.getSsid());

        initScanner();

        btn_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScanner();
                if (measureCount == 1) {
                    mDrawView.drawCircle(xPos, yPos, distance, measureCount);
                    editText.setHint("向右移动的距离");
                    editText.setVisibility(View.VISIBLE);
                } else if (measureCount == 2) {
                    String string = editText.getText().toString();
                    if (string.equals("")) {
                        dialog.show();
                    } else {
                        xOffset = Integer.parseInt(string);
                    }
                    xPos = xPos + (xOffset * 20);
                    mDrawView.drawCircle(xPos, yPos, distance, measureCount);
                    editText.setHint("向前移动的距离");
                    editText.clearFocus();
                    editText.setText("");
                } else if (measureCount == 3) {
                    String string = editText.getText().toString();

                    if (string.equals("")) {
                        dialog.show();
                    } else {
                        yOffset = Integer.parseInt(string);
                    }
                    yPos = yPos - (yOffset * 20);
                    mDrawView.setxOriginal(xPos);
                    mDrawView.setyOriginal(yPos);
                    mDrawView.drawCircle(xPos, yPos, distance, measureCount);
                    editText.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = mDrawView.getBitmap();
                    Log.d(TAG, "onClick: target color" + bitmap.getPixel(495, 552));
                    if (bitmap != null) {
                        Color pixel;
                        outer:
                        for (int x = 50; x < bitmap.getWidth(); x += 10) {
                            for (int y = 50; y < bitmap.getHeight(); y += 10) {
                                pixel = Color.valueOf(bitmap.getPixel(x, y));
                                Log.d(TAG, "onClick: now pixel: " + pixel.toArgb() + " now target color: " + targetColor.toArgb());
                                if (pixel.equals(targetColor)) {
                                    xTarget = x + 50;
                                    yTarget = y + 50;
                                    mDrawView.setxDestination(xTarget);
                                    mDrawView.setyDestination(yTarget);
                                    break outer;
                                }
                            }
                        }

                    }
                } else if (measureCount > 3) {
                    mDrawView.drawCircle(xTarget, yTarget, distance, measureCount);
                    if (xTarget == 0 && yTarget == 0) {
                        dialog.show();
                    }
                }
                measureCount++;
            }
        });

    }


    public void initScanner() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanner = new Scanner();
        wifiManager.startScan();
        registerReceiver(scanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }


    public static double calculateDistance(WifiItem targetWifi) {
        double exp = (27.55 - (20 * Math.log10(targetWifi.getFrequency())) + Math.abs(targetWifi.getSignalStrengthIndB())) / 20.0;
//        return Math.pow(10.0, exp);
        return Math.pow(10.0, exp);
    }

    private class Scanner extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanResultList = wifiManager.getScanResults();
            size = scanResultList.size();

            Log.d(TAG, "onReceive: size of result" + size);

            //find target wifi

            boolean flag = true;
            for (ScanResult i :
                    scanResultList) {
                if (i.BSSID.equals(targetWifi.getBSSID())) {
                    targetWifi.setSignalStrengthIndB(i.level);
                    targetWifi.setFrequency(i.frequency);
                    flag = false;
                }
            }

            if (flag) {
                Toast.makeText(context, "失去与此 Wifi 的连接", Toast.LENGTH_SHORT).show();
            } else {
                distance = calculateDistance(targetWifi);
                test_distance.setText(String.format("距离：%.2f m", distance));
                test_ssid.setText(targetWifi.getSsid());
                test_strength.setText(String.format("信号强度：%d dBm", targetWifi.getSignalStrengthIndB()));
            }
        }

    }

}
