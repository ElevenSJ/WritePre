package com.easier.writepre.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.utils.ToastUtil;


/**
 * 执行某操作签的确认提醒的对话框模板
 * @author zhaomaohan
 *
 */
public class ConfirmHintDialog extends Dialog implements android.view.View.OnClickListener {
	TextView tv_title;
	TextView tv_confirm;
	TextView tv_cancel;
	Context context;
	String title;
	String confirm;
	String cancle;
	ConfirmHintDialogListener confirmHintDialogListener;
	public ConfirmHintDialog(Context context,int theme){
		super(context,theme);
	}

	public ConfirmHintDialog(Context context,int theme,String title,ConfirmHintDialogListener confirmHintDialogListener) {
		this(context, theme, title, null, null, confirmHintDialogListener);
	}
	public ConfirmHintDialog(Context context,int theme,String title,String confirm,String cancle,ConfirmHintDialogListener confirmHintDialogListener) {
		super(context,theme);
		this.context = context;
		this.title = title;
		this.confirm = confirm;
		this.cancle = cancle;
		this.confirmHintDialogListener = confirmHintDialogListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_confirm_hint);
		
		initView();
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);
		
		tv_confirm = (TextView) findViewById(R.id.tv_confirm);
		if (!TextUtils.isEmpty(confirm)) {
			tv_confirm.setText(confirm);
		}
		
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		if (!TextUtils.isEmpty(cancle)) {
			tv_cancel.setText(cancle);
		}
		
		tv_confirm.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		confirmHintDialogListener.OnClick(v);
		dismiss();
	}
	
	public interface ConfirmHintDialogListener {
		  public void OnClick(View view);
	}
}
