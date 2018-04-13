package com.easier.writepre.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.volley.Response.Listener;
import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerViewAdapter;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.entity.WelcomeAdvInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.ChildViewManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.manager.ActStackManager;
import com.easier.writepre.manager.BindClientManager;
import com.easier.writepre.param.InstallCountParams;
import com.easier.writepre.param.LoginParams;
import com.easier.writepre.param.LoginParams.LoginType;
import com.easier.writepre.param.MiLoginParams;
import com.easier.writepre.param.SocialInfoGetParams;
import com.easier.writepre.param.VersionParams;
import com.easier.writepre.param.WelcomeAdvParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.LoginResponse;
import com.easier.writepre.response.SocialInfoGetResponse;
import com.easier.writepre.response.VersionResponse;
import com.easier.writepre.response.WelcomeAdvResponse;
import com.easier.writepre.rongyun.RongYunUtils;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.easier.writepre.widget.NoScrollViewPager;
import com.easier.writepre.widget.VersionHintDialog;
import com.easier.writepre.widget.VersionUpdateDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import io.rong.imkit.RongIM;

public class MainActivity extends NoSwipeBackActivity implements OnClickListener, Listener<BaseResponse> {

    private long backTime = 0;
    public static boolean isMyCourseUpdate = false;
    public static boolean isCourseUpdate = false;
    // 缓存我的课程id
    public static final Map<String, String> myCourse;

    static {
        myCourse = new HashMap<String, String>();
    }

    public static final String MAIN_TAB_INDEX = "main_index";

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THR = 2;
    public static final int TYPE_FOR = 3;
    public static final int TYPE_FIV = 4;
    public static final Map<Integer, String> content;

    static {
        content = new HashMap<Integer, String>();
        content.put(TYPE_ONE, "课程");
        content.put(TYPE_TWO, "社交");
        content.put(TYPE_THR, "学院");
        content.put(TYPE_FOR, "发现");
        content.put(TYPE_FIV, "我");
    }

    private NoScrollViewPager pager;
    private ViewPagerViewAdapter adapter;

    private ChildViewManager viewManager;

    private RadioButton tvOne, tvTwo, tvThree, tvFour,tvFive;

    private RadioGroup bottomLayout;

    public static int index = TYPE_TWO;

    private String url;

    private int newVersionCode = 0;
    private String newVersion = "";

    private VersionHintDialog dlg;

    private ArrayList<View> mContainers = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        index = getIntent().getIntExtra(MAIN_TAB_INDEX, index);
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(MAIN_TAB_INDEX, index);
            // 防止fragment没被回收
        }
        initView();
        checkLogin();
        getVersionUpdate();
        getStartUpAdv();
        ActStackManager.getInstance().popRemoveActivity();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MAIN_TAB_INDEX, index);
