package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.databinding.LoginMainLayoutBinding;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

public class LaunchActivity extends BaseActivity<LoginMainLayoutBinding> {
    private AuthManager mAuthManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryAutoLogin();
        initView();
        initData();
    }

    private void tryAutoLogin() {
        String oldToken = SPManager.getUserInfo(this).getToken();
        if (TextUtils.isEmpty(oldToken)) {
            return;
        }

        mAuthManager.tryAutoLogin(oldToken, new ResultCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo user) {

            }

            @Override
            public void onError(String err) {

            }
        });
        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_main_layout;
    }

    private void initData() {
        mAuthManager = new AuthManager();
    }


    private void initView() {
        mBinding.tvAnother.setOnClickListener(view -> {
            initBottomSheetDialog();
        });

        mBinding.tvVisitor.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        mBinding.btnLoginEmail.setOnClickListener(view -> {
            mAuthManager.checkProtocol(mBinding.cbProtocol.isChecked(), new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    startActivity(new Intent(LaunchActivity.this, LoginWithEmailActivity.class));
                }

                @Override
                public void onError(String err) {
                    MyToast.showToast(LaunchActivity.this, err);
                }
            });
        });
    }

    private void initBottomSheetDialog() {
        View anotherMethod = getLayoutInflater().inflate(R.layout.another_login_method_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(anotherMethod);
        bottomSheetDialog.show();
        anotherMethod.setOnClickListener(view -> {

        });
    }

}