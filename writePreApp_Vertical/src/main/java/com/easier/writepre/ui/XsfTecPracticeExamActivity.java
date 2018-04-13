package com.easier.writepre.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.entity.WorksItem;
import com.easier.writepre.fragment.XsfTecPracticeExamFragment;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CommitWorksParams;
import com.easier.writepre.param.XsfTecPracticeExamStartParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CommitWorksResponse;
import com.easier.writepre.response.XsfTecPracticeExamDetailResponse;
import com.easier.writepre.response.XsfTecPracticeExamStartResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.CountDownChronometer;
import com.easier.writepre.widget.NoPreloadViewPager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 承载实践考试试题fragment的Activity
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamActivity extends NoSwipeBackActivity implements CountDownChronometer.OnTimeCompleteListener {

    private TextView tv_time;// 倒计时
    private long mCountDownTime = 20 * 60;   //倒计时
    private CountDownChronometer chronometer;         // 计时器

    private TextView tv_submit;// 交卷

    private Button btn_pre;// 上一题
    private Button btn_next;// 下一题
    public NoPreloadViewPager answerViewPager;     // 题目滑动
    private List<XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailInfo> listData;
    private String stu_type;
    private String exam_id;

    //private ArrayList<String> ossPathList = new ArrayList<String>();    //Oss文件路径
    private Map<String, String> ossPathMap = new HashMap<String, String>();    //Oss文件路径(通过sortNum键获取对应的OSS路径值)
    private ArrayList<String> locPathList = new ArrayList<String>();    //本地图片路径
    private ArrayList<String> needUpLoadList = new ArrayList<String>();   //需要上传至Oss本地文件路径
    public static ArrayList<String> sortNumList = new ArrayList<String>();   //需要上传至Oss文件顺序
    private ArrayList<WorksItem> list = new ArrayList<WorksItem>();   // 三幅作品文件路径集合
    private XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailBody examDetailBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xsf_tec_practice_exam);
        initView();
        initData();
    }

    private void initView() {
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        chronometer = (CountDownChronometer) findViewById(R.id.chronometer);
        chronometer.setOnTimeCompleteListener(this);


        btn_pre = (Button) findViewById(R.id.btn_pre);
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre();
            }
        });
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        answerViewPager = (NoPreloadViewPager) findViewById(R.id.answerViewPager);
    }

    /**
     * 提交作品
     */
    private void submit() {
//        if (!locPathList.isEmpty()) {
//            locPathList.clear();
//        }
        locPathList.clear();
        needUpLoadList.clear();
        sortNumList.clear();
        Cursor cAllLoc = DBHelper.instance().query("select loc_path from works_table order by sort_num");
        while (cAllLoc.moveToNext()) {
            locPathList.add(cAllLoc.getString(0));
        }
        cAllLoc.close();
        LogUtils.e("locPathList-->" + locPathList.size());
        if (locPathList.size() == 9) {
            showConfirmDialog("完成本次考试，是否交卷？");
        } else {
            showConfirmDialog("您有题目尚未完成，是否确定提前交卷？");
        }
    }

    /**
     * 上传文件
     */
    public void upLoadFile() {
        Cursor cFailLoc = DBHelper.instance().query("select loc_path,sort_num from works_table where up_state != 200 order by sort_num");
        while (cFailLoc.moveToNext()) {          // 状态不为成功的本地文件集合(成功为200)
            needUpLoadList.add(cFailLoc.getString(0));
            sortNumList.add(cFailLoc.getString(1));
        }
        commitFilesOss(needUpLoadList, handler);
    }

    private void showConfirmDialog(String tip) {
        ConfirmHintDialog dialog = new ConfirmHintDialog(this,
                R.style.loading_dialog, tip,
                new ConfirmHintDialog.ConfirmHintDialogListener() {

                    @Override
                    public void OnClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_confirm:
                                if (locPathList.size() == 0) {
                                    commitRequst();
                                } else
                                    upLoadFile();
                                break;
                            case R.id.tv_cancel:
                                break;

                            default:
                                break;
                        }
                    }
                });
        dialog.show();
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
                    //ossPathList.clear();
                    Cursor cAllOss = DBHelper.instance().query("select sort_num,oss_path from works_table order by sort_num");
                    while (cAllOss.moveToNext()) {
                        // ossPathList.add(cAllOss.getString(0));
                        ossPathMap.put(cAllOss.getString(0), cAllOss.getString(1));
                    }