//		super.onSaveInstanceState(outState);
    }

    //会话页面跳转
    private void goToConversationActivity(Intent intent) {
        if (intent != null) {
            String id = intent.getStringExtra("rong_id");
            String title = intent.getStringExtra("rong_title");
            String type = intent.getStringExtra("rong_type");
            String tagName = intent.getStringExtra("tagName");
            if (TextUtils.equals("group", type)) {
                if (TextUtils.equals("WP:NtcNtf", tagName)) {
                    //圈子动态
                    Intent circleIntent = new Intent(this, CircleMsgListActivity.class);
                    circleIntent.putExtra("circle_id", id);
                    startActivity(circleIntent);
                } else {
                    //群组会话
                    RongYunUtils.getInstances().startGroupChat(this, id, title, "0");
                }
            } else if (TextUtils.equals("private", type)) {
                //私聊
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(
                            this,
                            id,
                            title);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent().getBooleanExtra("isFinish", false)) {
            finish();
        } else {
            index = getIntent().getIntExtra(MAIN_TAB_INDEX, index);
            setCurrentPage(index);
            viewManager.onNewIntent(index, intent);
            goToConversationActivity(getIntent());
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void checkLogin() {
        if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {

            // 从共享参数里拿出用户名和密码
            String userno = (String) SPUtils.instance().get(SPUtils.LOGIN_NAME, "");
            String pwd = (String) SPUtils.instance().get(SPUtils.LOGIN_PWD, "");

            if (!TextUtils.isEmpty(userno) && !TextUtils.isEmpty(pwd)) {// 判断用户名和密码都不为空才请求登录
                LoginParams params = new LoginParams(userno, pwd);
                RequestManager.request(this, params, LoginResponse.class, this, Constant.URL);
            } else {// 判断第三方的登录信息的userId和token都不为空才请求登录
                boolean canLogin = false;
                while (!canLogin) {
                    // QQ
                    Platform qq = ShareSDK.getPlatform(MainActivity.this, QQ.NAME);
                    String userId = qq.getDb().getUserId();
                    String token = qq.getDb().getToken();
                    String qqHead = qq.getDb().getUserIcon();
                    String qqName = qq.getDb().getUserName(); // 昵称
                    if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token)) {
                        String qqIconUrl = qqHead;
                        if (qqIconUrl != null && qqIconUrl.length() > 0) {
                            qqIconUrl = qqIconUrl.substring(0, qqIconUrl.lastIndexOf("/")) + "/100";
                        }
                        LoginParams params = new LoginParams(LoginType.QQ, userId, token, qqIconUrl, qqName);
                        RequestManager.request(this, params, LoginResponse.class, this, Constant.URL);
                        canLogin = true;
                        break;
                    }
                    // 新浪微博
                    Platform sina = ShareSDK.getPlatform(MainActivity.this, SinaWeibo.NAME);
                    String userId2 = sina.getDb().getUserId();
                    String token2 = sina.getDb().getToken();
                    if (!TextUtils.isEmpty(userId2) && !TextUtils.isEmpty(token2)) {
                        LoginParams params = new LoginParams(LoginType.WB, userId2, token2);
                        RequestManager.request(this, params, LoginResponse.class, this, Constant.URL);
                        canLogin = true;
                        break;
                    }
                    // 微信
                    Platform wechat = ShareSDK.getPlatform(MainActivity.this, Wechat.NAME);
                    String userId3 = wechat.getDb().getUserId();
                    String token3 = wechat.getDb().getToken();
                    if (!TextUtils.isEmpty(userId3) && !TextUtils.isEmpty(token3)) {
                        LoginParams params = new LoginParams(LoginType.WX, userId3, token3);
                        RequestManager.request(this, params, LoginResponse.class, this, Constant.URL);
                        canLogin = true;
                        break;
                    }
                    // 小米
                    String userId4 = (String) SPUtils.instance().get(SPUtils.MI_OPENID, "");
                    String token4 = (String) SPUtils.instance().get(SPUtils.MI_TOKEN, "");
                    if (!TextUtils.isEmpty(userId4) && !TextUtils.isEmpty(token4)) {
                        RequestManager.request(this, new MiLoginParams(token4, userId4),
                                LoginResponse.class, this, Constant.URL);
                        canLogin = true;
                        break;
                    }
                    break;
                }
//                if (!canLogin) {
//                    initData();
//                }
            }
        }
//        else {
//            initData();
//        }
    }

    /**
     * 获取版本升级
     */
    private void getVersionUpdate() {
        // 获取版本升级
        RequestManager.request(this, new VersionParams(), VersionResponse.class, this, Constant.URL);
        // 装机量
        if (!(boolean) SPUtils.instance().get(SPUtils.INSTALL_FLAG, false)) {
            RequestManager.request(this, new InstallCountParams(), BaseResponse.class, this, Constant.URL);
        }
    }

    /**
     * 获取启动动画
     */
    private void getStartUpAdv() {
        RequestManager.request(this, new WelcomeAdvParams(), WelcomeAdvResponse.class, this, Constant.URL);
    }

    private void initView() {
        pager = (NoScrollViewPager) findViewById(R.id.viewpager);

        bottomLayout = (RadioGroup) findViewById(R.id.layout_bottom);
        tvOne = (RadioButton) findViewById(R.id.tv_one);
        tvTwo = (RadioButton) findViewById(R.id.tv_two);
        tvThree = (RadioButton) findViewById(R.id.tv_three);
        tvFour = (RadioButton) findViewById(R.id.tv_four);
        tvFive = (RadioButton) findViewById(R.id.tv_five);

        viewManager = new ChildViewManager(this, content);

        for (int i = 0; i < content.size(); i++) {
            mContainers.add(viewManager.getView(i));
        }
        adapter = new ViewPagerViewAdapter(mContainers);
//        pager.setOffscreenPageLimit(mContainers.size()-1);
        pager.setAdapter(adapter);

        bottomLayout.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = Integer.parseInt(findViewById(checkedId).getTag().toString());
                //友盟结构化事件统计
                if (position != index) {
                    umeng();
                }
                index = position;
                pager.setCurrentItem(position, true);
                viewManager.showView(position,getIntent());
            }
        });

        dlg = new VersionHintDialog(this);
        dlg.setOnClickListener(this);
        setCurrentPage(index);
        goToConversationActivity(getIntent());
    }

    /**
     * 友盟统计
     */
    private void umeng() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(index));
        YouMengType.onEvent(MainActivity.this, var, getShowTime(), YouMengType.getName(index));
    }

    public void setCurrentPage(int position) {
        switch (position) {
            case TYPE_ONE:
                tvOne.setChecked(true);
                break;
            case TYPE_TWO:
                tvTwo.setChecked(true);
                break;
            case TYPE_THR:
                tvThree.setChecked(true);
                break;
            case TYPE_FOR:
                tvFour.setChecked(true);
                break;
            case TYPE_FIV:
                tvFive.setChecked(true);
                break;
        }
    }


    // private void downloadApk(String url) {
    // final String apkPath = FileUtils.SD_APP_PATH + newVersionCode + "_" +
    // FileUtils.SYSTEM_VERSION_NAME;
    // if (new File(apkPath).exists()) {
    // installAPk(apkPath);
    // return;
    // }
    // final String tmpfileName = System.currentTimeMillis() + ".tmp";
    // dlgLoad.setDialogCancelable(false);
    // new HttpUtils().download(url, FileUtils.SD_APP_PATH + tmpfileName, true,
    // true, new RequestCallBack<File>() {
    //
    // private boolean isSuccess = false;
    //
    // @Override
    // public void onLoading(long total, long current, boolean isUploading) {
    // if (!isUploading) {
    // dlgLoad.loading("正在下载：" + FileUtils.getFormatSize(current) + "/" +
    // FileUtils.getFormatSize(total));
    // if (current == total) {
    // isSuccess = true;
    // }
    // }
    // }
    //
    // @Override
    // public void onFailure(HttpException error, String msg) {
    // FileUtils.deleteFile(FileUtils.SD_APP_PATH + tmpfileName);
    // dlgLoad.dismissDialog();
    // ToastUtil.show("下载失败，请重试！");
    // }
    //
    // @Override
    // public void onSuccess(ResponseInfo<File> responseInfo) {
    // dlgLoad.dismissDialog();
    // new File(FileUtils.SD_APP_PATH + tmpfileName).renameTo(new
    // File(apkPath));
    // installAPk(apkPath);
    // }
    // });
    //
    // }

    protected void installAPk(String path) {
        FileUtils.installApk(this, path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (url.endsWith(".apk")) {
                    FileUtils.downloadApk(MainActivity.this, "", url, FileUtils.SYSTEM_VERSION_NAME + "-" + newVersionCode);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            case R.id.btn_cancel:
                dlg.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backTime == 0) {
                backTime = event.getDownTime();
                ToastUtil.show("请再按一次退出!");
            } else {
                if (event.getDownTime() - backTime <= 2000) {
                    finish();
                    ActStackManager.getInstance().allFinish();
                } else {
                    ToastUtil.show("请再按一次退出!");
                    backTime = event.getDownTime();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResponse(BaseResponse response) {
        if (isDestroyed) {
            return;
        }
        if (response == null) {
            return;
        }
        if (response instanceof WelcomeAdvResponse) {
            if (response.getResCode().equals("0")) {
                WelcomeAdvResponse sigrResult = (WelcomeAdvResponse) response;
                if (sigrResult != null) {
                    WelcomeAdvInfo rBody = sigrResult.getRepBody();
                    LogUtils.e("WELCOME_ADV:" + new Gson().toJson(rBody));
                    SPUtils.instance().put(SPUtils.WELCOME_ADV, new Gson().toJson(rBody));
                }
            }
        } else if (response instanceof VersionResponse) {
            if ("0".equals(response.getResCode())) {
                VersionResponse resp = (VersionResponse) response;
                if (resp != null && resp.getRepBody() != null) {
                    if (Utils.getCurrentCode(this) < resp.getRepBody().getLevel()) {
                        VersionUpdateDialog dialog = new VersionUpdateDialog(MainActivity.this, R.style.loading_dialog,
                                resp.getRepBody());
                        if (!isDestroyed) {
                            dialog.show();
                        }
                    }
                }
            } else {
                ToastUtil.show(response.getResMsg());
            }
        } else if (response instanceof LoginResponse) {
            if (!"0".equals(response.getResCode())) {
                ToastUtil.show(response.getResMsg());
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 1);
                SPUtils.instance().unLogin();
            } else {
                LoginResponse mLoginResponse = (LoginResponse) response;
                SPUtils.instance().put(SPUtils.IS_LOGIN, true);
                SPUtils.instance().put(SPUtils.LOGIN_DATA, new Gson().toJson(mLoginResponse.getRepBody()));
            }
        } else {
            if ("0".equals(response.getResCode())) {
                SPUtils.instance().put(SPUtils.INSTALL_FLAG, true);
            } else {
                ToastUtil.show(response.getResMsg());
            }
        }
    }

    @Override
    protected void onStop() {
        viewManager.onStop(index);
        super.onStop();
    }

    @Override
    protected void onResume() {
        viewManager.onResume(index);
        super.onResume();
    }

    @Override
    protected void onPause() {
        viewManager.onPause(index);
        umeng();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewManager.destoryAllView();
        index = 0;
        ActStackManager.getInstance().popRemoveAllActivity();
    }

    public void updateNotice(int num) {
        if (num == 0) {
            findViewById(R.id.notice).setVisibility(View.GONE);
        } else {
            findViewById(R.id.notice).setVisibility(View.VISIBLE);
        }
    }
}
