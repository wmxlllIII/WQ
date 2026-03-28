package com.memory.wq.activities;

import static com.memory.wq.managers.UserManager.REQUEST_ALBUM_CODE;
import static com.memory.wq.managers.UserManager.REQUEST_CAMERA_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.databinding.AvatarDetailLayoutBinding;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.provider.FileOP;
import com.memory.wq.utils.FileUtil;
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
    private final FileOP mFileOP = new FileOP(this);


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
            mBinding.ivAvatarDetail.setDrawingCacheEnabled(true);
            mBinding.ivAvatarDetail.buildDrawingCache();

            Bitmap bitmap = mBinding.ivAvatarDetail.getDrawable() != null
                    ? ((BitmapDrawable) mBinding.ivAvatarDetail.getDrawable()).getBitmap()
                    : null;

            if (bitmap == null) {
                MyToast.showToast(this, "头像获取失败");
                return;
            }

            String fileName = FileUtil.generateUniqueObjectKey("avatar.png");

            boolean success = FileUtil.saveBitmapToGallery(this, bitmap, fileName);

            if (success) {
                MyToast.showToast(this, "已保存到相册");
            } else {
                MyToast.showToast(this, "保存失败");
            }
        });
    }

    private void updateUI(){
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
                                openAlbum();
                            } else {
                                mPermissionManager.requestPermission(albumPermissions, REQUEST_ALBUM_CODE);
                            }
                            break;
                        case 1:
                            isPermit = mPermissionManager.isPermitPermission(Manifest.permission.CAMERA);
                            if (isPermit) {
                                openCamera();
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
                    openAlbum();
                } else {
                    MyToast.showToast(this, "相册权限被拒绝");

                }
                break;

            case REQUEST_CAMERA_CODE:
                isGranted = mPermissionManager.isPermissionGranted(grantResults);
                if (isGranted) {
                    openCamera();
                } else {
                    MyToast.showToast(this, "noPermit Camera");
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "用户取消或操作失败");
            if (requestCode == UserManager.REQUEST_CAMERA_CODE && tempCameraFile != null) {
                mFileOP.deleteTempCameraFile();
                tempCameraFile = null;
                Log.d(TAG, "已清理相机临时文件");
            }
            return;
        }

        if (requestCode == UserManager.REQUEST_ALBUM_CODE) {
            handleAlbumResult(data);
        } else if (requestCode == UserManager.REQUEST_CAMERA_CODE) {
            handleCameraResult();
        } else if (requestCode == UserManager.REQUEST_CROP_CODE) {
            handleCropResult(data);
        }
    }

    private void handleAlbumResult(@Nullable Intent data) {
        if (data == null) {
            Log.d(TAG, "[x] handleAlbumResult #266");
            return;
        }

        Uri albumUri = data.getData();
        if (albumUri == null) {
            Log.d(TAG, "[x] handleAlbumResult #272");
            MyToast.showToast(this, "未选择图片");
            return;
        }

        startCrop(albumUri);
    }

    private void handleCameraResult() {
        if (tempCameraFile == null || !tempCameraFile.exists()) {
            MyToast.showToast(this, "拍摄失败，请重试");
            return;
        }

        if (tempCameraFile.length() <= 0) {
            MyToast.showToast(this, "拍摄的图片无效");
            return;
        }

        Uri cameraUri = mFileOP.file2Uri(this, tempCameraFile);
        startCrop(cameraUri);
    }

    private void handleCropResult(@Nullable Intent data) {
        if (data == null) {
            MyToast.showToast(this, "裁剪失败");
            return;
        }

        Throwable error = UCrop.getError(data);
        if (error != null) {
            MyToast.showToast(this, "裁剪错误");
            return;
        }

        Uri resultUri = UCrop.getOutput(data);
        if (resultUri == null) {
            MyToast.showToast(this, "裁剪失败");
            return;
        }

        processCroppedImage(resultUri);
    }

    private void processCroppedImage(@NonNull Uri imageUri) {
        File croppedFile = mFileOP.handleImage(imageUri);

        if (croppedFile == null || !croppedFile.exists() || croppedFile.length() <= 0) {
            MyToast.showToast(this, "图片处理失败");
            return;
        }

        uploadAvatar(croppedFile);
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_ALBUM_CODE);
    }

    private void openCamera() {
        tempCameraFile  = mFileOP.createTempImageFile();
        Uri uriForFile = mFileOP.file2Uri(this, tempCameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }

    private void uploadAvatar(File file) {
        mUserManager.upLoadAvatar(file, new ResultCallback<String>() {
            @Override
            public void onSuccess(String path) {
                Log.d(TAG, "[test] uploadAvatar #190");
                updateUI();
                mFileOP.deleteTempCameraFile();
                MyToast.showToast(AvatarActivity.this, "头像已更新");
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "[x] uploadAvatar #202 " + err);
                MyToast.showToast(AvatarActivity.this, "头像更新失败");
            }
        });
    }

    private void startCrop(@NonNull Uri sourceUri) {
        File cropFile = mFileOP.createTempImageFile();
        Uri destinationUri = mFileOP.file2Uri(this, cropFile);

        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        uCrop.withAspectRatio(1, 1);
        uCrop.start(this, UserManager.REQUEST_CROP_CODE);
    }

}