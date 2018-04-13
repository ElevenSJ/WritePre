package com.easier.writepre.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.entity.XiaoMiOpenIdInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.manager.BindClientManager;
import com.easier.writepre.manager.BindRongYunTokenManager;
import com.easier.writepre.param.LoginParams;
import com.easier.writepre.param.LoginParams.LoginType;
import com.easier.writepre.param.MiLoginParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.LoginResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.google.gson.Gson;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;

import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends BaseActivity {

    public final String FLAG_ISlOGIN = "flag_islogin";

    private EditText etUserno;

    private EditText etPwd;
    private String token;

    private int[] ScopeFromUi = {XiaomiOAuthConstants.SCOPE_PROFILE, XiaomiOAuthConstants.SCOPE_OPEN_ID};
    private AsyncTask waitResultTask;
    private XiaomiOAuthResults miOAuthResults;
    private XiaoMiOpenIdInfo openInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        init();
    }

    private void init() {
        setTopTitle("用户登录");
        etUserno = (EditText) findViewById(R.id.et_login_email);
        etPwd = (EditText) findViewById(R.id.et_login_pwd);

//        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.tv_forget).setOnClickListener(this);
        findViewById(R.id.tv_regist).setOnClickListener(this);
        findViewById(R.id.btn_login_submit).setOnClickListener(this);
        findViewById(R.id.btn_qq).setOnClickListener(this);
        findViewById(R.id.btn_wb).setOnClickListener(this);
        findViewById(R.id.btn_wx).setOnClickListener(this);
        findViewById(R.id.btn_xiaomi).setOnClickListener(this);

        etUserno.setText((String) SPUtils.instance()
                .get(SPUtils.LOGIN_NAME, ""));
        etPwd.setText((String) SPUtils.instance().get(SPUtils.LOGIN_PWD, ""));
        etUserno.setSelection(etUserno.getText().toString().length());
    }


    @Override
    public void onTopLeftClick(View view) {
        this.finish();
        overridePendingTransition(0, R.anim.out_topbottom);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, R.anim.out_topbottom);
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        etUserno.setText((String) SPUtils.instance()
                .get(SPUtils.LOGIN_NAME, ""));
        etPwd.setText((String) SPUtils.instance().get(SPUtils.LOGIN_PWD, ""));
        etUserno.setSelection(etUserno.getText().toString().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forget:
                Intent intent = new Intent(LoginActivity.this,
                        RegistOrResetActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.tv_regist:
                Intent intent2 = new Intent(LoginActivity.this,
                        RegistOrResetActivity.class);
                startActivity(intent2);
                break;
            case R.id.img_back:
                onTopLeftClick(v);
                break;
            case R.id.btn_login_submit:
                submitCheck();
                break;
            case R.id.btn_qq:
                dlgLoad.loading();
                Platform qq = ShareSDK.getPlatform(LoginActivity.this, QQ.NAME);
                qq.setPlatformActionListener(listener);
                qq.authorize();
                break;
            case R.id.btn_wb:
                dlgLoad.loading();
                Platform sina = ShareSDK.getPlatform(LoginActivity.this,
                        SinaWeibo.NAME);
                sina.setPlatformActionListener(listener);
                sina.authorize();
                break;
            case R.id.btn_wx:
                Platform wechat = ShareSDK.getPlatform(LoginActivity.this,
                        Wechat.NAME);
                if (wechat.isClientValid()) {
                    dlgLoad.loading();
                    wechat.setPlatformActionListener(listener);
                    wechat.authorize();
                } else {
                    ToastUtil.show("未检测到微信客户端，请安装后再使用微信登录。");
                }
                break;
            case R.id.btn_xiaomi:
                getXiaoMiToken();
                break;
        }
    }

    /**
     * 获取小米授权token
     */
    private void getXiaoMiToken() {
        final XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                .setAppId(Constant.XIAOMI_APPID)
                .setRedirectUrl(Constant.XIAOMI_REDIRECTURI)
                .setScope(ScopeFromUi)
                .setKeepCookies(false) // 不调的话默认是false
                .setNoMiui(false) // 不调的话默认是false
                .setSkipConfirm(false) // 不调的话默认是false
                .startGetAccessToken(LoginActivity.this);
        waitXiaoMiOAuthResult(future);
    }

    /**
     * 小米账号对接服务器接口
     */
    private void doXiaoMiLogin(String openId) {
        dlgLoad.dismissDialog();
        openInfo = new Gson().fromJson(openId, XiaoMiOpenIdInfo.class);
        if (openInfo != null && openInfo.getCode().equals("0")) {
            //调用服务端小米登陆接口
            LogUtils.e("openId:" + openInfo.getData().getOpenId());
            RequestManager.request(LoginActivity.this, new MiLoginParams(miOAuthResults.getAccessToken(), openInfo.getData().getOpenId()),
                    LoginResponse.class, LoginActivity.this, Constant.URL);
        } else {
            dlgLoad.dismissDialog();
            ToastUtil.show("获取用户id失败,请重试");
        }

    }

    /**
     * 获取OpenID
     */
    private void getOpenID(String accessToken, String macKey, String macAlgorithm) {
        LogUtils.e("accessToken:" + accessToken);
        final XiaomiOAuthFuture<String> future = new XiaomiOAuthorize()
                .callOpenApi(LoginActivity.this,
                        Constant.XIAOMI_APPID,
                        XiaomiOAuthConstants.OPEN_API_PATH_OPEN_ID,
                        accessToken,
                        macKey,
                        macAlgorithm);
        waitXiaoMiOAuthResult(future);
    }

    private void submitCheck() {
        LoginParams params = new LoginParams(etUserno.getText().toString(),
                etPwd.getText().toString());
        if (TextUtils.isEmpty(params.getLoginName())
                || TextUtils.isEmpty(params.getPwd())) {
            ToastUtil.show("帐号密码不能为空。");
            return;
        } else if (params.getLoginName().length() < 6) {
            ToastUtil.show("帐号不能少于6位数。");
            return;
        } else if (params.getPwd().length() < 6) {
            ToastUtil.show("密码不能少于6位数。");
            return;
        }
        SPUtils.instance().put(SPUtils.LOGIN_NAME, params.getLoginName());
        SPUtils.instance().put(SPUtils.LOGIN_PWD, params.getPwd());
        dlgLoad.loading();
        RequestManager.request(LoginActivity.this, params, LoginResponse.class,
                this, Constant.URL);
    }



    @Override
    public void onResponse(BaseResponse resp) {
//        dlgLoad.dismissDialog();
        if (resp instanceof LoginResponse) {
            if ("0".equals(resp.getResCode())) {
//                dlgLoad.loading();
                MainActivity.isMyCourseUpdate = true;
                if (miOAuthResults != null && openInfo != null) {
                    SPUtils.instance().put(SPUtils.MI_TOKEN, miOAuthResults.getAccessToken());
                    SPUtils.instance().put(SPUtils.MI_OPENID, openInfo.getData().getOpenId());
                }
                SPUtils.instance().put(SPUtils.IS_LOGIN, true);
                SPUtils.instance().put(SPUtils.LOGIN_DATA,
                        new Gson().toJson(((LoginResponse) resp).getRepBody()));
                setResult(Activity.RESULT_OK);
                BindClientManager.getInstance(this.getApplicationContext())
                        .bindClientId();
                BindRongYunTokenManager.getInstance(LoginActivity.this).getToken();
                dlgLoad.dismissDialog();
                LoginActivity.this.finish();
            } else {
                dlgLoad.dismissDialog();
                SPUtils.instance().unLogin();
                ToastUtil.show(resp.getResMsg());
            }
        }
    }

    PlatformActionListener listener = new PlatformActionListener() {

        @Override
        public void onError(Platform arg0, int arg1, Throwable arg2) {
            LogUtils.d("login onError");
            dlgLoad.dismissDialog();
        }

        @Override
        public void onComplete(Platform platform, int arg1,
                               HashMap<String, Object> arg2) {
            LogUtils.d("login ok");
            PlatformDb platDB = platform.getDb();
            LoginType type = null;
            LoginParams params;

            if (QQ.NAME.equals(platform.getName())) {
                type = LoginType.QQ;
                String qqIconUrl = platDB.getUserIcon();
                if (qqIconUrl != null && qqIconUrl.length() > 0) {
                    qqIconUrl = qqIconUrl.substring(0,
                            qqIconUrl.lastIndexOf("/"))
                            + "/100";
                }
                params = new LoginParams(type, platDB.getUserId(),
                        platDB.getToken(), qqIconUrl, platDB.getUserName());
            } else if (SinaWeibo.NAME.equals(platform.getName())) {
                type = LoginType.WB;
                params = new LoginParams(type, platDB.getUserId(),
                        platDB.getToken());
            } else {
                type = LoginType.WX;
                params = new LoginParams(type, platDB.getUserId(),
                        platDB.getToken());
            }

            RequestManager.request(LoginActivity.this, params,
                    LoginResponse.class, LoginActivity.this, Constant.URL);
        }

        @Override
        public void onCancel(Platform arg0, int arg1) {
            LogUtils.d("login onCancel");
            dlgLoad.dismissDialog();
        }
    };


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (waitResultTask != null && !waitResultTask.isCancelled()) {
            waitResultTask.cancel(false);
        }
        super.onDestroy();
    }

    private <V> void waitXiaoMiOAuthResult(final XiaomiOAuthFuture<V> future) {
        waitResultTask = new AsyncTask<Void, Void, V>() {
            Exception e;
            private String errorInfo = "";

            @Override
            protected void onPreExecute() {
                dlgLoad.loading();
            }

            @Override
            protected V doInBackground(Void... params) {
                V v = null;
                try {
                    v = future.getResult();
                } catch (IOException e1) {
                    this.e = e1;
                    errorInfo = "授权登陆异常,请重试";
                } catch (OperationCanceledException e1) {
                    //用户取消授权
                    this.e = e1;
                    errorInfo = "取消授权登陆";
                } catch (XMAuthericationException e1) {
                    this.e = e1;
                    errorInfo = "授权登陆失败,请重试";
                }
                return v;
            }

            @Override
            protected void onPostExecute(V v) {
                if (v != null) {
                    if (v instanceof XiaomiOAuthResults) {
                        //获取token返回
                        miOAuthResults = (XiaomiOAuthResults) v;
                        if (miOAuthResults != null && !miOAuthResults.hasError()) {
                            getOpenID(miOAuthResults.getAccessToken(), miOAuthResults.getMacKey(), miOAuthResults.getMacAlgorithm());
                        } else {
                            dlgLoad.dismissDialog();
                            ToastUtil.show(miOAuthResults.getErrorMessage());
                        }
                        return;
                    } else if (v instanceof String) {
                        //获取openID返回
                        doXiaoMiLogin(v.toString());
                    }
                } else if (e != null) {
                    dlgLoad.dismissDialog();
                    ToastUtil.show(errorInfo);
                } else {
                    dlgLoad.dismissDialog();
                    ToastUtil.show("未获取到授权信息");
                }
            }
        }.execute();
    }
}
