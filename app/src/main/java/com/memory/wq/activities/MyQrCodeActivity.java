package com.memory.wq.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityMyQrCodeBinding;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.QRCodeManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

public class MyQrCodeActivity extends BaseActivity<ActivityMyQrCodeBinding> {
    private static final String TAG = "WQ_MyQrCodeActivity";
    private SharedPreferences sp;
    private FriendManager mFriendManager;
    private String token;

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
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        //TODO 使用uuNum
        long uuNumber = sp.getLong("uuNumber", -1L);
        mBinding.tvUunum.setText(String.valueOf(uuNumber));

        QRCodeManager manager = new QRCodeManager();
        Bitmap userQRCode = manager.getUserQRCode(this, String.valueOf(uuNumber), 300, 300);
        if (userQRCode != null)
            mBinding.ivQrcode.setImageBitmap(userQRCode);

        mFriendManager = new FriendManager();
    }

    private void initView() {

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
        mFriendManager.searchUser(type, targetAccount, token, new ResultCallback<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo result) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(MyQrCodeActivity.this, PersonalActivity.class);
                    intent.putExtra(AppProperties.FRIENDINFO, result);
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String err) {

            }
        });
    }
}