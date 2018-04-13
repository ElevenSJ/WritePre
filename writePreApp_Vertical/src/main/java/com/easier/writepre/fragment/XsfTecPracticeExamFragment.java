package com.easier.writepre.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.adapter.XsfTecPracticeExamAdapter;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.WorksInfo;
import com.easier.writepre.response.ExamResponse;
import com.easier.writepre.response.XsfTecPracticeExamDetailResponse;
import com.easier.writepre.ui.ExamImageLookActivity;
import com.easier.writepre.ui.RecorderVideoActivity;
import com.easier.writepre.ui.XsfTecPracticeExamActivity;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.NineGridViewWrapper;
import com.easier.writepre.widget.SelectPicPopupWindow;
import com.easier.writepre.widget.ViewPageIndicator;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.HomeFragmentActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实践考试试题fragment
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView tv_title;// 试卷题目
    private TextView tv_content;// 试卷内容

    //*****轮播图片*******
    private XsfTecPracticeExamAdapter advAdapter;
    private ViewPageIndicator mBannerIndicator;
    private ArrayList<XsfTecPracticeExamDetailResponse.Items> advs = new ArrayList<XsfTecPracticeExamDetailResponse.Items>();
    public ChildViewPager mBannerViewPager;
    //*****轮播图片*******

    private ImageView img_add_video;// 添加视频按钮
    private ImageView img_selected_video;// 已选择的视频
//    private ImageView img_video_icon;// 选择视频后的播放器图片
    private ImageView img_delete;// 视频的删除
    private TextView picTxt1,picTxt2;

    private SelectPicPopupWindow popWindow;//选照片和视频的底部弹窗
    private static final int REQUEST_CODE_VIDEO = 0x0000001;//拍视频请求码
    private static final int REQUEST_CODE_LOCAL_VIDEO = 0x0000002;//本地视频请求码
    private String videoFilePath;

    private ImageView img_selected_picture_first;
    private ImageView img_add_picture_first;
    private ImageView img_delete_picture_first;

    private ImageView img_selected_picture_second;
    private ImageView img_add_picture_second;
    private ImageView img_delete_picture_second;

    private int number = 0;

    private String photoName;
    private static final int PHOTO = 103; // 不剪裁
    private static final int PHOTO_GRAPH = 104;// 拍照
    private static final int PHOTO_ZOOM = 105; // 缩放
    private static final int PHOTO_RESOULT = 106;// 结果
    private static final int PHOTO_GRAPH_NOT_ZOOM = 107;// 拍照 不剪裁
    private static final int PHOTO_ALBUM_NOT_ZOOM = 108;// 相册 不剪裁
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private LocalBroadcastManager broadcastManager;

    private XsfTecPracticeExamActivity activity;

    private XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailInfo examBean;
    private Bitmap bitmap;
    private File photoGraphFile;//拍照保存的文件

    private String stu_type;

    public XsfTecPracticeExamFragment() {
        // Required empty public constructor
    }

    public static XsfTecPracticeExamFragment newInstance(String stu_type,XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailInfo xsfTecPracticeExamDetailInfo) {
        XsfTecPracticeExamFragment fragment = new XsfTecPracticeExamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2,stu_type);
        args.putSerializable(ARG_PARAM1, xsfTecPracticeExamDetailInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContextView() {
        return R.layout.fragment_xsf_tec_practice_exam;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            examBean = (XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailInfo) getArguments().getSerializable(ARG_PARAM1);
            stu_type = getArguments().getString(ARG_PARAM2);
            LogUtils.e("+++&&&&&&&&&&&++");
        }
    }

    @Override
    protected void init() {
        activity = ((XsfTecPracticeExamActivity) getActivity());
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);

        mBannerViewPager = (ChildViewPager) findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) findViewById(R.id.banner_indicator);

        advAdapter = new XsfTecPracticeExamAdapter(getActivity());
        mBannerViewPager.setAdapter(advAdapter);
        try {
            mBannerViewPager.stopPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);

        //选视频
        img_add_video = (ImageView) findViewById(R.id.img_add_video);
        img_add_video.setSelected(false);
        img_add_video.setOnClickListener(this);

        img_selected_video = (ImageView) findViewById(R.id.img_selected_video);

