package com.memory.wq.beans;

import java.util.List;

public class PostCommentInfo {
    private String id;
    private String userName;
    private String content;
    private List<ReplyCommentInfo> replies;
    private boolean expanded;
    private long timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ReplyCommentInfo> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyCommentInfo> replies) {
        this.replies = replies;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
