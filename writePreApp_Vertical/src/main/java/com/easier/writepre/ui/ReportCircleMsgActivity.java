package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleReportParams;
import com.easier.writepre.param.ReportCircleMsgParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 圈帖举报页面
 * 
 * @author zhaomaohan
 *
 */
public class ReportCircleMsgActivity extends BaseActivity {

	private RadioGroup rg_report_type;// 举报类型

	private RadioButton rb_false_information;// 虚假信息

	private RadioButton rb_induced_sharing_information;// 诱导分享信息

	private RadioButton rb_copyright_infringement;// 侵犯版权

	private RadioButton rb_pornography;// 黄赌毒

	private RadioButton rb_language_attack;// 语言人身攻击

	private RadioButton rb_other;// 其他

	private String report_type = "-1";// 举报类型，接口需要

	private String topic_id;// 传过来的帖子id,也是要被举报的id

	private String circle_id;// 圈子id

	private EditText et_input_report_content;// 举报内容输入框;

	private TextView tv_input_left;

	private String report_title;

	protected int num = 140;

	private int type = 0;// 举报类型，0举报圈帖，1举报圈子

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_topic);
		init();
	}

	private void init() {
		setTopTitle("举报");
		setTopRightTxt("完成");

		et_input_report_content = (EditText) findViewById(R.id.et_input_report_content);

		tv_input_left = (TextView) findViewById(R.id.tv_input_left);

		rg_report_type = (RadioGroup) findViewById(R.id.rg_report_type);

		rb_false_information = (RadioButton) findViewById(R.id.rb_false_information);

		rb_induced_sharing_information = (RadioButton) findViewById(R.id.rb_induced_sharing_information);

		rb_copyright_infringement = (RadioButton) findViewById(R.id.rb_copyright_infringement);

		rb_pornography = (RadioButton) findViewById(R.id.rb_pornography);

		rb_language_attack = (RadioButton) findViewById(R.id.rb_language_attack);

		rb_other = (RadioButton) findViewById(R.id.rb_other);

		editTextWatcher();

		// 举报的帖子id
		Intent intent = getIntent();
		type = intent.getIntExtra("type", 0);
		topic_id = intent.getStringExtra("topic_id");
		circle_id = intent.getStringExtra("circle_id");

		// 举报类型
		rg_report_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_false_information:
					report_type = "0";
					break;
				case R.id.rb_induced_sharing_information:
					report_type = "1";
					break;
				case R.id.rb_copyright_infringement:
					report_type = "2";
					break;
				case R.id.rb_pornography:
					report_type = "3";
					break;
				case R.id.rb_language_attack:
					report_type = "4";
					break;
				case R.id.rb_other:
					report_type = "5";
					break;

				default:
					break;
				}
			}
		});

	}

	private void editTextWatcher() {
		// TODO Auto-generated method stub
		et_input_report_content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
				tv_input_left.setText("" + number);
				selectionStart = et_input_report_content.getSelectionStart();
				selectionEnd = et_input_report_content.getSelectionEnd();
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					et_input_report_content.setText(s);
					et_input_report_content.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});
	}

	@Override
	public void onResponse(BaseResponse response) {
		// TODO Auto-generated method stub
		dlgLoad.dismissDialog();

		if ("0".equals(response.getResCode())) {
			ToastUtil.show("举报成功");
			finish();
		} else {
			if (response != null) {
				ToastUtil.show(response.getResMsg());
			} else {
				ToastUtil.show("举报失败");
			}
		}

	}

	@Override
	public void onTopRightTxtClick(View view) {
		report_title = et_input_report_content.getText().toString();
		if (TextUtils.isEmpty(report_title)) {
			ToastUtil.show("请填写举报内容");
			return;
		}
		if (report_type.equals("-1")) {
			ToastUtil.show("请选择举报类型");
			return;
		}
		dlgLoad.loading();
		if (type == 0) {
			RequestManager.request(this,new ReportCircleMsgParams(circle_id, topic_id, report_title, report_type), BaseResponse.class, this,
					SPUtils.instance().getSocialPropEntity().getApp_socail_server());
		} else {
			RequestManager.request(this,new CircleReportParams(circle_id, report_title, report_type), BaseResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
		}

	}

}
