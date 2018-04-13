package com.easier.writepre.ui;

import com.easier.writepre.R;
import android.os.Bundle;
import android.view.View;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * 用户协议
 * 
 * @author zhoulu
 * 
 * 
 */
public class AgreementActivity extends BaseActivity {

	private WebView wv_detail;// WebView对象

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_webview_layout);
		init();
	}

	private void init() {
		setTopTitle("用户协议");
		findViewById(R.id.progress).setVisibility(View.INVISIBLE);
		findViewById(R.id.img_back).setOnClickListener(this);
		wv_detail = (WebView) findViewById(R.id.webView);

		WebSettings webSettings = wv_detail.getSettings();
		// 支持缩放

		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		// 解决webview的适配屏幕问题
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		wv_detail.loadUrl("file:///android_asset/register.htm");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			onTopLeftClick(v);
			break;
		default:
			break;
		}
	}
}
