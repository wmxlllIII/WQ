package com.memory.wq.enumertions;

public enum ChatPage {
    CHAT_INDIVIDUAL(true),
    CHAT_DETAIL(true),
    CHAT_INVITE_MEMBER(true);

    private final boolean reusable;

    ChatPage(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isReusable() {
        return reusable;
    }
}
