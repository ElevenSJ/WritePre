package com.easier.writepre.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerFragmentAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.BaseCourseCategory;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.CourseJJFragment;
import com.easier.writepre.fragment.CourseMLFragment;
import com.easier.writepre.fragment.CoursePLFragment;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CourseCategoryParams;
import com.easier.writepre.param.VodCourseCategoryParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseCategoryResponse;
import com.easier.writepre.response.VodCourseCategoryResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.DisplayUtil;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.video.universalvideoview.UniversalMediaController;
import com.easier.writepre.video.universalvideoview.UniversalVideoView;
import com.easier.writepre.widget.CustomScrollView;
import com.easier.writepre.widget.MainViewPager;
import com.easier.writepre.widget.TabIndicator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程目录
 */
public class CourseCatalogActivityNew extends NoSwipeBackActivity implements UniversalVideoView.VideoViewCallback {
    private final int THIRD_TAB = 2;
    private final int FIST_TAB = 0;
    private final int SECOND_TAB = 1;
    private static final String[] CONTENT = new String[]{"简介", "目录", "评论"};
    public final static String COURSE_ID = "course_id";
    public final static String COURSE_NAME = "course_name";
    public final static String COURSE_GROUP = "course_group";
    public final static String COURSE_TYPE = "course_type";
    public final static String TYPE = "type";

    public final static String vodTypeTag = "vod"; //视频课程类型
    public static String type = ""; //课程类型，普通课程，视频课程
    public static String courseType = "";//课程分类
    public static String courseId = "";
    public static String courseName = "";
    public static String courseGroup = "";
    public static BaseCourseCategory categoryList;


    private CustomScrollView mainScroll;

    private SimpleDraweeView courseIcon;

    private ImageView imgPlayVideo;

    private MainViewPager pager;

    private TabIndicator indicator;

    private ViewPagerFragmentAdapter adapter;

    private final float TITLE_HEIGHT = 0.074f;
    private float IMAGE_HEIGHT = 0.566f;
    private final float TAB_HEIGHT = 0.07f;

    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    private static final String NEED_SHOWVIDEO_KEY = "NEED_SHOWVIDEO_KEY";
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;

    private FrameLayout mVideoLayout;

