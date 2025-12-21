package com.memory.wq.beans;

public class FriendRelaInfo {
    private int relaId;
    private long sourceId;
    private long targetId;
    private String state;
    private String validMsg;
    private long createAt;
    private long updateAt;

    public int getRelaId() {
        return relaId;
    }

    public void setRelaId(int relaId) {
        this.relaId = relaId;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
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


    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "FriendRelaInfo{" +
                "relaId=" + relaId +
                ", sourceId=" + sourceId +
                ", targetId=" + targetId +
                ", state='" + state + '\'' +
                ", validMsg='" + validMsg + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
