package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.databinding.ActivityInfoBinding;
import com.memory.wq.dialog.AddFriendDialog;
import com.memory.wq.enumertions.UpdateInfoType;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.AuthManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

public class ProfileActivity extends BaseActivity<ActivityInfoBinding> {

    private static final String TAG = "WQ_UserInfoActivity";
    private final AuthManager mAuthManager = new AuthManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info;
    }

    private void initData() {

    }

    private void initView() {
        mBinding.clId.setTitle("WQID");
        mBinding.clNickname.setTitle("昵称");
        mBinding.clPhone.setTitle("绑定手机号");
        mBinding.clEmail.setTitle("绑定邮箱");
        mBinding.clGender.setTitle("性别");
        mBinding.clQrcode.setTitle("我的二维码");

        mBinding.clId.setValue(String.valueOf(AccountManager.getUserId()));
        mBinding.clNickname.setValue(AccountManager.getUserInfo().getUsername());
        mBinding.clEmail.setValue(AccountManager.getUserInfo().getEmail());


        mBinding.ivBack.setOnClickListener(view -> finish());

        mBinding.clQrcode.setOnClickListener(view -> {
            startActivity(new Intent(this, MyQrCodeActivity.class));
        });
        mBinding.ivAvatar.setOnClickListener(view -> {
            startActivity(new Intent(this, AvatarActivity.class));
        });

        Glide.with(this)
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .into(mBinding.ivAvatar);

        mBinding.clNickname.setOnClickListener(view -> {
            AddFriendDialog dialog = new AddFriendDialog(mBinding.getRoot().getContext());
            dialog.setTitle("更改昵称");
            dialog.setContent(AccountManager.getUserInfo().getUsername());
            dialog.setOnConfirmListener(new AddFriendDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String content) {
                    mAuthManager.updateProfileInformation(UpdateInfoType.USERNAME, content, new updateCallbackImpl());
                }

                @Override
                public void onCancel() {
                }
            });
            dialog.show();
        });

        mBinding.clEmail.setOnClickListener(view -> {
            AddFriendDialog dialog = new AddFriendDialog(mBinding.getRoot().getContext());
            dialog.setTitle("绑定邮箱");
            dialog.setContent(AccountManager.getUserInfo().getEmail());
            dialog.setOnConfirmListener(new AddFriendDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String content) {
                    mAuthManager.updateProfileInformation(UpdateInfoType.EMAIL, content, new updateCallbackImpl());
                }

                @Override
                public void onCancel() {
                }
            });
            dialog.show();
        });

        mBinding.clPhone.setOnClickListener(view -> {

        });

        mBinding.clSignatureZone.setOnClickListener(view -> {
            AddFriendDialog dialog = new AddFriendDialog(mBinding.getRoot().getContext());
            dialog.setTitle("个性签名");
            dialog.setContent("个性签名");
            dialog.setOnConfirmListener(new AddFriendDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String content) {
                    mAuthManager.updateProfileInformation(UpdateInfoType.SIGNATURE, content, new updateCallbackImpl());
                }

                @Override
                public void onCancel() {
                }
            });
            dialog.show();
        });

    }

    private class updateCallbackImpl implements ResultCallback<Boolean> {

        @Override
        public void onSuccess(Boolean result) {
            MyToast.showToast(ProfileActivity.this, "更新成功");
        }

        @Override
        public void onError(String err) {
            MyToast.showToast(ProfileActivity.this, "更新失败");
        }
    }

}