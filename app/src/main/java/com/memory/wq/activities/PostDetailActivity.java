package com.memory.wq.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.adapters.PostCommentAdapter;
import com.memory.wq.adapters.PostImagesAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.PostDetailInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityPostInfoBinding;
import com.memory.wq.interfaces.OnCommentActionListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.CommentManager;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends BaseActivity<ActivityPostInfoBinding> {

    public static final String TAG = "WQ_PostDetailActivity";
    private int mPostId;
    private final CommentManager mCommentManager = new CommentManager();
    private final PostManager mPostManager = new PostManager();
    private final UserManager mUserManager = new UserManager();
    private final PostImagesAdapter mPostImagesAdapter = new PostImagesAdapter();
    private final PostCommentAdapter mCommentAdapter = new PostCommentAdapter(new OnCommentActionListenerImpl());
    private PostCommentInfo mComment;
    private int mCurrentPage = 1;
    private static final int PAGE_SIZE = 10;
    private FriendInfo mPoster;
    private final FriendManager mFriendManager = new FriendManager();

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
        Intent intent = getIntent();
        mPostId = intent.getIntExtra(AppProperties.POSTID, -1);
        if (mPostId == -1) {
            Log.d(TAG, "[x] initData #62");
            return;
        }

        mPostManager.saveFootprintPost(mPostId);
        loadDetail();
        loadComments(1, true);
    }

    private void loadDetail() {
        mPostManager.getPostDetail(mPostId, new ResultCallback<PostDetailInfo>() {

            @Override
            public void onSuccess(PostDetailInfo postDetail) {
                updatePostUI(postDetail);

                mUserManager.getUserById(postDetail.getPosterId(), new ResultCallback<FriendInfo>() {
                    @Override
                    public void onSuccess(FriendInfo user) {
                        mPoster = user;
                        updatePosterUI(user);
                    }

                    @Override
                    public void onError(String err) {

                    }
                });

            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void updatePosterUI(FriendInfo user) {
        Glide.with(this)
                .load(user.getAvatarUrl())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .transform(new RoundedCorners(12))
                .into(mBinding.ivAvatar);

        mBinding.tvNickname.setText(user.getNickname());

        mBinding.ivAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(PostDetailActivity.this, PersonInfoActivity.class);
            intent.putExtra(AppProperties.PERSON_ID, user.getUuNumber());
            startActivity(intent);
        });

        mBinding.tvFollow.setText(AccountManager.getUserId() == user.getUuNumber() ? "删除" : user.isFollow() ? "取消关注" : "关注");
    }

    private void updatePostUI(PostDetailInfo postDetail) {
        if (postDetail == null) {
            Log.d(TAG, "[x] updateUI #106");
            return;
        }

        mBinding.tvPostTitle.setText(postDetail.getPostTitle());
        mBinding.tvPostContent.setText(postDetail.getPostContent());

        mBinding.tvLikeCount.setText(String.valueOf(postDetail.getLikeCount()));
        mBinding.ivLike.setImageResource(postDetail.isLiked() ? R.mipmap.icon_like_full : R.mipmap.icon_like_empty);
        mBinding.tvMsgCount.setText(String.valueOf(postDetail.getCommentCount()));
        mPostImagesAdapter.submitList(postDetail.getContentImagesUrlList());
    }

    private void initView() {
        mBinding.rvComments.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvComments.setAdapter(mCommentAdapter);

        mBinding.vpPostImages.setAdapter(mPostImagesAdapter);
        mBinding.vpPostImages.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

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
                MyToast.showToast(PostDetailActivity.this, "评论内容不能为空");
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


        mBinding.llLike.setOnClickListener(view -> {
            mPostManager.likePostIfNeed(mPostId, new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    loadDetail();
                }

                @Override
                public void onError(String err) {
                    MyToast.showToast(PostDetailActivity.this, "点赞失败");
                }
            });
        });
        mBinding.llMsg.setOnClickListener(v -> {
            mBinding.nestedScroll.post(() ->
                    mBinding.nestedScroll.smoothScrollTo(
                            0,
                            mBinding.rvComments.getTop()
                    )
            );
        });

        mBinding.tvFollow.setOnClickListener(v -> {
            if (mPoster == null) {
                Log.d(TAG, "[x] initView #202");
                return;
            }

            if (AccountManager.getUserId() == mPoster.getUuNumber()) {
                mPostManager.deletePostById(mPostId, new ResultCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        MyToast.showToast(PostDetailActivity.this, "删除成功");
                        finish();
                    }

                    @Override
                    public void onError(String err) {
                        MyToast.showToast(PostDetailActivity.this, "删除失败,请稍后再试");
                    }
                });
            } else {
                if (!mPoster.isFollow()) {
                    followUser(mPoster.getUuNumber());
                } else {
                    unfollowUser(mPoster.getUuNumber());
                }
            }
        });
    }

    private void sendComment(String content) {
        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPostId(mPostId);
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
        mCommentManager.addComment(postCommentInfo, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mBinding.etComment.setText("");
                hideKeyboard();
                MyToast.showToast(PostDetailActivity.this, "发布成功");

                loadComments(1, true);

            }

            @Override
            public void onError(String err) {
                MyToast.showToast(PostDetailActivity.this, "发布失败");
            }
        });
    }

    private void loadComments(int page, boolean isRefresh) {

        QueryPostInfo queryPostInfo = new QueryPostInfo();
        queryPostInfo.setPage(page);
        queryPostInfo.setSize(PAGE_SIZE);

        mCommentManager.getCommentByPostId(
                mPostId,
                queryPostInfo,
                new ResultCallback<List<PostCommentInfo>>() {

                    @Override
                    public void onSuccess(List<PostCommentInfo> result) {

                        if (result == null) return;

                        if (isRefresh) {
                            mCommentAdapter.submitList(result);
                            mCurrentPage = 1;
                        } else {
                            List<PostCommentInfo> current = mCommentAdapter.getCurrentList();
                            List<PostCommentInfo> newList = new ArrayList<>(current);
                            newList.addAll(result);

                            mCommentAdapter.submitList(newList);
                            mCurrentPage++;
                        }
                    }

                    @Override
                    public void onError(String err) {
                    }
                }
        );
    }

    private void followUser(long userId) {
        mFriendManager.followUser(userId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mPoster.setFollow(true);
                mBinding.tvFollow.setText("取消关注");
            }

            @Override
            public void onError(String err) {
                MyToast.showToast(PostDetailActivity.this, "关注失败");
            }
        });
    }

    private void unfollowUser(long userId) {
        mFriendManager.unfollowUser(userId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mPoster.setFollow(false);
                mBinding.tvFollow.setText("关注");
            }

            @Override
            public void onError(String err) {
                MyToast.showToast(PostDetailActivity.this, "取消关注失败");
            }
        });
    }

    private class OnCommentActionListenerImpl implements OnCommentActionListener {

        @Override
        public void onReplyToComment(PostCommentInfo comment) {
            mComment = comment;
            showKeyboard(mBinding.etComment);
            MyToast.showToast(PostDetailActivity.this, "点击了回复" + comment.getUserName());
        }
    }
}