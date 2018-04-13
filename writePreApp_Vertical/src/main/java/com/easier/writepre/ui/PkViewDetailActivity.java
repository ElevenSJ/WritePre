package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkNewsAdapter;
import com.easier.writepre.adapter.PkStudentGridViewAdapter;
import com.easier.writepre.adapter.PkTeacherGridViewAdapter;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.PKInfoParams;
import com.easier.writepre.param.PkNewsParams;
import com.easier.writepre.param.PkWorksGoodedParams;
import com.easier.writepre.param.PkWorksQueryParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BannersResponse.BannersBody;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkInfoResponse;
import com.easier.writepre.response.PkNewsResponse;
import com.easier.writepre.response.PkWorksQueryStudentResponse;
import com.easier.writepre.response.PkWorksQueryTeacherResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.PathButtonLayout;
import com.easier.writepre.widget.ViewPageIndicator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 展赛
 *
 * @author kai.zhong
 */

public class PkViewDetailActivity extends NoSwipeBackActivity {

    private String pk_id, pk_type, role;

    private String startDate, endDate;

    private TextView tv_news_title1, tv_news_title2, tv_news_title3;

    private ImageView iv_stage_img;

    private LinearLayout rl_notice;

    private PathButtonLayout clayout;

    private SocialAdvertiseAdapter advAdapter;

    private ViewPageIndicator mBannerIndicator;

    private MyGridView teacherGV, studentGV;

    private PkTeacherGridViewAdapter teacherAdapter;

    private PkStudentGridViewAdapter studentAdapter;


    private LinearLayout ll_all_teacher, ll_all_student;

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    private List<PkWorksQueryTeacherResponse.ContentInfo> teacherList = new ArrayList<PkWorksQueryTeacherResponse.ContentInfo>();

    private List<PkContentInfo> studentList = new ArrayList<PkContentInfo>();

    public ChildViewPager mBannerViewPager; // 广告viewpage

    private String pkStatus = "0";

    private PullToRefreshScrollView pull_refresh_scrollview;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_pk_view);
        pk_id = getIntent().getStringExtra("pk_id");
        setTopTitle(getIntent().getStringExtra("pk_title"));
        pk_type = getIntent().getStringExtra("pk_type");
        startDate = getIntent().getStringExtra("pk_startDate");
        endDate = getIntent().getStringExtra("pk_endDate");
        initViews();
        loadBanner();
        loadData();
    }

    private void loadBanner() {
        RequestManager.request(this, "banner", new BannersParams("2"), BannersResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void initViews() {

        pull_refresh_scrollview = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        clayout = (PathButtonLayout) findViewById(R.id.path_button);

        teacherGV = (MyGridView) findViewById(R.id.gv_teacher);
        studentGV = (MyGridView) findViewById(R.id.gv_student);
        iv_stage_img = (ImageView) findViewById(R.id.iv_stage_img);
        tv_news_title1 = (TextView) findViewById(R.id.tv_news_title1);
        tv_news_title2 = (TextView) findViewById(R.id.tv_news_title2);
        tv_news_title3 = (TextView) findViewById(R.id.tv_news_title3);
        ll_all_teacher = (LinearLayout) findViewById(R.id.ll_all_teacher);
        ll_all_student = (LinearLayout) findViewById(R.id.ll_all_student);
        rl_notice = (LinearLayout) findViewById(R.id.rl_notice);
        mBannerViewPager = (ChildViewPager) findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(this);
        mBannerViewPager.setAdapter(advAdapter);
        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);

        clayout.init(new int[]{R.drawable.baoming, R.drawable.tongzhi, R.drawable.wode}, R.drawable.sign_off,
                PathButtonLayout.RIGHTBOTTOM, 180, 300);
        clayout.setButtonsOnClickListener(this);
        ll_all_student.setOnClickListener(this);
        ll_all_teacher.setOnClickListener(this);

        findViewById(R.id.rl_sc).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (clayout.isShow()) {
                    clayout.collapse();
                }
                return false;
            }
        });

        studentGV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(PkViewDetailActivity.this, PkTeacherStudentInfoActivity.class);
                intent.putExtra("pk_type", pk_type);
                intent.putExtra("works_id", studentList.get(arg2).get_id());
                startActivity(intent);
            }
        });

        teacherGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(PkViewDetailActivity.this, PkTeacherStudentInfoActivity.class);
                intent.putExtra("pk_type", pk_type);
                intent.putExtra("works_id", teacherList.get(arg2).get_id());
                startActivity(intent);
            }
        });

        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadPkData();
            }
        });
