package com.easier.writepre.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by SunJie on 17/1/12.
 */

public class PayManager {

    private static PayManager payManager = null;

    public static final int PAY_SUCCESS = 100;//支付成功
    public static final int PAY_FAIL = 101;//支付失败


    public static final int PAY_ALIPAY = 1;//支付宝支付
    public static final int PAY_WECHAT = 2;//微信支付
    public static final int PAY_UP = 3;//银联支付


    private static final int PAY_ALIPAY_FLAG = 1001;
    private static final int PAY_WECHAT_FLAG = 1002;
    private static final int PAY_UP_FLAG = 1003;


    private Handler handler;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_ALIPAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "支付成功");
                        handler.obtainMessage(PAY_SUCCESS, intent).sendToTarget();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "支付取消");
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", payResult.getMemo());
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    }
                    break;
                case PAY_WECHAT_FLAG:
                    int code = (int) msg.obj;
                    if (code == 0) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "支付成功！");
                        handler.obtainMessage(PAY_SUCCESS, intent).sendToTarget();
                    } else if (code == -1) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "支付失败！");
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    } else if (code == -2) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "支付取消！");
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    } else if (code == -1011) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "订单信息解析异常！");
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    } else if (code == -1012) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "请先安装微信客户端！");
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    }else if (code == -1013) {
                        Intent intent = new Intent();
                        intent.putExtra("resultMsg", "当前微信版本不支持微信支付，请升级！");
                        handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                    }
                    break;
                case PAY_UP_FLAG:
                    break;
            }
        }
    };

    public static PayManager instance() {
        if (payManager == null) {
            payManager = new PayManager();
        }
        return payManager;
    }

    public void sendWeChatPayResult(int code) {
        Message msg = new Message();
        msg.what = PAY_WECHAT_FLAG;
        msg.obj = code;
        mHandler.sendMessage(msg);
    }

    public void doPay(Activity ctx, int payMode, Object orderInfo, Handler mHandler) {
        this.handler = mHandler;
        switch (payMode) {
            case PAY_ALIPAY:
                aliPay(ctx, (String) orderInfo);
                break;
            case PAY_WECHAT:
                weChatPay(ctx, (String) orderInfo);
                break;
            case PAY_UP:
                break;
            default:
                Intent intent = new Intent();
                intent.putExtra("resultMsg", "请选择支付方式");
                handler.obtainMessage(PAY_FAIL, intent).sendToTarget();
                break;
        }
    }

    /**
     * 微信支付
     *
     * @param ctx
     */
    private void weChatPay(Activity ctx, String orderInfo) {
        try {
            String order = URLDecoder.decode(orderInfo, "UTF-8");
            LogUtils.e("wechat-order:"+order);
            WeChatOrder weChatOrder = new Gson().fromJson(order, WeChatOrder.class);
            SPUtils.instance().put(SPUtils.WECHAT_APPID,TextUtils.isEmpty(weChatOrder.getAppid())?"wx5975378d0d3925d4":weChatOrder.getAppid());
            // 将该app注册到微信
            final IWXAPI msgApi = WXAPIFactory.createWXAPI(ctx, null);
            if (msgApi.isWXAppInstalled()){
                if (msgApi.isWXAppSupportAPI()){
                    msgApi.registerApp(weChatOrder.getAppid());

                    PayReq request = new PayReq();
                    request.appId = weChatOrder.getAppid();
                    request.partnerId = weChatOrder.getPartnerid();
                    request.prepayId = weChatOrder.getPrepayid();
                    request.packageValue = "Sign=WXPay";
                    request.nonceStr = weChatOrder.getNoncestr();
                    request.timeStamp = weChatOrder.getTimestamp();
                    request.sign = weChatOrder.getSign();
                    msgApi.sendReq(request);
                }else{
                    sendWeChatPayResult(-1013);
                }
            }else{
                sendWeChatPayResult(-1012);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            sendWeChatPayResult(-1011);
        }
    }

    /**
     * 支付宝支付
     */
    private void aliPay(final Activity ctx, final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ctx);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = PAY_ALIPAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
