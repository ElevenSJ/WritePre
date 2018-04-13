package com.easier.writepre.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.util.Base64;
import com.easier.writepre.R;
import com.easier.writepre.adapter.ImageAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.jni.AESUtils;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.PkUpdateWorksParams;
import com.easier.writepre.param.PkUpdateWorksParams.ImgUrlUpdateWork;
import com.easier.writepre.param.YouKuTokenParams;
import com.easier.writepre.param.YoukuVideoUpdateParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.SquarePostAddResponse;
import com.easier.writepre.response.YouKuTokenResponse;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.DisplayUtil;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SelectPicPopupWindow;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.HomeFragmentActivity;
import com.youku.uploader.IUploadResponseHandler;
import com.youku.uploader.YoukuUploader;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 上传作品页面
 * 
 * @author zhaomaohan
 * 
 */
@SuppressLint("HandlerLeak")
public class PkUpdateWorksActivity extends BaseActivity {

	private RoundImageView iv_ts_head_info;

	private TextView tv_ts_uname_info, tv_ts_status_info,
			tv_ts_cata_title_info, tv_ts_teacher_info;


	private boolean STATE; // true 图片上传成功 false图片上传失败或未上传图片

	private boolean FLAG; // true图片类型 false视频类型

	private View parentView;

	private String yk_video_id;

	private MyGridView gv_image;

	private String photoName;

	private TextView tv_works_no;

	private int REQUEST_CODE_VIDEO = 0x0000001;

	private String videoFilePath; // 本地视频路径

	private String pk_id ,pk_type,pk_title,works_id, works_no, teacher, selected_work, user_nike;

	private ImageView img_back;

	private String user_id;

	private String EncodeAES;

	private YoukuUploader uploader;

	private TextView tv_percent; // 视频上传进度

	private TextView tv_send_works; // 提交作品

	private ImageView img_add_video, img_selected_video, img_video_icon, img_delete;

	private static final int MAX_SIZE = 6;

	private ImageAdapter mAdapter;

	private SelectPicPopupWindow popWindow;

	private static final int PHOTO_GRAPH = 104;

