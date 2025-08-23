package com.memory.wq.activities;

import static com.memory.wq.managers.UserManager.REQUEST_ALBUM_CODE;
import static com.memory.wq.managers.UserManager.REQUEST_CAMERA_CODE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.memory.wq.R;
import com.memory.wq.databinding.AvatarDetailLayoutBinding;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.managers.UserManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.io.File;

public class AvatarActivity extends BaseActivity<AvatarDetailLayoutBinding> {
    private static final String TAG = AvatarActivity.class.getName();
    private UserManager mUserManager;
    private String token;
    private String email;
    private ActivityResultLauncher<String> register;
    private FileOP fileOP;
    private String avatarUrl;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.avatar_detail_layout;
    }

    private void initData() {
        mUserManager = new UserManager(this);
        fileOP = new FileOP(this);

        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        email = sp.getString("email", "");
        avatarUrl = sp.getString("avatarUrl", "");
        mBinding.sivAvatarDetail.setImageUrl(avatarUrl);
    }

    private void initView() {
        // new BottomSheetDialog()

        mBinding.sivAvatarDetail.setOnClickListener(view -> {

        });

        mBinding.ivBack.setOnClickListener(view -> {
            finish();
        });

        mBinding.tvAlterAvatar.setOnClickListener(view -> {
            showImageSourceDialog();
        });

        mBinding.tvSaveAvatar.setOnClickListener(view -> {

        });
    }

    public void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择图片")
                .setItems(new String[]{"从相册选择", "拍一张"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_ALBUM);
                            break;
                        case 1:
                            mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_CAMERA);
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
                isPermit = mUserManager.handleRequestPermission(AvatarActivity.this, grantResults, SelectImageType.IMAGE_FROM_ALBUM);
                if (!isPermit)
                    MyToast.showToast(this, "noPermit Album");
                break;
            case REQUEST_CAMERA_CODE:
                isPermit = mUserManager.handleRequestPermission(AvatarActivity.this, grantResults, SelectImageType.IMAGE_FROM_CAMERA);
                if (!isPermit)
                    MyToast.showToast(this, "noPermit Camera");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = mUserManager.handleAvatarResult(requestCode, resultCode, data, this);
        if (uri != null) {
            File file = fileOP.handleImage(uri);
            uploadAvatar(file);
        }
    }

    private void uploadAvatar(File file) {
        mUserManager.upLoadAvatar(file, AppProperties.UPLOAD_URL, token, new ResultCallback<String>() {
            @Override
            public void onSuccess(String path) {
                System.out.println("=======uploadAvatar==onSuccess(String path)" + path);
                sp.edit().putString("avatarUrl", path).commit();
                runOnUiThread(() -> {
                    mBinding.sivAvatarDetail.setImageUrl(path);
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