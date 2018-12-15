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
import android.widget.TextView;

import com.focjoe.roucator.R;
import com.focjoe.roucator.adapter.ChannelRateItemAdapter;
import com.focjoe.roucator.model.ChannelInfo;
import com.focjoe.roucator.util.MyApplication;

import java.util.Comparator;
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

        TextView textViewBestChannel = view.findViewById(R.id.best_channel);

        String bestChannel;
        channelInfos.sort(new Comparator<ChannelInfo>() {
            @Override
            public int compare(ChannelInfo o1, ChannelInfo o2) {
                return o1.getRating() - o2.getRating();
            }
        });
        bestChannel = channelInfos.get(0).getName();
        textViewBestChannel.setText(bestChannel);

        return view;
    }
}
