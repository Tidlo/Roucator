package com.focjoe.roucator.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focjoe.roucator.R;
import com.focjoe.roucator.WifiInfoActivity;
import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;

import java.util.List;

import static android.content.ContentValues.TAG;


public class WifiItemAdapter extends RecyclerView.Adapter<WifiItemAdapter.ViewHolder> {

    private List<WifiItem> wifiItems;
    private Context context;

    public WifiItemAdapter(List<WifiItem> wifiItems1) {
        wifiItems = wifiItems1;
        context = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        context = view.getContext();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * wifi item click listener
             * @param v
             */
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final WifiItem wifiItem = wifiItems.get(position);
                Log.d(TAG, "onClick: item clicked");
                Intent intent = new Intent(MyApplication.getContext(), WifiInfoActivity.class);
                intent.putExtra("item_index", position);
                MyApplication.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WifiItem wifiItem = wifiItems.get(position);

        holder.ssid.setText(wifiItem.getSsid());

        if (wifiItem.isConfigured()) {
            holder.configured.setImageResource(R.drawable.round_lock_open_black_36);
        }

        if (wifiItem.isSaved()) {
            holder.savedIcon.setImageResource(R.drawable.round_save_black_36);
        }

        if (wifiItem.getInfoFrequencyType().equals("5G")) {
            holder.freqIcon.setImageResource(R.drawable.m5g_token);
        }

        holder.mac.setText(wifiItem.getBSSID());

        int percentage = wifiItem.getPercentage();
        if (percentage < 25) {
            holder.signalBar.setImageResource(R.drawable.round_signal_wifi_1_bar_black_48);
        } else if (percentage < 50) {
            holder.signalBar.setImageResource(R.drawable.round_signal_wifi_2_bar_black_48);
        } else if (percentage < 75) {
            holder.signalBar.setImageResource(R.drawable.round_signal_wifi_3_bar_black_48);
        } else {
            holder.signalBar.setImageResource(R.drawable.round_signal_wifi_4_bar_black_48);
        }

    }

    @Override
    public int getItemCount() {
        return wifiItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView ssid;
        TextView mac;

        ImageView configured;
        ImageView signalBar;

        ImageView freqIcon;
        ImageView savedIcon;



        public ViewHolder(View view) {
            super(view);
            itemView = view;
            ssid = view.findViewById(R.id.wifi_ssid);

            freqIcon = view.findViewById(R.id.wifi_token);
            mac = view.findViewById(R.id.wifi_mac);
            signalBar = view.findViewById(R.id.wifi_signal_bar);
            configured = view.findViewById(R.id.wifi_configured);
            savedIcon = view.findViewById(R.id.wifi_saved);
        }
    }

}
