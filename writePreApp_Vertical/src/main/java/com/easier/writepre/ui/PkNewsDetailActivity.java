package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.tencent.smtt.sdk.WebView;

public class PkNewsDetailActivity extends BaseActivity {
	private ImageView img_back;

	private WebView wv_detail;// WebView对象

	private ImageView img_detail;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_pk_news_detail);
		init();
	}

	private void init() {
		img_back = (ImageView) findViewById(R.id.img_back);
		img_detail = (ImageView) findViewById(R.id.img_detail);
		wv_detail = (WebView) findViewById(R.id.wv_detail);

		img_back.setOnClickListener(this);
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		BitmapHelp.getBitmapUtils().display(img_detail, StringUtil.getImgeUrl(url));
		wv_detail.loadUrl(url);
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
