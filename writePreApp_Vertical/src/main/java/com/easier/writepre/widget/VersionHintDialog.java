package com.easier.writepre.widget;

import com.easier.writepre.R;

import android.app.Activity;
import android.app.Dialog;

public class VersionHintDialog extends Dialog {

	public VersionHintDialog(Activity context) {
		super(context, R.style.loading_dialog);
		setContentView(R.layout.dlg_version_hint);
		setCanceledOnTouchOutside(true);
	}

	public void setOnClickListener(android.view.View.OnClickListener listener) {
		findViewById(R.id.btn_ok).setOnClickListener(listener);
		findViewById(R.id.btn_cancel).setOnClickListener(listener);
	}

}
