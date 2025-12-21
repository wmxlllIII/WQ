package com.memory.wq.interfaces;

public interface OnFriItemClickListener {
    void onItemClick(long targetId);

    void onItemLongClick();

    void onAcceptClick(long targetId);

    void onRejectClick(long targetId);
}
