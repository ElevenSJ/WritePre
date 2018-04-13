package com.easier.writepre.ui;

import com.easier.writepre.R;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ImageView;

public class AboutActivity extends BaseActivity {

	private ImageView img_back;

	private WebView wv_detail;// WebView对象


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_about);
		init();
	}

	private void init() {
		setTopTitle("关于写字派");

		wv_detail = (WebView) findViewById(R.id.webView);


		WebSettings webSettings = wv_detail.getSettings();
		//支持缩放

		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		// 解决webview的适配屏幕问题
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		//支持js
		webSettings.setJavaScriptEnabled(true);

//		wv_detail.loadUrl("http://m.xiezipai.com/apphtml/index.html");
		wv_detail.loadUrl("file:///android_asset/about.html");

//		String version = "javascript:setVerson(' "+ 220 +" ')";
		wv_detail.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {

				view.loadUrl("javascript:setVerson(' "+getVersionName()+" ')");
			}

		});
	}

	private String getVersionName()
	{
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packInfo.versionName;
		return version;
	}
}

