package com.memory.wq.provider;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.memory.wq.beans.UserInfo;
import com.memory.wq.db.SqlHelper;
import com.memory.wq.properties.AppProperties;

public class UserSqlOP {

    private final SqlHelper helper;

    public UserSqlOP(Context context) {
        helper = new SqlHelper(context);
    }

    public void insertUser(UserInfo userInfo) {
        if (userInfo == null)
            return;
        SQLiteDatabase db = helper.getReadableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("email", userInfo.getEmail());
        values.put("nickname", userInfo.getUserName());
        values.put("avatarurl", userInfo.getAvatarPath());
        values.put("uuid", userInfo.getId());
        //TODO 插入uuNum
        long result = db.insertWithOnConflict(AppProperties.USER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (result >0) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }


}
