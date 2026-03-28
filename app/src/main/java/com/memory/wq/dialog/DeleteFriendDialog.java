package com.memory.wq.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.memory.wq.R;
import com.memory.wq.databinding.DialogDeleteFriendBinding;
import com.memory.wq.databinding.DialogRequesFriendBinding;

public class DeleteFriendDialog extends Dialog {

    private DialogDeleteFriendBinding mBinding;
    private OnConfirmListener onConfirmListener;

    public DeleteFriendDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialogStyle);
        init();
    }

    private void init() {
        mBinding = DialogDeleteFriendBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mBinding.tvCancel.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onCancel();
            }
            dismiss();
        });

        mBinding.tvConfirm.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onConfirm();
            }
            dismiss();
        });
        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
        }

    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.onConfirmListener = listener;
    }

    public void setTitle(String title){
        mBinding.tvTitle.setText(title);
    }

    public interface OnConfirmListener {
        void onConfirm();
        void onCancel();
    }

}

