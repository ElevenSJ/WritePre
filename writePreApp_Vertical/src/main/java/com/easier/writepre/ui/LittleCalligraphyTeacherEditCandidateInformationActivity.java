package com.easier.writepre.ui;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TecXsfStuInfoBindParams;
import com.easier.writepre.param.TecXsfStuInfoGetParams;
import com.easier.writepre.param.TecXsfStuInfoVerifyParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TecXsfStuInfoBindResponse;
import com.easier.writepre.response.TecXsfStuInfoGetResponse;
import com.easier.writepre.response.TecXsfStuInfoVerifyResponse;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.ui.myinfo.EditIdCardActivity;
import com.easier.writepre.ui.myinfo.EditInfoReuseActivity;
import com.easier.writepre.ui.myinfo.EditRealNameActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyListView;
import com.easier.writepre.widget.SelectPicPopupWindow;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 小书法师报名信息填写页面
 */
public class LittleCalligraphyTeacherEditCandidateInformationActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private TextView tv_failure_reminder;

    private MyListView listView;

    private Button btn_bind;

    private List<MyListData> list;

    public static String left_text[] = new String[]{"姓名", "身份证号", "所在城市", "详细地址", "培训机构或学校", "培训机构或学校联系方式"};
    public static String right_text[] = new String[]{null, null, null, null, null, null};
    private static final int NAME = 0,
            IDCARD = 1,
            CITY = 2,
            ADDRESS = 3,
            SCHOOL = 4,
            SCHOOLTEL = 5;

    private MyListAdapter adapter;

    private ImageView img_photo;
    private ImageView img_add;
    private ImageView img_delete;

    private String photoName;
    private String photoPath;
    private String ossPath;
    private static final int PHOTO = 103; // 不剪裁
    private static final int PHOTO_GRAPH = 104;// 拍照
    private static final int PHOTO_ZOOM = 105; // 缩放
    private static final int PHOTO_RESOULT = 106;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private static final int MAX_SIZE = 1;
    private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);

    private static final String EDIT_TYPE = "EDIT_TYPE";
    private static final int EDIT_IDCARD = 21;
    private static final int EDIT_ADDRESS = 23;
    private static final int EDIT_SCHOOL = 24;
    private static final int EDIT_SCHOOL_TEL = 25;
    public static String OLD_IDCARD = "OLD_IDCARD";
    public static String OLD_ADDRESS = "OLD_ADDRESS";
    public static String OLD_SCHOOL = "OLD_SCHOOL";
    public static String OLD_SCHOOL_TEL = "OLD_SCHOOL_TEL";
    public static String EDITTEXT = "EDITTEXT";

    private TecXsfStuInfoBindParams params;
    private String stu_status;
    private String picturePath;

    private ScrollView scrollView;
    private SelectPicPopupWindow popWindow;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            onUpdate();
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMMIT_FILE_OSS_ALL_FAIL:
                    dlgLoad.dismissDialog();
                    ToastUtil.show("图片上传失败");
                    break;
                case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
                case COMMIT_FILE_OSS_ALL_SUCCESS:
                    dlgLoad.dismissDialog();
                    if (((ArrayList<String>) msg.obj).size() != 0) {

                        ossPath = ((ArrayList<String>) msg.obj).get(0);
                        if (btn_bind.getText().equals("重新提交验证")) {
                            requestVerify();
                        } else {
                            requestBind();
                        }
                    }
                    break;
            }

        }
    };

    private void requestBind() {
        RequestManager.request(LittleCalligraphyTeacherEditCandidateInformationActivity.this,
                new TecXsfStuInfoBindParams(right_text[0], right_text[1], right_text[2], right_text[3], right_text[4], right_text[5], ossPath,(String)SPUtils.instance().get("stu_type",SPUtils.instance().get("stu_type","0"))),
                TecXsfStuInfoBindResponse.class, LittleCalligraphyTeacherEditCandidateInformationActivity.this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_little_calligraphy_teacher_edit_candidate_information);
        initView();
    }

    private void onUpdate() {
        if (mCache.isEmpty()) {
            img_add.setVisibility(View.VISIBLE);
            img_photo.setVisibility(View.GONE);
            img_delete.setVisibility(View.GONE);
        } else {
            img_add.setVisibility(View.GONE);
            img_photo.setVisibility(View.VISIBLE);
            img_delete.setVisibility(View.VISIBLE);
            BitmapHelp.getBitmapUtils().display(img_photo, mCache.get(0));
        }
    }

    private void initView() {
        setTopTitle("考生信息填写");
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        tv_failure_reminder = (TextView) findViewById(R.id.tv_failure_reminder);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(this);
        stu_status = getIntent().getStringExtra("stu_status");
        if (stu_status != null) {
            if (stu_status.equals("to_verify")) {
                btn_bind.setText("重新提交验证");
                tv_failure_reminder.setVisibility(View.VISIBLE);
                requestStuInfo();
            }
        }
//        requestStuInfo();
        list = getData();
        listView = (MyListView) findViewById(R.id.listView);
        adapter = new MyListAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        img_photo = (ImageView) findViewById(R.id.img_photo);
        img_add = (ImageView) findViewById(R.id.img_add);
        img_delete = (ImageView) findViewById(R.id.img_delete);

        img_add.setOnClickListener(this);
        img_photo.setOnClickListener(this);
        img_delete.setOnClickListener(this);

        popWindow = new SelectPicPopupWindow(this, itemsOnClick);
    }

    private void requestStuInfo() {
        RequestManager.request(LittleCalligraphyTeacherEditCandidateInformationActivity.this,
                new TecXsfStuInfoGetParams((String)SPUtils.instance().get("stu_type","0")), TecXsfStuInfoGetResponse.class, LittleCalligraphyTeacherEditCandidateInformationActivity.this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_add:
                popWindow.showAtLocation(this.findViewById(R.id.activity_little_calligraphy_teacher_edit_candidate_information), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.img_photo:
                Intent intent = new Intent(this, ImageDetailActivity.class);
                intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
                intent.putExtra("MAX_SIZE", MAX_SIZE);
                intent.putExtra("position", 0);
                intent.putStringArrayListExtra("selected_data", mCache);
                intent.putStringArrayListExtra("data", mCache);
                startActivityForResult(intent, ImageDetailActivity.REQUEST_CODE_DELETE);
                break;
            case R.id.img_delete:
                mCache.clear();
                picturePath = null;
                mHandler.sendEmptyMessage(0);
                break;
            case R.id.btn_bind:
                if (TextUtils.isEmpty(right_text[NAME])
                        || TextUtils.isEmpty(right_text[IDCARD])
                        || TextUtils.isEmpty(right_text[CITY])
                        || TextUtils.isEmpty(right_text[ADDRESS])
                        || TextUtils.isEmpty(right_text[SCHOOL])
                        || TextUtils.isEmpty(right_text[SCHOOLTEL])) {
                    ToastUtil.show("请完善考生信息");
                } else {
                    if (mCache.size() == 1) {
                        /**
                         * 如果ossPath有值并且ossPath补全后 == picturePath，
                         *      说明当前已上传的ossPath就是当前的图片
                         *      则不用再上传，直接请求
                         * 否则ossPath为空或者ossPath补全和picturePath不一致时，
                         *      说明未上传过图片或者换了新的图片
                         *      则需要先上传到阿里云
                         */
                        if (!TextUtils.isEmpty(ossPath)) {
                            if (StringUtil.getImgeUrl(ossPath).equals(mCache.get(0))) {
                                if (btn_bind.getText().equals("重新提交验证")) {
                                    requestVerify();
                                } else {
                                    requestBind();
                                }
                            } else {
                                commitFilesOss(picturePath, handler);
                            }
                        } else {
                            commitFilesOss(picturePath, handler);
                        }
                    } else {
                        ToastUtil.show("请上传照片");
                    }
                }
                break;
        }
    }

    private void requestVerify() {
        RequestManager.request(LittleCalligraphyTeacherEditCandidateInformationActivity.this,
                new TecXsfStuInfoVerifyParams(right_text[0], right_text[1], right_text[2], right_text[3], right_text[4], right_text[5], ossPath ,(String)SPUtils.instance().get("stu_type","0")),
                TecXsfStuInfoVerifyResponse.class, LittleCalligraphyTeacherEditCandidateInformationActivity.this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

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

    /**
     * 照片中选择
     */
    private void formGallerya() {
        photoName = System.currentTimeMillis() + ".png";
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);
    }

    private Uri uri = null;
    private Uri zoomUri = null;//剪裁后的uri

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case PHOTO:
//                    Uri uri =data.getData();
//                    File myFile = new File(uri.getPath());
//                    Uri selectedImage = getImageContentUri(this,myFile); //获取系统返回的照片的Uri
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContentResolver().query(selectedImage,
//                            filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    picturePath = cursor.getString(columnIndex);  //获取照片路径
//                    cursor.close();
//                    mCache.clear();
//                    mCache.add(picturePath);
//                    mHandler.sendEmptyMessage(0);
//                    break;
                // 拍照
                case PHOTO_GRAPH:
                    // 设置文件保存路径
                    File picture = new File(FileUtils.SD_IMAGES_PATH + photoName);
                    uri = Uri.fromFile(picture);
                    startPhotoZoom(uri);
                    break;
                // 读取相册缩放图片
                case PHOTO_ZOOM:
                    if (data == null)
                        return;
                    uri = data.getData();
                    startPhotoZoom(uri);
                    break;
                case PHOTO_RESOULT:
//                    if (data == null)
//                        return;
//                    Bundle extras = data.getExtras();
//                    if (extras != null) {
//                        Bitmap photo = extras.getParcelable("data");
                    if (zoomUri == null) {
                        return;
                    }
                    try {
                        // 读取uri所在的图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), zoomUri);
                        if (photo == null) {
                            ToastUtil.show("处理图片失败,请重试!");
                            return;
                        }
                        File pohotFile = FileUtils.saveBitmap(
                                FileUtils.SD_CLIP_IMAGES_PATH, photoName, photo);
                        // 选择的图片的路径
                        picturePath = pohotFile.getAbsolutePath();
                        // 初始化参数
                        mCache.clear();
                        mCache.add(picturePath);
                        mHandler.sendEmptyMessage(0);
//                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;
                case ImageDetailActivity.REQUEST_CODE_DELETE:
                    if (data != null) {
                        ArrayList<String> results = data.getStringArrayListExtra("data1");
                        if (results != null) {
                            mCache.clear();
                            mCache.addAll(results);
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                    break;
                case NAME:
                    right_text[NAME] = data.getStringExtra("real_name");
                    updateData();
                    break;
                case IDCARD:
                    right_text[IDCARD] = data.getStringExtra("id_card");
                    updateData();
                    break;
                case CITY:
                    right_text[CITY] = data.getStringExtra("city");
                    updateData();
                    break;
                case ADDRESS:
                    right_text[ADDRESS] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case SCHOOL:
                    right_text[SCHOOL] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case SCHOOLTEL:
                    right_text[SCHOOLTEL] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
            }
        }
    }

    private void updateData() {
        list = getData();
        adapter.setData(list);
    }

    /**
     * 收缩图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        //设置的路径目录和名称来单独获取这个图片
        File picture = new File(FileUtils.SD_IMAGES_PATH + photoName);
        zoomUri = Uri.fromFile(picture);

        Intent intent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");// 进行修剪

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 413);
        intent.putExtra("aspectY", 626);

        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY", 530);

        //"scale"：锁定比例
        intent.putExtra("scale", true);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        //"noFaceDetection"：去掉人脸识别，不去的话会影响上面的比例锁定
        intent.putExtra("noFaceDetection", true);

        //MediaStore.EXTRA_OUTPUT：输出位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, zoomUri);

        /*
        "return-data"是将结果保存在data中返回，
        在onActivityResult中，直接调用intent.getdata()就可以获取值了，
        这里设为fase，就是不让它保存在data中，
        原因是在于Intent 的data域最大传递的值的大小约为1M，所以图片的BITMAP当超过1M时就会失败。
        通常我们只是设置头像可以用这个方法
        设置为return-data设为false，不从data域获取图片，
        而是越过这个桥梁，通过我们刚刚在剪切图片后设置的路径目录和名称来单独获取这个图片，就可以完美显示了。
         */
        intent.putExtra("return-data", false);
        startActivityForResult(intent, PHOTO_RESOULT);
    }

    /**
     * 获取动态数组数据 可以由其他地方传来(json等)
     */
    private List<MyListData> getData() {
        List<MyListData> list = new ArrayList<MyListData>();
        for (int i = 0; i < left_text.length; i++) {
            MyListData myListData = new MyListData(-1, left_text[i], -1, right_text[i], null);
            list.add(myListData);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case NAME:
                Intent intentName = new Intent(this, EditRealNameActivity.class);
                intentName.putExtra("OLD_REALNAME", right_text[NAME]);
                startActivityForResult(intentName, NAME);
                break;
            case IDCARD:
                Intent intentIdCard = new Intent(this, EditIdCardActivity.class);
                intentIdCard.putExtra(OLD_IDCARD, right_text[IDCARD]);
                startActivityForResult(intentIdCard, IDCARD);
                break;
            case CITY:
                Intent intentCity = new Intent(this, EditCityActivity.class);
                startActivityForResult(intentCity, CITY);
                break;
            case ADDRESS:
                Intent intentAddress = new Intent(this, EditInfoReuseActivity.class);
                intentAddress.putExtra(EDIT_TYPE, EDIT_ADDRESS);
                intentAddress.putExtra(OLD_ADDRESS, right_text[ADDRESS]);
                startActivityForResult(intentAddress, ADDRESS);
                break;
            case SCHOOL:
                Intent intentSchool = new Intent(this, EditInfoReuseActivity.class);
                intentSchool.putExtra(EDIT_TYPE, EDIT_SCHOOL);
                intentSchool.putExtra(OLD_SCHOOL, right_text[SCHOOL]);
                startActivityForResult(intentSchool, SCHOOL);
                break;
            case SCHOOLTEL:
                Intent intentSchoolTel = new Intent(this, EditInfoReuseActivity.class);
                intentSchoolTel.putExtra(EDIT_TYPE, EDIT_SCHOOL_TEL);
                intentSchoolTel.putExtra(OLD_SCHOOL_TEL, right_text[SCHOOLTEL]);
                startActivityForResult(intentSchoolTel, SCHOOLTEL);
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof TecXsfStuInfoBindResponse) {
                TecXsfStuInfoBindResponse xsfResult = (TecXsfStuInfoBindResponse) response;
                if (xsfResult != null) {
                    TecXsfStuInfoBindResponse.TecXsfStuInfoBindBody body = xsfResult
                            .getRepBody();
                    if (body.getStu_status().equals("available")) {
                        Intent intentBind = new Intent(this, LittleCalligraphyTeacherInformationConfirmActivity.class);
                        startActivity(intentBind);
                        finish();
                    } else {
                        btn_bind.setText("重新提交验证");
                        tv_failure_reminder.setVisibility(View.VISIBLE);
                        scrollView.scrollTo(0, 0);
//                        ToastUtil.show(response.getResMsg());
                    }

                }
            } else if (response instanceof TecXsfStuInfoGetResponse) {
                TecXsfStuInfoGetResponse xsfInfoResult = (TecXsfStuInfoGetResponse) response;
                if (xsfInfoResult != null) {
                    TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody body = xsfInfoResult
                            .getRepBody();
                    if (!TextUtils.isEmpty(body.get_id())) {
                        right_text[0] = body.getReal_name();
                        right_text[1] = body.getId_num();
                        right_text[2] = body.getCity();
                        right_text[3] = body.getAddr();
                        right_text[4] = body.getSchool_name();
                        right_text[5] = body.getSchool_contact();
                        ossPath = body.getPhoto_url();
                        updateData();
                        picturePath = StringUtil.getImgeUrl(ossPath);  //获取照片路径
                        BitmapHelp.getBitmapUtils().display(
                                img_photo, picturePath + Constant.VSHOW_IMAGE_SUFFIX, new BitmapLoadCallBack<ImageView>() {
                                    @Override
                                    public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                                        imageView.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
                                        imageView
                                                .setImageResource(R.drawable.empty_photo);
                                    }
                                });
                        mCache.clear();
                        mCache.add(picturePath);
                        mHandler.sendEmptyMessage(0);
                    } else {
//                        ToastUtil.show(response.getResMsg());
                    }

                }
            } else if (response instanceof TecXsfStuInfoVerifyResponse) {
                TecXsfStuInfoVerifyResponse xsfInfoVerifyResult = (TecXsfStuInfoVerifyResponse) response;
                if (xsfInfoVerifyResult != null) {
                    TecXsfStuInfoVerifyResponse.TecXsfStuInfoVerifyBody body = xsfInfoVerifyResult
                            .getRepBody();
                    if (body.getStu_status().equals("available")) {
                        Intent intentVerify = new Intent(this, LittleCalligraphyTeacherInformationConfirmActivity.class);
                        startActivity(intentVerify);
                        finish();
                    } else {
//                        ToastUtil.show(response.getResMsg());
                    }
                } else {
//                    ToastUtil.show(response.getResMsg());
                }

            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    };
    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

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
}
