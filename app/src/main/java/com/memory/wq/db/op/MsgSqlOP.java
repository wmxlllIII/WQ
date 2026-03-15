package com.memory.wq.db.op;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.enumertions.ChatType;
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

    public List<MsgInfo> queryAllMsg(long curUser, long chatId, ChatType chatType) {
        List<MsgInfo> list = new ArrayList<>();

        Cursor cursor = mResolver.query(MsgProvider.CONTENT_URI,
                null,
                "(sender_id = ? AND chat_id = ? and chat_type = ?)",
                new String[]{
                        String.valueOf(curUser),
                        String.valueOf(chatId),
                        String.valueOf(chatType)
                },
                "create_at ASC");
        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            int msgId = cursor.getInt(cursor.getColumnIndex("msg_id"));
            long senderId = cursor.getLong(cursor.getColumnIndex("sender_id"));

            long chat_id = cursor.getLong(cursor.getColumnIndex("chat_id"));
            int chat_type = cursor.getInt(cursor.getColumnIndex("chat_type"));

            String content = cursor.getString(cursor.getColumnIndex("content"));
            int messageType = cursor.getInt(cursor.getColumnIndex("message_type"));
            int createAt = cursor.getInt(cursor.getColumnIndex("create_at"));
            int updateAt = cursor.getInt(cursor.getColumnIndex("update_at"));

            MsgInfo msg = new MsgInfo();
            msg.setMsgId(msgId);
            msg.setSenderId(senderId);
            msg.setChatId(chat_id);
            msg.setChatType(chat_type);

            msg.setMessageType(messageType);
            msg.setCreateAt(createAt);
            msg.setUpdateAt(updateAt);

            if (messageType == ContentType.TYPE_TEXT.toInt()) {
                msg.setContent(content);
            } else if (messageType == ContentType.TYPE_LINK.toInt()) {
//                MsgInfo shareMsg = JsonParser.shareMsgParser(content);
//                msg.setLinkImageUrl(shareMsg.getLinkImageUrl());
//                msg.setLinkTitle(shareMsg.getLinkTitle());
//                msg.setLinkContent(shareMsg.getLinkContent());
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

            values.put("msg_id", msg.getMsgId());
            values.put("sender_id", msg.getSenderId());
            values.put("chat_id", msg.getChatId());
            values.put("chat_type", msg.getChatType());
            values.put("content", msg.getContent());
            values.put("message_type", msg.getMessageType());
            values.put("create_at", msg.getCreateAt());
            values.put("update_at", msg.getUpdateAt());

            Uri uri = mResolver.insert(MsgProvider.CONTENT_URI, values);
            if (uri == null) {
                return false;
            }
        }

        return true;

    }
}
