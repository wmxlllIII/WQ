package com.memory.wq.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.memory.wq.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected T mBinding;
    public static List<Activity> activityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        }
        addActivity(this);

    }

    public void hideKeyboard() {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    public void showKeyboard(View focusView) {
        if (focusView != null) {
            focusView.requestFocus();
            focusView.post(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
    }

    protected abstract int getLayoutId();

    public static void addActivity(Activity activity) {

        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {

        activityList.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}