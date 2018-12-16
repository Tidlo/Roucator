package com.focjoe.roucator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.focjoe.roucator.model.SavedWifi;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public static String toastInfo = "";
    private static long lastClickTime = 0;
    private static int flag = 0;
    TextInputEditText username;
    TextInputEditText password;
    Button log_in;
    Button sign_up;
    Toolbar toolbar;
    CheckBox remember;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    SharedPreferences spForwifi;
    SharedPreferences.Editor editorForwifi;

    //防止高频率点击
    public static boolean fastclick() {
        boolean flag = true;
        long clickTime = System.currentTimeMillis();
        if ((clickTime - lastClickTime) >= 1200) {
            flag = false;
        }
        lastClickTime = clickTime;
        return flag;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initToolbar();
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.user_password);
        log_in = findViewById(R.id.log_in);
        sign_up = findViewById(R.id.sign_up);
        log_in.setOnClickListener(this);
        sign_up.setOnClickListener(this);
        remember = findViewById(R.id.checkBox);
        spForwifi = getSharedPreferences("saveForwifi", 0);
        sp = getSharedPreferences("save", 0);
        Remember(sp.getBoolean("rememberpwd", false));
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.log_in_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_in:
                if (username.getText().length() == 0 || password.getText().length() == 0) {
                    Toast.makeText(this, "信息未填写完整", Toast.LENGTH_SHORT).show();
                } else if (!fastclick()) {
                    try {
                        flag = 0;
                        int dex = 0;
                        Thread loginThread = new Thread(new LoginThread());
                        loginThread.start();

                        while ((dex++) != 10) {
                            Thread.sleep(300);
                            if (flag == 1) {
                                Toast.makeText(this, toastInfo, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if (flag != 1) {
                            Toast.makeText(this, "登录失败，稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.sign_up:
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;

        }
    }

    public HttpClient getHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    private void sendJson() {
        String urlStr = MyApplication.SERVER_IP + "loginServlet";
        HttpPost post = new HttpPost(urlStr);
        try {
            //向服务器写json
            JSONObject json = new JSONObject();
            String name = username.getText().toString();
            String pwd = password.getText().toString();
            json.put("name", name);
            json.put("pwd", pwd);

            Log.d("MainActivity", "+=========+" + json.toString());
            //保证json数据不是乱码
            StringEntity se = new StringEntity(json.toString());
            se.setContentEncoding(new BasicHeader("data", "application/json"));
            post.setEntity(se);

            //发送json给服务器
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(post);
            //接收来自服务器的信息，这里只有成功与失败提示信息 ->登录
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpURLConnection.HTTP_OK && httpResponse != null) {
                String Info = EntityUtils.toString(httpResponse.getEntity());
                JSONObject result = new JSONObject(Info);
                Log.i("MainActivity", result.getString("vertifyInfo"));
                flag = 1;
                toastInfo = result.getString("vertifyInfo");
                if (result.getString("vertifyInfo").equals("登陆成功")) {
                    //在判断登录成功后保存账号密码
                    editor = sp.edit();
                    editorForwifi = spForwifi.edit();
                    editorForwifi.putString("username", name);
                    if (remember.isChecked()) {
                        editor.putBoolean("rememberpwd", true);
                        editor.putString("name", name);
                        editor.putString("pwd", pwd);
                        Log.i("MainActivity", name);
                        Log.i("MainActivity", pwd);
                        Log.i("MainActivity", "账号密码记录成功");
                    } else {
                        editor.clear();
                    }
                    editor.apply();
                    editorForwifi.apply();
                    //解析接收的JSONArray数据
                    JSONArray JA = result.getJSONArray("wifilist");
                    List<SavedWifi> wifilist = new ArrayList<>();
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject jo = JA.getJSONObject(i);

                        wifilist.add(new
                                SavedWifi(jo.getString("wifiname")
                                , jo.getString("wifipwd")
                                , jo.getString("capability")));
                    }

                    Thread.sleep(400);
                    finish();
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void Remember(Boolean flag) {
        Log.i("MainActivity", "++++++" + flag.toString() + "++++++");
        if (flag) {
            String name = sp.getString("name", "");
            String pwd = sp.getString("pwd", "");
            username.setText(name);
            password.setText(pwd);
            remember.setChecked(true);
        }
    }

    class LoginThread implements Runnable {
        public void run() {
            sendJson();
        }
    }
}
