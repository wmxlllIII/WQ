package com.memory.wq.enumertions;

public enum FriendRelaStatus {
    UNKNOWN(-1),
    PENDING(0),
    ACCEPTED(1),
    REJECTED(2),
    BLOCKED(3),
    DELETED(4);

    private final int value;

    FriendRelaStatus(int type) {
        this.value = type;
    }

    public static FriendRelaStatus fromInt(int status) {
        for (FriendRelaStatus type : FriendRelaStatus.values()) {
            if (type.value == status) {
                return type;
            }
        }

        return UNKNOWN;
    }

    public int toInt() {
        return this.value;
    }
}
