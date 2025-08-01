package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.properties.AppProperties;

public class PostInfoActivity extends BaseActivity {

    private ImageView iv_back;
    private ImageView iv_avatar;
    private ImageView iv_share;
    private TextView tv_nickname;
    private TextView tv_follow;
    private PostInfo postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        postInfo = (PostInfo) intent.getParcelableExtra(AppProperties.POSTINFO);
        setData();
    }

    private void setData() {
        postInfo.setTitle(postInfo.getTitle());

    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        ViewPager2 vp_post_images = (ViewPager2) findViewById(R.id.vp_post_images);
        TextView tv_post_title = (TextView) findViewById(R.id.tv_post_title);
        TextView tv_post_content = (TextView) findViewById(R.id.tv_post_content);
        RecyclerView rv_comments = (RecyclerView) findViewById(R.id.rv_comments);
        TextInputEditText et_comment = (TextInputEditText) findViewById(R.id.et_comment);

        LinearLayout ll_Like = (LinearLayout) findViewById(R.id.ll_Like);
        ImageView iv_like = (ImageView) findViewById(R.id.iv_like);
        TextView tv_likecount = (TextView) findViewById(R.id.tv_likecount);

        LinearLayout ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        TextView tv_commentcount = (TextView) findViewById(R.id.tv_commentcount);

    }
}