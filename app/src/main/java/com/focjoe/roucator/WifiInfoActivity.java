package com.focjoe.roucator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.focjoe.roucator.model.WifiItem;
import com.focjoe.roucator.util.MyApplication;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class WifiInfoActivity extends AppCompatActivity {

    //views
    private Toolbar toolbar;
    private TextView textViewSsid;
    private TextView textViewMacAddress;
    private TextView textViewFrequencyType;

    private TextView textViewManufacture;
    private TextView textViewLinkChannel;
    private TextView textViewDistance;
    private TextView textViewCapility;
    private TextView textViewFrequencyBand;


    private Button buttonLocator;
    private Button buttonManage;

    //local variables
    private int wifiItemIndex;
    private WifiItem wifiItem;
    SharedPreferences getUsername;

    //wifi data
    String ssid;
    String password;
    String capability;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_info);

        //使用 info_toolbar 取代 actionbar
        toolbar = findViewById(R.id.info_tool_bar);
        setSupportActionBar(toolbar);

        wifiItemIndex = getIntent().getIntExtra("item_index", 0);
        wifiItem = MyApplication.getWifiItemList().get(wifiItemIndex);

        //init views
        textViewSsid = findViewById(R.id.info_ssid);
        textViewMacAddress = findViewById(R.id.info_mac_address);
        textViewFrequencyType = findViewById(R.id.info_frequency_type);
        textViewManufacture = findViewById(R.id.info_manufacture);
        textViewLinkChannel = findViewById(R.id.info_link_channel);
        textViewDistance = findViewById(R.id.info_distance);
        textViewCapility = findViewById(R.id.info_capability);
        textViewFrequencyBand = findViewById(R.id.info_frequency_band);

        buttonLocator = findViewById(R.id.btn_test_locator);
        buttonManage = findViewById(R.id.btn_manage);

        //set views
        textViewSsid.setText(wifiItem.getSsid());
        textViewMacAddress.setText(wifiItem.getBSSID());
        textViewFrequencyType.setText(wifiItem.getInfoFrequencyType());
        textViewManufacture.setText(wifiItem.getInfoManufacture());
        textViewLinkChannel.setText(String.valueOf(wifiItem.getChannel()));
        textViewDistance.setText(wifiItem.getInfoDistance());
        textViewCapility.setText(wifiItem.getInfoCapility());
        textViewFrequencyBand.setText(wifiItem.getInfoFrequency());

        buttonLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiInfoActivity.this, LocatorActivity.class);
                intent.putExtra("item_index", wifiItemIndex);
                startActivity(intent);
            }
        });

//        if(wifiItem.getManageUrl() != null && !wifiItem.getManageUrl().equals("")){
//
//        }else {
//            buttonManage.setVisibility(View.INVISIBLE);
//        }

        buttonManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.baidu.com"));
                startActivity(intent);
            }
        });

        //获取登录成功后保存的SharedPreferences --->取出username
        getUsername = getSharedPreferences("saveForwifi", 0);
    }

    //重写下面的两个方法来使用自定义 main_toolbar 上的菜单，定义菜单的的点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.toolbar_menu_upload:
                Toast.makeText(this, "You clicked upload", Toast.LENGTH_SHORT).show();


                WifiDbOpenHelper dbOpenHelper = new WifiDbOpenHelper(this);
                SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
                Cursor cursor = db.query(SavedWifiEntry.TABLE_NAME, null, null, null, null, null, null);
                // use queried data to create a list
                while (cursor.moveToNext()) {
                    ssid = cursor.getString(1);
                    capability = cursor.getString(2);
                    password = cursor.getString(3);
                }
                cursor.close();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendJson();
                    }
                });
                thread.start();
                
                break;
            case R.id.toolbar_menu_qr_code:
                intent = new Intent(WifiInfoActivity.this, QRCodeGenerateActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }


    public HttpClient getHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    private void sendJson() {
        //boolean loginValidate = false;
        String urlStr = "http://192.168.32.2:8080/Test/WFPDServlet";
        HttpPost post = new HttpPost(urlStr);
        try {
            //向服务器写json
            JSONObject json = new JSONObject();
            String username = getUsername.getString("username", "");
            json.put("username", username);
            json.put("wifiname", ssid);
            json.put("wifipwd", password);
            json.put("capa", capability);


            System.out.println("==============" + json.toString());
            //保证json数据不是乱码
            StringEntity se = new StringEntity(json.toString());
            se.setContentEncoding(new BasicHeader("data", "application/json"));
            post.setEntity(se);

            //发送json给服务器
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(post);
            //接收来自服务器的数据，这里只有成功与失败的提示信息
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpURLConnection.HTTP_OK && httpResponse != null) {
                String Info = EntityUtils.toString(httpResponse.getEntity());

                JSONObject result = new JSONObject(Info);

                System.out.println(result.getString("vertifyInfo"));
//                flag=1;
                Log.i("MainActivity", result.getString("vertifyInfo"));
//                toastInfo = result.getString("vertifyInfo");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }



}
