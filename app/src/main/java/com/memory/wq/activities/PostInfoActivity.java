package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.adapters.PostCommentAdapter;
import com.memory.wq.adapters.PostImagesAdapter;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.managers.CommentManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostInfoActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "PostInfoActivity";

    private ImageView iv_back;
    private CircleImageView iv_avatar;
    private ImageView iv_share;
    private TextView tv_nickname;
    private TextView tv_follow;
    private PostInfo postInfo;
    private RecyclerView rv_comments;
    private ViewPager2 vp_post_images;
    private TextView tv_post_title;
    private TextView tv_post_content;
    private TextView tv_like_count;
    private TextView tv_msg_count;
    private List<PostCommentInfo> mCommentInfoList = new ArrayList<>();
    private TextView tv_send;
    private EditText et_comment;
    private CommentManager commentManager;
    private String token;
    private PostCommentInfo comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        initView();
        initData();
    }

    private void initData() {
        token = SPManager.getUserInfo(this).getToken();
        Intent intent = getIntent();
        postInfo = (PostInfo) intent.getParcelableExtra(AppProperties.POSTINFO);
        setData();

        commentManager = new CommentManager();
        QueryPostInfo queryPostInfo = new QueryPostInfo();
        queryPostInfo.setPage(1);
        queryPostInfo.setSize(10);
        commentManager.getCommentByPostId(token, postInfo.getPostId(), queryPostInfo, new ResultCallback<List<PostCommentInfo>>() {
            @Override
            public void onSuccess(List<PostCommentInfo> result) {
                if (result != null && result.size() > 0) {
                    mCommentInfoList.addAll(result);
                    setCommentData();
                }

            }

            @Override
            public void onError(String err) {

            }
        });


    }

    private void setCommentData() {
        PostCommentAdapter adapter = new PostCommentAdapter(this, mCommentInfoList);
        rv_comments.setLayoutManager(new LinearLayoutManager(this));
        rv_comments.setAdapter(adapter);

        adapter.setOnCommentActionListener(new PostCommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyToComment(PostCommentInfo comment) {
                PostInfoActivity.this.comment = comment;
                showKeyboard(et_comment);
                MyToast.showToast(PostInfoActivity.this, "点击了回复" + comment.getReplyToUserName());
            }
        });
    }

    private void setData() {
        if (postInfo == null) {
            Log.d(TAG, "===[x] initData #106");
            MyToast.showToast(this, "帖子数据加载失败");
            return;
        }

        Glide.with(this)
                .load(postInfo.getPoster())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(iv_avatar);
        //标题
        tv_post_title.setText(postInfo.getTitle());
        //内容
        tv_post_content.setText(postInfo.getContent());
        //图片列表
        PostImagesAdapter imagesAdapter = new PostImagesAdapter(this, postInfo.getContentImagesUrlList());
        vp_post_images.setAdapter(imagesAdapter);
        vp_post_images.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        //喜欢数量
        tv_like_count.setText(String.valueOf(postInfo.getLikeCount()));


    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_avatar = (CircleImageView) findViewById(R.id.iv_avatar);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        vp_post_images = (ViewPager2) findViewById(R.id.vp_post_images);
        tv_post_title = (TextView) findViewById(R.id.tv_post_title);
        tv_post_content = (TextView) findViewById(R.id.tv_post_content);
        rv_comments = (RecyclerView) findViewById(R.id.rv_comments);
        et_comment = (EditText) findViewById(R.id.et_comment);
        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_send.setOnClickListener(this);

        LinearLayout ll_Like = (LinearLayout) findViewById(R.id.ll_like);
        ImageView iv_like = (ImageView) findViewById(R.id.iv_like);
        tv_like_count = (TextView) findViewById(R.id.tv_like_count);

        LinearLayout ll_msg = (LinearLayout) findViewById(R.id.ll_msg);
        ImageView iv_msg = (ImageView) findViewById(R.id.iv_msg);
        tv_msg_count = (TextView) findViewById(R.id.tv_msg_count);

        iv_back.setOnClickListener(View -> {
            finish();
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send:
                String commentContent = et_comment.getText().toString().trim();
                if (commentContent.isEmpty()) {
                    MyToast.showToast(PostInfoActivity.this, "评论内容不能为空");
                    return;
                }

                if (comment == null) {
                    sendComment(commentContent);
                } else {
                    sendReplyComment(commentContent);
                }
        }
    }

    private void sendComment(String content) {
        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPostId(postInfo.getPostId());
        postCommentInfo.setParentId(-1);
        postCommentInfo.setContent(content);
        addComment(postCommentInfo);
    }

    private void sendReplyComment(String content) {
        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPostId(comment.getPostId());
        postCommentInfo.setParentId(comment.getCommentId());
        postCommentInfo.setReplyToUserId(comment.getUserId());
        postCommentInfo.setContent(content);

        addComment(postCommentInfo);
    }

    private void addComment(PostCommentInfo postCommentInfo) {
        commentManager.addComment(token, postCommentInfo, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                et_comment.setText("");
                hideKeyboard();
                MyToast.showToast(PostInfoActivity.this, "发布成功");

            }

            @Override
            public void onError(String err) {
                MyToast.showToast(PostInfoActivity.this, "发布失败");
            }
        });
    }
}