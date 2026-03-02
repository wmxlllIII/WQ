package com.memory.wq.beans;

import java.util.List;

public class PostDetailInfo {
    private int postId;

    private long posterId;

    private String postTitle;

    private String postContent;

    private List<String> contentImagesUrlList;

    private List<TagInfo> tags;

    private boolean isLiked;

    private int likeCount;

    private int commentCount;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public long getPosterId() {
        return posterId;
    }

    public void setPosterId(long posterId) {
        this.posterId = posterId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public List<String> getContentImagesUrlList() {
        return contentImagesUrlList;
    }

    public void setContentImagesUrlList(List<String> contentImagesUrlList) {
        this.contentImagesUrlList = contentImagesUrlList;
    }

    public List<TagInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagInfo> tags) {
        this.tags = tags;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "PostDetailInfo{" +
                "postId=" + postId +
                ", posterId=" + posterId +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", contentImagesUrlList=" + contentImagesUrlList +
                ", tags=" + tags +
                ", isLiked=" + isLiked +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                '}';
    }
}
