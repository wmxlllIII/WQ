package com.memory.wq.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.db.SqlHelper;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MsgSqlOP {

    private final SqlHelper helper;

    public MsgSqlOP(Context context) {
        helper = new SqlHelper(context);
    }

    public List<MsgInfo> queryAllMsg(String currentEmail, String targetEmail) {
        List<MsgInfo> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(AppProperties.MESSAGE_TABLE_NAME, null, "(sender_email = ? AND receiver_email = ?) OR (sender_email = ? AND receiver_email = ?)", new String[]{currentEmail, targetEmail, targetEmail, currentEmail}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String senderEmail = cursor.getString(cursor.getColumnIndex("sender_email"));
                String receiverEmail = cursor.getString(cursor.getColumnIndex("receiver_email"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int contentType = cursor.getInt(cursor.getColumnIndex("content_type"));

                Cursor userCursor = db.query(AppProperties.USER_TABLE_NAME, new String[]{"avatarurl"}, "email=?", new String[]{currentEmail}, null, null, null);
                String myAvatarUrl="";
                if (userCursor!=null &&userCursor.getCount()>0){
                    userCursor.moveToFirst();
                    myAvatarUrl = userCursor.getString(0);
                }

                Cursor friendCursor = db.query(AppProperties.FRIEND_TABLE_NAME, new String[]{"avatar_url"}, "email=?", new String[]{targetEmail}, null, null, null);
                String friendAvatarUrl = "";
                if (friendCursor!=null &&friendCursor.getCount()>0){
                    friendCursor.moveToFirst();
                    friendAvatarUrl=friendCursor.getString(0);
                }

                MsgInfo msg = new MsgInfo();
                msg.setSenderEmail(senderEmail);
                msg.setReceiverEmail(receiverEmail);
                msg.setMsgType(contentType);
                msg.setMyAvatarUrl(myAvatarUrl);
                msg.setFriendAvatarUrl(friendAvatarUrl);
                if (contentType==0){
                    msg.setContent(content);
                }else if (contentType==1){
                    MsgInfo shareMsg = JsonParser.shareMsgParser(content);
                    msg.setLinkImageUrl(shareMsg.getLinkImageUrl());
                    msg.setLinkTitle(shareMsg.getLinkTitle());
                    msg.setLinkContent(shareMsg.getLinkContent());
                }


                list.add(msg);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public boolean insertMessages(List<MsgInfo> msgInfoList) {
        if (msgInfoList == null || msgInfoList.size() == 0) {
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (MsgInfo msg : msgInfoList) {
                ContentValues values = new ContentValues();
                int msgType = msg.getMsgType();

                String content = msg.getContent();
                String receiverEmail = msg.getReceiverEmail();
                String senderEmail = msg.getSenderEmail();

                values.put("sender_email",senderEmail);
                values.put("content_type",msgType);
                values.put("receiver_email",receiverEmail);
                if (msgType==0){
                    values.put("content",content);
                }else if (msgType==1){
                    String shareMsgJson = GenerateJson.getShareMsgJson(msg);
                    values.put("content",shareMsgJson);
                }


                long result = db.insert(AppProperties.MESSAGE_TABLE_NAME, null, values);
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
}
