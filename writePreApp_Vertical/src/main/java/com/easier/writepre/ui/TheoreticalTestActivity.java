package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.TheoryAnswerAdapter;
import com.easier.writepre.db.DBExamManager;
import com.easier.writepre.entity.ExamTableEntity;
import com.easier.writepre.fragment.PracticeAnswerTheoryFragment;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.ExamFormalSubmitParams;
import com.easier.writepre.param.ExamTestSubmitParams;
import com.easier.writepre.param.ExamTheoreticalParams;
import com.easier.writepre.param.ExamTimeParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ExamResponse;
import com.easier.writepre.response.ExamSubmitResponse;
import com.easier.writepre.response.ExamTimeResponse;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.AnswerViewPager;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.CountDownChronometer;
import com.easier.writepre.widget.SlidingUpPanelLayout;
import com.easier.writepre.widget.WaveLoadingView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 理论测试
 * Created by zhoulu on 17/07/27.
 */
public class TheoreticalTestActivity extends NoSwipeBackActivity implements CountDownChronometer.OnTimeCompleteListener {

    private String pkg_id;
    private String localPath;//试卷路径
    private long mCountDownTime = 20 * 60;   //倒计时 秒

    private ImageView iv_time;

    private ImageView shadowView;

    private CountDownChronometer chronometer;         // 计时器

    private LinearLayout ll_time;

    public int prePosition, prePosition2;

    public int curPosition, curPosition2;

    private RecyclerView recyclerView;

    private TheoryAnswerAdapter AnswerAdapter;

    private SlidingUpPanelLayout mLayout;

    private AnswerViewPager answerViewPager;     // 题目滑动

    private List<ExamResponse.ExamInfo> listData;

    public ArrayList<String> okList = new ArrayList<String>();       //正确集合

    public ArrayList<String> errorList = new ArrayList<String>();    // 错误集合

    public ArrayList<String> haveTodoList = new ArrayList<String>();    // 已做集合

    private TextView tv_ok_question, tv_error_question, tv_now_question, tv_total_question;
    private boolean isFirst = true;
    private Handler handler;
    public static final int EXAM_TYPE_TEST = 1;
    public static final int EXAM_TYPE_FORMAL = 2;
    private int exam_type;
    private String type;

