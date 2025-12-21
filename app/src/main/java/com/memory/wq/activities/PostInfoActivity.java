package com.memory.wq.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.adapters.PostCommentAdapter;
import com.memory.wq.adapters.PostImagesAdapter;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.databinding.ActivityPostInfoBinding;
import com.memory.wq.managers.CommentManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class PostInfoActivity extends BaseActivity<ActivityPostInfoBinding> {

    public static final String TAG = "PostInfoActivity";
    private PostInfo mPostInfo;
    private final List<PostCommentInfo> mCommentInfoList = new ArrayList<>();
    private CommentManager mCommentManager;
    private String token;
    private PostCommentInfo mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_info;
    }

    private void initData() {
        token = SPManager.getUserInfo().getToken();
        Intent intent = getIntent();
        mPostInfo = (PostInfo) intent.getParcelableExtra(AppProperties.POSTINFO);
        setData();

        mCommentManager = new CommentManager();
        QueryPostInfo queryPostInfo = new QueryPostInfo();
        queryPostInfo.setPage(1);
        queryPostInfo.setSize(10);
        mCommentManager.getCommentByPostId(token, mPostInfo.getPostId(), queryPostInfo, new ResultCallback<List<PostCommentInfo>>() {
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
        mBinding.rvComments.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvComments.setAdapter(adapter);

        adapter.setOnCommentActionListener(new PostCommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyToComment(PostCommentInfo comment) {
                PostInfoActivity.this.mComment = comment;
                showKeyboard(mBinding.etComment);
                MyToast.showToast(PostInfoActivity.this, "点击了回复" + comment.getUserName());
            }
        });
    }

    private void setData() {
        if (mPostInfo == null) {
            Log.d(TAG, "===[x] initData #106");
            MyToast.showToast(this, "帖子数据加载失败");
            return;
        }

        Glide.with(this)
                .load(AppProperties.HTTP_SERVER_ADDRESS + mPostInfo.getPosterAvatar())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(mBinding.ivAvatar);
        //标题
        mBinding.tvPostTitle.setText(mPostInfo.getTitle());
        //内容
        mBinding.tvPostContent.setText(mPostInfo.getContent());
        //图片列表
        Log.d(TAG, "===postInfo" + mPostInfo);
        PostImagesAdapter imagesAdapter = new PostImagesAdapter(this, mPostInfo.getContentImagesUrlList());
        mBinding.vpPostImages.setAdapter(imagesAdapter);
        mBinding.vpPostImages.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        //喜欢数量
        mBinding.tvLikeCount.setText(String.valueOf(mPostInfo.getLikeCount()));
        mBinding.tvNickname.setText(mPostInfo.getPoster());

    }

    private void initView() {
        mBinding.bottomInputBar.setOnClickListener(view -> {
            if (AccountManager.isVisitorUser()) {
                new AlertDialog.Builder(this)
                        .setTitle("未登录")
                        .setMessage("登录后即可体验完整功能哦~")
                        .setIcon(R.mipmap.ic_bannertest2)
                        .setNegativeButton("去登录", (dialogInterface, i) -> {
                            startActivity(new Intent(this, LaunchActivity.class));
                        })
                        .setPositiveButton("取消", null)
                        .setCancelable(false)
                        .show();
            }
        });

        mBinding.tvSend.setOnClickListener(view -> {
            String commentContent = mBinding.etComment.getText().toString().trim();
            if (commentContent.isEmpty()) {
                MyToast.showToast(PostInfoActivity.this, "评论内容不能为空");
                return;
            }

            if (mComment == null) {
                sendComment(commentContent);
            } else {
                sendReplyComment(commentContent);
            }
        });

        mBinding.ivBack.setOnClickListener(View -> {
            finish();
        });

    }

    private void sendComment(String content) {
        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPostId(mPostInfo.getPostId());
        postCommentInfo.setParentId(-1);
        postCommentInfo.setContent(content);
        addComment(postCommentInfo);
    }

    private void sendReplyComment(String content) {
        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPostId(mComment.getPostId());
        postCommentInfo.setParentId(mComment.getCommentId());
        postCommentInfo.setReplyToUserId(mComment.getUserId());
        postCommentInfo.setContent(content);

        addComment(postCommentInfo);
    }

    private void addComment(PostCommentInfo postCommentInfo) {
        mCommentManager.addComment(token, postCommentInfo, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mBinding.etComment.setText("");
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