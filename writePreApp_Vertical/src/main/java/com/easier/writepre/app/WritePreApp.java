package com.easier.writepre.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.danikula.videocache.HttpProxyCacheServer;
import com.easier.writepre.BuildConfig;
import com.easier.writepre.R;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.manager.InitManager;
import com.easier.writepre.param.RongYunTokenParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.RongYunTokenResponse;
import com.easier.writepre.rongyun.WPNoticeMessage;
import com.easier.writepre.rongyun.WPNoticeMessageItemProvider;
import com.easier.writepre.rongyun.WPShortVideoMessage;
import com.easier.writepre.rongyun.WPShortVideoMessageItemProvider;
import com.easier.writepre.social.ui.youku.CachedActivity;
import com.easier.writepre.social.ui.youku.CachingActivity;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.youku.player.YoukuPlayerBaseConfiguration;

import java.io.File;

import cn.sharesdk.framework.ShareSDK;
import io.rong.imkit.RongIM;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.ipc.RongExceptionHandler;

public class WritePreApp extends DefaultApplicationLike {

    private static WritePreApp app;
    // http请求
    public static RequestQueue mVolleyQueue;
    // 网络图片显示管理类
    public static BitmapUtils bitmapUtils;
    // 个推clientid
    public static String clientId;
    // 个推appid
    public static String geTuiAppId;
    // 渠道id
    public static String channelId;
    // 优酷初始化
    public static YoukuPlayerBaseConfiguration configuration;

    public static DisplayMetrics displayMetrics;

    public static String CITY;

    private HttpProxyCacheServer proxy;

    public static final String TAG = "Tinker.WritePreApp";

    public WritePreApp(Application application, int tinkerFlags,
                                 boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                                 long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        app = this;
        InitManager.getInstance().init(getApplication());
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

    /**
     * 视频播放缓存
     *
     * @param context
     * @return
     */
    public static HttpProxyCacheServer getProxy(Context context) {
        return getApp().proxy == null ? (getApp().proxy = getApp().newProxy()) : getApp().proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(getApplication())
                .maxCacheSize(1024 * 1024 * 1024).cacheDirectory(new File(FileUtils.SD_VIDEO_CACHE))
                .build();
    }



    public void clearBitmapCache() {
        if (bitmapUtils != null) {
            bitmapUtils.clearCache();
        }
    }

    public int getWidth(float scale) {
        if (scale > 1) {
            return displayMetrics.widthPixels;
        }
        return (int) (displayMetrics.widthPixels * scale);
    }

    public int getHeight(float scale) {
        if (scale > 1) {
            return displayMetrics.heightPixels;
        }
        return (int) (displayMetrics.heightPixels * scale);
    }

    public static WritePreApp getApp() {
        return app;
    }

    public <T> void addRequest(Request<T> request) {
        mVolleyQueue.add(request);
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
        mVolleyQueue.cancelAll(getApplication());
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }


}
