package com.easier.writepre.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.easier.writepre.R;
import com.easier.writepre.entity.CircleDetail;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.MarkNo;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.CircleAddParams;
import com.easier.writepre.param.CircleAddParams.MarkParams;
import com.easier.writepre.param.CircleUpdateParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleCreatResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.SelectPicPopupWindow;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 新建/修改圈子
 * 
 */
public class CircleCreatActivity extends BaseActivity {

	public static final int MODIFY = 1001;
	private final int REQUEST_SUCCESS = 0;
	private final int REQUEST_ERROR = 1;

	/**
	 * 已选的item名称键值
	 */
	public static final String SELECTED_STRING = "selected_string";
	/**
	 * 已选的item值键值
	 */
	public static final String SELECTED_VALUE = "selected_value";

	/**
	 * 列表数据键值
	 */
	public static final String ALL_DATA = "all_data";

	/**
	 * result code
	 */
	public static final int DATA_RESULT_CODE = 99;

	/**
	 * 圈子类型
	 */
	private final int TYPE_REQUEST_CODE = 100;
	/**
	 * 圈子标签
	 */
	private final int TAG_REQUEST_CODE = 101;
	/**
	 * 圈子私密性
	 */
	private final int ISOPEN_REQUEST_CODE = 102;
	/**
	 * 圈子人数上限
	 */
	private final int NUM_REQUEST_CODE = 103;

	/**
	 * 圈子名称
	 */
	private final int NAME_REQUEST_CODE = 104;

	/**
	 * 圈子简介
	 */
	private final int DESC_REQUEST_CODE = 105;

	private ImageView imgHead;
	private EditText etName;
	private EditText etDesc;
	private TextView etType;
	private TextView etTag;
	private TextView etIsOpen;
	private TextView etNum;

	private CircleDetail circleDetail;
	private CircleAddParams params;

	private SelectPicPopupWindow popWindow;

