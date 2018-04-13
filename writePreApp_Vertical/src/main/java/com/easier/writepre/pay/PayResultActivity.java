package com.easier.writepre.pay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.RegisterForExaminationActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by SunJie on 17/1/12.
 */

public class PayResultActivity extends BaseActivity {

    public static final String PAY_MODE = "pay_mode";//支付方式
    public static final String PAY_INFO = "pay_info";//订单信息

    private int payMode = 0;//支付方式
    private String payInfo = "";//支付信息

    private View mainView;
    private View topView;
    private View resultView;
    private TextView txtResult;
    private Button bt;

    private void setView() {
        setTopTitle("支付结果");
        mainView.setBackgroundResource(R.color.white);
        topView.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pay_result);
        payMode = getIntent().getIntExtra(PAY_MODE, payMode);
        payInfo = getIntent().getStringExtra(PAY_INFO);
        initView();
        PayManager.instance().doPay(this, payMode, payInfo, new Handler() {
            public void handleMessage(Message msg) {
                setView();
                Intent intent = (Intent) msg.obj;
                String resultMsg = intent.getStringExtra("resultMsg");
                txtResult.setText(resultMsg);
                switch (msg.what) {
                    case PayManager.PAY_SUCCESS:
//                        ToastUtil.show(resultMsg);
                        bt.setText("查看我的报考");
                        SPUtils.instance().put("payResult",true);
                        break;
                    case PayManager.PAY_FAIL:
//                        ToastUtil.show(resultMsg);
                        bt.setText("再试一次");
                        break;
                }
            }
        });
    }

    private void initView() {
        mainView = findViewById(R.id.main_layout);
        topView = findViewById(R.id.rl_title);
        resultView = findViewById(R.id.result_view);
        txtResult = (TextView) findViewById(R.id.txt_result);
        bt = (Button) findViewById(R.id.bt);

        bt.setOnClickListener(this);
    }


    /**
     * 返回支付结果
     *
     * @param resultCode
     * @param resultIntent
     */
    private void setPayResult(int resultCode, Intent resultIntent) {
        setResult(resultCode, resultIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt:
                if (bt.getText().equals("查看我的报考")) {
                    setPayResult(PayManager.PAY_SUCCESS, null);
                    Intent intent0 = new Intent(this, RegisterForExaminationActivity.class);
                    this.startActivity(intent0);
                } else {
                    setPayResult(PayManager.PAY_FAIL, null);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setView();
    }

    private void onback() {
        if (bt.getText().equals("查看我的报考")) {
            setPayResult(PayManager.PAY_SUCCESS, null);
        } else {
            setPayResult(PayManager.PAY_FAIL, null);
        }
        overridePendingTransition(0, R.anim.slide_right_out);
        finish();
    }

    @Override
    public void onTopLeftClick(View view) {
        onback();
    }

    @Override
    public void onSwipBack() {
        onback();
    }
}
