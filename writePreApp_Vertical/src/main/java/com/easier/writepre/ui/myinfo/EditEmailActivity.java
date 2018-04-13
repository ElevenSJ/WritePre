package com.easier.writepre.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response.Listener;
import com.easier.writepre.R;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.UpdateUserNickPatams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MyInfoActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;


public class EditEmailActivity extends BaseActivity{

	private EditText et_email;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_email);
		init();
	}

	private void init() {
		setTopTitle("编辑邮箱");
		setTopRightTxt("完成");

		et_email = (EditText) findViewById(R.id.et_email);
		
		Intent intent = getIntent();
		String oldText = intent.getStringExtra(MyInfoActivity.OLD_EMAIL);
		et_email.setText(oldText);
		et_email.setSelection(oldText.length());
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	@Override
	public void onTopRightTxtClick(View view) {
		setResultFinish(RESULT_OK);
	}
	@Override
	public void onTopLeftClick(View view) {
		setResultFinish(RESULT_CANCELED);
	}
	
	private void setResultFinish(int resultCode) {
		Intent intent = new Intent();
		intent.putExtra("email", et_email.getText().toString());
		setResult(resultCode, intent);
		finish();
	}
}
