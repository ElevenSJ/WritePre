package com.easier.writepre.mainview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ColleageMenuAdapter;
import com.easier.writepre.adapter.ColleageNewsAdapter;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.CollegeMenuInfo;
import com.easier.writepre.entity.TecSchoolNewsInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.TecSchoolNewsListParams;
import com.easier.writepre.param.TecXsfStuInfoGetParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TecSchoolNewsListResponse;
import com.easier.writepre.response.TecXsfStuInfoGetResponse;
import com.easier.writepre.rongyun.UnReadCountListener;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.CalligraphyJoinActivity;
import com.easier.writepre.ui.LittleCalligraphyTeacherEditCandidateInformationActivity;
import com.easier.writepre.ui.LittleCalligraphyTeacherInformationConfirmActivity;
import com.easier.writepre.ui.LittleCalligraphyTeacherIntroduceActivity;
import com.easier.writepre.ui.RegisterForExaminationActivity;
import com.easier.writepre.ui.TaskListActivity;
import com.easier.writepre.ui.WebViewActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.ViewPageIndicator;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 学院
 *
 * @author zhaomaohan
 */
public class CollegeMainView extends BaseView {

    private SocialAdvertiseAdapter advAdapter;

    private ViewPageIndicator mBannerIndicator;

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    // 广告viewpage
    public ChildViewPager mBannerViewPager;

    private MyGridView gv_menu;
    private List<CollegeMenuInfo> menuList;//菜单list

    private PullToRefreshListView listView;
    private List<TecSchoolNewsInfo> newsList;//书院风采list


    private Boolean oldLogined = false;

    public static String menu_text[] = new String[]{"小书法师", "书法专业人才", "大师在线课", "高研班", "合作机构", "合作院校"};
    public static int menu_image[] = new int[]{R.drawable.college_menu_little_master, R.drawable.college_menu_calligraphy_talents, R.drawable.college_menu_master_online, R.drawable.college_menu_high_research_class, R.drawable.college_menu_cooperative_institutions,
            R.drawable.college_menu_more};
    public static String news_title[] = new String[]{"小书法师", "书法专业人才", "大师在线课", "高研班", "合作机构", "合作院校"};
    public static String news_content[] = new String[]{"小书法师小书法师小书法师小书法师小书法师小书法师小书法师",
            "书法专业人才书法专业人才书法专业人才书法专业人才书法专业人才",
            "大师在线课大师在线课大师在线课大师在线课大师在线课大师在线课",
            "高研班高研班高研班高研班高研班高研班高研班",
            "合作机构合作机构合作机构合作机构合作机构合作机构",
            "合作院校合作院校合作院校合作院校合作院校合作院校合作院校"};
    public static int news_image[] = new int[]{R.drawable.comment2x, R.drawable.my_search, R.drawable.my_message, R.drawable.renzheng, R.drawable.my_download,
            R.drawable.my_feedback};

    private View headView;
    private ColleageNewsAdapter newsAdapter;

    private LinearLayout ll_little_master;//小书法师
    private LinearLayout ll_calligraphy_talents;//书法专业人才

    private TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody tecXsfStuInfoGetBody;
    private TecXsfStuInfoGetResponse tecXsfStuInfoGetResponse;
    private TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody tecXsfStuInfoGetBody1;
    private TecXsfStuInfoGetResponse tecXsfStuInfoGetResponse1;

    private TextView tv_little_master;
    private TextView tv_calligraphy_talents;
    private String stu_type; //0小书法师 1书法专业人才
//    private int xsfsSignedStatus = 0; // 0是都未报名状态 1是小书法师已报名，2是书法专业人才已报名
    private boolean isXsfsSigned = false; // 小书法师是否已报名：false是未报名状态 true是小书法师已报
    private boolean isSfzyrcSigned = false; // 书法专业人才是否已报名：false是未报名状态 true是书法专业人才已报
    private String exam_type ;// 0(报考停止) 1(允许报考)
    private String exam_info ;// 报名(6月1日至6月30日)

