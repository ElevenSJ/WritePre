package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.entity.CircleDetail;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleMemberClearCodeParams;
import com.easier.writepre.param.CircleMemberSetCodeParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMemberClearCodeResponse;
import com.easier.writepre.response.CircleMemberSetCodeResponse;
import com.easier.writepre.utils.ChineseOrEnglishTextWatcher;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

/**
 * 圈子设置口令
 * @author zhaomaohan
 *
 */
public class CircleMemberSetCodeActivity extends BaseActivity implements OnClickListener {

	private EditText et_name;
	private String circleId;
	private String circleName;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_circle_member_set_code);
		init();
	}
	private void init() {
		setTopTitle("编辑圈子口令");
		setTopRightTxt("完成");

		et_name = (EditText) findViewById(R.id.et_name);
//		InputFilter[] filters = {new MyInputFilter(8,16)};
//		et_name.setFilters(filters);
		et_name.addTextChangedListener(new ChineseOrEnglishTextWatcher(et_name,6));
		Intent intent = getIntent();
		String oldText = intent.getStringExtra(CircleDetailActivity.OLD_CODE);
		circleId = intent.getStringExtra("circle_id");
		et_name.setText(oldText);
//		et_name.setSelection(oldText.length());
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	@Override
	public void onTopRightTxtClick(View view) {
		if(!TextUtils.isEmpty(et_name.getText().toString())){
			RequestManager.request(this, new CircleMemberSetCodeParams(circleId,et_name.getText().toString()), CircleMemberSetCodeResponse.class, this,
					SPUtils.instance().getSocialPropEntity().getApp_socail_server());
		}else{
			RequestManager.request(this, new CircleMemberClearCodeParams(circleId), CircleMemberClearCodeResponse.class, this,
					SPUtils.instance().getSocialPropEntity().getApp_socail_server());
		}

//		setResultFinish(RESULT_OK);
	}
	@Override
	public void onTopLeftClick(View view) {
		setResultFinish(RESULT_CANCELED);
	}
	private void setResultFinish(int resultCode) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra(CircleDetailActivity.CIRCLE_CODE,et_name.getText().toString());
		setResult(resultCode, intent);
		finish();
	}

	@Override
	public void onResponse(BaseResponse response) {
//		super.onResponse(response);
		if ("0".equals(response.getResCode())){
			if(response instanceof CircleMemberSetCodeResponse){
				setResultFinish(RESULT_OK);
			}else if(response instanceof  CircleMemberClearCodeResponse){
				setResultFinish(RESULT_OK);
			}
		}else{
			ToastUtil.show(response.getResMsg());
		}

	}
}
