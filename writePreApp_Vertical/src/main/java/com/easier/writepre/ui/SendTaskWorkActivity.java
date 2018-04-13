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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.easier.writepre.R;
import com.easier.writepre.adapter.ImageListViewAdapter;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.SquarePostAddParams;
import com.easier.writepre.param.SquarePostAddParams.ImgUrlPostAdd;
import com.easier.writepre.param.TaskWorkStuSubmitParams;
import com.easier.writepre.param.TaskWorkTeacherPubParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TaskWorkStuSubmitResponse;
import com.easier.writepre.response.TaskWorkTeacherPubResponse;
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

/**
 * 发布作业
 *
 * @author zhoulu
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SendTaskWorkActivity extends BaseActivity {
    private String level = "5";
    private int modeType = 0;
    public static final int TYPE_TEACHER = 1;//老师
    public static final int TYPE_STUDENT = 2;//学生
    private static final int MAX_SIZE = 6;
    private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);
    private ListView lv_images_list;
    private TextView tv_btn;
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
    private List<ImgUrlPostAdd> imgUrlList; // 上传到阿里云的图片路径的集合
    private List<String> ossPathList;
    private boolean mDestroy = false;
    private boolean mVisible = false;
    private boolean isShared = false;

    private String path; // 集字生成图片所属路径

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    //视频截图的文件尾标识
    private final String VIDEO_IMAGE_END = "videoImage.png";
    private ImageListViewAdapter imageListViewAdapter;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
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
                case 200: // 发布成功
                    ossPathList.clear();
                    System.gc();
                    switch (modeType) {
                        case TYPE_TEACHER:
                            ToastUtil.show("作业发布成功");
                            break;
                        case TYPE_STUDENT:
                            ToastUtil.show("作业提交成功");
                            break;
                    }
                    setResult(RESULT_OK);
                    Bimp.map.clear(); // 发帖成功后清除需要蒙版效果图片的集合
                    finish();
                    break;
                case 201: // 发布失败
                    if (msg.obj != null) {
                        ToastUtil.show(msg.obj.toString());
                    } else {
                        switch (modeType) {
                            case TYPE_TEACHER:
                                ToastUtil.show("作业发布失败");
                                break;
                            case TYPE_STUDENT:
                                ToastUtil.show("作业提交失败");
                                break;
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_send_task,
                null);
        setContentView(parentView);
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
        ossPathList = new ArrayList<String>();
        id = getIntent().getStringExtra("id");
        path = getIntent().getStringExtra("path");
        modeType = getIntent().getIntExtra("modeType", 0);
        if (!TextUtils.isEmpty(path)) {
            mCache.add(path);
            mHandler.sendEmptyMessage(0);
        }
        switch (modeType) {
            case TYPE_TEACHER:
                setTopTitle("发布作业");
                tv_btn.setText("发布");
                break;

            case TYPE_STUDENT:
                setTopTitle("提交作业");
                tv_btn.setText("提交");
                findViewById(R.id.rel_et).setVisibility(View.GONE);
                break;
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
        et_content = (EditText) findViewById(R.id.et_content);
        tv_input_left = (TextView) findViewById(R.id.tv_input_left);
        lv_images_list = (ListView) findViewById(R.id.lv_images_list);
        tv_btn = (TextView) findViewById(R.id.tv_btn);
        tv_btn.setOnClickListener(this);
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
            }
        });

        btn_showLocation = (Button) findViewById(R.id.btn_showlocation);
        btn_showLocation.setOnClickListener(this);

        imageListViewAdapter = new ImageListViewAdapter(this, lv_images_list, MAX_SIZE);
        lv_images_list.setAdapter(imageListViewAdapter);
        imageListViewAdapter.setData(null);
        imageListViewAdapter.setOnItemClickListener(new ImageListViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                LogUtils.e("================");
                String path = imageListViewAdapter.getItem(position);
                if ("addItem".equals(path)) {
                    popWindow.showAtLocation(parentView, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else if (path.endsWith(VIDEO_IMAGE_END)) {
                    File file = new File(path);
                    Intent intent = new Intent(SendTaskWorkActivity.this, MediaActivity.class);
                    intent.putExtra(MediaActivity.URL, file.getAbsolutePath().replace(VIDEO_IMAGE_END, ""));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SendTaskWorkActivity.this,
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
                imageListViewAdapter.setImagMax(MAX_SIZE);
                popWindow.setVideoSupport(false);
            }
        });

        popWindow = new SelectPicPopupWindow(this, itemsOnClick);
        findViewById(R.id.ll_city).setVisibility(View.GONE);
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
                    Intent intent = new Intent(SendTaskWorkActivity.this,
                            ImageSelectionActivity.class);
                    intent.putExtra("maxSize", MAX_SIZE);
                    intent.putStringArrayListExtra("data", mCache);
                    startActivityForResult(intent,
                            ImageSelectionActivity.REQUEST_CODE_ADD);
                    break;
                case R.id.item_popupwindows_Video:
                    //录制视频
                    Intent videoIntent = new Intent(SendTaskWorkActivity.this, VideoRecordActivity.class);
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
        if (imageListViewAdapter != null) {
            if (!mCache.isEmpty()) {
                if (mCache.get(0).endsWith(VIDEO_IMAGE_END)) {
                    popWindow.setVideoSupport(true);
                    imageListViewAdapter.setImagMax(1);
                } else {
                    imageListViewAdapter.setImagMax(MAX_SIZE);
                    popWindow.setVideoSupport(false);
                }
            }
            imageListViewAdapter.setData(mCache);
            imageListViewAdapter.setSelectedData(mCache);
        }
//        setTopRightTxt(mCache.size() > 0
//                || !TextUtils.isEmpty(et_content.getText().toString()) ? "完成"
//                : null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_showlocation:
                if (mLocationClient == null) {
                    mLocationClient = new LocationClient(
                            SendTaskWorkActivity.this.getApplicationContext());
                }
                if (mMyLocationListener == null) {
                    mMyLocationListener = new MyLocationListener();
                    mLocationClient.registerLocationListener(mMyLocationListener);
                    setLoactionOption();
                }
                mLocationClient.start();
                break;
            case R.id.tv_btn:
                if (LoginUtil.checkLogin(this)) {
                    if (modeType == TYPE_TEACHER) {
                        //老师发布作业文字不能为空
                        if (TextUtils.isEmpty(et_content.getText().toString())) {
                            ToastUtil.show("老师，文字描述下作业内容吧🙂");
                            return;
                        }
                        if (imageListViewAdapter.getSelectedData().size() == 0) {
                            sendTopick();
                        } else {
                            ArrayList<String> commitData = new ArrayList<String>();
                            for (int i = 0; i < imageListViewAdapter.getSelectedData().size(); i++) {
                                if (imageListViewAdapter.getSelectedData().get(i).endsWith(VIDEO_IMAGE_END)) {
                                    commitData.add(imageListViewAdapter.getSelectedData().get(i).replace(VIDEO_IMAGE_END, ""));
                                }
                            }
                            commitData.addAll(imageListViewAdapter.getSelectedData());
                            commitFilesOss(commitData, mHandler);
                        }
                    } else {
                        //学生提交作业 只能提交 图片
                        if (imageListViewAdapter.getSelectedData().size() == 0) {
                            ToastUtil.show("请选择需要提交的作业图片");
                            return;
                        }
                        ArrayList<String> commitData = new ArrayList<String>();
                        for (int i = 0; i < imageListViewAdapter.getSelectedData().size(); i++) {
                            if (imageListViewAdapter.getSelectedData().get(i).endsWith(VIDEO_IMAGE_END)) {
                                commitData.add(imageListViewAdapter.getSelectedData().get(i).replace(VIDEO_IMAGE_END, ""));
                            }
                        }
                        commitData.addAll(imageListViewAdapter.getSelectedData());
                        commitFilesOss(commitData, mHandler);
                    }
                }
                break;
            default:
                break;
        }

    }

    public void sendTopick() {
        dlgLoad.loading();
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
        switch (modeType) {
            case TYPE_TEACHER:
                TaskWorkTeacherPubParams taskWorkTeacherPubParams = new TaskWorkTeacherPubParams(level, et_content.getText().toString().trim(), imgUrlList);
                RequestManager.request(this, taskWorkTeacherPubParams,
                        TaskWorkTeacherPubResponse.class, this,
                        SPUtils.instance().getSocialPropEntity()
                                .getApp_school_server());
                break;

            case TYPE_STUDENT:
                TaskWorkStuSubmitParams taskWorkStuSubmitParams = new TaskWorkStuSubmitParams(id, et_content.getText().toString().trim(), imgUrlList);
                RequestManager.request(this, taskWorkStuSubmitParams,
                        TaskWorkStuSubmitResponse.class, this,
                        SPUtils.instance().getSocialPropEntity()
                                .getApp_school_server());
                break;
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
            if (response instanceof TaskWorkTeacherPubResponse || response instanceof TaskWorkStuSubmitResponse) {
                Message message = new Message();
                message.what = 200;
                handlerPostTopic.sendMessage(message);
            }
        } else {
            if (response instanceof TaskWorkTeacherPubResponse || response instanceof TaskWorkStuSubmitResponse) {
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
        mCache.addAll(imageListViewAdapter.getSelectedData());
    }
}
