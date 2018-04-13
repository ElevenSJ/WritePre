package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.manager.AntZipManager;
import com.easier.writepre.param.ExamPkgGetParams;
import com.easier.writepre.param.ExamTimeParams;
import com.easier.writepre.param.ExamZipPkgGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ExamPkgResponse;
import com.easier.writepre.response.ExamTimeResponse;
import com.easier.writepre.response.ExamZipPkgResponse;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.MD5Util;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.CircleLevelProgress;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.WaveLoadingView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LoaddingPaperActivity extends BaseActivity {
    private String exam_id, type;
    private Button btStart;
    private CircleLevelProgress circleLevelProgress;
    private static final int GET_EXAM_PKG = 0X0004;
    private static final int DOWNLOAD_EXAM_PKG = 0X0005;
    private static final int UPDATE_EXAM_PKG = 0X0006;
    private static final int GET_ZIPEXAM_PKG = 0X0007;
    private static final int UPDATE_ZIPEXAM_PKG = 0X0008;
    private static final int DOWNLOAD_EXAM_PKG_SUCCESS = 0X0009;
    private static final int DOWNLOAD_EXAM_PKG_FAILURE = 0X0010;
    private int currentProgress;
    private int allProgress;
    private ExamPkgResponse.ExamPkgInfo examPkgInfo;
    private final int CurrentDownLoadTimes = 0; //当前重新下载次数
    private final int MaxDownLoadTimes = 3; //下载失败时最大重新下载次数
    //定义一个进度
    private int progress;
    private ArrayList<ExamPkgResponse.PkgInfo> localImageList;
    private ArrayList<ExamPkgResponse.PkgInfo> errorImageList;
    private ExamZipPkgResponse.ExamZipPkgInfo examZipPkgInfo;
    private String localPath = "";//试卷的解压路径
    private String zipUrl;//试卷下载路径
    private WaveLoadingView wv_progress;
    private String theory_info_url;//理论考试说明
    private WebView webView;
    private ProgressBar progressBar;
    private int theory_count_limit;//剩余考试次数
    private String theory_score;//历史最高成绩
    private boolean isNeedShowInfo = true;
    private String pkg_id;//试卷id
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_EXAM_PKG://获取考试试卷
                    examPkgInfo = (ExamPkgResponse.ExamPkgInfo) msg.obj;
                    if (examPkgInfo.getList() != null) {
                        localImageList = new ArrayList<>();
                        errorImageList = new ArrayList<>();
                        for (ExamPkgResponse.PkgInfo pkgInfo : examPkgInfo.getList()) {
                            if (!TextUtils.isEmpty(pkgInfo.getPic_url())) {
                                localImageList.add(pkgInfo);
                            }
                        }
                        allProgress = localImageList.size();
                        sendEmptyMessage(DOWNLOAD_EXAM_PKG);
                    }
                    break;
                case DOWNLOAD_EXAM_PKG://下载试卷中的图片
                    if (localImageList != null && !localImageList.isEmpty()) {
                        //取队列的第一个下载
                        downloadPic2Local(localImageList.get(0));
                    }
                    break;
                case UPDATE_EXAM_PKG://更新试卷
                    ExamPkgResponse.PkgInfo pkgInfoTemp = (ExamPkgResponse.PkgInfo) msg.obj;
                    //根据下载的回调的本地图片路径判断 是否有必要操作队列
                    if (TextUtils.isEmpty(pkgInfoTemp.getPic_local())) {
                        //超过下载重试上限
                        if (pkgInfoTemp.getCurrentDownloadTimes() >= MaxDownLoadTimes) {
                            errorImageList.add(pkgInfoTemp);
                            localImageList.remove(0);
                        } else {
                            //下载失败,将队列中的当前下载元素放置到末尾
                            localImageList.add(pkgInfoTemp);
                            //删除第一个
                            localImageList.remove(0);
                        }
                    } else {
                        //下载成功,删除队列中的第一个,接着下载
                        localImageList.remove(0);
                        //进度增加
                        currentProgress++;
                        //下载反馈的结果更新到对象列表中
                        for (int i = 0; i < examPkgInfo.getList().size(); i++) {
                            if (TextUtils.equals(examPkgInfo.getList().get(i).get_id(), pkgInfoTemp.get_id())) {
                                examPkgInfo.getList().get(i).setCurrentDownloadTimes(pkgInfoTemp.getCurrentDownloadTimes());
                                examPkgInfo.getList().get(i).setPic_local(pkgInfoTemp.getPic_local());
                                break;
                            }
                        }
                    }
                    progress = 100 * currentProgress / allProgress;
                    //判断当前队列中有无下载数据
                    if (!localImageList.isEmpty()) {
                        //下载队列有元素,继续下载
                        circleLevelProgress.setCurrentProgress(progress);
                        sendEmptyMessage(DOWNLOAD_EXAM_PKG);
                    } else {
                        //下载队列无数据,需要查看是否存在无法下载的元素
                        if (errorImageList.isEmpty()) {
                            //无法的下载列表无数据 无数据:队列中所有的数据都下载完成.[可以开始考试]
                        } else {
                            //无法的下载列表有数据 有数据:队列中有的数据无法下载完成.[不可以开始考试]
                        }
                    }

                    break;
                case UPDATE_ZIPEXAM_PKG://更新zip试卷 下载进度
                    progress = (int) (long) msg.obj;
                    wv_progress.setProgressValue(progress);
                    wv_progress.setCenterTitle(progress + "%");
                    if (progress >= 100) {
                        wv_progress.setBottomTitle("加载完成");
                    } else {
                        wv_progress.setBottomTitle("加载中...");
                    }
                    break;
                case GET_ZIPEXAM_PKG://获取zip包形式的试卷
                    examZipPkgInfo = (ExamZipPkgResponse.ExamZipPkgInfo) msg.obj;
                    if (!TextUtils.isEmpty(examZipPkgInfo.getZipUrl())) {
                        zipUrl = StringUtil.getImgeUrl(examZipPkgInfo.getZipUrl());
                        if (zipUrl.startsWith("https")) {
                            zipUrl = zipUrl.replaceFirst("https", "http");
                        }
                        LogUtils.e("zipurl:" + zipUrl);
                    } else {
                        ToastUtil.show("试卷地址获取失败!");
                    }
                    if (!TextUtils.isEmpty(examZipPkgInfo.getTitle())) {
                        setTopTitle(examZipPkgInfo.getTitle());
                    }
                    if (!TextUtils.isEmpty(examZipPkgInfo.get_id())) {
                        pkg_id = examZipPkgInfo.get_id();
                    }
                    break;
                case DOWNLOAD_EXAM_PKG_SUCCESS://下载成功
                    String loaclZipPath = (String) msg.obj;
                    AntZipManager.getInstance().unZipTask(loaclZipPath, FileUtils.SD_DOWN_PATH + "exam", new AntZipManager.AntZipCallBack() {
                        @Override
                        public void onAntZipMaskZipCallback(String result) {

                        }

                        @Override
                        public void onAntZipUnZipCallback(String result) {
                            localPath = result;
                            if (TextUtils.isEmpty(localPath)) {
                                ToastUtil.show("无法获取试卷路径");
                                return;
                            }
                            //解压完成获取考试时间
                            requestExamTime();

                        }
                    });
                    break;
                case DOWNLOAD_EXAM_PKG_FAILURE://下载失败
                    progress = 0;
                    String errorMsg = (String) msg.obj;
                    ToastUtil.show("下载失败!");
                    break;


            }
        }
    };

    /**
     * 前往考试页面
     */
    private void toExamActivity(long time) {
        Intent intent = new Intent(LoaddingPaperActivity.this, TheoreticalTestActivity.class);
        intent.putExtra("pkg_id", examZipPkgInfo.get_id());
        intent.putExtra("title", examZipPkgInfo.getTitle());
        intent.putExtra("localPath", localPath);
        intent.putExtra("currentTime", time);
        intent.putExtra("exam_type", TheoreticalTestActivity.EXAM_TYPE_FORMAL);
        startActivity(intent);
        LoaddingPaperActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadding_paper);
        theory_info_url = getIntent().getStringExtra("theory_info_url");
        theory_score = getIntent().getStringExtra("theory_score");
        type = (String) SPUtils.instance().get("stu_type", "0");
        theory_count_limit = getIntent().getIntExtra("theory_count_limit", 0);
        initView();
        requestExamZipPkg();

    }

    /**
     * 获取考试时间
     */
    private void requestExamTime() {
        if (TextUtils.isEmpty(pkg_id)) {
            ToastUtil.show("试卷ID不存在,考试信息获取失败!");
            return;
        }
        RequestManager.request(this, new ExamTimeParams(pkg_id, type),
                ExamTimeResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    private void initView() {
        setTopTitle("考生须知");
        wv_progress = (WaveLoadingView) findViewById(R.id.wv_progress);
        wv_progress.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        wv_progress.setAnimDuration(3000);
        wv_progress.setBorderWidth(1.0f);
        wv_progress.setProgressValue(0);
        wv_progress.startAnimation();

        btStart = (Button) findViewById(R.id.bt_start);
        circleLevelProgress = (CircleLevelProgress) findViewById(R.id.progress);
        btStart.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        webView.setHapticFeedbackEnabled(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new LoaddingPaperActivity.WebChromeClient());

        if (TextUtils.isEmpty(theory_info_url)) {
            ToastUtil.show("链接地址为空");
            return;
        }
        webView.loadUrl(theory_info_url);
    }

    public class WebChromeClient extends com.tencent.smtt.sdk.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                if (progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    /**
     * 获取考试试卷
     */
    private void requestExamPkg() {
        if (!TextUtils.isEmpty(exam_id) && !TextUtils.isEmpty(type)) {
            RequestManager.request(this, new ExamPkgGetParams(exam_id, type), ExamPkgResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_school_server());
        } else {
            ToastUtil.show("试卷下载失败,参数无效!");
        }
    }

    /**
     * 获取考试试卷zip包
     */
    private void requestExamZipPkg() {
        if (!TextUtils.isEmpty(type)) {
            RequestManager.request(this, new ExamZipPkgGetParams(type), ExamZipPkgResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_school_server());
        } else {
            ToastUtil.show("试卷下载失败,参数无效!");
        }
    }

    /**
     * 将试卷中的图片下载到本地
     */
    private void downloadPic2Local(final ExamPkgResponse.PkgInfo pkgInfo) {
        new HttpUtils().download(StringUtil.getImgeUrl(pkgInfo.getPic_url()), FileUtils.SD_IMAGES_PATH + MD5Util.MD5(pkgInfo.getPic_url()), true, false, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                pkgInfo.setPic_local(responseInfo.result.getPath());
                handler.obtainMessage(UPDATE_EXAM_PKG, pkgInfo).sendToTarget();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                pkgInfo.setPic_local("");
                pkgInfo.setCurrentDownloadTimes(pkgInfo.getCurrentDownloadTimes() + 1);
                handler.obtainMessage(UPDATE_EXAM_PKG, pkgInfo).sendToTarget();
            }
        });
    }

    /**
     * 展示以往考试信息
     *
     * @param text
     */
    private void showInfoDialog(String text) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_submitexam,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_tips);
        TextView confirm = (TextView) view.findViewById(R.id.tv_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView line = (TextView) view.findViewById(R.id.tv_line);
        tv.setText(text);
        cancel.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        confirm.setText("我知道了");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    /**
     * 将zip试卷下载到本地
     */
    private void downloadZip2Local(String zipurl) {
        FileUtils.deleteFile(FileUtils.SD_DOWN_PATH + MD5Util.MD5(zipurl));
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(zipurl, FileUtils.SD_DOWN_PATH + MD5Util.MD5(zipurl) + ".zip", false, false, new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                long currentProgress = 100 * current / total;
                handler.obtainMessage(UPDATE_ZIPEXAM_PKG, currentProgress).sendToTarget();
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                LogUtils.e("试卷路径:" + responseInfo.result.getPath());
                handler.obtainMessage(DOWNLOAD_EXAM_PKG_SUCCESS, responseInfo.result.getPath()).sendToTarget();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                handler.obtainMessage(DOWNLOAD_EXAM_PKG_FAILURE, s).sendToTarget();
            }

        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_start:
                if (isNeedShowInfo) {
                    isNeedShowInfo = false;
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("您还有 " + theory_count_limit + " 次理论考试机会");
                    if (Float.parseFloat(theory_score) > 0) {
                        stringBuffer.append("\n");
                        stringBuffer.append("历史最高成绩: " + theory_score + " 分");
                    }
                    showInfoDialog(stringBuffer.toString());
                    return;
                }
                if (!TextUtils.isEmpty(zipUrl)) {
                    if (progress == 0) {
                        progress++;
                        findViewById(R.id.rel_desc).setVisibility(View.GONE);
                        wv_progress.setVisibility(View.VISIBLE);
                        downloadZip2Local(zipUrl);
                    } else if (progress > 0 && progress < 100) {
                        ToastUtil.show("试卷加载中请稍后...");
                    } else if (progress == 100) {
                        ToastUtil.show("正在组装试题");
                    }
                } else {
                    ToastUtil.show("无法获取试卷地址");
                }
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if (response == null) {
            return;
        }
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof ExamPkgResponse) {
                ExamPkgResponse examPkgResponse = (ExamPkgResponse) response;
                if (examPkgResponse != null) {
                    ExamPkgResponse.ExamPkgInfo examPkgInfo = examPkgResponse.getRepBody();
                    handler.obtainMessage(GET_EXAM_PKG, examPkgInfo).sendToTarget();
                }
            } else if (response instanceof ExamZipPkgResponse) {
                ExamZipPkgResponse examZipPkgResponse = (ExamZipPkgResponse) response;
                if (examZipPkgResponse != null) {
                    ExamZipPkgResponse.ExamZipPkgInfo examZipPkgInfo = examZipPkgResponse.getRepBody();
                    handler.obtainMessage(GET_ZIPEXAM_PKG, examZipPkgInfo).sendToTarget();
                }
            } else if (response instanceof ExamTimeResponse) {
                ExamTimeResponse.ExamBody examBody = ((ExamTimeResponse) response).getRepBody();
                if (examBody != null) {
                    if (!TextUtils.isEmpty(examBody.getCurrent_time())) {
                        String currentTime, endTime;
                        currentTime = examBody.getCurrent_time();
                        endTime = examBody.getEnd_time();
                        long mCountDownTime = ((getTimeStr2Long(endTime) - getTimeStr2Long(currentTime)))/1000;
                        LogUtils.e("mCountDownTime:" + mCountDownTime);
                        toExamActivity(mCountDownTime);
                    }
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 将日期字符串转成long类型
     *
     * @param strTime 日期字符串
     * @return
     */
    private long getTimeStr2Long(String strTime) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(strTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTimeInMillis();
    }
}
