package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.memory.wq.R;
import com.memory.wq.adapters.PostCommentAdapter;
import com.memory.wq.adapters.PostImagesAdapter;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.ReplyCommentInfo;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

public class PostInfoActivity extends BaseActivity {

    private ImageView iv_back;
    private ImageView iv_avatar;
    private ImageView iv_share;
    private TextView tv_nickname;
    private TextView tv_follow;
    private PostInfo postInfo;
    private RecyclerView rv_comments;
    private ViewPager2 vp_post_images;
    private TextView tv_post_title;
    private TextView tv_post_content;
    private TextView tv_likecount;

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

// 创建子评论列表
        List<ReplyCommentInfo> replyInfoList = new ArrayList<>();
        ReplyCommentInfo info = new ReplyCommentInfo();
        info.setUserName("ReplyInfo_name");
        info.setReplyToUser("ReplyInfo_toUser");
        info.setContent("reply content");
        replyInfoList.add(info);
        replyInfoList.add(info);

        // 创建主评论列表
        List<PostCommentInfo> commentInfoList = new ArrayList<>();
        PostCommentInfo commentInfo = new PostCommentInfo();
        commentInfo.setContent("主评论");
        commentInfo.setReplies(replyInfoList);
        commentInfo.setExpanded(true);
        commentInfo.setUserName("comm name");
        commentInfoList.add(commentInfo);
        commentInfoList.add(commentInfo);
        commentInfoList.add(commentInfo);

        // 绑定适配器
        PostCommentAdapter adapter = new PostCommentAdapter(this, commentInfoList);
        rv_comments.setLayoutManager(new LinearLayoutManager(this));
        rv_comments.setAdapter(adapter);

        // 设置监听器（如果需要）
        adapter.setOnCommentActionListener(new PostCommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyToComment(PostCommentInfo comment) {
                // 点击主评论的“回复”
                MyToast.showToast(PostInfoActivity.this, "点击主评论的“回复" + comment);
            }

            @Override
            public void onReplyToReply(ReplyCommentInfo reply) {
                // 点击子评论的“回复”
                MyToast.showToast(PostInfoActivity.this, "点击子评论的“回复" + reply);
            }
        });
    }

    private void setData() {
        Glide.with(this)
                .load(postInfo.getPoster())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.load_failure)
                .into(iv_avatar);
        tv_post_title.setText(postInfo.getTitle());
        tv_post_content.setText(postInfo.getContent());
        PostImagesAdapter imagesAdapter = new PostImagesAdapter(this, postInfo.getContentImagesUrlList());
        vp_post_images.setAdapter(imagesAdapter);
        vp_post_images.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);


    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        vp_post_images = (ViewPager2) findViewById(R.id.vp_post_images);
        tv_post_title = (TextView) findViewById(R.id.tv_post_title);
        tv_post_content = (TextView) findViewById(R.id.tv_post_content);
        rv_comments = (RecyclerView) findViewById(R.id.rv_comments);
        EditText et_comment = (EditText) findViewById(R.id.et_comment);

        LinearLayout ll_Like = (LinearLayout) findViewById(R.id.ll_Like);
        ImageView iv_like = (ImageView) findViewById(R.id.iv_like);
        tv_likecount = (TextView) findViewById(R.id.tv_likecount);

        LinearLayout ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        TextView tv_commentcount = (TextView) findViewById(R.id.tv_commentcount);

    }
}