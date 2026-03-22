package com.memory.wq.adapters.viewholders;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.beans.MsgListInfo;
import com.memory.wq.databinding.ItemMsgsLayoutBinding;
import com.memory.wq.interfaces.OnMsgListClickListener;
import com.memory.wq.utils.TimeUtils;

public class MsgListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "WQ_MsgListViewHolder";
    private final ItemMsgsLayoutBinding binding;

    public MsgListViewHolder(@NonNull ItemMsgsLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(MsgListInfo msgListInfo, OnMsgListClickListener listener) {
        binding.tvNickname.setText(msgListInfo.getDisplayName());
        binding.tvLastMsg.setText(msgListInfo.getLastMsg());
        binding.tvTime.setText(TimeUtils.convertToNumberTime(msgListInfo.getCreateAt()));
        Glide.with(itemView.getContext())
                .load(msgListInfo.getAvatar())
                .error(R.mipmap.icon_default_avatar)
                .transform(new RoundedCorners(12))
                .into(binding.ivAvatar);

        itemView.setOnClickListener(v -> {
            if (listener == null) {
                Log.d(TAG, "[x] bind #37");
                return;
            }

            listener.onItemClick(msgListInfo.getChatId(), msgListInfo.getChatType());
        });
    }
}
