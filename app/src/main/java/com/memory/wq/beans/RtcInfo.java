package com.memory.wq.beans;

public class RtcInfo {
    private String token;
    private String appId;
    private long channelName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getChannelName() {
        return channelName;
    }

    public void setChannelName(long channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return "RtcInfo{" +
                "token='" + token + '\'' +
                ", appId='" + appId + '\'' +
                ", channelName='" + channelName + '\'' +
                '}';
    }
}
