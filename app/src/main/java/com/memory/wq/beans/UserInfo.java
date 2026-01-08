package com.memory.wq.beans;

public class UserInfo {
    private String email;
    private String token;
    private String username;
    private String avatarUrl;
    private long uuNumber;

    public long getUuNumber() {
        return uuNumber;
    }

    public void setUuNumber(long uuNumber) {
        this.uuNumber = uuNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
                ", userName='" + username + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", uuNumber=" + uuNumber +
                '}';
    }
}
