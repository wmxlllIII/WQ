package com.memory.wq.beans;

import com.memory.wq.enumertions.ContentType;

import java.io.Serializable;

public class MsgInfo implements Serializable {

    private long msgId;
    private String content;
    private long senderId;
    private long receiverId;
    private long createAt;
    private long updateAt;

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


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public long getMsgId() {

        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }
}
