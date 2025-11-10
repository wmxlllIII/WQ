package com.memory.wq.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.signer.SignVersion;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.memory.wq.R;
import com.memory.wq.adapters.SelectImageAdapter;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.StsTokenInfo;
import com.memory.wq.databinding.ActivityEditRecommendBinding;
import com.memory.wq.managers.OssManager;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.PostManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GlideEngine;
import com.memory.wq.utils.JsonParser;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UploadPostActivity extends BaseActivity<ActivityEditRecommendBinding> {

    public static final String TAG = "EditPostActivity";

    private String token;
    private final List<File> postImagesList = new ArrayList<>();
    private PostManager mPostManager;
    private SelectImageAdapter mAdapter;
    private PermissionManager permissionManager;
    public static final int PERMISSION_REQUEST_CODE = 0;

    public UploadPostActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_recommend;
    }

    private void initData() {
        permissionManager = new PermissionManager(this);
        mPostManager = new PostManager();
        token = SPManager.getUserInfo(this).getToken();
        mAdapter = new SelectImageAdapter(postImagesList);
        mAdapter.setOnAddClickListener(new SelectImageAdapter.OnAddOrRemoveClickListener() {
            @Override
            public void onAddClick() {
                if (hasPermission()) {
                    selectPostImages();
                } else {
                    permissionManager.requestPermission(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
                }
            }

            @Override
            public void onRemoveClick(int position) {
                postImagesList.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, postImagesList.size() - position);
            }
        });

        mBinding.rvSelectImages.setLayoutManager(new GridLayoutManager(this, 3));
        mBinding.rvSelectImages.setAdapter(mAdapter);
    }

    private boolean hasPermission() {
        return permissionManager.isPermitPermission(Manifest.permission.READ_MEDIA_IMAGES);
    }

    private void initView() {

        mBinding.tvPublish.setOnClickListener(v -> {
            mBinding.tvPublish.setEnabled(false);
            publishPost();
        });


    }


    private void selectPostImages() {
        int remainCount = 9 - postImagesList.size();
        if (remainCount <= 0) {
            MyToast.showToast(this, "最多选择9张图片");
            Log.d(TAG, "===[x] selectPostImages #96");
            return;
        }

        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setMaxSelectNum(9 - postImagesList.size())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void publishPost() {
        String content = mBinding.etContent.getText().toString().trim();
        String title = mBinding.etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Log.d(TAG, "===[x] publishPost #127");
            mBinding.tvPublish.setEnabled(true);
            return;
        }

        Log.d(TAG, "======" + token);
        ThreadPoolManager.getInstance().execute(() -> {
            HttpStreamOP.postJson(AppProperties.STS_TOKEN, token, "{}", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        int code = json.getInt("code");
                        if (code == 1) {
                            StsTokenInfo stsTokenInfo = JsonParser.stsTokenParser(json);
                            OssManager.getInstance().uploadFiles(UploadPostActivity.this, stsTokenInfo, postImagesList, new ResultCallback<List<String>>() {
                                @Override
                                public void onSuccess(List<String> result) {
                                }

                                @Override
                                public void onError(String err) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "============获取sts异常" + e.getMessage());
                    }
                }
            });
        });


        PostInfo postInfo = new PostInfo();
        postInfo.setContent(content);
        postInfo.setTitle(title);
        Log.d(TAG, "发布");
        mPostManager.publishPost(token, postInfo, postImagesList, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                runOnUiThread(() -> {
                    MyToast.showToast(UploadPostActivity.this, "发布成功");
                    finish();
                    mBinding.tvPublish.setEnabled(true);
                });
            }

            @Override
            public void onError(String err) {
                runOnUiThread(() -> {
                    Log.d(TAG, "[x] publishPost #145" + err);
                    mBinding.tvPublish.setEnabled(true);
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainSelectorList(data);
                    for (LocalMedia localMedia : selectList) {
                        String path = localMedia.getRealPath();
                        if (!TextUtils.isEmpty(path)) {
                            postImagesList.add(new File(path));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (permissionManager.isPermissionGranted(grantResults)) {
                    selectPostImages();
                } else {
                    MyToast.showToast(this, "读取图片权限被拒绝");
                }
        }
    }

    /**
     * 调用同步接口遇到异常时，将直接抛出ClientException或者ServiceException异常。
     * ClientException异常是指本地遇到的异常，如网络异常参数非法等。
     * ServiceException异常是指OSS返回的服务异常，如鉴权失败、服务器错误等。
     */
//    private void initOssClient() {
//        String endpoint = "yourEndpoint";
//        String accessKeyId = "yourAccessKeyId";
//        String accessKeySecret = "yourAccessKeySecret";
//        String securityToken = "yourSecurityToken";
//        String region = "yourRegion";
//
//        ClientConfiguration config = new ClientConfiguration();
//        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
//        config.setSignVersion(SignVersion.V4);
//
//        OSSClient oss = new OSSClient(this, endpoint, credentialProvider);
//        oss.setRegion(region);
//        // 设置最大并发数，默认值5。
//        // configuration.setMaxConcurrentRequest(3);
//        // 设置Socket层传输数据的超时时间，默认值60s。
//        // configuration.setSocketTimeout(50000);
//        // 设置建立连接的超时时间，默认值60s。
//        // configuration.setConnectionTimeout(50000);
//        // 设置日志文件大小，默认值5 MB。
//        // configuration.setMaxLogSize(3 * 1024 * 1024);
//        // 请求失败后最大的重试次数，默认值2。
//        // configuration.setMaxErrorRetry(3);
//        // 列表中的元素将跳过CNAME解析。
//        // List<String> cnameExcludeList = new ArrayList<>();
//        // cnameExcludeList.add("cname");
//        // configuration.setCustomCnameExcludeList(cnameExcludeList);
//        // 代理服务器主机地址。
//        // configuration.setProxyHost("yourProxyHost");
//        // 代理服务器端口。
//        // configuration.setProxyPort(8080);
//        // 用户代理中HTTP的User-Agent头。
//        // configuration.setUserAgentMark("yourUserAgent");
//        // 是否开启CRC校验，默认值false。
//        // configuration.setCheckCRC64(true);
//        // 是否开启HTTP重定向，默认值false。
//        // configuration.setFollowRedirectsEnable(true);
//        // 设置自定义OkHttpClient。
//        // OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        // configuration.setOkHttpClient(builder.build());
//
//        /**
//         * 异步调用
//         *
//         * OSSAsyncTask task = oss.asyncGetObejct(...);
//         *         task.cancel(); // 取消任务。
//         *         task.waitUntilFinished(); // 等待直到任务完成。
//         *         GetObjectResult result = task.getResult(); // 阻塞等待结果返回。
//         *
//         */
//        //上传文件
//        PutObjectRequest put = new PutObjectRequest("bucketName", "key");
//
//        put.setProgressCallback((request, currentSize, totalSize) -> {
//
//        });
//
//        OSSAsyncTask<PutObjectResult> putTask = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                //什么线程?
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
//
//            }
//        });
//        putTask.cancel(); // 可以取消任务。
//        putTask.waitUntilFinished(); // 等待上传完成。
//
//        //下载文件
//        GetObjectRequest get = new GetObjectRequest("bucketName", "key");
//
//        get.setProgressListener((request, currentSize, totalSize) -> {
//
//        });
//
//        OSSAsyncTask<GetObjectResult> getTask = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
//            @Override
//            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
//                InputStream inputStream = result.getObjectContent();
//                byte[] buffer = new byte[2048];
//                int len;
//
//                try {
//                    while ((len = inputStream.read(buffer)) != -1) {
//                        // 您可以在此处编写代码来处理下载的数据。
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
//                // 请求异常。
//                if (clientException != null) {
//                    // 本地异常，如网络异常等。
//                    clientException.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常。
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//            }
//        });
//
//        getTask.cancel();//         取消任务。
//        getTask.waitUntilFinished();//        等待任务完成。
//
//    }

//    private void initStsToken() {
//        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
//            @Override
//            public OSSFederationToken getFederationToken() {
//
//                /* 获取ak/sk/token/expiration，
//                 * 示例从应用服务器获取ak/sk/token/expiration：
//                 * URL stsUrl = new URL("<server_url>");
//                 * HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
//                 * InputStream input = conn.getInputStream();
//                 * String jsonText = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
//                 * JSONObject jsonObjs = new JSONObject(jsonText);
//                 * String ak = jsonObjs.getString("AccessKeyId");
//                 * String sk = jsonObjs.getString("AccessKeySecret");
//                 * String token = jsonObjs.getString("SecurityToken");
//                 * String expiration = jsonObjs.getString("Expiration");
//                 */
//
//                String ak = "<ALIBABA_CLOUD_ACCESS_KEY_ID>";
//                String sk = "<ALIBABA_CLOUD_ACCESS_KEY_SECRET>";
//                String token = "<ALIBABA_CLOUD_SECURITY_TOKEN>";
//                String expiration = "<ALIBABA_CLOUD_EXPIRATION>";
//
//                // 用ak/sk/token/expiration构建OSSFederationToken
//                OSSFederationToken federationToken = new OSSFederationToken(ak, sk, token, expiration);
//                return federationToken;
//            }
//        };
//    }

}