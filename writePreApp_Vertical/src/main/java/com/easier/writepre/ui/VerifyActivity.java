package com.easier.writepre.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.RegistParams;
import com.easier.writepre.param.VerifyForgetParams;
import com.easier.writepre.param.VerifyRegistParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.LoginResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.google.gson.Gson;

public class VerifyActivity extends BaseActivity {
	private String pwd, numb, proving;

	private String flag;

	private String jusdge = "";

	private EditText verify;

	private TextView numphone, attaint;

	private int nub = 60;
	Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (nub > 0) {
				handler.postDelayed(this, 1000);
				nub--;
				Intent intent = new Intent("ACTION_NAME");
				intent.putExtra("time", nub);
				sendBroadcast(intent);
			}
		}
	};
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("ACTION_NAME")) {
				int nub = intent.getExtras().getInt("time");
				if (nub > 0) {
					attaint.setText(nub + "");
				} else {
					// 重新获取
					attaint.setText(getString(R.string.regist_verify_get));

				}
			}
		}

	};
	protected void onCreate(Bundle arg0) {
    	// TODO Auto-generated method stub
    	super.onCreate(arg0);
    	setContentView(R.layout.fragment_verify);
    	init();
    }

	private void init() {
		setTopTitle("验证");
		// 注册广播
		Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        
		pwd = bundle.getString("pwd");
		numb = bundle.getString("numb");
		flag = bundle.getString("boolean");
		findViewById(R.id.btn_regist).setOnClickListener(this);
		numphone = (TextView) findViewById(R.id.regist_verify_phonenumb);
		attaint = (TextView) findViewById(R.id.tv_get);
		attaint.setOnClickListener(this);
		verify = (EditText) findViewById(R.id.tv_verify_pwd);
		numphone.setText(numb);
		registerBoradcastReceiver();
	}

	public void registerBoradcastReceiver() {
		// 注册广播
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("ACTION_NAME");
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	public void onDestroy() {
		// 销毁广播
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_get:
			if (attaint.getText().toString().equals(getString(R.string.regist_verify_get))) {
				dlgLoad.loading();
				RequestManager.request(this,new RegistParams(numb), BaseResponse.class, this, Constant.URL);
				jusdge = "get";
			}
			break;
		case R.id.btn_regist:
			checkSubmit();
			break;
		case R.id.img_back:
			onTopLeftClick(v);
			break;
		default:
			break;
		}

	}

	private void checkSubmit() {
		proving = verify.getText().toString();
		if (TextUtils.isEmpty(proving) || TextUtils.isEmpty(proving)) {
			ToastUtil.show("请输入验证码！");
		} else if (flag.equals("regist")) {
			dlgLoad.loading();
			RequestManager.request(this,new VerifyRegistParams(numb, pwd, proving), BaseResponse.class, this, Constant.URL);

		} else if (flag.equals("forget")) {
			dlgLoad.loading();
			RequestManager.request(this,new VerifyForgetParams(numb, proving, pwd), BaseResponse.class, this, Constant.URL);
		}
	}

	@Override
	public void onResponse(BaseResponse resp) {
		dlgLoad.dismissDialog();
		if ("0".equals(resp.getResCode())) {
			if (jusdge.equals("get")) {
				ToastUtil.show("验证码在短信中");
				nub = 60;
				attaint.setText(nub + "");
				handler.postDelayed(runnable, 1000);
				jusdge = "";
				verify.setText("");
				findViewById(R.id.receive_phone_layout).setVisibility(View.VISIBLE);
			} else if (flag.equals("regist")) {
				ToastUtil.show("注册成功");
				SPUtils.instance().put(SPUtils.LOGIN_NAME, numb);
				SPUtils.instance().put(SPUtils.LOGIN_PWD, pwd);
				Intent intent = new Intent(this,LoginActivity.class);
				this.startActivity(intent);
				finish();
//				dlgLoad.loading();
//				RequestManager.request(this,new LoginParams(numb, pwd), LoginResponse.class, this, Constant.URL);
//				flag = "login";

			} else if (flag.equals("forget")) {
				ToastUtil.show("密码重置成功");
				setResult(RESULT_OK);
				finish();
			} else if (flag.equals("login")) {
				if (resp instanceof LoginResponse) {
					SPUtils.instance().put(SPUtils.IS_LOGIN, true);
					SPUtils.instance().put(SPUtils.LOGIN_DATA, new Gson().toJson(((LoginResponse) resp).getRepBody()));
					setResult(Activity.RESULT_OK);
					Intent intent = new Intent(this,LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					this.startActivity(intent);
					finish();
				}
			}

		} else {
			ToastUtil.show(resp.getResMsg());
			if (resp instanceof LoginResponse) {
				Intent intent = new Intent(this,LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(intent);
				finish();
			}
		}
	}

}
