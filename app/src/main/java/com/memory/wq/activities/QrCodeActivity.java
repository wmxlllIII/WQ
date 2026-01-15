package com.memory.wq.activities;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityMyQrCodeBinding;
import com.memory.wq.databinding.BaseQrCodeBinding;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.QRCodeManager;
import com.memory.wq.utils.FileUtil;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

public class QrCodeActivity extends BaseActivity<ActivityMyQrCodeBinding> {
    private static final String TAG = "WQ_MyQrCodeActivity";
    private FriendManager mFriendManager;
    private final QRCodeManager mQrCodeManager = new QRCodeManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_qr_code;
    }

    private void initData() {
        long uuNumber = AccountManager.getUserInfo().getUuNumber();
        mBinding.tvUunum.setText(String.valueOf(uuNumber));

        Bitmap userQRCode = mQrCodeManager.getUserQRCode(this, String.valueOf(uuNumber), 300, 300);
        if (userQRCode != null)
            mBinding.ivQrcode.setImageBitmap(userQRCode);

        mFriendManager = new FriendManager();
    }

    private void initView() {
        mBinding.ivBack.setOnClickListener(v -> finish());

        mBinding.tvSave.setOnClickListener(v -> {
            Bitmap bitmap = createSaveBitmap();
            boolean success = FileUtil.saveBitmapToGallery(
                    this,
                    bitmap,
                    "WQ_QR_" + System.currentTimeMillis() + ".png"
            );

            if (success) {
                MyToast.showToast(this, "二维码已保存到相册");
            } else {
                MyToast.showToast(this, "保存失败");
            }
        });

        Glide.with(this)
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .circleCrop()
                .into(mBinding.ivAvatar);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (result.getContents() == null) {
            MyToast.showToast(this, "扫描取消");
        } else {
            String uuNum = result.getContents();
            //TODO 进主页
            enterPersonalHome(SearchUserType.SEARCH_USER_TYPE_UUNUM, uuNum);
            MyToast.showToast(this, "扫描结果:" + uuNum);
        }


    }

    private void enterPersonalHome(SearchUserType type, String targetAccount) {
        mFriendManager.searchUser(type, targetAccount, new ResultCallback<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo result) {
                Intent intent = new Intent(QrCodeActivity.this, PersonInfoActivity.class);
                intent.putExtra(AppProperties.PERSON_ID, result.getUuNumber());
                startActivity(intent);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private Bitmap createBitmapFromView(View view) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Drawable bg = view.getBackground();
        if (bg != null) {
            bg.draw(canvas);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }

        view.draw(canvas);
        return bitmap;
    }

    private Bitmap createSaveBitmap() {
        BaseQrCodeBinding saveBinding = BaseQrCodeBinding.inflate(LayoutInflater.from(this), null, false);

        Glide.with(this)
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .circleCrop()
                .into(saveBinding.ivAvatar);

        Bitmap qrBitmap = mQrCodeManager.getUserQRCode(
                this,
                String.valueOf(AccountManager.getUserInfo().getUuNumber()),
                300,
                300
        );
        saveBinding.ivQrcode.setImageBitmap(qrBitmap);
        saveBinding.tvUunum.setText(String.valueOf(AccountManager.getUserInfo().getUuNumber()));

        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                View.MeasureSpec.EXACTLY
        );
        int heightSpec = View.MeasureSpec.makeMeasureSpec(
                Resources.getSystem().getDisplayMetrics().heightPixels,
                View.MeasureSpec.EXACTLY
        );

        saveBinding.getRoot().measure(widthSpec, heightSpec);
        saveBinding.getRoot().layout(
                0,
                0,
                saveBinding.getRoot().getMeasuredWidth(),
                saveBinding.getRoot().getMeasuredHeight()
        );

        return createBitmapFromView(saveBinding.getRoot());
    }


}