package com.memory.wq.beans;


import java.io.Serializable;

public class FriendInfo implements Serializable {
  private String nickname;
  private String avatarUrl;
  private String verifyMsg;
  private long updateAt;
  private boolean isOnline;
  private String email;

  public long getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(long updateAt) {
    this.updateAt = updateAt;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getVerifyMsg() {
    return verifyMsg;
  }

  public void setVerifyMsg(String verifyMsg) {
    this.verifyMsg = verifyMsg;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean online) {
    isOnline = online;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return "FriendInfo{" +
            "nickname='" + nickname + '\'' +
            ", avatarUrl='" + avatarUrl + '\'' +
            ", verifyMsg='" + verifyMsg + '\'' +
            ", isOnline=" + isOnline +
            ", email='" + email + '\'' +
            '}';
  }
}
