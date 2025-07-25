package com.memory.wq.enumertions;

public enum LoginType {

    LOGIN_TYPE_EMAIL("LOGIN_TYPE_EMAIL"),
    LOGIN_TYPE_WX("LOGIN_TYPE_WX"),
    LOGIN_TYPE_QQ("LOGIN_TYPE_QQ");
    private String type;

    LoginType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
