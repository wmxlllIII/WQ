package com.memory.wq.db.op;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.memory.wq.beans.UserInfo;
import com.memory.wq.db.SqlHelper;
import com.memory.wq.constants.AppProperties;

public class UserSqlOP {

    private final SqlHelper helper;
    public UserSqlOP(Context context) {
        helper = new SqlHelper(context);
    }

    /**
     * id integer primary key autoincrement ,
     * uu_number integer unique,
     * email varchar(255) unique,
     * nickname varchar(20),
     * avatar_url varchar(255))";
     *
     */

    public void insertUser(UserInfo userInfo) {
        if (userInfo == null)
            return;
        SQLiteDatabase db = helper.getReadableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("email", userInfo.getEmail());
        values.put("nickname", userInfo.getUsername());
        values.put("avatar_url", userInfo.getAvatarUrl());
        values.put("uu_number", userInfo.getUuNumber());
        long result = db.insertWithOnConflict(AppProperties.TABLE_NAME_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (result >0) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }


}
