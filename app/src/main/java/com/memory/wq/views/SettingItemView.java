package com.memory.wq.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.memory.wq.R;
import com.memory.wq.databinding.ViewItemSettingBinding;

public class SettingItemView extends LinearLayout {

    private TextView tvTitle;
    private Switch switchButton;

    public SettingItemView(Context context) {
        super(context, null);
    }

    public SettingItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SettingItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(android.view.Gravity.CENTER_VERTICAL);
//        ViewItemSettingBinding.inflate()
        LayoutInflater.from(context).inflate(R.layout.view_item_setting, this, true);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        switchButton = (Switch) findViewById(R.id.switch_button);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsItemView);
            String title = a.getString(R.styleable.SettingsItemView_text);
            boolean showSwitch = a.getBoolean(R.styleable.SettingsItemView_enableSwitch, false);
            a.recycle();

            if (title != null) {
                tvTitle.setText(title);
            }
            switchButton.setVisibility(showSwitch ? VISIBLE : GONE);
        }
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setShowSwitch(boolean show) {
        switchButton.setVisibility(show ? VISIBLE : GONE);
    }

    public void setSwitchChecked(boolean checked) {
        switchButton.setChecked(checked);
    }

    public boolean isSwitchChecked() {
        return switchButton.isChecked();
    }

    public void setOnCheckedChangeListener(SwitchCompat.OnCheckedChangeListener listener) {
        switchButton.setOnCheckedChangeListener(listener);
    }
}
