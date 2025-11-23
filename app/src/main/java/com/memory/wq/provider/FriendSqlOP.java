package com.memory.wq.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.db.SqlHelper;
import com.memory.wq.constants.AppProperties;

import java.util.ArrayList;
import java.util.List;

public class FriendSqlOP {

    private final SqlHelper helper;

    public FriendSqlOP(Context context) {
        helper = new SqlHelper(context);
    }

    public List<FriendRelaInfo> queryAllRelations() {
        List<FriendRelaInfo> friendRelaList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppProperties.CREATE_TABLE_FRIEND_RELATIONSHIP, null,null,null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                FriendRelaInfo friendRelaInfo = new FriendRelaInfo();
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                String sourceEmail = cursor.getString(cursor.getColumnIndex("source_email"));
                String targetEmail = cursor.getString(cursor.getColumnIndex("target_email"));
                String sourceAvatarUrl = cursor.getString(cursor.getColumnIndex("source_avatar_url"));
                String targetAvatarUrl = cursor.getString(cursor.getColumnIndex("target_avatar_url"));
                String sourceNickname = cursor.getString(cursor.getColumnIndex("source_nickname"));
                String targetNickname = cursor.getString(cursor.getColumnIndex("target_nickname"));

                String state = cursor.getString(cursor.getColumnIndex("state"));
                String validMsg = cursor.getString(cursor.getColumnIndex("valid_msg"));
                long updateAt = cursor.getLong(cursor.getColumnIndex("update_at"));

                friendRelaInfo.setId(id);

                friendRelaInfo.setSourceEmail(sourceEmail);
                friendRelaInfo.setTargetEmail(targetEmail);
                friendRelaInfo.setSourceNickname(sourceNickname);
                friendRelaInfo.setTargetNickname(targetNickname);
                friendRelaInfo.setSourceAvatarUrl(sourceAvatarUrl);
                friendRelaInfo.setTargetAvatarUrl(targetAvatarUrl);

                friendRelaInfo.setState(state);
                friendRelaInfo.setUpdateAt(updateAt);
                friendRelaInfo.setValidMsg(validMsg);


                friendRelaList.add(friendRelaInfo);
            }
            cursor.close();
        }
        db.close();
        return friendRelaList;
    }


    public boolean insertRelations(List<FriendRelaInfo> friendRelaList) {
        if (friendRelaList == null || friendRelaList.size() == 0) {
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (FriendRelaInfo info : friendRelaList) {
                ContentValues values = new ContentValues();
                values.put("id", info.getId());


                values.put("source_email", info.getSourceEmail());
                values.put("target_email", info.getTargetEmail());
                values.put("source_nickname", info.getSourceNickname());
                values.put("target_nickname", info.getTargetNickname());
                values.put("source_avatar_url", info.getSourceAvatarUrl());
                values.put("target_avatar_url", info.getTargetAvatarUrl());

                values.put("valid_msg", info.getValidMsg());
                values.put("state", info.getState());
                values.put("update_at", info.getUpdateAt());

                long result = db.insertWithOnConflict(AppProperties.CREATE_TABLE_FRIEND_RELATIONSHIP, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (result == -1) {
                    return false;
                }
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void updateRelations(List<FriendRelaInfo> friendRelaList) {
        insertRelations(friendRelaList);
    }

    public boolean insertFriends(List<FriendInfo> friendInfoList,String uuid){
        if (friendInfoList == null || friendInfoList.size() == 0) {
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (FriendInfo friendInfo : friendInfoList) {
                ContentValues values = new ContentValues();
                values.put("email",friendInfo.getEmail());
                values.put("avatar_url",friendInfo.getAvatarUrl());
                values.put("nickname",friendInfo.getNickname());
                values.put("update_at",friendInfo.getUpdateAt());

                values.put("uuid",uuid);
                long result = db.insertWithOnConflict(AppProperties.FRIEND_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (result == -1) {
                    return false;
                }
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
            db.close();
        }

    }
    public List<FriendInfo> queryAllFriend(String uuid){
        List<FriendInfo> friendList=new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppProperties.FRIEND_TABLE_NAME, null, "uuid=?", new String[]{uuid}, null, null, null);
        if (cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                String email = cursor.getString(cursor.getColumnIndex("email"));
                String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                String avatarUrl = cursor.getString(cursor.getColumnIndex("avatar_url"));
                long updateAt = cursor.getLong(cursor.getColumnIndex("update_at"));

                FriendInfo friendInfo = new FriendInfo();
                friendInfo.setEmail(email);
                friendInfo.setNickname(nickname);
                friendInfo.setAvatarUrl(avatarUrl);
                friendInfo.setUpdateAt(updateAt);
                friendList.add(friendInfo);
            }
            cursor.close();
        }
        db.close();
        return friendList;
    }

}
