package com.easier.writepre.widget;

import android.app.Dialog;
import android.view.View;

import com.easier.writepre.R;
import com.easier.writepre.ui.NoSwipeBackActivity;

public class ChoiceSexDialog extends Dialog {

	public ChoiceSexDialog(NoSwipeBackActivity context) {
		super(context, R.style.loading_dialog);
		setContentView(R.layout.dlg_choice_sex);
		setCanceledOnTouchOutside(true);
	}

	public void setOnClickListener(android.view.View.OnClickListener listener) {
		View btnPictrue = findViewById(R.id.btn_man);
		View btnCamera = findViewById(R.id.btn_women);
		btnPictrue.setOnClickListener(listener);
		btnCamera.setOnClickListener(listener);
	}


}
