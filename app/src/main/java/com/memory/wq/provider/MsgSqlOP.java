package com.memory.wq.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.db.SqlHelper;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MsgSqlOP {

    private final SqlHelper mHelper;
    private final ContentResolver mResolver;

    public MsgSqlOP(Context context) {
        mHelper = new SqlHelper(WqApplication.getInstance());
        mResolver = WqApplication.getInstance().getContentResolver();
    }

    public List<MsgInfo> queryAllMsg(String currentEmail, String targetEmail) {
        List<MsgInfo> list = new ArrayList<>();

        Cursor cursor = mResolver.query(MsgProvider.CONTENT_URI, null, "(sender_email = ? AND receiver_email = ?) OR (sender_email = ? AND receiver_email = ?)", new String[]{currentEmail, targetEmail, targetEmail, currentEmail}, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        SQLiteDatabase db = mHelper.getReadableDatabase();
        while (cursor.moveToNext()) {
            String senderEmail = cursor.getString(cursor.getColumnIndex("sender_email"));
            String receiverEmail = cursor.getString(cursor.getColumnIndex("receiver_email"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            int contentType = cursor.getInt(cursor.getColumnIndex("content_type"));


            Cursor userCursor = db.query(AppProperties.USER_TABLE_NAME, new String[]{"avatarurl"}, "email=?", new String[]{currentEmail}, null, null, null);
            String myAvatarUrl = "";
            if (userCursor != null && userCursor.getCount() > 0) {
                userCursor.moveToFirst();
                myAvatarUrl = userCursor.getString(0);
            }

            Cursor friendCursor = db.query(AppProperties.FRIEND_TABLE_NAME, new String[]{"avatar_url"}, "email=?", new String[]{targetEmail}, null, null, null);
            String friendAvatarUrl = "";
            if (friendCursor != null && friendCursor.getCount() > 0) {
                friendCursor.moveToFirst();
                friendAvatarUrl = friendCursor.getString(0);
            }

            MsgInfo msg = new MsgInfo();
            msg.setSenderEmail(senderEmail);
            msg.setReceiverEmail(receiverEmail);
            msg.setMsgType(ContentType.fromInt(contentType));
            msg.setSenderAvatar(myAvatarUrl);
            msg.setReceiverAvatar(friendAvatarUrl);
            if (contentType == 0) {
                msg.setContent(content);
            } else if (contentType == 1) {
                MsgInfo shareMsg = JsonParser.shareMsgParser(content);
                msg.setLinkImageUrl(shareMsg.getLinkImageUrl());
                msg.setLinkTitle(shareMsg.getLinkTitle());
                msg.setLinkContent(shareMsg.getLinkContent());
            }


            list.add(msg);
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean insertMessages(List<MsgInfo> msgInfoList) {
        if (msgInfoList == null || msgInfoList.isEmpty()) {
            return false;
        }


        for (MsgInfo msg : msgInfoList) {
            ContentValues values = new ContentValues();
            int msgType = msg.getMsgType().toInt();

            String content = msg.getContent();
            String receiverEmail = msg.getReceiverEmail();
            String senderEmail = msg.getSenderEmail();

            values.put("sender_email", senderEmail);
            values.put("content_type", msgType);
            values.put("receiver_email", receiverEmail);

            if (msgType == ContentType.TYPE_TEXT.toInt()) {
                values.put("content", content);
            } else if (msgType == ContentType.TYPE_LINK.toInt()) {
                String shareMsgJson = GenerateJson.getShareMsgJson(msg);
                values.put("content", shareMsgJson);
            }


            Uri uri = mResolver.insert(MsgProvider.CONTENT_URI, values);
            if (uri == null) {
                return false;
            }
        }

        return true;

    }
}
