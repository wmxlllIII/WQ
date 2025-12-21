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

public class FriendProvider extends ContentProvider {

    private final static String TAG = "WQ_FriendProvider";
    private static final String AUTHORITY = "com.memory.wq.provider.FriendProvider";
    public static final String TABLE_NAME_FRIEND = AppProperties.TABLE_NAME_FRIEND;
    public static final String TABLE_NAME_FRIEND_RELATIONSHIP = AppProperties.TABLE_FRIEND_RELATIONSHIP;
    public static final Uri CONTENT_URI_FRIEND = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_FRIEND);
    public static final Uri CONTENT_URI_FRIEND_RELATIONSHIP = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_FRIEND_RELATIONSHIP);

    private static final int MATCH_CODE_FRIEND = 1;
    private static final int MATCH_CODE_FRIEND_RELATIONSHIP = 2;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SqlHelper mHelper;

    static {
        mUriMatcher.addURI(AUTHORITY, AppProperties.TABLE_NAME_FRIEND, MATCH_CODE_FRIEND);
        mUriMatcher.addURI(AUTHORITY, AppProperties.TABLE_FRIEND_RELATIONSHIP, MATCH_CODE_FRIEND_RELATIONSHIP);
    }

    @Override
    public boolean onCreate() {
        mHelper = new SqlHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE_FRIEND:
                Cursor friendCursor = db.query(TABLE_NAME_FRIEND, projection, selection, selectionArgs, null, null, sortOrder);
                //todo 为什么使用setNotificationUri，什么意思，怎么用
                friendCursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI_FRIEND);
                return friendCursor;

            case MATCH_CODE_FRIEND_RELATIONSHIP:
                Cursor relaCursor = db.query(TABLE_NAME_FRIEND_RELATIONSHIP, projection, selection, selectionArgs, null, null, sortOrder);
                //todo 为什么使用setNotificationUri，什么意思，怎么用
                relaCursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI_FRIEND_RELATIONSHIP);
                return relaCursor;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE_FRIEND:
                long friRowId = db.insertWithOnConflict(TABLE_NAME_FRIEND, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (friRowId < 0) {
                    return null;
                }

                Uri friNewUri = Uri.withAppendedPath(CONTENT_URI_FRIEND, String.valueOf(friRowId));
                getContext().getContentResolver().notifyChange(CONTENT_URI_FRIEND, null);
                return friNewUri;

            case MATCH_CODE_FRIEND_RELATIONSHIP:

                long relaRowId = db.insertWithOnConflict(TABLE_NAME_FRIEND_RELATIONSHIP, null, values,SQLiteDatabase.CONFLICT_REPLACE);
                if (relaRowId < 0) {
                    return null;
                }

                Uri newUri = Uri.withAppendedPath(CONTENT_URI_FRIEND_RELATIONSHIP, String.valueOf(relaRowId));
                getContext().getContentResolver().notifyChange(CONTENT_URI_FRIEND_RELATIONSHIP, null);
                return newUri;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE_FRIEND:
                count = db.delete(TABLE_NAME_FRIEND, selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(CONTENT_URI_FRIEND, null);
                }
                break;

            case MATCH_CODE_FRIEND_RELATIONSHIP:
                count = db.delete(TABLE_NAME_FRIEND_RELATIONSHIP, selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(CONTENT_URI_FRIEND_RELATIONSHIP, null);
                }
                break;

            default:
                return 0;
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE_FRIEND:
                count = db.update(TABLE_NAME_FRIEND, values, selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(CONTENT_URI_FRIEND, null);
                }
                break;

            case MATCH_CODE_FRIEND_RELATIONSHIP:
                count = db.update(TABLE_NAME_FRIEND_RELATIONSHIP, values, selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(CONTENT_URI_FRIEND_RELATIONSHIP, null);
                }
                break;

            default:
                return 0;
        }

        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_CODE_FRIEND:
                return "vnd.android.cursor.dir/" + TABLE_NAME_FRIEND;
            case MATCH_CODE_FRIEND_RELATIONSHIP:
                return "vnd.android.cursor.dir/" + TABLE_NAME_FRIEND_RELATIONSHIP;
        }
        return null;
    }
}