    public CollegeMainView(Context ctx) {
        super(ctx);
    }


    @Override
    public int getContextView() {
        return R.layout.main_view_college;
    }

    @Override
    public void initView() {


        listView = (PullToRefreshListView) findViewById(R.id.listView);

        addHeader();
        newsList = new ArrayList<TecSchoolNewsInfo>();
        newsAdapter = new ColleageNewsAdapter(mCtx, newsList);
        listView.setAdapter(newsAdapter);


        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                RequestCollegeData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        findViewById(R.id.bt_join).setOnClickListener(this);
    }

    private void RequestCollegeData() {
        RequestManager.request(mCtx, new TecSchoolNewsListParams(), TecSchoolNewsListResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());

    }

    private void RequestData() {
//        dlgLoad.loading();
        //小书法师
        RequestManager.request(mCtx, "0", new TecXsfStuInfoGetParams("0"), TecXsfStuInfoGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
        //书法专业人才
        RequestManager.request(mCtx, "1", new TecXsfStuInfoGetParams("1"), TecXsfStuInfoGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());

    }

    private void addHeader() {
        headView = View.inflate(mCtx, R.layout.list_header_college_main_view, null);
        listView.getRefreshableView().addHeaderView(headView);
        mBannerViewPager = (ChildViewPager) findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(mCtx);
        mBannerViewPager.setAdapter(advAdapter);

        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);

        gv_menu = (MyGridView) findViewById(R.id.gv_menu);
        ll_little_master = (LinearLayout) findViewById(R.id.ll_little_master);
        ll_calligraphy_talents = (LinearLayout) findViewById(R.id.ll_calligraphy_talents);
        tv_little_master = (TextView) findViewById(R.id.tv_little_master);
        tv_calligraphy_talents = (TextView) findViewById(R.id.tv_calligraphy_talents);

        ll_little_master.setOnClickListener(this);
        ll_calligraphy_talents.setOnClickListener(this);

        menuList = getMenuData();
        gv_menu.setAdapter(new ColleageMenuAdapter(mCtx, menuList));
        gv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (LoginUtil.checkLogin(mCtx)) {
                            RequestData();
                        }
                        break;
                    case 1:
//                        Intent intent =new Intent(mCtx,LittleCalligraphyTeacherEditCandidateInformationActivity.class);
//                        mCtx.startActivity(intent);
                        ToastUtil.show("敬请期待");
                        break;
                    case 2:
//                        Intent intent = new Intent(mCtx,LittleCalligraphyTeacherInformationConfirmActivity.class);
//                        mCtx.startActivity(intent);
                        ToastUtil.show("敬请期待");
                        break;
                    case 3:
                        ToastUtil.show("敬请期待");
                        break;
                    case 4:
                        ToastUtil.show("敬请期待");
                        break;
                    case 5:
                        ToastUtil.show("敬请期待");
                        break;
                }
            }
        });

        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mCtx, WebViewActivity.class);
                intent.putExtra("url", newsList.get(position - 2).getHtml_url());
                intent.putExtra("title", newsList.get(position - 2).getTitle());
                mCtx.startActivity(intent);
            }
        });
    }

    /**
     * 选择下一个跳转的页面
     */
    private void SelectNextPageToJump(TecXsfStuInfoGetResponse tecXsfStuInfoGetResponse, TecXsfStuInfoGetResponse.TecXsfStuInfoGetBody tecXsfStuInfoGetBody) {
        if (tecXsfStuInfoGetResponse != null) {
            tecXsfStuInfoGetBody = tecXsfStuInfoGetResponse
                    .getRepBody();

            if (TextUtils.equals(tecXsfStuInfoGetBody.getIs_xsf_teacher(), "1")) {//考试管理员
                Intent intent0 = new Intent(mCtx, TaskListActivity.class);
                intent0.putExtra("isTeacher", TextUtils.equals(tecXsfStuInfoGetBody.getIs_xsf_teacher(), "1"));
                mCtx.startActivity(intent0);
            } else {//考生
                if (TextUtils.isEmpty(tecXsfStuInfoGetBody.get_id())) {//未报名

                    Intent intent0 = new Intent(mCtx, LittleCalligraphyTeacherIntroduceActivity.class);
                    intent0.putExtra("url", tecXsfStuInfoGetBody.getInfo_url());
                    intent0.putExtra("id", tecXsfStuInfoGetBody.get_id());
//                    intent0.putExtra("open", DateKit.dayBetweenFormat("20170531") >= 0);
                    intent0.putExtra("open", tecXsfStuInfoGetBody.getExam_type().equals("1"));
                    intent0.putExtra("exam_info", tecXsfStuInfoGetBody.getExam_info());
//                    intent0.putExtra("xsfsSignedStatus", xsfsSignedStatus);
                    intent0.putExtra("isXsfsSigned", isXsfsSigned);
                    intent0.putExtra("isSfzyrcSigned", isSfzyrcSigned);
                    mCtx.startActivity(intent0);

                } else {//报名

                    if (tecXsfStuInfoGetBody.getStu_status().equals("to_verify")) {//报名待校验
                        Intent intent0 = new Intent(mCtx, LittleCalligraphyTeacherEditCandidateInformationActivity.class);
                        intent0.putExtra("stu_status", "to_verify");
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("stu_info",);
//                                intent0.putExtra("",)
                        mCtx.startActivity(intent0);
                    } else if (tecXsfStuInfoGetBody.getStu_status().equals("available")) {//可报考
                        Intent intentBind = new Intent(mCtx, LittleCalligraphyTeacherInformationConfirmActivity.class);
                        intentBind.putExtra("status", tecXsfStuInfoGetBody.getStu_status());
                        mCtx.startActivity(intentBind);
                    } else if (tecXsfStuInfoGetBody.getStu_status().equals("study")) {//报考学习中
                        Intent intent0 = new Intent(mCtx, RegisterForExaminationActivity.class);
                        intent0.putExtra("info_url", tecXsfStuInfoGetBody.getInfo_url());
                        mCtx.startActivity(intent0);
                    } else {
                        Intent intentBind = new Intent(mCtx, LittleCalligraphyTeacherInformationConfirmActivity.class);
                        mCtx.startActivity(intentBind);
                    }

                }
            }
        }
    }

    /**
     * 获取动态数组数据 可以由其他地方传来(json等)
     */
    private List<CollegeMenuInfo> getMenuData() {
        List<CollegeMenuInfo> list = new ArrayList<CollegeMenuInfo>();
        for (int i = 0; i < menu_text.length; i++) {
            CollegeMenuInfo collegeMenuInfo = new CollegeMenuInfo(menu_image[i], menu_text[i]);
            list.add(collegeMenuInfo);
        }
        return list;
    }

    @Override
    public void showView(Intent intent) {
        if (isShowView) {
            // 非首次显示
            LogUtils.e("学院非首次显示");
//            if (oldLogined != (Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
//                oldLogined = (Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false);
//                RequestData();
//            }
        } else {
            // 首次显示
            LogUtils.e("学院首次显示");
            RequestCollegeData();
//            oldLogined = (Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false);
//            if (oldLogined) {
//                RequestData();
//            }
        }
        if (advs.isEmpty()) {
            RequestManager.request(mCtx, new BannersParams("5"), BannersResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        }
        if ((Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            RequestData();
        } else {
            tv_little_master.setText("小书法师报名");
            tv_calligraphy_talents.setText("书法专业人才报名");
        }
        super.showView(intent);
    }


    @Override
    public void onResume() {
        if (!isShowView) {
            return;
        } else {
            if ((boolean) SPUtils.instance().get("payResult", false)) {
                SPUtils.instance().put("payResult", false);
                RequestData();
            } else {
//                if (oldLogined != (Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
//                    oldLogined = (Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false);
//                    RequestData();
//                }
                if ((Boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                    RequestData();
                } else {
                    tv_little_master.setText("小书法师报名");
                    tv_calligraphy_talents.setText("书法专业人才报名");
                }
            }

        }
    }


    @Override
    public void onStop() {
    }

    @Override
    public void destory() {
        super.destory();
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof TecXsfStuInfoGetResponse) {
                switch (tag) {
                    case "0":
                        tecXsfStuInfoGetResponse = (TecXsfStuInfoGetResponse) response;
                        LogUtils.e("111111response====" + tecXsfStuInfoGetResponse);
                        if (tecXsfStuInfoGetResponse != null) {
                            tecXsfStuInfoGetBody = tecXsfStuInfoGetResponse
                                    .getRepBody();
                            if (TextUtils.isEmpty(tecXsfStuInfoGetBody.get_id())) {
                                if ((!TextUtils.isEmpty(tecXsfStuInfoGetBody.getIs_xsf_teacher()) && tecXsfStuInfoGetBody.getIs_xsf_teacher().equals("1"))) {
                                    tv_little_master.setText("小书法师");
                                } else {
                                    tv_little_master.setText("小书法师报名");
                                }
                            } else {
                                if (tecXsfStuInfoGetBody.getStu_status().equals("study")) {
                                    tv_little_master.setText("小书法师");
                                    isXsfsSigned = true;
                                } else {
                                    tv_little_master.setText("小书法师报名");
                                    isXsfsSigned = false;
                                }
                            }
                        }
                        break;
                    case "1":
                        tecXsfStuInfoGetResponse1 = (TecXsfStuInfoGetResponse) response;
                        LogUtils.e("111111response====" + tecXsfStuInfoGetResponse1);
                        if (tecXsfStuInfoGetResponse1 != null) {
                            tecXsfStuInfoGetBody1 = tecXsfStuInfoGetResponse1
                                    .getRepBody();

                            if (TextUtils.isEmpty(tecXsfStuInfoGetBody1.get_id())) {
                                if ((!TextUtils.isEmpty(tecXsfStuInfoGetBody1.getIs_xsf_teacher()) && tecXsfStuInfoGetBody1.getIs_xsf_teacher().equals("1"))) {
                                    tv_calligraphy_talents.setText("书法专业人才");
                                } else {
                                    tv_calligraphy_talents.setText("书法专业人才报名");
                                }
                            } else {
                                if (tecXsfStuInfoGetBody1.getStu_status().equals("study")) {
                                    tv_calligraphy_talents.setText("书法专业人才");
                                    isSfzyrcSigned = true;
                                } else {
                                    tv_calligraphy_talents.setText("书法专业人才报名");
                                    isSfzyrcSigned = false;
                                }
                            }
                            break;
                        }


                }

            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
        listView.onRefreshComplete();
        dlgLoad.dismissDialog();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof BannersResponse) {
                BannersResponse bannersResult = (BannersResponse) response;
                if (bannersResult != null) {
                    BannersResponse.BannersBody body = bannersResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null) {
                            advs.clear();
                            advs.addAll(body.getList());
                            advAdapter.setData(advs);
                            mBannerIndicator.setViewPager(mBannerViewPager);
                            if (body.getList().size() == 1) {
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
            } else if (response instanceof TecSchoolNewsListResponse) {
                TecSchoolNewsListResponse schoolResult = (TecSchoolNewsListResponse) response;
                if (schoolResult != null) {
                    TecSchoolNewsListResponse.TecSchoolNewsBody body = schoolResult.getRepBody();
                    if (body.getList() == null || body.getList().isEmpty()) {
                        return;
                    }
                    if (newsList == null || newsList.size() == 0) {
                        LogUtils.e("没有学院数据");
                        for (int i = 0; i < body.getList().size(); i++) {
                            newsList.add(body.getList().get(i));
                        }
                        if (newsAdapter == null) {
                            newsAdapter = new ColleageNewsAdapter(mCtx, newsList);
                            listView.setAdapter(newsAdapter);
                        } else {
                            newsAdapter.notifyDataSetChanged();
                        }
                    } else if (newsList.get(0).get_id().equals(body.getList().get(0).get_id())) {
                        LogUtils.e("学院数据没有更新");
                    } else {
                        LogUtils.e("学院数据更新");
                        for (int i = 0; i < body.getList().size(); i++) {
                            newsList.add(body.getList().get(i));
                        }
                        if (newsAdapter == null) {
                            newsAdapter = new ColleageNewsAdapter(mCtx, newsList);
                            listView.setAdapter(newsAdapter);
                        } else {
                            newsAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_join:
                mCtx.startActivity(new Intent(mCtx, CalligraphyJoinActivity.class));
                break;
            case R.id.ll_little_master:
                if (LoginUtil.checkLogin(mCtx)) {
                    SPUtils.instance().put("stu_type", "0");
                    /*
                    如果书法专业人才已报名成功,
                        则点击小书法师按钮，无论小书法师已经进行到什么状态，
                        都强制跳转到小书法师介绍页面。
                    否则，正常根据当前的考生状态，判断下面该跳到那一个页面。
                     */
                    if(isSfzyrcSigned){
                        if (tecXsfStuInfoGetResponse != null) {
                            tecXsfStuInfoGetBody = tecXsfStuInfoGetResponse
                                    .getRepBody();
                            Intent intent0 = new Intent(mCtx, LittleCalligraphyTeacherIntroduceActivity.class);
                            intent0.putExtra("url", tecXsfStuInfoGetBody.getInfo_url());
                            intent0.putExtra("id", tecXsfStuInfoGetBody.get_id());
//                            intent0.putExtra("open", DateKit.dayBetweenFormat("20170531") >= 0);
                            intent0.putExtra("open", tecXsfStuInfoGetBody.getExam_type().equals("1"));
                            intent0.putExtra("exam_info", tecXsfStuInfoGetBody.getExam_info());
//                            intent0.putExtra("xsfsSignedStatus", xsfsSignedStatus);
                            intent0.putExtra("isXsfsSigned", isXsfsSigned);
                            intent0.putExtra("isSfzyrcSigned", isSfzyrcSigned);
                            mCtx.startActivity(intent0);
                        }
                    }else{
                        SelectNextPageToJump(tecXsfStuInfoGetResponse, tecXsfStuInfoGetBody);
                    }
                }
                break;
            case R.id.ll_calligraphy_talents:
                if (LoginUtil.checkLogin(mCtx)) {
                    SPUtils.instance().put("stu_type", "1");
                    /*
                    如果小书法师已报名成功,
                        则点击书法专业人才按钮，无论书法专业人才已经进行到什么状态，
                        都强制跳转到书法专业人才介绍页面。
                    否则，正常根据当前的考生状态，判断下面该跳到那一个页面。
                     */
                    if(isXsfsSigned){
                        if (tecXsfStuInfoGetResponse1 != null) {
                            tecXsfStuInfoGetBody1 = tecXsfStuInfoGetResponse1
                                    .getRepBody();
                            Intent intent0 = new Intent(mCtx, LittleCalligraphyTeacherIntroduceActivity.class);
                            intent0.putExtra("url", tecXsfStuInfoGetBody1.getInfo_url());
                            intent0.putExtra("id", tecXsfStuInfoGetBody1.get_id());
//                            intent0.putExtra("open", DateKit.dayBetweenFormat("20170531") >= 0);
                            intent0.putExtra("open", tecXsfStuInfoGetBody1.getExam_type().equals("1"));
                            intent0.putExtra("exam_info", tecXsfStuInfoGetBody1.getExam_info());
//                            intent0.putExtra("xsfsSignedStatus", xsfsSignedStatus);
                            intent0.putExtra("isXsfsSigned", isXsfsSigned);
                            intent0.putExtra("isSfzyrcSigned", isSfzyrcSigned);
                            mCtx.startActivity(intent0);
                        }
                    }else{
                        SelectNextPageToJump(tecXsfStuInfoGetResponse1, tecXsfStuInfoGetBody1);
                    }
                }
                break;
        }
    }

}
