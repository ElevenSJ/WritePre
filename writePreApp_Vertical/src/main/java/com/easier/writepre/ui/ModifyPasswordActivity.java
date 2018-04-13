package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.EditPwdParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.ToastUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ModifyPasswordActivity extends BaseActivity {
	private EditText et_password_old;
	private EditText et_password_new;
	private Button btn_ok;
	private EditPwdParams params;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_modify_password);
		initView();
	}

	private void initView() {
		setTopTitle("修改密码");
		
		//加载控件
		et_password_old = (EditText) findViewById(R.id.et_password_old);
		et_password_new = (EditText) findViewById(R.id.et_password_new);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		
		//控件添加监听事件
		btn_ok.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			checkSubmit();
			break;

		default:
			break;
		}
	}

	private void checkSubmit() {
		// TODO Auto-generated method stub
		String oldPwd = et_password_old.getText().toString();
		String newPwd = et_password_new.getText().toString();
		
		if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd)) {
			ToastUtil.show("请将需要修改密码的信息填写完整。");
		} else if (newPwd.length() < 6 || oldPwd.length() < 6) {
			ToastUtil.show("密码必须由6-20位字符组成.");
		} else {
			dlgLoad.loading();
			params = new EditPwdParams(oldPwd, newPwd);
			RequestManager.request(this,params, BaseResponse.class, this, Constant.URL);
		}
	}
	@Override
	public void onResponse(BaseResponse response) {
		dlgLoad.dismissDialog();
		if ("0".equals(response.getResCode())) {
			if (params instanceof EditPwdParams) {
				finish();
				ToastUtil.show(response.getResMsg());
			}
		} else{
			ToastUtil.show(response.getResMsg());
		}
	}
}
