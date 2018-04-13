package com.easier.writepre.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TimeGetParams;
import com.easier.writepre.response.TimeGetResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class CourseWebViewActivity extends BaseActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private String url;
    private String title;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursewebview_layout);
        id = getIntent().getStringExtra("xsf_cs_id");
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        initWebView();
        requestTimeGet(id);
    }

    private void initWebView() {
        setTopTitle(title);
        progressBar = (ProgressBar) findViewById(R.id.progress);
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
        webView.setWebChromeClient(new WebChromeClient());

        if (TextUtils.isEmpty(url)) {
            ToastUtil.show("链接地址为空");
            return;
        }
        webView.loadUrl(url);

    }

    public void onBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return false;
    }

    /**
     * 获取学时
     *
     * @param xsf_cs_id
     */
    private void requestTimeGet(String xsf_cs_id) {
        RequestManager.request(this, new TimeGetParams(xsf_cs_id), TimeGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
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
}
