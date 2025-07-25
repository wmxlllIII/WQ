package com.memory.wq.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.QRCodeManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.MyToast;

public class SearchFriendActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_search;
    private ImageView iv_qrcode;
    private SharedPreferences sp;
    private TextView tv_scan;
    private FriendManager friendManager;
    private String token;
    private TextView tv_uunum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        initView();
        initData();
    }

    private void initData() {
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        //TODO 使用uuNum
        long uuNumber = sp.getLong("uuNumber", -1L);
        tv_uunum.setText(String.valueOf(uuNumber));

        QRCodeManager manager = new QRCodeManager();
        Bitmap userQRCode = manager.getUserQRCode(this, String.valueOf(uuNumber), 300, 300);
        if (userQRCode != null)
            iv_qrcode.setImageBitmap(userQRCode);

        friendManager = new FriendManager();
    }

    private void initView() {
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        tv_scan = (TextView) findViewById(R.id.tv_scan);
        tv_uunum = (TextView) findViewById(R.id.tv_uunum);
        ll_search.setOnClickListener(this);
        tv_scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                startActivity(new Intent(this, SearchUserActivity.class));
                break;
            case R.id.tv_scan:
                scanQRCode();
                break;
        }
    }

    private void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("扫描用户二维码");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(PortraitCaptureActivity.class);
        integrator.initiateScan();
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
            enterPersonalHome(SearchUserType.SEARCH_USER_TYPE_UUNUM,uuNum);
            MyToast.showToast(this, "扫描结果:" + uuNum);
        }


    }

    private void enterPersonalHome(SearchUserType type,String targetAccount) {
        friendManager.searchUser(type,targetAccount, token, new ResultCallback<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo result) {
                runOnUiThread(()->{
                    Intent intent = new Intent(SearchFriendActivity.this,PersonalActivity.class);
                    intent.putExtra(AppProperties.FRIENDINFO,result);
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String err) {

            }
        });
    }
}