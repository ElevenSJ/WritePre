package com.easier.writepre.utils;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

import android.graphics.Bitmap;

public class BitmapHelp {
	private BitmapHelp() {
	}
	public static BitmapUtils getBitmapUtils() {
		if (WritePreApp.bitmapUtils == null) {
			WritePreApp.bitmapUtils = new BitmapUtils(WritePreApp.getApp().getApplication(),FileUtils.SD_IMAGES_CACHE,Runtime.getRuntime().maxMemory()/6);
			WritePreApp.bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
			WritePreApp.bitmapUtils.configMemoryCacheEnabled(true);
			WritePreApp.bitmapUtils.configDefaultAutoRotation(true);
			WritePreApp.bitmapUtils.configDiskCacheEnabled(true);
			WritePreApp.bitmapUtils.configDefaultShowOriginal(false);
			WritePreApp.bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils
					.getScreenSize(WritePreApp.getApp().getApplication()));
			WritePreApp.bitmapUtils.configDefaultLoadingImage(R.drawable.translucent);
		}
		return WritePreApp.bitmapUtils;
	}
	
}
