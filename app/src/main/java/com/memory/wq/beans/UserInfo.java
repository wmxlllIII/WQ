package com.memory.wq.beans;

public class UserInfo {
    private String email;
    private String token;
    private String id;
    private String userName;
    private String avatarPath;
    private long uuNumber;
    private boolean isLogin;

    public long getUuNumber() {
        return uuNumber;
    }

    public void setUuNumber(long uuNumber) {
        this.uuNumber = uuNumber;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
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
                ", id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", uuNumber=" + uuNumber +
                ", isLogin=" + isLogin +
                '}';
    }

}
