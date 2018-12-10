package com.focjoe.roucator.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.focjoe.roucator.R;
import com.focjoe.roucator.adapter.ChannelRateItemAdapter;
import com.focjoe.roucator.model.ChannelInfo;
import com.focjoe.roucator.util.MyApplication;

import java.util.List;

public class ChannelRatingFragment extends Fragment {

    private List<ChannelInfo> channelInfos;

    public ChannelRatingFragment() {
    }

    public List<ChannelInfo> getChannelInfos() {
        return channelInfos;
    }

    public ChannelRatingFragment setChannelInfos(List<ChannelInfo> channelInfos) {
        this.channelInfos = channelInfos;
        return this;
    }
//    public ChannelRatingFragment newInstance(List<ChannelInfo> channelInfoList ) {
//
//        ChannelRatingFragment fragment = new ChannelRatingFragment();
//        fragment.channelInfos = channelInfoList;
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_rating, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
        recyclerView.setLayoutManager(layoutManager);
        ChannelRateItemAdapter adapter = new ChannelRateItemAdapter(channelInfos);
        recyclerView.setAdapter(adapter);
        recyclerView.hasFixedSize();
        return view;
    }
}
