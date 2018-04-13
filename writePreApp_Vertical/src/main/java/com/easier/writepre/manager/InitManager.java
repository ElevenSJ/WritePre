package com.easier.writepre.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.volley.toolbox.Volley;
import com.easier.writepre.BuildConfig;
import com.easier.writepre.R;
import com.easier.writepre.app.RongCloudEvent;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.http.Constant;
import com.easier.writepre.rongyun.WPNoticeMessage;
import com.easier.writepre.rongyun.WPNoticeMessageItemProvider;
import com.easier.writepre.rongyun.WPShortVideoMessage;
import com.easier.writepre.rongyun.WPShortVideoMessageItemProvider;
import com.easier.writepre.social.ui.youku.CachedActivity;
import com.easier.writepre.social.ui.youku.CachingActivity;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.easier.writepre.widget.NineGridView;
import com.easier.writepre.widget.NineGridViewWrapper;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.youku.player.YoukuPlayerBaseConfiguration;

import cn.sharesdk.framework.ShareSDK;
import io.rong.imkit.RongIM;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.ipc.RongExceptionHandler;

public class InitManager {

    private static InitManager instance;

    private Context ctx;

    public synchronized static InitManager getInstance() {
        if (instance == null) {
            instance = new InitManager();
        }
        return instance;
    }

    public void init(Context mCtx) {
        this.ctx = mCtx.getApplicationContext();
        // 初始化屏幕宽高
        WritePreApp.displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(WritePreApp.displayMetrics);
        Fresco.initialize(ctx);
        // 本地持久化
        SPUtils.instance().init(ctx);
        LogUtils.setDebug((boolean) SPUtils.instance().get("log_debug", BuildConfig.LOG_DEBUG));

        initThirdService();
    }

    public void initThirdService() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
        //设置线程优先级，不抢占主线程资源
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        // 初始化volley
        WritePreApp.mVolleyQueue = Volley.newRequestQueue(ctx);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //x5内核初始化接口
        QbSdk.initX5Environment(ctx, new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.d("QbSdk onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        });
        // 初始化文件目录
        if (!FileUtils.init()) {
            ToastUtil.show(ctx.getResources().getString(R.string.init_sd_error));
        }
        Bugly.init(ctx, "1104291495", BuildConfig.LOG_DEBUG);
//        CrashReport
//                .initCrashReport(ctx, "1104291495", BuildConfig.LOG_DEBUG); // 测试true
        // 初始化网络图片加载工具
        initBitmapConfig();
        // 分享插件初始化
        ShareSDK.initSDK(ctx);
        // 初始化meta值
        initMetaData();
        // 数据库初始化
        DBHelper.instance().open(ctx, "writepre.db");
        NineGridView.setImageLoader(new FrescoImageLoader());
//            }
//        }.start();
    }

    // 初始化优酷配置
    public void initYouKu() {
        WritePreApp.configuration = new YoukuPlayerBaseConfiguration(ctx) {
            /**
             * 通过覆写该方法，返回“正在缓存视频信息的界面”， 则在状态栏点击下载信息时可以自动跳转到所设定的界面. 用户需要定义自己的缓存界面
             */
            @Override
            public Class<? extends Activity> getCachingActivityClass() {
                return CachingActivity.class;
            }

            /**
             * 通过覆写该方法，返回“已经缓存视频信息的界面”， 则在状态栏点击下载信息时可以自动跳转到所设定的界面.
             * 用户需要定义自己的已缓存界面
             */
            @Override
            public Class<? extends Activity> getCachedActivityClass() {
                return CachedActivity.class;
            }

            /**
             * 配置视频的缓存路径，格式举例： /appname/videocache/ 如果返回空，则视频默认缓存路径为：
             * /应用程序包名/videocache/
             */
            @Override
            public String configDownloadPath() {
                // return "/myapp/videocache/"; //举例
                return null;
            }
        };
    }

    /**
     * 初始化Meta数据
     */
    private void initMetaData() {
        PackageManager packageManager = ctx.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(
                    ctx.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                WritePreApp.geTuiAppId = (String) applicationInfo.metaData
                        .get("PUSH_APPID");
                WritePreApp.channelId = applicationInfo.metaData
                        .get("UMENG_CHANNEL").toString();
            }
        } catch (Exception e) {
            LogUtils.e(WritePreApp.class,
                    "you must set PUSH_APPID and UMENG_CHANNEL in your dimen file.");
            throw new RuntimeException(
                    "you must set PUSH_APPID and UMENG_CHANNEL in your dimen file.",
                    e);
        }
    }

    private void initBitmapConfig() {
        if (WritePreApp.bitmapUtils == null) {
            WritePreApp.bitmapUtils = new BitmapUtils(ctx, FileUtils.SD_IMAGES_CACHE);
            WritePreApp.bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
            WritePreApp.bitmapUtils.configMemoryCacheEnabled(true);
            WritePreApp.bitmapUtils.configThreadPoolSize(100);
            WritePreApp.bitmapUtils.configDiskCacheEnabled(true);
            WritePreApp.bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils
                    .getScreenSize(ctx));
            WritePreApp.bitmapUtils.configDefaultShowOriginal(false);
        }
    }

    /**
     * 融云初始化
     */
    public void initRongYun() {
        /**
         *
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (ctx.getApplicationInfo().packageName.equals(Utils.getCurProcessName(ctx)) ||
                "io.rong.push".equals(Utils.getCurProcessName(ctx))) {
            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(ctx, Constant.CURRENT_RONGIMAPPKEY);
            try {
                // 注册一个自定义消息类型-公告。
                RongIMClient.registerMessageType(WPNoticeMessage.class);
                // 注册一个自定义消息模板-公告
                RongIM.getInstance().registerMessageTemplate(new WPNoticeMessageItemProvider());
                // 注册一个自定义消息类型-视频。
                RongIMClient.registerMessageType(WPShortVideoMessage.class);
                // 注册一个自定义消息模板-视频
                RongIM.getInstance().registerMessageTemplate(new WPShortVideoMessageItemProvider());
            } catch (AnnotationNotFoundException e) {
                e.printStackTrace();
            }
            /**
             * c 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if (ctx.getApplicationInfo().packageName
                    .equals(Utils.getCurProcessName(ctx))) {
                RongCloudEvent.init(ctx);
                Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(
                        ctx));
            }

        }
    }

    /**
     * Fresco 加载
     */
    private class FrescoImageLoader implements NineGridView.ImageLoader<String> {

        @Override
        public void onDisplayImage(Context context, NineGridViewWrapper imageView, String data) {
            imageView.setImageURI(data);
        }
    }
}
