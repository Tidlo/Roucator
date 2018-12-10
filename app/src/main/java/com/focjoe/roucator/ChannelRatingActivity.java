package com.focjoe.roucator;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.focjoe.roucator.adapter.SectionsPagerAdapter;
import com.focjoe.roucator.fragments.ChannelRatingFragment;
import com.focjoe.roucator.model.ChannelInfo;
import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;
import com.focjoe.roucator.util.Tools;

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

    //views
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_rating);

        initToolbar();
        wifiItemList = MyApplication.getWifiItemList();

        channels2dot4GInfoList = new ArrayList<>();
        channels5GInfoList = new ArrayList<>();
        initLists();
        initComponent();
        ///////////////////////////////////
        String test = "";
        for (ChannelInfo info :
                channels5GInfoList) {
            test += info.getConnectCount();
            test += "\n";
        }
        Log.d(TAG, "onCreate: 5G:" + test);

        test = "";
        for (ChannelInfo info :
                channels2dot4GInfoList) {
            test += info.getConnectCount();
            test += "\n";
        }

        Log.d(TAG, "onCreate: 2.4G:" + test);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.round_menu_black_24);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gallery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChannelRatingFragment().setChannelInfos(channels2dot4GInfoList), "2.4G");
        adapter.addFragment(new ChannelRatingFragment().setChannelInfos(channels5GInfoList), "5G");
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    void initLists() {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
