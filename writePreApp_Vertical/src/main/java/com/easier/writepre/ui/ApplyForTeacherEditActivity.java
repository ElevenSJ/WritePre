package com.easier.writepre.ui;

import com.easier.writepre.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * 申请成为老师编辑
 * 
 */
public class ApplyForTeacherEditActivity extends BaseActivity {

	/**
	 * title
	 */
	public static final String TITLE_VALUE = "title_value";
	/**
	 * 已选的item hint
	 */
	public static final String HINT_VALUE = "hint_value";
	/**
	 * 已选的item值键值
	 */
	public static final String SELECTED_VALUE = "selected_value";
	
	/**
	 * 已选的item长度
	 */
	public static final String MAX_LENGTH = "max_length";

	private String titleValue = "";
	
	// 选中的item名称
	private String selectedValue = "";

	private String textHint = "";
	
	private int maxLength = 100;

	private EditText etName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_edit);
		titleValue = getIntent().getStringExtra(TITLE_VALUE);
		selectedValue = getIntent().getStringExtra(SELECTED_VALUE);
		textHint = getIntent().getStringExtra(HINT_VALUE);
		maxLength = getIntent().getIntExtra(MAX_LENGTH,100);
		initView();
	}

	private void initView() {
		setTopTitle(TextUtils.isEmpty(titleValue)?"编辑":titleValue);

		etName = (EditText) findViewById(R.id.et_input_name);
		etName.setHint(textHint);

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
				if (s.toString().length()>maxLength) {
					etName.setText(s.toString().substring(0, maxLength));
				}
				Selection.setSelection(s, etName.getText().toString().length());
			}
		});
		etName.setText(selectedValue);

	}

	@Override
	public void onTopRightTxtClick(View view) {
		Intent intent = new Intent();
		intent.putExtra(SELECTED_VALUE, etName.getText().toString());
		setResult(RESULT_OK,intent);
		finish();
	}


}