//        img_video_icon = (ImageView) findViewById(R.id.img_video_icon);

        img_delete = (ImageView) findViewById(R.id.img_delete);
        img_delete.setOnClickListener(this);

        //fragment注册广播
        // broadcastManager = LocalBroadcastManager.getInstance(getActivity());
//        IntentFilter videoIntentFilter = new IntentFilter(
//                MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
//        getActivity().registerReceiver(videoBroadcastReceiver, videoIntentFilter);

        //选第一个图片
        img_selected_picture_first = (ImageView) findViewById(R.id.img_selected_picture_first);
        img_selected_picture_first.setOnClickListener(this);
        img_add_picture_first = (ImageView) findViewById(R.id.img_add_picture_first);
        img_add_picture_first.setOnClickListener(this);
        img_delete_picture_first = (ImageView) findViewById(R.id.img_delete_picture_first);
        img_delete_picture_first.setOnClickListener(this);
        picTxt1 = (TextView) findViewById(R.id.pic_txt1);

        //选第二个图片
        img_selected_picture_second = (ImageView) findViewById(R.id.img_selected_picture_second);
        img_selected_picture_second.setOnClickListener(this);
        img_add_picture_second = (ImageView) findViewById(R.id.img_add_picture_second);
        img_add_picture_second.setOnClickListener(this);
        img_delete_picture_second = (ImageView) findViewById(R.id.img_delete_picture_second);
        img_delete_picture_second.setOnClickListener(this);
        picTxt2 = (TextView) findViewById(R.id.pic_txt2);

        if (TextUtils.isEmpty(stu_type)){
            img_selected_video.setBackgroundResource(R.drawable.empty_photo);
            img_selected_picture_second.setBackgroundResource(R.drawable.empty_photo);
            img_selected_picture_first.setBackgroundResource(R.drawable.empty_photo);
        }else if (stu_type.equals("0")){
            img_selected_video.setBackgroundResource(R.drawable.children_video);
            img_selected_picture_second.setBackgroundResource(R.drawable.children_pic_hand);
            img_selected_picture_first.setBackgroundResource(R.drawable.children_pic);
        }else{
            img_selected_video.setBackgroundResource(R.drawable.adult_video);
            img_selected_picture_second.setBackgroundResource(R.drawable.adult_pic_hand);
            img_selected_picture_first.setBackgroundResource(R.drawable.adult_pic);
        }


        ininData();
        query();
    }

    /**
     * 查询需要加载的图片
     */
    public void query() {
        if (activity.answerViewPager.getCurrentItem() == 0) {
            bugShowPhoto(1);
        } else if (activity.answerViewPager.getCurrentItem() == 1) {
            bugShowPhoto(4);
        } else if (activity.answerViewPager.getCurrentItem() == 2) {
            bugShowPhoto(7);
        }
    }

    /**
     * 显示图片(预防奔溃时)
     *
     * @param value
     */
    public void bugShowPhoto(final int value) {
        Cursor cursor;
        for (int i = value; i < (value + 3); i++) {
            cursor = DBHelper.instance().query("select loc_path  from works_table where sort_num = " + i);
            if (cursor.getCount() == 1) {
                if (i == value) {
                    if (cursor.moveToFirst()) {
                        img_selected_video.setImageBitmap(Bimp
                                .getVideoThumbnail(cursor.getString(cursor
                                        .getColumnIndex("loc_path"))));
                        img_delete.setVisibility(View.VISIBLE);
                        img_add_video.setSelected(true);
                    }
                } else if (i == (value + 1)) {
                    if (cursor.moveToFirst()) {
                        picturePathFirst = cursor.getString(cursor.getColumnIndex("loc_path"));
                        BitmapHelp.getBitmapUtils().display(img_selected_picture_first,picturePathFirst);
//                        img_selected_picture_first.setVisibility(View.VISIBLE);
                        img_delete_picture_first.setVisibility(View.VISIBLE);
                        picTxt1.setVisibility(View.GONE);
                        img_add_picture_first.setVisibility(View.GONE);
                    }
                } else if (i == (value + 2)) {
                    if (cursor.moveToFirst()) {
                        picturePathSecond = cursor.getString(cursor.getColumnIndex("loc_path"));
                        BitmapHelp.getBitmapUtils().display(img_selected_picture_second, picturePathSecond);
//                        img_selected_picture_second.setVisibility(View.VISIBLE);
                        img_delete_picture_second.setVisibility(View.VISIBLE);
                        picTxt2.setVisibility(View.GONE);
                        img_add_picture_second.setVisibility(View.GONE);
                    }
                }
            }
            cursor.close();
        }
    }

    /**
     * type为0 表示视频，为1表示第一个图片，为2表示第二个图片
     * path为图片视频所属sd卡路径
     */

    public void tHandle(int type, String path) {
        if (activity.answerViewPager.getCurrentItem() == 0) {
            if (type == 0) {
                sql("1", path);
            } else if (type == 1) {
                sql("2", path);
            } else if (type == 2) {
                sql("3", path);
            }
        } else if (activity.answerViewPager.getCurrentItem() == 1) {
            if (type == 0) {
                sql("4", path);
            } else if (type == 1) {
                sql("5", path);
            } else if (type == 2) {
                sql("6", path);
            }
        } else if (activity.answerViewPager.getCurrentItem() == 2) {
            if (type == 0) {
                sql("7", path);
            } else if (type == 1) {
                sql("8", path);
            } else if (type == 2) {
                sql("9", path);
            }
        }
    }

    private void ininData() {
        tv_title.setText(examBean.getTitle());
        //拼接试题内容
        String result = "";
        for (int i = 0; i < examBean.getItems().size(); i++) {
            String type = examBean.getItems().get(i).getTitle();
            if (i == 0) {
                result = result + type;
            } else {
                result = result + "、" + type;
            }
        }
        tv_content.setText(result);
        if (examBean != null) {
            if (examBean.getItems() != null) {
                advs.clear();
                advs.addAll(examBean.getItems());
                advAdapter.setData(advs);
                mBannerIndicator.setViewPager(mBannerViewPager);
                if (examBean.getItems().size() == 1) {
                    mBannerIndicator.setVisibility(View.GONE);
                } else {
                    mBannerIndicator.setVisibility(View.VISIBLE);
                }
                try {
                    mBannerViewPager.startPlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private boolean isPictrue; // true图片类型 false视频类型

    @Override
    public void onClick(View v) {
        if (popWindow != null) {
            popWindow.dismiss();
        }
        switch (v.getId()) {
            case R.id.img_add_video:
                if (v.isSelected()&&!TextUtils.isEmpty(videoFilePath)){
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW);
                    String type = "video/mp4";
                    Uri uri = Uri.parse(videoFilePath);
                    intent.setDataAndType(uri, type);
                    startActivity(intent);
                }else {
                    isPictrue = false;
                    popWindow = new SelectPicPopupWindow(getActivity(), this, false);
                    popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.item_popupwindows_camera:
                if (isPictrue) {
                    formCamera(PHOTO_GRAPH_NOT_ZOOM);
                } else {
                    Intent intent1 = new Intent(getActivity(),
                            RecorderVideoActivity.class);
                    startActivityForResult(intent1, REQUEST_CODE_VIDEO);
                }
                break;
            case R.id.item_popupwindows_Photo:
                if (isPictrue) {
                    formGallerya(PHOTO_ALBUM_NOT_ZOOM);
                } else {
                    Intent intent2 = new Intent(getActivity(),
                            HomeFragmentActivity.class);
                    startActivityForResult(intent2, REQUEST_CODE_LOCAL_VIDEO);
                }
                break;
            case R.id.img_delete:
                //删除选中视屏封面，显示加号
                img_delete.setVisibility(View.GONE);
                img_add_video.setSelected(false);
                img_selected_video.setImageBitmap(null);
                if (activity.answerViewPager.getCurrentItem() == 0) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 1");
                    LogUtils.e("删除成功视频1");
                } else if (activity.answerViewPager.getCurrentItem() == 1) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 4");
                    LogUtils.e("删除成功视频2");
                } else if (activity.answerViewPager.getCurrentItem() == 2) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 7");
                    LogUtils.e("删除成功视频3");
                }
                break;
            case R.id.img_add_picture_first:
                isPictrue = true;
                number = 1;
                popWindow = new SelectPicPopupWindow(getActivity(), this, true);
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.img_add_picture_second:
                isPictrue = true;
                number = 2;
                popWindow = new SelectPicPopupWindow(getActivity(), this, true);
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.img_delete_picture_first:
                //删除选中图片1，显示加号
//                img_selected_picture_first.setVisibility(View.INVISIBLE);
                img_delete_picture_first.setVisibility(View.GONE);
                img_add_picture_first.setVisibility(View.VISIBLE);
                picTxt1.setVisibility(View.VISIBLE);
                img_selected_picture_first.setImageBitmap(null);
                if (activity.answerViewPager.getCurrentItem() == 0) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 2");
                    LogUtils.e("删除成功图片2");
                } else if (activity.answerViewPager.getCurrentItem() == 1) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 5");
                    LogUtils.e("删除成功图片5");
                } else if (activity.answerViewPager.getCurrentItem() == 2) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 8");
                    LogUtils.e("删除成功图片8");
                }
                break;
            case R.id.img_delete_picture_second:
                //删除选中图片2，显示加号
