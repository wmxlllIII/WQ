package com.memory.wq.enumertions;

public enum SelectImageType {
    IMAGE_FROM_ALBUM("IMAGE_FROM_ALBUM"),
    IMAGE_FROM_CAMERA("IMAGE_FROM_CAMERA");
    private String type;

    SelectImageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
