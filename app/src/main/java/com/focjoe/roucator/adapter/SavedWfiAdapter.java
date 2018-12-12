package com.focjoe.roucator.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.focjoe.roucator.QRCodeGenerateActivity;
import com.focjoe.roucator.R;
import com.focjoe.roucator.model.SavedWifi;
import com.focjoe.roucator.util.MyApplication;

import java.util.List;

public class SavedWfiAdapter extends RecyclerView.Adapter<SavedWfiAdapter.ViewHolder> {

    private List<SavedWifi> savedWifiList;

    public SavedWfiAdapter(List<SavedWifi> savedWifiList) {
        this.savedWifiList = savedWifiList;
    }

    public SavedWfiAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_saved_wifi, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final SavedWifi savedWifi = savedWifiList.get(i);

        viewHolder.textView_ssid.setText(savedWifi.getSsid());
        viewHolder.textView_capability.setText(savedWifi.getCapability());
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), QRCodeGenerateActivity.class);
                intent.putExtra("type", savedWifi.getCapability());
                intent.putExtra("ssid", savedWifi.getSsid());
                intent.putExtra("password", savedWifi.getPassword());
                MyApplication.getContext().startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedWifiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_ssid;
        TextView textView_capability;

        public ViewHolder(View view) {
            super(view);
            textView_ssid = view.findViewById(R.id.saved_ssid);
            textView_capability = view.findViewById(R.id.saved_capability);
        }
    }
}
