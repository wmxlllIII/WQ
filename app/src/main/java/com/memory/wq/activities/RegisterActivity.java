package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.MyToast;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = RegisterActivity.class.getName();

    private EditText et_register_email;
    private EditText et_register_pwd1;
    private EditText et_register_pwd2;
    private EditText et_code;
    private Button btn_getcode;
    private Button btn_submitcode;
    private AuthManager authManager;

    private int remainTime = 0;
    private static final int REFRESH_TIME = 1000;
    private static final int TOTAL_TIME = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_account_layout);
        initView();
        initData();
    }

    private void initData() {
        authManager = new AuthManager();
    }

    private void initView() {
        et_register_email = (EditText) findViewById(R.id.et_register_email);
        et_register_pwd1 = (EditText) findViewById(R.id.et_register_pwd1);
        et_register_pwd2 = (EditText) findViewById(R.id.et_register_pwd2);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_getcode = (Button) findViewById(R.id.btn_getcode);
        btn_submitcode = (Button) findViewById(R.id.btn_submitcode);

        btn_getcode.setOnClickListener(this);
        btn_submitcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String email = et_register_email.getText().toString().trim();
        String pwd1 = et_register_pwd1.getText().toString().trim();
        String pwd2 = et_register_pwd2.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_getcode:
                if (remainTime > 0) {
                    return;
                }

                if (authManager.isEmail(email)) {
                    int checkPwd = authManager.checkPwd(pwd1, pwd2);
                    switch (checkPwd) {
                        case AuthManager.EMPTY_PWD:
                            MyToast.showToast(this, "密码不能为空");
                            break;
                        case AuthManager.MISMATCH_PWD:
                            MyToast.showToast(this, "密码不匹配");
                            break;
                        case AuthManager.OK_PWD:
                            btn_getcode.setEnabled(false);
                            getCode(email);
                            break;
                    }
                } else {
                    MyToast.showToast(RegisterActivity.this, "请输入正确邮箱格式");
                }
                break;
            case R.id.btn_submitcode:
                signIn(email, pwd1);
                break;
        }
    }

    private void getCode(String email) {
        authManager.getCode(email, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                runOnUiThread(() -> {
                    MyToast.showToast(RegisterActivity.this, "验证码已发送");
                    startDeadline();
                });
            }

            @Override
            public void onError(String err) {
                runOnUiThread(() -> {
                    MyToast.showToast(RegisterActivity.this, "验证码发送失败");
                    Log.d(TAG, "onError: ====获取验证码错误日志" + err);
                    resetGetCode();
                });
            }

        });

    }

    private void signIn(String email, String pwd1) {
        String code = et_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            MyToast.showToast(this, "请输入验证码");
            return;
        }
        authManager.register(email, Integer.parseInt(code), pwd1, new ResultCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                userInfo.setLogin(true);
                SPManager.saveUserInfo(RegisterActivity.this, userInfo);

                runOnUiThread(() -> {
                    MyToast.showToast(RegisterActivity.this, "登录成功");
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                });
            }

            @Override
            public void onError(String err) {
                runOnUiThread(() -> {
                    MyToast.showToast(RegisterActivity.this, err);

                });
                Log.d(TAG, "onError: ===register错误" + err);
            }
        });

    }

    private void resetGetCode() {
        remainTime = 0;
        if (btn_getcode != null) {
            btn_getcode.setEnabled(true);
            btn_getcode.setText("获取验证码");
        }
    }

    private void startDeadline() {
        remainTime = TOTAL_TIME;
        updateButtonText();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (remainTime > 0) {
                    remainTime--;
                    updateButtonText();
                    handler.postDelayed(this, REFRESH_TIME);
                } else {
                    resetGetCode();
                }
            }
        }, REFRESH_TIME);
    }

    private void updateButtonText() {
        if (btn_getcode != null) {
            btn_getcode.setText(remainTime + "秒后重试");
        }
    }

}