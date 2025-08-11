package com.memory.wq.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.properties.AppProperties;

public class ShareConfirmDialog extends BottomSheetDialog {

    private FriendInfo friendInfo;
    private MsgInfo linkInfo;
    private OnShareActionListener listener;

    private ImageView iv_friend_avatar;
    private TextView tv_share_friend_name;
    private TextView tv_share_title;
    private TextView tv_share_content;
    private ImageView iv_cover;
    private TextView tv_cancel;
    private TextView tv_share;


    public ShareConfirmDialog(@NonNull Context context, FriendInfo friendInfo, MsgInfo linkInfo, OnShareActionListener listener) {
        super(context, R.style.BottomSheetDialog);
        this.friendInfo = friendInfo;
        this.linkInfo = linkInfo;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_share_confirm_layout);
        initView();
        initData();
    }

    private void initData() {
        Glide.with(getContext())
                .load(AppProperties.HTTP_SERVER_ADDRESS + friendInfo.getAvatarUrl())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(iv_friend_avatar);

        tv_share_friend_name.setText(friendInfo.getNickname());
        tv_share_title.setText(linkInfo.getLinkTitle());
        tv_share_content.setText(linkInfo.getContent());

        Glide.with(getContext())
                .load(AppProperties.HTTP_SERVER_ADDRESS + linkInfo.getLinkImageUrl())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(iv_cover);

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onShare(linkInfo);
                dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancel();
                dismiss();
            }
        });

    }

    private void initView() {
        iv_friend_avatar = (ImageView) findViewById(R.id.iv_friend_avatar);
        tv_share_friend_name = (TextView) findViewById(R.id.tv_share_friend_name);
        tv_share_title = (TextView) findViewById(R.id.tv_share_title);
        tv_share_content = (TextView) findViewById(R.id.tv_share_content);
        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_share = (TextView) findViewById(R.id.tv_share);
    }

    public interface OnShareActionListener {
        void onShare(MsgInfo shareMsg);

        void onCancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listener.onCancel();
    }
}
