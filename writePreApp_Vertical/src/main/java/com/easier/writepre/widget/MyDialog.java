package com.easier.writepre.widget;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.entity.ActionItem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyDialog extends Dialog {

	private Context mContext;

	private OnItemOnClickListener mItemOnClickListener;
	private LinearLayout view;
	private TextView txtView;

	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();

	public MyDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		setCanceledOutside(true);
		view = new LinearLayout(context);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		view.setOrientation(LinearLayout.VERTICAL);
	}

	public void addAction(final ActionItem action) {
		if (action != null) {
			mActionItems.add(action);
			if (!mActionItems.isEmpty() && mActionItems.size() > 1) {
				View lineView = getLayoutInflater().inflate(
						R.layout.dialog_line, null);
				view.addView(lineView);
			}
			View v = getLayoutInflater().inflate(R.layout.my_dialog_item, null);
			Button item = (Button) v.findViewById(R.id.item_bt);
			item.setText(action.mTitle);
			item.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					mItemOnClickListener.onItemClick(action);
				}
			});
			view.addView(v);
		}
	}

	public void setCanceledOutside(boolean isTrue) {
		setCanceledOnTouchOutside(isTrue);
	}

	public void show() {
		super.show();
		Window dialogWindow = getWindow();
		dialogWindow.setContentView(view);
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.5
		dialogWindow.setAttributes(p);
	}

	public void show(int layoutId) {
		super.show();
		Window dialogWindow = getWindow();
		dialogWindow.setContentView(layoutId);
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.7
		dialogWindow.setAttributes(p);
	}

	public void show(int layoutId, int... ids) {
		super.show();
		Window dialogWindow = getWindow();
		dialogWindow.setContentView(layoutId);
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.7
		dialogWindow.setAttributes(p);
		for (int i = 0; i < ids.length; i++) {
			dialogWindow.findViewById(ids[i]).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dismiss();
							mItemOnClickListener.onItemClick(new ActionItem(
									mContext, v.getId()));
						}
					});
		}
	}

	public void cleanAction() {
		if (!mActionItems.isEmpty()) {
			mActionItems.clear();
			view.removeAllViews();
		}
	}

	public ActionItem getAction(int position) {
		if (position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}

	public void setItemOnClickListener(
			OnItemOnClickListener onItemOnClickListener) {
		this.mItemOnClickListener = onItemOnClickListener;
	}

	public static interface OnItemOnClickListener {
		public void onItemClick(ActionItem item);
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public void dismiss() {
		if (txtView != null) {
			txtView.setBackgroundColor(mContext.getResources().getColor(
					R.color.transparent));
		}
		super.dismiss();
	}

	public void setTextView(View textView) {
		if (textView != null && textView instanceof TextView) {
			this.txtView = (TextView) textView;
		}

	}
}
