package com.memory.wq.db.op;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.MsgListInfo;
import com.memory.wq.enumertions.ChatType;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.provider.MsgProvider;
import com.memory.wq.provider.WqApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MsgSqlOP {

    private final static String TAG = "WQ_MsgSqlOP";
    private final ContentResolver mResolver = WqApplication.getInstance().getContentResolver();
    private static final String COLUMN_ID = "msg_id";
    private static final String COLUMN_SENDER_ID = "sender_id";
    private static final String COLUMN_CHAT_ID = "chat_id";
    private static final String COLUMN_CHAT_TYPE = "chat_type";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_MESSAGE_TYPE = "message_type";
    private static final String COLUMN_CREATE_AT = "create_at";
    private static final String COLUMN_UPDATE_AT = "update_at";

    public List<MsgInfo> queryAllMsg(long chatId, ChatType chatType) {
        List<MsgInfo> list = new ArrayList<>();

        Cursor cursor = mResolver.query(MsgProvider.CONTENT_URI,
                null,
                "(chat_id = ? and chat_type = ?)",
                new String[]{
                        String.valueOf(chatId),
                        String.valueOf(chatType.toInt())
                },
                "create_at ASC");
        if (cursor == null || cursor.getCount() <= 0) {
            Log.d(TAG, "[x] queryAllMsg #37");
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            int msgId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            int chat_type = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAT_TYPE));
            long senderId = cursor.getLong(cursor.getColumnIndex(COLUMN_SENDER_ID));

            long chat_id = cursor.getLong(cursor.getColumnIndex(COLUMN_CHAT_ID));

            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            int messageType = cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGE_TYPE));
            int createAt = cursor.getInt(cursor.getColumnIndex(COLUMN_CREATE_AT));
            int updateAt = cursor.getInt(cursor.getColumnIndex(COLUMN_UPDATE_AT));

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

            values.put(COLUMN_ID, msg.getMsgId());
            values.put(COLUMN_SENDER_ID, msg.getSenderId());
            values.put(COLUMN_CHAT_ID, msg.getChatId());
            values.put(COLUMN_CHAT_TYPE, msg.getChatType());
            values.put(COLUMN_CONTENT, msg.getContent());
            values.put(COLUMN_MESSAGE_TYPE, msg.getMessageType());
            values.put(COLUMN_CREATE_AT, msg.getCreateAt());
            values.put(COLUMN_UPDATE_AT, msg.getUpdateAt());

            Uri uri = mResolver.insert(MsgProvider.CONTENT_URI, values);
            if (uri == null) {
                return false;
            }
        }

        return true;

    }

    public List<MsgListInfo> queryMsgList() {
        List<MsgListInfo> chatList = new ArrayList<>();
        Set<String> processedChats = new HashSet<>();

        Cursor cursor = mResolver.query(MsgProvider.CONTENT_URI,
                new String[]{
                        COLUMN_CHAT_ID,
                        COLUMN_CHAT_TYPE,
                        COLUMN_CONTENT,
                        COLUMN_CREATE_AT
                },
                null,
                null,
                COLUMN_CREATE_AT + " DESC");

        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "[✓] queryMsgList #131");
            return chatList;
        }

        while (cursor.moveToNext()) {
            long chatId = cursor.getLong(cursor.getColumnIndex(COLUMN_CHAT_ID));
            int chatType = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAT_TYPE));
            String lastMsg = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            long createAt = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATE_AT));

            String key = chatId + "_" + chatType;

            if (processedChats.contains(key)) {
                continue;
            }

            processedChats.add(key);

            MsgListInfo msgListInfo = new MsgListInfo();
            msgListInfo.setChatId(chatId);
            msgListInfo.setChatType(chatType);
            msgListInfo.setLastMsg(lastMsg);
            msgListInfo.setCreateAt(createAt);

            chatList.add(msgListInfo);
        }

        cursor.close();
        return chatList;
    }
}
