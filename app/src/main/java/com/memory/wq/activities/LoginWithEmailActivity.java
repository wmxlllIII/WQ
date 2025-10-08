package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.databinding.ActivityLoginWithEmailBinding;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.provider.UserSqlOP;
import com.memory.wq.utils.MyToast;

public class LoginWithEmailActivity extends BaseActivity<ActivityLoginWithEmailBinding> {

    private static final String TAG = LoginWithEmailActivity.class.getName();
    private AuthManager mAuthManager;


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
        mBinding.tvSignin.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        mBinding.ivBack.setOnClickListener(view -> {
            finish();
        });
        mBinding.btnLogin.setOnClickListener(view -> {
            login();
        });
    }

    private void login() {
        String email = mBinding.etLoginAccount.getText().toString().trim();
        String pwd = mBinding.etLoginPwd.getText().toString().trim();

        boolean isEmail = mAuthManager.isEmail(email);
        boolean isPwd = mAuthManager.isPwd(pwd);
        if (!isEmail) {
            MyToast.showToast(this, "邮箱格式不正确");
            return;
        }
        if (!isPwd) {
            MyToast.showToast(this, "请输入密码");
            return;
        }

        mAuthManager.login(email, pwd, new ResultCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                SPManager.saveUserInfo(LoginWithEmailActivity.this, userInfo);
                //TODO
                UserSqlOP sqlOP = new UserSqlOP(LoginWithEmailActivity.this);
                sqlOP.insertUser(userInfo);

                System.out.println("================回调token: " + userInfo.getToken() + userInfo.getEmail());
                runOnUiThread(() -> {
                    AccountManager.saveLoginState(LoginWithEmailActivity.this, AccountManager.UserType.USER_TYPE_USER);
                    MyToast.showToast(LoginWithEmailActivity.this, "登录成功");
                    Intent intent = new Intent(LoginWithEmailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });

            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    MyToast.showToast(LoginWithEmailActivity.this, "登录失败:" + error);
                });

            }
        });

    }
}