	private List<ImgUrlUpdateWork> imgUrlList = new ArrayList<ImgUrlUpdateWork>();
	private List<String> ossPathList = new ArrayList<String>(); // 存放oss图片路径
	private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				onUpdate();
				break;
			case COMMIT_FILE_OSS_ALL_FAIL:
				dlgLoad.dismissDialog();
				wHandler.sendEmptyMessage(201);
				break;
			case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
			case COMMIT_FILE_OSS_ALL_SUCCESS:
				dlgLoad.dismissDialog();
				ossPathList.clear();
				ossPathList.addAll((ArrayList<String>) msg.obj);
				if (ossPathList.size() > 0) {
					for (int i = 0; i < ossPathList.size(); i++) {
						ImgUrlUpdateWork imgUrl = new PkUpdateWorksParams().new ImgUrlUpdateWork();
						imgUrl.setUrl(ossPathList.get(i));
						imgUrlList.add(imgUrl);
					}
				}
				wHandler.sendEmptyMessage(200);
				break;
			default:
				break;
			}

		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = getLayoutInflater().inflate(R.layout.activity_pk_update_works, null);
		setContentView(parentView);
		init();
		if (savedInstanceState != null) {
			mCache = (ArrayList<String>) savedInstanceState.getSerializable("photoData");
			if (!TextUtils.isEmpty(savedInstanceState.getString("photoName"))&&!savedInstanceState.getString("photoName").contains("null")) {
				photoName = savedInstanceState.getString("photoName");
				if (new File(FileUtils.SD_IMAGES_PATH, photoName).exists()) {
					mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
				}
			}else{
				if (new File(FileUtils.SD_IMAGES_PATH, "null").exists()) {
					new File(FileUtils.SD_IMAGES_PATH, "null").delete();
				}
				ToastUtil.show("拍照失败,请重试!");
			}
			mHandler.sendEmptyMessage(0);
			photoName = null;
		}
	}

	private void init() {
		pk_id= getIntent().getStringExtra("pk_id");
		pk_type= getIntent().getStringExtra("pk_type");
		pk_title= getIntent().getStringExtra("pk_title");
		works_id = getIntent().getStringExtra("works_id");
		works_no = getIntent().getStringExtra("works_no");
		teacher = getIntent().getStringExtra("teacher");
		user_nike = getIntent().getStringExtra("user_nike");
		selected_work = getIntent().getStringExtra("selected_work");

		iv_ts_head_info = (RoundImageView) findViewById(R.id.iv_ts_head_info);

		tv_ts_uname_info = (TextView) findViewById(R.id.tv_ts_uname_info);

		tv_ts_status_info = (TextView) findViewById(R.id.tv_ts_status_info);

		tv_ts_cata_title_info = (TextView) findViewById(R.id.tv_ts_cata_title_info);

		tv_ts_teacher_info = (TextView) findViewById(R.id.tv_ts_teacher_info);

		tv_send_works = (TextView) findViewById(R.id.tv_send_works);
		tv_percent = (TextView) findViewById(R.id.tv_percent);
		tv_works_no = (TextView) findViewById(R.id.tv_works_no);
		img_selected_video = (ImageView) findViewById(R.id.img_selected_video);
		img_back = (ImageView) findViewById(R.id.img_back);
		img_add_video = (ImageView) findViewById(R.id.img_add_video);
		img_video_icon = (ImageView) findViewById(R.id.img_video_icon);
		img_delete = (ImageView) findViewById(R.id.img_delete);
		gv_image = (MyGridView) findViewById(R.id.gv_image);
		mAdapter = new ImageAdapter(this, gv_image, MAX_SIZE);
		gv_image.setAdapter(mAdapter);
		mAdapter.setData(null);
		mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
			@Override
			public void onClick(int position, View v) {
				String path = mAdapter.getItem(position);
				if ("addItem".equals(path)) {
					FLAG = true;
					popWindow = new SelectPicPopupWindow(PkUpdateWorksActivity.this, itemsOnClick, FLAG);
					popWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					Intent intent = new Intent(PkUpdateWorksActivity.this, ImageDetailActivity.class);
					intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("selected_data", mCache);
					intent.putStringArrayListExtra("data", mCache);
					startActivityForResult(intent, ImageDetailActivity.REQUEST_CODE_DELETE);
				}
			}

			@Override
			public void onDelete(int position, View v) {

			}
		});
		int gvImageWidth = (WritePreApp.getApp().getWidth(1f) - DisplayUtil.dip2px(this, 40)) / 3;
		img_add_video.setLayoutParams(new LayoutParams(gvImageWidth, gvImageWidth));
		img_selected_video.setLayoutParams(new LayoutParams(gvImageWidth, gvImageWidth));

		img_add_video.setOnClickListener(itemsOnClick);
		img_back.setOnClickListener(itemsOnClick);
		img_delete.setOnClickListener(itemsOnClick);
		tv_send_works.setOnClickListener(itemsOnClick);
		user_id = SPUtils.instance().getLoginEntity().get_id();
		IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);

		LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
		iv_ts_head_info.setImageView(StringUtil.getHeadUrl(loginEntity.getHead_img()));
		iv_ts_head_info.setIconView(loginEntity.getIs_teacher());
		tv_ts_uname_info.setText(loginEntity.getUname());
		tv_ts_teacher_info.setVisibility(TextUtils.isEmpty(teacher)?View.GONE:View.VISIBLE);
		tv_ts_teacher_info.setText("指导老师:" +teacher);
		tv_works_no.setText("NO:" + works_no);
		tv_ts_cata_title_info.setVisibility(TextUtils.isEmpty(selected_work)?View.GONE:View.VISIBLE);
		tv_ts_cata_title_info.setText(selected_work);
		if (pk_type!=null&&pk_type.equals("view_pk")){
			findViewById(R.id.txt_video_title).setVisibility(View.GONE);
			findViewById(R.id.rl_video_layout).setVisibility(View.GONE);
			findViewById(R.id.iv_guide).setVisibility(TextUtils.isEmpty(teacher)?View.GONE:View.VISIBLE);
			tv_ts_status_info.setVisibility(View.GONE);
		}else{
			tv_ts_status_info.setText("初选");
			requestYouKuToken();
		}
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			switch (v.getId()) {
			case R.id.item_popupwindows_camera: // true拍照 false拍视频
				if (FLAG) {
					formCamera();
				} else {
					Intent intent = new Intent(PkUpdateWorksActivity.this, RecorderVideoActivity.class);
					startActivityForResult(intent, REQUEST_CODE_VIDEO);
				}
				break;
			case R.id.item_popupwindows_Photo: // true本地图片 false本地视频
				if (FLAG) {
					Intent intent = new Intent(PkUpdateWorksActivity.this, ImageSelectionActivity.class);
					intent.putExtra("maxSize", MAX_SIZE);
					intent.putStringArrayListExtra("data", mCache);
					startActivityForResult(intent, ImageSelectionActivity.REQUEST_CODE_ADD);
				} else {
					Intent intent = new Intent(PkUpdateWorksActivity.this, HomeFragmentActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.img_add_video:
				FLAG = false;
				popWindow = new SelectPicPopupWindow(PkUpdateWorksActivity.this, itemsOnClick, FLAG);
				popWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.img_back:
				onTopLeftClick(null);
				break;
			case R.id.img_delete:
				videoFilePath = "";
				img_selected_video.setVisibility(View.GONE);
				img_selected_video.setImageBitmap(null);
				img_video_icon.setVisibility(View.GONE);
				img_delete.setVisibility(View.GONE);
				img_add_video.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_send_works:
				if (mCache.size() == 0 && (videoFilePath == null || TextUtils.isEmpty(videoFilePath))) {
					ToastUtil.show("请上传作品");
					return;
				}
				commitFilesOss(mCache, mHandler);
				break;
			default:
				break;
			}

		}

	};

	public void updateSelectedData() {
		mCache.clear();
		mCache.addAll(mAdapter.getSelectedData());
	};
	private void formCamera() {
		photoName = System.currentTimeMillis() + ".png";
		new File(FileUtils.SD_IMAGES_PATH).mkdirs();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
		startActivityForResult(intent, PHOTO_GRAPH);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("photoData", mCache);
		if (!TextUtils.isEmpty(photoName)) {
			outState.putString("photoName", photoName);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			return;
		if (resultCode == RESULT_OK) {
			if (requestCode == ImageDetailActivity.REQUEST_CODE_DELETE
					|| requestCode == ImageSelectionActivity.REQUEST_CODE_ADD) {
				if (data != null) {
					ArrayList<String> results = data.getStringArrayListExtra("data1");
					if (results != null) {
						mCache.clear();
						mCache.addAll(results);
						mHandler.sendEmptyMessage(0);
					}
				}
			} else if (requestCode == PHOTO_GRAPH) {
				if (TextUtils.isEmpty(photoName) || photoName.contains("null")) {
					return;
				}
				mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
				mHandler.sendEmptyMessage(0);
				photoName = null;
			} else if (requestCode == REQUEST_CODE_VIDEO) {
				if (data != null) {// 自定义录制视频
					img_selected_video.setVisibility(View.VISIBLE);
					img_video_icon.setVisibility(View.VISIBLE);
					img_video_icon.bringToFront();
					img_delete.setVisibility(View.VISIBLE);
					img_add_video.setVisibility(View.GONE);
					img_selected_video.setImageBitmap(Bimp.getVideoThumbnail(data.getStringExtra("path")));
					videoFilePath = data.getStringExtra("path");
					// uploadVideo(data.getStringExtra("path"));
					img_selected_video.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							String type = "video/mp4";
							Uri uri = Uri.parse(videoFilePath);
							intent.setDataAndType(uri, type);
							startActivity(intent);
						}
					});
				}
			}
		}

	}

	private void onUpdate() {
		if (mAdapter != null) {
			mAdapter.setData(mCache);
			mAdapter.setSelectedData(mCache);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCache != null) {
			mCache.clear();
		}
	}

	private BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) { // 本地视频路径
			List<String> filePathList = intent.getStringArrayListExtra("list");
			// 设置选择的视频可见并且设置布局
			img_selected_video.setVisibility(View.VISIBLE);
			img_video_icon.setVisibility(View.VISIBLE);
			img_video_icon.bringToFront();
			tv_percent.bringToFront();
			img_delete.setVisibility(View.VISIBLE);
			img_add_video.setVisibility(View.GONE);
			img_selected_video.setImageBitmap(Bimp.getVideoThumbnail(filePathList.get(0)));
			videoFilePath = filePathList.get(0);
			// uploadVideo(filePathList.get(0));
			img_selected_video.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					String type = "video/mp4";
					Uri uri = Uri.parse(videoFilePath);
					intent.setDataAndType(uri, type);
					startActivity(intent);
				}
			});
		}
	};


	private Handler wHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				RequestManager.request(PkUpdateWorksActivity.this,
						new PkUpdateWorksParams(pk_id,works_id, imgUrlList, "", "0"), SquarePostAddResponse.class,
						PkUpdateWorksActivity.this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
				break;

			case 201:
				if (videoFilePath == null || TextUtils.isEmpty(videoFilePath)) {
					dlgLoad.dismissDialog();
					ToastUtil.show("提交作品失败");
				} else { // 上传视频
					STATE = false;
					uploadVideo(videoFilePath);
				}
				break;
			case 202: // 图片 、 视频上传成功
				dlgLoad.dismissDialog();
				STATE = false;
				// ToastUtil.show("提交作品成功");
//				Intent intent = new Intent(PkUpdateWorksActivity.this, MainActivity.class);
//				intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
//				intent.putExtra(SocialMainView.TAB_INDEX, SocialMainView.TAB_PK);
//				startActivity(intent);
				finish();
				break;
			case 203:// 视频上传失败
				dlgLoad.dismissDialog();
				ToastUtil.show("视频提交失败");
				tv_percent.setText("");
				if (STATE) { // 图片上传失败
//					Intent intent1 = new Intent(PkUpdateWorksActivity.this, MainActivity.class);
//					intent1.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
//					intent1.putExtra(SocialMainView.TAB_INDEX, SocialMainView.TAB_PK);
//					startActivity(intent1);
					finish();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void requestYouKuToken() {
		RequestManager.request(this, new YouKuTokenParams("android"), YouKuTokenResponse.class,
				PkUpdateWorksActivity.this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}

	private void uploadVideo(String locVideoPath) {
		/**
		 * 内网环境：425a15656116e199 外网环境：2442dc16694b06f1
		 */
		uploader = YoukuUploader.getInstance(Constant.PK_VIDEO_UPLOAD_ID, "", getApplicationContext());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", EncodeAES);
		HashMap<String, String> uploadInfo = new HashMap<String, String>();
		uploadInfo.put("title", user_nike + "_" + works_no + "_" + selected_work);
		// uploadInfo.put("title", "吐吧吐" + "_" + "10000048" + "_" + "千字文");
		uploadInfo.put("tags", "优酷,EVA");
		uploadInfo.put("file_name", locVideoPath);

		uploader.upload(params, uploadInfo, new IUploadResponseHandler() {

			@Override
			public void onStart() {
				LogUtils.v("Main upload onStart");
				// tv_percent.setText("等待中");
			}

			@Override
			public void onSuccess(JSONObject response) { // {"video_id":"XMTUxODE0NTE0OA=="}
				LogUtils.v("Main upload onSuccess "+response.toString());
				try {
					yk_video_id = response.getString("video_id");
					updateVideo();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onProgressUpdate(int counter) {
				LogUtils.v("Main upload onProgress "+ counter + "");
				tv_percent.setText(counter + "%");
			}

			@Override
			public void onFailure(JSONObject errorResponse) {
				Message message = new Message();
				message.what = 203;
				wHandler.sendMessage(message);
			}

			@Override
			public void onFinished() {
				LogUtils.v("Main upload onFinished");
				// tv_percent.setText("完成");
				dlgLoad.dismissDialog();
			}
		});
	}

	private void updateVideo() {
		RequestManager.request(this, new YoukuVideoUpdateParams(works_id, yk_video_id), GiveOkResponse.class, this,
				SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}

	@Override
	public void onResponse(BaseResponse response) {
		super.onResponse(response);
		if ("0".equals(response.getResCode())) {
			if (response instanceof SquarePostAddResponse) {
				if (videoFilePath == null || TextUtils.isEmpty(videoFilePath)) {
					dlgLoad.dismissDialog();
					// ToastUtil.show("提交作品成功");
					STATE = false;
					Intent intent = new Intent();
					if (pk_type.equals("view_pk")){
						intent.setClass(PkUpdateWorksActivity.this, PkViewDetailActivity.class);
					}else{
						intent.setClass(PkUpdateWorksActivity.this, PkOnLineDetailActivity.class);
					}
					intent.putExtra("pk_id",pk_id);
					intent.putExtra("pk_title",pk_title);
					intent.putExtra("pk_type",pk_type);
					startActivity(intent);
					finish();
				} else { // 上传视频
					STATE = true;
					uploadVideo(videoFilePath);
				}
			} else if (response instanceof YouKuTokenResponse) {
				YouKuTokenResponse yktResult = (YouKuTokenResponse) response;
				try {
					EncodeAES = new String(AESUtils.aesDecrypt(Base64.decodeFast(yktResult.getRepBody().getToken())));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LogUtils.e("token:" + EncodeAES);
			} else if (response instanceof GiveOkResponse) {
				Message message = new Message();
				message.what = 202;
				wHandler.sendMessage(message);
			}
		} else { // 返回错误处理
			dlgLoad.dismissDialog();
			ToastUtil.show(response.getResMsg());
		}
	}
}
