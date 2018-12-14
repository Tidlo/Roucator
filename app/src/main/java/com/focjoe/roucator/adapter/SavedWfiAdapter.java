package com.focjoe.roucator.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.focjoe.roucator.QRCodeGenerateActivity;
import com.focjoe.roucator.R;
import com.focjoe.roucator.model.SavedWifi;
import com.focjoe.roucator.util.MyApplication;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

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

        Log.d(TAG, "onBindViewHolder: onbind ssid:" + savedWifi.getSsid());
        viewHolder.textView_ssid.setText(savedWifi.getSsid());
        viewHolder.textView_capability.setText(savedWifi.getCapability());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), QRCodeGenerateActivity.class);
                intent.putExtra("type", savedWifi.getCapability());
                intent.putExtra("ssid", savedWifi.getSsid());
                intent.putExtra("password", savedWifi.getPassword());
                MyApplication.getContext().startActivity(intent);
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
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.saved_wifi_cardview);
            textView_ssid = view.findViewById(R.id.saved_ssid);
            textView_capability = view.findViewById(R.id.saved_capability);
        }
    }
}
