package com.memory.wq.activities;

import static com.memory.wq.managers.UserManager.REQUEST_ALBUM_CODE;
import static com.memory.wq.managers.UserManager.REQUEST_CAMERA_CODE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.databinding.AvatarDetailLayoutBinding;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.io.File;

public class AvatarActivity extends BaseActivity<AvatarDetailLayoutBinding> {

    private static final String TAG = AvatarActivity.class.getName();

    private final UserManager mUserManager = new UserManager(this);
    private final PermissionManager mPermissionManager = new PermissionManager(this);
    private String token;
    private String email;
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
        fileOP = new FileOP(this);

        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        email = sp.getString("email", "");
        avatarUrl = sp.getString("avatarUrl", "");
        if (TextUtils.isEmpty(avatarUrl)) {
            mBinding.ivAvatarDetail.setImageResource(R.mipmap.icon_default_avatar);
            return;
        }

        Glide.with(this)
                .load(avatarUrl)
                .error(R.mipmap.icon_default_avatar)
                .into(mBinding.ivAvatarDetail);
    }

    private void initView() {
        // new BottomSheetDialog()

        mBinding.ivAvatarDetail.setOnClickListener(view -> {

        });

        mBinding.ivBack.setOnClickListener(view -> {
            finish();
        });

        mBinding.tvAlterAvatar.setOnClickListener(view -> {
            if (AccountManager.isVisitorUser(this)) {
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
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                                albumPermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
//                            } else {
//                                albumPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
//                            }
//
//                            isPermit = mPermissionManager.isPermitPermission(albumPermissions[0]);
//                            if (isPermit) {
//                                mUserManager.open(AvatarActivity.this, SelectImageType.IMAGE_FROM_ALBUM);
//                            } else {
//                                mPermissionManager.requestPermission(albumPermissions, REQUEST_ALBUM_CODE);
//                            }
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
                sp.edit().putString("avatarUrl", path).apply();
                Glide.with(AvatarActivity.this)
                        .load(path)
                        .error(R.mipmap.icon_default_avatar)
                        .into(mBinding.ivAvatarDetail);

                fileOP.deleteTempCameraFile();
            }

            @Override
            public void onError(String err) {
                System.out.println("=======uploadErr:" + err);
            }
        });
    }


}