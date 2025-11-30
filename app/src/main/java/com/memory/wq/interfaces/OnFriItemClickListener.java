package com.memory.wq.interfaces;

public interface OnFriItemClickListener {
    void onItemClick(String targetId);

    void onItemLongClick();

    void onAcceptClick(String targetId);

    void onRejectClick(String targetId);
}
