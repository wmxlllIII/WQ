package com.memory.wq.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.memory.wq.beans.UserInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.WqApplication;

public class SPManager {
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_AVATAR_URL = "avatarUrl";
    private static final String KEY_UU_NUMBER = "uuNumber";


    public static void saveUserInfo(Context context, UserInfo userInfo) {
        SharedPreferences sp = context.getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String token = userInfo.getToken();
        String email = userInfo.getEmail();
        String userName = userInfo.getUserName();
        String avatarPath = userInfo.getAvatarUrl();
        long uuNumber = userInfo.getUuNumber();
        if (!TextUtils.isEmpty(token))
            editor.putString(KEY_TOKEN, token);
        if (!TextUtils.isEmpty(email))
            editor.putString(KEY_EMAIL, email);
        if (!TextUtils.isEmpty(userName))
            editor.putString(KEY_USERNAME, userName);
        if (!TextUtils.isEmpty(avatarPath))
            editor.putString(KEY_AVATAR_URL, avatarPath);
        if (uuNumber != 0)
            editor.putLong(KEY_UU_NUMBER, uuNumber);

        editor.commit();
    }

    public static UserInfo getUserInfo() {
        SharedPreferences sp = WqApplication.getInstance().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        UserInfo userInfo = new UserInfo();
        userInfo.setToken(sp.getString(KEY_TOKEN, null));
        userInfo.setEmail(sp.getString(KEY_EMAIL, null));
        userInfo.setUserName(sp.getString(KEY_USERNAME, "游客"));
        userInfo.setAvatarUrl(sp.getString(KEY_AVATAR_URL, null));
        userInfo.setUuNumber(sp.getLong(KEY_UU_NUMBER, -1L));
        return userInfo;
    }
}
