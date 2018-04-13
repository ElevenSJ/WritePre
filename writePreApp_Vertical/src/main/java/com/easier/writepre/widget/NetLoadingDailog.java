package com.easier.writepre.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.NoSwipeBackActivity;

/**
 * 只控制显示或者不显示对话框，或者单纯的显示对话框没有线程
 * 
 * @author xiayuemei
 * 
 */
public class NetLoadingDailog {

	private Dialog mDialog;
	
	private TextView progressTxt;

	private Context context;

	public NetLoadingDailog(Context mContext) {
		this.context = mContext;
		mDialog = new Dialog(context, R.style.loading_dialog);
		mDialog.setContentView(View
				.inflate(context, R.layout.dlg_loading, null));
		mDialog.setCanceledOnTouchOutside(false);
		progressTxt = (TextView) mDialog.findViewById(R.id.progress_txt);
		mDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (context instanceof NoSwipeBackActivity) {
						if (((NoSwipeBackActivity)context).getTask()!=null) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	// public static NetLoadingDailog getDialog(Context context) {
	// if (loadingDailog == null) {
	// loadingDailog = new NetLoadingDailog(context);
	// }
	// return loadingDailog;
	// }

	public void loading() {
		if(!mDialog.isShowing()) {
			try {
				progressTxt.setText("");
				mDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void loading(String msg) {
		if(!mDialog.isShowing()) {
			try {
				progressTxt.setText(msg);
				mDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			progressTxt.setText(msg);
		}
	}
	public void setDialogCanceledOnTouchOutside(boolean isCancle){
		if (mDialog != null) {
			mDialog.setCanceledOnTouchOutside(isCancle);
		}
	}
	public void setDialogCancelable(boolean isCancle){
		if (mDialog != null) {
			mDialog.setCancelable(false);
		}
	}
	/**
	 * 隐藏对话框
	 */
	private void hideLoadingDialog() {
		if (mDialog != null) {
			try {
				mDialog.dismiss();
			} catch (Exception e) {
			}
		}
	}

	public void dismissDialog() {
		hideLoadingDialog();
	}

	public boolean isShow() {
		return mDialog.isShowing();
	}

}
