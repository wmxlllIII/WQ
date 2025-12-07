package com.memory.wq.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.memory.wq.constants.AppProperties;
import com.memory.wq.db.SqlHelper;

public class MsgProvider extends ContentProvider {

    private static final String TAG = "WQ_MsgProvider";
    private static final String AUTHORITY = "com.memory.wq.provider.MsgProvider";
    public static final String TABLE_NAME = AppProperties.MESSAGE_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    private static final int MATCH_CODE = 1;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SqlHelper mHelper;

    static {
        mUriMatcher.addURI(AUTHORITY, AppProperties.MESSAGE_TABLE_NAME, MATCH_CODE);
    }

    @Override
    public boolean onCreate() {
        mHelper = new SqlHelper(getContext());
        //TODO 返回值什么意义
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE:
                SQLiteDatabase db = mHelper.getReadableDatabase();
                Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                //todo 为什么使用setNotificationUri，什么意思，怎么用
                cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
                return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                long rowId = db.insert(TABLE_NAME, null, values);
                if (rowId < 0) {
                    return null;
                }

                Uri newUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(rowId));
                getContext().getContentResolver().notifyChange(CONTENT_URI, null);
                return newUri;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                int count = db.delete(TABLE_NAME, selection, selectionArgs);
                if (count <= 0) {
                    return 0;
                }

                getContext().getContentResolver().notifyChange(CONTENT_URI, null);
                return count;
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (mUriMatcher.match(uri)){
            case MATCH_CODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                int count = db.update(TABLE_NAME, values, selection, selectionArgs);
                if (count <= 0) {
                    return 0;
                }

                getContext().getContentResolver().notifyChange(CONTENT_URI, null);
                return count;
        }
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE:
                return "vnd.android.cursor.dir/" + TABLE_NAME;
        }
        return null;
    }

}