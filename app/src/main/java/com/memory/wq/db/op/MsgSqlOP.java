package com.memory.wq.db.op;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.provider.MsgProvider;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MsgSqlOP {

    private final static String TAG = "WQ_MsgSqlOP";
    private final ContentResolver mResolver = WqApplication.getInstance().getContentResolver();

    public List<MsgInfo> queryAllMsg(long currentUuNumber, long targetUuNumber) {
        List<MsgInfo> list = new ArrayList<>();

        Cursor cursor = mResolver.query(MsgProvider.CONTENT_URI,
                null,
                "(sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)",
                new String[]{
                        String.valueOf(currentUuNumber),
                        String.valueOf(targetUuNumber),
                        String.valueOf(targetUuNumber),
                        String.valueOf(currentUuNumber)
                },
                "create_at ASC");
        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            long senderId = cursor.getLong(cursor.getColumnIndex("sender_id"));
            long receiverId = cursor.getLong(cursor.getColumnIndex("receiver_id"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            int contentType = cursor.getInt(cursor.getColumnIndex("content_type"));
            int createAt = cursor.getInt(cursor.getColumnIndex("create_at"));
            int msgId = cursor.getInt(cursor.getColumnIndex("msg_id"));

            MsgInfo msg = new MsgInfo();
            msg.setSenderId(senderId);
            msg.setReceiverId(receiverId);
            msg.setMsgType(ContentType.fromInt(contentType));
            msg.setCreateAt(createAt);
            msg.setMsgId(msgId);
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
            long senderId = msg.getSenderId();
            long receiverId = msg.getReceiverId();

            values.put("sender_id", senderId);
            values.put("receiver_id", receiverId);
            values.put("content_type", msgType);
            values.put("create_at", msg.getCreateAt());
            values.put("msg_id", msg.getMsgId());

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
