package com.memory.wq.beans;

public class UserInfo {
    private String email;
    private String token;
    private String userName;
    private String avatarUrl;
    private long uuNumber;

    public long getUuNumber() {
        return uuNumber;
    }

    public void setUuNumber(long uuNumber) {
        this.uuNumber = uuNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", userName='" + userName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", uuNumber=" + uuNumber +
                '}';
    }
}
