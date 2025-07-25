package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.MyToast;

public class LaunchActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_visitor;
    private Button btn_login_email;
    private TextView tv_another;
    private CheckBox cb_protocol;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.login_main_layout);
        initView();
        initData();
    }

    private boolean isLogin() {
        return SPManager.getUserInfo(this).isLogin();
    }


    private void initData() {
        authManager = new AuthManager();
        UserInfo userInfo = SPManager.getUserInfo(this);
        boolean isLogin = userInfo.isLogin();
        if (isLogin) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    private void initView() {
        tv_visitor = findViewById(R.id.tv_visitor);
        btn_login_email = findViewById(R.id.btn_login_email);
        tv_another = findViewById(R.id.tv_another);
        cb_protocol = findViewById(R.id.cb_protocol);


        tv_another.setOnClickListener(this);
        tv_visitor.setOnClickListener(this);
        btn_login_email.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_email:
                authManager.checkProtocol(cb_protocol.isChecked(), new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        startActivity(new Intent(LaunchActivity.this, LoginWithEmailActivity.class));
                    }

                    @Override
                    public void onError(String err) {
                        MyToast.showToast(LaunchActivity.this, err);
                    }
                });
                break;
            case R.id.tv_visitor:
                startActivity(new Intent(this, MainActivity.class));
                finish();

                break;
            case R.id.tv_another:
                initBottomSheetDialog();
                break;
        }
    }

    private void initBottomSheetDialog() {
        View anotherMethod = getLayoutInflater().inflate(R.layout.another_login_method_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(anotherMethod);
        bottomSheetDialog.show();
        anotherMethod.setOnClickListener(this);
    }

}