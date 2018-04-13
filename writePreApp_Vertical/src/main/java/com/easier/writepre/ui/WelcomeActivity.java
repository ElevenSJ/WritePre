package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.AppPathInfo;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.entity.WelcomeAdvInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.manager.BindRongYunTokenManager;
import com.easier.writepre.manager.InitManager;
import com.easier.writepre.param.SocialInfoGetParams;
import com.easier.writepre.response.AppPathResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SocialInfoGetResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;

import java.util.List;

/**
 * @author 孙杰
 */
public class WelcomeActivity extends NoSwipeBackActivity {

    //暂停推广
//    public static final Map<String, Integer> welcomeResMap;
//
//    static {
//        welcomeResMap = new HashMap<String, Integer>();
//        welcomeResMap.put("all", R.drawable.welcome_default);
//        welcomeResMap.put("xiezipai", R.drawable.welcome_default);
//        welcomeResMap.put("360", R.drawable.welcome_default);
//        welcomeResMap.put("qq", R.drawable.welcome_default);
//        welcomeResMap.put("huawei", R.drawable.welcome_default);
//        welcomeResMap.put("xiaomi", R.drawable.welcome_default);
//        welcomeResMap.put("wandoujia", R.drawable.welcome_default);
//        welcomeResMap.put("baidu", R.drawable.welcome_default);
//    }

    private TextView timeTxt;

    private SimpleDraweeView advImg;

    private int millis = 5;
    private final String APP_PATH_KEY = "app_path_key";
    private boolean needLoadPath = false;
    private Animation alphaAnimation;

    private List<AppPathInfo> list;
    private int index = 0;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (millis < 1) {
                        if (!needLoadPath) {
                            toMainActivity();
                        }
                    } else {
                        if (timeTxt.getVisibility()!=View.VISIBLE) {
                            timeTxt.setVisibility(View.VISIBLE);
                        }
                        timeTxt.setText("点击跳过(" + millis + "S)");
                        millis--;
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                case 0:
                    if (!needLoadPath) {
                        toMainActivity();
                    }
                    break;
                case 100://判断path
                    break;
            }
        }

    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_view);
        initView();
    }


    private void initView() {
        timeTxt = (TextView) findViewById(R.id.time_txt);
        timeTxt.setOnClickListener(this);

//        welcomeDefault = (ImageView) findViewById(R.id.welcome_default);
        advImg = (SimpleDraweeView) findViewById(R.id.welcome_img);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        advImg.setOnClickListener(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final long time = System.currentTimeMillis();
//                if (!WritePreApp.Inited){
//                    LogUtils.e("开始初始化");
//                    InitManager.getInstance().initThirdService();
//                    WritePreApp.Inited = true;
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtils.e("初始化时长："+(System.currentTimeMillis()-time));
                        PushManager.getInstance().initialize(WelcomeActivity.this.getApplicationContext());
                        InitManager.getInstance().initRongYun();
                        InitManager.getInstance().initYouKu();
                        BindRongYunTokenManager.getInstance(WelcomeActivity.this).getToken();
                        initData();
                        disPlayAdv();
//                    }
//                });
//            }
//        }).start();

//        int item = welcomeResMap.get(WritePreApp.channelId) == null ? R.drawable.welcome_default
//                : welcomeResMap.get(WritePreApp.channelId);
//        welcomeBitmap = Bimp.getBitmapDrawableFromStream(item, WelcomeActivity.this, 1);
//        welcomeDefault.setImageBitmap(welcomeBitmap);
//        welcomeDefault.startAnimation(alphaAnimation);
    }

    private void initData() {
        // dlgLoad.loading();
        LogUtils.e("获取配置参数");
        // 获取配置参数
        RequestManager.request(this, new SocialInfoGetParams(), SocialInfoGetResponse.class, this, Constant.URL);
    }

    private void disPlayAdv() {
        WelcomeAdvInfo info = SPUtils.instance().getWelcomeAdvInfo();
        if (!TextUtils.isEmpty(info.getLink_url())) {
            advImg.setClickable(true);
        }
        if (!TextUtils.isEmpty(info.getUrl())) {
            advImg.setImageURI(info.getUrl());
            handler.sendEmptyMessage(1);
        } else {
//            timeTxt.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(0, 1500);
        }
    }

    /**
     * 进入登录页面
     */
    protected void toMainActivity() {
        handler.removeMessages(1);
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
        startActivity(loginIntent);
        finish();
    }

    /**
     * 进入web页面
     */
    protected void toWebActivity() {
        WelcomeAdvInfo info = SPUtils.instance().getWelcomeAdvInfo();
        if (!TextUtils.isEmpty(info.getLink_url())) {
            handler.removeMessages(1);
            Intent itIn = new Intent(this, BannerLinkActivity.class);
            itIn.putExtra("url", info.getLink_url());
            startActivityForResult(itIn, 100);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_txt:
                toMainActivity();
                break;
            case R.id.welcome_img:
                toWebActivity();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            toMainActivity();
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof AppPathResponse) {
                AppPathResponse.AppPathBody body = ((AppPathResponse) response).getRepBody();
                if (body != null) {
                    if (body.getList() != null && body.getList().size() != 0) {
                        list = body.getList();
                        handler.sendEmptyMessage(100);
                    }
                }
            } else if (response instanceof SocialInfoGetResponse) {
                SocialInfoGetResponse sigrResult = (SocialInfoGetResponse) response;
                if (sigrResult != null) {
                    SocialPropEntity rBody = sigrResult.getRepBody();
                    SPUtils.instance().put(SPUtils.SOCIAL_PROP_DATA, new Gson().toJson(rBody));
                }
            }
        }
    }

}
