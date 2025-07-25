package com.memory.wq.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.caches.SmartImageView;
import com.memory.wq.properties.AppProperties;

public class PersonalActivity extends AppCompatActivity {

    private TextView tv_remarks;
    private TextView tv_nickname;
    private TextView tv_email;
    private TextView tv_uunum;
    private TextView tv_region;
    private Button btn_message;
    private Button btn_call;
    private SmartImageView siv_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        FriendInfo friendInfo = (FriendInfo) intent.getSerializableExtra(AppProperties.FRIENDINFO);
        String email = friendInfo.getEmail();
        String avatarUrl = friendInfo.getAvatarUrl();
        String nickname = friendInfo.getNickname();
        //TODO
        if (TextUtils.isEmpty(email)) {
            tv_email.setVisibility(View.GONE);
        }
        tv_email.setText(email);

        if (TextUtils.isEmpty(nickname)) {
            tv_nickname.setVisibility(View.GONE);
        }
        tv_nickname.setText(nickname);

        if (TextUtils.isEmpty(avatarUrl)) {
            siv_avatar.setVisibility(View.GONE);
        }
        siv_avatar.setImageUrl(avatarUrl);

    }

    private void initView() {
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_uunum = (TextView) findViewById(R.id.tv_uunum);
        tv_region = (TextView) findViewById(R.id.tv_region);
        btn_message = (Button) findViewById(R.id.btn_message);
        btn_call = (Button) findViewById(R.id.btn_call);
        siv_avatar = (SmartImageView) findViewById(R.id.siv_avatar);
    }
}