package com.easier.writepre.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MyInfoActivity;
import com.easier.writepre.utils.ChineseOrEnglishTextWatcher;

/**
 * 编辑姓名
 * @author zhaomaohan
 *
 */
public class EditRealNameActivity extends BaseActivity implements OnClickListener {

	private EditText et_name;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_real_name);
		init();
	}
	private void init() {
		setTopTitle("编辑姓名");
		setTopRightTxt("完成");
		
		et_name = (EditText) findViewById(R.id.et_name);
		et_name.addTextChangedListener(new ChineseOrEnglishTextWatcher(et_name,8));
		Intent intent = getIntent();
		String oldText = intent.getStringExtra(MyInfoActivity.OLD_REALNAME);
		et_name.setText(oldText);
		if(!TextUtils.isEmpty(oldText)){
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
		intent.putExtra("real_name",et_name.getText().toString());
		setResult(resultCode, intent);
		finish();
	}
}
