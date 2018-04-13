package com.easier.writepre.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.easier.writepre.R;
import com.easier.writepre.adapter.ImageAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.GroupNoticePostAddParams;
import com.easier.writepre.param.SquarePostAddParams;
import com.easier.writepre.param.SquarePostAddParams.ImgUrlPostAdd;
import com.easier.writepre.param.SquarePostAddParams.MarkNoPostAdd;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GroupNoticePostAddResponse;
import com.easier.writepre.response.SquarePostAddResponse;
import com.easier.writepre.rongyun.WPNoticeMessage;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.MediaFile;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.video.VideoRecordActivity;
import com.easier.writepre.widget.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SendTopicActivity extends BaseActivity {

    //发帖模块区别
    public static final String MODE_TYPE = "mode_type";

    //发帖模块类别
    public static final int MODE_SQUARE = 0;//广场贴
    public static final int MODE_CIRCLE = 1;//圈子贴
    public static final int MODE_ACTIVE = 2;//活动贴
    public static final int MODE_NOTICE = 3;//群公告
    private int modeType = 0;

    public static final int REQUEST_CODE_SELECTION = 100;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_CLEAR_TOP = 1;
    private static final int MAX_SIZE = 6;
    private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);
    private GridView noScrollgridview;
    private EditText et_content;
    private TextView tv_input_left;
    private Button btn_showLocation;// 显示地理位置
    private double longitude, latitude; // 经纬度
    private String city = "";// 城市
    // private LocationManager myLocationManager;
    protected int num = 140;// 字数限制

    private View parentView;

    private SelectPicPopupWindow popWindow;

    private String photoName;// 拍照图片名

    //小视频地址
    private String vod_url = "";

    public int flag = 0;// 上传的图片只要有失败的，为1，全都成功，为0;

    private String id;

    private static final int PHOTO_GRAPH = 104;// 拍照

    private List<Double> coordList; // 广场帖子发布经纬度坐标
    private List<MarkNoPostAdd> markNoList; // 圈子中的标签
    private List<ImgUrlPostAdd> imgUrlList; // 上传到阿里云的图片路径的集合
    private List<String> ossPathList;
    private ImageAdapter mAdapter;
    private boolean mDestroy = false;
    private boolean mVisible = false;
    private boolean isShared = false;

    private boolean from;  // true 表示从集字页面跳转过来

    private String path; // 集字生成图片所属路径

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    //视频截图的文件尾标识
    private final String VIDEO_IMAGE_END = "videoImage.png";

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (TextUtils.isEmpty(path)) {
                        if (isFinished() || !mVisible) {
                            return;
                        }
                    }
                    onUpdate();
                    break;
                case COMMIT_FILE_OSS_ALL_FAIL:
                    dlgLoad.dismissDialog();
                    ToastUtil.show("文件上传失败");
                    break;
                case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
                case COMMIT_FILE_OSS_ALL_SUCCESS:
                    dlgLoad.dismissDialog();
                    ossPathList.clear();
                    ossPathList.addAll((ArrayList<String>) msg.obj);
                    sendTopick();
                    break;
                default:
                    break;
            }

        }
    };

    Handler handlerPostTopic = new Handler() {
        public void handleMessage(Message msg) {
            dlgLoad.dismissDialog();
            switch (msg.what) {
                case 200: // 發帖成功
                    ossPathList.clear();
                    System.gc();
                    ToastUtil.show("发帖成功");
                    setResult(RESULT_OK);
                    Bimp.map.clear(); // 发帖成功后清除需要蒙版效果图片的集合
                    if (from) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        int index = SocialMainView.TAB_SQUARE;
                        intent.setClass(SendTopicActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
                        intent.putExtra(SocialMainView.TAB_INDEX, index);
                        intent.putExtra(SocialMainView.ITEM_SQUARE_INDEX, SocialMainView.ITEM_1);
                        SendTopicActivity.this.startActivity(intent);
                    }
                    finish();
                    break;
                case 201: // 發帖失敗
                    if (msg.obj != null) {
                        ToastUtil.show(msg.obj.toString());
                    } else {
                        ToastUtil.show("发帖失败");
                    }
                    break;
                case 202://融云发送消息完成
                    ossPathList.clear();
                    System.gc();
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_send_topic,
                null);
        setContentView(parentView);
        modeType = getIntent().getIntExtra(MODE_TYPE, MODE_SQUARE);
        from = getIntent().getBooleanExtra("from", false);    // true表示从集字页面跳转过来
        initView();
        init();
        initData();
        if (savedInstanceState != null) {
            mCache = (ArrayList<String>) savedInstanceState
                    .getSerializable("photoData");
            if (!TextUtils.isEmpty(savedInstanceState.getString("photoName"))
                    && !savedInstanceState.getString("photoName").contains(
                    "null")) {
                photoName = savedInstanceState.getString("photoName");
                if (new File(FileUtils.SD_IMAGES_PATH, photoName).exists()) {
                    mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
                }
            } else {
                if (new File(FileUtils.SD_IMAGES_PATH, "null").exists()) {
                    new File(FileUtils.SD_IMAGES_PATH, "null").delete();
                }
                ToastUtil.show("拍照失败,请重试!");
            }
            onUpdate();
            photoName = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void init() {
        // imageUploadFailSize = new ArrayList<String>();
        // imageUploadSucessSize = new ArrayList<String>();
        // ossImageSuccList = new ArrayList<String>();
        ossPathList = new ArrayList<String>();
        id = getIntent().getStringExtra("id");
        path = getIntent().getStringExtra("path");
        if (!TextUtils.isEmpty(path)) {
            mCache.add(path);
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onStop() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onStop();
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            LogUtils.e("city = " + arg0.getCity());
            city = arg0.getCity();
            if (isDestroyed) {
                return;
            }
            mLocationClient.stop();
            if (arg0.getCity() == null) {
                btn_showLocation.setText("定位失败,请重试");
                return;
            }
            // city = arg0.getCity().substring(0, arg0.getCity().length() - 1);
            longitude = arg0.getLongitude();
            latitude = arg0.getLatitude();
            btn_showLocation.setText(city);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // user_id = SPUtils.instance().getLoginEntity().get_id();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.getClipData() == null) {
            return;
        }
        int count = intent.getClipData().getItemCount();
        if (count < 1
                || TextUtils.isEmpty(intent.getClipData().getItemAt(0)
                .getText().toString())) {
            Toast.makeText(this, "没有可分享的内容!", Toast.LENGTH_SHORT).show();
            onBack();
        } else {
            isShared = true;
            String urlContent = intent.getClipData().getItemAt(0).getText()
                    .toString();
            LogUtils.i("urlContent:" + urlContent);
            et_content.setText(urlContent);
        }

    }


    private void initView() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.index));
        var.add("发帖");
        setTopTitle("发帖");
        if (modeType == MODE_SQUARE) {
            var.add("广场");
            YouMengType.onEvent(this, var, 1, "广场");
        } else if (modeType == MODE_CIRCLE) {
            var.add("圈子");
            YouMengType.onEvent(this, var, 1, "圈子");
        } else if (modeType == MODE_ACTIVE) {
            var.add("活动");
            YouMengType.onEvent(this, var, 1, "活动");
        } else {
            var.add("群公告");
            YouMengType.onEvent(this, var, 1, "群公告");
            setTopTitle("发布公告");
        }

        et_content = (EditText) findViewById(R.id.et_content);
        tv_input_left = (TextView) findViewById(R.id.tv_input_left);

        et_content.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = num - s.length();
                tv_input_left.setText("" + (number >= 0 ? number : 0));
                if (!isShared) {
                    selectionStart = et_content.getSelectionStart();
                    selectionEnd = et_content.getSelectionEnd();
                    if (temp.length() > num) {
                        s.delete(selectionStart - 1, selectionEnd);
                        int tempSelection = selectionEnd;
                        et_content.setText(s);
                        et_content.setSelection(tempSelection);// 设置光标在最后
                    }
                }
                setTopRightTxt(mCache.size() > 0 || s.length() != 0 ? "完成"
                        : null);
            }
        });

        btn_showLocation = (Button) findViewById(R.id.btn_showlocation);
        btn_showLocation.setOnClickListener(this);

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        mAdapter = new ImageAdapter(this, noScrollgridview, MAX_SIZE);
        noScrollgridview.setAdapter(mAdapter);
        mAdapter.setData(null);

        mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                String path = mAdapter.getItem(position);
                if ("addItem".equals(path)) {
                    popWindow.showAtLocation(parentView, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else if (path.endsWith(VIDEO_IMAGE_END)) {
                    File file = new File(path);
                    Intent intent = new Intent(SendTopicActivity.this, MediaActivity.class);
                    intent.putExtra(MediaActivity.URL, file.getAbsolutePath().replace(VIDEO_IMAGE_END, ""));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SendTopicActivity.this,
                            ImageDetailActivity.class);
                    intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
                    intent.putExtra("position", position);
                    intent.putExtra("MAX_SIZE", MAX_SIZE);
                    intent.putStringArrayListExtra("selected_data", mCache);
                    intent.putStringArrayListExtra("data", mCache);
                    startActivityForResult(intent,
                            ImageDetailActivity.REQUEST_CODE_DELETE);
                }
            }

            @Override
            public void onDelete(int position, View v) {
                if (modeType == MODE_SQUARE || modeType == MODE_CIRCLE) {
                    if (!mAdapter.getSelectedData().isEmpty()) {
                        if (mAdapter.getSelectedData().get(0).endsWith(VIDEO_IMAGE_END)) {
                            popWindow.setVideoSupport(true);
                            mAdapter.setImagMax(1);
                        } else {
                            mAdapter.setImagMax(MAX_SIZE);
                            popWindow.setVideoSupport(false);
                        }
                    } else {
                        mAdapter.setImagMax(MAX_SIZE);
                        popWindow.setVideoSupport(true);
                    }
                }
            }
        });

        popWindow = new SelectPicPopupWindow(this, itemsOnClick);

        //广场贴/圈子帖支持小视频
        if (modeType == MODE_SQUARE || modeType == MODE_CIRCLE) {
            popWindow.setVideoSupport(true);
            ((TextView) findViewById(R.id.txt_file_tag)).setText("图片/小视频");
        }
        if (modeType == MODE_NOTICE) {
            findViewById(R.id.ll_city).setVisibility(View.GONE);
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
                    Intent intent = new Intent(SendTopicActivity.this,
                            ImageSelectionActivity.class);
                    intent.putExtra("maxSize", MAX_SIZE);
                    intent.putStringArrayListExtra("data", mCache);
                    startActivityForResult(intent,
                            ImageSelectionActivity.REQUEST_CODE_ADD);
                    break;
                case R.id.item_popupwindows_Video:
                    //选择视频
//                    Intent videoIntent = new Intent(SendTopicActivity.this, HomeFragmentActivity.class);
                    //录制视频
                    Intent videoIntent = new Intent(SendTopicActivity.this, VideoRecordActivity.class);
                    startActivityForResult(videoIntent, 999);
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 拍照
     */
    private void formCamera() {
        photoName = System.currentTimeMillis() + ".png";
        new File(FileUtils.SD_IMAGES_PATH).mkdirs();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    protected boolean isFinished() {
        return isFinishing() || mDestroy;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mVisible = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroy = true;
        if (mCache != null) {
            mCache.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED)
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
                        mHandler.sendEmptyMessage(0);
                    }
                }
            } else if (requestCode == PHOTO_GRAPH) {
                if (TextUtils.isEmpty(photoName) || photoName.contains("null")) {
                    return;
                }
                mCache.add(FileUtils.SD_IMAGES_PATH + photoName);
                photoName = null;
                mHandler.sendEmptyMessage(0);
            } else if (requestCode == 999) {
                mCache.clear();
                if (data != null) {
                    //选择小视频返回
//                    ArrayList<String> videoPaths = data.getStringArrayListExtra("list");
//                    if (videoPaths != null && videoPaths.size() != 0) {
//                        for (int i = 0; i < videoPaths.size(); i++) {
//                            File videoFile = new File(videoPaths.get(i));
//                            if (new File(videoFile.getAbsolutePath() + VIDEO_IMAGE_END).exists()){
//                                Bimp.saveBitmap(Bimp.getVideoThumbnail(videoFile.getAbsolutePath()), videoFile.getAbsolutePath() + VIDEO_IMAGE_END);
//                            }
//                            mCache.add(videoFile.getAbsolutePath() + VIDEO_IMAGE_END);
//                        }
//                    }
                    //录制视频返回
                    String videoPath = data.getStringExtra("video_path");
                    if (!TextUtils.isEmpty(videoPath)) {
                        File videoFile = new File(videoPath);
                        if (videoFile.exists()) {
                            if (!new File(videoFile.getAbsolutePath() + VIDEO_IMAGE_END).exists()) {
                                Bimp.saveBitmap(Bimp.getVideoThumbnail(videoFile.getAbsolutePath()), videoFile.getAbsolutePath() + VIDEO_IMAGE_END);
                            }
                            mCache.add(videoFile.getAbsolutePath() + VIDEO_IMAGE_END);
                        }
                    }
                }
                mHandler.sendEmptyMessage(0);

            }
        }

    }

    private void onUpdate() {
        if (mAdapter != null) {
            if (!mCache.isEmpty()) {
                if (mCache.get(0).endsWith(VIDEO_IMAGE_END)) {
                    popWindow.setVideoSupport(true);
                    mAdapter.setImagMax(1);
                } else {
                    mAdapter.setImagMax(MAX_SIZE);
                    popWindow.setVideoSupport(false);
                }
            }
            mAdapter.setData(mCache);
            mAdapter.setSelectedData(mCache);
        }
        setTopRightTxt(mCache.size() > 0
                || !TextUtils.isEmpty(et_content.getText().toString()) ? "完成"
                : null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_showlocation:
                if (mLocationClient == null) {
                    mLocationClient = new LocationClient(
                            SendTopicActivity.this.getApplicationContext());
                }
                if (mMyLocationListener == null) {
                    mMyLocationListener = new MyLocationListener();
                    mLocationClient.registerLocationListener(mMyLocationListener);
                    setLoactionOption();
                }
                mLocationClient.start();
                break;

            default:
                break;
        }

    }

    public void sendTopick() {
        dlgLoad.loading();
        if (coordList == null) {
            coordList = new ArrayList<Double>();
        } else {
            coordList.clear();
        }
        if (markNoList == null) {
            markNoList = new ArrayList<MarkNoPostAdd>();
        } else {
            markNoList.clear();
        }
        if (imgUrlList == null) {
            imgUrlList = new ArrayList<ImgUrlPostAdd>();
        } else {
            imgUrlList.clear();
        }
        vod_url = "";
        if (ossPathList.size() > 0) {
            for (int i = 0; i < ossPathList.size(); i++) {
                if (MediaFile.isImageFileType(ossPathList.get(i))) {
                    //图片文件地址
                    ImgUrlPostAdd imgUrl = new SquarePostAddParams().new ImgUrlPostAdd();
                    imgUrl.setUrl(ossPathList.get(i));
                    imgUrlList.add(imgUrl);
                } else if (MediaFile.isVideoFileType(ossPathList.get(i))) {
                    //视频文件地址
                    vod_url = ossPathList.get(i);
                } else if (MediaFile.isAudioFileType(ossPathList.get(i))) {
                    //音频文件地址
                }
            }
        }
        if (longitude != 0.0 && latitude != 0.0) {
            coordList.add(longitude); // 经度
            coordList.add(latitude); // 纬度
        }
        switch (modeType) {
            case MODE_SQUARE:// 广场贴
                SquarePostAddParams square = new SquarePostAddParams(et_content
                        .getText().toString().trim(), coordList, city.replaceAll(
                        "\\s*", ""), imgUrlList, vod_url, markNoList);
                square.setFlag(false);
                RequestManager.request(SendTopicActivity.this, square,
                        SquarePostAddResponse.class, SendTopicActivity.this,
                        SPUtils.instance().getSocialPropEntity()
                                .getApp_socail_server());
                break;
            case MODE_CIRCLE:// 圈子帖
                SquarePostAddParams circle = new SquarePostAddParams(modeType, id,
                        et_content.getText().toString().trim(), coordList,
                        city.replaceAll("\\s*", ""), imgUrlList, vod_url,
                        markNoList);
                circle.setFlag(true);
                RequestManager.request(SendTopicActivity.this, circle,
                        SquarePostAddResponse.class, SendTopicActivity.this,
                        SPUtils.instance().getSocialPropEntity()
                                .getApp_socail_server());
                break;
            case MODE_ACTIVE:// 活动贴topic_id
                SquarePostAddParams active = new SquarePostAddParams(modeType, id, et_content
                        .getText().toString().trim(), coordList, city.replaceAll(
                        "\\s*", ""), imgUrlList, vod_url, markNoList);
                active.setFlag(false);
                RequestManager.request(SendTopicActivity.this, active,
                        SquarePostAddResponse.class, SendTopicActivity.this,
                        SPUtils.instance().getSocialPropEntity()
                                .getApp_socail_server());
                break;
            case MODE_NOTICE:// 群公告topic_id
                //发送群公告到后台服务器同时发送公告消息给融云
                GroupNoticePostAddParams groupNoticePostAddParams = new GroupNoticePostAddParams(id, et_content.getText().toString().trim(), imgUrlList);
                RequestManager.request(SendTopicActivity.this, groupNoticePostAddParams,
                        GroupNoticePostAddResponse.class, SendTopicActivity.this,
                        SPUtils.instance().getSocialPropEntity()
                                .getApp_socail_server());
                break;
        }
    }

    /**
     * 发送
     */
    @Override
    public void onTopRightTxtClick(View view) {
        if (LoginUtil.checkLogin(this)) {
            if (mAdapter.getSelectedData().size() == 0) {
                sendTopick();
            } else {
                ArrayList<String> commitData = new ArrayList<String>();
                for (int i = 0; i < mAdapter.getSelectedData().size(); i++) {
                    if (mAdapter.getSelectedData().get(i).endsWith(VIDEO_IMAGE_END)) {
                        commitData.add(mAdapter.getSelectedData().get(i).replace(VIDEO_IMAGE_END, ""));
                    }
                }
                commitData.addAll(mAdapter.getSelectedData());
                commitFilesOss(commitData, mHandler);
            }
        }
    }

    private void setLoactionOption() {
        // if (!gpsIsOpen()) {
        // Toast.makeText(getBaseContext(), "请开启GPS功能!",
        // Toast.LENGTH_LONG).show();
        // return;
        // }
        // // 获取位置信息
        // myLocationManager = (LocationManager)
        // this.getSystemService(Context.LOCATION_SERVICE);
        // // 查找服务信息
        // Criteria criteria = new Criteria();
        // criteria.setAccuracy(Criteria.ACCURACY_FINE); // 定位精度: 最高
        // criteria.setAltitudeRequired(false); // 海拔信息：不需要
        // criteria.setBearingRequired(false); // 方位信息: 不需要
        // criteria.setCostAllowed(true); // 是否允许付费
        // criteria.setPowerRequirement(Criteria.POWER_LOW); // 耗电量: 低功耗
        // String provider = myLocationManager.getBestProvider(criteria, true);
        // // 获取GPS信息
        // myLocationManager.requestLocationUpdates(provider, 1000, 5,
        // locationListener);
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0); // 10分钟扫描1次
        // 需要地址信息，设置为其他任何值（string类型，且不能为null）时，都表示无地址信息。
        option.setAddrType("all");
        // 设置是否返回POI的电话和地址等详细信息。默认值为false，即不返回POI的电话和地址信息。
        // option.setPoiExtraInfo(true);
        // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setProdName("通过GPS定位我当前的位置");
        // 禁用启用缓存定位数据
        option.disableCache(true);
        // 设置最多可返回的POI个数，默认值为3。由于POI查询比较耗费流量，设置最多返回的POI个数，以便节省流量。
        // option.setPoiNumber(3);
        // 设置定位方式的优先级。
        // 当gps可用，而且获取了定位结果时，不再发起网络请求，直接返回给用户坐标。这个选项适合希望得到准确坐标位置的用户。如果gps不可用，再发起网络请求，进行定位。
        option.setPriority(LocationClientOption.GpsFirst);
        mLocationClient.setLocOption(option);
    }

    // 判断当前是否开启了GPS
    private boolean gpsIsOpen() {
        boolean isOpen = true;
        LocationManager alm = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {// 没有开启GPS
            isOpen = false;
        }
        return isOpen;
    }

    // 监听GPS位置改变后得到新的经纬度
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
                longitude = location.getLongitude(); // 经度
                latitude = location.getLatitude(); // 纬度
                // 获取国家，省份，城市的名称
                String str = getAddress(location).toString();

                String country = str.substring(
                        str.indexOf("countryName=") + 12,
                        str.indexOf(",hasLatitude"));
                String admin = str.substring(str.indexOf("admin=") + 6,
                        str.indexOf(",sub-admin"));
                city = str.substring(str.indexOf("locality=") + 9,
                        str.indexOf(",thoroughfare"));
                btn_showLocation.setText(city);
            } else {
                btn_showLocation.setText("获取不到数据");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    // 获取地址信息
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    class MyFileSendRunnable implements Runnable {
        String message;
        String circleID;
        String noticeID;

        public MyFileSendRunnable(String message, String circleID, String noticeID) {
            this.message = message;
            this.circleID = circleID;
            this.noticeID = noticeID;
        }

        @Override
        public void run() {
            WPNoticeMessage wpNoticeMessage = WPNoticeMessage.obtain(message, circleID, noticeID);
            io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(id, Conversation.ConversationType.GROUP, wpNoticeMessage);
            if (RongIM.getInstance() != null)
                RongIM.getInstance().sendMessage(message, "通知公告", "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(io.rong.imlib.model.Message message) {
                        LogUtils.e("onAttached=====");
                    }

                    @Override
                    public void onSuccess(io.rong.imlib.model.Message message) {
                        LogUtils.e("onSuccess=====message" + ((WPNoticeMessage) message.getContent()).getMessage());
                        handlerPostTopic.sendEmptyMessage(202);
                    }

                    @Override
                    public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                        LogUtils.e("onError======");
                        handlerPostTopic.sendEmptyMessage(202);
                    }
                });
        }
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
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof SquarePostAddResponse) {
                Message message = new Message();
                message.what = 200;
                handlerPostTopic.sendMessage(message);
            } else if (response instanceof GroupNoticePostAddResponse) {
                GroupNoticePostAddResponse groupNoticePostAddResponse = (GroupNoticePostAddResponse) response;
                //公告发送成功后通知融云
                handlerPostTopic.post(new MyFileSendRunnable(et_content.getText().toString().trim(), id, TextUtils.isEmpty(groupNoticePostAddResponse.getRepBody().getPub_news_id()) ? "" : groupNoticePostAddResponse.getRepBody().getPub_news_id()));
            }
        } else {
            if (response instanceof SquarePostAddResponse) {
                Message message = new Message();
                message.what = 201;
                message.obj = response.getResMsg();
                handlerPostTopic.sendMessage(message);
            }
        }

    }

    @Override
    public void updateSelectedData() {
        mCache.clear();
        mCache.addAll(mAdapter.getSelectedData());
    }
}