    public String getLocalPath() {
        return localPath;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_theory_question_answer);
        type = (String) SPUtils.instance().get("stu_type", "0");
        exam_type = getIntent().getIntExtra("exam_type", 0);
        pkg_id = getIntent().getStringExtra("pkg_id");
        handler = new Handler(this.getMainLooper());
        initView();
        setTopRightTxt("提交");
        checkSelect();// 从数据库表中检查已做过的题目，便于更新颜色状态。
        initSlidingUoPanel();
        initRecycList();
        if (exam_type == EXAM_TYPE_FORMAL) {
            mCountDownTime = getIntent().getLongExtra("currentTime", 0);
            localPath = getIntent().getStringExtra("localPath");
            initNativeData(localPath + "/exam.json");//加载本地数据
        } else if (exam_type == EXAM_TYPE_TEST) {
            initNetworkData();
        }
        chronometer.initTime(mCountDownTime);
        chronometer.start();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && isFirst) {
            isFirst = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBottomLayoutHight();
                }
            }, 500);
        }
    }

    private void checkSelect() {
        List<ExamTableEntity> examTableEntities = DBExamManager.queryAll();
        if (examTableEntities != null)
            for (ExamTableEntity tempExamTableEntity : examTableEntities) {
                haveTodoList.add(tempExamTableEntity.getSort_num() + "");
            }

    }

    /**
     * 获取SD缓存的json文本数据(转换成bean对象)
     *
     * @param jsonPath
     * @return
     */
    private List<ExamResponse.ExamInfo> getCacheJson(String jsonPath) {
        Type listType = new TypeToken<List<ExamResponse.ExamInfo>>() {
        }.getType();
        return new Gson().fromJson(FileUtils.getJson(jsonPath), listType);
    }

    /**
     * 请求本地试题
     */
    private void initNativeData(String path) {
        LogUtils.e("路径:" + path);
        listData = getCacheJson(path);
//        if (listData != null) {
//            for (int i = 0; i < listData.size(); i++) {
//                String num = listData.get(i).getTitle().substring(0, listData.get(i).getTitle().indexOf("、"));
//                LogUtils.e("题目序号==========>" + num);
//                listData.get(i).setSort_num(num);
//            }
//        }
        showView();
    }

    /**
     * 请求网络理论测试
     */
    private void initNetworkData() {
        listData = new ArrayList<ExamResponse.ExamInfo>();
        if (TextUtils.isEmpty(pkg_id)) {
            return;
        }
        RequestManager.request(this, new ExamTheoreticalParams(pkg_id),
                ExamResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    /**
     * 底部弹出快速选择框
     */
    private void initSlidingUoPanel() {
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        int height = getWindowManager().getDefaultDisplay().getHeight();

        LinearLayout dragView = (LinearLayout) findViewById(R.id.dragView);

        dragView.setLayoutParams(new SlidingUpPanelLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.8f)));   //(int) (height * 0.5f)  ViewGroup.LayoutParams.MATCH_PARENT

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED || newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
//                    if (AnswerAdapter != null) {
//                        AnswerAdapter.updateState();
//                    }
//                }
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }

    /**
     * 动态设置底部布局高度
     */
    private void setBottomLayoutHight() {
        LinearLayout dragContentTitle = (LinearLayout) findViewById(R.id.dragContentTitle);
        int dragContentTitleHight = dragContentTitle.getMeasuredHeight();
        LogUtils.e("dragContentTitleHight=" + dragContentTitleHight);
        mLayout.setPanelHeight(dragContentTitleHight);
        findViewById(R.id.fl_layout).setPadding(0, 0, 0, dragContentTitleHight);
    }

    /**
     * 题目框填充
     */
    private void initRecycList() {
        recyclerView = (RecyclerView) findViewById(R.id.recy_list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);

        AnswerAdapter = new TheoryAnswerAdapter(this);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(AnswerAdapter);

        AnswerAdapter.setOnTopicClickListener(new TheoryAnswerAdapter.OnTopicClickListener() {
            @Override
            public void onClick(TheoryAnswerAdapter.AnswerViewHolder holder, int position) {
                curPosition = position;
                if (mLayout != null &&
                        (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                answerViewPager.setCurrentItem(position);
                AnswerAdapter.notifyCurPosition(curPosition);
                AnswerAdapter.notifyPrePosition(prePosition);
                prePosition = curPosition;

            }
        });
    }

    private void initView() {
        setTopTitle(getIntent().getStringExtra("title"));
        chronometer = (CountDownChronometer) findViewById(R.id.chronometer);
        chronometer.setOnTimeCompleteListener(this);
        shadowView = (ImageView) findViewById(R.id.shadowView);
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        iv_time = (ImageView) findViewById(R.id.iv_time);
        tv_ok_question = (TextView) findViewById(R.id.tv_ok_question);
        tv_error_question = (TextView) findViewById(R.id.tv_error_question);
        tv_now_question = (TextView) findViewById(R.id.tv_now_question);
        tv_total_question = (TextView) findViewById(R.id.tv_total_question);
        answerViewPager = (AnswerViewPager) findViewById(R.id.answerViewPager);
        iv_time.setOnClickListener(this);
        findViewById(R.id.top_right_txt).setOnClickListener(this);
    }

    private void showView() {
        if (AnswerAdapter != null) {
            AnswerAdapter.setDataNum(listData.size());
        }
        tv_now_question.setText((prePosition2 + 1) + "");// "P"
        tv_total_question.setText("/" + listData.size() + "");// "P"
        answerViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return PracticeAnswerTheoryFragment.newInstance(listData.get(position));
            }

            @Override
            public int getCount() {
                return listData.size();
            }
        });

        answerViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                shadowView.setTranslationX(answerViewPager.getWidth() - positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                curPosition2 = position;
                AnswerAdapter.notifyCurPosition(curPosition2);
                AnswerAdapter.notifyPrePosition(prePosition2);
                prePosition2 = curPosition2;
                tv_now_question.setText((prePosition2 + 1) + "");      // "P"
                tv_total_question.setText("/" + listData.size() + "");// "P"
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 上一题
     */
    public void pre() {
        int currentItem = answerViewPager.getCurrentItem();
        currentItem = currentItem - 1;
        if (currentItem > listData.size() - 1) {
            currentItem = listData.size() - 1;
        }
        answerViewPager.setCurrentItem(currentItem, true);
    }

    /**
     * 下一题
     */
    public void next() {
        int currentItem = answerViewPager.getCurrentItem();
        currentItem = currentItem + 1;
        if (currentItem < 0) {
            currentItem = 0;
        }
        answerViewPager.setCurrentItem(currentItem, true);
    }

    /**
     * 暂停1s
     */
    public void stopOnes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);   //1000
                } catch (InterruptedException e) {
                } finally {
                    TheoreticalTestActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            next();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 记录答题对错情况
     */
    public void changeText() {
        tv_ok_question.setText(okList.size() + "");
        tv_error_question.setText(errorList.size() + "");
    }

    StringBuilder strLast;
    String strTemp;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_time:
                if (ll_time.getVisibility() == View.VISIBLE) {
                    ll_time.setVisibility(View.INVISIBLE);
                } else {
                    ll_time.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.top_right_txt:
                strLast = new StringBuilder();
                for (ExamResponse.ExamInfo examInfo : listData) {
                    strTemp = TextUtils.isEmpty(examInfo.getSelect()) ? "Z" : examInfo.getSelect();
                    strLast.append(strTemp);
                }
                LogUtils.e("考题选择项:" + strLast.toString());
                if (strLast.toString().contains("Z")) {
                    showSubmitDialog(2);
                } else {
                    showSubmitDialog(1);
                }
                break;
        }
    }

    @Override
    public void onTopLeftClick(View view) {
        if (exam_type == EXAM_TYPE_FORMAL) {
            showSubmitDialog(4);
        } else {
            showSubmitDialog(3);
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if (exam_type == EXAM_TYPE_FORMAL) {
                showSubmitDialog(4);
            } else {
                showSubmitDialog(3);
            }
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof ExamResponse) {
                ExamResponse exResponse = (ExamResponse) response;
                ExamResponse.ExamBody body = exResponse.getRepBody();
                listData = body.getList();
                if (listData != null && listData.size() > 0) {
//                    for (int i = 0; i < listData.size(); i++) {
//                        String num = listData.get(i).getTitle().substring(0, listData.get(i).getTitle().indexOf("、"));
//                        LogUtils.e("题目序号==========>" + num);
//                        listData.get(i).setSort_num(num);
//                    }
                    showView();
                } else {
                    ToastUtil.show("测试题加载失败");
                }
            } else if (response instanceof ExamSubmitResponse) {
                ExamSubmitResponse examSubmitResponse = (ExamSubmitResponse) response;
                ExamSubmitResponse.ExamSubmitResponseInfo examSubmitResponseInfo = examSubmitResponse.getRepBody();
                if (examSubmitResponseInfo != null) {
                    Intent intent = new Intent(TheoreticalTestActivity.this, ExamResultActivity.class);
                    intent.putExtra("title", "理论成绩");
                    intent.putExtra("result", examSubmitResponseInfo);
                    TheoreticalTestActivity.this.startActivity(intent);
                    DBExamManager.delAll();       // 清空数据表
                    TheoreticalTestActivity.this.finish();
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 将日期字符串转成long类型
     *
     * @param strTime 日期字符串
     * @return
     */
    private long getTimeStr2Long(String strTime) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(strTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTimeInMillis();
    }

    /**
     * 提交考试成绩弹窗
     *
     * @param type 0 时间到自动交卷 1 已完成全部答题交卷 2未完成全部答题交卷 3放弃本次测试 4退出自动交卷
     */
    private void showSubmitDialog(final int type) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_submitexam,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_tips);
        TextView confirm = (TextView) view.findViewById(R.id.tv_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView line = (TextView) view.findViewById(R.id.tv_line);
        switch (type) {
            case 0://时间到自动交卷
                tv.setText("本次测试时间已到,系统自动为您交卷!");
                cancel.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                break;
            case 1://已完成全部答题交卷
                tv.setText("本次测试已完成,是否交卷?");
                cancel.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                break;
            case 2://未完成全部答题交卷
                tv.setText("您未完成全部题目,是否交卷?");
                cancel.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                break;
            case 3://放弃本次测试
                tv.setText("现在退出将放弃本次测试?");
                cancel.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                break;
            case 4://退出自动交卷
                tv.setText("现在退出,系统将自动为您交卷?");
                cancel.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                break;
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (type) {
                    case 0://时间到自动交卷
                    case 1://已完成全部答题交卷
                    case 2://未完成全部答题交卷
                        if (exam_type == EXAM_TYPE_FORMAL) {
                            requestSubmitFormalExam();
                        } else if (exam_type == EXAM_TYPE_TEST) {
                            requestSubmitTestExam();
                        }

                        break;
                    case 3://放弃本次测试
                        DBExamManager.delAll();       // 清空数据表
                        TheoreticalTestActivity.this.finish();
                        overridePendingTransition(0, R.anim.slide_right_out);
                        break;
                    case 4:
                        strLast = new StringBuilder();
                        for (ExamResponse.ExamInfo examInfo : listData) {
                            strTemp = TextUtils.isEmpty(examInfo.getSelect()) ? "Z" : examInfo.getSelect();
                            strLast.append(strTemp);
                        }
                        LogUtils.e("考题选择项:" + strLast.toString());
                        requestSubmitFormalExam();
                        break;
                }
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

    /**
     * 提交理论测试试卷
     */
    private void requestSubmitTestExam() {
        dlgLoad.loading("正在提交试卷...");
        RequestManager.request(this, new ExamTestSubmitParams(pkg_id, strLast.toString()),
                ExamSubmitResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    /**
     * 提交理论考试试卷
     */
    private void requestSubmitFormalExam() {
        dlgLoad.loading("正在提交试卷...");
        RequestManager.request(this, new ExamFormalSubmitParams(type, pkg_id, strLast.toString()),
                ExamSubmitResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onTimeComplete() {
        //倒计时结束监听
        ToastUtil.show("时间到!");
        strLast = new StringBuilder();
        for (ExamResponse.ExamInfo examInfo : listData) {
            strTemp = TextUtils.isEmpty(examInfo.getSelect()) ? "Z" : examInfo.getSelect();
            strLast.append(strTemp);
        }
        LogUtils.e("考题选择项:" + strLast.toString());
        showSubmitDialog(0);
    }
}

