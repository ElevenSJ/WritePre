package com.easier.writepre.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.easier.writepre.R;
import com.easier.writepre.pay.PayManager;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付回调页面
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, (String)SPUtils.instance().get(SPUtils.WECHAT_APPID,"wx5975378d0d3925d4"));
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
//        ToastUtil.show(req.toString());
        LogUtils.e(req.toString());
    }

    @Override
    public void onResp(BaseResp resp) {

        int code = resp.errCode;
        if (resp instanceof PayResp) {
            LogUtils.e(resp.errCode + "," + resp.errStr + "," + ((PayResp) resp).prepayId + "," + ((PayResp) resp).returnKey + "," + ((PayResp) resp).extData);
        }
        PayManager.instance().sendWeChatPayResult(code);
        finish();
    }


}
