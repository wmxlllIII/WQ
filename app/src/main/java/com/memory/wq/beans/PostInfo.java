package com.memory.wq.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class PostInfo implements Serializable {
//    private String uuNumber;

    private int postId;
    private String poster;
    private String title;
    private String commentCoverUrl;
    private String posterAvatar;
    private String content;
    private List<String> contentImagesUrlList;
    private int likeCount;
    private boolean isLiked;
    private long timestamp;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
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

    public String getPosterAvatar() {
        return posterAvatar;
    }

    public void setPosterAvatar(String posterAvatar) {
        this.posterAvatar = posterAvatar;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PostInfo{" +
                "postId=" + postId +
                ", poster='" + poster + '\'' +
                ", title='" + title + '\'' +
                ", commentCoverUrl='" + commentCoverUrl + '\'' +
                ", posterAvatar='" + posterAvatar + '\'' +
                ", content='" + content + '\'' +
                ", contentImagesUrlList=" + contentImagesUrlList +
                ", likeCount=" + likeCount +
                ", isLiked=" + isLiked +
                ", timestamp=" + timestamp +
                '}';
    }
}
