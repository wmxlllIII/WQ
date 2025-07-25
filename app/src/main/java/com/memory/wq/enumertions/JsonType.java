package com.memory.wq.enumertions;

public enum JsonType {
    JSONTYPE_REQUEST("JSONTYPE_REQUEST"),
    JSONTYPE_REGISTER("JSONTYPE_REGISTER"),
    JSONTYPE_LOGIN("JSONTYPE_LOGIN");
    private String type;

    JsonType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
