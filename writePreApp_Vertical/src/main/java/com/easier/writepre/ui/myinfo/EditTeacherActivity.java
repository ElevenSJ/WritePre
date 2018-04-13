package com.easier.writepre.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MyInfoActivity;
import com.easier.writepre.ui.PkSignUpActivity;

public class EditTeacherActivity extends BaseActivity {
	private EditText et_name;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_teacher);
		init();
	}
	private void init() {
		setTopTitle("编辑指导老师");
		setTopRightTxt("完成");
		
		et_name = (EditText) findViewById(R.id.et_name);
		
		Intent intent = getIntent();
		String oldText = intent.getStringExtra(PkSignUpActivity.OLD_TEACHER);
		if(oldText != null){
			et_name.setText(oldText);
			et_name.setSelection(oldText.length());
		}
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
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("teacher",et_name.getText().toString());
		setResult(resultCode, intent);
		finish();
	}
}
