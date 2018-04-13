package com.easier.writepre.ui.myinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.BindTelParams;
import com.easier.writepre.param.RegistParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.NetLoadingDailog;
import com.google.gson.Gson;

/**
 * 更换手机号
 * 
 * @author zhaomaohan
 * 
 */
public class EditTelActivity extends BaseActivity {

	protected NetLoadingDailog loadingDlg;

	private String numb, proving;

	private String flag;

	private String jusdge = "";

	private EditText et_tel;// 手机号输入框

	private EditText et_verifycode;// 验证码输入框

	private TextView btn_get_verifycode;// 获取验证码按钮，以及倒计时

	private Button btn_bind;// 绑定按钮

	private int nub = 60;
	Handler handler = new Handler();
	private Runnable runnable;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_tel);
		initView();
	}

	private void initView() {
		setTopTitle("绑定手机号");

		loadingDlg = new NetLoadingDailog(this);
		// 加载布局控件
		et_tel = (EditText) findViewById(R.id.et_tel);
		et_verifycode = (EditText) findViewById(R.id.et_verifycode);
		btn_get_verifycode = (TextView) findViewById(R.id.btn_get_verifycode);
		btn_bind = (Button) findViewById(R.id.btn_bind);

		registerBroadcastReceiver();
	}

	private void registerBroadcastReceiver() {
		// 注册广播
		IntentFilter myIntentFilter2 = new IntentFilter();
		myIntentFilter2.addAction("ACTION_NAME");
		this.registerReceiver(nBroadcastReceiver, myIntentFilter2);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 销毁广播
		this.unregisterReceiver(nBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_get_verifycode:
			numb = et_tel.getText().toString();
			if (TextUtils.isEmpty(numb.trim())) {
				ToastUtil.show("请输入手机号！");
			} else if (numb.length() != 11) {
				ToastUtil.show("请输入正确的手机号！");
			} else {
				getVerifyCode();
			}

			break;
		case R.id.btn_bind:
			checkSubmit();
			break;
		default:
			break;
		}
	}

	private void checkSubmit() {
		numb = et_tel.getText().toString();
		proving = et_verifycode.getText().toString();
		if (TextUtils.isEmpty(numb.trim())) {
			ToastUtil.show("请输入手机号！");
		} else if (TextUtils.isEmpty(proving.trim())) {
			ToastUtil.show("请输入验证码！");
		} else {
			loadingDlg.loading();
			RequestManager.request(this, new BindTelParams(numb, proving), BaseResponse.class, this, Constant.URL);
		}
	}

	private void getVerifyCode() {
		// TODO Auto-generated method stub
		runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (nub > 0) {
					handler.postDelayed(this, 1000);
					nub--;

					Intent intent = new Intent("ACTION_NAME");
					intent.putExtra("time", nub);
					// 发送广播
					sendBroadcast(intent);
				}
			}

		};

		// proving = et_verifycode.getText().toString();
		// 如果验证码倒计时==“获得验证码时”或者“重新获取”时
		if (btn_get_verifycode.getText().toString().equals("获取验证码")
				|| btn_get_verifycode.getText().toString().equals("重新获取"))
		// .equals(getString(R.string.regist_verify_get)))
		{
			loadingDlg.loading();
			RequestManager.request(this, new RegistParams(numb), BaseResponse.class, this, Constant.URL);
			jusdge = "get";// jusdge设为get,在onResponse里进行判断
			flag = "bind";
		}
	}

	private BroadcastReceiver nBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("ACTION_NAME")) {
				int nub = intent.getExtras().getInt("time");
				if (nub > 0) {
					btn_get_verifycode.setText(nub + "");
				} else {
					// regist_verify_get=重新获取
					btn_get_verifycode.setText(getString(R.string.regist_verify_get));
				}
			}
		}
	};

	@Override
	public void onResponse(BaseResponse response) {
		// 隐藏Dialog();
		loadingDlg.dismissDialog();
		if ("0".equals(response.getResCode())) {
			if (jusdge.equals("get")) {
				ToastUtil.show("验证码在短信中");
				nub = 60;
				btn_get_verifycode.setText(nub + "");
				handler.postDelayed(runnable, 1000);
				jusdge = "";
				et_verifycode.setText("");
			} else {
				// ToastUtil.show(response.getResMsg());
				// ToastUtil.show("绑定成功");
				// 成功后把手机号传到MyInfo
				LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
				loginEntity.setTel(numb);
				SPUtils.instance().put(SPUtils.LOGIN_DATA, new Gson().toJson(loginEntity));
				Intent intent2 = new Intent();
				numb = et_tel.getText().toString();
				intent2.putExtra("tel", numb);
				// intent2.putExtra("isSuccess",true);
				setResult(RESULT_OK, intent2);
				finish();
			}
		} else {
			ToastUtil.show(response.getResMsg());
			// ToastUtil.show("请输入正确的手机号和验证码");
		}

	}
}
