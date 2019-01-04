package com.focjoe.roucator.auth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = LoginAsyncTask.class.getSimpleName();

    private final static String serverUrl = "http://www.funnycode.net:12345/api/v1/";
    private final static String urlLogin = "user/login";

    private EditText etUserName;
    private EditText etPassword;
    private Login loginInfo;

    private Context context;

    public LoginAsyncTask(Context context, EditText etUserName, EditText etPassword) {

        super();

        this.context = context;

        this.etUserName = etUserName;
        this.etPassword = etPassword;

        loginInfo = new Login();

        String userName = this.etUserName.getText().toString();
        String password = this.etPassword.getText().toString();

        loginInfo.setUsername(userName);
        loginInfo.setPassword(password);
    }

    @Override
    protected String doInBackground(String... strings) {

        Gson gson = new Gson();
        String json = gson.toJson(loginInfo);

        Log.i(TAG, json);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder().url(serverUrl + urlLogin)
                .post(requestBody).build();

        try {
            OkHttpClient client = new OkHttpClient();


            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseData = response.body().string();


                return responseData;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {

        String responseData = s;

        Gson gson = new Gson();

//        Type jsonType = new TypeToken<BaseResponse<LoginUserInfo>>() {
//        }.getType();
//        BaseResponse<LoginUserInfo> loginUserInfoBaseResponse = gson.fromJson(responseData, jsonType);
//
//        Log.i(TAG, responseData);
//
//        if (loginUserInfoBaseResponse.getCode() != BaseResponse.RESPONSE_SUCCESS) {
//
//            Toast.makeText(context, "Login failed: " + loginUserInfoBaseResponse.getMsg(),
//                    Toast.LENGTH_SHORT).show();
//
//            return;
//        }

//        LoginUserInfo lll = loginUserInfoBaseResponse.getData();
//
//        if (lll != null) {
//            Log.i(TAG, "Id: " + lll.getId());
//            Log.i(TAG, "Name: " + lll.getName());
//            Log.i(TAG, "Real Name: " + lll.getRealName());
//            Log.i(TAG, "Real Name: " + lll.getLastLoginDatetime());
//
//            Intent detailIntent = new Intent(context, Main2Activity.class);
//            Bundle extras = new Bundle();
//
//            extras.putString("user_name", lll.getName());
//            extras.putString("avatar", lll.getAvatar());
//            extras.putString("thumb_avatar", lll.getThumbAvatar());
//            extras.putString("last_login_date", lll.getLastLoginDatetime());
//
//            detailIntent.putExtras(extras);
//            context.startActivity(detailIntent);
//        }

        super.onPostExecute(s);
    }
}
