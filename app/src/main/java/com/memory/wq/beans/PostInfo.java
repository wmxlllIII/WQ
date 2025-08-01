package com.memory.wq.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class PostInfo implements Parcelable {
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

    public PostInfo() {
    }

    protected PostInfo(Parcel in) {
        postId = in.readInt();
        poster = in.readString();
        title = in.readString();
        commentCoverUrl = in.readString();
        content = in.readString();
        contentImagesUrlList = in.createStringArrayList();
        likeCount = in.readInt();
        timestamp = in.readLong();
    }

    public static final Creator<PostInfo> CREATOR = new Creator<PostInfo>() {
        @Override
        public PostInfo createFromParcel(Parcel in) {
            return new PostInfo(in);
        }

        @Override
        public PostInfo[] newArray(int size) {
            return new PostInfo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(postId);
        parcel.writeString(poster);
        parcel.writeString(title);
        parcel.writeString(commentCoverUrl);
        parcel.writeString(content);
        parcel.writeStringList(contentImagesUrlList);
        parcel.writeInt(likeCount);
        parcel.writeLong(timestamp);
    }
}
