package com.memory.wq.db.op;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.enumertions.FriendRelaStatus;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.provider.FriendProvider;
import com.memory.wq.provider.WqApplication;

import java.util.ArrayList;
import java.util.List;

public class FriendSqlOP {

    private final static String TAG = "WQ_FriendSqlOP";
    private final ContentResolver mResolver = WqApplication.getInstance().getContentResolver();

    private static final String COLUMN_ID = "rela_id";
    private static final String COLUMN_SENDER_ID = "sender_id";
    private static final String COLUMN_RECEIVER_ID = "receiver_id";
    private static final String COLUMN_VALID_MSG = "valid_msg";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATE_AT = "create_at";
    private static final String COLUMN_UPDATE_AT = "update_at";


    //todo 应该去除头像昵称可变量字段，不持久到本地，拿id向服务器要
    public List<FriendRelaInfo> queryAllRelations(long userId) {
        Cursor cursor = mResolver.query(FriendProvider.CONTENT_URI_FRIEND,
                null,
                "(sender_id = ? OR receiver_id = ?)",
                new String[]{String.valueOf(userId), String.valueOf(userId)},
                null);

        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        List<FriendRelaInfo> friendRelaList = new ArrayList<>();
        while (cursor.moveToNext()) {
            FriendRelaInfo friendRelaInfo = new FriendRelaInfo();
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            long senderId = cursor.getLong(cursor.getColumnIndex(COLUMN_SENDER_ID));
            long receiverId = cursor.getLong(cursor.getColumnIndex(COLUMN_RECEIVER_ID));
            int status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS));
            String validMsg = cursor.getString(cursor.getColumnIndex(COLUMN_VALID_MSG));
            long createAt = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATE_AT));
            long updateAt = cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATE_AT));

            friendRelaInfo.setId(id);
            friendRelaInfo.setSenderId(senderId);
            friendRelaInfo.setReceiverId(receiverId);
            friendRelaInfo.setStatus(status);
            friendRelaInfo.setValidMsg(validMsg);
            friendRelaInfo.setCreateAt(createAt);
            friendRelaInfo.setUpdateAt(updateAt);

            friendRelaList.add(friendRelaInfo);
        }
        cursor.close();
        return friendRelaList;
    }


    public boolean insertRelations(List<FriendRelaInfo> friendRelaList) {
        if (friendRelaList == null || friendRelaList.isEmpty()) {
            return false;
        }

        for (FriendRelaInfo info : friendRelaList) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, info.getId());
            values.put(COLUMN_SENDER_ID, info.getSenderId());
            values.put(COLUMN_RECEIVER_ID, info.getReceiverId());
            values.put(COLUMN_STATUS, info.getStatus());
            values.put(COLUMN_VALID_MSG, info.getValidMsg());
            values.put(COLUMN_CREATE_AT, info.getCreateAt());
            values.put(COLUMN_UPDATE_AT, info.getUpdateAt());


            String whereClause = COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(info.getId())};
            int updatedRows = mResolver.update(FriendProvider.CONTENT_URI_FRIEND, values, whereClause, whereArgs);

            // 2. 如果更新行数为0，说明记录不存在，执行插入
            if (updatedRows == 0) {
                Uri uri = mResolver.insert(FriendProvider.CONTENT_URI_FRIEND, values);
                if (uri == null) {
                    return false; // 插入失败
                }
            }
        }
        return true;
    }

    public List<Long> queryAllFriend(long userId) {
        List<Long> friendIdList = new ArrayList<>();
        Cursor cursor = mResolver.query(FriendProvider.CONTENT_URI_FRIEND,
                new String[]{"sender_id", "receiver_id"},
                "(sender_id = ? OR receiver_id = ?) AND status = ?",
                new String[]{
                        String.valueOf(userId),
                        String.valueOf(userId),
                        String.valueOf(FriendRelaStatus.ACCEPTED.toInt())
                },
                null);

        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            long senderId = cursor.getLong(cursor.getColumnIndex(COLUMN_SENDER_ID));
            long receiverId = cursor.getLong(cursor.getColumnIndex(COLUMN_RECEIVER_ID));


            friendIdList.add(senderId == userId ? receiverId : senderId);
        }
        cursor.close();
        return friendIdList;
    }

}
