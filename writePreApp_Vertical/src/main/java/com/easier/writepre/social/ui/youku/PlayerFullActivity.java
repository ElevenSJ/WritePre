package com.easier.writepre.social.ui.youku;

import com.easier.writepre.R;
import com.easier.writepre.service.MusicService;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.ToastUtil;
import com.youku.player.base.YoukuBasePlayerManager;
import com.youku.player.base.YoukuPlayer;
import com.youku.player.base.YoukuPlayerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

/**
 * 全屏视频播放
 * 
 */
public class PlayerFullActivity extends BaseActivity {

	private String vid; // 需要播放的视频id

	private String id = "";

	private YoukuPlayerView mYoukuPlayerView; // 播放器控件

	private YoukuBasePlayerManager basePlayerManager;

	private YoukuPlayer youkuPlayer; // YoukuPlayer实例，进行视频播放控制

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.youku_player_full);

		basePlayerManager = new YoukuBasePlayerManager(this) {

			@Override
			public void setPadHorizontalLayout() {

			}

			@Override
			public void goSmall() {
				ToastUtil.show("只能全屏播放哦，亲!");
				return;
			}
			@Override
			public void onInitializationSuccess(YoukuPlayer player) {
				// 初始化成功后需要添加该行代码
				addPlugins();

				// 实例化YoukuPlayer实例
				youkuPlayer = player;

				// 进行播放
				goPlay();
			}

			@Override
			public void onSmallscreenListener() {
//				finish();
				
			}

			@Override
			public void onFullscreenListener() {

			}
		};
		basePlayerManager.onCreate();

		// 通过上个页面传递过来的Intent获取播放参数
		getIntentData(getIntent());

		if (TextUtils.isEmpty(id)) {
			vid = "XODQwMTY4NDg0"; // 默认视频
		} else {
			vid = id;
		}

		// 播放器控件
		mYoukuPlayerView = (YoukuPlayerView) this
				.findViewById(R.id.full_holder);
		// 控制竖屏和全屏时候的布局参数。这两句必填。
		mYoukuPlayerView
				.setSmallScreenLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT));
		mYoukuPlayerView
				.setFullScreenLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT));
		// 初始化播放器相关数据
		mYoukuPlayerView.initialize(basePlayerManager);
		basePlayerManager.setPlayerFullScreen(true);
		basePlayerManager.setOrientionDisable();
	}

	@Override
	public void onBackPressed() { // android系统调用
		super.onBackPressed();
		basePlayerManager.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//basePlayerManager.getMediaPlayerDelegate().isFullScreen = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		basePlayerManager.onDestroy();
		Intent i = new Intent();
		i.setAction(MusicService.MUSIC_PLAYER_ACTION_RESTART);
		sendBroadcast(i);
	}

	@Override
	public void onLowMemory() { // android系统调用
		super.onLowMemory();
		basePlayerManager.onLowMemory();
	}

	@Override
	protected void onPause() {
		super.onPause();
		basePlayerManager.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		basePlayerManager.onResume();
	}

	@Override
	public boolean onSearchRequested() { // android系统调用
		return basePlayerManager.onSearchRequested();
	}

	@Override
	protected void onStart() {
		super.onStart();
		basePlayerManager.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		basePlayerManager.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		// 通过Intent获取播放需要的相关参数
		getIntentData(intent);

		// 进行播放
		goPlay();
	}

	/**
	 * 获取上个页面传递过来的数据
	 */
	private void getIntentData(Intent intent) {
		if (intent != null) {
			id = intent.getStringExtra("vid");// 在线播放
		}

	}

	private void goPlay() {
		Intent i = new Intent();
		i.setAction(MusicService.MUSIC_PLAYER_ACTION_PAUSE);
		sendBroadcast(i);
		youkuPlayer.playVideo(vid); // 播放在线视频

		// XNzQ3NjcyNDc2
		// XNzQ3ODU5OTgw
		// XNzUyMzkxMjE2
		// XNzU5MjMxMjcy 加密视频
		// XNzYxNzQ1MDAw 万万没想到
		// XNzgyODExNDY4 魔女范冰冰扑倒黄晓明
		// XNDcwNjUxNzcy 姐姐立正向前走
		// XNDY4MzM2MDE2 向着炮火前进
		// XODA2OTkwMDU2 卧底韦恩突出现 劫持案愈发棘手
		// XODUwODM2NTI0 会员视频
		// XODQwMTY4NDg0 一个人的武林
	}
}
