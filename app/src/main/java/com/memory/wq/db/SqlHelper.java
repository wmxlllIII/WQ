package com.memory.wq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.memory.wq.properties.AppProperties;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_USERS = "create table " + AppProperties.USER_TABLE_NAME + " (id integer primary key autoincrement ,uuid varchar(255) not null,email varchar(255) unique,nickname varchar(20),avatarurl varchar(255))";
    private static final String CREATE_TABLE_FRIENDS = "create table " + AppProperties.FRIEND_TABLE_NAME + " (id integer primary key autoincrement,uuid varchar(255),email varchar(255) unique,nickname varchar(20),avatar_url varchar(255),update_at integer)";
    private static final String CREATE_TABLE_FRIEND_RELATIONSHIP = "create table " + AppProperties.CREATE_TABLE_FRIEND_RELATIONSHIP + "(_id integer primary key autoincrement,id integer unique,source_email varchar(30),target_email varchar(30),state varchar(10),valid_msg varchar(30),update_at integer,source_avatar_url varchar(100),target_avatar_url varchar(100),source_nickname varchar(30),target_nickname varchar(30))";
    private static final String CREATE_TABLE_MESSAGE = "create table " + AppProperties.MESSAGE_TABLE_NAME + "(_id integer primary key autoincrement,session_id varchar(255),sender_email varchar(100),receiver_email varchar(100) ,content varchar(255),content_type integer)";

    public SqlHelper(@Nullable Context context) {
        super(context, AppProperties.MY_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FRIENDS);
        db.execSQL(CREATE_TABLE_FRIEND_RELATIONSHIP);
        db.execSQL(CREATE_TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
