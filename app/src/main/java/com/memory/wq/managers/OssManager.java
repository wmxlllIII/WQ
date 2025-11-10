package com.memory.wq.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
    private static final String BUCKET_NAME = AppProperties.OSS_BUCKET_NAME;
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

    private OSS initOssClient(Context context,StsTokenInfo stsToken) {
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        String region = "cn-beijing";


        ClientConfiguration config = new ClientConfiguration();

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(stsToken.getAccessKeyId(), stsToken.getAccessKeySecret(), stsToken.getSecurityToken());
        config.setSignVersion(SignVersion.V4);

//        OSSClient oss = new OSSClient(context, stsToken.getEndPoint(), credentialProvider, config);
        OSSClient oss = new OSSClient(context, endpoint, credentialProvider, config);
        oss.setRegion(region);
        return oss;
    }

    public void uploadFiles(Context context, StsTokenInfo stsToken,List<File> fileList, ResultCallback<List<String>> callback) {
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
                //objectKey 文件名
                String objectKey = FileUtil.generateUniqueObjectKey(file.getName());
                try {
                    PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, objectKey, file.getAbsolutePath());

                    put.setProgressCallback((request, currentSize, totalSize) -> {
                        //TODO
                    });

                    ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                            Log.d(TAG, "===" + result.toString());
//                            String ossUrl = result.getServerCallbackReturnBody();
//                            fileUrls.add(ossUrl);
                        }

                        @Override
                        public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                            Log.d(TAG, "===clientException： " + clientException);
                            Log.d(TAG, "===： " + serviceException);
                        }
                    });


                } catch (Exception e) {
                    Log.d(TAG, "[x] uploadFiles #115 " + e);
                }
            }
        });
    }

}
