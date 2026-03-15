package com.memory.wq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.memory.wq.constants.AppProperties;

public class SqlHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_USERS = "create table " + AppProperties.TABLE_NAME_USER + " (id integer primary key autoincrement ,uu_number integer unique,email varchar(255) unique,nickname varchar(20),avatar_url varchar(255))";
    private static final String CREATE_TABLE_FRIENDS = "create table " + AppProperties.TABLE_NAME_FRIEND + " (id integer primary key autoincrement,rela_id integer,sender_id integer ,receiver_id integer,valid_msg varchar(255) ,status integer,create_at integer ,update_at integer)";
    private static final String CREATE_TABLE_MESSAGE = "create table " + AppProperties.TABLE_NAME_MESSAGE + "(_id integer primary key autoincrement,msg_id integer, sender_id integer,chat_id integer,chat_type integer, content varchar(255),message_type integer,create_at integer,update_at integer)";

    public SqlHelper(@Nullable Context context) {
        super(context, AppProperties.WQ_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FRIENDS);
        db.execSQL(CREATE_TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