//                    if (((ArrayList<String>) msg.obj).size() != 0) {
//                        LogUtils.e("oss size ->" + ((ArrayList<String>) msg.obj).size());
//                        for (int i = 0; i < ((ArrayList<String>) msg.obj).size(); i++) {
//                            Log.e("oss path ->", ((ArrayList<String>) msg.obj).get(i) + "");
//                            ossPathList.add(((ArrayList<String>) msg.obj).get(i) + "");
//                        }
//                    }
//                    LogUtils.e("所有OSS路径--->" + ossPathList.size());
//                    if (ossPathList.size() == 9) {
//                        commitRequst();
//                    } else
//                        ToastUtil.show("作品上传异常、请重新提交");
                    commitRequst();
                    break;
            }
        }
    };

    /**
     * 提交作品
     */
    private void commitRequst() {
        WorksItem worksItem;
        for (int i = 0; i < listData.size(); i++) {
            if (i == 0) {
                worksItem = new WorksItem(listData.get(i).get_id(), ossPath(1), ossPath(2), ossPath(3));
            } else if (i == 1) {
                worksItem = new WorksItem(listData.get(i).get_id(), ossPath(4), ossPath(5), ossPath(6));
            } else {
                worksItem = new WorksItem(listData.get(i).get_id(), ossPath(7), ossPath(8), ossPath(9));
            }
            list.add(worksItem);
        }
        RequestManager.request(this, new CommitWorksParams(stu_type, exam_id, list), CommitWorksResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    /**
     * 获取oss路径
     *
     * @param num
     * @return
     */
    public String ossPath(int num) {
        return (ossPathMap.get(num) == null ? "" : ossPathMap.get(num));
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
     * 初始化数据
     */
    private void initData() {
        stu_type = getIntent().getStringExtra("stu_type");
        exam_id = getIntent().getStringExtra("exam_id");
        examDetailBody = (XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailBody) getIntent().getSerializableExtra("exam_detail_body");
        listData = new ArrayList<XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailInfo>();
        listData = examDetailBody.getList();
        showView();
        requestTime();
        //当前页面试卷请求接口屏蔽掉，改为温馨提示页面调，然后把对象传过来。
//        RequestManager.request(this, new XsfTecPracticeExamDetailParams(exam_id, stu_type), XsfTecPracticeExamDetailResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());

    }

    private void showView() {
        answerViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return XsfTecPracticeExamFragment.newInstance(stu_type,listData.get(position));
            }

            @Override
            public int getCount() {
                return listData.size();
            }
        });

        answerViewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    btn_pre.setVisibility(View.GONE);
                    btn_next.setVisibility(View.VISIBLE);
//                    tv_submit.setVisibility(View.GONE);
                } else if (position > 0 && position < listData.size() - 1) {
                    btn_pre.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.VISIBLE);
//                    tv_submit.setVisibility(View.GONE);
                } else if (position == listData.size() - 1) {
                    btn_pre.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.GONE);
//                    tv_submit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
//            if (response instanceof XsfTecPracticeExamDetailResponse) {
//                XsfTecPracticeExamDetailResponse.XsfTecPracticeExamDetailBody repBody = ((XsfTecPracticeExamDetailResponse) response).getRepBody();
//                listData = repBody.getList();
//                showView();
//                requestTime();
//            } else
            if (response instanceof XsfTecPracticeExamStartResponse) {
                XsfTecPracticeExamStartResponse.ExamBody examBody = ((XsfTecPracticeExamStartResponse) response).getRepBody();
                if (examBody != null) {
                    if (!TextUtils.isEmpty(examBody.getCurrent_time())) {
                        String currentTime, endTime;
                        currentTime = examBody.getCurrent_time();
                        endTime = examBody.getEnd_time();
                        long allTime = ((getTimeStr2Long(endTime) - getTimeStr2Long(currentTime))) / 1000;
                        LogUtils.e("alltime:" + allTime);
                        mCountDownTime = allTime;
                        chronometer.setTimeFormatHHmmss();
                        chronometer.initTime(mCountDownTime);
                        chronometer.start();
                    }
                }
            } else if (response instanceof CommitWorksResponse) {
                ToastUtil.show("作品提交成功");
                DBHelper.instance().delete("delete from works_table");
                Intent intent = new Intent(this, ExaminationProcedureActivity.class);
                startActivity(intent);
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    private void requestTime() {
        RequestManager.request(this, new XsfTecPracticeExamStartParams(stu_type, exam_id), XsfTecPracticeExamStartResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
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

    @Override
    public void onTimeComplete() {
        //倒计时结束监听
        ToastUtil.show("考试时间已到，系统自动为您交卷！");
        if (locPathList.size() == 0) {
            commitRequst();
        } else
            upLoadFile();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
