package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.CalligraphyInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CalligraphyCommitParams;
import com.easier.writepre.param.CalligraphyParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CalligraphyResponse;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

/**
 * Created by SunJie on 17/3/16.
 */

public class CalligraphyJoinActivity extends BaseActivity {

    private TextView tvName;
    private TextView tvKemu;
    private TextView tvCity;
    private TextView tvEmail;
    private TextView tvTel;

    private CheckBox cbAgreed;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_calligraphy_join);
        initView();
        loadData();
    }

    private void loadData() {
        dlgLoad.loading();
        RequestManager.request(this, new CalligraphyParams(), CalligraphyResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void commitInfo() {
        dlgLoad.loading();
        RequestManager.request(this, new CalligraphyCommitParams(tvName.getText().toString(), tvKemu.getText().toString(), tvCity.getText().toString(), tvEmail.getText().toString(), tvTel.getText().toString()), BaseResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void initView() {
        setTopTitle("机构入驻申请");

        tvName = (TextView) findViewById(R.id.et_input_name);
        tvKemu = (TextView) findViewById(R.id.et_input_desc);
        tvCity = (TextView) findViewById(R.id.et_input_type);
        tvEmail = (TextView) findViewById(R.id.et_input_tag);
        tvTel = (TextView) findViewById(R.id.et_input_isopen);

        cbAgreed = (CheckBox) findViewById(R.id.cb_agreement);

        findViewById(R.id.tv_next).setOnClickListener(this);
        findViewById(R.id.tv_useragreement).setOnClickListener(this);

        tvName.setOnClickListener(this);
        tvKemu.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        tvTel.setOnClickListener(this);
    }

    public void updateView(CalligraphyInfo info) {
        if (info == null|| TextUtils.isEmpty(info.get_id())) {
            return;
        }
        tvName.setText(info.getOrg_name());
        tvKemu.setText(info.getBusiness());
        tvCity.setText(info.getCity());
        tvEmail.setText(info.getEmail());
        tvTel.setText(info.getTel());

        tvName.setClickable(false);
        tvKemu.setClickable(false);
        tvCity.setClickable(false);
        tvEmail.setClickable(false);
        tvTel.setClickable(false);
        cbAgreed.setClickable(false);
        findViewById(R.id.tv_next).setEnabled(false);

        if (!TextUtils.isEmpty(info.getStatus())) {
            if (info.getStatus().equals("wait")) {
                ((TextView) findViewById(R.id.tv_next)).setText("正在审核中");
            } else if (info.getStatus().equals("accept")) {
                ((TextView) findViewById(R.id.tv_next)).setText("审核已通过");
            } else if (info.getStatus().equals("refuse")) {
                ((TextView) findViewById(R.id.tv_next)).setText("审核未通过，重新提交");
                tvName.setClickable(true);
                tvKemu.setClickable(true);
                tvCity.setClickable(true);
                tvEmail.setClickable(true);
                tvTel.setClickable(true);
                cbAgreed.setClickable(true);
                findViewById(R.id.tv_next).setEnabled(true);
            }
        }else {
            ((TextView) findViewById(R.id.tv_next)).setText("机构信息已提交");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent nameIntent = new Intent(this, EditTextActivity.class);
        switch (v.getId()) {
            case R.id.et_input_name:
                nameIntent.putExtra(EditTextActivity.TEXT_TITLE, "编辑机构名称");
                nameIntent.putExtra(EditTextActivity.TEXT_VALUE, tvName.getText().toString());
                nameIntent.putExtra(EditTextActivity.TEXT_HINT, "请输入真实的机构名称");
                startActivityForResult(nameIntent, 100);
                break;
            case R.id.et_input_desc:
                nameIntent.putExtra(EditTextActivity.TEXT_TITLE, "编辑机构主营项目");
                nameIntent.putExtra(EditTextActivity.TEXT_VALUE, tvKemu.getText().toString());
                nameIntent.putExtra(EditTextActivity.TEXT_HINT, "请输入机构的主营项目（如：书法）");
                startActivityForResult(nameIntent, 101);
                break;
            case R.id.et_input_type:
                Intent intent3 = new Intent(this, EditCityActivity.class);
                startActivityForResult(intent3, 102);
                break;
            case R.id.et_input_tag:
                nameIntent.putExtra(EditTextActivity.TEXT_TITLE, "编辑机构邮箱");
                nameIntent.putExtra(EditTextActivity.TEXT_VALUE, tvEmail.getText().toString());
                nameIntent.putExtra(EditTextActivity.TEXT_HINT, "请输入邮箱地址");
                startActivityForResult(nameIntent, 103);
                break;
            case R.id.et_input_isopen:
                nameIntent.putExtra(EditTextActivity.TEXT_TITLE, "编辑机构联系方式");
                nameIntent.putExtra(EditTextActivity.TEXT_VALUE, tvTel.getText().toString());
                nameIntent.putExtra(EditTextActivity.TEXT_HINT, "请输入有效的联系方式");
                startActivityForResult(nameIntent, 104);
                break;
            case R.id.tv_next:
                if (!cbAgreed.isChecked()) {
                    ToastUtil.show("请先仔细阅读《机构入驻须知》");
                } else {
                    if(checkText()){
                        commitInfo();
                    }else{
                        ToastUtil.show("信息请填写完整");
                    }
                }
                break;
            case R.id.tv_useragreement:
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("url","http://m.xiezipai.com/sfs/sfsrzsm/");
                intent.putExtra("title","机构入驻须知");
                startActivity(intent);
                break;
        }
    }

    private boolean checkText() {
        if (TextUtils.isEmpty(tvName.getText().toString())){
            return false;
        }
        if (TextUtils.isEmpty(tvKemu.getText().toString())){
            return false;
        }
        if (TextUtils.isEmpty(tvCity.getText().toString())){
            return false;
        }
        if (TextUtils.isEmpty(tvEmail.getText().toString())){
            return false;
        }
        if (TextUtils.isEmpty(tvTel.getText().toString())){
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                tvName.setText(data.getStringExtra("value"));
            } else if (requestCode == 101) {
                tvKemu.setText(data.getStringExtra("value"));
            } else if (requestCode == 102) {
                tvCity.setText(data.getStringExtra("city"));
            } else if (requestCode == 103) {
                tvEmail.setText(data.getStringExtra("value"));
            } else if (requestCode == 104) {
                tvTel.setText(data.getStringExtra("value"));
            }
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        dlgLoad.dismissDialog();
        if (response.getResCode().equals("0")) {
            if (response instanceof CalligraphyResponse) {
                updateView(((CalligraphyResponse) response).getRepBody());
            }else{
                ToastUtil.show("信息提交成功");
                finish();
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }
}
