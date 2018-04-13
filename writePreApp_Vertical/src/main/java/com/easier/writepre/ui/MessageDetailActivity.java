package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.entity.PushMessageEntity;
import com.easier.writepre.utils.ToastUtil;
import com.sj.autolayout.utils.DateKit;

import android.os.Bundle;
import android.widget.TextView;

public class MessageDetailActivity extends BaseActivity {
	private PushMessageEntity entity;
	private TextView tvTitle;
	private TextView tvDesc;
	private TextView tvTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);
		entity = (PushMessageEntity) getIntent().getSerializableExtra("message");
		initView();
		initData();
	}

	private void initData() {
		if (entity == null) {
			ToastUtil.show("获取消息详情异常");
		} else {
			tvTitle.setText(entity.getTitle());
			tvDesc.setText(entity.getDesc());
			tvTime.setText(DateKit.timeFormat(entity.getCtime()));
		}
	}

	private void initView() {
		setTopTitle("通知详情");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvTime = (TextView) findViewById(R.id.tv_creat_time);
	}

}
