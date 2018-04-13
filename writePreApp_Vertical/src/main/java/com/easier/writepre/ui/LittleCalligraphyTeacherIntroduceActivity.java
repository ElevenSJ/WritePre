package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.easier.writepre.R;
import com.easier.writepre.ui.myinfo.EditTelActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 小书法师介绍页面
 */
public class LittleCalligraphyTeacherIntroduceActivity extends BaseActivity {

    private WebView wv_content;

    private Button btn_sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_little_calligraphy_teacher_introduce);
        initView();
    }

    private void initView() {
        setTopTitle(SPUtils.instance().get("stu_type","0").equals("0")?"小书法师等级考试介绍":"书法专业人才等级考试介绍");
        wv_content = (WebView) findViewById(R.id.wv_content);
        WebSettings mWebSettings = wv_content.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        wv_content.setHapticFeedbackEnabled(false);

        wv_content.setWebViewClient(new WebViewClient());
        wv_content.setWebChromeClient(new WebChromeClient());

        wv_content.loadUrl(getIntent().getStringExtra("url"));

        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);

        btn_sign_up.setText(getIntent().getStringExtra("exam_info"));
        btn_sign_up.setEnabled(getIntent().getBooleanExtra("open",true));
        btn_sign_up.setOnClickListener(this);
    }

    public void onBack(){
        if (wv_content.canGoBack()) {
            wv_content.goBack();
        }else{
            finish();
        }
    }

    @Override
    public void onTopLeftClick(View view) {
        onBack();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_sign_up:
                //手机号为空，先绑定手机号
                if(TextUtils.isEmpty(SPUtils.instance().getLoginEntity().getTel())){
                    Intent intent = new Intent(this, EditTelActivity.class);
                    startActivity(intent);
                }else{
//                    if(!TextUtils.isEmpty(getIntent().getStringExtra("id"))){//已报名
//                        Intent intentS = new Intent(this,LittleCalligraphyTeacherInformationConfirmActivity.class);
//                        startActivity(intentS);
//                    }else{//未报名
//                        Intent intentS = new Intent(this,LittleCalligraphyTeacherEditCandidateInformationActivity.class);
//                        startActivity(intentS);
//                    }

                    if(!getIntent().getBooleanExtra("isXsfsSigned",false) && !getIntent().getBooleanExtra("isSfzyrcSigned",false)){
                        Intent intentS = new Intent(this,LittleCalligraphyTeacherEditCandidateInformationActivity.class);
                        startActivity(intentS);
                        finish();
                    }else if(getIntent().getBooleanExtra("isXsfsSigned",false)){
                        ToastUtil.show("该账号已报名小书法师，暂不支持同时报名书法专业人才");
                    }else if(getIntent().getBooleanExtra("isSfzyrcSigned",false)){
                        ToastUtil.show("该账号已报名书法专业人才，暂不支持同时报名小书法师");
                    }
//                    if(getIntent().getIntExtra("xsfsSignedStatus",0) == 0){
//                        Intent intentS = new Intent(this,LittleCalligraphyTeacherEditCandidateInformationActivity.class);
//                        startActivity(intentS);
//                        finish();
//                    }else if(getIntent().getIntExtra("xsfsSignedStatus",0) == 1){
//                        ToastUtil.show("该账号已报名小书法师，暂不支持同时报名书法专业人才");
//                    }else{
//                        ToastUtil.show("该账号已报名书法专业人才，暂不支持同时报名小书法师");
//                    }

                }

                break;
        }
    }
}
