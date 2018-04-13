package com.easier.writepre.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.volley.Response.Listener;
import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.entity.WordFontListInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.Constant.Extras;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.FoundMainView;
import com.easier.writepre.param.SearchWordParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SearchWordResponse;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MiaoHongActivity;
import com.easier.writepre.ui.WordPreviewActivity;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.PopUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.WordListView;
import com.umeng.analytics.MobclickAgent;

public class FoundJiZiFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener, OnEditorActionListener, TextWatcher,
		Listener<BaseResponse> {

	private int res = 0; // 回宫格 九宫格

	private int type; // 行书 楷书 ...

	private EditText etTxt;

	private StyleWindow window;

	private LinearLayout llBody;

	private SearchWordParams params;

	private DisplayMetrics displayMetrics = new DisplayMetrics();

	public List<WordFontListInfo> list = new ArrayList<WordFontListInfo>(); // 过滤掉未集到字集合

	public static List<CourseContentInfo> wordList = new ArrayList<CourseContentInfo>();

	/**
	 * 控件初始化
	 */
	public void init() {
		window = new StyleWindow();
		etTxt = (EditText) findViewById(R.id.et_word_txt);
		llBody = (LinearLayout) findViewById(R.id.ll_body);
		findViewById(R.id.iv_search).setOnClickListener(this);
		findViewById(R.id.iv_writ).setOnClickListener(this);
		findViewById(R.id.iv_revo).setOnClickListener(this);
		findViewById(R.id.iv_miaohong).setOnClickListener(this);
		RadioGroup rgp = (RadioGroup) findViewById(R.id.rgp_fonts);
		rgp.setOnCheckedChangeListener(this);
		etTxt.setOnEditorActionListener(this);
		etTxt.addTextChangedListener(this);
		rgp.check(R.id.rbtn_xs);

		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);

		/**
		 * 弹出输入软键盘
		 */
		findViewById(R.id.rl_jizi_sousuo).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						InputMethodManager m = (InputMethodManager) etTxt
								.getContext().getSystemService(
										Context.INPUT_METHOD_SERVICE);
						m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

					}
				});

	}

	@Override
	public int getContextView() {
		return R.layout.found_jizi;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_writ:
			PopUtils.setSmartBar(window);
			window.show(v);
			break;
		case R.id.iv_search:
			String txt = etTxt.getText().toString();
			params = new SearchWordParams();
			if (!TextUtils.isEmpty(txt)) {
				umengSearch(txt);
				loadingDlg.loading();
				params.setType(type);
				params.setWords(txt);
				RequestManager.request(getActivity(), params,
						SearchWordResponse.class, this, Constant.URL);
			}
			break;
		case R.id.iv_revo:
			ArrayList<String> strs_min = new ArrayList<String>();
			ArrayList<String> strs = new ArrayList<String>();
			ArrayList<String> ids = new ArrayList<String>();
			WordListView item = null;
			for (int i = 0; i < llBody.getChildCount(); i++) {
				item = ((WordListView) llBody.getChildAt(i));
				strs_min.add(item.getCurPicMin());
				strs.add(item.getCurPic());
				ids.add(item.getWordId());
			}
			if (!strs.isEmpty() && params == null) {
				Intent intent = new Intent(getActivity(),
						WordPreviewActivity.class);
				intent.putStringArrayListExtra(Extras.FLAG_PIC_MIN, strs_min);
				intent.putStringArrayListExtra(Extras.FLAG_PIC, strs);
				intent.putStringArrayListExtra(Extras.FLAG_WORD_ID, ids);
				startActivity(intent);
			}
			break;
		case R.id.iv_miaohong:
			if (wordList.size() > 0
					&& !TextUtils.isEmpty(etTxt.getText().toString().trim())) {
				Intent intent = new Intent(getActivity(),
						MiaoHongActivity.class);
				intent.putExtra("fz_id", wordList.get(0).get_id());
				intent.putExtra("wordList", (Serializable) wordList);
				startActivity(intent);
			} else
				ToastUtil.show("请先集字");

			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rbtn_cs:
			type = 1;
			break;
		case R.id.rbtn_xs:
			type = 2;
			break;
		case R.id.rbtn_ks:
			type = 3;
			break;
		case R.id.rbtn_ls:
			type = 4;
			break;
		case R.id.rbtn_zs:
			type = 5;
			break;
		}
		String txt = etTxt.getText().toString();
		if (!TextUtils.isEmpty(txt) && params == null) {
			loadingDlg.loading();
			params = new SearchWordParams();
			params.setType(type);
			params.setWords(txt);
			RequestManager.request(getActivity(), params,
					SearchWordResponse.class, this, Constant.URL);
		}
	}

	@Override
	public void onResponse(BaseResponse response) {
		if (loadingDlg.isShow()) {
			loadingDlg.dismissDialog();
		}
		if ("0".equals(response.getResCode())) {
			displayData(((SearchWordResponse) response).getRepBody().getList());
		} else {
			ToastUtil.show(response.getResMsg());
		}
		params = null;
	}

	
	/**
	 * 展示集字
	 * @param data
	 */
	private void displayData(List<WordFontListInfo> data) {
		WordListView v = null;
		llBody.removeAllViews();
		wordList.clear();
		list.clear();
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				WordFontListInfo item = data.get(i);
				if (item.getList() != null && item.getList().isEmpty()) {
					continue;
				} else {
					list.add(item);
				}
			}
			for (int j = 0; j < list.size(); j++) {
				WordFontListInfo item = list.get(j);
				v = new WordListView(getActivity(), j);
				v.refreshData(item.getList(),
						WritePreApp.getApp().getWidth(1) / 3); // 集字缩略图大小适配
				v.setBackgroud(res);
				llBody.addView(v);
				CourseContentInfo ccInfo = new CourseContentInfo();
				ccInfo.set_id(item.getList().get(0).get_id());
				ccInfo.setPic_url(item.getList().get(0).getPic_url());
				ccInfo.setPic_url_min(item.getList().get(0).getPic_url_min());
				wordList.add(ccInfo);
			}
		}
		if (llBody.getChildCount() <= 0) {
			if (params != null && !TextUtils.isEmpty(params.getWords())) {
				if (params.getWords().length() == 1) {
					ToastUtil.show("数据整理中"); // 未找到对应集字
				} else {
					ToastUtil.show("数据整理中"); // 未找到所有对应集字
				}
			}
			// findViewById(R.id.right_layout).setVisibility(View.INVISIBLE);
		} else {
			if (params != null && !TextUtils.isEmpty(params.getWords())) {
				if (llBody.getChildCount() != params.getWords().length()) {
					// ToastUtil.show("未找到所有对应集字");
				}
			}
			// findViewById(R.id.right_layout).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 弹出字格选择
	 *
	 */
	class StyleWindow extends PopupWindow implements OnClickListener {

		public StyleWindow() {
			super(View.inflate(getActivity(), R.layout.pop_word_style, null),
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new BitmapDrawable());
			setOutsideTouchable(true);
			init();
		}

		private void init() {
			View v = this.getContentView();
			v.findViewById(R.id.btn_style_0).setOnClickListener(this);
			v.findViewById(R.id.btn_style_1).setOnClickListener(this);
			v.findViewById(R.id.btn_style_2).setOnClickListener(this);
			v.findViewById(R.id.btn_style_3).setOnClickListener(this);
			v.findViewById(R.id.btn_style_4).setOnClickListener(this);
			v.findViewById(R.id.btn_style_5).setOnClickListener(this);
			v.findViewById(R.id.btn_style_6).setOnClickListener(this);
		}

		public void show(View v) {
			showAsDropDown(v, -v.getMeasuredWidth(), 0);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_style_0:
				res = 0;
				break;
			case R.id.btn_style_1:
				res = R.drawable.ge_1;
				break;
			case R.id.btn_style_2:
				res = R.drawable.ge_2;
				break;
			case R.id.btn_style_3:
				res = R.drawable.ge_3;
				break;
			case R.id.btn_style_4:
				res = R.drawable.ge_4;
				break;
			case R.id.btn_style_5:
				res = R.drawable.ge_5;
				break;
			case R.id.btn_style_6:
				res = R.drawable.ge_6;
				break;
			}
			for (int i = 0; i < llBody.getChildCount(); i++) {
				((WordListView) llBody.getChildAt(i)).setBackgroud(res);
			}
			window.dismiss();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {// 点击搜索按钮进行调用

		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String txt = etTxt.getText().toString();
			params = new SearchWordParams();
			if (!TextUtils.isEmpty(txt)) {
				umengSearch(txt);
				// loadingDlg.loading();
				params.setType(type);
				params.setWords(txt);
				DeviceUtils.closeKeyboard(v, getActivity()); // 关闭软键盘
				RequestManager.request(getActivity(), params,
						SearchWordResponse.class, this, Constant.URL);
			} else {
				llBody.removeAllViews();
			}
			return true;
		}
		return false;
	}

	/**
	 * 集字搜索
	 */
	private void umengSearch(String txt) {
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FOR));
		var.add(FoundMainView.CONTENT[FoundMainView.index]);
		var.add("搜索");
		YouMengType.onEvent(getActivity(), var,1, txt);
	}
	/**
	 * 集字发帖
	 */
	private void umengTie() {
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FOR));
		var.add(FoundMainView.CONTENT[FoundMainView.index]);
		var.add("发帖");
		YouMengType.onEvent(getActivity(), var, 1, "集字发帖");
	}

	@Override
	public void afterTextChanged(Editable s) { // 输入文本后自动调用接口
		// if (!TextUtils.isEmpty(s) && params == null
		// && s.toString().getBytes().length != s.toString().length()) {
		// loadingDlg.loading();
		// params = new SearchWordParams();
		// params.setType(type);
		// params.setWords(s.toString());
		// RequestManager.request(getActivity(), params,
		// SearchWordResponse.class, this, Constant.URL);
		// } else {
		// displayData(null);
		// }
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (TextUtils.isEmpty(s)) {
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			etTxt.setLayoutParams(layoutParams);
			etTxt.setGravity(Gravity.LEFT);
		} else {
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			etTxt.setLayoutParams(layoutParams);
			etTxt.setGravity(Gravity.CENTER);
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser&&isPrepared){
			umeng();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getUserVisibleHint()){
			umeng();
		}
	}
	/**
	 * 友盟统计
	 */
	private void umeng() {
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FOR));
		var.add(FoundMainView.CONTENT[FoundMainView.index]);
		YouMengType.onEvent(getActivity(), var, getShowTime(), FoundMainView.CONTENT[FoundMainView.index]);
	}

}
