package com.memory.wq.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.signer.SignVersion;
import com.memory.wq.beans.StsTokenInfo;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.FileUtil;
import com.memory.wq.utils.ResultCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OssManager {
    public static final String TAG = "WQ_OssManager";
    private static OssManager instance;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    public static OssManager getInstance() {
        if (instance == null) {
            synchronized (OssManager.class) {
                if (instance == null) {
                    instance = new OssManager();
                }
            }
        }
        return instance;
    }

    private OSS initOssClient(Context context, StsTokenInfo stsToken) {
        ClientConfiguration config = new ClientConfiguration();
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                stsToken.getAccessKeyId(),
                stsToken.getAccessKeySecret(),
                stsToken.getSecurityToken()
        );

        config.setSignVersion(SignVersion.V4);

        OSSClient oss = new OSSClient(
                context,
                stsToken.getEndPoint(),
                credentialProvider,
                config
        );

        oss.setRegion(stsToken.getRegion());
        return oss;
    }

    public void uploadFiles(Context context, StsTokenInfo stsToken, List<File> fileList, ResultCallback<List<String>> callback) {
        ThreadPoolManager.getInstance().execute(() -> {
            List<String> fileUrls = new ArrayList<>();
            OSS ossClient = initOssClient(context, stsToken);
            for (File file : fileList) {
                if (file == null || !file.exists()) {
                    Log.d(TAG, "[x] uploadFiles FileNotExist #72");
                    mHandler.post(() -> callback.onError("file is NullOrEmpty"));
                    return;
                }
            }


            for (File file : fileList) {
                String objectKey = FileUtil.generateUniqueObjectKey(file.getName());
                try {
                    PutObjectRequest put = new PutObjectRequest(stsToken.getBucketName(), objectKey, file.getAbsolutePath());

                    put.setProgressCallback((request, currentSize, totalSize) -> {
                        //TODO
                    });

                    ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                            String url = FileUtil.tagConvertUrl(request.getObjectKey(), request.getBucketName(), stsToken.getEndPoint());
                            if (TextUtils.isEmpty(url)) {
                                Log.d(TAG, "[x] uploadFiles onSuccess #101");
                                return;
                            }

                            fileUrls.add(url);
                            if (fileUrls.size() == fileList.size()) {
                                callback.onSuccess(fileUrls);
                            }
                        }

                        @Override
                        public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                            Log.e(TAG, "[x] uploadFiles onFailure #106" + clientException.getMessage());
                            Log.e(TAG, "[x] uploadFiles onFailure #107" + serviceException.getMessage());
                            callback.onError("上传失败");
                        }
                    });


                } catch (Exception e) {
                    Log.e(TAG, "[x] uploadFiles #115 " + e.getMessage());
                }
            }
        });
    }

}
