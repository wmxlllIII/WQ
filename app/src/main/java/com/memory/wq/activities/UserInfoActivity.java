package com.memory.wq.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.databinding.ActivityInfoBinding;
import com.memory.wq.managers.UserManager;
import com.memory.wq.properties.AppProperties;

public class UserInfoActivity extends BaseActivity<ActivityInfoBinding> {

    private SharedPreferences sp;
    private String token;
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        showUI();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info;
    }

    private void showUI() {

        String userName = sp.getString("userName", "");
        String email = sp.getString("email", "");
        mBinding.etNickname.setHint(userName);
        mBinding.etEmail.setHint(email);
    }

    private void initData() {
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        mUserManager = new UserManager(this);
    }

    private void initView() {
        mBinding.btnOk.setOnClickListener(view -> {
            String nickname = mBinding.etNickname.getText().toString().trim();
            if (TextUtils.isEmpty(nickname))
                return;
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(nickname);
            mUserManager.updateUserInfo(token, userInfo);
        });
    }

}