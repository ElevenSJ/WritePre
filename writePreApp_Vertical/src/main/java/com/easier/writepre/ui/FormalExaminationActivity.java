package com.easier.writepre.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.AnswerAdapter;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.fragment.PracticeAnswerFragment;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.ExamParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ExamResponse;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.AnswerViewPager;
import com.easier.writepre.widget.SlidingUpPanelLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 正式考试
 * Created by zhoulu on 17/07/19.
 */
public class FormalExaminationActivity extends NoSwipeBackActivity {

    public int from;   //  0表示练习题   其它值表示模拟或者正式考试

    private String pkg_id;

    private long mRecordTime;   //练习考试计时器

    private ImageView iv_time;

    private ImageView shadowView;

    private Chronometer chronometer;         // 计时器

    private LinearLayout ll_time;

    public int prePosition, prePosition2;

    public int curPosition, curPosition2;

    private RecyclerView recyclerView;

    private AnswerAdapter AnswerAdapter;

    private SlidingUpPanelLayout mLayout;

    private AnswerViewPager answerViewPager;     // 题目滑动

    private List<ExamResponse.ExamInfo> listData;

    public ArrayList<String> okList = new ArrayList<String>();       //正确集合

    public ArrayList<String> errorList = new ArrayList<String>();    // 错误集合

    public ArrayList<String> haveTodoList = new ArrayList<String>();    // 已做集合

    private TextView tv_ok_question, tv_error_question, tv_now_question, tv_total_question;
    private boolean isFirst = true;
    private Handler handler;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_question_answer);
        handler = new Handler(this.getMainLooper());
        from = getIntent().getIntExtra("FROM", 0);
        initView();
        if (from == 0) {       // 练习考试
            initNetworkData();
            initSlidingUoPanel();
            initRecycList();
        } else {          // 模拟和正式考试
            setTopRightTxt("提交");
            checkSelect();          // 从数据库表中检查已做过的题目，便于更新颜色状态。
            initSlidingUoPanel();
            initRecycList();
            initNativeData();
        }
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
        Cursor cursor = DBHelper.instance().query("select sort_num from exam order by sort_num");
        while (cursor.moveToNext()) {
            haveTodoList.add(cursor.getString(0));
        }
    }

    /**
     * 获取SD缓存的json文本数据(转换成bean对象)
     *
     * @param jsonPath
     * @return
     */
    private ExamResponse.ExamBody getCacheJson(String jsonPath) {
        return new Gson().fromJson(FileUtils.getJson(jsonPath), ExamResponse.ExamBody.class);
    }

    /**
     * 请求本地试题
     */
    private void initNativeData() {
        ExamResponse.ExamBody examBody = getCacheJson(FileUtils.SD_APP_PATH + "/exam.json");
        listData = examBody.getList();
        showView();
    }

    /**
     * 请求网络试题
     */
    private void initNetworkData() {
        listData = new ArrayList<ExamResponse.ExamInfo>();
        pkg_id = getIntent().getStringExtra("pkg_id");
        if (TextUtils.isEmpty(pkg_id)) {
            return;
        }
        RequestManager.request(this, new ExamParams(pkg_id),
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
        findViewById(R.id.fl_layout).setPadding(0,0,0, dragContentTitleHight);
    }

    /**
     * 题目框填充
     */
    private void initRecycList() {
        recyclerView = (RecyclerView) findViewById(R.id.recy_list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);

        AnswerAdapter = new AnswerAdapter(this);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(AnswerAdapter);

        AnswerAdapter.setOnTopicClickListener(new AnswerAdapter.OnTopicClickListener() {
            @Override
            public void onClick(AnswerAdapter.AnswerViewHolder holder, int position) {
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
        chronometer = (Chronometer) findViewById(R.id.chronometer);
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
                ExamResponse.ExamInfo examBean = listData.get(position);
                return PracticeAnswerFragment.newInstance(examBean);
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
                    FormalExaminationActivity.this.runOnUiThread(new Runnable() {
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
                    if (strTemp != null) {
                        if (strLast.length() > 0) {
                            strLast.append(",").append(strTemp);
                        } else {
                            strLast.append(strTemp);
                        }
                    }
                }
                DBHelper.instance().delete("delete from exam");        // 提交后清空数据表
                if (strLast.toString().contains("Z")) {
                    ToastUtil.show("您有遗漏的题目未作答，是否依然提交?");
                }
                LogUtils.e("++++++++" + strLast);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof ExamResponse) {
                ExamResponse exResponse = (ExamResponse) response;
                ExamResponse.ExamBody body = exResponse.getRepBody();
                for (int i = 0; i < body.getList().size(); i++) {
                    ExamResponse.ExamInfo examInfo = body.getList().get(i);
                    // examInfo.setIndex((i + 1) + "");
                    listData.add(examInfo);
                }
                if (listData.size() > 0) {
                    showView();
                }
            }
        } else {
            ToastUtil.show(response.getResCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecordTime != 0) {
            chronometer.setBase(chronometer.getBase()
                    + (SystemClock.elapsedRealtime() - mRecordTime));
        } else {
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
        chronometer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chronometer.stop();
        mRecordTime = SystemClock.elapsedRealtime();
    }
}

