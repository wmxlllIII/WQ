package com.memory.wq.beans;

import com.memory.wq.enumertions.ContentType;

import java.io.Serializable;

public class MsgInfo implements Serializable {

    private long msgId;
    private String content;

    private int status;
    private String senderEmail;
    private String senderAvatar;
    private String receiverAvatar;
    private String receiverEmail;
    private long timestamp;

    private ContentType msgType;
    private String linkTitle;
    private String linkTime;
    private String linkContent;
    private String linkImageUrl;

    public ContentType getMsgType() {
        return msgType;
    }

    public void setMsgType(ContentType msgType) {
        this.msgType = msgType;
    }

    public String getLinkTitle() {
        return linkTitle;
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

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getReceiverAvatar() {
        return receiverAvatar;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
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

    public long getMsgId() {

        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "MsgInfo{" +
                "msgId=" + msgId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", senderEmail='" + senderEmail + '\'' +
                ", senderAvatar='" + senderAvatar + '\'' +
                ", receiverAvatar='" + receiverAvatar + '\'' +
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
