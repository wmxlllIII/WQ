package com.memory.wq.beans;

import java.util.List;

public class UiChatInfo {
    private String displayName;
    private List<Long> memberIdList;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Long> getMemberIdList() {
        return memberIdList;
    }

    public void setMemberIdList(List<Long> memberIdList) {
        this.memberIdList = memberIdList;
    }

    @Override
    public String toString() {
        return "UiChatInfo{" +
                "displayName='" + displayName + '\'' +
                ", memberIdList=" + memberIdList +
                '}';
    }
}
