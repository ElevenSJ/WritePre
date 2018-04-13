package com.easier.writepre.manager;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.BindClientIdParams;
import com.easier.writepre.param.SocialInfoGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.RongYunTokenResponse;
import com.easier.writepre.response.SocialInfoGetResponse;
import com.easier.writepre.rongyun.RongYunUtils;
import com.easier.writepre.ui.LoginActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.google.gson.Gson;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class BindRongYunTokenManager implements WritePreListener<BaseResponse>, Response.ErrorListener {

    private static BindRongYunTokenManager instance;

    private int start = 0;

    private int count = 10;

    private boolean isBind = false;

    private boolean isRequest = false;

    private Context ctx;

    public synchronized static BindRongYunTokenManager getInstance(Context mCtx) {

        if (instance == null) {
            instance = new BindRongYunTokenManager(mCtx.getApplicationContext());
        }
        return instance;
    }

    private BindRongYunTokenManager(Context mCtx) {
        this.ctx = mCtx;
    }

    public void getToken() {
        LogUtils.e("开始获取融云Token");
        isBind = false;
        isRequest = false;
        start = 0;
        count = 10;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isBind) {
                    if (start >= count) {
                        LogUtils.e("已尝试获取融云Token：" + start + "次，结束尝试!");
                        isBind = true;
                        break;
                    }
                    if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                        isBind = true;
                        break;
                    }
                    if (!isRequest) {
                        LogUtils.e("开始第" + (start + 1) + "次获取融云Token");
                        if (TextUtils.isEmpty(SPUtils.instance().getToken())) {
                            SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
                            if (!TextUtils.isEmpty(propEntity.getApp_socail_server())) {
                                isRequest = true;
                                requestToken();
                            }
                        }else{
                            reconnect(SPUtils.instance().getToken());
                            isBind = true;
                        }
                        start++;
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }

    private void reconnect(String token) {
        LogUtils.e("登陆成功,获取到融云token后,尝试连接融云...");
        RongYunUtils.getInstances().connect(
                ctx, token,
                new RongIMClient.ConnectCallback() {

                    /**
                     * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App
                     * Server 重新请求一个新的 Token
                     */
                    @Override
                    public void onTokenIncorrect() {
                        LogUtils.e("登录融云--Token出错");
                    }

                    /**
                     * 连接融云成功
                     *
                     * @param userid
                     *            当前 token
                     */
                    @Override
                    public void onSuccess(String userid) {
                        LogUtils.e("连接融云 --成功 ! userid= "
                                + userid);
                        // 将最新的个人信息同步给融云
                        if (RongIM.getInstance() != null) {
                            String headurl = StringUtil
                                    .getHeadUrl(SPUtils
                                            .instance()
                                            .getLoginEntity()
                                            .getHead_img());
                            UserInfo userInfo = new UserInfo(
                                    userid,
                                    SPUtils.instance()
                                            .getLoginEntity()
                                            .getUname(),
                                    TextUtils.isEmpty(headurl) ? null : Uri.parse(headurl));
                            RongIM.getInstance()
                                    .setCurrentUserInfo(
                                            userInfo);
                            RongIM.getInstance()
                                    .setMessageAttachedUserInfo(
                                            true);
                        }
                    }

                    /**
                     * 连接融云失败
                     *
                     * @param errorCode
                     *            错误码，可到官网 查看错误码对应的注释
                     */
                    @Override
                    public void onError(
                            RongIMClient.ErrorCode errorCode) {
                        LogUtils.e("连接融云 --连接失败 ! errorCode= "
                                + errorCode);

                    }
                });
    }

    /**
     * 获取融云Token
     */
    private void requestToken() {
        RongYunUtils.getInstances().requestRongYunToken(ctx,
                this);
    }

    @Override
    public void onResponse(BaseResponse response) {
        if (response instanceof RongYunTokenResponse) {
            isRequest = false;
            if ("0".equals(response.getResCode())) {
                RongYunTokenResponse rongYunTokenResponse = (RongYunTokenResponse) response;
                String token = rongYunTokenResponse.getRepBody().getToken();
                SPUtils.instance().put(SPUtils.RONGYUN_TOKEN, token);
                LogUtils.e("获取融云Token成功");
                LogUtils.e("融云Token:" + token);
                reconnect(token);
                isBind = true;
            } else {
                ToastUtil.show(response.getResMsg());
                LogUtils.e("获取融云Token失败");
                isBind = false;
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        // TODO Auto-generated method stub

    }

}
