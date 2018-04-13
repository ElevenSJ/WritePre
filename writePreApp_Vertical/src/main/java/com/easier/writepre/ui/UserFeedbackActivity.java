package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.UserFeedbackParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.ToastUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * 用户反馈
 * @author zhaomaohan
 *
 */
public class UserFeedbackActivity extends BaseActivity {
	private EditText et_content;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_feedback);
		initView();
	}

	private void initView() {
		setTopTitle("用户反馈");
		setTopRightTxt("发送");
		
		//加载控件
		et_content = (EditText) findViewById(R.id.et_content);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	@Override
	public void onTopRightTxtClick(View view) {
		if(TextUtils.isEmpty(et_content.getText())){
			ToastUtil.show("请填写反馈内容");
		}else{
			RequestManager.request(this,new UserFeedbackParams(et_content.getText().toString()), BaseResponse.class, this, Constant.URL);
		}
	}
	@Override
	public void onResponse(BaseResponse response) {
		
		if("0".equals(response.getResCode())){
			ToastUtil.show("感谢您的反馈！");
			finish();
		}else{
			ToastUtil.show(response.getResMsg());
		}
	}
}
