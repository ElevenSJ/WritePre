package com.easier.writepre.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.entity.PicInfo;
import com.easier.writepre.entity.VideoInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CourseContentParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseContentResponse;
import com.easier.writepre.response.CourseContentResponse.CourseContentResponseBody;
import com.easier.writepre.ui.CourseDetailActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.ui.MiaoHongActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kai.zhong
 */
public class CourseDetailFragment extends BaseFragment {

    private WebView wv_detail;
    private ImageView iv_close;
    private FrameLayout video;
    // private ProgressBar progressBar;

    private List<CourseContentInfo> wordList;// 课程内容集合
    private List<VideoInfo> videoList;// 视频集合

    private String big_pic_url;

    private String cs_cata_id = "";// 章节id

    private boolean FROM_FLAG = false; // 用于标识课程还是知识库 true知识库 false课程


    private String group = "";
    private String title = "";

    private CourseContentResponseBody mCourseContentResponseBody;

    /**
     * 多张图片左右滑动预览
     */
    private List<PicInfo> picList;
    private ArrayList<String> picUrls = new ArrayList<String>();
    private String[] pic_id = null;

    public CourseDetailFragment newInstance(String cs_cata_id, String group, String title, boolean FROM_FLAG) {
        final CourseDetailFragment f = new CourseDetailFragment();
        final Bundle args = new Bundle();
        args.putString("cs_cata_id", cs_cata_id);
        args.putString("group", group);
        args.putString("title", title);
        args.putBoolean("from_flag", FROM_FLAG);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cs_cata_id = getArguments() != null ? getArguments().getString("cs_cata_id") : "";
        FROM_FLAG = getArguments() != null ? getArguments().getBoolean("from_flag") : false;
        title = getArguments() != null ? getArguments().getString("title") : "";
        group = getArguments() != null ? getArguments().getString("group") : "";
    }

    @Override
    public int getContextView() {
        return R.layout.course_detail_fragment;
    }


