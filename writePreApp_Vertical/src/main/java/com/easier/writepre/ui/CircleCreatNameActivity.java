package com.easier.writepre.ui;

import com.easier.writepre.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * 新建圈子 选择参数
 * 
 */
public class CircleCreatNameActivity extends BaseActivity {

	// 选中的item名称
	private String selectedValue = "";

	private EditText etName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_name);
		selectedValue = getIntent().getStringExtra(CircleCreatActivity.SELECTED_VALUE);
		initView();
	}

	private void initView() {
		setTopTitle("编辑");

		etName = (EditText) findViewById(R.id.et_input_name);
		etName.setText(selectedValue);
		
		etName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setTopRightTxt("完成");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public void onTopRightTxtClick(View view) {
		Intent intent = new Intent();
		intent.putExtra(CircleCreatActivity.SELECTED_STRING, etName.getText().toString());
		intent.putExtra(CircleCreatActivity.SELECTED_VALUE, etName.getText().toString());
		setResult(CircleCreatActivity.DATA_RESULT_CODE, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

}
