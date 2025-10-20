package com.memory.wq.managers;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.memory.wq.activities.LaunchActivity;
import com.memory.wq.activities.MainActivity;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.enumertions.JsonType;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.thread.ThreadPoolManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AuthManager {
    public static final String TAG = AuthManager.class.getName();

    public static final int EMPTY_PWD = 0;
    public static final int MISMATCH_PWD = 1;
    public static final int OK_PWD = 2;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void login(String account, String pwd, ResultCallback<UserInfo> callback) {
        String loginJson = GenerateJson.generateJson(JsonType.JSONTYPE_LOGIN, account, 0, pwd);
        System.out.println("===login===AuthOP" + loginJson);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.LOGIN_URL, "", loginJson, new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        callback.onError(response.toString());
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            UserInfo userInfo = JsonParser.loginParser(json);
                            System.out.println("=======login==成功了" + userInfo);
                            userInfo.setLogin(true);
                            callback.onSuccess(userInfo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }
            });
        });

    }

    public void tryAutoLogin(String token,ResultCallback<UserInfo> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.AUTOLOGIN_URL, token, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(()-> callback.onError("网络连接失败"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("code") == 1) {
                            //TODO onSuccess("token")
                            UserInfo userInfo = JsonParser.loginParser(json);
                            mHandler.post(()-> callback.onSuccess(userInfo));
                        } else {
                            mHandler.post(()-> callback.onError(response.message()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public boolean isEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPwd(String pwd) {
        return !TextUtils.isEmpty(pwd);
    }

    public int checkPwd(String pwd1, String pwd2) {

        if (TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)) {
            return EMPTY_PWD;
        } else if (!pwd1.equals(pwd2)) {

            return MISMATCH_PWD;
        } else {
            return OK_PWD;
        }
    }

    public void checkProtocol(boolean isChecked, ResultCallback<Boolean> callback) {
        if (isChecked) {
            callback.onSuccess(null);
        } else {
            callback.onError("请先阅读并同意用户协议");
        }
    }

    public void register(String email, int code, String password, ResultCallback<UserInfo> callback) {
        String json = GenerateJson.generateJson(JsonType.JSONTYPE_REGISTER, email, code, password);
        System.out.println("===register===AuthOP" + json);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.REGISTER_URL, "", json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError(response.toString());
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            UserInfo userInfo = JsonParser.registerParser(json);
                            Log.d(TAG, "onResponse: ===注册返回数据" + userInfo);
                            callback.onSuccess(userInfo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.onError("验证码错误,请重试");

                }
            });
        });

    }

    public void getCode(String email, ResultCallback<Boolean> callback) {
        String getCodeJson = GenerateJson.generateJson(JsonType.JSONTYPE_REQUEST, email, 0, "");
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.REQUEST_URL, "", getCodeJson, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError(response.toString());
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1)
                            callback.onSuccess(true);
                        else
                            callback.onError("===请输入正确邮箱地址");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        });

    }

}
