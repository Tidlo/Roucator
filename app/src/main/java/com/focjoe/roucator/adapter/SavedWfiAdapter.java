package com.focjoe.roucator.adapter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.QRCodeGenerateActivity;
import com.focjoe.roucator.R;
import com.focjoe.roucator.model.SavedWifi;
import com.focjoe.roucator.util.MyApplication;
import com.focjoe.roucator.util.WifiDbOpenHelper;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SavedWfiAdapter extends RecyclerView.Adapter<SavedWfiAdapter.ViewHolder> {

    private List<SavedWifi> savedWifiList;
    private SQLiteDatabase database;
    private WifiDbOpenHelper dbOpenHelper;

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

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String ssid = savedWifi.getSsid();
                dbOpenHelper = new WifiDbOpenHelper(MyApplication.getContext());
                database = dbOpenHelper.getReadableDatabase();
                database.execSQL("DELETE FROM wifi WHERE ssid=?", new Object[]{ssid});
                Toast.makeText(MyApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    // TODO: 2018/12/14 [已保存的项目]列表，会展示是否保存了，点击项目弹出上下文菜单
    // TODO: 2018/12/14 上下文菜单根据是否已经 sync 显示sync 菜单，
    // TODO: 2018/12/14 已经sync过的就展示已sync的图标，未sync 的展示上传图标
    // TODO: 2018/12/14 用户登录时会把已sync 的同步到本地，逐条执行跟本地数据库的判断
    // TODO: 2018/12/14 如果本地已经存在，则跳过，仍然标记为未sync
    // TODO: 2018/12/14 如果本地不存在，则添加到本地数据库，标记为 synced

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
