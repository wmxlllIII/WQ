package com.memory.wq.beans;

import java.sql.Timestamp;
import java.util.List;

public class PostCommentInfo {
    private int commentId;
    private int postId;
    private String userId;
    private int parentId;
    private String userName;
    private String content;
    private List<ReplyCommentInfo> replies;
    private boolean expanded;
    private long timestamp;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
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
