package com.memory.wq.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.memory.wq.beans.UserInfo;
import com.memory.wq.provider.WqApplication;

public class AccountManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_TYPE = "userType";
    public enum UserType {
        USER_TYPE_VISITOR("USER_TYPE_VISITOR"),
        USER_TYPE_USER("USER_TYPE_USER");
        private String type;

        public String getType() {
            return type;
        }

        UserType(String type) {
            this.type = type;
        }
    }

    public static void saveLoginState(Context context, UserType userType) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USER_TYPE, userType.type);
        editor.apply();
    }


    public static boolean isVisitorUser() {
        SharedPreferences sp = WqApplication.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return UserType.USER_TYPE_VISITOR.getType().equals(sp.getString(KEY_USER_TYPE, UserType.USER_TYPE_VISITOR.getType()));
    }

    public static UserInfo getUserInfo(){
        
        return SPManager.getUserInfo();
    }

    public static long getUserId(){
        return getUserInfo().getUuNumber();
    }
}
