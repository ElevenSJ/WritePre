package com.easier.writepre.ui;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.entity.MarkNo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.MarkParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.MarkResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyGridLayout;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 圈子标签选择
 * 
 */
public class CircleChooseTagActivity extends BaseActivity {


	public final int REQUEST_MARK_SUCCESS = 0;
	public final int REQUEST_ERROR = -1;

	private TextView tvTagNum;
	private TextView tvTagInfo;
	private MyGridLayout layoutTag;
	private MyGridLayout layoutHotTag;
	private Button btAdd;
	private EditText etTag;

	private ArrayList<MarkNo> tagList = new ArrayList<MarkNo>();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			dlgLoad.dismissDialog();
			switch (msg.what) {
			case REQUEST_MARK_SUCCESS:
				MarkResponse response = (MarkResponse) msg.obj;
				if (response != null) {
					updateMarkLayout(response.getRepBody());
				}
				break;
			case REQUEST_ERROR:
				BaseResponse baseResponse = (BaseResponse) msg.obj;
				if (baseResponse != null) {
					ToastUtil.show(baseResponse.getResMsg());
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle_tag_choose);
		tagList = (ArrayList<MarkNo>) getIntent().getSerializableExtra(CircleCreatActivity.ALL_DATA);
		initView();
		updateTag();
		getMark();
	}

	protected void updateMarkLayout(MarkResponse.Repbody repBody) {
		if (repBody == null) {
			return;
		}
		if (repBody.getList() == null || repBody.getList().isEmpty()) {
			return;
		}
		for (int i = 0; i < repBody.getList().size(); i++) {
			View markView = getLayoutInflater().inflate(R.layout.circle_tag_txt_item, null);
			TextView name = (TextView) markView.findViewById(R.id.item_name);
			markView.setTag(repBody.getList().get(i));
			name.setTag(repBody.getList().get(i));
			AutoUtils.autoSize(markView);
			layoutHotTag.addView(markView);
			name.setBackgroundColor(Color.parseColor("#" + repBody.getList().get(i).getBgcolor()));
			name.setText(repBody.getList().get(i).getTitle());
			name.setOnClickListener(this);
		}

	}

	private void getMark() {
		dlgLoad.loading();
		RequestManager.request(this,new MarkParams(), MarkResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}

	private void initView() {
		setTopTitle("圈子标签");
		setTopRightTxt("完成");

		tvTagNum = (TextView) findViewById(R.id.txt_tag_num);
		tvTagInfo = (TextView) findViewById(R.id.txt_tag_info);
		layoutTag = (MyGridLayout) findViewById(R.id.layout_tag);
		layoutHotTag = (MyGridLayout) findViewById(R.id.layout_hot_tag);

		btAdd = (Button) findViewById(R.id.bt_add);
		etTag = (EditText) findViewById(R.id.et_tag);

		btAdd.setOnClickListener(this);
	}

	@Override
	public void onTopRightTxtClick(View view) {
//		if (tagList.isEmpty()) {
//			ToastUtil.show("请选择标签");
//			return;
//		} else {
			Intent intent = new Intent();
			intent.putExtra(CircleCreatActivity.SELECTED_VALUE, tagList);
			setResult(CircleCreatActivity.DATA_RESULT_CODE, intent);
			finish();

//		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.bt_add:

			break;
		case R.id.item_name:
			setTag(v);
			break;
		case R.id.img_delete:
		case R.id.layout:
			tagList.remove((MarkNo) v.getTag());
			updateTag();
			break;
		default:
			break;
		}

	}

	private void setTag(View v) {
		if (tagList.size() >= 6) {
			ToastUtil.show("只能添加6个标签");
			return;
		}
		MarkNo markNo = (MarkNo) v.getTag();
		for (int i = 0; i < tagList.size(); i++) {
			if (tagList.get(i).getNo() == markNo.getNo()) {
				ToastUtil.show("已选择"+markNo.getTitle()+"标签");
				return;
			}
		}
		tagList.add(markNo);
		updateTag();
	}

	private void updateTag() {
		tvTagNum.setText(tagList.size() + "/6");
		if (tagList.size() != 0) {
			tvTagInfo.setVisibility(View.GONE);
		} else {
			tvTagInfo.setVisibility(View.VISIBLE);
		}
		layoutTag.removeAllViews();
		for (int i = 0; i < tagList.size(); i++) {
			View markView = getLayoutInflater().inflate(R.layout.circle_tag_txt_item, null);
			ImageView imgDelete = (ImageView) markView.findViewById(R.id.img_delete);
			TextView name = (TextView) markView.findViewById(R.id.item_name);
			markView.setTag(tagList.get(i));
			AutoUtils.autoSize(markView);
			layoutTag.addView(markView);
			name.setTag(tagList.get(i));
			name.setBackgroundColor(Color.parseColor("#" + tagList.get(i).getBgcolor()));
			name.setText(tagList.get(i).getTitle());
			markView.setOnClickListener(this);
			imgDelete.setTag(tagList.get(i));
			imgDelete.setVisibility(View.VISIBLE);
			imgDelete.setOnClickListener(this);
		}

	}

	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof MarkResponse) {
				Message message = new Message();
				message.what = REQUEST_MARK_SUCCESS;
				message.obj = response;
				handler.sendMessage(message);
			}
		} else {
			Message message = new Message();
			message.what = REQUEST_ERROR;
			message.obj = response;
			handler.sendMessage(message);
		}

	}
}
