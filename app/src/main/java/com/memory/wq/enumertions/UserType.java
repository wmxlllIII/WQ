package com.memory.wq.enumertions;

public enum UserType {
    USER_TYPE_VISITOR("USER_TYPE_VISITOR"),
    USER_TYPE_USER("USER_TYPE_USER");

    private String type;

    UserType(String type) {
        this.type= type;
    }

    public String getType() {
        return type;
    }

}
