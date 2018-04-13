package com.easier.writepre.ui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.util.Base64;
import com.easier.writepre.R;
import com.easier.writepre.adapter.PkInfoGridAdapter;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.jni.AESUtils;
import com.easier.writepre.param.PkUpdateWorksParams;
import com.easier.writepre.param.PkUpdateWorksParams.ImgUrlUpdateWork;
import com.easier.writepre.param.PkWorksDetailParams;
import com.easier.writepre.param.PkWorksRefreshParams;
import com.easier.writepre.param.YouKuTokenParams;
import com.easier.writepre.param.YoukuVideoUpdateParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.PkWorksDetailResponse;
import com.easier.writepre.response.PkWorksRefreshResponse;
import com.easier.writepre.response.SquarePostAddResponse;
import com.easier.writepre.response.YouKuTokenResponse;
import com.easier.writepre.social.ui.youku.PlayerFullActivity;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
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
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;
import com.youku.uploader.IUploadResponseHandler;
import com.youku.uploader.YoukuUploader;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 作品管理模块
 *
 * @author zhaomaohan
 *
 */
public class PkWorksManageActivity extends BaseActivity implements
		OnItemClickListener {
	private int TAG;// 0初选可编辑，1复选可编辑，2现场大会可编辑，3可编辑

	private String video_thumbnail;

	private boolean FLAG; // true图片类型 false视频类型

	private String yk_video_id;

	private String pk_id = "",pk_type="",works_id = "";

	private PopupWindow popupwindow;

	private ImageView img_back;

	private ImageView iv_pk_report_info;

	private RoundImageView iv_ts_head_info;

	private MyGridView gv_pk_ts_status0_info, gv_pk_ts_status1_info,
			gv_pk_ts_status2_info;

	private TextView tv_ts_uname_info, tv_ts_status_info,
			tv_ts_cata_title_info, tv_ts_uptime_info, tv_ts_city_info,
			tv_ts_works_no_info, tv_ts_teacher_info, tv_pk_status0_info,
			tv_pk_status1_info, tv_pk_status2_info;

	public static Bitmap bimap;

	private GridAdapter adapter;

	private SelectPicPopupWindow popWindow;
	// private PopupWindow pop_video = null;
	private LinearLayout ll_popup;

	private View parentView;

	private TextView tv_send_works;// 提交作品

	// private List<Double> coordList; // 广场帖子发布经纬度坐标
	// private List<MarkNoPostAdd> markNoList; // 圈子中的标签
	private List<ImgUrlUpdateWork> imgUrlList; // 上传到阿里云的图片路径的集合
	private String vod_url = "";
	private List<String> ossPathList = new ArrayList<String>();
	private String user_id;
	private List<String> ossImageSuccList = new ArrayList<String>(); // 用于标识图片是否都上传至阿里云,都上传成功则表示发帖成功
	private int delOrSave = 1;// 1是保存，0是删除。
	public int flag = 0;// 上传的图片只要有失败的，为1，全都成功，为0;
	private TextView tv_endtime;
	private ImageView img_add_video;// 添加视频按钮
	private ImageView img_selected_video;// 已选择的视频
	private ImageView img_video_icon;// 选择视频后的播放器图片
	private ImageView img_delete;// 视频的删除

	private WindowManager windowManager;

	private TextView percent;

	private int width;

	private LayoutParams layoutParams;// 添加视频按钮的样式布局

	private YoukuUploader uploader;

	private String worksNo;

	private String videoFilePath;

	private static final int MAX_SIZE = 6;

	private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);

	private int REQUEST_CODE_VIDEO = 0x0000001;

	private String photoName;

	private static final int PHOTO_GRAPH = 104;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COMMIT_FILE_OSS_ALL_FAIL:
				dlgLoad.dismissDialog();
				handlerPostTopic.sendEmptyMessage(201);
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
				RequestManager.request(PkWorksManageActivity.this,
						new PkUpdateWorksParams(pk_id,works_id, imgUrlList, vod_url,
								body.getStatus()), SquarePostAddResponse.class,
						PkWorksManageActivity.this, SPUtils.instance()
								.getSocialPropEntity().getApp_socail_server());

				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);// 添加照片的按钮
		parentView = getLayoutInflater().inflate(
				R.layout.activity_pk_works_manage, null);
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
			if (adapter!=null) {
				adapter.notifyDataSetChanged();
			}
			photoName = null;
		}
		loadData();
		requestYouKuToken();
	}

	@Override
	protected void onRestart() {
		if (adapter != null) {
			adapter.update();
		}
		super.onRestart();
	}

	private void init() {
		pk_id = getIntent().getStringExtra("pk_id");
		pk_type= getIntent().getStringExtra("pk_type");
		user_id = SPUtils.instance().getLoginEntity().get_id();
		works_id = getIntent().getStringExtra("manage_works_id");
		LogUtils.e("传过来的作品id "+ works_id);

		img_back = (ImageView) findViewById(R.id.img_back);

		percent = (TextView) findViewById(R.id.percent);

		iv_pk_report_info = (ImageView) findViewById(R.id.iv_pk_report_info);

		tv_pk_status0_info = (TextView) findViewById(R.id.tv_pk_status0_info);

		tv_pk_status1_info = (TextView) findViewById(R.id.tv_pk_status1_info);

		tv_pk_status2_info = (TextView) findViewById(R.id.tv_pk_status2_info);

		gv_pk_ts_status0_info = (MyGridView) findViewById(R.id.gv_pk_ts_status0_info);

		gv_pk_ts_status1_info = (MyGridView) findViewById(R.id.gv_pk_ts_status1_info);

		gv_pk_ts_status2_info = (MyGridView) findViewById(R.id.gv_pk_ts_status2_info);

		iv_ts_head_info = (RoundImageView) findViewById(R.id.iv_ts_head_info);

		tv_ts_uname_info = (TextView) findViewById(R.id.tv_ts_uname_info);

		tv_ts_status_info = (TextView) findViewById(R.id.tv_ts_status_info);

		tv_ts_cata_title_info = (TextView) findViewById(R.id.tv_ts_cata_title_info);

		tv_ts_uptime_info = (TextView) findViewById(R.id.tv_ts_uptime_info);

		tv_ts_city_info = (TextView) findViewById(R.id.tv_ts_city_info);

		tv_ts_works_no_info = (TextView) findViewById(R.id.tv_ts_works_no_info);

		tv_ts_teacher_info = (TextView) findViewById(R.id.tv_ts_teacher_info);

		tv_send_works = (TextView) findViewById(R.id.tv_send_works);// 提交作品

		tv_endtime = (TextView) findViewById(R.id.tv_endtime);// 截止日期

		img_add_video = (ImageView) findViewById(R.id.img_add_video);

		img_selected_video = (ImageView) findViewById(R.id.img_selected_video);

		img_video_icon = (ImageView) findViewById(R.id.img_video_icon);

		img_delete = (ImageView) findViewById(R.id.img_delete);
		img_delete.setOnClickListener(itemsOnClick);

		windowManager = this.getWindowManager();
		width = windowManager.getDefaultDisplay().getWidth();
		layoutParams = (LayoutParams) img_add_video.getLayoutParams();
		layoutParams.height = width / 3;
		layoutParams.width = width / 3;
		img_add_video.setLayoutParams(layoutParams);
		img_selected_video.setLayoutParams(layoutParams);

		tv_send_works.setOnClickListener(itemsOnClick);

		tv_pk_status0_info.setOnClickListener(itemsOnClick);

		tv_pk_status1_info.setOnClickListener(itemsOnClick);

		tv_pk_status2_info.setOnClickListener(itemsOnClick);

		img_back.setOnClickListener(itemsOnClick);

		iv_pk_report_info.setOnClickListener(itemsOnClick);

		img_add_video.setOnClickListener(itemsOnClick);

		IntentFilter videoIntentFilter = new IntentFilter(
				MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);
		if (pk_type!=null&&pk_type.equals("view_pk")){
			tv_ts_status_info.setVisibility(View.GONE);
			findViewById(R.id.txt_video_title).setVisibility(View.GONE);
			findViewById(R.id.rl_video_layout).setVisibility(View.GONE);
			findViewById(R.id.ll_pk_status_layout).setVisibility(View.GONE);
		}
	}

	private static final int TAKE_PICTURE = 0x000001;


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("photoData", mCache);
		if (!TextUtils.isEmpty(photoName)) {
			outState.putString("photoName", photoName);
		}
		super.onSaveInstanceState(outState);
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			return;
		if (resultCode == RESULT_OK) {
			if (requestCode == ImageDetailActivity.REQUEST_CODE_DELETE
					|| requestCode == ImageSelectionActivity.REQUEST_CODE_ADD) {
				if (data != null) {
					ArrayList<String> results = data
							.getStringArrayListExtra("data1");
					if (results != null) {
						mCache.clear();
						mCache.addAll(results);
						adapter.notifyDataSetChanged();
					}
				}
			} else if (requestCode == PHOTO_GRAPH) {
				if (TextUtils.isEmpty(photoName) || photoName.contains("null")) {
					return;
				}
				mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
				if (adapter!=null) {
					adapter.notifyDataSetChanged();
				}
				photoName = null;
			} else if (requestCode == REQUEST_CODE_VIDEO) {
				if (data != null) {// 自定义录制视频
					img_selected_video.setVisibility(View.VISIBLE);
					img_video_icon.setVisibility(View.VISIBLE);
					img_video_icon.bringToFront();
					img_delete.setVisibility(View.VISIBLE);
					img_add_video.setVisibility(View.GONE);
					img_selected_video.setImageBitmap(Bimp
							.getVideoThumbnail(data.getStringExtra("path")));
					videoFilePath = data.getStringExtra("path");
					// uploadVideo(data.getStringExtra("path"));

					img_selected_video
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW);
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

	private void loadData() {
		RequestManager.request(this, new PkWorksDetailParams(works_id),
				PkWorksDetailResponse.class, this, SPUtils.instance()
						.getSocialPropEntity().getApp_socail_server());
	}

	/**
	 * 点击查看大图
	 *
	 * @param position
	 * @param urls
	 */
	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(PkWorksManageActivity.this,
				SquareImageLookActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
		PkWorksManageActivity.this.startActivity(intent);
	}

	PkWorksDetailResponse.Repbody body;

	// private String url;

	@Override
	public void onResponse(BaseResponse response) {
		super.onResponse(response);
		if ("0".equals(response.getResCode())) {
			if (response instanceof GiveOkResponse) {
				dlgLoad.dismissDialog();
				if (!videoFilePath.equals("del")) {
					ToastUtil.show("提交作品成功");
					success();
				}
			} else if (response instanceof YouKuTokenResponse) {
				dlgLoad.dismissDialog();
				YouKuTokenResponse yktResult = (YouKuTokenResponse) response;
				try {
					EncodeAES = new String(AESUtils.aesDecrypt(Base64
							.decodeFast(yktResult.getRepBody().getToken())));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (response instanceof PkWorksDetailResponse) {
				dlgLoad.dismissDialog();
				PkWorksDetailResponse pkwdResult = (PkWorksDetailResponse) response;
				if (pkwdResult != null) {
					body = pkwdResult.getRepBody();
					tv_ts_uname_info.setText(body.getUname());

					tv_ts_cata_title_info.setVisibility(TextUtils.isEmpty(body.getCata_title())?View.GONE:View.VISIBLE);
					tv_ts_cata_title_info.setText(body.getCata_title());

					tv_ts_uptime_info.setText(DateKit.timeFormat(body
							.getUptime().split("-")[0]));

					tv_ts_city_info.setText(body.getCity());

					worksNo = body.getWorks_no();

					tv_ts_works_no_info.setText("NO:" + body.getWorks_no());

					if (!body.getTeacher().equals("")) {
						tv_ts_teacher_info.setText("指导老师:" + body.getTeacher());
						tv_ts_teacher_info.setVisibility(View.VISIBLE);
					}else{
						if(pk_type!=null&&pk_type.equals("view_pk")){
							findViewById(R.id.iv_guide).setVisibility(View.GONE);
						}
					}
					// 获取视频截图
					if (!TextUtils.isEmpty(body.getYk_thumbnail())) {
						img_selected_video.setVisibility(View.VISIBLE);
						img_video_icon.setVisibility(View.VISIBLE);
						img_video_icon.bringToFront();
						img_delete.setVisibility(View.VISIBLE);
						img_add_video.setVisibility(View.GONE);

						BitmapHelp.getBitmapUtils().display(img_selected_video,
								body.getYk_thumbnail());

						img_selected_video
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!TextUtils.isEmpty(body
												.getYk_video_id())) {
											if (body.getYk_video_state()
													.equals("ok")) {
												Intent i = new Intent(
														PkWorksManageActivity.this,
														PlayerFullActivity.class);
												i.putExtra("vid",
														body.getYk_video_id());
												PkWorksManageActivity.this
														.startActivity(i);
											} else if (body.getYk_video_state()
													.equals("fail")) {
												ToastUtil.show("视频审核失败");
											} else {
												ToastUtil.show("视频审核中,请稍后再试");
											}
										} else
											ToastUtil.show("暂无视频");
									}
								});
					}

					video_thumbnail = body.getYk_thumbnail();

					// 获取截止日期
					if(body.getStage_end()!=null){
						Date date;
						try {
							date = new SimpleDateFormat("yyyyMMddHHmmss")
									.parse(body.getStage_end());
							String now = new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
									.format(date);
							LogUtils.e("接口获取的时间 "+body.getStage_end());
							LogUtils.e("格式化后的时间 "+now);
							tv_endtime.setText(now);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// BitmapHelp.getBitmapUtils().display(iv_ts_head_info,
					// StringUtil.getHeadUrl(body.getHead_img()));

					iv_ts_head_info.setImageView(
							StringUtil.getHeadUrl(body.getHead_img()));
					iv_ts_head_info.setIconView(body.getIs_teacher());
					if (mCache.isEmpty()) {
						if (body.getStatus().equals("0")
								&& body.getWorks_0().getImgs() != null) {
							for (int j = 0; j < body.getWorks_0().getImgs().length; j++) {
								String url = body.getWorks_0().getImgs()[j];
								LogUtils.e("接口获取的图片地址_" + j + ":"+ body.getWorks_0()
										.getImgs()[j]);
								mCache.add(url);
							}
						} else if (body.getStatus().equals("1")
								&& body.getWorks_1().getImgs() != null) {
							for (int j = 0; j < body.getWorks_1().getImgs().length; j++) {
								String url = body.getWorks_1().getImgs()[j];
								LogUtils.e("接口获取的图片地址_" + j + ":"+ body.getWorks_1()
										.getImgs()[j]);
								mCache.add(url);
							}
						} else if (body.getStatus().equals("1")
								&& body.getWorks_2().getImgs() != null) {
							for (int j = 0; j < body.getWorks_2().getImgs().length; j++) {
								String url = body.getWorks_2().getImgs()[j];
								LogUtils.e("接口获取的图片地址_" + j + ":"+ body.getWorks_2()
										.getImgs()[j]);
								mCache.add(url);
							}
						}
					}
					adapter = new GridAdapter(this);
					if (body.getStatus().equals("0")) {
						tv_ts_status_info.setText("初选");
						gv_pk_ts_status0_info.setVisibility(View.VISIBLE);
						gv_pk_ts_status1_info.setVisibility(View.GONE);
						gv_pk_ts_status2_info.setVisibility(View.GONE);
						if (gv_pk_ts_status0_info.getAdapter()==null) {
							gv_pk_ts_status0_info.setAdapter(adapter);
						}
						tv_pk_status0_info
								.setBackgroundResource(R.drawable.state_bg);
						tv_pk_status1_info
								.setBackgroundResource(R.drawable.state_bg_no);
						tv_pk_status2_info
								.setBackgroundResource(R.drawable.state_bg_no);

					} else if (body.getStatus().equals("1")) {
						tv_ts_status_info.setText("复选");
						// 复赛的按钮显示
						tv_pk_status1_info.setVisibility(View.VISIBLE);
						// 复赛的图片显示
						gv_pk_ts_status0_info.setVisibility(View.GONE);
						gv_pk_ts_status1_info.setVisibility(View.VISIBLE);
						gv_pk_ts_status2_info.setVisibility(View.GONE);
						gv_pk_ts_status1_info.setAdapter(adapter);
						tv_pk_status0_info
								.setBackgroundResource(R.drawable.state_bg_no);
						tv_pk_status1_info
								.setBackgroundResource(R.drawable.state_bg);
						tv_pk_status2_info
								.setBackgroundResource(R.drawable.state_bg_no);
					} else {
						tv_ts_status_info.setText("现场大会");
						// 复赛的按钮显示
						tv_pk_status1_info.setVisibility(View.VISIBLE);
						// 决赛的按钮显示
						tv_pk_status2_info.setVisibility(View.VISIBLE);
						// 复赛的图片显示
						gv_pk_ts_status0_info.setVisibility(View.GONE);
						gv_pk_ts_status1_info.setVisibility(View.GONE);
						gv_pk_ts_status2_info.setVisibility(View.VISIBLE);
						gv_pk_ts_status2_info.setAdapter(adapter);
						tv_pk_status0_info
								.setBackgroundResource(R.drawable.state_bg_no);
						tv_pk_status1_info
								.setBackgroundResource(R.drawable.state_bg_no);
						tv_pk_status2_info
								.setBackgroundResource(R.drawable.state_bg);
					}

					gv_pk_ts_status0_info.setOnItemClickListener(this);

					gv_pk_ts_status1_info.setOnItemClickListener(this);

					gv_pk_ts_status2_info.setOnItemClickListener(this);

					if (body.getWorks_1().getImgs() != null) {
						tv_pk_status1_info.setVisibility(View.VISIBLE);
					}

					if (body.getWorks_2().getImgs() != null) {
						tv_pk_status2_info.setVisibility(View.VISIBLE);
					}
				}
			} else if (response instanceof SquarePostAddResponse) {
				dlgLoad.dismissDialog();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Message message = new Message();
						message.what = 200;
						handlerPostTopic.sendMessage(message);
					}
				}).start();
			} else if (response instanceof PkWorksRefreshResponse) {
				dlgLoad.dismissDialog();
				ToastUtil.show("刷新作品成功");
			}
		} else {
			dlgLoad.dismissDialog();
			ToastUtil.show(response.getResMsg());
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
					Intent intent = new Intent(PkWorksManageActivity.this,
							RecorderVideoActivity.class);
					startActivityForResult(intent, REQUEST_CODE_VIDEO);
				}
				break;
			case R.id.item_popupwindows_Photo: // true本地图片 false本地视频
				if (FLAG) {
					Intent intent = new Intent(PkWorksManageActivity.this,
							ImageSelectionActivity.class);
					intent.putExtra("maxSize", MAX_SIZE);
					intent.putStringArrayListExtra("data", mCache);
					startActivityForResult(intent,
							ImageSelectionActivity.REQUEST_CODE_ADD);
				} else {
					Intent intent = new Intent(PkWorksManageActivity.this,
							HomeFragmentActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.tv_pk_status0_info:
				TAG = 0;
				tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg);
				tv_pk_status1_info
						.setBackgroundResource(R.drawable.state_bg_no);
				tv_pk_status2_info
						.setBackgroundResource(R.drawable.state_bg_no);
				gv_pk_ts_status0_info.setVisibility(View.VISIBLE);
				if (gv_pk_ts_status0_info.getAdapter()==null){
					PkInfoGridAdapter adapter = new PkInfoGridAdapter(body.getWorks_0()!=null?body.getWorks_0().getImgs():null,PkWorksManageActivity.this);
					gv_pk_ts_status0_info.setAdapter(adapter);
				}
				gv_pk_ts_status1_info.setVisibility(View.GONE);
				gv_pk_ts_status2_info.setVisibility(View.GONE);
				break;

			case R.id.tv_pk_status1_info:
				TAG = 1;
				tv_pk_status0_info
						.setBackgroundResource(R.drawable.state_bg_no);
				tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg);
				tv_pk_status2_info
						.setBackgroundResource(R.drawable.state_bg_no);
				gv_pk_ts_status0_info.setVisibility(View.GONE);
				gv_pk_ts_status1_info.setVisibility(View.VISIBLE);
				if (gv_pk_ts_status1_info.getAdapter()==null){
					PkInfoGridAdapter adapter = new PkInfoGridAdapter(body.getWorks_1()!=null?body.getWorks_1().getImgs():null,PkWorksManageActivity.this);
					gv_pk_ts_status1_info.setAdapter(adapter);
				}
				gv_pk_ts_status2_info.setVisibility(View.GONE);
				break;

			case R.id.tv_pk_status2_info:
				TAG = 2;
				tv_pk_status0_info
						.setBackgroundResource(R.drawable.state_bg_no);
				tv_pk_status1_info
						.setBackgroundResource(R.drawable.state_bg_no);
				tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg);
				gv_pk_ts_status0_info.setVisibility(View.GONE);
				gv_pk_ts_status1_info.setVisibility(View.GONE);
				gv_pk_ts_status2_info.setVisibility(View.VISIBLE);
				break;

			case R.id.img_add_video:
				FLAG = false;
				popWindow = new SelectPicPopupWindow(
						PkWorksManageActivity.this, itemsOnClick, FLAG);
				popWindow.showAtLocation(parentView, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.img_back:
				// 返回时销毁图片临时列表
				mCache.clear();
				onTopLeftClick(v);
				break;
			case R.id.iv_pk_report_info:
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					return;
				} else {
					popView();
					popupwindow.showAsDropDown(iv_pk_report_info, -50, 0);
				}
				break;
			case R.id.tv_send_works:
				if (mCache.size() == 0) {
					ToastUtil.show("请上传图片作品");
					return;
				}
				// dlgLoad.loading();
				if (imgUrlList == null) {
					imgUrlList = new ArrayList<ImgUrlUpdateWork>();
				} else {
					imgUrlList.clear();
				}
				final ArrayList<String> tempImage = new ArrayList<String>();
				if (mCache != null && mCache.size() > 0) {
					for (int i = 0; i < mCache.size(); i++) {
						if (mCache.get(i).startsWith("/")) {
							tempImage.add(mCache.get(i));
						} else {
							ImgUrlUpdateWork imgUrl = new PkUpdateWorksParams().new ImgUrlUpdateWork();
							imgUrl.setUrl(mCache.get(i));
							imgUrlList.add(imgUrl);
						}
					}
					if (tempImage.isEmpty()) {
						RequestManager.request(PkWorksManageActivity.this,
								new PkUpdateWorksParams(pk_id,works_id, imgUrlList,
										vod_url, body.getStatus()),
								SquarePostAddResponse.class,
								PkWorksManageActivity.this, SPUtils.instance()
										.getSocialPropEntity()
										.getApp_socail_server());
					} else {
						commitFilesOss(tempImage, mHandler);
					}

				} else { // 不需要上传图片至阿里云
					ToastUtil.show("作品不能为空");
				}
				break;
			case R.id.img_delete:
				if (video_thumbnail != null) {
					videoFilePath = "del";
				}
				img_selected_video.setVisibility(View.INVISIBLE);
				img_video_icon.setVisibility(View.GONE);
				img_delete.setVisibility(View.GONE);
				img_add_video.setVisibility(View.VISIBLE);
				img_selected_video.setImageBitmap(null);
				break;
			default:
				break;
			}

		}
	};

	private void formCamera() {
		photoName = System.currentTimeMillis() + ".png";
		new File(FileUtils.SD_IMAGES_PATH).mkdirs();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(
				MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
		startActivityForResult(intent, PHOTO_GRAPH);
	}

	String EncodeAES;

	Handler handlerPostTopic = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200: // 图片发帖成功
				if (videoFilePath == null || videoFilePath.equals("del")) {
					if ("del".equals(videoFilePath)) { // 删除视频
						updateVideo("");
					}
					dlgLoad.dismissDialog();
					ToastUtil.show("提交作品成功");
					success();
				} else {
					dlgLoad.loading();
					uploadVideo(videoFilePath);
				}
				break;

			case 201: // 图片发帖失败(图片失败后不进行视频上传)
				dlgLoad.dismissDialog();
				ToastUtil.show("提交作品失败");
				break;
			case 202:
				dlgLoad.dismissDialog(); // 视频提交失败
				ToastUtil.show("提交视频失败");
				success();
				break;
			}

			super.handleMessage(msg);
		}
	};

	private void success() {
		delOrSave = 0;
		mCache.clear();
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * 弹出詳情和舉報菜單
	 */
	public void popView() {
		View customView = getLayoutInflater().inflate(
				R.layout.pop_works_manage, null, false);
		popupwindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popupwindow.setAnimationStyle(R.style.AnimationFade);
		popupwindow.setFocusable(true);
		popupwindow.setOutsideTouchable(true); // 设置点击屏幕其它地方弹出框消失
		popupwindow.setBackgroundDrawable(new BitmapDrawable());
		customView.findViewById(R.id.pop_btn_refresh).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						popupwindow.dismiss();
						// ToastUtil.show("刷新");
						dlgLoad.loading();
						RequestManager.request(PkWorksManageActivity.this,
								new PkWorksRefreshParams(works_id),
								PkWorksRefreshResponse.class,
								PkWorksManageActivity.this, SPUtils.instance()
										.getSocialPropEntity()
										.getApp_socail_server());
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回时销毁图片临时列表
		mCache.clear();
		finish();
		return true;
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private int width;
		private WindowManager wm;
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			this.wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			this.width = wm.getDefaultDisplay().getWidth();
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (mCache.size() == MAX_SIZE) {
				return mCache.size();
			}
			return mCache.size() + 1;

		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_pk_works_manage,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				holder.img_delete = (ImageView) convertView
						.findViewById(R.id.img_delete);
				holder.selectedLayout = (LinearLayout) convertView
						.findViewById(R.id.selected_layout);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width / 3,width / 3);
				holder.image.setLayoutParams(layoutParams);
				convertView.setTag(holder);
				AutoUtils.autoSize(convertView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			/*
			 * works_0 初选 works_1 复选 works_2 现场大会
			 * 
			 * works_0不判断 if works_1为空，并且 状态 为1 就初选可编辑; else if works_2为空，并且 状态
			 * 为3 就复选可编辑; else if works_2不为空， 并且 状态 为5 就现场大会可编辑; else 都不可编辑;
			 */
			if (body.getStatus().equals("0") && body.getStage().equals("1")) {
				// ToastUtil.show("初选可编辑,测试中。。。");
				holder.img_delete.setVisibility(View.VISIBLE);
				// img_delete.setVisibility(View.VISIBLE);
				tv_send_works.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(body.getYk_thumbnail())) {
					img_add_video.setVisibility(View.VISIBLE);
				}else{
					img_add_video.setVisibility(View.GONE);
				}
				TAG = 3;
			} else if (body.getStatus().equals("1") && body.getStage().equals("3")) {
				// ToastUtil.show("复选可编辑,测试中。。。");
				holder.img_delete.setVisibility(View.VISIBLE);
				tv_send_works.setVisibility(View.VISIBLE);
//				img_delete.setVisibility(View.GONE);
				if (TextUtils.isEmpty(body.getYk_thumbnail())) {
					img_add_video.setVisibility(View.VISIBLE);
				}else{
					img_add_video.setVisibility(View.GONE);
				}
				TAG = 3;
			} else if (body.getStatus().equals("2") && body.getStage().equals("5")) {
				// ToastUtil.show("现场大会可编辑,测试中。。。");
				holder.img_delete.setVisibility(View.VISIBLE);
				tv_send_works.setVisibility(View.VISIBLE);
//				img_delete.setVisibility(View.GONE);
				if (TextUtils.isEmpty(body.getYk_thumbnail())) {
					//2016年12月20日修改：复赛结束后，无论有没有视频作品，都不可再提交视频作品
					img_add_video.setVisibility(View.GONE);
				}else{
					img_add_video.setVisibility(View.GONE);
				}
				TAG = 3;
			} else {
				// ToastUtil.show("作品提交超过24小时，已无法修改");
				tv_send_works.setVisibility(View.GONE);
				holder.img_delete.setVisibility(View.GONE);
				img_delete.setVisibility(View.GONE);
				if (position == mCache.size()) {
					holder.image.setVisibility(View.GONE);
				}
				if (TextUtils.isEmpty(body.getYk_thumbnail())) {
					img_selected_video.setVisibility(View.GONE);
					img_video_icon.setVisibility(View.GONE);
					img_delete.setVisibility(View.GONE);
					img_add_video.setVisibility(View.GONE);
				}
			}
			holder.selectedLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mCache.remove(position);
					notifyDataSetChanged();
				}
			});

			if (position == mCache.size()) {
				holder.img_delete.setVisibility(View.GONE);
				holder.image.setImageResource(R.drawable.icon_addpic_unfocused);
				if (position == MAX_SIZE) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				if (mCache.get(position).startsWith("/")) {
					BitmapHelp.getBitmapUtils().display(holder.image,
							mCache.get(position));
				} else {
					BitmapHelp.getBitmapUtils().display(
							holder.image,
							StringUtil.getImgeUrl(mCache.get(position))
									+ Constant.MIDDLE_IMAGE_SUFFIX);
				}
			}
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
			public ImageView img_delete;// 作品图片右上角的删除按钮
			public LinearLayout selectedLayout;
		}

		public Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if (adapter!=null) {
						adapter.notifyDataSetChanged();
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == mCache.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						}
					}
				}
			}).start();
		}
	}

	public void requestYouKuToken() {
		RequestManager.request(this, new YouKuTokenParams("android"),
				YouKuTokenResponse.class, PkWorksManageActivity.this, SPUtils
						.instance().getSocialPropEntity()
						.getApp_socail_server());
	}

	private List<String> filePathList; // 本地视频路径

	private static final int VIDEO_CAPTURE = 0;

	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			filePathList = intent.getStringArrayListExtra("list");
			// ToastUtil.show("本地视频路径--->" + filePathList.get(0));
			// 设置选择的视频可见并且设置布局
			img_selected_video.setVisibility(View.VISIBLE);
			img_video_icon.setVisibility(View.VISIBLE);
			img_video_icon.bringToFront();
			img_delete.setVisibility(View.VISIBLE);
			img_add_video.setVisibility(View.GONE);

			img_selected_video.setImageBitmap(Bimp
					.getVideoThumbnail(filePathList.get(0)));
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

	@Override
	protected void onDestroy() {
		unregisterReceiver(videoBroadcastReceiver);
		super.onDestroy();
	}

	private void uploadVideo(String locVideoPath) {
		/**
		 * 内网环境：425a15656116e199 外网环境：2442dc16694b06f1
		 */
		uploader = YoukuUploader.getInstance(Constant.PK_VIDEO_UPLOAD_ID, "",
				getApplicationContext());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", EncodeAES);
		HashMap<String, String> uploadInfo = new HashMap<String, String>();
		uploadInfo.put("title", tv_ts_uname_info.getText().toString() + "_"
				+ worksNo + "_" + tv_ts_cata_title_info.getText().toString());
		uploadInfo.put("tags", "优酷,EVA");
		uploadInfo.put("file_name", locVideoPath);

		uploader.upload(params, uploadInfo, new IUploadResponseHandler() {

			@Override
			public void onStart() {
				LogUtils.v("Main upload onStart");
				// progressBar.setProgress(0);
				percent.setText("等待中");
			}

			@Override
			public void onSuccess(JSONObject response) { // {"video_id":"XMTUxODE0NTE0OA=="}
				LogUtils.v("Main upload onSuccess "+response.toString());
				try {
					yk_video_id = response.getString("video_id");
					updateVideo(yk_video_id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onProgressUpdate(int counter) {
				LogUtils.v("Main upload onProgress "+ counter + "");
				// progressBar.setProgress(counter);
				percent.setText(counter + "%");
			}

			@Override
			public void onFailure(JSONObject errorResponse) {
				Message message = new Message();
				message.what = 202;
				handlerPostTopic.sendMessage(message);
			}

			@Override
			public void onFinished() {
				LogUtils.v("Main upload onFinished");
				percent.setText("完成");
			}
		});
	}

	/**
	 * 视频上传到优酷后更新至服务端
	 */
	private void updateVideo(String yk_video_id) {
		RequestManager.request(this, new YoukuVideoUpdateParams(works_id,
				yk_video_id), GiveOkResponse.class, this, SPUtils.instance()
				.getSocialPropEntity().getApp_socail_server());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (TAG) {
		case 0:
			if(body.getWorks_0().getImgs()!= null && body.getWorks_0().getImgs().length != 0) {
				imageBrower(position, body.getWorks_0().getImgs());
			}
			break;
		case 1:
			imageBrower(position, body.getWorks_1().getImgs());
			break;
		case 2:
			imageBrower(position, body.getWorks_2().getImgs());
			break;
		case 3:
			// 如果可编辑状态下
			String path = (String) parent.getAdapter().getItem(position);
			if (position == mCache.size()) {
				FLAG = true;
				popWindow = new SelectPicPopupWindow(
						PkWorksManageActivity.this, itemsOnClick, FLAG);
				popWindow.showAtLocation(parentView, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			break;

		default:
			break;
		}
	}

}
