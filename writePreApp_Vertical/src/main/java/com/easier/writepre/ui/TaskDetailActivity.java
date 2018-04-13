package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SquareAllEssenceGridAdapter;
import com.easier.writepre.adapter.TaskHomeWorkStuSubAdapter;
import com.easier.writepre.adapter.TaskImageSlidePageAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.TaskHomeWorkInfo;
import com.easier.writepre.entity.TaskHomeWorkSubmitInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TaskWorkSubmitListParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TaskWorkSubmitListResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.easier.writepre.widget.TaskChildViewPager;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

/**
 * 作业详情页面
 */
public class TaskDetailActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener2<ListView> {
    private RoundImageView img_head;
    private TextView tv_username;
    private TextView tv_create_time;
    private TextView tv_content;
    private TextView tv_like_number;
    private TextView tv_comment_number;
    private TaskChildViewPager mPager;
    private LinearLayout dot_containter;
    private ImageView video_play;
    private PullToRefreshListView lv_task_detail;
    private int start = 0;
    private int count = 20;
    private View headView;
    private TaskHomeWorkInfo taskHomeWorkInfo;
    private TaskHomeWorkStuSubAdapter taskHomeWorkAdapter;
    private TextView tv_btn;
    private TextView tv_date;
    private boolean isTeacher;
    private int START_ACTIVITY_CODE = 1000;
    private RelativeLayout rel_images;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_taskdetail);
        taskHomeWorkInfo = (TaskHomeWorkInfo) getIntent().getSerializableExtra("data");
        isTeacher = getIntent().getBooleanExtra("isTeacher", false);
        init();
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        this.startActivity(intent);
    }

    private void init() {
        setTopTitle("作业详情");
        tv_btn = (TextView) findViewById(R.id.tv_btn);
        tv_btn.setText("提交作业");

        if (isTeacher || !TextUtils.equals("0", taskHomeWorkInfo.getIs_submit())) {
            tv_btn.setVisibility(View.GONE);
        } else {
            tv_btn.setVisibility(View.VISIBLE);
        }
        tv_btn.setOnClickListener(this);
        taskHomeWorkAdapter = new TaskHomeWorkStuSubAdapter(this);
        taskHomeWorkAdapter.setNeedShowImage(true);
        taskHomeWorkAdapter.setIsTeacher(isTeacher);
        taskHomeWorkAdapter.setHandler(mHandler);
        lv_task_detail = (PullToRefreshListView) findViewById(R.id.lv_task_detail);
        headView = LayoutInflater.from(this).inflate(
                R.layout.list_header_task_detail, null);
        AutoUtils.autoSize(headView);
        tv_date = (TextView) headView.findViewById(R.id.tv_date);
        img_head = (RoundImageView) headView.findViewById(R.id.iv_head);
        tv_username = (TextView) headView.findViewById(R.id.tv_uname);
        tv_create_time = (TextView) headView.findViewById(R.id.tv_ctime);
        mPager = (TaskChildViewPager) headView.findViewById(R.id.pk_image_view_pager);
        dot_containter = (LinearLayout) headView.findViewById(R.id.pk_linear_indicator);
        rel_images = (RelativeLayout) headView.findViewById(R.id.image_layout);
        video_play = (ImageView) headView.findViewById(R.id.play_video);
        tv_content = (TextView) headView.findViewById(R.id.tv_content);
        tv_like_number = (TextView) headView.findViewById(R.id.tv_ok_num);
        tv_comment_number = (TextView) headView
                .findViewById(R.id.tv_comment_num);
        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserDetail(taskHomeWorkInfo.getUser_id());
            }
        });
        img_head.setImageView(StringUtil.getHeadUrl(taskHomeWorkInfo.getHead_img()));
        tv_username.setText(taskHomeWorkInfo.getUname());
        tv_content.setText(taskHomeWorkInfo.getTitle());
        tv_comment_number.setText(taskHomeWorkInfo.getSubmit_cnt());
        tv_date.setText(DateKit.timeFormat(taskHomeWorkInfo.getCtime()));
        showImages();
        lv_task_detail.getRefreshableView().addHeaderView(headView);
        lv_task_detail.setAdapter(taskHomeWorkAdapter);
        lv_task_detail.setOnRefreshListener(this);
    }

    private void setIndicatorSelected(ImageView[] indicator_imgs, int selectedndex) {
        for (int i = 0; i < indicator_imgs.length; i++) { // null异常
            indicator_imgs[i].setImageResource(R.drawable.dot_white);
        }
        // 改变当前背景图片为：选中
        indicator_imgs[selectedndex].setImageResource(R.drawable.dot_red);
    }

    private ImageView[] initIndicator(LinearLayout pDotview_contair, int size) {
        ImageView imgView;
        pDotview_contair.removeAllViews();
        ImageView[] indicator_imgs = new ImageView[size];
        for (int i = 0; i < size; i++) {
            imgView = new ImageView(this);
            LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(20, 20); // 设置原点大小
            params_linear.setMargins(7, 10, 7, 10);
            imgView.setLayoutParams(params_linear);
            try {
                indicator_imgs[i] = imgView;
            } catch (Exception e) {
                return null;
            }
            if (i == 0) { // 初始化第一个为选中状态
                indicator_imgs[i].setImageResource(R.drawable.dot_red);  //setBackgroundResource
            } else {
                indicator_imgs[i].setImageResource(R.drawable.dot_white);
            }
            pDotview_contair.addView(indicator_imgs[i]);
        }
        return indicator_imgs;
    }

    private void showImages() {
        rel_images.setVisibility(View.VISIBLE);
        if (taskHomeWorkInfo.getImg_url() != null && taskHomeWorkInfo.getImg_url().length > 0) {
            if (taskHomeWorkInfo.getImg_url().length == 1) {
                dot_containter.setVisibility(View.GONE);
                mPager.setVisibility(View.VISIBLE);
            } else if (taskHomeWorkInfo.getImg_url().length > 1) {
                dot_containter.setVisibility(View.VISIBLE);
                mPager.setVisibility(View.VISIBLE);
            }
            TaskImageSlidePageAdapter mPagerAdapter = new TaskImageSlidePageAdapter(this,
                    taskHomeWorkInfo.getImg_url());
            mPager.setAdapter(mPagerAdapter);
            // 初始化小圆点数组
            final ImageView[] indicator_imgs = initIndicator(dot_containter, taskHomeWorkInfo.getImg_url().length);
            mPager.setCurrentItem(taskHomeWorkInfo.getImageSelectIndex());
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    LogUtils.e("选中第几个" + arg0 + "");
                    setIndicatorSelected(indicator_imgs, arg0);
                    taskHomeWorkInfo.setImageSelectIndex(arg0);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
        } else {
            rel_images.setVisibility(View.GONE);
        }
    }

    private void imageBrower(int position, String[] urls) {
        //友盟统计
        //        List<String> var = new ArrayList<String>();
        //        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        //        var.add("查看大图");
        //        if (getItem(position) instanceof ContentInfo && !TextUtils.isEmpty(((ContentInfo) getItem(position)).getTopic_id())) {
        //            YouMengType.onEvent(mContext, var, 1, "活动");
        //        } else {
        //            YouMengType.onEvent(mContext, var, 1, "广场");
        //        }
        Intent intent = new Intent(this, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
    }

    public void onBack(){
        Intent intent = new Intent();
        intent.putExtra("data", taskHomeWorkInfo);
        setResult(RESULT_OK,intent);
        finish();
    }
    @Override
    public void onSwipBack() {
        onBack();
    }

    @Override
    public void onTopLeftClick(View view) {
        onBack();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_btn:
                Intent intent = new Intent(this, SendTaskWorkActivity.class);
                intent.putExtra("id", taskHomeWorkInfo.get_id());
                intent.putExtra("modeType", SendTaskWorkActivity.TYPE_STUDENT);
                startActivityForResult(intent, START_ACTIVITY_CODE);
                break;
        }
    }

    private void updateView(String itemId) {
        int start = lv_task_detail.getRefreshableView().getFirstVisiblePosition();
        int end = lv_task_detail.getRefreshableView().getLastVisiblePosition();
        for (int i = start, j = end; i <= j; i++) {
            View view = lv_task_detail.getRefreshableView().getChildAt(i - start);
            if (view.getTag() != null && ((TaskHomeWorkSubmitInfo) lv_task_detail.getRefreshableView().getItemAtPosition(i)).get_id().equals(itemId)) {
                int position = (int) lv_task_detail.getRefreshableView().getItemIdAtPosition(i);
                taskHomeWorkAdapter.updateItemView(view, position);
                return;
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:// 刷新
                    loadRefresh();
                    break;
                case 1:// 更新学生作业列表数据
                    updateStuTaskWorkList((List<TaskHomeWorkSubmitInfo>) msg.obj);
                    break;
                case 3:// 局部更新学生作业列表数据
                    updateView((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 更新学生提交的作业列表
     */
    private void updateStuTaskWorkList(List<TaskHomeWorkSubmitInfo> list) {
        if (list == null) {
            return;
        }
//        if (list.isEmpty()) {
//            ToastUtil.show("暂无更多");
//        } else {
        if (taskHomeWorkAdapter.getCount() == 0) {
            taskHomeWorkAdapter.setData(list);
        } else {
            if (list.isEmpty()) {
                ToastUtil.show("暂无更多");
            } else
                taskHomeWorkAdapter.addData(list);
        }
//        }
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        if (taskHomeWorkInfo != null) {
            taskHomeWorkAdapter.clearData();
            requestSubmitTaskList(taskHomeWorkInfo.get_id(), taskHomeWorkAdapter.getLastId(), count + "");
        }
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        if (taskHomeWorkInfo != null)
            requestSubmitTaskList(taskHomeWorkInfo.get_id(), taskHomeWorkAdapter.getLastId(), count + "");
    }

    /**
     * 获取学生提交的作业详情
     *
     * @param task_id
     * @param last_id
     * @param count
     */
    private void requestSubmitTaskList(String task_id, String last_id, String count) {
        TaskWorkSubmitListParams taskWorkSubmitListParams = new TaskWorkSubmitListParams(task_id, last_id, count);
        RequestManager.request(this, taskWorkSubmitListParams,
                TaskWorkSubmitListResponse.class, this,
                SPUtils.instance().getSocialPropEntity()
                        .getApp_school_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        lv_task_detail.onRefreshComplete();
        if ("0".equals(response.getResCode())) {
            if (response instanceof TaskWorkSubmitListResponse) {
                TaskWorkSubmitListResponse taskWorkSubmitListResponse = (TaskWorkSubmitListResponse) response;
                mHandler.obtainMessage(1, taskWorkSubmitListResponse.getRepBody().getList()).sendToTarget();
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                taskHomeWorkInfo.setIs_submit("1");
                taskHomeWorkInfo.setSubmit_cnt(Integer.parseInt(taskHomeWorkInfo.getSubmit_cnt())+1+"");
                tv_comment_number.setText(taskHomeWorkInfo.getSubmit_cnt());
                if (TextUtils.equals("0", taskHomeWorkInfo.getIs_submit())) {
                    //未提交作业
                    tv_btn.setVisibility(View.VISIBLE);
                } else {
                    //已提交
                    tv_btn.setVisibility(View.GONE);
                }
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadMore();
    }

    public void updateConfirmNo() {
        if(taskHomeWorkInfo!=null){
            taskHomeWorkInfo.setConfirm_cnt((Integer.parseInt(taskHomeWorkInfo.getConfirm_cnt())+1)+"");
        }
    }
}

