package com.focjoe.roucator.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.focjoe.roucator.R;
import com.focjoe.roucator.model.ChannelInfo;

import java.util.List;

public class ChannelRateItemAdapter extends RecyclerView.Adapter<ChannelRateItemAdapter.ViewHolder> {
    List<ChannelInfo> channelInfoList;

    public ChannelRateItemAdapter(List<ChannelInfo> channelInfoList) {
        this.channelInfoList = channelInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_channel_rate, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChannelInfo channelInfo = channelInfoList.get(position);
        holder.textViewChannelName.setText(channelInfo.getName());
        holder.textViewChannelRate.setText(channelInfo.getRate());

    }

    @Override
    public int getItemCount() {
        return channelInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChannelName;
        TextView textViewChannelRate;

        public ViewHolder(View view) {
            super(view);
            textViewChannelName = view.findViewById(R.id.channel_name);
            textViewChannelRate = view.findViewById(R.id.channel_rate);
        }
    }
}

