package com.memory.wq.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.memory.wq.R;
import com.memory.wq.adapters.SelectImageAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.utils.GlideEngine;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditPostActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "EditPostActivity";

    private TextView tv_publish;
    private EditText et_content;
    private EditText et_title;
    private String token;
    private List<File> postImagesList = new ArrayList<>();
    private PostManager postManager;
    private RecyclerView rv_select_images;
    private SelectImageAdapter adapter;
    private PermissionManager permissionManager;
    public static final int PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recommend);
        initView();
        initData();
    }

    private void initData() {
        permissionManager = new PermissionManager(this);
        postManager = new PostManager();
        token = SPManager.getUserInfo(this).getToken();
        adapter = new SelectImageAdapter(postImagesList);
        adapter.setOnAddClickListener(new SelectImageAdapter.OnAddOrRemoveClickListener() {
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
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, postImagesList.size() - position);
            }
        });

        rv_select_images.setLayoutManager(new GridLayoutManager(this, 3));
        rv_select_images.setAdapter(adapter);
    }

    private boolean hasPermission() {
        return permissionManager.isPermitPermission(Manifest.permission.READ_MEDIA_IMAGES);
    }

    private void initView() {
        tv_publish = (TextView) findViewById(R.id.tv_publish);
        et_content = (EditText) findViewById(R.id.et_content);
        et_title = (EditText) findViewById(R.id.et_title);
        rv_select_images = (RecyclerView) findViewById(R.id.rv_select_images);
        tv_publish.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_publish:
                publishPost();
                break;
        }
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
        String content = et_content.getText().toString().trim();
        String title = et_title.getText().toString().trim();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
            Log.d(TAG, "===[x] publishPost #127");
            return;
        }

        PostInfo postInfo = new PostInfo();
        postInfo.setContent(content);
        postInfo.setTitle(title);
        postManager.publishPost(token, postInfo, postImagesList, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                runOnUiThread(() -> {
                    MyToast.showToast(EditPostActivity.this, "发布成功");
                    finish();
                });
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "===[x] publishPost #145");
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
                    adapter.notifyDataSetChanged();
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