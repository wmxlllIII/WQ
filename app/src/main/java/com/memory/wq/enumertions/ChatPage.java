package com.memory.wq.enumertions;

public enum ChatPage {
    CHAT(true),
    CHAT_DETAIL(true);

    private final boolean reusable;

    ChatPage(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isReusable() {
        return reusable;
    }
}
