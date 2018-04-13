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

public class RegistActivity extends BaseActivity {
	private EditText etNumb;

    private EditText etPwd;

    private EditText etConfirm;

    private String pwd,numb;
    
    @Override
    protected void onCreate(Bundle arg0) {
    	// TODO Auto-generated method stub
    	super.onCreate(arg0);
    	setContentView(R.layout.fragment_regist);
    	init();
    }
    
    private void init()
    {
    	setTopTitle("用户注册");

        etNumb = (EditText) findViewById(R.id.et_login_email);
        etPwd = (EditText) findViewById(R.id.et_login_pwd);
        // etConfirm = (EditText) findViewById(R.id.et_regist_confirm);

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.tv_regist_login).setOnClickListener(this);
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
            case R.id.tv_regist_login:
            	Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
            	startActivity(intent);
                break;
            case R.id.btn_regist:
                checkSubmit();
                break;
        }
    }

    public void checkSubmit()
    {
        pwd = etPwd.getText().toString();
        numb = etNumb.getText().toString();
        if (TextUtils.isEmpty(numb) || TextUtils.isEmpty(pwd))
        {
            ToastUtil.show("请将信息填写完整！");
        }
        else if (pwd.length() < 6 || pwd.length() > 20)
        {
            ToastUtil.show("密码为6～20位数！");
        }
        else
        {
//            dlgLoad.loading();
//            RequestManager.request(this,new RegistParams(numb), BaseResponse.class, this,Constant.URL);
        	Intent intent = new Intent(RegistActivity.this,VerifyActivity.class);
            Bundle bundle =new Bundle();
            bundle.putString("pwd", pwd);
            bundle.putString("numb", numb);
            bundle.putString("boolean", "regist");
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
            ToastUtil.show("验证码在短信中");
            Intent intent = new Intent(RegistActivity.this,VerifyActivity.class);
            Bundle bundle =new Bundle();
            bundle.putString("pwd", pwd);
            bundle.putString("numb", numb);
            bundle.putString("boolean", "regist");
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
