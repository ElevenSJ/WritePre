package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.RecommendCourseAdapter;
import com.easier.writepre.adapter.TaskHomeWorkAdapter;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.entity.MaskCoordinateInfo;
import com.easier.writepre.entity.TaskHomeWorkInfo;
import com.easier.writepre.entity.TecXsfCsInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.ExamGetParams;
import com.easier.writepre.param.ExamRecGetParams;
import com.easier.writepre.param.TaskWorkListParams;
import com.easier.writepre.param.TecXsfCsParams;
import com.easier.writepre.param.TecXsfStuInfoGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CurrentExamInfoResponse;
import com.easier.writepre.response.ExamRecResponse;
import com.easier.writepre.response.ExamResponse;
import com.easier.writepre.response.TaskWorkResponse;
import com.easier.writepre.response.TecXsfCsResponse;
import com.easier.writepre.response.TecXsfStuInfoGetResponse;
import com.easier.writepre.rongyun.RongYunUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.SpanStringUtil;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.OpenListViewOnScrollView;
import com.easier.writepre.widget.RoundImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.sj.autolayout.utils.AutoUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 我的报考页面
 */
public class RegisterForExaminationActivity extends BaseActivity {
    private RoundImageView headIcon;
    private TextView tv_name;
    private TextView tv_examination_grades;
    private TextView tv_learning_time;
    private TextView tv_recommendmore;//更多课程
    private TextView tv_taskmore;//更多作业
    private LinearLayout ll_questions;
    private LinearLayout ll_simulation_test;
    private LinearLayout ll_examination;
    private PullToRefreshScrollView sl_content;
    private OpenListViewOnScrollView lv_course_push, lv_taskhomework;
    private RecommendCourseAdapter recommendCourseAdapter;
    private TaskHomeWorkAdapter taskHomeWorkAdapter;
    private boolean isTeacher = false;
    private String circle_id = "";
    private String circle_name = "";
    private boolean needShowMask = false;
    private boolean isFirst = true;
    private final int REQUEST_MASK_CODE = 10223;
    private final int REQUEST_TASKDETAIL_CODE = 10224;
    private MaskCoordinateInfo currMask = null;
    private String infoUrl = "";//考试说明链接
    private static String type = "0";//默认为 0 :小书法师的类型 ,1:专业人才

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_rfe);
        type = (String) SPUtils.instance().get("stu_type", "0");
        //获取传递过来的type,不存在默认为 0 :小书法师的类型
        if (TextUtils.isEmpty(type)) {
            type = "0";
        }
        init();
        requestData();
    }

    @Override
    public void onTopRightTxtClick(View view) {
        super.onTopRightTxtClick(view);
        if (!TextUtils.isEmpty(infoUrl)) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("title", "考试说明");
            intent.putExtra("url", infoUrl);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //true 有显示过蒙版 ,false 没有显示过蒙版
        needShowMask = (boolean) SPUtils.instance().get(SPUtils.MASK_EXAMINATION, false);
        if (needShowMask) {
            requestTecXsfStuInfo();
            requestTaskWorkList("9", "2");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && isFirst) {
            isFirst = false;
            //获取屏幕位置，可以争取获取到位置参数。
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMask(0);
                }
            }, 500);

        }
    }

    /**
     * 显示蒙版
     */
    private void showMask(int index) {
        needShowMask = (boolean) SPUtils.instance().get(SPUtils.MASK_EXAMINATION, false);
        if (!needShowMask) {
            //跳转到蒙版页面
            LogUtils.e("启动蒙版页面");
            currMask = createMaskData(index);
            if (currMask == null) {
                SPUtils.instance().put(SPUtils.MASK_EXAMINATION, true);
                return;
            }
            Intent intentMask = new Intent(RegisterForExaminationActivity.this, MaskActivity.class);
            intentMask.putExtra("maskInfo", currMask);
            startActivityForResult(intentMask, REQUEST_MASK_CODE);
            overridePendingTransition(0, 0);
        }
    }

    private void init() {
        setTopTitle("我的报考");
        infoUrl = getIntent().getStringExtra("info_url");
        if (!TextUtils.isEmpty(infoUrl)) {
            setTopRightTxt("考试说明");
        }
        headIcon = (RoundImageView) findViewById(R.id.head_image);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_examination_grades = (TextView) findViewById(R.id.tv_examination_grades);
        tv_learning_time = (TextView) findViewById(R.id.tv_learning_time);
        ll_questions = (LinearLayout) findViewById(R.id.ll_questions);
        ll_questions.setOnClickListener(this);
        ll_simulation_test = (LinearLayout) findViewById(R.id.ll_simulation_test);
        ll_simulation_test.setOnClickListener(this);
        ll_examination = (LinearLayout) findViewById(R.id.ll_examination);
        ll_examination.setOnClickListener(this);
        tv_recommendmore = (TextView) findViewById(R.id.tv_recommendmore);
        findViewById(R.id.rel_top_recommendtitle).setOnClickListener(this);
        tv_taskmore = (TextView) findViewById(R.id.tv_taskmore);
        findViewById(R.id.rel_top_tasktitle).setOnClickListener(this);
        lv_course_push = (OpenListViewOnScrollView) findViewById(R.id.lv_course_push);
        lv_taskhomework = (OpenListViewOnScrollView) findViewById(R.id.lv_taskhomework);
        recommendCourseAdapter = new RecommendCourseAdapter(this);
        lv_course_push.setAdapter(recommendCourseAdapter);
        taskHomeWorkAdapter = new TaskHomeWorkAdapter(this);
        taskHomeWorkAdapter.setNeedShowImage(true);
        lv_taskhomework.setAdapter(taskHomeWorkAdapter);
        lv_taskhomework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskHomeWorkAdapter taskHomeWorkAdapter = (TaskHomeWorkAdapter) adapterView.getAdapter();
                TaskHomeWorkInfo taskHomeWorkInfo = taskHomeWorkAdapter.getItem(i);
                Intent intent = new Intent(RegisterForExaminationActivity.this, TaskDetailActivity.class);
                intent.putExtra("data", taskHomeWorkInfo);
                intent.putExtra("isTeacher", isTeacher);
                startActivity(intent);
            }
        });
        findViewById(R.id.fab).setOnClickListener(this);
        sl_content = (PullToRefreshScrollView) findViewById(R.id.sl_content);
        sl_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                requestData();
            }
        });
    }

    private void requestData() {
        requestTecXsfStuInfo();
        requestTecXsfCsList("9", "2");
        requestTaskWorkList("9", "2");
    }

    int h = 154;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rel_top_recommendtitle://更多课程
                if (recommendCourseAdapter.getCount() > 0) {
                    Intent intent = new Intent(this, PushCurriculumListActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rel_top_tasktitle://更多作业
                if (recommendCourseAdapter.getCount() > 0) {
                    Intent intent = new Intent(this, TaskListActivity.class);
                    intent.putExtra("circle_id", circle_id);
                    intent.putExtra("circle_name", circle_name);
                    startActivity(intent);
                }
                break;
            case R.id.ll_questions://理论题库
                Intent intent = new Intent(this, PracticeQuestionsListActivity.class);
                intent.putExtra("from", PracticeQuestionsListActivity.FROM_QUESTIONS);
                startActivity(intent);
                break;
            case R.id.ll_simulation_test://理论测试
                Intent intentTheoreticalTest = new Intent(RegisterForExaminationActivity.this, PracticeQuestionsListActivity.class);
                intentTheoreticalTest.putExtra("from", PracticeQuestionsListActivity.FROM_THEORETICAL_TEST);
                startActivity(intentTheoreticalTest);
                break;
            case R.id.ll_examination://正式考试
                dlgLoad.loading("正在获取考试信息...");
                requestExamProgress();
                break;
            case R.id.fab:
                RongYunUtils.getInstances().startGroupChat(this, circle_id, circle_name, "0");
                break;

        }
    }

    /**
     * 获取考试进度
     */
    private void requestExamProgress() {
        String stu_type = (String) SPUtils.instance().get("stu_type", "0");
        RequestManager.request(this, new ExamRecGetParams(stu_type), ExamRecResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:// 刷新
                    break;
                case 1://刷新考生信息
                    TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody body = (TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody) msg.obj;
                    updateInfo(body);
                    break;
                case 2://刷新推荐课程数据
                    List<TecXsfCsInfo> tecXsfCsInfos = (List<TecXsfCsInfo>) msg.obj;
                    updateRecommendCourse(tecXsfCsInfos);
                    break;
                case 3://刷新学习圈作业数据
                    List<TaskHomeWorkInfo> taskHomeWorkInfos = (List<TaskHomeWorkInfo>) msg.obj;
                    updateTaskInfos(taskHomeWorkInfos);
                    break;
                case 4://获取当前考试进度信息
                    ExamRecResponse.ExamRecInfo examRecInfo = (ExamRecResponse.ExamRecInfo) msg.obj;
                    if (examRecInfo != null) {
                        Intent intentExam = new Intent(RegisterForExaminationActivity.this, ExaminationProcedureActivity.class);
                        intentExam.putExtra(ExaminationProcedureActivity.TEST_MODE, ExaminationProcedureActivity.TEST_FORMAL);
                        intentExam.putExtra("title", "正式考试");
                        RegisterForExaminationActivity.this.startActivity(intentExam);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 生成 蒙版数据
     *
     * @return
     */
    private MaskCoordinateInfo createMaskData(int index) {
        MaskCoordinateInfo maskCoordinateInfo = null;
        switch (index) {
            case 0:
                maskCoordinateInfo = new MaskCoordinateInfo();
                int[] mask = getViewLocation(findViewById(R.id.tv_simulation_test));
                maskCoordinateInfo.setIndex(0);
                maskCoordinateInfo.setTopLeftX(mask[0] - AutoUtils.getPercentWidthSize(100));
                maskCoordinateInfo.setTopLeftY(mask[1] - AutoUtils.getPercentHeightSize(85));
                if (TextUtils.equals(type, "0")) {
                    maskCoordinateInfo.setBgRes(R.drawable.exam_mask_1_1);
                } else {
                    maskCoordinateInfo.setBgRes(R.drawable.exam_mask_1_2);
                }
                LogUtils.e("mask0.x=" + maskCoordinateInfo.getTopLeftX() + "| mask0.y=" + maskCoordinateInfo.getTopLeftY());
                break;
            case 1:
                if (lv_course_push.getVisibility() == View.VISIBLE) {
                    maskCoordinateInfo = new MaskCoordinateInfo();
                    int[] mask1 = getViewLocation(findViewById(R.id.rel_top_recommendtitle));
                    maskCoordinateInfo.setIndex(1);
                    maskCoordinateInfo.setTopLeftX(mask1[0] + AutoUtils.getPercentWidthSize(210));
                    maskCoordinateInfo.setTopLeftY(mask1[1] + AutoUtils.getPercentHeightSize(50));
                    maskCoordinateInfo.setBgRes(R.drawable.exam_mask_2);
                    LogUtils.e("mask1.x=" + maskCoordinateInfo.getTopLeftX() + "| mask1.y=" + maskCoordinateInfo.getTopLeftY());
                }
                break;
            case 2:
                if (lv_taskhomework.getVisibility() == View.VISIBLE) {
                    final int titleHight = findViewById(R.id.rel_top_tasktitle).getMeasuredHeight();
                    sl_content.getRefreshableView().post(new Runnable() {
                        @Override
                        public void run() {

                            sl_content.getRefreshableView().scrollBy(0, AutoUtils.getPercentHeightSize(titleHight));
                        }
                    });
                    maskCoordinateInfo = new MaskCoordinateInfo();
                    int[] mask2 = getViewLocation(findViewById(R.id.rel_top_tasktitle));
                    maskCoordinateInfo.setIndex(2);
                    maskCoordinateInfo.setTopLeftX(mask2[0] + 10);
                    maskCoordinateInfo.setTopLeftY(mask2[1] - AutoUtils.getPercentHeightSize(300 + titleHight));
                    maskCoordinateInfo.setBgRes(R.drawable.exam_mask_3);
                    LogUtils.e("mask2.x=" + maskCoordinateInfo.getTopLeftX() + "| mask2.y=" + maskCoordinateInfo.getTopLeftY());
                }
                break;
        }
        return maskCoordinateInfo;
    }

    private int[] getViewLocation(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    private String dateFormat(String date) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy年MM月dd日");
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     * 刷新考生信息
     */
    private void updateInfo(TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody body) {
        if (body == null) {
            return;
        }
        circle_id = body.getCircle_id();
        circle_name = body.getCircle_name();
        isTeacher = TextUtils.equals("1", body.getIs_xsf_teacher()) ? true : false;
        headIcon.setImageView(StringUtil.getHeadUrl(body.getPhoto_url()));
        tv_name.setText(body.getReal_name());
        if (TextUtils.equals("0", type)) {
            ((TextView) findViewById(R.id.tv_recommend_title)).setText("小书法师推送课");
//            tv_learning_time.setText(SpanStringUtil.matcherSearchTitleFirst(getResources().getColor(R.color.text_color_red_1), "0" + "/" + "120", "0"));
            tv_examination_grades.setText(SpanStringUtil.matcherSearchTitle(getResources().getColor(R.color.text_color_red_1), "小书法师 " + body.getStu_level() + " 级", body.getStu_level()));
        } else {
            ((TextView) findViewById(R.id.tv_recommend_title)).setText("专业人才推送课");
//            tv_learning_time.setText(SpanStringUtil.matcherSearchTitleFirst(getResources().getColor(R.color.text_color_red_1), "0" + "/" + "200", "0"));
            tv_examination_grades.setText(SpanStringUtil.matcherSearchTitle(getResources().getColor(R.color.text_color_red_1), "专业人才 " + body.getStu_level() + " 级", body.getStu_level()));
        }
        tv_learning_time.setText(SpanStringUtil.matcherSearchTitleFirst(getResources().getColor(R.color.text_color_red_1), body.getStu_time() + "/" + body.getNeed_time(), body.getStu_time()));

    }

    /**
     * 更新推荐课程数据
     *
     * @param tecXsfCsInfos
     */
    private void updateRecommendCourse(List<TecXsfCsInfo> tecXsfCsInfos) {
        if (tecXsfCsInfos == null) {
            findViewById(R.id.rel_top_recommendtitle).setVisibility(View.GONE);
            lv_course_push.setVisibility(View.GONE);
            return;
        }
        if (!tecXsfCsInfos.isEmpty()) {
            lv_course_push.setVisibility(View.VISIBLE);
            findViewById(R.id.rel_top_recommendtitle).setVisibility(View.VISIBLE);
            if (TextUtils.equals("0", type)) {
                ((TextView) findViewById(R.id.tv_recommend_title)).setText("小书法师推送课");
            } else {
                ((TextView) findViewById(R.id.tv_recommend_title)).setText("专业人才推送课");
            }
            recommendCourseAdapter.setData(tecXsfCsInfos);
        } else {
            lv_course_push.setVisibility(View.GONE);
            findViewById(R.id.rel_top_recommendtitle).setVisibility(View.GONE);
        }
    }

    /**
     * 更新学习圈作业数据
     *
     * @param taskHomeWorkInfos
     */
    private void updateTaskInfos(List<TaskHomeWorkInfo> taskHomeWorkInfos) {
        if (taskHomeWorkInfos == null) {
            findViewById(R.id.rel_top_tasktitle).setVisibility(View.GONE);
            lv_taskhomework.setVisibility(View.GONE);
            return;
        }
        if (!taskHomeWorkInfos.isEmpty()) {
            lv_taskhomework.setVisibility(View.VISIBLE);
            findViewById(R.id.rel_top_tasktitle).setVisibility(View.VISIBLE);
            taskHomeWorkAdapter.setData(taskHomeWorkInfos);
        } else {
            lv_taskhomework.setVisibility(View.GONE);
            findViewById(R.id.rel_top_tasktitle).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if (response == null) {
            return;
        }
        dlgLoad.dismissDialog();
        sl_content.onRefreshComplete();
        if ("0".equals(response.getResCode())) {
            if (response instanceof TecXsfCsResponse) {
                TecXsfCsResponse tecXsfCsResponse = (TecXsfCsResponse) response;
                if (tecXsfCsResponse != null) {
                    List<TecXsfCsInfo> tecXsfCsInfos = tecXsfCsResponse.getRepBody().getList();
                    mHandler.obtainMessage(2, tecXsfCsInfos).sendToTarget();
                }
            } else if (response instanceof TecXsfStuInfoGetResponse) {
                TecXsfStuInfoGetResponse xsfResult = (TecXsfStuInfoGetResponse) response;
                if (xsfResult != null) {
                    TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody body = xsfResult
                            .getRepBody();
                    mHandler.obtainMessage(1, body).sendToTarget();
                }
            } else if (response instanceof TaskWorkResponse) {
                TaskWorkResponse taskWorkResponse = (TaskWorkResponse) response;
                if (taskWorkResponse != null) {
                    List<TaskHomeWorkInfo> homeWorkInfos = taskWorkResponse.getRepBody().getList();
                    mHandler.obtainMessage(3, homeWorkInfos).sendToTarget();
                }
            } else if (response instanceof ExamRecResponse) {
                ExamRecResponse examRecResponse = (ExamRecResponse) response;
                ExamRecResponse.ExamRecInfo examRecInfo = examRecResponse.getRepBody();
                mHandler.obtainMessage(4, examRecInfo).sendToTarget();

            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 查询考生信息
     */
    private void requestTecXsfStuInfo() {
        RequestManager.request(this, new TecXsfStuInfoGetParams((String) SPUtils.instance().get("stu_type", "0")), TecXsfStuInfoGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    /**
     * 获取推荐课程
     *
     * @param last_id 首次默认 9
     * @param count
     */
    private void requestTecXsfCsList(String last_id, String count) {
        TecXsfCsParams tecXsfCsParams = new TecXsfCsParams(last_id, count);
        RequestManager.request(this, tecXsfCsParams, TecXsfCsResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    /**
     * 获取学习圈作业列表
     *
     * @param last_id 首次默认 9
     * @param count
     */
    private void requestTaskWorkList(String last_id, String count) {
        TaskWorkListParams taskWorkListParams = new TaskWorkListParams(last_id, count);
        RequestManager.request(this, taskWorkListParams, TaskWorkResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MaskActivity.RESULT_MASK_CODE && requestCode == REQUEST_MASK_CODE) {
            //蒙版返回
            if (data != null) {
                MaskCoordinateInfo coordinateInfo = (MaskCoordinateInfo) data.getSerializableExtra("resultMask");
                if (coordinateInfo.getIndex() < 2) {
                    int maskIndex = coordinateInfo.getIndex() + 1;
                    showMask(maskIndex);
                } else {
                    SPUtils.instance().put(SPUtils.MASK_EXAMINATION, true);
                }
            }
        }
//        if (requestCode == REQUEST_TASKDETAIL_CODE && resultCode == RESULT_OK) {
//            if (data != null) {
//                TaskHomeWorkInfo taskHomeWorkInfo = (TaskHomeWorkInfo) data.getSerializableExtra("data");
//                if (taskHomeWorkInfo != null) {
//                    //刷新作业代查看人数
//                    taskHomeWorkAdapter.updateData(taskHomeWorkInfo);
//                    taskHomeWorkAdapter.notifyDataSetChanged();
//                }
//            }
//
//        }
    }

}

