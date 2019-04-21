package com.focjoe.roucator;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static String toastInfo = "";
    private static long lastClickTime = 0;
    private static int flag = 0;
    Toolbar toolbar;
    TextInputEditText username;
    TextInputEditText password;
    TextInputEditText email;
    TextInputEditText phone;
    Button confirm;

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
        setContentView(R.layout.activity_sign_up);

        initToolbar();

        username = findViewById(R.id.name);
        password = findViewById(R.id.pwd);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        confirm = findViewById(R.id.confirm_sign_up);
        confirm.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.sign_up_tool_bar);
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
            case R.id.confirm_sign_up:

                if (username.getText().length() == 0 || password.getText().length() == 0) {
                    Toast.makeText(this, "信息未填写完整", Toast.LENGTH_SHORT).show();
                } else if (!fastclick()) {
                    try {
                        flag = 0;
                        int dex = 0;
                        Thread signupThread = new Thread(new SignupThread());
                        signupThread.start();
                        //若flag!=1 --> toastInfo 还没有获取到
                        //等待时间超过3s 自动结束
                        while ((dex++) != 15) {
                            Thread.sleep(300);
                            if (flag == 1) {
                                Toast.makeText(this, toastInfo, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if (flag != 1) {
                            Toast.makeText(this, "注册失败，稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                break;

            default:
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
        //boolean loginValidate = false;
        String urlStr = MyApplication.SERVER_IP + "RegisterServlet";
        HttpPost post = new HttpPost(urlStr);
        try {
            //向服务器写json
            JSONObject json1 = new JSONObject();

            json1.put("name", username.getText().toString());
            json1.put("pwd", password.getText().toString());
            json1.put("mail", email.getText().toString());
            json1.put("phone", phone.getText().toString());

            System.out.println("==============" + json1.toString());
            //保证json数据不是乱码
            StringEntity se = new StringEntity(json1.toString());
            se.setContentEncoding(new BasicHeader("data", "application/json"));
            post.setEntity(se);
            //发送json给服务器
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(post);
            //接收来自服务器的数据，这里只有成功与失败的提示信息 ->注册
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (httpCode == HttpURLConnection.HTTP_OK && httpResponse != null) {
                String Info = EntityUtils.toString(httpResponse.getEntity());
                JSONObject result = new JSONObject(Info);
                flag = 1;
                System.out.println(result.getString("verifyInfo"));
                Log.i("MainActivity", result.getString("verifyInfo"));
                toastInfo = result.getString("verifyInfo");
                if (toastInfo.equals("注册成功")) {
                    Thread.sleep(400);
                    finish();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    class SignupThread implements Runnable {
        public void run() {
            sendJson();
        }
    }

}
