package com.memory.wq.beans;

import com.memory.wq.enumertions.Gender;

public class ActorInfo {
    private int actorId;
    private String actorName;
    private Gender gender;
    private String introduction;
    private String avatarUrl;

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "ActorInfo{" +
                "actorId=" + actorId +
                ", actorName='" + actorName + '\'' +
                ", gender=" + gender +
                ", introduction='" + introduction + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
