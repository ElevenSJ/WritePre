package com.easier.writepre.widget;

import android.app.Dialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

import com.easier.writepre.R;
import com.easier.writepre.ui.NoSwipeBackActivity;

public class ChoiceImgDialog extends Dialog implements
		android.view.View.OnClickListener {

	private static final String TYPE_OF_GET_CONTENT = "image/*";

	private final NoSwipeBackActivity context;
	
	public static final int PIC = 101,CAMERA =102;

	public ChoiceImgDialog(NoSwipeBackActivity context) {
		super(context, R.style.loading_dialog);
		this.context = context;
		setContentView(R.layout.dlg_choice_img);
		setCanceledOnTouchOutside(true);
		init();
	}

	private void init() {
		View btnPictrue = findViewById(R.id.btn_pictrue);
		View btnCamera = findViewById(R.id.btn_cam);
		btnPictrue.setOnClickListener(this);
		btnCamera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_pictrue:
			showPictrue();
			break;
		case R.id.btn_cam:
			showCamera();
			break;
		}
	}

	private void showPictrue() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(TYPE_OF_GET_CONTENT);
		context.startActivityForResult(intent, PIC);
		// context.startActivityForResult(intent, context.RESULT_FIRST_USER);
	}

	private void showCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		context.startActivityForResult(intent, CAMERA);
		// context.startActivityForResult(intent, context.RESULT_FIRST_USER);
	}
}
