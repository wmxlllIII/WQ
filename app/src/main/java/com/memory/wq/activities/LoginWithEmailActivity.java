package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.databinding.ActivityLoginWithEmailBinding;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.db.op.UserSqlOP;
import com.memory.wq.utils.MyToast;

public class LoginWithEmailActivity extends BaseActivity<ActivityLoginWithEmailBinding> {

    private static final String TAG = LoginWithEmailActivity.class.getName();
    private AuthManager mAuthManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_with_email;
    }

    private void initData() {
        mAuthManager = new AuthManager();
    }

    private void initView() {
        mBinding.tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        mBinding.ivBack.setOnClickListener(view -> {
            finish();
        });
        mBinding.tvLogin.setOnClickListener(view -> {
            login();
        });
        mBinding.ivPasswordVisible.setOnClickListener(v -> {
            togglePasswordVisibility();
        });

        mBinding.etLoginAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.etLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void login() {
        String account = mBinding.etLoginAccount.getText().toString().trim();
        String pwd = mBinding.etLoginPwd.getText().toString().trim();
        if (!mAuthManager.isValidAccount(account)) {
            MyToast.showToast(this, "邮箱格式不正确");
            return;
        }

        if (!mAuthManager.isValidPwd(pwd)) {
            MyToast.showToast(this, "请输入密码");
            return;
        }

        mAuthManager.login(account, pwd, new ResultCallback<UserInfo>() {

            @Override
            public void onSuccess(UserInfo userInfo) {
                SPManager.saveUserInfo(LoginWithEmailActivity.this, userInfo);
                //TODO
                UserSqlOP sqlOP = new UserSqlOP(LoginWithEmailActivity.this);
                sqlOP.insertUser(userInfo);
                Log.d(TAG, "[✓] login #78 " + userInfo);

                AccountManager.saveLoginState(LoginWithEmailActivity.this, AccountManager.UserType.USER_TYPE_USER);
                MyToast.showToast(LoginWithEmailActivity.this, "登录成功");
                Intent intent = new Intent(LoginWithEmailActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                MyToast.showToast(LoginWithEmailActivity.this, "登录失败:" + error);
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            mBinding.etLoginPwd.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mBinding.ivPasswordVisible.setImageResource(R.mipmap.ic_eye_closed);
            isPasswordVisible = false;
        } else {
            mBinding.etLoginPwd.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mBinding.ivPasswordVisible.setImageResource(R.mipmap.ic_eye_open);
            isPasswordVisible = true;
        }

        mBinding.etLoginPwd.setSelection(mBinding.etLoginPwd.getText().length());
    }

}