package com.memory.wq.enumertions;

public enum Page {
    PAGE_DISCOVER(0),
    PAGE_COWATCH(1),
    PAGE_MESSAGE(2),
    PAGE_HISTORY(3);
    private final int value;

    Page(int value) {
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}
