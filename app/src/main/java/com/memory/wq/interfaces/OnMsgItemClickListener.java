package com.memory.wq.interfaces;

import com.memory.wq.beans.MsgInfo;

public interface OnMsgItemClickListener {
    void onLinkClick(MsgInfo msgInfo);

    void onMsgLongClick(MsgInfo msgInfo);

    void onAvatarClick(long userId);
}
