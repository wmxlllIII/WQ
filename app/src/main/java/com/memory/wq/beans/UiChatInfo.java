package com.memory.wq.beans;

import java.util.List;

public class UiChatInfo {
    private String displayName;
    private List<FriendInfo> members;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<FriendInfo> getMembers() {
        return members;
    }

    public void setMembers(List<FriendInfo> members) {
        this.members = members;
    }
}
