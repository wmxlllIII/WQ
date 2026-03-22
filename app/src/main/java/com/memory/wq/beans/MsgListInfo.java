package com.memory.wq.beans;

public class MsgListInfo {
    private long chatId;
    private int chatType;
    private String displayName;
    private String avatar;
    private String lastMsg;
    private long createAt;

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "MsgListInfo{" +
                "chatId=" + chatId +
                ", chatType=" + chatType +
                ", displayName='" + displayName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", lastMsg='" + lastMsg + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}
