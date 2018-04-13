package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.RegistParams;
import com.easier.writepre.param.VerifyForgetParams;
import com.easier.writepre.param.VerifyRegistParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.NetWorkUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 注册或重置密码页面
 * 
 * @author zhoulu
 * 
 */
public class RegistOrResetActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
	private EditText etNumb, etVerify_pwd, et_newpwd, et_newpwd_again,et_login_psw;
	private TextView tv_get, tv_tips;
	private String strPhoneNumber, strVerifyNumber, strPwd;
	private LinearLayout ll_get_verify_layout, ll_set_pwd_layout;
	private CheckBox cb_useragreement,cb_password_visible;
	private Button btn_forgetnext;
	private int flag = 0; // 0注册 1忘记密码
	private boolean isFirst = true;
	private CountDownTimer timer = new CountDownTimer(60000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) {
			tv_get.setText((millisUntilFinished / 1000) + "s");
			tv_get.setTextColor(getResources().getColor(R.color.social_red));
		}

		@Override
		public void onFinish() {
			tv_get.setEnabled(true);
			tv_get.setText(R.string.regist_verify_get);
			tv_get.setTextColor(getResources().getColor(R.color.black));
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_regist_or_reset);
		init();
	}

	private void init() {
		btn_forgetnext = (Button) findViewById(R.id.btn_forgetnext);
		flag = getIntent().getIntExtra("type", 0);
		if (flag == 0) {
			setTopTitle("用户注册");
			findViewById(R.id.regist_agreement_layout).setVisibility(
					View.VISIBLE);
			findViewById(R.id.rl_password).setVisibility(
					View.VISIBLE);
			btn_forgetnext.setText("登录");
		} else {
			setTopTitle("忘记密码");
			findViewById(R.id.regist_agreement_layout).setVisibility(View.GONE);
			findViewById(R.id.rl_password).setVisibility(
					View.GONE);
			btn_forgetnext.setText("下一步");
		}
		ll_get_verify_layout = (LinearLayout) findViewById(R.id.get_verify_layout);
		etNumb = (EditText) findViewById(R.id.et_login_email);
		et_login_psw = (EditText) findViewById(R.id.et_login_psw);
		cb_password_visible = (CheckBox) findViewById(R.id.cb_password_visible);

		cb_password_visible.setOnCheckedChangeListener(this);

		etVerify_pwd = (EditText) findViewById(R.id.et_verify_pwd);
		tv_get = (TextView) findViewById(R.id.tv_get);
		tv_tips = (TextView) findViewById(R.id.tips);
		ll_set_pwd_layout = (LinearLayout) findViewById(R.id.set_pwd_layout);
		et_newpwd = (EditText) findViewById(R.id.et_regist_pwd);
		et_newpwd_again = (EditText) findViewById(R.id.et_regist_confirm);
		cb_useragreement = (CheckBox) findViewById(R.id.cb_agreement);
		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.tv_useragreement).setOnClickListener(this);
		tv_get.setOnClickListener(this);
		btn_forgetnext.setOnClickListener(this);
		findViewById(R.id.btn_setpwd).setOnClickListener(this);
		ll_get_verify_layout.setVisibility(View.VISIBLE);
		ll_set_pwd_layout.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			onTopLeftClick(v);
			break;
		case R.id.tv_useragreement:
			// 用户协议
			startActivity(new Intent(this, AgreementActivity.class));
			break;
		case R.id.tv_get:
			// 获取验证码
			requestGetVerifyCode();
			break;
		case R.id.btn_forgetnext:
			// 下一步
			if(btn_forgetnext.getText().equals("下一步")){
				if (check()) {
					ll_get_verify_layout.setVisibility(View.GONE);
					ll_set_pwd_layout.setVisibility(View.VISIBLE);
				}
			}else{//
				if(check()){
					strPhoneNumber = etNumb.getText().toString();
					strVerifyNumber = etVerify_pwd.getText().toString();
					strPwd = et_login_psw.getText().toString();
					requestVerifyRegist();
				}
			}

			break;
		case R.id.btn_setpwd:
			// 设置密码
			if (check()) {
				if (flag == 0) {
					strPhoneNumber = etNumb.getText().toString();
					strVerifyNumber = etVerify_pwd.getText().toString();
					strPwd = et_newpwd.getText().toString();
					requestVerifyRegist();
				} else {
					requestVerifyForgetPwd();
				}
			}
			break;
		}
	}

	/**
	 * 忘记密码 - 重置密码请求
	 */
	private void requestVerifyForgetPwd() {
		strPhoneNumber = etNumb.getText().toString();
		strVerifyNumber = etVerify_pwd.getText().toString();
		strPwd = et_newpwd.getText().toString();
		if (!NetWorkUtils.isNetworkConnected()) {
			ToastUtil.show("没有可用的网络!");
			return;
		}
		dlgLoad.loading();
		RequestManager.request(this, new VerifyForgetParams(strPhoneNumber,
				strVerifyNumber, strPwd), BaseResponse.class,
				new WritePreListener<BaseResponse>() {

					@Override
					public void onResponse(BaseResponse response) {
						dlgLoad.dismissDialog();
						if ("0".equals(response.getResCode())) {
							// TODO 密码重置成功,需要重新登录
							ToastUtil.show("密码重置成功");
							SPUtils.instance().put(SPUtils.LOGIN_NAME,
									strPhoneNumber);
							SPUtils.instance().put(SPUtils.LOGIN_PWD, strPwd);
							Intent intent = new Intent(
									RegistOrResetActivity.this,
									LoginActivity.class);
							RegistOrResetActivity.this.startActivity(intent);
							finish();
						} else {
							ToastUtil.show(response.getResMsg());
						}
					}

					@Override
					public void onResponse(String tag, BaseResponse response) {
						// TODO Auto-generated method stub
						
					}
				}, Constant.URL);
	}

	/**
	 * 注册请求
	 */
	private void requestVerifyRegist() {

		if (!NetWorkUtils.isNetworkConnected()) {
			ToastUtil.show("没有可用的网络!");
			return;
		}
		dlgLoad.loading();
		RequestManager.request(this, new VerifyRegistParams(strPhoneNumber,
				strPwd, strVerifyNumber), BaseResponse.class,
				new WritePreListener<BaseResponse>() {

					@Override
					public void onResponse(BaseResponse response) {
						dlgLoad.dismissDialog();
						if ("0".equals(response.getResCode())) {
							// TODO 注册成功,启动登录页面
							ToastUtil.show("注册成功");
							SPUtils.instance().put(SPUtils.LOGIN_NAME,
									strPhoneNumber);
							SPUtils.instance().put(SPUtils.LOGIN_PWD, strPwd);
							Intent intent = new Intent(
									RegistOrResetActivity.this,
									LoginActivity.class);
							RegistOrResetActivity.this.startActivity(intent);
							finish();
						} else {
							ToastUtil.show(response.getResMsg());
						}
					}

					@Override
					public void onResponse(String tag, BaseResponse response) {
						// TODO Auto-generated method stub
						
					}
				}, Constant.URL);
	}

	/**
	 * 验证
	 */
	private boolean check() {
		strPhoneNumber = etNumb.getText().toString();
		strVerifyNumber = etVerify_pwd.getText().toString();
		if (ll_get_verify_layout.getVisibility() != View.GONE) {
			if (TextUtils.isEmpty(strPhoneNumber)) {
				ToastUtil.show("请填写手机号码");
				return false;
			}
			if (!Utils.isPhone(strPhoneNumber)) {
				ToastUtil.show("手机号码不正确");
				return false;
			}
			if (isFirst) {
				ToastUtil.show("请先获取验证码");
				return false;
			}
			if (TextUtils.isEmpty(strVerifyNumber)) {
				ToastUtil.show("请填写验证码");
				return false;
			}
			if (strVerifyNumber.length() != 5) {
				ToastUtil.show("请填写有效的验证码");
				return false;
			}
			if (flag == 0) {
				if (!cb_useragreement.isChecked()) {
					ToastUtil.show("请勾选用户协议");
					return false;
				}
			}
		}
		if (ll_set_pwd_layout.getVisibility() != View.GONE) {
			if (TextUtils.isEmpty(et_newpwd.getText().toString())
					|| Utils.isPhone(et_newpwd_again.getText().toString())) {
				ToastUtil.show("密码不能为空");
				return false;
			}
			if (!TextUtils.equals(et_newpwd.getText().toString(),
					et_newpwd_again.getText().toString())) {
				ToastUtil.show("两次输入的密码不一致");
				return false;
			}
			if (!(et_newpwd.getText().toString().length() >= 6 && et_newpwd
					.getText().toString().length() <= 20)) {
				ToastUtil.show("密码长度为6~20位");
				return false;
			}
			if (!(et_newpwd_again.getText().toString().length() >= 6 && et_newpwd_again
					.getText().toString().length() <= 20)) {
				ToastUtil.show("密码长度为6~20位");
				return false;
			}

		}
		return true;
	}

	/**
	 * 获取验证码网络请求
	 */
	private void requestGetVerifyCode() {
		strPhoneNumber = etNumb.getText().toString();
		if (TextUtils.isEmpty(strPhoneNumber)) {
			ToastUtil.show("请填写手机号码");
			return;
		}
		if (!Utils.isPhone(strPhoneNumber)) {
			ToastUtil.show("手机号码不正确");
			return;
		}
		if (!NetWorkUtils.isNetworkConnected()) {
			ToastUtil.show("没有可用的网络!");
			return;
		}
		tv_get.setEnabled(false);
		// 启动倒计时
		timer.start();
		isFirst = false;
		tv_tips.setVisibility(View.INVISIBLE);
		RequestManager.request(this, new RegistParams(strPhoneNumber),
				BaseResponse.class, new WritePreListener<BaseResponse>() {

					@Override
					public void onResponse(BaseResponse response) {
						dlgLoad.dismissDialog();
						if ("0".equals(response.getResCode())) {
							ToastUtil.show("请在短信中查收验证码");
							tv_tips.setVisibility(View.VISIBLE);
						} else {
							ToastUtil.show(response.getResMsg());
						}
					}

					@Override
					public void onResponse(String tag, BaseResponse response) {
						// TODO Auto-generated method stub
						
					}
				}, Constant.URL);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()){
			case R.id.cb_password_visible:
//				cb_password_visible.setSelected(isChecked);
				if(isChecked){
					//如果选中，显示密码
//					ToastUtil.show(et_login_psw.getText().toString());
					et_login_psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					et_login_psw.setSelection(et_login_psw.getText().length());
				}else{
					//否则隐藏密码
					et_login_psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
					et_login_psw.setSelection(et_login_psw.getText().length());
				}
				break;
		}
	}

}
