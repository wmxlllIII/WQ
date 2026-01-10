package com.memory.wq.activities;

import static com.memory.wq.managers.UserManager.REQUEST_ALBUM_CODE;
import static com.memory.wq.managers.UserManager.REQUEST_CAMERA_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.databinding.AvatarDetailLayoutBinding;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class AvatarActivity extends BaseActivity<AvatarDetailLayoutBinding> {

    private static final String TAG = "WQ_AvatarActivity";

    private final UserManager mUserManager = new UserManager();
    private File tempCameraFile;
    private final PermissionManager mPermissionManager = new PermissionManager(this);
    public static final int REQUEST_CROP_CODE = 30;
    private final FileOP fileOP = new FileOP(this);


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

    }

    private void initView() {
        // new BottomSheetDialog()
        Glide.with(this)
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .transform(
                        new MultiTransformation<>(
                                new CenterCrop(),
                                new RoundedCorners(12)
                        )
                )
                .into(mBinding.ivAvatarDetail);

        mBinding.ivAvatarDetail.setOnClickListener(view -> {

        });

        mBinding.ivBack.setOnClickListener(view -> {
            finish();
        });

        mBinding.tvAlterAvatar.setOnClickListener(view -> {
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
                return;
            }

            showImageSourceDialog();
        });

        mBinding.tvSaveAvatar.setOnClickListener(view -> {

        });
    }

    public void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择图片")
                .setItems(new String[]{"从相册选择", "拍一张"}, (dialog, which) -> {
                    boolean isPermit;
                    switch (which) {
                        case 0:
                            //todo 33sdk
                            String[] albumPermissions;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                albumPermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
                            } else {
                                albumPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                            }

                            isPermit = mPermissionManager.isPermitPermission(albumPermissions[0]);
                            if (isPermit) {
                                mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_ALBUM);
                            } else {
                                mPermissionManager.requestPermission(albumPermissions, REQUEST_ALBUM_CODE);
                            }
                            break;
                        case 1:
                            isPermit = mPermissionManager.isPermitPermission(Manifest.permission.CAMERA);
                            if (isPermit) {
                                mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_CAMERA);
                            } else {
                                mPermissionManager.requestPermission(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                            }
                            break;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted;
        switch (requestCode) {
            case REQUEST_ALBUM_CODE:
                isGranted = mPermissionManager.isPermissionGranted(grantResults);
                if (isGranted) {
                    mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_ALBUM);
                } else {
                    MyToast.showToast(this, "相册权限被拒绝");

                }
                break;

            case REQUEST_CAMERA_CODE:
                isGranted = mPermissionManager.isPermissionGranted(grantResults);
                if (isGranted) {
                    mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_CAMERA);
                } else {
                    MyToast.showToast(this, "noPermit Camera");
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            // 如果是相机拍摄失败，清理临时文件
            if (requestCode == UserManager.REQUEST_CAMERA_CODE && tempCameraFile != null) {
                fileOP.deleteTempCameraFile();
                tempCameraFile = null;
            }
            return;
        }

        Uri resultUri = null;

        if (requestCode == UserManager.REQUEST_ALBUM_CODE && data != null) {
            Uri albumUri = data.getData();
            if (albumUri != null) {
                startCrop(albumUri);
            }
        } else if (requestCode == UserManager.REQUEST_CAMERA_CODE) {
            // 相机拍摄成功，tempCameraFile 应已存在
            if (tempCameraFile != null && tempCameraFile.exists()) {
                Uri cameraUri = fileOP.file2Uri(this, tempCameraFile);
                startCrop(cameraUri);
            }
        } else if (requestCode == UserManager.REQUEST_CROP_CODE) {
            if (data != null) {
                resultUri = UCrop.getOutput(data);
                // 清理相机临时文件（裁剪后不再需要原始相机文件）
                if (tempCameraFile != null) {
                    fileOP.deleteTempCameraFile();
                    tempCameraFile = null;
                }
            }
        }

        if (resultUri != null) {
            File croppedFile = fileOP.handleImage(resultUri);
            if (croppedFile != null && croppedFile.exists()) {
                uploadAvatar(croppedFile);
            } else {
                MyToast.showToast(this, "裁剪后的图片无效");
            }
        }
    }

    private void uploadAvatar(File file) {
        mUserManager.upLoadAvatar(file, new ResultCallback<String>() {
            @Override
            public void onSuccess(String path) {
                Log.d(TAG, "[test] uploadAvatar #190");
                Glide.with(AvatarActivity.this)
                        .load(path)
                        .error(R.mipmap.icon_default_avatar)
                        .into(mBinding.ivAvatarDetail);

                fileOP.deleteTempCameraFile();
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "[x] uploadAvatar #202 " + err);
            }
        });
    }

    private void startCrop(@NonNull Uri sourceUri) {
        File cropFile = fileOP.createTempImageFile();
        Uri destinationUri = fileOP.file2Uri(this, cropFile);

        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        uCrop.withAspectRatio(1, 1);
        uCrop.start(this, UserManager.REQUEST_CROP_CODE);
    }

}