	private static final int NONE = 0;
	private static final int PHOTO_GRAPH = 104;// 拍照
	private static final int PHOTO_ZOOM = 105; // 缩放
	private static final int PHOTO_RESOULT = 106;// 结果
	private static final String IMAGE_UNSPECIFIED = "image/*";
	private String photoName;
	private String photoPath;
	private ArrayList<MarkNo> tagList = new ArrayList<MarkNo>();

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQUEST_SUCCESS:
				dlgLoad.dismissDialog();
				int flag = 0;
				if (circleDetail == null) {
					ToastUtil.show("创建成功");
					flag = 0;
				} else {
					ToastUtil.show("修改成功");
					flag = 1;
				}
				onFinish(flag);
				break;
			case REQUEST_ERROR:
				dlgLoad.dismissDialog();
				if (msg.obj != null) {
					ToastUtil.show(msg.obj.toString());
				} else {
					ToastUtil.show("创建失败");
				}
				break;
			case COMMIT_FILE_OSS_ALL_FAIL:
				dlgLoad.dismissDialog();
				ToastUtil.show("图片上传失败");
				break;
			case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
			case COMMIT_FILE_OSS_ALL_SUCCESS:
				dlgLoad.dismissDialog();
				if (((ArrayList<String>) msg.obj).size() != 0) {
					params.setFace_url(((ArrayList<String>) msg.obj).get(0));
					if (circleDetail != null) {
						RequestManager.request(CircleCreatActivity.this,
								new CircleUpdateParams(circleDetail.get_id(), params), CircleCreatResponse.class,
								CircleCreatActivity.this,
								SPUtils.instance().getSocialPropEntity().getApp_socail_server());
					} else {
						RequestManager.request(CircleCreatActivity.this, params, CircleCreatResponse.class,
								CircleCreatActivity.this,
								SPUtils.instance().getSocialPropEntity().getApp_socail_server());
					}
				}
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_circle);
		params = new CircleAddParams();
		// 设置圈子默认人数
		params.setNum_limit(100);
		circleDetail = (CircleDetail) getIntent().getSerializableExtra("circle_body");
		initView();
		if (circleDetail != null) {
			params.setDesc(circleDetail.getDesc());
			params.setFace_url(circleDetail.getFace_url());
			photoPath = circleDetail.getFace_url();
			params.setIs_open(circleDetail.getIs_open());
			for (int i = 0; i < circleDetail.getMark_no().size(); i++) {
				MarkNo markNo = new MarkNo();
				markNo.setBgcolor(circleDetail.getMark_no().get(i).getBgcolor());
				markNo.setNo(circleDetail.getMark_no().get(i).getNo());
				markNo.setTitle(circleDetail.getMark_no().get(i).getTitle());
				tagList.add(markNo);
			}
			params.setName(circleDetail.getName());
			params.setNum_limit(circleDetail.getNum_limit());
			params.setType(circleDetail.getType());
			updateView();
		}
	}

	protected void onFinish(int flag) {
		setResult(RESULT_OK);
//		Intent intent = new Intent();
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		switch (flag) {
//		case 0:
//			intent.setClass(this, MainActivity.class);
//			intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
//			intent.putExtra(SocialMainView.TAB_INDEX, SocialMainView.TAB_CIRCLE);
//			intent.putExtra(SocialMainView.ITEM_CIRCLE_INDEX, SocialMainView.ITEM_2);
//			break;
//		case 1:
//			intent.setClass(this, CircleDetailActivity.class);
//			intent.putExtra("circle_id", circleDetail.get_id());
//			break;
//		}
//		startActivity(intent);
		finish();
	}

	private void initView() {
		if (circleDetail != null) {
			setTopTitle("修改圈子");
		} else {
			setTopTitle("新建圈子");
		}

		imgHead = (ImageView) findViewById(R.id.img_head);

		etName = (EditText) findViewById(R.id.et_input_name);
		etDesc = (EditText) findViewById(R.id.et_input_desc);
		etType = (TextView) findViewById(R.id.et_input_type);
		etTag = (TextView) findViewById(R.id.et_input_tag);
		etIsOpen = (TextView) findViewById(R.id.et_input_isopen);
		etNum = (TextView) findViewById(R.id.et_input_num);

		findViewById(R.id.layout_head).setOnClickListener(this);
		imgHead.setOnClickListener(this);

		etName.setOnClickListener(this);
		etDesc.setOnClickListener(this);
		etType.setOnClickListener(this);
		etTag.setOnClickListener(this);
		etIsOpen.setOnClickListener(this);
		etNum.setOnClickListener(this);

		popWindow = new SelectPicPopupWindow(this, itemsOnClick);
	}

	@Override
	public void onTopRightTxtClick(View view) {
		ArrayList<String> toastStr = check();
		if (toastStr.size() == 0) {
			dlgLoad.loading();
			params.setName(etName.getText().toString());
			params.setDesc(etDesc.getText().toString());
			ArrayList<MarkParams> markParamsList = new ArrayList<MarkParams>();
			for (int i = 0; i < tagList.size(); i++) {
				MarkParams mark = new CircleAddParams().new MarkParams(tagList.get(i).getNo());
				markParamsList.add(mark);
			}
			params.setMark_no(markParamsList);
			if (TextUtils.isEmpty(params.getFace_url())) {
				commitFilesOss(photoPath, handler);
			} else {
				if (circleDetail != null) {
					RequestManager.request(this, new CircleUpdateParams(circleDetail.get_id(), params),
							CircleCreatResponse.class, this,
							SPUtils.instance().getSocialPropEntity().getApp_socail_server());
				} else {
					RequestManager.request(this, params, CircleCreatResponse.class, this,
							SPUtils.instance().getSocialPropEntity().getApp_socail_server());
				}
			}
		} else {
			toastStr.add("不可为空!");
			ToastUtil.show(TextUtils.join(",", toastStr));
		}
	}

	/**
	 * 上传图片到阿里云
	 */
//	private void uploadPhoto() {
//		OssTokenInfo ossToken = SPUtils.instance().getOSSTokenInfo();
//		OSS oss = OSSSTS(ossToken.getAccess_key_id(), ossToken.getAccess_key_Secret(), ossToken.getSecurity_token(),
//				ossToken.getExpiration());
//		final String ossPath = SPUtils.instance().getSocialPropEntity().getOss_root() + "/"
//				+ SPUtils.instance().getLoginEntity().get_id() + Constant.OSS_IMAGE_PATH + System.currentTimeMillis()
//				+ ".png";
//		PutObjectRequest put = new PutObjectRequest(SPUtils.instance().getSocialPropEntity().getOss_bucket(), ossPath,
//				photoPath);
//		put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//
//			@Override
//			public void onProgress(PutObjectRequest arg0, long currentSize, long totalSize) {
//				LogUtils.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//			}
//		});
//
//		oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//
//			@Override
//			public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//
//			}
//
//			@Override
//			public void onFailure(PutObjectRequest request, ClientException clientExcepion,
//					ServiceException serviceException) {
//				dlgLoad.dismissDialog();
//				params.setFace_url(null);
//				// 请求异常
//				if (clientExcepion != null) {
//					// 本地异常如网络异常等
//					// ToastUtil.show("网络异常");
//					clientExcepion.printStackTrace();
//					// return;
//				}
//				ToastUtil.show("上传图片失败,请重试!");
//				if (serviceException != null) {
//					// 服务异常
//					LogUtils.e("ErrorCode", serviceException.getErrorCode());
//					LogUtils.e("RequestId", serviceException.getRequestId());
//					LogUtils.e("HostId", serviceException.getHostId());
//					LogUtils.e("RawMessage", serviceException.getRawMessage());
//				}
//			}
//		});
//	}

	/**
	 * 检测合法性
	 * 
	 * @return
	 */
	private ArrayList<String> check() {
		ArrayList<String> toastStr = new ArrayList<String>();
		if (TextUtils.isEmpty(photoPath)) {
			toastStr.add("圈子封面");
		}
		if (TextUtils.isEmpty(etName.getText().toString())) {
			toastStr.add("圈子名称");
		}
		if (TextUtils.isEmpty(etDesc.getText().toString())) {
			toastStr.add("圈子简介");
		}
		if (TextUtils.isEmpty(etType.getText().toString())) {
			toastStr.add("圈子类型");
		}
		// if (tagList.isEmpty()) {
		// toastStr.add("圈子标签");
		// }
		if (TextUtils.isEmpty(etIsOpen.getText().toString())) {
			toastStr.add("圈子私密性");
		}
		// if (TextUtils.isEmpty(etNum.getText().toString())) {
		// toastStr.add("圈子人数上限");
		// }
		return toastStr;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.layout_head:
		case R.id.img_head:
			popWindow.showAtLocation(CircleCreatActivity.this.findViewById(R.id.main),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.et_input_type:
			toChooseItem(0);
			break;
		case R.id.et_input_tag:
			toChooseItem(1);
			break;
		case R.id.et_input_isopen:
			toChooseItem(2);
			break;
		case R.id.et_input_num:
			toChooseItem(3);
			break;
		case R.id.et_input_name:
			toChooseItem(4);
			break;
		case R.id.et_input_desc:
			toChooseItem(5);
			break;
		default:
			break;
		}

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
				formGallerya();
				break;
			default:
				break;
			}

		}

	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** 拍照 */
	private void formCamera() {
		photoName = System.currentTimeMillis() + ".png";
		new File(FileUtils.SD_IMAGES_PATH).mkdirs();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
		startActivityForResult(intent, PHOTO_GRAPH);
	}

	/** 照片中选择 */
	private void formGallerya() {
		photoName = System.currentTimeMillis() + ".png";
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
		startActivityForResult(intent, PHOTO_ZOOM);
	}

	/**
	 * 收缩图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");// 进行修剪
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_RESOULT);
	}

	private void toChooseItem(int i) {
		ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
		switch (i) {
		case 0:
			String[] types = getResources().getStringArray(R.array.array_circle_type);
			for (int j = 0; j < types.length; j++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", types[j]);
				map.put("value", j + "");
				datas.add(map);
			}
			LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
			if (TextUtils.isEmpty(loginEntity.getOper_role()) || !loginEntity.getOper_role().equals("school")) {
				datas.remove(0);
			}
			Intent intent = new Intent(this, CircleCreatItemChooseActivity.class);
			intent.putExtra(ALL_DATA, datas);
			intent.putExtra(SELECTED_VALUE, params.getType());
			startActivityForResult(intent, TYPE_REQUEST_CODE);
			break;
		case 1:
			Intent intent1 = new Intent(this, CircleChooseTagActivity.class);
			intent1.putExtra(ALL_DATA, tagList);
			startActivityForResult(intent1, TAG_REQUEST_CODE);
			break;
		case 2:
			String[] isopen = getResources().getStringArray(R.array.array_circle_isopen);
			for (int j = 0; j < isopen.length; j++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", isopen[j]);
				map.put("value", j + "");
				datas.add(map);
			}
			Intent intent2 = new Intent(this, CircleCreatItemChooseActivity.class);
			intent2.putExtra(ALL_DATA, datas);
			intent2.putExtra(SELECTED_VALUE, params.getIs_open());
			startActivityForResult(intent2, ISOPEN_REQUEST_CODE);
			break;
		case 3:
			SocialPropEntity socialProp = SPUtils.instance().getSocialPropEntity();
			String[] circle_limit_num = socialProp.getCircle_num_limit().split(",");
			for (int j = 0; j < circle_limit_num.length; j++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", circle_limit_num[j] + "人");
				map.put("value", circle_limit_num[j]);
				datas.add(map);
			}
			Intent intent3 = new Intent(this, CircleCreatItemChooseActivity.class);
			intent3.putExtra(ALL_DATA, datas);
			intent3.putExtra(SELECTED_VALUE, params.getNum_limit() + "");
			startActivityForResult(intent3, NUM_REQUEST_CODE);
			break;
		case 4:
			Intent intent4 = new Intent(this, CircleCreatNameActivity.class);
			intent4.putExtra(SELECTED_VALUE, params.getName());
			startActivityForResult(intent4, NAME_REQUEST_CODE);
			break;
		case 5:
			Intent intent5 = new Intent(this, CircleCreatDescActivity.class);
			intent5.putExtra(SELECTED_VALUE, params.getDesc());
			startActivityForResult(intent5, DESC_REQUEST_CODE);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;

		if (RESULT_OK == resultCode) {
			// 拍照
			if (requestCode == PHOTO_GRAPH) {
				// 设置文件保存路径
				File picture = new File(FileUtils.SD_IMAGES_PATH + photoName);
				startPhotoZoom(Uri.fromFile(picture));
			}
			// 读取相册缩放图片
			if (requestCode == PHOTO_ZOOM) {
				if (data == null)
					return;
				startPhotoZoom(data.getData());
			}
			// 处理结果
			if (requestCode == PHOTO_RESOULT) {
				if (data == null)
					return;
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					if (photo == null) {
						ToastUtil.show("处理图片失败,请重试!");
						return;
					}
					File pohotFile = FileUtils.saveBitmap(FileUtils.SD_CLIP_IMAGES_PATH, photoName, photo);
					// 选择的图片的路径
					photoPath = pohotFile.getAbsolutePath();
					// 初始化参数
					params.setFace_url(null);
					imgHead.setImageDrawable(Drawable.createFromPath(photoPath));
				}
			}
		} else if (DATA_RESULT_CODE == resultCode) {
			if (data == null)
				return;
			// 圈子类型
			if (TYPE_REQUEST_CODE == requestCode) {
				params.setType(data.getStringExtra(SELECTED_VALUE));
				updateView(requestCode, data.getStringExtra(SELECTED_STRING));
			}
			// 圈子标签
			if (TAG_REQUEST_CODE == requestCode) {
				tagList.clear();
				tagList.addAll((ArrayList<MarkNo>) data.getSerializableExtra(SELECTED_VALUE));
				updateView(requestCode, data.getSerializableExtra(SELECTED_VALUE));
			}
			// 圈子私密性
			if (ISOPEN_REQUEST_CODE == requestCode) {
				params.setIs_open(data.getStringExtra(SELECTED_VALUE));
				updateView(requestCode, data.getStringExtra(SELECTED_STRING));
			}
			// 圈子人数上限
			if (NUM_REQUEST_CODE == requestCode) {
				try {
					params.setNum_limit(Integer.parseInt(data.getStringExtra(SELECTED_VALUE)));
				} catch (Exception e) {
					params.setNum_limit(0);
				}
				updateView(requestCode, data.getStringExtra(SELECTED_STRING));
			}
			// 圈子名称
			if (NAME_REQUEST_CODE == requestCode) {
				params.setName(data.getStringExtra(SELECTED_VALUE));
				updateView(requestCode, data.getStringExtra(SELECTED_STRING));
			}
			// 圈子简介
			if (DESC_REQUEST_CODE == requestCode) {
				params.setDesc(data.getStringExtra(SELECTED_VALUE));
				updateView(requestCode, data.getStringExtra(SELECTED_STRING));
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateView() {
		setTopRightTxt("完成");

		BitmapHelp.getBitmapUtils().display(imgHead, StringUtil.getImgeUrl(params.getFace_url()));
		etName.setText(params.getName());
		etDesc.setText(params.getDesc());

		String[] types = getResources().getStringArray(R.array.array_circle_type);
		for (int j = 0; j < types.length; j++) {
			if(params.getType().equals(j + "")){
				etType.setText(types[j]);
				break;
			}
		}
		ArrayList<String> tagString = new ArrayList<String>();
		for (int i = 0; i < tagList.size(); i++) {
			tagString.add(tagList.get(i).getTitle());
		}
		etTag.setText(TextUtils.join(",", tagString.toArray()));
		String[] isopen = getResources().getStringArray(R.array.array_circle_isopen);
		for (int j = 0; j < isopen.length; j++) {
			if (params.getIs_open().equals(j + "")){
				etIsOpen.setText(isopen[j]);
				break;
			}
		}
		etNum.setText(params.getNum_limit() + "人");

	}

	private void updateView(int requestCode, Object obj) {
		setTopRightTxt("完成");
		switch (requestCode) {
		case TYPE_REQUEST_CODE:
			etType.setText(obj.toString());
			break;
		case TAG_REQUEST_CODE:
			ArrayList<String> tagString = new ArrayList<String>();
			for (int i = 0; i < tagList.size(); i++) {
				tagString.add(tagList.get(i).getTitle());
			}
			etTag.setText(TextUtils.join(",", tagString.toArray()));
			break;
		case ISOPEN_REQUEST_CODE:
			etIsOpen.setText(obj.toString());
			break;
		case NUM_REQUEST_CODE:
			etNum.setText(obj.toString());
			break;
		case NAME_REQUEST_CODE:
			etName.setText(obj.toString());
			break;
		case DESC_REQUEST_CODE:
			etDesc.setText(obj.toString());
			break;
		default:
			break;
		}

	}

	@Override
	public void onResponse(BaseResponse response) {
		super.onResponse(response);
		if ("0".equals(response.getResCode())) {
			if (response instanceof CircleCreatResponse) {
				Message message = new Message();
				message.what = REQUEST_SUCCESS;
				handler.sendMessage(message);
			}
		} else {
			if (response instanceof CircleCreatResponse) {
				Message message = new Message();
				message.what = REQUEST_ERROR;
				message.obj = response.getResMsg();
				handler.sendMessage(message);
			}
		}

	}
}
