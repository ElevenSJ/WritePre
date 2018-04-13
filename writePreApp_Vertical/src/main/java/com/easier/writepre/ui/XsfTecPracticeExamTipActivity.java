package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.easier.writepre.R;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.entity.WorksInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.XsfTecPracticeExamDetailParams;
import com.easier.writepre.param.XsfTecPracticeExamGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.XsfTecPracticeExamDetailResponse;
import com.easier.writepre.response.XsfTecPracticeExamGetResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.lidroid.xutils.exception.DbException;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 实践考试温馨提示页面
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamTipActivity extends BaseActivity {

    private Button button;
    private String exam_id;//考试id
    private String practice_info_url;//实践考试介绍
    private WebView wv_content;
    private String stu_type;
    private int practice_count_limit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xsf_tec_practice_exam_tip);
        initView();
        initData();
        request();
    }

    private void initData() {
        setTopTitle("温馨提示");

        practice_count_limit = getIntent().getIntExtra("practice_count_limit",0);
        stu_type = getIntent().getStringExtra("stu_type");
        practice_info_url = getIntent().getStringExtra("practice_info_url");
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

        wv_content.loadUrl(practice_info_url);

        try {
            DBHelper.getExecutor().createTableIfNotExist(WorksInfo.class);         //  创建作品表
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void request() {
        RequestManager.request(this, new XsfTecPracticeExamGetParams(stu_type), XsfTecPracticeExamGetResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    private void initView() {
        setTitle("温馨提示");
        wv_content = (WebView) findViewById(R.id.wv_content);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

    }

    private void showConfirmDialog() {
        ConfirmHintDialog dialog = new ConfirmHintDialog(this,
                R.style.loading_dialog, "你还有"+practice_count_limit+"次考试机会",
                new ConfirmHintDialog.ConfirmHintDialogListener() {

                    @Override
                    public void OnClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_confirm:
                                confirm();
                                // ToastUtil.show("退出成功");
                                break;
                            case R.id.tv_cancel:
                                break;

                            default:
                                break;
                        }
                    }
                });
        dialog.show();
//        ToastUtil.show("你还有"+(practice_count_limit-practice_count)+"次考试机会");

    }

    private void confirm() {
        if(!exam_id.isEmpty()){
            requestExamDeitail();
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof XsfTecPracticeExamGetResponse) {
                XsfTecPracticeExamGetResponse.XsfTecPracticeExamGetInfo reBody = ((XsfTecPracticeExamGetResponse) response).getRepBody();
                exam_id = reBody.get_id();
                button.setEnabled(true);

            }else if(response instanceof XsfTecPracticeExamDetailResponse){
                XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailBody examDetailBody = ((XsfTecPracticeExamDetailResponse) response).getRepBody();
                Intent intent = new Intent(XsfTecPracticeExamTipActivity.this, XsfTecPracticeExamActivity.class);
                intent.putExtra("stu_type", stu_type);
                intent.putExtra("exam_id", exam_id);
                intent.putExtra("exam_detail_body", examDetailBody);
                startActivity(intent);
            }
        } else {
            ToastUtil.show(response.getResMsg());
            button.setEnabled(false);
        }
    }

    private void requestExamDeitail() {
        RequestManager.request(this, new XsfTecPracticeExamDetailParams(exam_id, stu_type), XsfTecPracticeExamDetailResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }
}
