package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
//import android.webkit.WebChromeClient;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.VShowUserThirdParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.VShowUserThirdResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;


/**
 * v展三级界面（v展专辑图片）
 */
public class VShowUserThirdActivity extends BaseActivity {

    private ImageView imageView;

    private WebView webView;

    private ProgressBar progressBar;

    private List<VShowUserThirdResponse.PicInfo> list = new ArrayList<VShowUserThirdResponse.PicInfo>();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_v_show_third);
        init();
        request();
    }

    /**
     * 查询V展专辑图片
     */
    public void request() {
        RequestManager.request(this, new VShowUserThirdParams(getIntent().getStringExtra("group_id")), VShowUserThirdResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void init() {
        setTopTitle(getIntent().getStringExtra("title"));
        setTopRight(R.drawable.share_icon);
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(getIntent().getStringExtra("url"));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        WebSettings mWebSettings = webView.getSettings();
        imageView = (ImageView) findViewById(R.id.iv_share_icon);
        BitmapHelp.getBitmapUtils().display(imageView, getIntent().getStringExtra("share_url"), new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View arg0, String arg1,
                                        Bitmap arg2, BitmapDisplayConfig arg3,
                                        BitmapLoadFrom arg4) {
                ((ImageView) arg0).setImageBitmap(arg2);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1,
                                     Drawable arg2) {

            }
        });
//        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        mWebSettings.setBlockNetworkImage(true);
//        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setJavaScriptEnabled(true);
//        mWebSettings.setAppCacheEnabled(true);
//        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        mWebSettings.setUseWideViewPort(true);
//        mWebSettings.setLoadWithOverviewMode(true);
//        mWebSettings.setBuiltInZoomControls(true);
//        mWebSettings.setLightTouchEnabled(true);
//        mWebSettings.setSupportZoom(true);
//        webView.setHapticFeedbackEnabled(false);
        //webView.setWebChromeClient(new WebChromeClients());
        webView.getSettings().setUserAgentString("xzp_android");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (view.getUrl().equals(url)) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                parseUrl(url);
                super.onPageFinished(view, url);
            }
        });
    }

//    public class WebChromeClients extends WebChromeClient {
//
//        @Override
//        public void onProgressChanged(android.webkit.WebView view, int newProgress) {
//            if (newProgress == 100) {
//                progressBar.setVisibility(View.GONE);
//            } else {
//                if (progressBar.getVisibility() == View.GONE)
//                    progressBar.setVisibility(View.VISIBLE);
//                progressBar.setProgress(newProgress);
//            }
//            super.onProgressChanged(view, newProgress);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onTopRightClick(View view) {
        super.onTopRightClick(view);
        sharedContentCustomize(getIntent().getStringExtra("name"), getIntent().getStringExtra("title"),
                getIntent().getStringExtra("url"), "", imageView);
    }

    private int position;

    private void parseUrl(String url) {
        if (url.contains("#")) {
            String id = url.substring(url.indexOf("#") + 1,
                    url.length());
            String[] strs = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                if (id.equals(list.get(i).get_id())) {
                    position = i;
                }
                strs[i] = list.get(i).getPic_url();
            }
            imageBrower(position, strs);
        }
    }

    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(VShowUserThirdActivity.this, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        VShowUserThirdActivity.this.startActivity(intent);
    }

    @Override
    public void onResponse(BaseResponse response) {
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof VShowUserThirdResponse) {
                VShowUserThirdResponse vResult = (VShowUserThirdResponse) response;
                if (vResult != null) {
                    VShowUserThirdResponse.Repbody body = vResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null && body.getList().size() > 0) {
                            for (int i = 0; i < body.getList().size(); i++) {
                                list.add(body.getList().get(i));
                            }
                        }
                    }
                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }
}
