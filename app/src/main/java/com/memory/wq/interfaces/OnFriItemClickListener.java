package com.memory.wq.interfaces;

public interface OnFriItemClickListener {
    void onItemClick(long targetId);

    void onItemLongClick();

    void onUpdateClick(long targetId, boolean isAgree, String validMsg);
}
