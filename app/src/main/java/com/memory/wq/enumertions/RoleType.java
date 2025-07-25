package com.memory.wq.enumertions;

import java.io.Serializable;

public enum RoleType implements Serializable {
    ROLE_TYPE_BROADCASTER("ROLE_TYPE_BROADCASTER"),
    ROLE_TYPE_AUDIENCE("ROLE_TYPE_AUDIENCE");
    private String type;

    RoleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
