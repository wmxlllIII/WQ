package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.databinding.LoginMainLayoutBinding;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.MyToast;

public class LaunchActivity extends BaseActivity<LoginMainLayoutBinding>  {
    private AuthManager mAuthManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 后台验证登陆
        if (isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_main_layout;
    }

    private boolean isLogin() {
        return SPManager.getUserInfo(this).isLogin();
    }


    private void initData() {
        mAuthManager = new AuthManager();
        UserInfo userInfo = SPManager.getUserInfo(this);
        boolean isLogin = userInfo.isLogin();
        if (isLogin) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
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