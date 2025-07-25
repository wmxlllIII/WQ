package com.memory.wq.provider;

import android.content.Context;
import android.content.SharedPreferences;

import com.memory.wq.beans.UserInfo;
import com.memory.wq.helper.SpHelper;
import com.memory.wq.properties.AppProperties;

public class SPOP {

    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_AVATAR_URL = "avatarUrl";
    private static final String KEY_IS_LOGIN = "isLogin";

    private final SpHelper spHelper;

    public SPOP(Context context) {
        spHelper = new SpHelper(context, AppProperties.SP_NAME);
    }


    public void saveUserInfo(UserInfo userInfo) {
        if (userInfo.getToken() != null) {
            spHelper.saveString(KEY_TOKEN, userInfo.getToken());
        }
        if (userInfo.getEmail() != null) {
            spHelper.saveString(KEY_EMAIL, userInfo.getEmail());
        }
        if (userInfo.getId() != null) {
            spHelper.saveString(KEY_USER_ID, userInfo.getId());
        }
        if (userInfo.getUserName() != null) {
            spHelper.saveString(KEY_USERNAME, userInfo.getUserName());
        }
        if (userInfo.getAvatarPath() != null) {
            spHelper.saveString(KEY_AVATAR_URL, userInfo.getAvatarPath());
        }
        spHelper.saveBoolean(KEY_IS_LOGIN, userInfo.isLogin());
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setToken(spHelper.getString(KEY_TOKEN, null));
        userInfo.setEmail(spHelper.getString(KEY_EMAIL, null));
        userInfo.setId(spHelper.getString(KEY_USER_ID, null));
        userInfo.setUserName(spHelper.getString(KEY_USERNAME, "游客"));
        userInfo.setLogin(spHelper.getBoolean(KEY_IS_LOGIN, false));
        userInfo.setAvatarPath(spHelper.getString(KEY_AVATAR_URL,null));
        return userInfo;
    }

    public void clearUserInfo() {
        spHelper.removeKey(KEY_TOKEN);
        spHelper.removeKey(KEY_EMAIL);
        spHelper.removeKey(KEY_USER_ID);
        spHelper.removeKey(KEY_USERNAME);
        spHelper.removeKey(KEY_IS_LOGIN);
    }

}
