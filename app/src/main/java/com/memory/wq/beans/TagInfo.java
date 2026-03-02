package com.memory.wq.beans;

public class TagInfo {
    private int id;

    private String tagName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return "TagInfo{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
