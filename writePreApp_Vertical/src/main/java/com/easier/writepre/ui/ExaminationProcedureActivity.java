package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.ExamRecGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ExamRecResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 考试流程页面
 *
 * @author zhoulu
 */

public class ExaminationProcedureActivity extends BaseActivity {

    public final static String TEST_MODE = "test_mode";

    public final static int TEST_MOCK = 1;//模拟考试
    public final static int TEST_FORMAL = 2;//正式考试

    private int testMode = TEST_MOCK;

    private Button btn_theory, btn_practice, btn_post;
    private TextView tv_theory, tv_practice, tv_post;
    private LinearLayout ll_theory, ll_practice, ll_post;
    private String id, exam_id, stu_type, noteLink;
    private ExamRecResponse.ExamRecInfo currentExamRecInfo;
    private PullToRefreshScrollView pullToRefreshScrollView;
    private TextView tv_time_and_time, tv_status;
    private String title;
    private RelativeLayout rel_content;
    WindowManager wm;
    private boolean isNeedShowError;
    private String practice_info_url;
    private int practice_count;
    private int theory_count;
    private int practice_count_limit;//实践考试还剩的次数
    private int theory_count_limit;//理论考试还剩的次数

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        setContentView(R.layout.activity_exam_pro);
        testMode = getIntent().getIntExtra(TEST_MODE, testMode);
        stu_type = (String) SPUtils.instance().get("stu_type", "0");
        title = getIntent().getStringExtra("title");
        isNeedShowError = (boolean) SPUtils.instance().get(SPUtils.EXAMTIMEOUT, true);
        initView();
    }

    private void initView() {
        pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_to_refresh_scrollview);
        rel_content = (RelativeLayout) findViewById(R.id.rel_content);
        ll_theory = (LinearLayout) findViewById(R.id.ll_theory);
        ll_practice = (LinearLayout) findViewById(R.id.ll_practice);
        ll_post = (LinearLayout) findViewById(R.id.ll_post);
        switch (testMode) {
            case TEST_MOCK:
                setTopTitle("模拟考试");
                ll_post.setVisibility(View.INVISIBLE);
                break;
            case TEST_FORMAL:
                setTopTitle("正式考试");
                break;
        }
        tv_time_and_time = (TextView) findViewById(R.id.tv_time_and_time);
        tv_status = (TextView) findViewById(R.id.tv_status);
        btn_theory = (Button) findViewById(R.id.btn_theory);
        btn_practice = (Button) findViewById(R.id.btn_practice);
        btn_post = (Button) findViewById(R.id.btn_post);

        tv_theory = (TextView) findViewById(R.id.tv_theory);
        tv_practice = (TextView) findViewById(R.id.tv_practice);
        tv_post = (TextView) findViewById(R.id.tv_post);

        tv_theory.setOnClickListener(this);
        tv_practice.setOnClickListener(this);
        tv_post.setOnClickListener(this);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                requestExamProgress();
            }
        });
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rel_content.getLayoutParams();
        layoutParams.height = (getScreenHeight(this) - getStatusHeight(this) - AutoUtils.getPercentHeightSize(150));
        rel_content.setLayoutParams(layoutParams);
        setTheoryEnable(false, "等待");
        setPracticeEnable(false, "等待");
        setPostEnable(false, "等待");
    }

    private String dateYMD(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String string = "";
        string = sdf.format(new Date());
        return string;
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestExamProgress();
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen.xml");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    private void setTheoryEnable(boolean enable, String btn_str) {
        btn_theory.setText(btn_str);
        btn_theory.setEnabled(enable);
        tv_theory.setEnabled(enable);
    }

    private void setPracticeEnable(boolean enable, String btn_str) {
        btn_practice.setText(btn_str);
        btn_practice.setEnabled(enable);
        tv_practice.setEnabled(enable);
    }

    private void setPostEnable(boolean enable, String btn_str) {
        btn_post.setText(btn_str);
        btn_post.setEnabled(enable);
        tv_post.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_theory:
                if (theory_count_limit > 0) {
                    ToastUtil.show("开始理论考试");
                    Intent intentLoad = new Intent(this, LoaddingPaperActivity.class);
                    intentLoad.putExtra("theory_info_url", currentExamRecInfo.getTheory_info_url());
                    intentLoad.putExtra("theory_score", currentExamRecInfo.getTheory_score());
                    intentLoad.putExtra("theory_count_limit", theory_count_limit);
                    startActivity(intentLoad);
                } else {
                    ToastUtil.show("您的理论考试机会已用完");
                }

                break;
            case R.id.tv_practice:
                if (practice_count_limit > 0) {
                    ToastUtil.show("开始实践考试");
                    Intent intentPractice = new Intent(this, XsfTecPracticeExamTipActivity.class);
                    intentPractice.putExtra("practice_info_url", practice_info_url);
                    intentPractice.putExtra("stu_type", stu_type);
                    intentPractice.putExtra("practice_count_limit", practice_count_limit);
                    startActivity(intentPractice);
                } else {
                    ToastUtil.show("您的实践考试机会已用完");
                }

                break;
            case R.id.tv_post:
                //ToastUtil.show("开始提交作品");
                Intent postIntent = new Intent(this, PostWorksActivity.class);
                postIntent.putExtra("url", noteLink);
                startActivity(postIntent);
                break;
        }

    }

    /**
     * 获取考试进度
     */
    private void requestExamProgress() {
        RequestManager.request(this, new ExamRecGetParams(stu_type), ExamRecResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:// 刷新考试进度
                    ExamRecResponse.ExamRecInfo examRecInfo = (ExamRecResponse.ExamRecInfo) msg.obj;
                    if (examRecInfo != null) {
                        currentExamRecInfo = examRecInfo;
                        upDateExamProgress(examRecInfo);
                    } else {
                        ToastUtil.show("考试进度获取失败");
                        currentExamRecInfo = null;
                        setTheoryEnable(false, "未知");
                        setPracticeEnable(false, "未知");
                        setPostEnable(false, "未知");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void upDateExamProgress(ExamRecResponse.ExamRecInfo examRecInfo) {
        pullToRefreshScrollView.onRefreshComplete();

        String currentStatus = "";

        // 考试进度：0 理论考、1 实践考、2 寄作品、3 结束、4 终止
        if (TextUtils.equals("0", examRecInfo.getStage())) {
            //理论考
            setTheoryEnable(true, "开始");
            setPracticeEnable(false, "等待");
            setPostEnable(false, "等待");
            currentStatus = "当前状态: 理论考试(待考)";
        } else if (TextUtils.equals("1", examRecInfo.getStage())) {
            //实践考
            setTheoryEnable(false, "已考");
            setPracticeEnable(true, "开始");
            setPostEnable(false, "等待");
            currentStatus = "当前状态: 实践考试(待考)";
        } else if (TextUtils.equals("2", examRecInfo.getStage())) {
            //寄作品
            setTheoryEnable(false, "已考");
            //实践考有一次提交，stage就为2了，还有可能再考一次，所以还得放开
            setPracticeEnable(true, "开始");
            setPostEnable(true, "开始");
            currentStatus = "当前状态: 投寄作品(待投递)";
        } else if (TextUtils.equals("3", examRecInfo.getStage())) {
            //结束
            setTheoryEnable(false, "结束");
            setPracticeEnable(false, "结束");
            setPostEnable(false, "结束");
            currentStatus = "当前状态: 考试结束";
        } else if (TextUtils.equals("4", examRecInfo.getStage())) {
            //终止
            setTheoryEnable(false, "终止");
            setPracticeEnable(false, "终止");
            setPostEnable(false, "终止");
            currentStatus = "当前状态: 考试终止";
        }
        tv_status.setText(currentStatus);
        practice_info_url = examRecInfo.getPractice_info_url();
        noteLink = stu_type.equals("1") ? examRecInfo.getMajor_duty_url() : examRecInfo.getXsf_duty_url();

        //获取理论考试和实践考试已考次数和考试次数限制
//        practice_count = Integer.parseInt(examRecInfo.getPractice_count());
//        theory_count = Integer.parseInt(examRecInfo.getTheory_count());
        practice_count_limit = Integer.parseInt(examRecInfo.getPractice_count_limit());
        theory_count_limit = Integer.parseInt(examRecInfo.getTheory_count_limit());
        if (TextUtils.equals("2", examRecInfo.getExam_status())) {
            if (isNeedShowError) {
                isNeedShowError = false;
                SPUtils.instance().put(SPUtils.EXAMTIMEOUT, false);
                showErrorExamDialog("您的上次考试未按时交卷，系统认定您为放弃，消耗掉一次考试机会!");
            }
        }
        tv_time_and_time.setText("考试时间: " + strToYMD(examRecInfo.getStart_time()) + "~" + strToYMD(examRecInfo.getEnd_time()));
    }

    /**
     * 时间转换为字符串:yyyy年MM月dd日
     */
    public static String strToYMD(String time) {

        Date date;
        try {
            date = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
            String now = new SimpleDateFormat("yyyy年MM月dd日").format(date);
            return now;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通知用户上次考试存在异常
     *
     * @param text
     */
    private void showErrorExamDialog(String text) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_submitexam,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_tips);
        TextView confirm = (TextView) view.findViewById(R.id.tv_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView line = (TextView) view.findViewById(R.id.tv_line);
        tv.setText(text);
        cancel.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if (response == null) {
            return;
        }
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof ExamRecResponse) {
                ExamRecResponse examRecResponse = (ExamRecResponse) response;
                if (examRecResponse != null) {
                    ExamRecResponse.ExamRecInfo examRecInfo = examRecResponse.getRepBody();
                    mHandler.obtainMessage(0, examRecInfo).sendToTarget();
                }
            }
        } else {
            pullToRefreshScrollView.onRefreshComplete();
            ToastUtil.show(response.getResMsg());
        }
    }
}
