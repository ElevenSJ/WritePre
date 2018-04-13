package com.easier.writepre.ui;

import com.easier.writepre.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class BannerLinkActivity extends BaseActivity {

    private boolean from;      //true 表示从投寄作品页面
    private WebView wv_detail;// WebView对象

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_banner_link);
        init();
    }

    @Override
    public void onTopLeftClick(View view) {
        setResult(RESULT_OK);
        super.onTopLeftClick(view);
    }

    private void init() {
        wv_detail = (WebView) findViewById(R.id.wv_detail);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        from = intent.getBooleanExtra("type", false);
//        setTopTitle(TextUtils.isEmpty(intent.getStringExtra("name")) ? getResources()
//                .getString(R.string.app_name) : intent.getStringExtra("name"));
        if (from) {
            setTopTitle("承诺书");
        } else setTopTitle("简介");

        WebSettings webSettings = wv_detail.getSettings();
        webSettings.setJavaScriptEnabled(true);// 支持js
        wv_detail.setWebViewClient(new webViewClient());
        wv_detail.loadUrl(url);
    }

    class webViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            // 如果不需要其他对点击链接事件的处理返回true，否则返回false
            return true;
        }
    }

}
