package com.memory.wq.beans;

import java.io.Serializable;

public class MsgInfo implements Serializable {
    private String content;

    private int status;
    private String senderEmail;
    private String myAvatarUrl;
    private String friendAvatarUrl;
    private String receiverEmail;
    private long timestamp;

    private int msgType; // 0=普通文本 1=分享卡片
    private String linkTitle;
    private String linkTime;
    private String linkContent;
    private String linkImageUrl;

    public int getMsgType() {
        return msgType;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLinkTime() {
        return linkTime;
    }

    public void setLinkTime(String linkTime) {
        this.linkTime = linkTime;
    }

    public String getLinkContent() {
        return linkContent;
    }

    public void setLinkContent(String linkContent) {
        this.linkContent = linkContent;
    }

    public String getLinkImageUrl() {
        return linkImageUrl;
    }

    public void setLinkImageUrl(String linkImageUrl) {
        this.linkImageUrl = linkImageUrl;
    }

    public String getMyAvatarUrl() {
        return myAvatarUrl;
    }

    public void setMyAvatarUrl(String myAvatarUrl) {
        this.myAvatarUrl = myAvatarUrl;
    }

    public String getFriendAvatarUrl() {
        return friendAvatarUrl;
    }

    public void setFriendAvatarUrl(String friendAvatarUrl) {
        this.friendAvatarUrl = friendAvatarUrl;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MsgInfo{" +
                "content='" + content + '\'' +
                ", status=" + status +
                ", senderEmail='" + senderEmail + '\'' +
                ", myAvatarUrl='" + myAvatarUrl + '\'' +
                ", friendAvatarUrl='" + friendAvatarUrl + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", timestamp=" + timestamp +
                ", msgType=" + msgType +
                ", linkTitle='" + linkTitle + '\'' +
                ", linkTime='" + linkTime + '\'' +
                ", linkContent='" + linkContent + '\'' +
                ", linkImageUrl='" + linkImageUrl + '\'' +
                '}';
    }
}
