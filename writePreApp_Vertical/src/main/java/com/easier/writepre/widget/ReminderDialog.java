package com.easier.writepre.widget;

import android.app.Dialog;
import android.content.Context;
import android.media.Ringtone;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easier.writepre.R;

public class ReminderDialog extends Dialog implements
		android.view.View.OnClickListener {

	private TextView tv_ok;

	private TextView tv_title;

	private String name, remark;

	private Ringtone r;

	public ReminderDialog(Context context) {
		super(context);

	}

	public ReminderDialog(Context context, Ringtone r, int theme, String name,
			String remark) {
		super(context, theme);

		this.r = r;
		this.name = name;
		this.remark = remark;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.reminder_dialog);

		tv_ok = (TextView) findViewById(R.id.tv_ok);
		tv_title = (TextView) findViewById(R.id.tv_title);

		tv_title.setText("课程: " + name + " " + remark + " 时间到了");

		tv_ok.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {

		if (r != null) {
			r.stop();
		}
		this.dismiss();

	}

}
