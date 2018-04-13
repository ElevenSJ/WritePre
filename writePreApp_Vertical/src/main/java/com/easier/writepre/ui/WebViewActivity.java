package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.ToastUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ProgressBar;

public class WebViewActivity extends BaseActivity {
	
	private WebView webView;
	private ProgressBar progressBar;
	private String url;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_layout);
		url = getIntent().getStringExtra("url");
		LogUtils.e("加载页面地址:"+url);
		title = getIntent().getStringExtra("title");
		initWebView();
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

	public void onBack(){
		if (webView.canGoBack()) {
			webView.goBack();
		}else{
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
