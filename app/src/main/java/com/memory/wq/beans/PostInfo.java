package com.memory.wq.beans;

import java.util.List;

public class PostInfo {
    //TODO 暂时先用邮箱查,改为使用用户数字id查发布者信息
//    private String uuNumber;

    private int postId;
    private String poster;
    private String title;
    private String commentCoverUrl;
    private String content;
    private List<String> contentImagesUrlList;
    private int likeCount;
    private long timestamp;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCommentCoverUrl() {
        return commentCoverUrl;
    }

    public void setCommentCoverUrl(String commentCoverUrl) {
        this.commentCoverUrl = commentCoverUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getContentImagesUrlList() {
        return contentImagesUrlList;
    }

    public void setContentImagesUrlList(List<String> contentImagesUrlList) {
        this.contentImagesUrlList = contentImagesUrlList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
