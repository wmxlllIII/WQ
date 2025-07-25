package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.memory.wq.R;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.managers.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditRecommendActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_publish;
    private EditText et_content;
    private ImageView iv_images;
    private UserManager userManager;
    private String token;
    private List<File> postImagesList = new ArrayList<>();
    private PostManager postManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recommend);
        initView();
        initData();
    }

    private void initData() {
        userManager = new UserManager(this);
        postManager = new PostManager();
        token = SPManager.getUserInfo(this).getToken();

    }

    private void initView() {
        tv_publish = (TextView) findViewById(R.id.tv_publish);
        et_content = (EditText) findViewById(R.id.et_content);
        iv_images = (ImageView) findViewById(R.id.iv_images);
        iv_images.setOnClickListener(this);
        tv_publish.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_publish:
                publishPost();
                break;
            case R.id.iv_images:
                selectPostImages();
                break;
        }
    }

    private void selectPostImages() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PostManager.REQUEST_POST_IMAGE_CODE);
    }

    private void publishPost() {
        String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(content))
            return;

        int maxTitleLength = 30;
        String title;
        title = content.length() > maxTitleLength ? content.substring(0, maxTitleLength) : content;

        PostInfo postInfo = new PostInfo();
        postInfo.setContent(content);
        postInfo.setTitle(title);
        postManager.publishPost(token, postInfo, postImagesList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> postImagesList = postManager.handlePostImageResult(requestCode, resultCode, data, EditRecommendActivity.this);
        if (postImagesList == null || postImagesList.size() == 0)
            return;

        this.postImagesList = postImagesList;
    }

}