package com.memory.wq.enumertions;

public enum SearchUserType {

    SEARCH_USER_TYPE_EMAIL("SEARCH_USER_TYPE_EMAIL"),
    SEARCH_USER_TYPE_PHONE("SEARCH_USER_TYPE_PHONE"),
    SEARCH_USER_TYPE_UUNUM("SEARCH_USER_TYPE_UUNUM");

    private String type;
    SearchUserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
