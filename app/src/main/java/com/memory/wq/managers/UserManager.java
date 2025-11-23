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

import com.memory.wq.beans.UserInfo;
import com.memory.wq.enumertions.SelectImageType;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.provider.FileOP;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserManager {
    public static final String TAG = UserManager.class.getName();
    private Context context;
    private FileOP fileOP;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static final int REQUEST_CAMERA_CODE = 10;
    public static final int REQUEST_ALBUM_CODE = 20;
    public static final int REQUEST_CROP_CODE = 30;
    private File tempImageFile;


    public UserManager(Context context) {
        this.context = context;
        this.fileOP = new FileOP(context);

    }


    public void upLoadAvatar(File file, String url, String token, ResultCallback<String> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postFile(url, token, file, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    handler.post(() -> {
                        callback.onError(e.getMessage());
                    });
                    Log.d(TAG, "onFailure: ======错误1");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    handler.post(() -> {
                        if (!response.isSuccessful()) {
                            callback.onError(response.toString());
                            Log.d(TAG, "onResponse: ======错误2");
                            return;
                        }
                        try {
                            JSONObject json = new JSONObject(response.body().string());
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
        tempImageFile = fileOP.createTempImageFile();
        Uri uriForFile = fileOP.file2Uri(context, tempImageFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        ((Activity) context).startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }


    public Uri handleAvatarResult(int requestCode, int resultCode, Intent data, Context context) {
        //相机成功拍照后data可能为null根据resultCode判断。
        if (resultCode != RESULT_OK) {
            fileOP.deleteTempCameraFile();
            return null;
        }
        Uri uri;
        switch (requestCode) {
            case REQUEST_ALBUM_CODE:
                if (data != null) {
                    uri = data.getData();
                    startCrop(uri);
                }
            case REQUEST_CAMERA_CODE:
                uri = fileOP.file2Uri(context, tempImageFile);
                startCrop(uri);
                break;
            case REQUEST_CROP_CODE:
                if (data != null) {
                    Uri mUri = UCrop.getOutput(data);
                    return mUri;
                }
        }
        return null;
    }

    private void startCrop(Uri uri) {
        File tempImageFile = fileOP.createTempImageFile();
        Uri desUri = fileOP.file2Uri(context, tempImageFile);

        UCrop uCrop = UCrop.of(uri, desUri);
        uCrop.withAspectRatio(1, 1);
        uCrop.start(((Activity) context), REQUEST_CROP_CODE);
    }

    public void updateUserInfo(String token, UserInfo userInfo) {
        //TODO 单独更新
        String json = GenerateJson.getUpdateUserInfoJson(userInfo);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.UPDATE_USER, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            //TODO
                            JSONObject data = json.getJSONObject("data");
                            String userName = data.getString("userName");
                            UserInfo userInfo1 = new UserInfo();
                            userInfo1.setUserName(userName);
                            SPManager.saveUserInfo(context, userInfo1);
                            ((Activity) context).runOnUiThread(() -> {
                                MyToast.showToast(context, "更改成功");
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

}
