package com.focjoe.roucator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WifiInfoActivity extends AppCompatActivity {

    private TextView textViewSsid;
    private Button buttonLocator;
    private int wifiItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_info);

        wifiItemIndex = getIntent().getIntExtra("item_index", 0);

        textViewSsid = findViewById(R.id.info_ssid);
        buttonLocator = findViewById(R.id.btn_test_locator);
        buttonLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiInfoActivity.this, LocatorActivity.class);
                intent.putExtra("item_index", wifiItemIndex);
                startActivity(intent);

            }
        });
    }
}
