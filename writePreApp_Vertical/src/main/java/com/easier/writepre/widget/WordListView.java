package com.easier.writepre.widget;

import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.entity.WordFontInfo;
import com.easier.writepre.fragment.FoundJiZiFragment;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyViewFlipper.OnViewListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

public class WordListView extends RelativeLayout implements OnViewListener,
		OnClickListener {

	private MyViewFlipper viewFlipper;

	private List<WordFontInfo> fontList;

	private TextView tvName; // tvBeiTie

	private WordImgListDialog wDlg;

	private ImageView ivStyle;

	private int position;

	private Context context;

	private BitmapUtils bitmapUtils;

	public WordListView(Context context, int position) {
		super(context);
		this.position = position;
		this.context = context;
		View view = View.inflate(context, R.layout.view_word_item, null);
		((LinearLayout) view.findViewById(R.id.linear_jizi))
				.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		addView(view, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		bitmapUtils = new BitmapUtils(WritePreApp.getApp().getApplication(),
				FileUtils.SD_IMAGES_CACHE);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		bitmapUtils.configMemoryCacheEnabled(true);
		bitmapUtils.configThreadPoolSize(100);
		bitmapUtils.configDiskCacheEnabled(true);
		bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils
				.getScreenSize(WritePreApp.getApp().getApplication()));
		bitmapUtils.configDefaultShowOriginal(false);
		init();
	}

	private void init() {
		fontList = new ArrayList<WordFontInfo>();
		viewFlipper = (MyViewFlipper) findViewById(R.id.vf_word_img);
		tvName = (TextView) findViewById(R.id.tv_font_hint);
		// tvBeiTie = (TextView) findViewById(R.id.tv_font_beitie);
		ivStyle = (ImageView) findViewById(R.id.iv_style);

		findViewById(R.id.btn_word_up).setOnClickListener(this);
		findViewById(R.id.btn_word_down).setOnClickListener(this);
		wDlg = new WordImgListDialog(getContext());
		viewFlipper.setListener(this);
	}

	public void setBackgroud(int res) {
		ivStyle.setBackgroundResource(res);
	}

	public void refreshData(List<WordFontInfo> data, int height) {
		if (data == null) {
			return;
		}

		fontList = data;
		ivStyle.setLayoutParams(new LayoutParams(height, height));
		viewFlipper.removeAllViews();

		ImageView iv = null;

		for (int i = 0; i < data.size(); i++) {
			iv = new ImageView(getContext());
			iv.setScaleType(ScaleType.CENTER_CROP);
			iv.setLayoutParams(new LayoutParams(height, height));
			if (i == 0) {
				bitmapUtils.display(iv,
						StringUtil.getImgeUrl(data.get(0).getPic_url_min()));
			}
			viewFlipper.addView(iv);
		}

		if (!data.isEmpty()) {
			tvName.setText(data.get(0).getTxtStr());
			// tvBeiTie.setText(data.get(0).getSfrom());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_word_up:
			if (fontList.size() > 1) {
				viewFlipper.showPrevious();
				bitmapUtils.configDefaultImageLoadAnimation(AnimationUtils
						.loadAnimation(context, R.anim.in_rightleft));
			} else
				ToastUtil.show("当前只有一个字");
			break;
		case R.id.btn_word_down:
			if (fontList.size() > 1) {
				viewFlipper.showNext();
				bitmapUtils.configDefaultImageLoadAnimation(AnimationUtils
						.loadAnimation(context, R.anim.in_leftright));
			} else
				ToastUtil.show("当前只有一个字");
			break;
		}
		bitmapUtils.display(
				viewFlipper.getChildAt(viewFlipper.getDisplayedChild()),
				StringUtil.getImgeUrl(fontList.get(
						viewFlipper.getDisplayedChild()).getPic_url_min()));
	}

	/**
	 * 弹出同一个字不同字体集合
	 *
	 */
	class WordImgListDialog extends Dialog {

		private MyAdapter adapter;

		public WordImgListDialog(Context context) {
			super(context, R.style.Dialog_Fullscreen);
			View v = View.inflate(context, R.layout.dlg_word, null);
			TextView tv_title = (TextView) v.findViewById(R.id.top_title);
			ImageView iv_back = (ImageView) v.findViewById(R.id.img_back);
			tv_title.setText("集字");
			setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			setCanceledOnTouchOutside(true);
			iv_back.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					wDlg.dismiss();
				}
			});
			init();
		}

		private void init() {
			adapter = new MyAdapter();
			GridView gv = (GridView) findViewById(R.id.gv_word_imgs);
			gv.setAdapter(adapter);
			gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					viewFlipper.setDisplayedChild(position);
					bitmapUtils.display(viewFlipper.getChildAt(viewFlipper
							.getDisplayedChild()), StringUtil
							.getImgeUrl(fontList.get(
									viewFlipper.getDisplayedChild())
									.getPic_url_min()));
					wDlg.dismiss();
				}
			});
		}

	}

	class MyAdapter extends BaseAdapter {

		private final int size;

		public MyAdapter() {
			// size = getContext().getResources().getDimensionPixelSize(
			// R.dimen.img_word_size);
			size = WritePreApp.getApp().getWidth(1) / 4 - 20;
		}

		@Override
		public int getCount() {
			return fontList.size();
		}

		@Override
		public WordFontInfo getItem(int position) {
			return fontList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WordFontInfo item = getItem(position);
			if (convertView == null) {
				convertView = new ImageView(getContext());
				convertView.setLayoutParams(new AbsListView.LayoutParams(size,
						size));
			}

			WritePreApp.bitmapUtils.display(convertView, StringUtil
					.getImgeUrl(TextUtils.isEmpty(item.getPic_url_min()) ? item
							.getPic_url() : item.getPic_url_min()));
			return convertView;
		}

	}

	@Override
	public void onChanger(int whichChild) {

		FoundJiZiFragment.wordList.remove(position);
		CourseContentInfo ccInfo = new CourseContentInfo();
		ccInfo.set_id(fontList.get(whichChild).get_id());
		// ccInfo.setTitle("");
		ccInfo.setPic_url(fontList.get(whichChild).getPic_url());
		ccInfo.setPic_url_min(fontList.get(whichChild).getPic_url_min());
		// ccInfo.setVedio_pic("");
		// ccInfo.setVedio_url("");
		// ccInfo.setSfrom("");
		FoundJiZiFragment.wordList.add(position, ccInfo);
		tvName.setText(fontList.get(whichChild).getTxtStr());
		// tvBeiTie.setText(fontList.get(whichChild).getSfrom());
	}

	public String getCurPic() { // 集字预览原图
		return StringUtil.getImgeUrl(fontList.get(
				viewFlipper.getDisplayedChild()).getPic_url());
	}

	public String getCurPicMin() { // 集字预览缩略图
		return StringUtil.getImgeUrl(fontList.get(
				viewFlipper.getDisplayedChild()).getPic_url_min());
	}

	public String getWordId() {
		return fontList.get(viewFlipper.getDisplayedChild()).get_id();
	}

	@Override
	public void onClick() {
		wDlg.show();
	}
}
