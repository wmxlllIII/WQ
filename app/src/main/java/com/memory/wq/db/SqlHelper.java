package com.memory.wq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.memory.wq.constants.AppProperties;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_USERS = "create table " + AppProperties.TABLE_NAME_USER + " (id integer primary key autoincrement ,uu_number integer unique,email varchar(255) unique,nickname varchar(20),avatar_url varchar(255))";
    private static final String CREATE_TABLE_FRIENDS = "create table " + AppProperties.TABLE_NAME_FRIEND + " (id integer primary key autoincrement,friend_uu_number integer ,user_uu_number integer,email varchar(255) unique,nickname varchar(20),avatar_url varchar(255),update_at integer)";
    private static final String CREATE_TABLE_FRIEND_RELATIONSHIP = "create table " + AppProperties.TABLE_FRIEND_RELATIONSHIP + "(_id integer primary key autoincrement,rela_id integer unique,source_id integer,target_id integer,state varchar(10),valid_msg varchar(30),create_at integer,update_at integer)";
    private static final String CREATE_TABLE_MESSAGE = "create table " + AppProperties.TABLE_NAME_MESSAGE + "(_id integer primary key autoincrement,msg_id integer,sender_id integer,receiver_id integer ,content varchar(255),content_type integer,create_at integer)";

    public SqlHelper(@Nullable Context context) {
        super(context, AppProperties.WQ_DB_NAME, null, 1);
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
