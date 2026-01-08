package com.memory.wq.managers;


import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.ResultCallback;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserManager {

    public static final String TAG = "WQ_UserManager";
    private final FileOP mFileOP = new FileOP(WqApplication.getInstance());
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static final int REQUEST_CAMERA_CODE = 10;
    public static final int REQUEST_ALBUM_CODE = 20;
    public static final int REQUEST_CROP_CODE = 30;
    private File tempImageFile;

    public void upLoadAvatar(File file, ResultCallback<String> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postFile(AppProperties.UPLOAD_URL, AccountManager.getUserInfo().getToken(), file, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mHandler.post(() -> {
                        callback.onError(e.getMessage());
                    });
                    Log.d(TAG, "onFailure: ======错误1");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    mHandler.post(() -> {
                        if (!response.isSuccessful()) {
                            callback.onError(response.toString());
                            Log.d(TAG, "[x] upLoadAvatar #65");
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            Log.d(TAG, "[test] upLoadAvatar #70"+json);
                            int code = json.getInt("code");
                            if (code == 1) {
                                String avatar = JsonParser.avatarParse(json);
                                if (!TextUtils.isEmpty(avatar)) {
                                    callback.onSuccess(avatar);
                                } else
                                    callback.onError("=====头像地址为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        callback.onError("我也不知道啥错误");
                    });
                }
            });
        });
    }


    public void open(Context context, SelectImageType type) {
        switch (type) {
            case IMAGE_FROM_ALBUM:
                openAlbum(context);
                break;
            case IMAGE_FROM_CAMERA:
                openCamera(context);
                break;

        }
    }

    private void openAlbum(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(intent, REQUEST_ALBUM_CODE);
    }

    private void openCamera(Context context) {
        tempImageFile = mFileOP.createTempImageFile();
        Uri uriForFile = mFileOP.file2Uri(context, tempImageFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        ((Activity) context).startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }

    public void deleteFriend(long friendId, ResultCallback<Boolean> callback) {
        String json = GenerateJson.getDeleteFriendJson(friendId);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.FRIEND_DELETE, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] deleteFriend #167" + e.getMessage());
                    mHandler.post(() -> callback.onError("删除失败"));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] deleteFriend #174 " + response.message());
                        mHandler.post(() -> callback.onError("删除失败"));
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Log.d(TAG, "[test] deleteFriend #180 " + json);
                        if (json.getInt("code") == 1) {
                            boolean isDelete = json.getBoolean("data");
                            if (isDelete) {
                                mHandler.post(() -> callback.onSuccess(true));
                            } else {
                                mHandler.post(() -> callback.onError("删除失败"));
                            }
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "[x] deleteFriend #1 " + e.getMessage());
                    }
                }
            });
        });
    }
}
