package com.memory.wq.db.op;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.provider.FriendProvider;
import com.memory.wq.provider.WqApplication;

import java.util.ArrayList;
import java.util.List;

public class FriendSqlOP {

    private final static String TAG = "WQ_FriendSqlOP";
    private final ContentResolver mResolver = WqApplication.getInstance().getContentResolver();

    public List<FriendRelaInfo> queryAllRelations() {
        List<FriendRelaInfo> friendRelaList = new ArrayList<>();

        Cursor cursor = mResolver.query(FriendProvider.CONTENT_URI_FRIEND_RELATIONSHIP,
                null,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            FriendRelaInfo friendRelaInfo = new FriendRelaInfo();
            int id = cursor.getInt(cursor.getColumnIndex("rela_id"));
            long sourceId = cursor.getLong(cursor.getColumnIndex("source_id"));
            long targetId = cursor.getLong(cursor.getColumnIndex("target_id"));
            String state = cursor.getString(cursor.getColumnIndex("state"));
            String validMsg = cursor.getString(cursor.getColumnIndex("valid_msg"));
            long createAt = cursor.getLong(cursor.getColumnIndex("create_at"));
            long updateAt = cursor.getLong(cursor.getColumnIndex("update_at"));

            friendRelaInfo.setRelaId(id);
            friendRelaInfo.setSourceId(sourceId);
            friendRelaInfo.setTargetId(targetId);
            friendRelaInfo.setState(state);
            friendRelaInfo.setValidMsg(validMsg);
            friendRelaInfo.setCreateAt(createAt);
            friendRelaInfo.setUpdateAt(updateAt);
            Log.d(TAG, "[test] queryAllRelations #53"+friendRelaInfo);
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
            values.put("rela_id", info.getRelaId());
            values.put("source_id", info.getSourceId());
            values.put("target_id", info.getTargetId());
            values.put("valid_msg", info.getValidMsg());
            values.put("state", info.getState());
            values.put("create_at", info.getCreateAt());
            values.put("update_at", info.getUpdateAt());

            Uri uri = mResolver.insert(FriendProvider.CONTENT_URI_FRIEND_RELATIONSHIP, values);
            if (uri == null) {
                return false;
            }
        }
        return true;
    }

    public void updateRelations(List<FriendRelaInfo> friendRelaList) {
        if (friendRelaList == null || friendRelaList.isEmpty()) {
            return;
        }

        for (FriendRelaInfo friendRela : friendRelaList) {
            ContentValues values = new ContentValues();
            values.put("source_id", friendRela.getSourceId());
            values.put("target_id", friendRela.getTargetId());
            values.put("state", friendRela.getState());
            values.put("valid_msg", friendRela.getValidMsg());
            values.put("update_at", friendRela.getUpdateAt());


            int rowsUpdated = mResolver.update(
                    FriendProvider.CONTENT_URI_FRIEND_RELATIONSHIP,
                    values,
                    "rela_id = ?",
                    new String[]{String.valueOf(friendRela.getRelaId())}
            );
            if (rowsUpdated <= 0) {
                Log.d(TAG, "[test] updateRelations #77 rowsUpdated <= 0");
            }
        }
    }

    public boolean insertFriends(List<FriendInfo> friendInfoList) {
        if (friendInfoList == null || friendInfoList.isEmpty()) {
            return false;
        }

        for (FriendInfo friendInfo : friendInfoList) {
            ContentValues values = new ContentValues();
            values.put("friend_uu_number", friendInfo.getUuNumber());
            values.put("email", friendInfo.getEmail());
            values.put("avatar_url", friendInfo.getAvatarUrl());
            values.put("nickname", friendInfo.getNickname());
            values.put("update_at", friendInfo.getUpdateAt());
            values.put("user_uu_number", AccountManager.getUserId());
            Uri uri = mResolver.insert(FriendProvider.CONTENT_URI_FRIEND, values);
            if (uri == null) {
                return false;
            }
        }
        return true;
    }

    public List<FriendInfo> queryAllFriend(long uuid) {
        List<FriendInfo> friendList = new ArrayList<>();
        Cursor cursor = mResolver.query(FriendProvider.CONTENT_URI_FRIEND,
                null,
                "(user_uu_number = ?)",
                new String[]{String.valueOf(uuid)},
                null);

        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            String avatarUrl = cursor.getString(cursor.getColumnIndex("avatar_url"));
            long uuNumber = cursor.getLong(cursor.getColumnIndex("friend_uu_number"));
            long updateAt = cursor.getLong(cursor.getColumnIndex("update_at"));

            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setEmail(email);
            friendInfo.setUuNumber(uuNumber);
            friendInfo.setNickname(nickname);
            friendInfo.setAvatarUrl(avatarUrl);
            friendInfo.setUpdateAt(updateAt);
            friendList.add(friendInfo);
        }
        cursor.close();
        return friendList;
    }

}