    private int mSeekPosition;
    private boolean isFullscreen;
    private boolean showVideoViewTag = false;//是否展示视频播放控件
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
        setContentView(R.layout.activity_course_catalog_new);
        type = getIntent().getStringExtra(TYPE);
        courseType = getIntent().getStringExtra(COURSE_TYPE);
        courseGroup = getIntent().getStringExtra(COURSE_GROUP);
        courseId = getIntent().getStringExtra(COURSE_ID);
        courseName = getIntent().getStringExtra(COURSE_NAME);
        initView();
        if (!TextUtils.isEmpty(type) && type.equals(vodTypeTag)) {
            //视频课程
            getVodCourseCatalog();
        } else {
            //普通课程
            getCourseCatalog();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 获取视频课程
     */
    private void getVodCourseCatalog() {
        dlgLoad.loading();
        RequestManager.request(this, new VodCourseCategoryParams(courseId), VodCourseCategoryResponse.class, this,
                Constant.URL);
    }

    /**
     * 获取普通课程
     */
    private void getCourseCatalog() {
        dlgLoad.loading();
        RequestManager.request(this, new CourseCategoryParams(courseId), CourseCategoryResponse.class, this,
                Constant.URL);
    }

    private void initView() {
        //视频课程宽高比1：0.556 ，其他课程保持不变1：0.333
        if(type.equals(vodTypeTag)){
            IMAGE_HEIGHT=0.566f;
        }else{
            IMAGE_HEIGHT=0.333f;
        }
        setTopTitle("课程详情");
        //动态设置title高度
//        RelativeLayout.LayoutParams topLayoutParams = new RelativeLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getHeight(TITLE_HEIGHT));
//        findViewById(R.id.top).setLayoutParams(topLayoutParams);
        imgPlayVideo = (ImageView) findViewById(R.id.play_video);
        mainScroll = (CustomScrollView) findViewById(R.id.main_layout);
        courseIcon = (SimpleDraweeView) findViewById(R.id.course_icon);
        //动态设置图片高度
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getWidth(IMAGE_HEIGHT));
        courseIcon.setLayoutParams(layoutParams);

        if (type.equals(vodTypeTag)) {
            imgPlayVideo.setVisibility(View.VISIBLE);
            courseIcon.setOnClickListener(this);
            imgPlayVideo.setOnClickListener(this);
        } else {
            imgPlayVideo.setVisibility(View.GONE);
        }

        //动态设置Tab高度
        indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);
        LinearLayout.LayoutParams tabLayoutParams = new LinearLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getHeight(TAB_HEIGHT));
        indicator.setLayoutParams(tabLayoutParams);

        //动态设置viewpage高度
        pager = (MainViewPager) findViewById(R.id.course_main_viewpager);
        LinearLayout.LayoutParams pagerLayoutParams = new LinearLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getHeight(1f - TITLE_HEIGHT - TAB_HEIGHT) - DisplayUtil.getStatusHeight(this));
        pager.setLayoutParams(pagerLayoutParams);

        adapter = new ViewPagerFragmentAdapter(
                getSupportFragmentManager(), CONTENT);
        adapter.addFragment(new CourseJJFragment());
        adapter.addFragment(new CourseMLFragment());
        adapter.addFragment(new CoursePLFragment());

        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        indicator.setViewPage(pager);
        //初始化位置为目录
        pager.setCurrentItem(SECOND_TAB);

    }

    private void initVideoView() {
        mVideoLayout = (FrameLayout) findViewById(R.id.video_layout);
        mVideoLayout.setVisibility(View.VISIBLE);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WritePreApp.getApp().getWidth(IMAGE_HEIGHT));
        mVideoLayout.setLayoutParams(layoutParams);
        mVideoLayout.setBackgroundColor(getResources().getColor(R.color.black));
        mVideoView.setAutoRotation(true);
        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();
        mVideoView.setVideoViewCallback(this);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //TODO 播放完成
            }
        });
    }

    /**
     * 视频播放
     *
     * @param url
     */
    private void playVideo(String url, String title) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.show("无效的播放地址!");
            return;
        }
        showVideoViewTag = true;
        if (mVideoView != null) {
            if (url.startsWith("http")) {
                String currentUrl = WritePreApp.getProxy(this).getProxyUrl(url);
                mVideoView.setVideoPath(currentUrl);
            } else {
                mVideoView.setVideoPath(url);
            }
            mVideoView.setVideoPath(url);
            if (mMediaController != null) {
                mMediaController.setTitle(TextUtils.isEmpty(title) ? "" : title);
                mMediaController.showLoading();
            }
            mVideoView.requestFocus();
            mVideoView.start();

        }

    }

    /**
     * 更新课程信息
     */
    private void updateCourseInfo() {
        if (categoryList != null) {
            courseIcon.setImageURI(StringUtil.getImgeUrl(TextUtils.isEmpty(categoryList.getFace_url_m()) ? TextUtils.isEmpty(categoryList.getFace_url_h()) ? categoryList.getFace_url() : categoryList.getFace_url_h() : categoryList.getFace_url_m()));
            ((CourseJJFragment) adapter.getItem(FIST_TAB)).updateData();
            ((CourseMLFragment) adapter.getItem(SECOND_TAB)).initData();
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.play_video:
            case R.id.course_icon:
                ((CourseMLFragment) adapter.getItem(SECOND_TAB)).onGroupClick(0);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        categoryList = null;
        courseId = null;
        courseName = null;
        courseType = null;
        courseGroup = null;
        if (showVideoViewTag) {
            if (mVideoView != null) {
                mVideoView.stopPlayback();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showVideoViewTag) {
            if (mVideoView != null && mSeekPosition > 0) {
                mVideoView.seekTo(mSeekPosition);
                if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_ONE));
        var.add(courseType);
        var.add(courseGroup + courseName);
        YouMengType.onEvent(this, var, getShowTime(), courseGroup + courseName);
        if (showVideoViewTag) {
            if (mVideoView != null && mVideoView.isPlaying()) {
                mSeekPosition = mVideoView.getCurrentPosition();
                mVideoView.pause();
            }
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();
        if (response.getResCode().equals("0")) {
            if (response instanceof CourseCategoryResponse) {
                categoryList = ((CourseCategoryResponse) response).getRepBody();
                if (categoryList != null) {
                    updateCourseInfo();
                }
            } else if (response instanceof VodCourseCategoryResponse) {
                categoryList = ((VodCourseCategoryResponse) response).getRepBody();
                if (categoryList != null) {
                    updateCourseInfo();
                }
            }

        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    public void setChildViewScrollTop(boolean isTop) {
        mainScroll.setChildViewOnScorllTop(isTop);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
        outState.putBoolean(NEED_SHOWVIDEO_KEY, showVideoViewTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        if (outState != null) {
            mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
            showVideoViewTag = outState.getBoolean(NEED_SHOWVIDEO_KEY);
        }
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (CourseCatalogActivityNew.this.isFullscreen) {
                    if (checkDeviceHasNavigationBar(CourseCatalogActivityNew.this)) {
                        setHideVirtualKey(window);
                    }
                    RelativeLayout.LayoutParams layoutParamsScrollView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mainScroll.setLayoutParams(layoutParamsScrollView);
                    mainScroll.scrollTo(0, 0);
                    mainScroll.setEnableScroll(false);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mVideoLayout.setLayoutParams(layoutParams);
                    mVideoLayout.setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    RelativeLayout.LayoutParams layoutParamsScrollView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParamsScrollView.setMargins(0, AutoUtils.getPercentHeightSize(100), 0, 0);
                    mainScroll.setLayoutParams(layoutParamsScrollView);
                    mainScroll.setEnableScroll(true);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WritePreApp.getApp().getWidth(IMAGE_HEIGHT));
                    mVideoLayout.setLayoutParams(layoutParams);
                    mVideoLayout.setBackgroundColor(getResources().getColor(R.color.black));
                }

            }
        });


    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        LogUtils.e("暂停");
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        LogUtils.e("开始");
        mVideoView.setBackgroundResource(R.drawable.transparent);
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        LogUtils.e("缓冲开始");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        LogUtils.e("缓冲结束");
    }

    @Override
    public void onFinishVideoView() {
        if (mVideoView != null) {
            mVideoView.setAutoRotation(false);
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        //关闭播放器
        if (mVideoLayout != null) {
            mVideoLayout.setVisibility(View.GONE);
        }
        showVideoViewTag=false;
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            if (mVideoView != null)
                mVideoView.setFullscreen(false);
        } else {
            finish();
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    //判断是否有 虚拟按键
    public boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    //隐藏虚拟按键
    public void setHideVirtualKey(Window window) {
        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        if (window != null)
            window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    public void playCourseDetail(String video_title, String video_url) {
        if (TextUtils.isEmpty(video_url)) {
            ToastUtil.show("未找到该视频");
            return;
        }
        if (mVideoView == null) {
            initVideoView();
        }
        playVideo(StringUtil.getVideoUrl(video_url), video_title);
    }
}