package com.memory.wq.sp;

import android.content.Context;

import com.memory.wq.beans.UserInfo;
import com.memory.wq.constants.AppProperties;

public class SPOP {

    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_AVATAR_URL = "avatarUrl";

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
        if (userInfo.getUuNumber() > 0) {
            spHelper.saveLong(KEY_USER_ID, userInfo.getUuNumber());
        }
        if (userInfo.getUserName() != null) {
            spHelper.saveString(KEY_USERNAME, userInfo.getUserName());
        }
        if (userInfo.getAvatarUrl() != null) {
            spHelper.saveString(KEY_AVATAR_URL, userInfo.getAvatarUrl());
        }
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setToken(spHelper.getString(KEY_TOKEN, null));
        userInfo.setEmail(spHelper.getString(KEY_EMAIL, null));
        userInfo.setUuNumber(spHelper.getLong(KEY_USER_ID, -1L));
        userInfo.setUserName(spHelper.getString(KEY_USERNAME, "游客"));
        userInfo.setAvatarUrl(spHelper.getString(KEY_AVATAR_URL, null));
        return userInfo;
    }

    public void clearUserInfo() {
        spHelper.removeKey(KEY_TOKEN);
        spHelper.removeKey(KEY_EMAIL);
        spHelper.removeKey(KEY_USER_ID);
        spHelper.removeKey(KEY_USERNAME);
    }

}
