package com.easier.writepre.service;

import com.easier.writepre.R;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Administrator
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

	MediaPlayer player;

	private final IBinder binder = new MusicBinder();

	public final static String MUSIC_PLAYER_ACTION_START = "com.easier.writepre.musci_start";
	public final static String MUSIC_PLAYER_ACTION_STOP = "com.easier.writepre.musci_stop";
	public final static String MUSIC_PLAYER_ACTION_BEGIN = "com.easier.writepre.musci_begin";
	public final static String MUSIC_PLAYER_ACTION_PAUSE = "com.easier.writepre.musci_pause";
	public final static String MUSIC_PLAYER_ACTION_RESTART = "com.easier.writepre.musci_restart";

	private MyBroadcastReceiver myBroadcastReceiver;
	private ScreenBroadcastReceiver mScreenReceiver;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (!Utils.isActivityRunnig(MusicService.this, MusicService.this.getPackageName(),0)) {
				if (player != null && player.isPlaying()) {
					player.pause();
				}
			} else {
				if (isPlayerAuto()) {
					playerStart();
				}
			}
			mHandler.sendEmptyMessageDelayed(0, 1000 * 2);
		}
	};

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (MUSIC_PLAYER_ACTION_START.equals(action)) {
				playerStart();
			} else if (MUSIC_PLAYER_ACTION_STOP.equals(action)) {
				playerPause();
			} else if (MUSIC_PLAYER_ACTION_BEGIN.equals(action)) {
				mHandler.sendEmptyMessage(0);
			} else if (MUSIC_PLAYER_ACTION_PAUSE.equals(action)) {
				mHandler.removeMessages(0);
				if (player != null && player.isPlaying()) {
					player.pause();
				}
			} else if (MUSIC_PLAYER_ACTION_RESTART.equals(action)) {
				mHandler.sendEmptyMessage(0);
			}

		}
	};

	/**
	 * screen状态广播接收者
	 */
	private class ScreenBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogUtils.e("Screen action:" + action);
			if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
				mHandler.removeMessages(0);
				if (player != null && player.isPlaying()) {
					player.pause();
				}
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
				mHandler.sendEmptyMessageDelayed(0, 1000 * 2);
			}
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

	public void playerStart() {
		setPlay(true);
		if (player != null && !player.isPlaying()) {
			player.start();
		}
	}

	public void playerPause() {
		setPlay(false);
		if (player != null && player.isPlaying()) {
			player.pause();
		}
	}

	@Override
	public void onCompletion(MediaPlayer player) {
	}

	public void onCreate() {
		super.onCreate();
		registerReceiver();
		registerScreenListener();
//		player = MediaPlayer.create(this, R.raw.bg_music);
		player.setOnCompletionListener(this);
		player.setVolume(0.8f, 0.8f);
		player.setLooping(true);

		LogUtils.e("MusicService--onCreate");
	}

	private void registerReceiver() {
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MUSIC_PLAYER_ACTION_START);
		filter.addAction(MUSIC_PLAYER_ACTION_STOP);
		filter.addAction(MUSIC_PLAYER_ACTION_BEGIN);
		filter.addAction(MUSIC_PLAYER_ACTION_PAUSE);
		filter.addAction(MUSIC_PLAYER_ACTION_RESTART);
		registerReceiver(myBroadcastReceiver, filter);
	}

	/**
	 * 启动screen状态广播接收器
	 */
	private void registerScreenListener() {
		mScreenReceiver = new ScreenBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(mScreenReceiver, filter);
	}

	private void unRegisterReceiver() {
		unregisterReceiver(myBroadcastReceiver);
	}

	private void unScreenRegisterReceiver() {
		unregisterReceiver(mScreenReceiver);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.e("MusicService--onStartCommand");
		mHandler.sendEmptyMessage(0);
		return START_STICKY;
	}

	public void onDestroy() {
		LogUtils.e("MusicService--onDestroy");
		mHandler.removeMessages(0);
		if (player != null && player.isPlaying()) {
			player.stop();
		}
		player.release();
		unRegisterReceiver();
		unScreenRegisterReceiver();
		super.onDestroy();
	}

	class MusicBinder extends Binder {

		MusicService getService() {
			return MusicService.this;
		}
	}

	public boolean isPlayerAuto() {
		return (Boolean) SPUtils.instance().get(SPUtils.BG_MUSIC_DEFUALT_OPEN, false);
	}

	public void setPlay(boolean isPlay) {
		SPUtils.instance().put(SPUtils.BG_MUSIC_DEFUALT_OPEN, isPlay);
	}

}
