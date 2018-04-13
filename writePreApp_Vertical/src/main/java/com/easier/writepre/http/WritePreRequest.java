package com.easier.writepre.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.LoginResponse;
import com.easier.writepre.ui.LoginActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.ResponseUtils;
import com.easier.writepre.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

public class WritePreRequest<T> extends Request<T> {

    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("text/xml; charset=%s", PROTOCOL_CHARSET);

    private final WritePreListener<T> mListener;

    private final String mRequestBody;

    private Gson mGson;
    private Class<T> mJavaClass;

    private Map<String, String> headers = new HashMap<String, String>();

    private Context ctx;

    private String tag;

    public WritePreRequest(Context ctx, int method, String tag, String url, Class<T> cls, String requestBody,
                           WritePreListener<T> listener) {
        super(method, url, null);
        this.ctx = ctx;
        this.tag = tag;
        mGson = new Gson();
        mJavaClass = cls;
        mListener = listener;
        mRequestBody = requestBody;
    }

    @Override
    public void deliverError(VolleyError volleyError) {
        // TODO Auto-generated method stub
        if (ctx != null && ctx instanceof NoSwipeBackActivity) {
            if (((NoSwipeBackActivity) ctx).isDestroyed) {
                return;
            }
        }
        String json = "{resCode:-1000,resMsg:服务器异常}";
        if (volleyError == null) {
            if (mListener != null) {
                if (TextUtils.isEmpty(tag)) {
                    mListener.onResponse(mGson.fromJson(json, mJavaClass));
                } else
                    mListener.onResponse(tag, mGson.fromJson(json, mJavaClass));
            }
            return;
        }
        LogUtils.e("volleyError:" + volleyError.toString());

        if (volleyError instanceof NoConnectionError) {
            json = "{resCode:-1000,resMsg:连接服务器异常}";
        } else if (volleyError instanceof NetworkError) {
            json = "{resCode:-1000,resMsg:网络异常，请确认网络通畅后重试}";
        } else if (volleyError instanceof TimeoutError) {
            json = "{resCode:-1000,resMsg:请求数据超时}";
        } else if (volleyError instanceof ParseError) {
            json = "{resCode:-1000,resMsg:报文异常}";
        } else {
            json = "{resCode:-1000,resMsg:服务器异常}";
        }
        if (mListener != null) {
            if (TextUtils.isEmpty(tag)) {
                mListener.onResponse(mGson.fromJson(json, mJavaClass));
            } else
                mListener.onResponse(tag, mGson.fromJson(json, mJavaClass));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (ctx != null && ctx instanceof NoSwipeBackActivity) {
            if (((NoSwipeBackActivity) ctx).isDestroyed) {
                return;
            }
        }
        if ("7".equals(((BaseResponse) response).getResCode()) || "3".equals(((BaseResponse) response).getResCode())) {
            // ToastUtil.show(((BaseResponse) response).getResMsg());
            SPUtils.instance().unLogin();
            if (response instanceof LoginResponse) {

            } else {
                if (ctx != null && ctx instanceof Activity) {
                    if (ctx instanceof LoginActivity) {

                    } else {
                        LoginUtil.checkLogin(ctx);
                    }
                }
            }
        }
        LogUtils.e("deliverReponse:" + response.toString());
        if (mListener != null) {
            if (TextUtils.isEmpty(tag)) {
                mListener.onResponse(response);
            } else
                mListener.onResponse(tag, response);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            LogUtils.e("返回结果:" + jsonString);
            T parsedGSON = null;
            if (!"RESULT: OK".equals(jsonString)) {
                parsedGSON = mGson.fromJson(jsonString, mJavaClass);
                String cooker = ResponseUtils.findToken(response.headers.get("Set-Cookie"));
                if (!TextUtils.isEmpty(cooker) && parsedGSON instanceof LoginResponse) {

                    LogUtils.e("登陆返回:" + ((LoginResponse) parsedGSON).getResMsg());
                    if (((LoginResponse) parsedGSON).getResCode().equals("0")) {
                        LogUtils.e("登陆成功保存cooker=" + cooker);
                        SPUtils.instance().put(SPUtils.LOGIN_COOKER, cooker);
                    } else {
                        SPUtils.instance().remove(SPUtils.LOGIN_COOKER);
                    }
                }
                if (((BaseResponse) parsedGSON).getResCode().equals("7")) {
                    SPUtils.instance().unLogin();
                } else if (((BaseResponse) parsedGSON).getResCode().equals("3")) {
                    SPUtils.instance().unLogin();
                }
            }
            return Response.success(parsedGSON, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody,
                    PROTOCOL_CHARSET);
            return null;
        }
    }

}
