package com.easier.writepre.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.mainview.CourseMainView;
import com.easier.writepre.ui.DiyCourseClassActivity;
import com.easier.writepre.ui.ImageDetailActivity;
import com.easier.writepre.ui.ImageSelectionActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.SelectPicPopupWindow;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiyCourseFragment extends BaseFragment {
    private EditText et_content;

    private static final int MAX_SIZE = 1;
    private ArrayList<String> mCache = new ArrayList<String>(MAX_SIZE);
    private SelectPicPopupWindow popWindow;

    private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 104;// 拍照
    private static final int PHOTO_ZOOM = 105; // 缩放
    private static final int PHOTO_RESOULT = 106;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private String diyCourseTitle = "";// 课程名
    private String diyCourseFacePath = "";// 课程封面路径
    private String photoName = "";// 拍照图片名

    private ImageView videoImage;
    private ImageView videoPlayImage;
    private ImageView faceImg;
    private ImageView deleteImg;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    mCache.clear();
                    photoName = "";
                    diyCourseFacePath = "";
                    diyCourseTitle = "";
                    et_content.setText(null);
                case 0:
                    onUpdate();
                    break;

            }
        }
    };

    @Override
    protected void init() {
        super.init();
        videoImage = (ImageView) findViewById(R.id.video_bg);
        videoPlayImage = (ImageView) findViewById(R.id.play_video);
        videoPlayImage.setOnClickListener(this);
        //动态设置图片高度
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getWidth(0.333f));
        videoImage.setLayoutParams(layoutParams);

        findViewById(R.id.tv_next).setOnClickListener(this);
        et_content = (EditText) findViewById(R.id.et_content);
        faceImg = (ImageView) findViewById(R.id.face_img);
        faceImg.setOnClickListener(this);
        deleteImg = (ImageView) findViewById(R.id.image_selected);
        deleteImg.setOnClickListener(this);
        popWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);

        et_content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public int getContextView() {
        return R.layout.diy_course_create_fragment;
    }

    private void onUpdate() {
        //有缓存时赋值
        if (!TextUtils.isEmpty(diyCourseTitle) && !diyCourseTitle.equalsIgnoreCase("null")) {
            et_content.setText(diyCourseTitle);
        }
        if (mCache.isEmpty()) {
            deleteImg.setVisibility(View.GONE);
            faceImg.setImageResource(R.drawable.icon_addpic_unfocused);
        } else {
            deleteImg.setVisibility(View.VISIBLE);
            BitmapHelp.getBitmapUtils().display(faceImg, mCache.get(0));
        }
    }


    public void onTopRightTxtClick(View view) {
        if (LoginUtil.checkLogin(getActivity())) {
            Intent intent = new Intent(getActivity(), DiyCourseClassActivity.class);
            intent.putExtra(DiyCourseClassActivity.COURSE_NAME,
                    TextUtils.isEmpty(et_content.getText().toString()) ? "自选课程" : et_content.getText().toString());
            if (mCache.size() > 0) {
                intent.putExtra(DiyCourseClassActivity.COURSE_FACE, mCache.get(0));
            }
            startActivityForResult(intent, 199);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            //友盟统计
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_ONE));
            var.add("自选课程");
            YouMengType.onEvent(getActivity(),var,1,"自选课程");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        diyCourseTitle = et_content.getText().toString();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_next:
                onTopRightTxtClick(v);
                break;
            case R.id.play_video:
                //友盟统计
                ArrayList<String> var = new ArrayList<String>();
                var.add(YouMengType.getName(MainActivity.TYPE_ONE));
                var.add("自选课程");
                var.add("查看视频");
                Intent intentV = new Intent(getActivity(), MediaActivity.class);
                intentV.putExtra("lable","自选课程查看视频");
                intentV.putStringArrayListExtra(MediaActivity.U_MENG_DATA,var);
                intentV.putExtra(MediaActivity.URL, Constant.DIY_COURSE_VIDEO_URL);
                startActivity(intentV);
                break;
            case R.id.face_img:
                if (mCache.isEmpty()) {
                    popWindow.showAtLocation(contextView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else {
                    Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                    intent.putExtra("type", ImageDetailActivity.TYPE_DELETE);
                    intent.putExtra("MAX_SIZE", MAX_SIZE);
                    intent.putExtra("position", 0);
                    intent.putStringArrayListExtra("selected_data", mCache);
                    intent.putStringArrayListExtra("data", mCache);
                    startActivityForResult(intent, ImageDetailActivity.REQUEST_CODE_DELETE);
                }
                break;
            case R.id.image_selected:
                mCache.clear();
                mHandler.sendEmptyMessage(0);
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
                    // Intent intent = new Intent(DiyCourseActivity.this,
                    // ImageSelectionActivity.class);
                    // intent.putExtra("maxSize", MAX_SIZE);
                    // intent.putStringArrayListExtra("data", mCache);
                    // startActivityForResult(intent,
                    // ImageSelectionActivity.REQUEST_CODE_ADD);
                    formGallerya();
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    /**
     * 照片中选择
     */
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        if (resultCode == getActivity().RESULT_OK) {
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
                    diyCourseFacePath = pohotFile.getAbsolutePath();
                    mCache.clear();
                    mCache.add(diyCourseFacePath);
                    mHandler.sendEmptyMessage(0);
                }
            } else if (requestCode == ImageDetailActivity.REQUEST_CODE_DELETE
                    || requestCode == ImageSelectionActivity.REQUEST_CODE_ADD) {
                if (data != null) {
                    ArrayList<String> results = data.getStringArrayListExtra("data1");
                    if (results != null) {
                        mCache.clear();
                        mCache.addAll(results);
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }
            // else if (requestCode == PHOTO_GRAPH) {
            // mCache.add(FileUtils.SD_IMAGES_PATH + photoName + ".png");
            // mHandler.sendEmptyMessage(0);
            // }
            else if (requestCode == 199) {
                mHandler.sendEmptyMessage(1);
                MainActivity.isMyCourseUpdate = true;
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_ONE);
                intent.putExtra(CourseMainView.TAB_INDEX, CourseMainView.TYPE_ONE);
                startActivity(intent);
            }
        }

    }
}
