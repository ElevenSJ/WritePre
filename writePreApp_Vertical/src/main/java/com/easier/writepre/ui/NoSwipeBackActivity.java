package com.easier.writepre.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.easier.writepre.R;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.OssTokenInfo;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.manager.ActStackManager;
import com.easier.writepre.param.OssTokenParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.OssTokenResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.MediaFile;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ShareContentCustomize;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.video.VideoRecordActivity;
import com.easier.writepre.widget.NetLoadingDailog;
import com.easier.writepre.widget.ScrollAlwaysTextView;
import com.google.gson.Gson;
import com.sj.autolayout.AutoLayoutActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NoSwipeBackActivity extends AutoLayoutActivity implements
        OnClickListener, WritePreListener<BaseResponse> {

    protected NetLoadingDailog dlgLoad;

    private OnekeyShare oks;

    private final DisplayMetrics displayMetrics = new DisplayMetrics();

    private long lastClickTime;

    private long startTime;//启动页面时间

    private int showTime;//页面停留时间单位：秒


    public boolean isDestroyed = false;

    /**
     * 本地需上传文件集合（本地选择文件）
     */
    private ArrayList<String> localFilePaths = new ArrayList<String>();
    /**
     * 过大文件压缩后文件集合（真正上传文件集合）
     */
    private ArrayList<String> compressPath = new ArrayList<String>();
    /**
     * 已上传文件集合
     */
    private ArrayList<String> ossCommitPath = new ArrayList<String>();
    /**
     * 上传任务
     */
    private OSSAsyncTask task;
    /**
     * oss保存路径
     */
    private String ossPath = "";

    private ViewGroup decor;

    private Handler ossHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (isDestroyed) {
                return;
            }
            switch (msg.what) {
                case COMP_FILE_START:
                    dlgLoad.loading("开始处理文件");
                    cOssHandler.sendEmptyMessage(COMP_FILE_START);
                    break;
                case COMP_FILE_ENR:
                    dlgLoad.loading("处理完成");
                    cOssHandler.sendEmptyMessage(COMP_FILE_ENR);
                    dlgLoad.loading("开始上传第（" + (ossIndexPath + 1) + "/"
                            + compressPath.size() + "）个文件");
                    if (task != null) {
                        task.cancel();
                    }
                    commitOss();
                    break;
                case COMMIT_FILE_OSS_START:
                    if (ossIndexPath == compressPath.size() - 1) {
                        ossHandler.sendEmptyMessage(COMMIT_FILE_OSS_END);
                    } else {
                        ossIndexPath++;
                        dlgLoad.loading("开始上传第（" + (ossIndexPath + 1) + "/"
                                + compressPath.size() + "）个文件");
                        commitOss();
                    }
                    break;
                case COMMIT_FILE_OSS_SUCCESS:
                    ossCommitPath.add(msg.obj.toString());
                    ossHandler.sendEmptyMessage(COMMIT_FILE_OSS_START);
                    break;
                case COMMIT_FILE_OSS_FAIL:
                    dlgLoad.loading("第" + msg.arg1 + "/" + msg.arg2 + "）个件上传失败");
                    Message cOssMsgFail = cOssHandler.obtainMessage();
                    cOssMsgFail.what = COMMIT_FILE_OSS_FAIL;
                    cOssMsgFail.obj = msg.arg1;
                    cOssMsgFail.sendToTarget();
                    ossHandler.sendEmptyMessage(COMMIT_FILE_OSS_START);
                    break;
                case COMMIT_FILE_OSS_END:
                    dlgLoad.loading("上传完成");
                    Message cOssMsg = cOssHandler.obtainMessage();
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.addAll(ossCommitPath);
                    cOssMsg.obj = arrayList;
                    if (ossCommitPath.size() == 0) {
                        cOssMsg.what = COMMIT_FILE_OSS_ALL_FAIL;
                    } else {
                        if (ossCommitPath.size() == compressPath.size()) {
                            cOssMsg.what = COMMIT_FILE_OSS_ALL_SUCCESS;
                        } else {
                            cOssMsg.what = COMMIT_FILE_OSS_ALL_NOT_SUCCESS;
                        }
                    }
                    cOssMsg.sendToTarget();
                    ossIndexPath = 0;
                    localFilePaths.clear();
                    compressPath.clear();
                    ossCommitPath.clear();
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private Handler cOssHandler;

    private int ossIndexPath = 0;

    public static final int COMP_FILE_START = 145863;

    public static final int COMP_FILE_ENR = 145864;

    public static final int COMMIT_FILE_OSS_START = 145865;

    public static final int COMMIT_FILE_OSS_SUCCESS = 145866;

    public static final int COMMIT_FILE_OSS_FAIL = 145867;

    public static final int COMMIT_FILE_OSS_END = 145868;

    public static final int COMMIT_FILE_OSS_ALL_SUCCESS = 145869;

    public static final int COMMIT_FILE_OSS_ALL_NOT_SUCCESS = 145870;

    public static final int COMMIT_FILE_OSS_ALL_FAIL = 145871;

    public static final int COMMIT_VOICE_OSS_SUCCESS = 145872;

    public static final int COMMIT_VOICE_OSS_FAIL = 145873;

    public static final int COMMIT_VIDEO_OSS_SUCCESS = 145874;

    public static final int COMMIT_VIDEO_OSS_FAIL = 145875;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        decor = (ViewGroup) this.getWindow().getDecorView();
        dlgLoad = new NetLoadingDailog(this);
        oks = new OnekeyShare();
        ActStackManager.getInstance().pushActivity(this);
    }

    public int getShowTime() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        showTime = (int) (System.currentTimeMillis() - startTime < 1000 ? 1000 : System.currentTimeMillis() - startTime);
        showTime = showTime / 1000;
        startTime = System.currentTimeMillis();
        return showTime;
    }

    public void sharedContent(String title, String txt, String uri,
                              String path, View v) {
        oks.setTitle(title == null ? "" : title);
        oks.setTitleUrl(uri == null ? "" : uri);
        oks.setText(txt == null ? "" : txt);
        oks.setUrl(uri == null ? "" : uri);
        oks.setFilePath(path == null ? "" : path);
        // oks.setComment();
        oks.setSite("写字派");
        oks.setSiteUrl(uri == null ? "" : uri);
        if (v == null) {
            oks.setImageUrl(uri == null ? "" : uri);
            oks.setImagePath(path == null ? "" : path);
        } else {
            oks.setViewToShare(v);
        }
        // oks.setShareFromQQAuthSupport(false);
        oks.show(this);
    }

    public void sharedContent(String title, String txt, String uri,
                              String path, View v, Bitmap bitmap, Context context) {
        oks.setTitle(title == null ? "" : title);
        oks.setTitleUrl(uri == null ? "" : uri);
        oks.setText(txt == null ? "" : txt);
        oks.setUrl(uri == null ? "" : uri);
        oks.setFilePath(path == null ? "" : path);
        // oks.setComment();
        oks.setSite("写字派");
        oks.setSiteUrl(uri == null ? "" : uri);
        if (v == null) {
            oks.setImageUrl(uri == null ? "" : uri);
            oks.setImagePath(path == null ? "" : path);
        } else {
            oks.setViewToShare(v, bitmap, context);
        }
        // oks.setShareFromQQAuthSupport(false);
        oks.show(this);
    }

    public void sharedContentCustomize(String title, String txt, String uri,
                                       String path, View v) {
        oks.setTitle(title == null ? "" : title);
        oks.setTitleUrl(uri == null ? "" : uri);
        oks.setText(txt == null ? "" : txt);
        oks.setUrl(uri == null ? "" : uri);
        oks.setFilePath(path == null ? "" : path);
        oks.setSite("写字派");
        oks.setSiteUrl(uri == null ? "" : uri);
        if (v == null) {
            oks.setImageUrl("http://www.shufap.net/xzpsrv/icon/icon01.png");
            oks.setImagePath(path == null ? "" : path);
        } else {
            oks.setViewToShare(v);
        }
        // oks.setShareFromQQAuthSupport(false);
        // oks.setShareContentCustomizeCallback(new ShareContentCustomize(txt));
        oks.setShareContentCustomizeCallback(new ShareContentCustomize(uri, txt));
        oks.show(this);
    }

    public void sharedContentCustomizePK(String title, String txt, String uri,
                                         String path, String URL) {
        oks.setTitle(title == null ? "" : title);
        oks.setTitleUrl(uri == null ? "" : uri);
        oks.setText(txt == null ? "" : txt);
        oks.setUrl(uri == null ? "" : uri);
        oks.setFilePath(path == null ? "" : path);
        oks.setSite("写字派");
        oks.setSiteUrl(uri == null ? "" : uri);
        oks.setImageUrl(URL);
        // oks.setShareFromQQAuthSupport(false);
        // oks.setShareContentCustomizeCallback(new ShareContentCustomize(txt));
        oks.setShareContentCustomizeCallback(new ShareContentCustomize(uri, txt));
        oks.show(this);
    }

    public OSSAsyncTask getTask() {
        return task;
    }

    public void setTask(OSSAsyncTask task) {
        this.task = task;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        startTime = System.currentTimeMillis();
    }

    public void onTopRightClick(View view) {
    }

    public void onTopLeftClick(View view) {
        this.finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }

    public void onTopLeftTextClick(View view) {
//        closeAndGotoMian();
    }

    public void onTopRightTxtClick(View view) {
    }

    public void onTopRightTxtClick1(View view) {
    }

    /**
     * 结束栈Main以上的页面
     */
    protected void closeAndGotoMian() {
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
        overridePendingTransition(0, R.anim.slide_right_out);
    }

    @Override
    public void onBackPressed() {
        if (task != null) {
            task.cancel();
        }
        overridePendingTransition(0, R.anim.slide_right_out);
        super.onBackPressed();
    }

    protected void setTopTitle(String title) {
        ScrollAlwaysTextView titleView = (ScrollAlwaysTextView) findViewById(R.id.top_title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    protected void setTopLeft(int drawableId) {
        View left = findViewById(R.id.img_back);
        if (left != null) {
            if (drawableId != -1) {
                left.setVisibility(View.VISIBLE);
                ((ImageView) left).setImageResource(drawableId);
            } else {
                left.setVisibility(View.GONE);
            }
        }
    }

    protected void setTopRight(int drawableId) {
        View right = findViewById(R.id.top_right);
        if (right != null) {
            if (drawableId != -1) {
                right.setVisibility(View.VISIBLE);
                ((ImageView) right).setImageResource(drawableId);
            } else {
                right.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示关闭按钮
     */
    protected void showCloseView() {
        findViewById(R.id.top_left_finish).setVisibility(View.VISIBLE);
    }

    protected void setTopRightTxt(String title) {
        View right = findViewById(R.id.top_right_txt);
        if (right != null) {
            if (!TextUtils.isEmpty(title)) {
                right.setVisibility(View.VISIBLE);
                ((TextView) right).setText(title);
            } else {
                right.setVisibility(View.GONE);
            }
        }
    }

    protected void setTopRightTxt1(String title) {
        View right = findViewById(R.id.top_right_txt1);
        if (right != null) {
            if (!TextUtils.isEmpty(title)) {
                right.setVisibility(View.VISIBLE);
                ((TextView) right).setText(title);
            } else {
                right.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        // if (v.getId() == R.id.top_right_txt) {
        // onTopRightTxtClick();
        // } else if (v.getId() == R.id.top_right) {
        // onTopRightClick(v);
        // }
    }

    public static void setListLabel(PullToRefreshListView listView,
                                    boolean isEnd) {
        if (listView == null) {
            return;
        }
        if (isEnd) {
            listView.setPullLabel("没有更多了", Mode.PULL_FROM_END);
        } else {
            listView.setPullLabel("正在加载数据", Mode.PULL_FROM_END);
        }
    }

    @Override
    public void onResponse(BaseResponse response) {

    }

    /**
     * 压缩图片
     */
    private void compFiles() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ossHandler.sendEmptyMessage(COMP_FILE_START);
                Bimp.picCompress(getApplicationContext(), localFilePaths,
                        compressPath);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ossHandler.sendEmptyMessage(COMP_FILE_ENR);

                    }
                });
            }
        }).start();
    }

    public int sortNum;       // 上传文件顺序

    /**
     * 上传文件到OSS
     */
    private void commitOss() {
        LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
        OssTokenInfo ossInfo = SPUtils.instance().getOSSTokenInfo();
        SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
        OSS oss = OSSSTS(ossInfo.getAccess_key_id(),
                ossInfo.getAccess_key_Secret(), ossInfo.getSecurity_token(),
                ossInfo.getExpiration());
        String filePath = compressPath.get(ossIndexPath);
        sortNum = ossIndexPath;
        int lastDot = filePath.lastIndexOf(".");
        String pathEndTag = lastDot < 0 ? "" : filePath.substring(lastDot);

        if (MediaFile.isImageFileType(filePath)) {
            ossPath = propEntity.getOss_root() + "/"
                    + loginEntity.get_id() + Constant.OSS_IMAGE_PATH
                    + System.currentTimeMillis() + "_" + ossIndexPath + ".png";

        } else if (MediaFile.isVideoFileType(filePath)) {
            ossPath = propEntity.getOss_root() + "/"
                    + loginEntity.get_id() + Constant.OSS_VIDEO_PATH
                    + System.currentTimeMillis() + "_" + ossIndexPath + pathEndTag;

        } else if (MediaFile.isAudioFileType(filePath)) {
            ossPath = propEntity.getOss_root() + "/"
                    + loginEntity.get_id() + Constant.OSS_VOICE_PATH
                    + System.currentTimeMillis() + "_" + ossIndexPath + pathEndTag;

        }
        PutObjectRequest put = new PutObjectRequest(propEntity.getOss_bucket(),
                ossPath, compressPath.get(ossIndexPath));
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

            @Override
            public void onProgress(PutObjectRequest arg0, long currentSize,
                                   long totalSize) {
                LogUtils.e("currentSize: " + currentSize
                        + " totalSize: " + totalSize);
            }
        });

        task = oss.asyncPutObject(put,
                new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                    @Override
                    public void onSuccess(PutObjectRequest request,
                                          PutObjectResult result) {
                        LogUtils.d("PutObject" + "UploadSuccess");
                        LogUtils.d("ETag" + result.getETag());
                        LogUtils.d("RequestId" + result.getRequestId());
                        Message msg = ossHandler.obtainMessage();
                        msg.what = COMMIT_FILE_OSS_SUCCESS;
                        msg.obj = ossPath;
                        msg.sendToTarget();
                        if (NoSwipeBackActivity.this instanceof XsfTecPracticeExamActivity) {
                            ContentValues cv = new ContentValues();
                            LogUtils.e("上传序号-->" + XsfTecPracticeExamActivity.sortNumList.get(sortNum));
                            LogUtils.e("上传下标-->" + sortNum);
                            cv.put("up_state", "200");
                            cv.put("oss_path", ossPath);
                            String[] args = {XsfTecPracticeExamActivity.sortNumList.get(sortNum)};
                            int code = DBHelper.instance().update("works_table", cv, "sort_num=?", args);
                            LogUtils.e("上传成功更新数据-->" + code);
                            LogUtils.e("视频作品上传---->" + ossIndexPath);
                        }
                    }

                    @Override
                    public void onFailure(PutObjectRequest request,
                                          ClientException clientExcepion,
                                          ServiceException serviceException) {
                        // 请求异常
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            // ToastUtil.show("网络异常");
                            LogUtils.e("ErrorCode" + clientExcepion.getMessage());
                            clientExcepion.printStackTrace();
                        }
                        if (serviceException != null) {
                            // 服务异常
                            LogUtils.e("ErrorCode" + serviceException.getErrorCode());
                            LogUtils.e("RequestId" + serviceException.getRequestId());
                            LogUtils.e("HostId" + serviceException.getHostId());
                            LogUtils.e("RawMessage" +
                                    serviceException.getRawMessage());
                        }
                        Message msg = ossHandler.obtainMessage();
                        msg.what = COMMIT_FILE_OSS_FAIL;
                        msg.obj = ossPath;
                        msg.sendToTarget();
//                        if (NoSwipeBackActivity.this instanceof XsfTecPracticeExamActivity) {
//                            ContentValues cv = new ContentValues();
//                            cv.put("up_state", (ossIndexPath + 1) + "");
//                            cv.put("oss_path", ossPath);
//                            String[] args = {(ossIndexPath + 1) + ""};
//                            int code = DBHelper.instance().update("works_table", cv, "sort_num=?", args);
//                            LogUtils.e("上传失败更新数据-->" + code);
//                            LogUtils.e("视频作品上传---->" + ossIndexPath);
//                        }
                    }
                });

    }

    public int getHeight(float scale) {
        if (scale > 1) {
            return displayMetrics.heightPixels;
        }
        return (int) (displayMetrics.heightPixels * scale);
    }

    public int getWidth(float scale) {
        if (scale > 1) {
            return displayMetrics.widthPixels;
        }
        return (int) (displayMetrics.widthPixels * scale);
    }

    /**
     * STS 鉴权
     *
     * @return
     */
    public OSS OSSSTS(final String access_key_id,
                      final String access_key_Secret, final String security_token,
                      final String expiration) {
        OSSCredentialProvider credetialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                return new OSSFederationToken(access_key_id, access_key_Secret,
                        security_token, expiration);
            }
        };
        return new OSSClient(NoSwipeBackActivity.this, SPUtils.instance()
                .getSocialPropEntity().getOss_endpoint(), credetialProvider);
    }


    /**
     * 上传文件到oss
     *
     * @param filePaths
     * @param ossHandler
     */
    public void commitFilesOss(ArrayList<String> filePaths, Handler ossHandler) {
        if (filePaths == null || filePaths.isEmpty() || ossHandler == null) {
            ToastUtil.show("上传参数错误!");
            return;
        }
        cOssHandler = ossHandler;
        if (filePaths == null || filePaths.isEmpty()) {
            ToastUtil.show("当前无文件可上传");
            return;
        }
        if (LoginUtil.checkLogin(this)) {
            dlgLoad.loading("获取文件上传信息");
            localFilePaths.clear();
            localFilePaths.addAll(filePaths);
            requestToken();
        }
    }

    public void commitFilesOss(String imagePath, Handler ossHandler) {
        if (TextUtils.isEmpty(imagePath) || ossHandler == null) {
            ToastUtil.show("上传参数错误!");
            return;
        }
        ArrayList<String> images = new ArrayList<String>();
        images.add(imagePath);
        commitFilesOss(images, ossHandler);
    }

    public void commitVoiceOss(final String voicePath, final Handler ossHandler) {
        if (TextUtils.isEmpty(voicePath) || ossHandler == null) {
            ToastUtil.show("上传参数错误!");
            return;
        }
        dlgLoad.loading();
        RequestManager.request(this, new OssTokenParams(),
                OssTokenResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof OssTokenResponse) {
                                OssTokenResponse otrResult = (OssTokenResponse) response;
                                if (otrResult != null) {
                                    OssTokenInfo rBody = otrResult.getRepBody();
                                    SPUtils.instance().put(SPUtils.OSS_DATA,
                                            new Gson().toJson(rBody));
                                    LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
                                    OssTokenInfo ossInfo = SPUtils.instance().getOSSTokenInfo();
                                    SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
                                    OSS oss = OSSSTS(ossInfo.getAccess_key_id(),
                                            ossInfo.getAccess_key_Secret(), ossInfo.getSecurity_token(),
                                            ossInfo.getExpiration());
                                    final String ossPath = propEntity.getOss_root()
                                            + "/"
                                            + loginEntity.get_id()
                                            + Constant.OSS_VOICE_PATH
                                            + System.currentTimeMillis()
                                            + "_"
                                            + voicePath.substring(voicePath.lastIndexOf("/") + 1);
                                    PutObjectRequest put = new PutObjectRequest(propEntity.getOss_bucket(),
                                            ossPath, voicePath);
                                    put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

                                        @Override
                                        public void onProgress(PutObjectRequest arg0, long currentSize,
                                                               long totalSize) {
                                            LogUtils.d("PutObject" + "currentSize: " + currentSize
                                                    + " totalSize: " + totalSize);
                                        }
                                    });

                                    task = oss.asyncPutObject(put,
                                            new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                                                @Override
                                                public void onSuccess(PutObjectRequest request,
                                                                      PutObjectResult result) {
                                                    LogUtils.d("PutObject" + "UploadSuccess");
                                                    LogUtils.d("ETag" + result.getETag());
                                                    LogUtils.d("RequestId" + result.getRequestId());
                                                    Message msg = ossHandler.obtainMessage();
                                                    msg.what = COMMIT_VOICE_OSS_SUCCESS;
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("filePath", voicePath);
                                                    bundle.putString("ossPath", ossPath);
                                                    msg.setData(bundle);
                                                    msg.sendToTarget();
                                                }

                                                @Override
                                                public void onFailure(PutObjectRequest request,
                                                                      ClientException clientExcepion,
                                                                      ServiceException serviceException) {
                                                    // 请求异常
                                                    if (clientExcepion != null) {
                                                        // 本地异常如网络异常等
                                                        // ToastUtil.show("网络异常");
                                                        clientExcepion.printStackTrace();
                                                    }
                                                    if (serviceException != null) {
                                                        // 服务异常
                                                        LogUtils.e("ErrorCode" + serviceException.getErrorCode());
                                                        LogUtils.e("RequestId" + serviceException.getRequestId());
                                                        LogUtils.e("HostId" + serviceException.getHostId());
                                                        LogUtils.e("RawMessage" +
                                                                serviceException.getRawMessage());
                                                    }
                                                    Message msg = ossHandler.obtainMessage();
                                                    msg.what = COMMIT_VOICE_OSS_FAIL;
                                                    msg.sendToTarget();
                                                }
                                            });

                                } else {
                                    dlgLoad.dismissDialog();
                                    ToastUtil.show("获取文件上传信息异常!");
                                }
                            }
                        } else {
                            if (response instanceof OssTokenResponse) {
                                dlgLoad.dismissDialog();
                                ToastUtil.show("获取文件上传信息错误!");

                            }
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {
                    }
                }, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    public void commitVideoOss(final String videoPath, final Handler ossHandler) {
        if (TextUtils.isEmpty(videoPath) || ossHandler == null) {
            ToastUtil.show("上传参数错误!");
            return;
        }
        dlgLoad.loading();
        RequestManager.request(this, new OssTokenParams(),
                OssTokenResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof OssTokenResponse) {
                                OssTokenResponse otrResult = (OssTokenResponse) response;
                                if (otrResult != null) {
                                    OssTokenInfo rBody = otrResult.getRepBody();
                                    SPUtils.instance().put(SPUtils.OSS_DATA,
                                            new Gson().toJson(rBody));
                                    LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
                                    OssTokenInfo ossInfo = SPUtils.instance().getOSSTokenInfo();
                                    SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
                                    OSS oss = OSSSTS(ossInfo.getAccess_key_id(),
                                            ossInfo.getAccess_key_Secret(), ossInfo.getSecurity_token(),
                                            ossInfo.getExpiration());
                                    final String ossPath = propEntity.getOss_root()
                                            + "/"
                                            + loginEntity.get_id()
                                            + Constant.OSS_VIDEO_PATH
                                            + System.currentTimeMillis()
                                            + "_"
                                            + videoPath.substring(videoPath.lastIndexOf("/") + 1);
                                    PutObjectRequest put = new PutObjectRequest(propEntity.getOss_bucket(),
                                            ossPath, videoPath);
                                    put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

                                        @Override
                                        public void onProgress(PutObjectRequest arg0, long currentSize,
                                                               long totalSize) {
                                            LogUtils.d("PutObject" + "currentSize: " + currentSize
                                                    + " totalSize: " + totalSize);
                                        }
                                    });

                                    task = oss.asyncPutObject(put,
                                            new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                                                @Override
                                                public void onSuccess(PutObjectRequest request,
                                                                      PutObjectResult result) {
                                                    LogUtils.d("PutObject" + "UploadSuccess");
                                                    LogUtils.d("ETag" + result.getETag());
                                                    LogUtils.d("RequestId" + result.getRequestId());
                                                    Message msg = ossHandler.obtainMessage();
                                                    msg.what = COMMIT_VIDEO_OSS_SUCCESS;
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("filePath", videoPath);
                                                    bundle.putString("ossPath", ossPath);
                                                    msg.setData(bundle);
                                                    msg.sendToTarget();
                                                }

                                                @Override
                                                public void onFailure(PutObjectRequest request,
                                                                      ClientException clientExcepion,
                                                                      ServiceException serviceException) {
                                                    // 请求异常
                                                    if (clientExcepion != null) {
                                                        // 本地异常如网络异常等
                                                        // ToastUtil.show("网络异常");
                                                        clientExcepion.printStackTrace();
                                                    }
                                                    if (serviceException != null) {
                                                        // 服务异常
                                                        LogUtils.e("ErrorCode" + serviceException.getErrorCode());
                                                        LogUtils.e("RequestId" + serviceException.getRequestId());
                                                        LogUtils.e("HostId" + serviceException.getHostId());
                                                        LogUtils.e("RawMessage" +
                                                                serviceException.getRawMessage());
                                                    }
                                                    Message msg = ossHandler.obtainMessage();
                                                    msg.what = COMMIT_VIDEO_OSS_FAIL;
                                                    msg.sendToTarget();
                                                }
                                            });

                                } else {
                                    dlgLoad.dismissDialog();
                                    ToastUtil.show("获取文件上传信息异常!");
                                }
                            }
                        } else {
                            if (response instanceof OssTokenResponse) {
                                dlgLoad.dismissDialog();
                                ToastUtil.show("获取文件上传信息错误!");

                            }
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {

                    }
                }, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    public void requestToken() {
        RequestManager.request(this, new OssTokenParams(),
                OssTokenResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof OssTokenResponse) {
                                OssTokenResponse otrResult = (OssTokenResponse) response;
                                if (otrResult != null) {
                                    OssTokenInfo rBody = otrResult.getRepBody();
                                    SPUtils.instance().put(SPUtils.OSS_DATA,
                                            new Gson().toJson(rBody));
                                    compFiles();
                                } else {
                                    dlgLoad.dismissDialog();
                                    ToastUtil.show("获取文件上传信息异常!");
                                }
                            }
                        } else {
                            if (response instanceof OssTokenResponse) {
                                dlgLoad.dismissDialog();
                                ToastUtil.show("获取文件上传信息错误!");

                            }
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {

                    }
                }, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.gc();
        if (task != null) {
            task.cancel();
        }
        isDestroyed = true;
        ActStackManager.getInstance().removeActivity(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (decor != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(decor.getApplicationWindowToken(), 0);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (lastClickTime == 0) {
//                LogUtils.e("dispatchTouchEvent:ACTION_DOWN:初次点击，有效");
                lastClickTime = ev.getDownTime();
//                LogUtils.e("dispatchTouchEvent:ACTION_DOWN:fist:"
//                        + lastClickTime);
                return super.dispatchTouchEvent(ev);
            } else {
//                LogUtils.e("dispatchTouchEvent:ACTION_DOWN:非初次点击，判断");
//                LogUtils.e("dispatchTouchEvent:ACTION_DOWN:second:"
//                        + ev.getDownTime());
                if (ev.getDownTime() - lastClickTime <= 400 && !(this instanceof VideoRecordActivity)) {
                    LogUtils.e("dispatchTouchEvent:ACTION_DOWN:快速点击，无效");
                    return true;
                } else {
//                    LogUtils.e("dispatchTouchEvent:ACTION_DOWN:非快速点击，有效，状态恢复");
                    lastClickTime = ev.getDownTime();
                    return super.dispatchTouchEvent(ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        // TODO Auto-generated method stub

    }
}
