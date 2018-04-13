package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.TecXsfCsInfo;
import com.easier.writepre.fragment.VideoPlayFragment;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TimeGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TimeGetResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

import java.lang.reflect.Method;

/**
 * 小书法师课程详情页面
 */
public class CurriculumInfoActivity extends NoSwipeBackActivity {
    private FrameLayout mVideoLayout;
    private boolean isFullscreen;
    private VideoPlayFragment videoPlayFragment;
    private float IMAGE_HEIGHT = 0.566f;//视频竖屏时 屏幕比例
    private Window window;
    private TextView tv_introduce;
    private RoundImageView riv_head;
    private TextView tv_teacher_name;
    private TextView tv_teacher_cv;
    private TecXsfCsInfo tecXsfCsInfo;
    public static final int CODE_GET_TIME = 0;
    public static final int CODE_GET_TIME_SUCCESS = 1;
    public static final int CODE_GET_TIME_FAIL = 2;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        window = getWindow();
        setContentView(R.layout.activity_curriculum_info);
        tecXsfCsInfo = (TecXsfCsInfo) getIntent().getSerializableExtra("data");
        init();
    }

    private void init() {
        setTopTitle("小书法师视频推荐");
        //视频课载体
        mVideoLayout = (FrameLayout) findViewById(R.id.video_layout);
        RelativeLayout.LayoutParams videoLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WritePreApp.getApp().getWidth(IMAGE_HEIGHT));
        videoLayoutParams.setMargins(0, AutoUtils.getPercentHeightSize(100), 0, 0);
        mVideoLayout.setLayoutParams(videoLayoutParams);
        mVideoLayout.setBackgroundColor(getResources().getColor(R.color.black));
        mVideoLayout.setVisibility(View.VISIBLE);
        addVideoFragment();
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        riv_head = (RoundImageView) findViewById(R.id.riv_head);
        tv_teacher_name = (TextView) findViewById(R.id.tv_teacher_name);
        tv_teacher_cv = (TextView) findViewById(R.id.tv_teacher_cv);
        if (tecXsfCsInfo != null) {
            setTopTitle(tecXsfCsInfo.getTitle());
            tv_introduce.setText(tecXsfCsInfo.getDesc());
            riv_head.setImageView(StringUtil.getHeadUrl(tecXsfCsInfo.getHead_img()));
            riv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //toUserDetail(tecXsfCsInfo.getTeacher_id());
                }
            });
            tv_teacher_name.setText(tecXsfCsInfo.getTeacher_name());
            tv_teacher_cv.setText(tecXsfCsInfo.getTeacher_desc());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(tecXsfCsInfo.getVideo_url())) {
                        ToastUtil.show("播放地址不存在!");
                    } else {
                        videoPlayFragment.setProgressEnable(false);
                        String realUrl = StringUtil.getVoiceUrl(tecXsfCsInfo.getVideo_url());
                        LogUtils.e("播放地址:" + realUrl);
                        String currentUrl = WritePreApp.getProxy(CurriculumInfoActivity.this).getProxyUrl(realUrl);
                        videoPlayFragment.playVideo(currentUrl, tecXsfCsInfo.getTitle());
                    }
                }
            }, 1000);

        }
    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        this.startActivity(intent);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_GET_TIME://获取学时
                    if (LoginUtil.checkLogin(CurriculumInfoActivity.this)) {
                        dlgLoad.loading("正在获取学时...");
                        requestTimeGet(tecXsfCsInfo.get_id(), "2");
                    }else
                    {
                        ToastUtil.show("学时获取失败,登录后重新学习!");
                    }
                    break;
                case CODE_GET_TIME_SUCCESS://获取学时 成功
                    showGetTimeDialog(true);
                    break;
                case CODE_GET_TIME_FAIL://获取学时 失败
                    showGetTimeDialog(false);
                    break;
            }
        }
    };

    private void addVideoFragment() {
        videoPlayFragment = new VideoPlayFragment();
        videoPlayFragment.setHandler(handler);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.video_layout, videoPlayFragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 获取学时
     *
     * @param xsf_cs_id
     */
    private void requestTimeGet(String xsf_cs_id, String time) {
        RequestManager.request(this, new TimeGetParams(xsf_cs_id), TimeGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    private void showGetTimeDialog(final boolean isSuccess) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_timeconfirm,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_tips);
        if (isSuccess) {
            tv.setText("课程学习完毕,获得5个学时!");
        } else {
            tv.setText("学时获取失败");
        }
        TextView confirm = (TextView) view.findViewById(R.id.tv_confirm);
        if (isSuccess) {
            confirm.setText("确定");
        } else {
            confirm.setText("重新获取");
        }
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isSuccess) {
                } else {
                    handler.sendEmptyMessage(CODE_GET_TIME);
                }
                dialog.dismiss();
            }
        });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 横竖屏切换
     *
     * @param isFullscreen
     */
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (CurriculumInfoActivity.this.isFullscreen) {
                    if (checkDeviceHasNavigationBar(CurriculumInfoActivity.this)) {
                        setHideVirtualKey(window);
                    }
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mVideoLayout.setLayoutParams(layoutParams);
                    mVideoLayout.setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    RelativeLayout.LayoutParams videoLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WritePreApp.getApp().getWidth(IMAGE_HEIGHT));
                    videoLayoutParams.setMargins(0, AutoUtils.getPercentHeightSize(100), 0, 0);
                    mVideoLayout.setLayoutParams(videoLayoutParams);
                    mVideoLayout.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            if (videoPlayFragment != null)
                videoPlayFragment.setFullscreen(false);
        } else {
            finish();
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    /**
     * 请求数据
     */
    private void requestData() {
        //用于测试
        //handler.sendEmptyMessage(CODE_GET_TIME);
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof TimeGetResponse) {
                TimeGetResponse timeGetResponse = (TimeGetResponse) response;
                if (TextUtils.equals("1", timeGetResponse.getRepBody().getStudy_status())) {
                    handler.sendEmptyMessage(CODE_GET_TIME_SUCCESS);
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
            handler.sendEmptyMessage(CODE_GET_TIME_FAIL);
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
}
