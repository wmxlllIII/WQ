package com.memory.wq.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.memory.wq.R;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.managers.UserManager;
import com.memory.wq.properties.AppProperties;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_ok;
    private EditText et_nickname;
    private EditText et_phone;
    private EditText et_email;
    private EditText et_signature;
    private SharedPreferences sp;
    private String token;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();
        initData();
        showUI();
    }

    private void showUI() {

        String userName = sp.getString("userName", "");
        String email = sp.getString("email", "");
        et_nickname.setHint(userName);
        et_email.setHint(email);
    }

    private void initData() {
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        userManager = new UserManager(this);
    }

    private void initView() {
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_signature = (EditText) findViewById(R.id.et_signature);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                String nickname = et_nickname.getText().toString().trim();
                if (TextUtils.isEmpty(nickname))
                    return;
                UserInfo userInfo = new UserInfo();
                userInfo.setUserName(nickname);
                userManager.updateUserInfo(token,userInfo);
        }
    }
}