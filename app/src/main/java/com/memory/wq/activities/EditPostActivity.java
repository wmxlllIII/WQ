package com.memory.wq.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.memory.wq.R;
import com.memory.wq.adapters.SelectImageAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.databinding.ActivityEditRecommendBinding;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.GlideEngine;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditPostActivity extends BaseActivity<ActivityEditRecommendBinding> {

    public static final String TAG = "EditPostActivity";

    private String token;
    private final List<File> postImagesList = new ArrayList<>();
    private PostManager mPostManager;
    private SelectImageAdapter mAdapter;
    private PermissionManager permissionManager;
    public static final int PERMISSION_REQUEST_CODE = 0;

    public EditPostActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_recommend;
    }

    private void initData() {
        permissionManager = new PermissionManager(this);
        mPostManager = new PostManager();
        token = SPManager.getUserInfo(this).getToken();
        mAdapter = new SelectImageAdapter(postImagesList);
        mAdapter.setOnAddClickListener(new SelectImageAdapter.OnAddOrRemoveClickListener() {
            @Override
            public void onAddClick() {
                if (hasPermission()) {
                    selectPostImages();
                } else {
                    permissionManager.requestPermission(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
                }
            }

            @Override
            public void onRemoveClick(int position) {
                postImagesList.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, postImagesList.size() - position);
            }
        });

        mBinding.rvSelectImages.setLayoutManager(new GridLayoutManager(this, 3));
        mBinding.rvSelectImages.setAdapter(mAdapter);
    }

    private boolean hasPermission() {
        return permissionManager.isPermitPermission(Manifest.permission.READ_MEDIA_IMAGES);
    }

    private void initView() {

        mBinding.tvPublish.setOnClickListener(v -> {
            mBinding.tvPublish.setEnabled(false);
            publishPost();
        });


    }


    private void selectPostImages() {
        int remainCount = 9 - postImagesList.size();
        if (remainCount <= 0) {
            MyToast.showToast(this, "最多选择9张图片");
            Log.d(TAG, "===[x] selectPostImages #96");
            return;
        }

        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setMaxSelectNum(9 - postImagesList.size())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void publishPost() {
        String content = mBinding.etContent.getText().toString().trim();
        String title = mBinding.etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
            Log.d(TAG, "===[x] publishPost #127");
            return;
        }

        PostInfo postInfo = new PostInfo();
        postInfo.setContent(content);
        postInfo.setTitle(title);
        mPostManager.publishPost(token, postInfo, postImagesList, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                    MyToast.showToast(EditPostActivity.this, "发布成功");
                    finish();
                    mBinding.tvPublish.setEnabled(true);
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "===[x] publishPost #145");
                mBinding.tvPublish.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainSelectorList(data);
                    for (LocalMedia localMedia : selectList) {
                        String path = localMedia.getRealPath();
                        if (!TextUtils.isEmpty(path)) {
                            postImagesList.add(new File(path));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (permissionManager.isPermissionGranted(grantResults)) {
                    selectPostImages();
                } else {
                    MyToast.showToast(this, "读取图片权限被拒绝");
                }
        }
    }
}