package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ActivityPersonalBinding;
import com.memory.wq.constants.AppProperties;

public class PersonalActivity extends BaseActivity<ActivityPersonalBinding> {

    private static final String TAG = PersonalActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    private void initData() {
        Intent intent = getIntent();
        FriendInfo friendInfo = (FriendInfo) intent.getSerializableExtra(AppProperties.FRIENDINFO);
        String email = friendInfo.getEmail();
        String avatarUrl = friendInfo.getAvatarUrl();
        String nickname = friendInfo.getNickname();
        //TODO
        if (TextUtils.isEmpty(email)) {
            mBinding.tvEmail.setVisibility(View.GONE);
        }
        mBinding.tvEmail.setText(email);

        if (TextUtils.isEmpty(nickname)) {
            mBinding.tvNickname.setVisibility(View.GONE);
        }
        mBinding.tvNickname.setText(nickname);

        if (TextUtils.isEmpty(avatarUrl)) {
            mBinding.ivAvatar.setImageResource(R.mipmap.icon_default_avatar);
            return;
        }
        Glide.with(this)
                .load(avatarUrl)
                .error(R.mipmap.icon_default_avatar)
                .into(mBinding.ivAvatar);

    }
}