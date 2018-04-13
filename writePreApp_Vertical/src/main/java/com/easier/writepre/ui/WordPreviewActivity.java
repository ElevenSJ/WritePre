package com.easier.writepre.ui;

import java.io.File;
import java.util.List;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.volley.Response.Listener;
import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.Constant.Extras;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.AddFavWordParams;
import com.easier.writepre.param.DelFavParams;
import com.easier.writepre.response.AddFavWordResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.ToastUtil;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WordPreviewActivity extends BaseActivity implements
		OnCheckedChangeListener, Listener<BaseResponse> {

	private Button fab;

	private String favId;

	private CheckBox chk;

	private View btnHeart;

	private List<String> ids;

	private int height; // 集字所在父控件的高度

	private List<String> pices;

	private List<String> pices_min;

	private LinearLayout ll_word_out;

	public static LinearLayout llBody;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_word_preview);
		init();
	}

	private void init() {
		setTopTitle("集字预览");
		ll_word_out = (LinearLayout) findViewById(R.id.ll_word_out);
		fab = (Button) findViewById(R.id.fab);
		pices = getIntent().getStringArrayListExtra(Extras.FLAG_PIC);
		pices_min = getIntent().getStringArrayListExtra(Extras.FLAG_PIC_MIN);
		ids = getIntent().getStringArrayListExtra(Extras.FLAG_WORD_ID);
		btnHeart = findViewById(R.id.btn_heart);
		chk = (CheckBox) findViewById(R.id.chk_orientation);
		llBody = (LinearLayout) findViewById(R.id.ll_word_body);
		chk.setOnCheckedChangeListener(this);
		fab.setOnClickListener(this);
		llBody.setDrawingCacheEnabled(true);
		ImageView iv = null;
		// for (final String item : pices) {
		// iv = new ImageView(this);
		// iv.setScaleType(ScaleType.CENTER_CROP);
		// WritePreApp.bitmapUtils.display(iv,item);
		// iv.setClickable(false);
		// llBody.addView(iv);
		// }
		for (int i = 0; i < pices.size(); i++) {
			iv = new ImageView(this);
			iv.setScaleType(ScaleType.CENTER_CROP);
			WritePreApp.bitmapUtils.display(
					iv,
					TextUtils.isEmpty(pices.get(i)) ? pices_min.get(i) : pices
							.get(i));
			iv.setClickable(false);
			llBody.addView(iv);
		}

		if (getIntent().getBooleanExtra("isFav", false)) {
			chk.setVisibility(View.GONE);
			btnHeart.setVisibility(View.GONE);
		}
		llBody.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WordPreviewActivity.this,
						WordPreviewBigPicActivity.class);
				startActivity(intent);
			}
		});

		ViewTreeObserver vto = ll_word_out.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				ll_word_out.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
				height = ll_word_out.getHeight();
				changeOri(getIntent().getBooleanExtra(Extras.FLAG_ORC, false),
						height);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		llBody.setBackgroundResource(R.color.gray_low);
	}

	private void changeOri(boolean isVer, int height) {
		LayoutParams params = null;
		LayoutParams bodyParams = (LayoutParams) llBody.getLayoutParams();
		if (!isVer) { // 默认竖
			if (pices.size() == 1) {
				bodyParams.width = getWidth(1.0f);
				bodyParams.height = getWidth(1.0f);
				params = new LayoutParams(getWidth(1.0f), getWidth(1.0f));
			} else {
				bodyParams.width = height / pices.size();
				bodyParams.height = height;
				params = new LayoutParams(height / pices.size(), height
						/ pices.size());
			}
			llBody.setOrientation(LinearLayout.VERTICAL);
		} else {
			if (pices.size() == 1) {
				bodyParams.width = getWidth(1.0f);
				bodyParams.height = getWidth(1.0f);
				params = new LayoutParams(getWidth(1.0f), getWidth(1.0f));
			} else {
				bodyParams.width = getWidth(1.0f);
				bodyParams.height = getWidth(1.0f) / pices.size();
				params = new LayoutParams(getWidth(1.0f) / pices.size(),
						getWidth(1.0f) / pices.size());
			}
			llBody.setOrientation(LinearLayout.HORIZONTAL);
		}
		for (int i = 0; i < llBody.getChildCount(); i++) {
			llBody.getChildAt(i).setLayoutParams(params);
		}
		llBody.setPadding(5, 5, 5, 5);
		llBody.setLayoutParams(bodyParams);

	}

	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_heart:
			dlgLoad.loading();
			if (TextUtils.isEmpty(favId)) {
				RequestManager.request(this,
						new AddFavWordParams(chk.isChecked(), ids),
						AddFavWordResponse.class, this, Constant.URL);
			} else {
				RequestManager.request(this, new DelFavParams(favId),
						BaseResponse.class, this, Constant.URL);
			}
			break;
		case R.id.btn_shared:
			Bitmap watermark = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.watermark)).getBitmap();
			sharedContent(null, null, null, null, llBody, watermark,
					WordPreviewActivity.this);
			break;
		case R.id.fab:
			llBody.setBackground(null); // 大图预览时去掉背景
			llBody.setPadding(0, 0, 0, 0);
			File file = FileUtils.saveBitmap(FileUtils.SD_IMAGES_CACHE,
					System.currentTimeMillis() + ".png", Bimp.Watermark(
							Bimp.createViewBitmap(llBody),
							((BitmapDrawable) getResources().getDrawable(
									R.drawable.watermark)).getBitmap(), 100));
			if (file == null) {
				ToastUtil.show("保存图片信息异常");
				return;
			}
			Intent intent = new Intent(WordPreviewActivity.this,
					SendTopicActivity.class);
			intent.putExtra("from", true);
			intent.putExtra("path", file.getPath());
			startActivity(intent);

			break;
		default:
			break;
		}
	}

	@Override
	public void onResponse(BaseResponse response) {
		dlgLoad.dismissDialog();
		if (!"0".equals(response.getResCode())) {
			ToastUtil.show(response.getResMsg());
		} else if (response instanceof AddFavWordResponse) {
			AddFavWordResponse resp = (AddFavWordResponse) response;
			favId = resp.getRepBody().getFav_id();
			btnHeart.setBackgroundResource(R.drawable.btn_heart1);
			ToastUtil.show("收藏成功。");
		} else {
			favId = null;
			btnHeart.setBackgroundResource(R.drawable.btn_heart);
			ToastUtil.show("取消收藏成功。");
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		changeOri(isChecked, height);
	}

}
