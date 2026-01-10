package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.databinding.RegisterAccountLayoutBinding;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.MyToast;

public class RegisterActivity extends BaseActivity<RegisterAccountLayoutBinding> {
    public static final String TAG = RegisterActivity.class.getName();
    private AuthManager mAuthManager;

    private int mRemainTime = 0;
    private static final int REFRESH_TIME = 1000;
    private static final int TOTAL_TIME = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.register_account_layout;
    }

    private void initData() {
        mAuthManager = new AuthManager();
    }

    private void initView() {

        mBinding.btnGetcode.setOnClickListener(view -> {
            String email = mBinding.etRegisterEmail.getText().toString().trim();
            String pwd1 = mBinding.etRegisterPwd1.getText().toString().trim();
            String pwd2 = mBinding.etRegisterPwd2.getText().toString().trim();
            if (mRemainTime > 0) {
                return;
            }

            if (mAuthManager.isValidAccount(email)) {
                int checkPwd = mAuthManager.checkPwd(pwd1, pwd2);
                switch (checkPwd) {
                    case AuthManager.EMPTY_PWD:
                        MyToast.showToast(this, "密码不能为空");
                        break;
                    case AuthManager.MISMATCH_PWD:
                        MyToast.showToast(this, "密码不匹配");
                        break;
                    case AuthManager.OK_PWD:
                        mBinding.btnGetcode.setEnabled(false);
                        getCode(email);
                        break;
                }
            } else {
                MyToast.showToast(RegisterActivity.this, "请输入正确邮箱格式");
            }
        });

        mBinding.tvRegister.setOnClickListener(view -> {
            String email = mBinding.etRegisterEmail.getText().toString().trim();
            String pwd = mBinding.etRegisterPwd1.getText().toString().trim();
            signIn(email, pwd);
        });
    }


    private void getCode(String email) {
        mAuthManager.getCode(email, new ResultCallback<Boolean>() {
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
        String code = mBinding.etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            MyToast.showToast(this, "请输入验证码");
            return;
        }
        mAuthManager.register(email, Integer.parseInt(code), pwd1, new ResultCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
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
        mRemainTime = 0;
        if (mBinding.btnGetcode != null) {
            mBinding.btnGetcode.setEnabled(true);
            mBinding.btnGetcode.setText("获取验证码");
        }
    }

    private void startDeadline() {
        mRemainTime = TOTAL_TIME;
        updateButtonText();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRemainTime > 0) {
                    mRemainTime--;
                    updateButtonText();
                    handler.postDelayed(this, REFRESH_TIME);
                } else {
                    resetGetCode();
                }
            }
        }, REFRESH_TIME);
    }

    private void updateButtonText() {
        mBinding.btnGetcode.setText(mRemainTime + "秒后重试");
    }

}