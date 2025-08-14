package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.memory.wq.beans.ReplyCommentInfo;
import com.memory.wq.managers.CommentManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostInfoActivity extends BaseActivity {

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
    private List<PostCommentInfo> mCommentInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        initView();
        initData();
    }

    private void initData() {
        String token = SPManager.getUserInfo(this).getToken();
        Intent intent = getIntent();
        postInfo = (PostInfo) intent.getParcelableExtra(AppProperties.POSTINFO);
        setData();

//        // 创建子评论列表
//        List<ReplyCommentInfo> replyInfoList = new ArrayList<>();
//        ReplyCommentInfo info = new ReplyCommentInfo();
//        info.setUserName("子评论用户名");
//        info.setReplyToUser("被回复者");
//        info.setContent("回复的消息");
//        replyInfoList.add(info);
//        replyInfoList.add(info);
//
//        // 创建主评论列表
//        mCommentInfoList = new ArrayList<>();
//        PostCommentInfo commentInfo = new PostCommentInfo();
//        commentInfo.setContent("主评论");
//        commentInfo.setReplies(replyInfoList);
//        commentInfo.setExpanded(false);
//        commentInfo.setUserName("评论人");
//        mCommentInfoList.add(commentInfo);
//        mCommentInfoList.add(commentInfo);
//        mCommentInfoList.add(commentInfo);
        CommentManager commentManager = new CommentManager();
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
        // 绑定适配器
        PostCommentAdapter adapter = new PostCommentAdapter(this, mCommentInfoList);
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
        EditText et_comment = (EditText) findViewById(R.id.et_comment);

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
}