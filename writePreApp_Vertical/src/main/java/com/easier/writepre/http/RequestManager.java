package com.easier.writepre.http;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.param.BaseBodyParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ErrorResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.NetWorkUtils;
import com.easier.writepre.utils.SPUtils;
import com.google.gson.Gson;

import android.content.Context;
import android.text.TextUtils;

public class RequestManager<T> {

	public static <T> void request(Context ctx, BaseBodyParams params, Class<T> response,
			WritePreListener<? extends BaseResponse> listener, String ServerURL, ErrorListener error) {
		request(ctx, Method.POST, null, params, response, listener, ServerURL);
	}

	public static <T> void request(Context ctx, BaseBodyParams params, Class<T> response,
			WritePreListener<? extends BaseResponse> listener, String ServerURL) {
		request(ctx, Method.POST, null, params, response, listener, ServerURL);
	}

	public static <T> void request(Context ctx, String tag, BaseBodyParams params, Class<T> response,
			WritePreListener<? extends BaseResponse> listener, String ServerURL) {
		request(ctx, Method.POST, tag, params, response, listener, ServerURL);
	}

	public static <T> void request(Context ctx, int method, String tag, BaseBodyParams params, Class<T> response,
			WritePreListener<? extends BaseResponse> listener, String ServerURL) {
		WritePreListener<T> l = (WritePreListener<T>) listener;
		//有異常，需有漢化解決
		if (TextUtils.isEmpty(ServerURL)||!ServerURL.startsWith("http")) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setResCode("-1000");
			errorResponse.setResMsg("请求地址异常");
			if (TextUtils.isEmpty(tag)) {
				l.onResponse((T)errorResponse);
			} else {
				l.onResponse(tag, (T)errorResponse);
			}
			return;
		}
		String paramStr = new ReqPackage(params).packetParam();
		WritePreRequest<T> writePreRequest = null;

		writePreRequest = new WritePreRequest<T>(ctx, method, tag, ServerURL + params.getUrl(), response, paramStr, l);
		LogUtils.e("Cookie:" + (String) SPUtils.instance().get(SPUtils.LOGIN_COOKER, ""));
		LogUtils.e("request-url:" + (ServerURL + params.getUrl()));
		LogUtils.e("request-body:" + paramStr);

		if (!TextUtils.isEmpty((String) SPUtils.instance().get(SPUtils.LOGIN_COOKER, ""))) {
			writePreRequest.putHeader("Cookie", (String) SPUtils.instance().get(SPUtils.LOGIN_COOKER, ""));
		}
		writePreRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		WritePreApp.getApp().addRequest(writePreRequest);

	}
}
