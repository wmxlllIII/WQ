package com.memory.wq.activities;

import static com.memory.wq.managers.UserManager.REQUEST_ALBUM_CODE;
import static com.memory.wq.managers.UserManager.REQUEST_CAMERA_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.memory.wq.R;


import com.memory.wq.caches.SmartImageView;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.managers.UserManager;

import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.MyToast;

import java.io.File;

public class AvatarActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private SmartImageView siv_avatar_detail;
    private TextView tv_alter_avatar;
    private TextView tv_save_avatar;
    private UserManager userManager;
    private String token;
    private String email;
    private ActivityResultLauncher<String> register;
    private FileOP fileOP;


    private String avatarUrl;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar_detail_layout);
        initView();
        initData();
    }


    private void initData() {
        userManager = new UserManager(this);
        fileOP = new FileOP(this);


        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        email = sp.getString("email", "");
        avatarUrl = sp.getString("avatarUrl", "");
        siv_avatar_detail.setImageUrl(avatarUrl);

    }


    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        siv_avatar_detail = (SmartImageView) findViewById(R.id.siv_avatar_detail);
        tv_alter_avatar = (TextView) findViewById(R.id.tv_alter_avatar);
        tv_save_avatar = (TextView) findViewById(R.id.tv_save_avatar);

        // new BottomSheetDialog()

        siv_avatar_detail.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_alter_avatar.setOnClickListener(this);
        tv_save_avatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.siv_avatar_detail:

            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_alter_avatar:
                showImageSourceDialog();
                break;
            case R.id.tv_save_avatar:

                break;

        }
    }

    public void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择图片")
                .setItems(new String[]{"从相册选择", "拍一张"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            userManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_ALBUM);
                            break;
                        case 1:
                            userManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_CAMERA);
                            break;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermit;
        switch (requestCode) {
            case REQUEST_ALBUM_CODE:
                isPermit = userManager.handleRequestPermission(AvatarActivity.this, grantResults, SelectImageType.IMAGE_FROM_ALBUM);
                if (!isPermit)
                    MyToast.showToast(this, "noPermit Album");
                break;
            case REQUEST_CAMERA_CODE:
                isPermit = userManager.handleRequestPermission(AvatarActivity.this, grantResults, SelectImageType.IMAGE_FROM_CAMERA);
                if (!isPermit)
                    MyToast.showToast(this, "noPermit Camera");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = userManager.handleAvatarResult(requestCode, resultCode, data, this);
        if (uri != null) {
            File file = fileOP.handleImage(uri);
            uploadAvatar(file);
        }
    }

    private void uploadAvatar(File file) {
        userManager.upLoadAvatar(file, AppProperties.UPLOAD_URL, token, new ResultCallback<String>() {
            @Override
            public void onSuccess(String path) {
                System.out.println("=======uploadAvatar==onSuccess(String path)" + path);
                sp.edit().putString("avatarUrl", path).commit();
                runOnUiThread(() -> {
                    siv_avatar_detail.setImageUrl(path);
                });

                fileOP.deleteTempCameraFile();
            }

            @Override
            public void onError(String err) {
                System.out.println("=======uploadErr:" + err);
            }
        });
    }


}