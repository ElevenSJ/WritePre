package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.RegistParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.ToastUtil;

public class ForgetPasswordActivity extends BaseActivity {
	private EditText etNumb, etPwd;
	
    private String pwd,numb;
    
    @Override
    protected void onCreate(Bundle arg0) {
    	// TODO Auto-generated method stub
    	super.onCreate(arg0);
    	setContentView(R.layout.fragment_forget);
    	init();
    }
    private void init()
    {
    	setTopTitle("忘记密码");
        etNumb = (EditText) findViewById(R.id.et_login_email);
        etPwd = (EditText) findViewById(R.id.et_login_pwd);
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.btn_regist).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_back:
            	onTopLeftClick(v);
                break;
            // case R.id.tv_back_login:
            // context.jump(this, context.fragLogin);
            // break;
            case R.id.btn_regist:
                checkSubmit();
                break;
        }
    }

    private void checkSubmit()
    {
        numb = etNumb.getText().toString();
        pwd = etPwd.getText().toString();
        // if (TextUtils.isEmpty(email)) {
        // ToastUtil.show("请输入邮箱");
        // } else if (BaseUtil.checkEmail(email)) {
        // loadingDlg.loading();
        // request(new ForgetParams(email), BaseResponse.class, this);
        // } else {
        // ToastUtil.show("您输入的邮箱格式不正确");
        // }
        if (TextUtils.isEmpty(numb) || TextUtils.isEmpty(pwd))
        {
            ToastUtil.show("请将信息填写完整！");
        }
        else if (pwd.length() < 6 || pwd.length() > 20)
        {
            ToastUtil.show("密码为6～20位数！");
        }
        else if (numb.length() < 11 || numb.length() > 11)
        {
            ToastUtil.show("手机号码不正确");
        }
        else
        {
//            dlgLoad.loading();
//            RequestManager.request(this,new RegistParams(numb), BaseResponse.class, this,Constant.URL);
        	 Intent intent = new Intent(ForgetPasswordActivity.this,VerifyActivity.class);
             Bundle bundle = new Bundle();
             bundle.putString("pwd", pwd);
             bundle.putString("numb", numb);
             bundle.putString("boolean", "forget");
             intent.putExtras(bundle);
             startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onResponse(BaseResponse resp)
    {
        dlgLoad.dismissDialog();

        if ("0".equals(resp.getResCode()))
        {
            // ToastUtil.show("修改连接已发送到您的邮箱，请查收");
            // context.jump(this, context.fragLogin);

            ToastUtil.show("验证码在短信中");
            Intent intent = new Intent(ForgetPasswordActivity.this,VerifyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("pwd", pwd);
            bundle.putString("numb", numb);
            bundle.putString("boolean", "forget");
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }
        else
        {
            ToastUtil.show(resp.getResMsg());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
		case 1:
			if(resultCode == RESULT_OK){
	    		finish();
	    	}
			break;

		default:
			break;
		}
    	
    }
}
