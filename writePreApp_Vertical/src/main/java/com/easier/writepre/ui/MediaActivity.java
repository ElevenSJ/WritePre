package com.easier.writepre.ui;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.VideoView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.utils.NetWorkUtils;
import com.easier.writepre.utils.ToastUtil;

import java.util.ArrayList;

public class MediaActivity extends NoSwipeBackActivity
		implements OnPreparedListener, OnCompletionListener, OnClickListener,MediaPlayer.OnErrorListener{
	public static final String  U_MENG_DATA = "umengData";

	private ArrayList<String> uMengData;
	private VideoView vv;

	public static final String URL = "url";
	private int per = 0;
	
	private boolean isStart = true;

	private String lable = "";
	private String url;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_media);
		url = getIntent().getStringExtra(URL);
		uMengData = getIntent().getStringArrayListExtra(U_MENG_DATA);
		lable = getIntent().getStringExtra("lable");
		init();
	}

	public void init() {
		if (!NetWorkUtils.isNetworkConnected()) {
			ToastUtil.show("网络异常，请设置网络");
			finish();
			return;
		}
		vv = (VideoView) findViewById(R.id.vv_play);
		findViewById(R.id.iv_close).setOnClickListener(this);
		MediaController ctrl = new MediaController(this);
		vv.setMediaController(ctrl);
		vv.getHolder().setFixedSize(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getHeight(1f));
		vv.setOnPreparedListener(this);

		dlgLoad.loading();
		if (url.startsWith("http")){
			String currentUrl = WritePreApp.getProxy(this).getProxyUrl(url);
			vv.setVideoPath(currentUrl);
		}else{
			vv.setVideoPath(url);
		}
		vv.setMediaController(ctrl);
		vv.setOnCompletionListener(this);
		vv.setOnErrorListener(this);
		vv.requestFocus();
		vv.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (vv != null) {
			vv.seekTo(per);
			if (isStart) {
				vv.start();
			}else{
				vv.pause();
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (vv != null) {
			if (vv.isPlaying()) {
				isStart = true;
			}else{
				isStart = false;
			}
			vv.pause();
			per = vv.getCurrentPosition();
		}
		if (uMengData!=null&&!uMengData.isEmpty()){
			YouMengType.onEvent(this,uMengData,getShowTime(), TextUtils.isEmpty(lable)?uMengData.get(uMengData.size()-1):lable);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		dlgLoad.dismissDialog();
		isStart = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (vv != null) {
			vv.pause();
		}
		if (WritePreApp.getProxy(this) != null) {
			WritePreApp.getProxy(this).shutdown();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		vv.start();
	}

	@Override
	public void onClick(View arg0) {
		setResult(RESULT_OK);
		super.onTopLeftClick(arg0);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!url.startsWith("http")){
			ToastUtil.show("播放异常");
			dlgLoad.dismissDialog();
			finish();
		}
		return false;
	}
}
