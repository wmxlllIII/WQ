package com.memory.wq.beans;

public class FriendRelaInfo {
    private int id;
    private String sourceEmail;
    private String targetEmail;
    private String sourceAvatarUrl;
    private String targetAvatarUrl;
    private String sourceNickname;//user
    private String targetNickname;//user
    private String state;
    private String validMsg;
    private long timeStamp;
    private long updateAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceEmail() {
        return sourceEmail;
    }

    public void setSourceEmail(String sourceEmail) {
        this.sourceEmail = sourceEmail;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getSourceAvatarUrl() {
        return sourceAvatarUrl;
    }

    public void setSourceAvatarUrl(String sourceAvatarUrl) {
        this.sourceAvatarUrl = sourceAvatarUrl;
    }

    public String getTargetAvatarUrl() {
        return targetAvatarUrl;
    }

    public void setTargetAvatarUrl(String targetAvatarUrl) {
        this.targetAvatarUrl = targetAvatarUrl;
    }

    public String getSourceNickname() {
        return sourceNickname;
    }

    public void setSourceNickname(String sourceNickname) {
        this.sourceNickname = sourceNickname;
    }

    public String getTargetNickname() {
        return targetNickname;
    }

    public void setTargetNickname(String targetNickname) {
        this.targetNickname = targetNickname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getValidMsg() {
        return validMsg;
    }

    public void setValidMsg(String validMsg) {
        this.validMsg = validMsg;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "FriendRelaInfo{" +
                "id=" + id +
                ", sourceEmail='" + sourceEmail + '\'' +
                ", targetEmail='" + targetEmail + '\'' +
                ", sourceAvatarUrl='" + sourceAvatarUrl + '\'' +
                ", targetAvatarUrl='" + targetAvatarUrl + '\'' +
                ", sourceNickname='" + sourceNickname + '\'' +
                ", targetNickname='" + targetNickname + '\'' +
                ", state='" + state + '\'' +
                ", validMsg='" + validMsg + '\'' +
                ", timeStamp=" + timeStamp +
                ", updateAt=" + updateAt +
                '}';
    }
}
