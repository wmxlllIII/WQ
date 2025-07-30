package com.memory.wq.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.memory.wq.R;

public class PostInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        ImageView iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        ImageView iv_share = (ImageView) findViewById(R.id.iv_share);
        TextView tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        TextView tv_follow = (TextView) findViewById(R.id.tv_follow);
    }
}