//                img_selected_picture_second.setVisibility(View.INVISIBLE);
                img_delete_picture_second.setVisibility(View.GONE);
                img_add_picture_second.setVisibility(View.VISIBLE);
                picTxt2.setVisibility(View.VISIBLE);
                img_selected_picture_second.setImageBitmap(null);
                if (activity.answerViewPager.getCurrentItem() == 0) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 3");
                    LogUtils.e("删除成功图片3");
                } else if (activity.answerViewPager.getCurrentItem() == 1) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 6");
                    LogUtils.e("删除成功图片6");
                } else if (activity.answerViewPager.getCurrentItem() == 2) {
                    DBHelper.instance().delete("delete  from works_table where sort_num = 9");
                    LogUtils.e("删除成功图片9");
                }
                break;
            case R.id.img_selected_picture_first:
                if (!TextUtils.isEmpty(picturePathFirst)) {
                    ViewBigPicture(picturePathFirst);
                }
                break;
            case R.id.img_selected_picture_second:
                if (!TextUtils.isEmpty(picturePathSecond)) {
                    ViewBigPicture(picturePathSecond);
                }
                break;
        }
    }

    /**
     * 查看大图
     * @param picturePath 图片路径
     */
    private void ViewBigPicture(String picturePath) {
        Intent intent = new Intent(getActivity(), ExamImageLookActivity.class);
        intent.putExtra("localPath",picturePath);
        startActivity(intent);
    }

    private Uri uri = null;
    private Uri zoomUri = null;//剪裁后的uri
    private String picturePath;

    /**
     * 之前只有一个图片路径，点击查看大图时候冲突，只显示一个
     * 所以现在添加了两个路径来区分。
     */
    private String picturePathFirst;//第一张图片的路径
    private String picturePathSecond;//第二张图片的路径


    /**
     * 插入更新
     *
     * @param sortNum
     */
    void sql(String sortNum, String locPath) {
        Cursor cursor = DBHelper.instance().query("select sort_num  from works_table where sort_num = " + sortNum);
        if (cursor.getCount() == 1) {        // 更新
            ContentValues cv = new ContentValues();
            cv.put("loc_path", locPath);
            String[] args = {sortNum};
            int code = DBHelper.instance().update("works_table", cv, "sort_num=?", args);
            LogUtils.e("更新数据-->" + code);
        } else if (cursor.getCount() == 0) {       // 插入
            try {
                DBHelper.getExecutor().save(new WorksInfo(locPath, "", "0", sortNum));
            } catch (DbException e) {
                e.printStackTrace();
            }
            LogUtils.e("插入成功");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_VIDEO:
                    if (data != null) {
                        // 自定义录制视频
//                        img_selected_video.setVisibility(View.VISIBLE);
//                        img_video_icon.setVisibility(View.VISIBLE);
//                        img_video_icon.bringToFront();
                        img_delete.setVisibility(View.VISIBLE);
                        img_add_video.setSelected(true);
                        img_selected_video.setImageBitmap(Bimp
                                .getVideoThumbnail(data.getStringExtra("path")));
                        videoFilePath = data.getStringExtra("path");
                        // uploadVideo(data.getStringExtra("path"));
                        Log.e("viewPage 当前下标", ((XsfTecPracticeExamActivity) getActivity()).answerViewPager.getCurrentItem() + "");
//                        if (((XsfTecPracticeExamActivity) getActivity()).answerViewPager.getCurrentItem() == 0) {
//                            sql("1", videoFilePath);
//                        } else if (((XsfTecPracticeExamActivity) getActivity()).answerViewPager.getCurrentItem() == 1) {
//                            sql("4", videoFilePath);
//                        } else if (((XsfTecPracticeExamActivity) getActivity()).answerViewPager.getCurrentItem() == 2) {
//                            sql("7", videoFilePath);
//                        }
                        tHandle(0, videoFilePath);
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
                    break;
                case REQUEST_CODE_LOCAL_VIDEO:
                    filePathList = data.getStringArrayListExtra("list");

                    if (filePathList.isEmpty()) {
                        return;
                    }

                    // 设置选择的视频可见并且设置布局
//                    img_selected_video.setVisibility(View.VISIBLE);
//                    img_video_icon.setVisibility(View.VISIBLE);
//                    img_video_icon.bringToFront();
                    img_delete.setVisibility(View.VISIBLE);
//                    img_add_video.setVisibility(View.GONE);

                    img_add_video.setSelected(true);
                    img_selected_video.setImageBitmap(Bimp
                            .getVideoThumbnail(filePathList.get(0)));
                    videoFilePath = filePathList.get(0);

                    tHandle(0, videoFilePath);

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
                    break;
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
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), zoomUri);
                        if (bitmap == null) {
                            ToastUtil.show("处理图片失败,请重试!");
                            return;
                        }
                        File pohotFile = FileUtils.saveBitmap(
                                FileUtils.SD_CLIP_IMAGES_PATH, photoName, bitmap);
                        // 选择的图片的路径
                        picturePath = pohotFile.getAbsolutePath();
                        //通知图库更新
                        MediaScannerConnection.scanFile(getActivity(), new String[]{picturePath}, null, null);
                        updatePicState();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case PHOTO_ALBUM_NOT_ZOOM:
                    Uri uri = data.getData();
                    picturePath = FileUtils.getRealFilePath(getActivity(),uri);
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

    /**
     * 更新选图片的状态
     */
    private void updatePicState() {
        if (number == 1) {
            img_add_picture_first.setVisibility(View.GONE);
            picTxt1.setVisibility(View.GONE);
            img_selected_picture_first.setVisibility(View.VISIBLE);
            img_delete_picture_first.setVisibility(View.VISIBLE);
            BitmapHelp.getBitmapUtils().display(img_selected_picture_first,picturePath);
            picturePathFirst = picturePath;

            tHandle(1, picturePath);
        } else if (number == 2) {
            img_add_picture_second.setVisibility(View.GONE);
            picTxt2.setVisibility(View.GONE);
            img_selected_picture_second.setVisibility(View.VISIBLE);
            img_delete_picture_second.setVisibility(View.VISIBLE);
            BitmapHelp.getBitmapUtils().display(img_selected_picture_second,picturePath);
            picturePathSecond = picturePath;

            tHandle(2, picturePath);
        }
    }

    /**
     * 拍照
     * @param PHOTO_GRAPH
     * PHOTO_GRAPH 剪裁
     * PHOTO_GRAPH_NOT_ZOOM 不剪裁
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
     * @param PHOTO_ZOOM
     * PHOTO_ZOOM 剪裁
     * PHOTO_ALBUM_NOT_ZOOM 不剪裁
     */
    private void formGallerya(int PHOTO_ZOOM) {
        photoName = System.currentTimeMillis() + ".png";
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);
    }

    private List<String> filePathList;// 本地视频路径
//    BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            filePathList = intent.getStringArrayListExtra("list");
//            // ToastUtil.show("本地视频路径--->" + filePathList.get(0));
//            // 设置选择的视频可见并且设置布局
//            img_selected_video.setVisibility(View.VISIBLE);
//            img_video_icon.setVisibility(View.VISIBLE);
//            img_video_icon.bringToFront();
//            img_delete.setVisibility(View.VISIBLE);
//            img_add_video.setVisibility(View.GONE);
//
//            img_selected_video.setImageBitmap(Bimp
//                    .getVideoThumbnail(filePathList.get(0)));
//            videoFilePath = filePathList.get(0);
//            // uploadVideo(filePathList.get(0));
//            img_selected_video.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    String type = "video/mp4";
//                    Uri uri = Uri.parse(videoFilePath);
//                    intent.setDataAndType(uri, type);
//                    startActivity(intent);
//                }
//            });
//
//        }
//    };

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
    public void onDestroy() {
        super.onDestroy();
        //broadcastManager.unregisterReceiver(videoBroadcastReceiver);
//        getActivity().unregisterReceiver(videoBroadcastReceiver);
    }

}
