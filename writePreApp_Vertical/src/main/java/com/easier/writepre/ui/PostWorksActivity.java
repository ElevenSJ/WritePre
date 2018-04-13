package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PostWorksParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.SelectPicPopupWindow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 投寄作品
 */
public class PostWorksActivity extends BaseActivity {

    private TextView tv_sure, tv_pic_demo;  //  提交
    private EditText et_kddh, et_wlgs;
    private Uri uri = null;
    private Uri zoomUri = null;//剪裁后的uri
    private TextView tv_link;
    private TextView tv_tips;
    private ImageView img_add, img_photo, img_delete;
    private String photoName;
    private String picturePath;//sd本地图片路径
    private String ossPath; // Oss路径
    private SelectPicPopupWindow popWindow;
    private static final int PHOTO_GRAPH = 104;// 拍照
    private static final int PHOTO_ZOOM = 105; // 缩放
    private static final int PHOTO_RESOULT = 106;// 裁剪后的结果
    private static final int PHOTO_GRAPH_NOT_ZOOM = 107;// 拍照 不剪裁
    private static final int PHOTO_ALBUM_NOT_ZOOM = 108;// 相册 不剪裁
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int MAX_SIZE = 1;
    private String noteLink = "";
    private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);
    private File photoGraphFile;//拍照保存的文件

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_post);
        initView();
    }

    private void initView() {
        setTopTitle("投寄作品");
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        tv_link = (TextView) findViewById(R.id.tv_link);
        img_add = (ImageView) findViewById(R.id.img_add);
        img_photo = (ImageView) findViewById(R.id.img_photo);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        tv_pic_demo = (TextView) findViewById(R.id.tv_pic_demo);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        et_kddh = (EditText) findViewById(R.id.et_kddh);
        et_wlgs = (EditText) findViewById(R.id.et_wlgs);
        initSet();
    }

    private void initSet() {
        noteLink = getIntent().getStringExtra("url");
        tv_link.setText(noteLink);
        //SpannableString spannableString = new SpannableString("请在考试结束后三日内将实践考试中所有考试作品（与实践考试期间提交图片、视频作品同幅）及亲笔签名的不作弊承诺书以快递方式寄出至主办方，如在规定时间内（以快递单时间为准）没有寄出，则视为考生自动放弃本次实践考试。");
        //SpannableString spannableString = new SpannableString("请在考试结束后三日内将实践考试中所有考试作品（与实践考试期间提交图片、视频作品同幅）及亲笔签名的不作弊承诺书以快递方式寄出至主办方，\n如在规定时间内（以快递单时间为准）没有寄出，则视为考生自动放弃本次实践考试。");
        //ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#c60000"));
        //ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#c60000"));
        //spannableString.setSpan(colorSpan1, 7, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        // spannableString.setSpan(new UnderlineSpan(), 7, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //spannableString.setSpan(colorSpan, 66, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //  spannableString.setSpan(new UnderlineSpan(), 66, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        // tv_tips.setText(spannableString);
        // tv_link.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        // tv_link.getPaint().setAntiAlias(true);//抗锯齿
        popWindow = new SelectPicPopupWindow(this, itemsOnClick);
        img_add.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        img_photo.setOnClickListener(this);      //查看大图
        tv_link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_link:
//                Intent intent1 = new Intent(this, BannerLinkActivity.class);
//                intent1.putExtra("type", true);
//                intent1.putExtra("url", noteLink);
//                startActivity(intent1);
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(noteLink);
                intent1.setData(content_url);
                startActivity(intent1);
                break;
            case R.id.img_add:
                popWindow.showAtLocation(this.findViewById(R.id.rl_post_works), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.img_delete:
                mCache.clear();
                picturePath = null;
                mHandler.sendEmptyMessage(0);
                break;
            case R.id.img_photo:          // 点击查看大图
                Intent intent = new Intent(this, ExamImageLookActivity.class);
                intent.putExtra("localPath", picturePath);
                startActivity(intent);
//                Intent intent = new Intent(this, SquareImageLookActivity.class);
//                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
//                intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, new String[]{picturePath});
//                intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, 0);
//                startActivity(intent);
////                Intent intent = new Intent(this, ImageDetailActivity.class);
////                intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
////                intent.putExtra("MAX_SIZE", MAX_SIZE);
////                intent.putExtra("position", 0);
////                intent.putStringArrayListExtra("selected_data", mCache);
////                intent.putStringArrayListExtra("data", mCache);
////                startActivityForResult(intent, ImageDetailActivity.REQUEST_CODE_DELETE);
                break;
            case R.id.tv_sure:
                if (mCache.isEmpty()) {
                    ToastUtil.show("请添加图片");
                } else if (TextUtils.isEmpty(et_kddh.getText().toString()) || TextUtils.isEmpty(et_wlgs.getText().toString())) {
                    ToastUtil.show("请完善信息");
                } else {
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.clear();
                    arrayList.add(picturePath);
                    commitFilesOss(arrayList, handler);
                }
                break;
        }
    }

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
                        LogUtils.e("投寄作品oss---->" + ossPath);
                        if (!TextUtils.isEmpty(ossPath)) {
                            requestCommitWorks();
                        }
                    }
                    break;
            }

        }
    };

    /**
     * 提交快递编号数据
     */
    private void requestCommitWorks() {
        RequestManager.request(PostWorksActivity.this,
                new PostWorksParams((String) SPUtils.instance().get("stu_type", "0"), ossPath, et_wlgs.getText().toString(), et_kddh.getText().toString()), BaseResponse.class, PostWorksActivity.this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            ToastUtil.show("信息提交成功");
            finish();
        } else
            ToastUtil.show(response.getResMsg());
    }

    /**
     * 拍照
     */
    private void formCamera(int PHOTO_GRAPH) {
        photoName = System.currentTimeMillis() + ".png";
        new File(FileUtils.SD_IMAGES_PATH).mkdirs();
        // 设置文件保存路径
        photoGraphFile = new File(FileUtils.SD_IMAGES_PATH + photoName);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    /**
     * 照片中选择
     */
    private void formGallerya(int PHOTO_ZOOM) {
        photoName = System.currentTimeMillis() + ".png";
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);
    }


    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            popWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:
                    formCamera(PHOTO_GRAPH_NOT_ZOOM);
                    break;
                case R.id.item_popupwindows_Photo:
                    formGallerya(PHOTO_ALBUM_NOT_ZOOM);
                    break;
                default:
                    break;
            }

        }

    };

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_GRAPH:
                    // 设置文件保存路径
                    File picture = new File(FileUtils.SD_IMAGES_PATH + photoName);
                    uri = Uri.fromFile(picture);
                    startPhotoZoom(uri);
                    break;
                case PHOTO_RESOULT:
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
                        LogUtils.e("投寄作品本地路径--->" + picturePath);
                        updatePicState();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                // 读取相册缩放图片
                case PHOTO_ZOOM:
                    if (data == null)
                        return;
                    uri = data.getData();
                    startPhotoZoom(uri);
                    break;
                case PHOTO_ALBUM_NOT_ZOOM:
                    Uri uri = data.getData();
                    picturePath = FileUtils.getRealFilePath(this, uri);
                    updatePicState();
                    break;
                case PHOTO_GRAPH_NOT_ZOOM:
//                    cameraPicturePath = new File(FileUtils.SD_IMAGES_PATH + photoName);
                    picturePath = photoGraphFile.getAbsolutePath();
                    updatePicState();
                    break;
            }
        }
    }

    //更新图片状态
    private void updatePicState() {
        mCache.clear();
        mCache.add(picturePath);
        mHandler.sendEmptyMessage(0);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            onUpdate();
        }
    };

    private void onUpdate() {
        if (mCache.isEmpty()) {
            img_add.setVisibility(View.VISIBLE);
            tv_pic_demo.setVisibility(View.VISIBLE);
            img_photo.setVisibility(View.GONE);
            img_delete.setVisibility(View.GONE);
        } else {
            img_add.setVisibility(View.GONE);
            tv_pic_demo.setVisibility(View.GONE);
            img_photo.setVisibility(View.VISIBLE);
            img_delete.setVisibility(View.VISIBLE);
            BitmapHelp.getBitmapUtils().display(img_photo, mCache.get(0));
        }
    }
}

