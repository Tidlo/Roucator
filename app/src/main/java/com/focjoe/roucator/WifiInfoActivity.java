package com.focjoe.roucator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;


public class WifiInfoActivity extends AppCompatActivity {

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
