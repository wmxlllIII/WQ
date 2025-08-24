package com.memory.wq.managers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    private Activity mActivity;

    public PermissionManager(Activity activity) {
        this.mActivity = activity;
        /**
         * 为何 this.context = context 能正常工作？
         * 正确逻辑：当你在初始化 PermissionManager 时传入 this（例如 new PermissionManager(AvatarActivity.this)），此时的 context 是 Activity Context，具备以下特性：
         *
         * 可以直接调用 Activity 的方法（如 requestPermissions()）。
         *
         * 能正确绑定到当前 Activity 的生命周期，确保权限弹窗正常显示。
         */
    }

    public boolean isPermitPermission(String permission) {
        return ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(mActivity, permissions, requestCode);//Application Context 无法强制转换为 Activity
    }

    public boolean isPermissionGranted(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
