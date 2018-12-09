package com.focjoe.roucator;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.model.ChannelInfo;
import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChannelRatingActivity extends AppCompatActivity {


    private static final String TAG = "ChannelRating";
    List<ChannelInfo> channels2dot4GInfoList;
    List<ChannelInfo> channels5GInfoList;
    List<WifiItem> wifiItemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int[] channels2dot4G = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    private int[] channels5G = {36, 40, 44, 48, 52, 56, 60, 64, 149, 153, 157, 161, 165};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_rating);

        swipeRefreshLayout = findViewById(R.id.channelRatingRefresh);
        swipeRefreshLayout.setOnRefreshListener(new ListViewOnRefreshListener());
        ListView listView = findViewById(R.id.channelRatingView);

        wifiItemList = MyApplication.getWifiItemList();

        channels2dot4GInfoList = new ArrayList<>();
        channels5GInfoList = new ArrayList<>();
        initializeLists();
        ///////////////////////////////////
        String test = "";
        TextView textView = findViewById(R.id.test_textview);
        for (ChannelInfo info :
                channels2dot4GInfoList) {
            test += info.getDevices();
            test += "\n";
        }
        Log.d(TAG, "onCreate: " + test);
        textView.setText(test);
        Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
    }


    void initializeLists() {
        for (int channel :
                channels2dot4G) {
            channels2dot4GInfoList.add(new ChannelInfo(channel));
        }
        for (int channel :
                channels5G) {
            channels5GInfoList.add(new ChannelInfo(channel));
        }
        for (WifiItem item :
                wifiItemList) {
            ChannelInfo channelInfo;
            if (item.getFrequency() > 5000) {
                //5G channels
                channelInfo = channels5GInfoList.get(Arrays.binarySearch(channels5G, item.getChannel()));
                channelInfo.addCount();
            } else {
                //2.4G channels
                channelInfo = channels2dot4GInfoList.get(Arrays.binarySearch(channels2dot4G, item.getChannel()));
                channelInfo.addCount();
            }
        }
    }


    private boolean is5GChannel(int channel) {
        return Arrays.binarySearch(channels5G, channel) > -1;
    }

    private boolean is2dot4GChannel(int channel) {
        return Arrays.binarySearch(channels2dot4G, channel) > -1;
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        //refresh
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class ListViewOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refresh();
        }
    }


}
