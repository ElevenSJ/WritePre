package com.easier.writepre.ui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ApplyForTeacherAdapter;
import com.easier.writepre.entity.ApplyForTeacherListItem;
import com.easier.writepre.entity.ApplyForTeacherStatusInfo;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CommitApplyForTeacherParams;
import com.easier.writepre.param.GetApplyForTeacherParams;
import com.easier.writepre.response.ApplyForTeacherStatusResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CommitApplyForTeacherResponse;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.SelectPicPopupWindow;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 申请成为老师
 */
public class ApplyForCertificationActvity extends BaseActivity implements OnItemClickListener {
	private View parentView;

	private ImageView picExample, picApply, image_selected;
	private ListView listView;
	private Button btApply;
	// private GridView imgPicGrid;
	private TextView tvApplyStatus;
	private List<ApplyForTeacherListItem> datas = new ArrayList<ApplyForTeacherListItem>();
	private ApplyForTeacherAdapter adapter;

	// private ImageAdapter imageAdapter;

	private final int MAX_SIZE = 1;

	private final int PHOTO_GRAPH = 104;// 拍照

	private SelectPicPopupWindow popWindow;

	private ArrayList<String> mCache;

	private String photoName;// 拍照图片名

	private ApplyForTeacherStatusInfo statusInfo;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COMMIT_FILE_OSS_ALL_FAIL:
				dlgLoad.dismissDialog();
				ToastUtil.show("图片上传失败");
				break;
			case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
			case COMMIT_FILE_OSS_ALL_SUCCESS:
				if (((ArrayList<String>) msg.obj).size() != 0) {
					statusInfo.setTech_pic(((ArrayList<String>) msg.obj).get(0));
					commitApplyForTeacher();
				}
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		parentView = getLayoutInflater().inflate(R.layout.activity_apply_for_cert, null);
		setContentView(parentView);
		initView();
		if (arg0 != null) {
			if (!TextUtils.isEmpty(arg0.getString("photoName")) && !arg0.getString("photoName").contains("null")) {
				photoName = arg0.getString("photoName");
				if (new File(FileUtils.SD_IMAGES_PATH, photoName).exists()) {
					mCache.clear();
					mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
					onUpdate();
				}
			} else {
				if (new File(FileUtils.SD_IMAGES_PATH, "null").exists()) {
					new File(FileUtils.SD_IMAGES_PATH, "null").delete();
				}
				ToastUtil.show("拍照失败,请重试!");
			}
			photoName = null;
		}
		initData();
		LoginUtil.checkLogin(this);
//		getApplyForTeacher();
	}

	private void getApplyForTeacher() {
		dlgLoad.loading();
		RequestManager.request(this, new GetApplyForTeacherParams(), ApplyForTeacherStatusResponse.class, this,
				SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}

	private void commitApplyForTeacher() {
		dlgLoad.loading();
		RequestManager.request(this, new CommitApplyForTeacherParams(statusInfo), CommitApplyForTeacherResponse.class,
				this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}

	private void initData() {
		LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
		ApplyForTeacherListItem item0 = new ApplyForTeacherListItem();
		item0.setId(0);
		item0.setName("姓名");
		item0.setDetail(loginEntity.getReal_name());
		datas.add(item0);

		ApplyForTeacherListItem item1 = new ApplyForTeacherListItem();
		item1.setId(1);
		item1.setName("性别");
		item1.setDetail(loginEntity.getSex());
		datas.add(item1);

		ApplyForTeacherListItem item2 = new ApplyForTeacherListItem();
		item2.setName("出生日期");
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(loginEntity.getBirth_day());
			String beginTime = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date);
			item2.setDetail(beginTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		item2.setId(2);
		datas.add(item2);

		ApplyForTeacherListItem item3 = new ApplyForTeacherListItem();
		item3.setName("任职城市");
		item3.setId(3);
		datas.add(item3);

		ApplyForTeacherListItem item4 = new ApplyForTeacherListItem();
		item4.setName("任职学校/机构");
		item4.setId(4);
		datas.add(item4);

		ApplyForTeacherListItem item5 = new ApplyForTeacherListItem();
		item5.setName("手机号码");
		item5.setId(5);
		item5.setDetail(loginEntity.getTel());
		datas.add(item5);

		adapter.setData(datas);

		statusInfo = new ApplyForTeacherStatusInfo();

	}

	private void initView() {
		setTopTitle("申请成为老师");

		picExample = (ImageView) findViewById(R.id.pic_example);
		picApply = (ImageView) findViewById(R.id.pic_apply);
		image_selected = (ImageView) findViewById(R.id.image_selected);
		// int mWidth =
		// (WritePreApp.getApp().getWidth(1f)-DisplayUtil.dip2px(this, 30)-30)/
		// 4;
		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(mWidth, mWidth);
		// picExample.setLayoutParams(layoutParams);

		listView = (ListView) findViewById(R.id.lv_list);
		btApply = (Button) findViewById(R.id.apply_bt);
		// imgPicGrid = (GridView) findViewById(R.id.noScrollgridview);
		tvApplyStatus = (TextView) findViewById(R.id.tv_apply_status);

		btApply.setOnClickListener(this);
		picApply.setOnClickListener(this);
		image_selected.setOnClickListener(this);

		adapter = new ApplyForTeacherAdapter(this);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);

		mCache = new ArrayList<String>(MAX_SIZE);
		// imageAdapter = new ImageAdapter(this, imgPicGrid, MAX_SIZE);
		// imgPicGrid.setAdapter(imageAdapter);
		// imageAdapter.setData(null);

		// imgPicGrid.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// if (statusInfo.getDealed().equals("0")) {
		// return;
		// } else {
		// if (statusInfo.getStatus().equals("ok")) {
		// return;
		// }
		// }
		// String path = (String) parent.getAdapter().getItem(position);
		// if ("addItem".equals(path)) {
		// popWindow.showAtLocation(parentView, Gravity.BOTTOM |
		// Gravity.CENTER_HORIZONTAL, 0, 0);
		// } else {
		// Intent intent = new Intent(ApplyForCertificationActvity.this,
		// ImageDetailActivity.class);
		// intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
		// intent.putExtra("position", position);
		// intent.putExtra("MAX_SIZE", MAX_SIZE);
		// intent.putStringArrayListExtra("selected_data",
		// imageAdapter.getSelectedData());
		// intent.putStringArrayListExtra("data",
		// imageAdapter.getSelectedData());
		// startActivityForResult(intent,
		// ImageDetailActivity.REQUEST_CODE_DELETE);
		// }
		// }
		// });
		popWindow = new SelectPicPopupWindow(this, itemsOnClick);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			popWindow.dismiss();
			switch (v.getId()) {
			case R.id.item_popupwindows_camera:
				formCamera();
				break;
			case R.id.item_popupwindows_Photo:
				Intent intent = new Intent(ApplyForCertificationActvity.this, ImageSelectionActivity.class);
				intent.putExtra("maxSize", MAX_SIZE);
				intent.putStringArrayListExtra("data", mCache);
				startActivityForResult(intent, ImageSelectionActivity.REQUEST_CODE_ADD);
				break;
			default:
				break;
			}

		}

	};

	/** 拍照 */
	private void formCamera() {
		photoName = System.currentTimeMillis() + ".png";
		new File(FileUtils.SD_IMAGES_PATH).mkdirs();
		File photoFile = new File(FileUtils.SD_IMAGES_PATH, photoName);
		// if (photoFile.exists()) {
		// photoFile.delete();
		// }
		// try {
		// photoFile.createNewFile();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		startActivityForResult(intent, PHOTO_GRAPH);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (!TextUtils.isEmpty(photoName)) {
			outState.putString("photoName", photoName);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.apply_bt:
			commitApply();
			break;
		case R.id.pic_apply:
			if (statusInfo.getDealed().equals("0")) {
				return;
			} else {
				if (statusInfo.getStatus().equals("ok")) {
					return;
				}
			}
			String path = mCache.size() != 0 ? mCache.get(0) : "";
			if ("".equals(path)) {
				popWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			} else {
				Intent intent = new Intent(ApplyForCertificationActvity.this, ImageDetailActivity.class);
				intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
				intent.putExtra("position", 0);
				intent.putExtra("MAX_SIZE", MAX_SIZE);
				intent.putStringArrayListExtra("selected_data", mCache);
				intent.putStringArrayListExtra("data", mCache);
				startActivityForResult(intent, ImageDetailActivity.REQUEST_CODE_DELETE);
			}
			break;
		case R.id.image_selected:
			if (statusInfo.getDealed().equals("0")) {
				return;
			} else {
				if (statusInfo.getStatus().equals("ok")) {
					return;
				}
			}
			mCache.clear();
			onUpdate();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) && TextUtils.isEmpty(statusInfo.get_id())) {
			getApplyForTeacher();
		}
	}

	private void commitApply() {
		ArrayList<String> toastStr = check();
		if (toastStr.isEmpty()) {
			updateParams();
			if (mCache.get(0).startsWith("http")) {
				commitApplyForTeacher();
			} else {
				commitFilesOss(mCache.get(0), mHandler);
			}
		} else {
			toastStr.add("编辑有误!");
			ToastUtil.show(TextUtils.join(",", toastStr));
		}

	}

	private void updateParams() {
		for (int i = 0; i < adapter.getCount(); i++) {
			String value = adapter.getItem(i).getDetail();
			switch ((int) adapter.getItemId(i)) {
			case 0:
				statusInfo.setReal_name(value);
				break;
			case 1:
				statusInfo.setSex(value);
				break;
			case 2:
				statusInfo.setBirth_day(value);
				break;
			case 3:
				statusInfo.setCity(value);
				break;
			case 4:
				statusInfo.setSchool_name(value);
				break;
			case 5:
				statusInfo.setTel(value);
				break;
			default:
				break;
			}
		}

	}

	private ArrayList<String> check() {
		ArrayList<String> toastStr = new ArrayList<String>();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (TextUtils.isEmpty(adapter.getItem(i).getDetail())) {
				switch ((int) adapter.getItemId(i)) {
				case 0:
					toastStr.add("姓名");
					break;
				case 1:
					toastStr.add("性别");
					break;
				case 2:
					toastStr.add("出生日期");
					break;
				case 3:
					toastStr.add("任职城市");
					break;
				case 4:
					toastStr.add("任职学校/机构");
					break;
				case 5:
					toastStr.add("手机号码");
					break;
				default:
					break;
				}
			}else{
				if ((int) adapter.getItemId(i) == 5) {
					if (adapter.getItem(i).getDetail().length()!=11) {
						toastStr.add("手机号码");
					}
				}
			}
		}
		if (mCache.isEmpty()) {
			toastStr.add("认证照片");
		}
		return toastStr;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (statusInfo.getDealed().equals("0")) {
			return;
		} else {
			if (statusInfo.getStatus().equals("ok")) {
				return;
			}
		}
		Intent intent = new Intent();
		switch ((int) id) {
		case 0:
			intent.setClass(this, ApplyForTeacherEditActivity.class);
			intent.putExtra(ApplyForTeacherEditActivity.HINT_VALUE, "请输入真实姓名");
			intent.putExtra(ApplyForTeacherEditActivity.TITLE_VALUE, "编辑姓名");
			intent.putExtra(ApplyForTeacherEditActivity.MAX_LENGTH, 4);
			intent.putExtra(ApplyForTeacherEditActivity.SELECTED_VALUE, adapter.getItem(position).getDetail());
			this.startActivityForResult(intent, (int) id);
			break;
		case 1:
			ArrayList<String> sexStrings = new ArrayList<String>();
			sexStrings.add("男");
			sexStrings.add("女");
			intent.setClass(this, CreatItemChooseActivity.class);
			intent.putExtra(CreatItemChooseActivity.ALL_DATA, sexStrings);
			intent.putExtra(CreatItemChooseActivity.SELECTED_STRING, adapter.getItem(position).getDetail());
			this.startActivityForResult(intent, (int) id);
			break;
		case 2:
			showDialog((int) id);
			break;
		case 3:
			intent.setClass(this, EditCityActivity.class);
			this.startActivityForResult(intent, (int) id);
			break;
		case 4:
			intent.setClass(this, ApplyForTeacherEditActivity.class);
			intent.putExtra(ApplyForTeacherEditActivity.HINT_VALUE, "请输入所在任职学校/机构");
			intent.putExtra(ApplyForTeacherEditActivity.TITLE_VALUE, "编辑任职学校/机构");
			intent.putExtra(ApplyForTeacherEditActivity.SELECTED_VALUE, adapter.getItem(position).getDetail());
			this.startActivityForResult(intent, (int) id);
			break;
		case 5:
			intent.setClass(this, ApplyForTeacherEditActivity.class);
			intent.putExtra(ApplyForTeacherEditActivity.HINT_VALUE, "请输入手机号码");
			intent.putExtra(ApplyForTeacherEditActivity.TITLE_VALUE, "编辑手机号码");
			intent.putExtra(ApplyForTeacherEditActivity.MAX_LENGTH, 11);
			intent.putExtra(ApplyForTeacherEditActivity.SELECTED_VALUE, adapter.getItem(position).getDetail());
			this.startActivityForResult(intent, (int) id);
			break;

		default:
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case 2:
			Calendar c = Calendar.getInstance();
			dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
					String beginTime = new StringBuffer().append(year).append("-").append(month + 1).append("-")
							.append(dayOfMonth).toString();
					try {
						Date date = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(beginTime);
						beginTime = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					LogUtils.e("出生日期:" + beginTime);
					for (int i = 0; i < adapter.getCount(); i++) {
						if (adapter.getItem(i).getId() == 2) {
							adapter.getItem(i).setDetail(beginTime);
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}, c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
			);
			break;
		}
		return dialog;
	}

	private void onUpdate() {
		if (mCache.size() != 0) {
			BitmapHelp.getBitmapUtils().display(picApply, mCache.get(0), new BitmapLoadCallBack<View>() {

				@Override
				public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					picApply.setImageBitmap(arg2);

				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					picApply.setImageResource(R.drawable.empty_photo);

				}
			});
			if (statusInfo.getDealed().equals("0")) {
				image_selected.setVisibility(View.GONE);
				return;
			} else {
				if (statusInfo.getStatus().equals("ok")) {
					image_selected.setVisibility(View.GONE);
					return;
				}
			}
			image_selected.setVisibility(View.VISIBLE);
		} else {
			picApply.setImageResource(R.drawable.icon_addpic_unfocused);
			image_selected.setVisibility(View.GONE);
		}
		// if (imageAdapter != null) {
		// imageAdapter.setData(mCache);
		// if (statusInfo != null) {
		// if (statusInfo.getDealed().equals("0")) {
		// imageAdapter.setSelectedData(null);
		// } else {
		// if (statusInfo.getStatus().equals("ok")) {
		// imageAdapter.setSelectedData(null);
		// } else
		// imageAdapter.setSelectedData(mCache);
		// }
		// }
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
			case 4:
			case 5:
				if (data != null) {
					for (int i = 0; i < adapter.getCount(); i++) {
						if (adapter.getItem(i).getId() == requestCode) {
							adapter.getItem(i)
									.setDetail(data.getStringExtra(ApplyForTeacherEditActivity.SELECTED_VALUE));
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
				break;
			case 3:
				if (data != null) {
					for (int i = 0; i < adapter.getCount(); i++) {
						if (adapter.getItem(i).getId() == 3) {
							adapter.getItem(i).setDetail(data.getStringExtra("city"));
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
				break;
			case 1:
				if (data != null) {
					for (int i = 0; i < adapter.getCount(); i++) {
						if (adapter.getItem(i).getId() == 1) {
							adapter.getItem(i).setDetail(data.getStringExtra(CreatItemChooseActivity.SELECTED_STRING));
							adapter.notifyDataSetChanged();
							break;
						}
					}
				}
				break;
			case ImageDetailActivity.REQUEST_CODE_DELETE:
			case ImageSelectionActivity.REQUEST_CODE_ADD:
				if (data != null) {
					ArrayList<String> results = data.getStringArrayListExtra("data1");
					if (results != null) {
						mCache.clear();
						mCache.addAll(results);
						onUpdate();
					}
				}
				break;
			case PHOTO_GRAPH:
				if (TextUtils.isEmpty(photoName) || photoName.contains("null")) {
					return;
				}
				mCache.clear();
				mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
				onUpdate();
				photoName = null;
				break;
			}
		}
	}

	@Override
	public void onResponse(BaseResponse response) {
		super.onResponse(response);
		if (response.getResCode().equals("0")) {
			if (response instanceof ApplyForTeacherStatusResponse) {
				dlgLoad.dismissDialog();
				statusInfo = ((ApplyForTeacherStatusResponse) response).getRepBody();
				if (!TextUtils.isEmpty(statusInfo.get_id())) {
					updateStatus(statusInfo);
				}
			} else if (response instanceof CommitApplyForTeacherResponse) {
				dlgLoad.dismissDialog();
				statusInfo.setDealed("0");
				tvApplyStatus.setText("审核中");
				btApply.setEnabled(false);
				btApply.setText("提交申请");
				onUpdate();
			}
		} else {
			dlgLoad.dismissDialog();
			if (response instanceof ApplyForTeacherStatusResponse) {
				if (response.getResCode().equals("1")) {
					LoginUtil.showLoginDialog(this);
					return;
				}
			}
			ToastUtil.show(response.getResMsg());
		}

	}

	private void updateStatus(ApplyForTeacherStatusInfo statusInfo) {
		for (int i = 0; i < adapter.getCount(); i++) {
			switch ((int) adapter.getItemId(i)) {
			case 0:
				adapter.getItem(i).setDetail(statusInfo.getReal_name());
				break;
			case 1:
				adapter.getItem(i).setDetail(statusInfo.getSex());
				break;
			case 2:
				String beginTime = statusInfo.getBirth_day();
				try {
					Date date = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(statusInfo.getBirth_day());
					beginTime = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.getItem(i).setDetail(beginTime);
				break;
			case 3:
				adapter.getItem(i).setDetail(statusInfo.getCity());
				break;
			case 4:
				adapter.getItem(i).setDetail(statusInfo.getSchool_name());
				break;
			case 5:
				adapter.getItem(i).setDetail(statusInfo.getTel());
				break;
			default:
				break;
			}
		}
		if (statusInfo.getDealed().equals("1")) {
			if (statusInfo.getStatus().equals("ok")) {
				tvApplyStatus.setText("审核通过");
				btApply.setText("申请已通过");
				btApply.setEnabled(false);
			} else if (statusInfo.getStatus().equals("no")) {
				tvApplyStatus.setText(statusInfo.getStatus_msg());
				btApply.setText("申请被拒绝，重新申请");
				btApply.setEnabled(true);
			} else {
				btApply.setText("提交申请");
				btApply.setEnabled(true);
			}
		} else if (statusInfo.getDealed().equals("0")) {
			tvApplyStatus.setText("审核中");
			btApply.setEnabled(false);
			btApply.setText("提交申请");
		}

		if (!TextUtils.isEmpty(statusInfo.getTech_pic())) {
			mCache.clear();
			mCache.add(StringUtil.getImgeUrl(statusInfo.getTech_pic()));
			onUpdate();
		}
		adapter.notifyDataSetChanged();

	}

	@Override
	protected void onPause() {
		super.onPause();
		umeng();
	}

	private void umeng() {
		//友盟统计
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FIV));
		var.add("申请老师");
		YouMengType.onEvent(this, var, getShowTime(), "申请老师");
	}
}
