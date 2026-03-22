package com.memory.wq.beans;

public class OnlineInfo {
    private long userId;

    private boolean isOnline;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "OnlineInfo{" +
                "userId=" + userId +
                ", isOnline=" + isOnline +
                '}';
    }
}