//        rl_notice.setOnClickListener(this);
        //目前先写死，后期还是通过接口下发
        String[] notices = getResources().getStringArray(R.array.pk_view_notice);
        String[] notice_urls = getResources().getStringArray(R.array.pk_view_notice_url);
        findViewById(R.id.rl_notice).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_new1).setVisibility(View.VISIBLE);
        tv_news_title1.setText(notices[0]);
        tv_news_title1.setTag(notice_urls[0]);
        findViewById(R.id.ll_new2).setVisibility(View.VISIBLE);
        tv_news_title2.setText(notices[1]);
        tv_news_title2.setTag(notice_urls[1]);
        findViewById(R.id.ll_new3).setVisibility(View.VISIBLE);
        tv_news_title3.setText(notices[2]);
        tv_news_title3.setTag(notice_urls[2]);
        tv_news_title1.setOnClickListener(this);
        tv_news_title2.setOnClickListener(this);
        tv_news_title3.setOnClickListener(this);
    }

    /**
     * 先请求大赛信息数据
     */
    private void loadData() {
        RequestManager.request(this, "info", new PKInfoParams(pk_id), PkInfoResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
//        RequestManager.request(this, "news", new PkNewsParams(pk_id, "9", "3"),
//                PkNewsResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        loadPkData();

    }

    private void loadPkData() {
        RequestManager.request(this, "teacher", new PkWorksQueryParams(pk_id, "9", pkStatus, "1", "", "", "3"),
                PkWorksQueryTeacherResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        RequestManager.request(this, "student", new PkWorksGoodedParams(pk_id, pkStatus, "1", "", "", 0, 3),
                PkWorksQueryStudentResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.rl_notice:
//                Intent intent = new Intent(this, PkNewsActivity.class);
//                intent.putExtra("pk_id", pk_id);
//                startActivity(intent);
//                break;
            case R.id.tv_news_title1:
                Intent intent1 = new Intent(this, WebViewActivity.class);
                intent1.putExtra("title", "大赛公告");
                intent1.putExtra("url", v.getTag().toString());
                startActivity(intent1);
                break;
            case R.id.tv_news_title2:
                Intent intent2 = new Intent(this, WebViewActivity.class);
                intent2.putExtra("title", "大赛公告");
                intent2.putExtra("url", v.getTag().toString());
                startActivity(intent2);
                break;
            case R.id.tv_news_title3:
                Intent intent3 = new Intent(this, WebViewActivity.class);
                intent3.putExtra("title", "大赛公告");
                intent3.putExtra("url", v.getTag().toString());
                startActivity(intent3);
                break;
            case 100:
                if (LoginUtil.checkLogin(this)) {
                    if (DateKit.dayBetweenFormat(startDate) <= 0) {
                        if (DateKit.dayBetweenFormat(endDate) < 0) {
                            ToastUtil.show("报名时间已截止");
                            return;
                        }
                    } else {
                        ToastUtil.show("未到报名时间");
                        return;
                    }
                    if (TextUtils.isEmpty(pk_type) || TextUtils.equals(pk_type, "xzp_pk")) {
                        Intent intent11 = new Intent(this, PkSignUpActivity.class);
                        intent11.putExtra("pk_id", pk_id);
                        intent11.putExtra("pk_type", pk_type);
                        startActivity(intent11); // 报名
                    } else if (TextUtils.equals(pk_type, "view_pk")) {
                        Intent intent12 = new Intent(this, PkViewSignUpActivity.class);
                        intent12.putExtra("pk_id", pk_id);
                        intent12.putExtra("pk_type", pk_type);
                        startActivity(intent12); // 报名
                    }
                }
                break;
            case 101:
                startActivity(new Intent(this, PushMessageActivity.class));// 通知
                break;
            case 102:
                if (LoginUtil.checkLogin(this)) {
                    Intent aIntent = new Intent(this, PkTeacherStudentListActivity.class);
                    aIntent.putExtra("pk_id", pk_id);
                    aIntent.putExtra("flag", 2);
                    aIntent.putExtra("role", "3");
                    aIntent.putExtra("pk_type", pk_type);
                    aIntent.putExtra("status", pkStatus);
                    startActivity(aIntent);
                }
                break;
            case R.id.ll_all_teacher:
                Intent tIntent = new Intent(this, PkTeacherStudentListActivity.class);
                tIntent.putExtra("pk_id", pk_id);
                tIntent.putExtra("pk_type", pk_type);
                tIntent.putExtra("role", "1");
                tIntent.putExtra("flag", 0);
                tIntent.putExtra("status", pkStatus);
                startActivity(tIntent);
                break;
            case R.id.ll_all_student:
                Intent sIntent = new Intent(this, PkTeacherStudentListActivity.class);
                sIntent.putExtra("pk_id", pk_id);
                sIntent.putExtra("pk_type", pk_type);
                sIntent.putExtra("role", "1");
                sIntent.putExtra("flag", 1);
                sIntent.putExtra("status", pkStatus);
                startActivity(sIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        if (clayout.isShow()) {
            clayout.collapse();
        }
        super.onPause();
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if (response == null) {
            return;
        }
        pull_refresh_scrollview.onRefreshComplete();
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkNewsResponse) {
//                PkNewsResponse mPkNewsResponse = (PkNewsResponse) response;
//                PkNewsResponse.PkNewsBody mPkNewsBody = mPkNewsResponse.getRepBody();
//                if (mPkNewsBody.getList()!=null&&mPkNewsBody.getList().size() != 0) {
//                    findViewById(R.id.rl_notice).setVisibility(View.VISIBLE);
//                }
//                if (mPkNewsBody.getList().size() >= 1){
//                    findViewById(R.id.ll_new1).setVisibility(View.VISIBLE);
//                    tv_news_title1.setText(mPkNewsBody.getList().get(0).getTitle());
//                }
//                if (mPkNewsBody.getList().size() >= 2){
//                    findViewById(R.id.ll_new2).setVisibility(View.VISIBLE);
//                    tv_news_title2.setText(mPkNewsBody.getList().get(1).getTitle());
//                }
//                if (mPkNewsBody.getList().size() >= 3){
//                    findViewById(R.id.ll_new3).setVisibility(View.VISIBLE);
//                    tv_news_title3.setText(mPkNewsBody.getList().get(2).getTitle());
//                }
            } else if (response instanceof PkInfoResponse) {
                PkInfoResponse pkInfoResult = (PkInfoResponse) response;
                if (pkInfoResult != null) {
                    final PkInfoResponse.Repbody body = pkInfoResult.getRepBody();
                    BitmapHelp.getBitmapUtils().display(iv_stage_img, StringUtil.getImgeUrl(body.getStage_img()),
                            new BitmapLoadCallBack<View>() {

                                @Override
                                public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
                                                            BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
                                    findViewById(R.id.rl_pk_stage).setVisibility(View.VISIBLE);
                                    iv_stage_img.setImageBitmap(arg2);
                                }

                                @Override
                                public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                                    findViewById(R.id.rl_pk_stage).setVisibility(View.GONE);
                                }
                            });

                    if (body.getStage().equals("1") || body.getStage().equals("2")) {
                        pkStatus = "0";
                    } else if (body.getStage().equals("3") || body.getStage().equals("4")) {
                        pkStatus = "1";
                    } else if (body.getStage().equals("5") || body.getStage().equals("6")) {
                        pkStatus = "2";
                    }
                }
            }
            if (response instanceof PkWorksQueryTeacherResponse) {
                PkWorksQueryTeacherResponse pkWGResult = (PkWorksQueryTeacherResponse) response;
                if (pkWGResult != null) {
                    PkWorksQueryTeacherResponse.Repbody body = pkWGResult.getRepBody();
                    if (body.getList() == null || body.getList().size() == 0) {
                        findViewById(R.id.gv_teacher).setVisibility(View.GONE);
                    } else {
                        teacherList.clear();
                        teacherList.addAll(body.getList());
                        if (teacherAdapter == null) {
                            teacherAdapter = new PkTeacherGridViewAdapter(this, teacherList);
                            teacherGV.setAdapter(teacherAdapter);
                        } else {
                            if (teacherAdapter != null) {
                                teacherAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                }
            } else if (response instanceof PkWorksQueryStudentResponse) {
                if (TextUtils.equals(tag, "student")) {
                    PkWorksQueryStudentResponse spkWGResult = (PkWorksQueryStudentResponse) response;
                    if (spkWGResult != null) {
                        PkWorksQueryStudentResponse.Repbody body = spkWGResult.getRepBody();
                        if (body.getList() == null || body.getList().size() == 0) {
                            findViewById(R.id.gv_student).setVisibility(View.GONE);
                        } else {
                            studentList.clear();
                            for (int i = 0; i < body.getList().size(); i++) {
                                studentList.add(body.getList().get(i));
                            }
                            if (studentAdapter == null) {
                                studentAdapter = new PkStudentGridViewAdapter(this, studentList);
                                studentGV.setAdapter(studentAdapter);
                            } else {
                                if (studentAdapter != null) {
                                    studentAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            } else if (response instanceof BannersResponse) {
                BannersResponse bannersResult = (BannersResponse) response;
                if (bannersResult != null) {
                    BannersBody body = bannersResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null) {
                            advs.clear();
                            advs.addAll(body.getList());
                            advAdapter.setData(advs);
                            mBannerIndicator.setViewPager(mBannerViewPager);
                            try {
                                mBannerViewPager.startPlay();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
//        else
        //屏蔽错误提示
//            ToastUtil.show(response.getResMsg());
    }
}