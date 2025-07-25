package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.provider.UserSqlOP;
import com.memory.wq.utils.MyToast;

public class LoginWithEmailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private ImageView iv_password_visible;
    private EditText et_login_account;
    private EditText et_login_pwd;
    private TextView tv_signin;
    private TextView tv_forgetpassword;
    private Button btn_login;

    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);
        initView();
        initData();
    }

    private void initData() {
        authManager = new AuthManager();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_password_visible = (ImageView) findViewById(R.id.iv_password_visible);
        et_login_account = (EditText) findViewById(R.id.et_login_account);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        tv_signin = (TextView) findViewById(R.id.tv_signin);
        tv_forgetpassword = (TextView) findViewById(R.id.tv_forgetpassword);
        btn_login = (Button) findViewById(R.id.btn_login);

        tv_signin.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_signin:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                login();
                break;


        }
    }

    private void login() {
        String email = et_login_account.getText().toString().trim();
        String pwd = et_login_pwd.getText().toString().trim();

        boolean isEmail = authManager.isEmail(email);
        boolean isPwd = authManager.isPwd(pwd);
        if (!isEmail) {
            MyToast.showToast(this, "邮箱格式不正确");
            return;
        }
        if (!isPwd) {
            MyToast.showToast(this, "请输入密码");
            return;
        }

        authManager.login(email, pwd, new ResultCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                SPManager.saveUserInfo(LoginWithEmailActivity.this, userInfo);
                //TODO
                UserSqlOP sqlOP = new UserSqlOP(LoginWithEmailActivity.this);
                sqlOP.insertUser(userInfo);

                System.out.println("================回调token: " + userInfo.getToken() + userInfo.getEmail());
                runOnUiThread(() -> {
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