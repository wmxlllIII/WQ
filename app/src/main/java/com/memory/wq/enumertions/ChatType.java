package com.memory.wq.enumertions;

public enum ChatType {
    CHAT_TYPE_INDIVIDUAL(0),
    CHAT_TYPE_GROUP(1),
    UNKNOWN(-1);

    private final int value;

    ChatType(int type) {
        this.value = type;
    }

    public static ChatType fromInt(int chatIntType) {
        for (ChatType type : ChatType.values()) {
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
