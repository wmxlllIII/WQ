package com.memory.wq.enumertions;

public enum UpdateInfoType {
    USERNAME(0),
    PHONE(1),
    EMAIL(2),
    GENDER(3),
    SIGNATURE(4),
    UNKNOWN(-1);
    private final int value;

    UpdateInfoType(int type) {
        this.value = type;
    }

    public static UpdateInfoType fromInt(int chatIntType) {
        for (UpdateInfoType type : UpdateInfoType.values()) {
            if (type.value == chatIntType) {
                return type;
            }
        }

        return UNKNOWN;
    }

    public int toInt() {
        return this.value;
    }
}