    @Override
    protected void init() {
        wv_detail = (WebView) findViewById(R.id.webview);
        // progressBar = (ProgressBar) findViewById(R.id.progress);
        video = (FrameLayout) findViewById(R.id.video);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        wv_detail.setWebViewClient(new CustomWebViewClient());
        initWebViewSetting();
//        wv_detail.setWebChromeClient(new CustomWebChromeClient());
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            LogUtils.e("courseDetail-----onPageStarted" + "url:" + s);
            super.onPageStarted(webView, s, bitmap);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            LogUtils.e("courseDetail-----shouldOverrideUrlLoading:" + url);
            webView.loadUrl(url);
            return true;
        }

//        @Override
//        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
//            LogUtils.e("courseDetail-----onReceivedError" + webResourceError.getDescription());
////            super.onReceivedError(webView, webResourceRequest, webResourceError);
//            addErrorView();
//        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            LogUtils.e("courseDetail-----onPageFinished" + "webView url:" + webView.getUrl());
            LogUtils.e("courseDetail-----onPageFinished" + "url:" + url);
            parseUrl(url);
        }
    }

    private void initWebViewSetting() {
        WebSettings webSetting = wv_detail.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setUserAgentString(
                "Mozilla/5.0 (iPad; CPU OS 5_1 " + "like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 "
                        + "Mobile/9B176 Safari/7534.48.3");
//        Bundle data = new Bundle();
//        //true表示标准全屏，false表示X5全屏；不设置默认false，
//        data.putBoolean("standardFullScreen", false);
//        //false：关闭小窗；true：开启小窗；不设置默认true，
//        data.putBoolean("supportLiteWnd", true);
//        //1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
//        data.putInt("DefaultVideoScreen", 1);
//        wv_detail.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
    }

    private void parseUrl(String url) {
        if (FROM_FLAG) {
            return;
        }
        if (url.contains("#")) {
            if (url.endsWith("vod")) {// 进入视频
                String video_id = url.substring(url.indexOf("#") + 1, url.length() - 4);
                if (videoList == null || videoList.size() == 0) {
                    return;
                } else {
                    for (VideoInfo mVideoInfo : videoList) {
                        if (mVideoInfo.get_id().equals(video_id)) {
                            Intent intent = new Intent(getActivity(), MediaActivity.class);
                            intent.putExtra(MediaActivity.URL, StringUtil.getImgeUrl(mVideoInfo.getUrl()));
                            startActivity(intent);
                            break;
                        }
                    }
                }

            } else if (url.endsWith("pic")) {// 进入图片
                if (!TextUtils.isEmpty(big_pic_url)) {
                    picUrls.clear();
                    pic_id = url.substring(url.indexOf("#") + 1, url.length() - 4)
                            .split("@");
                    if (picList == null || picList.size() == 0) {
                        picUrls.add(big_pic_url);
                    } else {
                        for (int i = 0; i < pic_id.length; i++) {
                            for (int j = 0; j < picList.size(); j++) {
                                if (pic_id[i].equals(picList.get(j).get_id())) {
                                    picUrls.add(picList.get(j).getUrl());
                                }
                            }
                        }
                    }
                    String[] tempStr = picUrls.toArray(new String[picUrls.size()]);
                    imageBrower(0, tempStr);
                } else {
                    return;
                }

            } else {// 进入描红
                if (wordList == null || wordList.size() == 0) {
                    return;
                } else {
                    // 跳转到描红页面
                    Intent intent = new Intent(getActivity(), MiaoHongActivity.class);
                    intent.putExtra("courseName", group + title);
                    // 当前点击的范字id
                    String fz_id = url.split("#")[1];
                    intent.putExtra("fz_id", fz_id);
                    intent.putExtra("wordList", (Serializable) wordList);
                    getActivity().startActivity(intent);
                }

            }
        }
    }

    protected void addErrorView() {
        TextView textView = new TextView(getActivity());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setText(Html.fromHtml("<u>加载页面失败,点击重试</u>"));
        textView.setTextColor(getActivity().getResources().getColor(R.color.black));
        wv_detail.addView(textView);
        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                wv_detail.removeAllViews();
                // 2003 课程--课程内容(包含范字)
                if (FROM_FLAG) {
                    wv_detail.loadUrl(StringUtil.getImgeUrl(cs_cata_id));
                } else {
                    loadingDlg.loading();
                    RequestManager.request(getActivity(), new CourseContentParams(cs_cata_id),
                            CourseContentResponse.class, CourseDetailFragment.this, Constant.URL);
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 2003 课程--课程内容(包含范字)
        if (FROM_FLAG) {
            wv_detail.loadUrl(StringUtil.getImgeUrl(cs_cata_id));
        } else
            RequestManager.request(getActivity(), new CourseContentParams(cs_cata_id), CourseContentResponse.class,
                    this, Constant.URL);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            if (wv_detail != null) {
                wv_detail.onResume();
            }
            if (mCourseContentResponseBody != null) {
                ((CourseDetailActivity) getActivity()).showExtVideo(mCourseContentResponseBody.getExt_video_id(), mCourseContentResponseBody.getExt_video_title(), mCourseContentResponseBody.getExt_video_url());
            }
        } else {
            if (wv_detail != null) {
                wv_detail.onPause();
            }
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        // wv_detail.reload();
        super.onPause();
        wv_detail.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        wv_detail.onResume();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        wv_detail.removeAllViews();
        wv_detail.destroy();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResponse(BaseResponse response) {
        loadingDlg.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof CourseContentResponse) {
                CourseContentResponse mCourseContentResponse = (CourseContentResponse) response;
                mCourseContentResponseBody = mCourseContentResponse.getRepBody();
                if (mCourseContentResponseBody != null) {
                    if (!TextUtils.isEmpty(mCourseContentResponseBody.getFile_url_v())
                            || !TextUtils.isEmpty(mCourseContentResponseBody.getFile_url())) {
                        String url = StringUtil.getImgeUrl(!TextUtils.isEmpty(mCourseContentResponseBody.getFile_url_v())
                                ? mCourseContentResponseBody.getFile_url_v()
                                : mCourseContentResponseBody.getFile_url());
                        LogUtils.e("onResponse url:" + url);
                        wv_detail.loadUrl(url);
                        wordList = mCourseContentResponseBody.getWords();
                        big_pic_url = mCourseContentResponseBody.getBig_pic_url();
                        videoList = mCourseContentResponseBody.getVedios();
                        picList = mCourseContentResponseBody.getPic_urls();
                    } else {
                        addErrorView();
                    }
                }
            }

        } else {
            ToastUtil.show(response.getResMsg());// 开始学习、内容列表->请重新登录
        }
    }

    public void toPlayVideo() {
        if (mCourseContentResponseBody != null) {
            //友盟统计
            ArrayList<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_ONE));
            var.add("课程详情");
            var.add("视频拆讲");
            var.add(mCourseContentResponseBody.getExt_video_title());
            Intent intent = new Intent(getActivity(), MediaActivity.class);
            intent.putExtra("lable", mCourseContentResponseBody.getExt_video_title());
            intent.putStringArrayListExtra(MediaActivity.U_MENG_DATA, var);
            intent.putExtra(MediaActivity.URL,
                    StringUtil.getImgeUrl(mCourseContentResponseBody.getExt_video_url()));
            startActivity(intent);
        }
    }

    /**
     * 图片预览查看
     *
     * @param position
     * @param urls
     */
    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(getActivity(), SquareImageLookActivity.class);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        getActivity().startActivity(intent);
    }

}
