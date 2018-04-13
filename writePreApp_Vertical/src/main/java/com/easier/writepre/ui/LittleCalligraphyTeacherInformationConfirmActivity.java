package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TecXsfPayOrderGetParams;
import com.easier.writepre.param.TecXsfStuInfoGetParams;
import com.easier.writepre.pay.PayManager;
import com.easier.writepre.pay.PayResultActivity;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TecXsfPayOrderGetResponse;
import com.easier.writepre.response.TecXsfStuInfoGetResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 小书法师报考等级确认页面
 */
public class LittleCalligraphyTeacherInformationConfirmActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private TextView tv_xsfs;
    private TextView tv_grade;
    private TextView tv_money;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_id_card;

    private RadioGroup rg_pay_type;
    private RadioButton rb_alipay;
    private RadioButton rb_weixin;
    private RadioButton rb_upmppay;
    private Button btn_pay;

    private int pay_type = PayManager.PAY_ALIPAY;
    private String sign_str;

//    private CountDownTimer countDownTimer;//倒计时
    private TextView tv_time;//倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_little_calligraphy_teacher_information_confirm);
        initView();
    }

    private void initView() {
        setTopTitle("报考等级确认");

        tv_time = (TextView) findViewById(R.id.tv_time);

        tv_xsfs = (TextView) findViewById(R.id.tv_xsfs);
        if(SPUtils.instance().get("stu_type","0").equals("0")){
            tv_xsfs.setText("小书法师");
        }else{
            tv_xsfs.setText("书法专业人才");
        }
        tv_grade = (TextView) findViewById(R.id.tv_grade);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_id_card = (TextView) findViewById(R.id.tv_id_card);

        rg_pay_type = (RadioGroup) findViewById(R.id.rg_pay_type);
        rb_alipay = (RadioButton) findViewById(R.id.rb_alipay);
        rb_weixin = (RadioButton) findViewById(R.id.rb_weixin);
        rb_upmppay = (RadioButton) findViewById(R.id.rb_upmppay);
        btn_pay = (Button) findViewById(R.id.btn_pay);

        rg_pay_type.setOnCheckedChangeListener(this);
        btn_pay.setOnClickListener(this);

        RequestData();

//        countDownTimer = new CountDownTimer(900000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Date date = new Date(millisUntilFinished);
//                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//                String dateStr = sdf.format(date);
//                tv_time.setText(dateStr);
//            }
//
//            @Override
//            public void onFinish() {
//                tv_time.setText("00:00");
//                btn_pay.setText("已超时");
//                btn_pay.setBackgroundColor(getResources().getColor(R.color.gray));
//                btn_pay.setEnabled(false);
//            }
//        };
//        countDownTimer.start();
    }

    private void RequestData() {
        RequestManager.request(this, new TecXsfStuInfoGetParams((String)SPUtils.instance().get("stu_type","0")), TecXsfStuInfoGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());

    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
        if (response == null) {
            return;
        }
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof TecXsfStuInfoGetResponse) {
                TecXsfStuInfoGetResponse xsfResult = (TecXsfStuInfoGetResponse) response;
                LogUtils.e("111111response====" + xsfResult);
                if (xsfResult != null) {
                    TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody body = xsfResult
                            .getRepBody();
                    tv_grade.setText(body.getStu_level());
                    tv_money.setText(body.getLevel_price());
                    tv_name.setText(body.getReal_name());
                    if (!TextUtils.isEmpty(body.getSex())) {
                        tv_sex.setText(body.getSex().equals("1") ? "男" : "女");
                    }
                    tv_id_card.setText(body.getId_num());
                }
            } else if (response instanceof TecXsfPayOrderGetResponse) {
                TecXsfPayOrderGetResponse xsfResult = (TecXsfPayOrderGetResponse) response;
                if (xsfResult != null) {
                    TecXsfPayOrderGetResponse.TecXsfPayOrderGetBody body = xsfResult
                            .getRepBody();
                    sign_str = body.getSign_str();
                    Intent intent = new Intent(this, PayResultActivity.class);
                    intent.putExtra(PayResultActivity.PAY_MODE, pay_type);
                    intent.putExtra(PayResultActivity.PAY_INFO, sign_str);
                    startActivityForResult(intent, 1);
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_alipay:
                pay_type = PayManager.PAY_ALIPAY;
                break;
            case R.id.rb_weixin:
                pay_type = PayManager.PAY_WECHAT;
                break;
            case R.id.rb_upmppay:
                pay_type = PayManager.PAY_UP;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_pay:
//                if (pay_type == PayManager.PAY_WECHAT || pay_type == PayManager.PAY_UP){
//                    ToastUtil.show("暂时只支持支付宝，敬请期待");
//                }else{
                dlgLoad.loading();
                RequestManager.request(this, new TecXsfPayOrderGetParams(pay_type,(String)SPUtils.instance().get("stu_type","0")), TecXsfPayOrderGetResponse.class, this,
                        SPUtils.instance().getSocialPropEntity().getApp_school_server());
//                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //返回支付結果
        if (requestCode == 1) {
            if (resultCode == PayManager.PAY_SUCCESS) {
                finish();
            } else if (resultCode == PayManager.PAY_FAIL) {

            }
        }
    }
}
