package com.easier.writepre.manager;

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
import com.easier.writepre.response.SocialInfoGetResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;

import android.content.Context;
import android.text.TextUtils;

public class BindClientManager implements WritePreListener<BaseResponse>,Response.ErrorListener {

	private static BindClientManager instance;

	private int start = 0;
	
	private int count = 10;

	private boolean isBind = false;

	private boolean isRequest = false;

	private Context ctx;

	private boolean getServiceInfo = false;
	
	public synchronized static BindClientManager getInstance(Context mCtx) {

		if (instance == null) {
			instance = new BindClientManager(mCtx.getApplicationContext());
		}
		return instance;
	}

	private BindClientManager(Context mCtx) {
		this.ctx = mCtx;
	}

	public synchronized void setGetServiceInfo(boolean success) {
		getServiceInfo = success;
	}

	public boolean getGetServiceInfo() {
		return getServiceInfo;
	}

	public void bindClientId() {
		LogUtils.e("开始绑定clientId");
		isBind = false;
		isRequest = false;
		start = 0;
		count = 10;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!isBind) {
					if (start>=count) {
						LogUtils.e("已尝试绑定clientId："+start+"次，结束尝试!");
						isBind = true;
						break;
					}
					if (!(boolean)SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
						isBind = true;
						break;
					}
					if (!isRequest) {
						LogUtils.e("开始第" + (start + 1) + "次绑定clientId");
						if (!TextUtils.isEmpty(WritePreApp.clientId)) {
							SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
							if (getGetServiceInfo()||!TextUtils.isEmpty(propEntity.getApp_socail_server())) {
									isRequest = true;
									RequestManager.request(ctx,
											new BindClientIdParams(WritePreApp.clientId,WritePreApp.geTuiAppId), BaseResponse.class,
											BindClientManager.this, propEntity.getApp_socail_server(),BindClientManager.this);
							} else {
								LogUtils.e("服务端配置信息未获取成功");
								LogUtils.e("重新获取服务端配置信息");
								isRequest = true;
								RequestManager.request(ctx,new SocialInfoGetParams(),
										SocialInfoGetResponse.class, BindClientManager.this, Constant.URL);
							}
						} else {
							LogUtils.e("clientId为空,个推未初始化成功,等待...");
							if (start==9){
								PushManager.getInstance().initialize(ctx);
							}
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

	@Override
	public void onResponse(BaseResponse response) {
		if (response instanceof SocialInfoGetResponse) {
			isRequest = false;
			if (response.getResCode().equals("0")) {
				SocialInfoGetResponse sigrResult = (SocialInfoGetResponse) response;
				if (sigrResult != null) {
					SocialPropEntity rBody = sigrResult.getRepBody();
					SPUtils.instance().put(SPUtils.SOCIAL_PROP_DATA, new Gson().toJson(rBody));
					setGetServiceInfo(true);
				}
			}
		} else if (response instanceof BaseResponse) {
			isRequest = false;
			if ("0".equals(response.getResCode())) {
				LogUtils.e("绑定clientId成功");
				isBind = true;
			} else {
				LogUtils.e("绑定clientId失败");
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
