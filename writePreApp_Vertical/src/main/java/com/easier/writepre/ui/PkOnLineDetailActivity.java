package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkStudentGridViewAdapter;
import com.easier.writepre.adapter.PkTeacherGridViewAdapter;
import com.easier.writepre.adapter.PkTeacherStudentListAdapter;
import com.easier.writepre.adapter.PkVideoGridViewAdapter;
import com.easier.writepre.adapter.PkWorksVideoGridViewAdapter;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.PKInfoParams;
import com.easier.writepre.param.PkWorksQueryParams;
import com.easier.writepre.param.PkWorksRecommendParams;
import com.easier.writepre.param.PkWorksVideoParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BannersResponse.BannersBody;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkInfoResponse;
import com.easier.writepre.response.PkWorksQueryStudentResponse;
import com.easier.writepre.response.PkWorksQueryTeacherResponse;
import com.easier.writepre.response.PkWorksRecommendResponse;
import com.easier.writepre.response.YoukuVodgpListResponse;
import com.easier.writepre.response.YoukuVodgpListResponse.YoukuVodgpInfo;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.social.ui.youku.PlayerFullActivity;
import com.easier.writepre.social.ui.youku.VideoListActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.PathButtonLayout;
import com.easier.writepre.widget.ViewPageIndicator;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 大赛
 *
 * @author kai.zhong
 */

public class PkOnLineDetailActivity extends NoSwipeBackActivity {

    private String pk_id, pk_type, role;

    private String startDate, endDate;

    private TextView tv_news_title;

    private ImageView iv_stage_img;

    private RelativeLayout rl_notice;

    private PathButtonLayout clayout;

    private PullToRefreshListView listView;

    private SocialAdvertiseAdapter advAdapter;

    private ViewPageIndicator mBannerIndicator;

    private MyGridView teacherGV, studentGV, videoGV;

    private PkTeacherGridViewAdapter teacherAdapter;

    private PkStudentGridViewAdapter studentAdapter;

    private PkVideoGridViewAdapter videoAdapter;

    //private PullToRefreshScrollView pull_refresh_scrollview;

    private LinearLayout ll_all_teacher, ll_all_student, ll_all_video;

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    private List<YoukuVodgpInfo> videoList = new ArrayList<YoukuVodgpInfo>();

    private List<PkWorksQueryTeacherResponse.ContentInfo> teacherList = new ArrayList<PkWorksQueryTeacherResponse.ContentInfo>();

    private List<PkContentInfo> studentList = new ArrayList<PkContentInfo>();

    public ChildViewPager mBannerViewPager; // 广告viewpage

    private View holderView;     // 头部布局文件

    private ArrayList<ImageView[]> m_list_imag;

    private PkTeacherStudentListAdapter adapter;

    private ArrayList<PkContentInfo> recommendList;

    private List<PkContentInfo> worksVideoList = new ArrayList<PkContentInfo>();
    private PkWorksVideoGridViewAdapter worksvideoAdapter;
    private TextView tv_recommend_works;
    private TextView tv_teacher_works;
    private TextView tv_student_works;
    private ImageView img_student_works_status;
    private ImageView img_teacher_works_status;
    private TextView tv_student_works_status;
    private TextView tv_teacher_works_status;
    public final static int REQUEST_ZAN_UPDATE_CODE = 0x000001;

    public final static int REQUEST_UPDATE_CODE = 0x000002;

    public static boolean GIVE_OK; // true 刷新赞数+1

    public static boolean COMMENT; // true 刷新评论数+1

    public static int LIST_INDEX;

    private String pkStatus = "0";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_pk);
        initViews();
        pk_id = getIntent().getStringExtra("pk_id");
        setTopTitle(getIntent().getStringExtra("pk_title"));
        pk_type = getIntent().getStringExtra("pk_type");
        startDate = getIntent().getStringExtra("pk_startDate");
        endDate = getIntent().getStringExtra("pk_endDate");
        RequestManager.request(this, "banner", new BannersParams("2"), BannersResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        loadData();
    }

    private void initViews() {
        m_list_imag = new ArrayList<ImageView[]>();
        recommendList = new ArrayList<PkContentInfo>();
        clayout = (PathButtonLayout) findViewById(R.id.path_button);
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        holderView = this.getLayoutInflater().inflate(R.layout.fragment_pk_head, null);
        AutoUtils.autoSize(holderView);
        listView.getRefreshableView().addHeaderView(holderView);
        tv_recommend_works = (TextView) holderView.findViewById(R.id.tv_recommend_works);
        tv_teacher_works = (TextView) holderView.findViewById(R.id.tv_teacher_works);
        tv_student_works = (TextView) holderView.findViewById(R.id.tv_student_works);
        img_student_works_status = (ImageView) holderView.findViewById(R.id.img_student_works_status);
        img_teacher_works_status = (ImageView) holderView.findViewById(R.id.img_teacher_works_status);
        tv_student_works_status = (TextView) holderView.findViewById(R.id.tv_student_works_status);
        tv_teacher_works_status = (TextView) holderView.findViewById(R.id.tv_teacher_works_status);

        teacherGV = (MyGridView) holderView.findViewById(R.id.gv_teacher);
        studentGV = (MyGridView) holderView.findViewById(R.id.gv_student);
        videoGV = (MyGridView) holderView.findViewById(R.id.gv_video);
        iv_stage_img = (ImageView) holderView.findViewById(R.id.iv_stage_img);
        tv_news_title = (TextView) holderView.findViewById(R.id.tv_news_title);
        ll_all_teacher = (LinearLayout) holderView.findViewById(R.id.ll_all_teacher);
        ll_all_student = (LinearLayout) holderView.findViewById(R.id.ll_all_student);
        ll_all_video = (LinearLayout) holderView.findViewById(R.id.ll_all_video);
        rl_notice = (RelativeLayout) holderView.findViewById(R.id.rl_notice);
        mBannerViewPager = (ChildViewPager) holderView.findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) holderView.findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(this);
        mBannerViewPager.setAdapter(advAdapter);
        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);
        rl_notice.setOnClickListener(this);
        clayout.init(new int[]{R.drawable.baoming, R.drawable.tongzhi, R.drawable.wode}, R.drawable.sign_off,
                PathButtonLayout.RIGHTBOTTOM, 180, 300);
        clayout.setButtonsOnClickListener(this);
        ll_all_student.setOnClickListener(this);
        ll_all_teacher.setOnClickListener(this);
        ll_all_video.setOnClickListener(this);

        adapter = new PkTeacherStudentListAdapter(this, pk_id, pk_type, recommendList, m_list_imag,
                0);
        listView.getRefreshableView().setAdapter(adapter);

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
                Intent intent = new Intent(PkOnLineDetailActivity.this, PkTeacherStudentInfoActivity.class);
                intent.putExtra("works_id", studentList.get(arg2).get_id());
                intent.putExtra("pk_type", pk_type);
                startActivity(intent);
            }
        });

        teacherGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(PkOnLineDetailActivity.this, PkTeacherStudentInfoActivity.class);
                intent.putExtra("works_id", teacherList.get(arg2).get_id());
                intent.putExtra("pk_type", pk_type);
                startActivity(intent);
            }
        });

        videoGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (worksVideoList.get(arg2).getYk_video_id() != null) {
                    Intent i = new Intent(PkOnLineDetailActivity.this, PlayerFullActivity.class);
                    i.putExtra("vid", worksVideoList.get(arg2).getYk_video_id()); // XNzgyODExNDY4
                    startActivity(i);
                } else {
                    ToastUtil.show("该作品没有视频");
                }
            }
        });

        /**
         * 楼上被注掉的方法有个问题，当滑滑滑，第一次滑到顶部的时候，不能触发下拉刷新
         * 再滑一次才能
         * onTouch改成楼下的onScroll就好了。
         */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && view.getChildAt(0).getTop() >= view.getListPaddingTop()) {
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    listView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PkOnLineDetailActivity.this, PkMsgDetailActivity.class);
                intent.putExtra("pk_id", pk_id);
                intent.putExtra("pk_type", pk_type);
                intent.putExtra("data", recommendList);
                intent.putExtra("position", position - 2);
                intent.putExtra("status", recommendList.get(position - 2).getStatus());
                intent.putExtra("role", recommendList.get(position - 2).getRole());
                intent.putExtra("pk_cata_id", recommendList.get(position - 2).getPk_cata_id());
                intent.putExtra("city", recommendList.get(position - 2).getCity());
                intent.putExtra("flag", 0);
                LIST_INDEX = position - 2;
                GIVE_OK = false;
                COMMENT = false;
                startActivityForResult(intent, REQUEST_ZAN_UPDATE_CODE);
            }
        });

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                /**
                 *  clear()于2016年12月26日去掉，不再统一调用，
                 *  为了分别单独控制——当有无新数据时，刷新与否，
                 *  （PS：没有新数据就不要刷新了，用户体验不好）
                 *  所以都在各自接口的response里各自根据不同情况clear.
                 */
//                clear();
                loadData();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    /**
     * 清除数据
     */
    private void clear() {
        if (recommendList != null && recommendList.size() > 0) {
            recommendList.clear();
        }
        if (m_list_imag != null && m_list_imag.size() > 0) {
            m_list_imag.clear();
        }
        if (adapter != null) {
            adapter = null;
        }

        if (teacherList != null && teacherList.size() > 0) {
            teacherList.clear();
        }

        if (studentList != null && studentList.size() > 0) {
            studentList.clear();
        }

        if (videoList != null && videoList.size() > 0) {
            videoList.clear();
        }

        if (worksVideoList != null && worksVideoList.size() > 0) {
            worksVideoList.clear();
        }

        if (teacherGV != null) {
            teacherGV = null;
        }

        if (studentGV != null) {
            studentGV = null;
        }

        if (videoGV != null) {
            videoGV = null;
        }
    }

    /**
     * 先请求大赛信息数据
     */
    private void loadData() {
        RequestManager.request(this, "info", new PKInfoParams(pk_id), PkInfoResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());

    }

    /**
     * 再根据大赛当前赛程获取相应的作品数据
     */
    private void loadWorksData() {
        RequestManager.request(this, "teacher", new PkWorksQueryParams(pk_id, "9", pkStatus, "2", "", "", "3"),
                PkWorksQueryTeacherResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 教师赛区
        RequestManager.request(this, "student", new PkWorksQueryParams(pk_id, "9", pkStatus, "1", "", "", "3"),
                PkWorksQueryStudentResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 学生赛区
        RequestManager.request(this, "good", new PkWorksRecommendParams(pk_id),         // 模拟初赛优秀作品
                PkWorksRecommendResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        RequestManager.request(this, "video", new PkWorksVideoParams(pk_id),         // 初赛优秀作品
                PkWorksQueryStudentResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_notice:
                Intent intent = new Intent(this, PkNewsActivity.class);
                intent.putExtra("pk_id", pk_id);
                startActivity(intent);
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
                        Intent intent1 = new Intent(this, PkSignUpActivity.class);
                        intent1.putExtra("pk_id", pk_id);
                        intent1.putExtra("pk_type", pk_type);
                        startActivity(intent1); // 报名
                    } else if (TextUtils.equals(pk_type, "view_pk")) {
                        Intent intent2 = new Intent(this, PkViewSignUpActivity.class);
                        intent2.putExtra("pk_id", pk_id);
                        intent2.putExtra("pk_type", pk_type);
                        startActivity(intent2); // 报名
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
                    aIntent.putExtra("pk_type", pk_type);
                    aIntent.putExtra("role", "3");
                    aIntent.putExtra("status", pkStatus);
                    startActivity(aIntent);
                }
                break;
            case R.id.ll_all_teacher:
                Intent tIntent = new Intent(this, PkTeacherStudentListActivity.class);
                tIntent.putExtra("pk_id", pk_id);
                tIntent.putExtra("pk_type", pk_type);
                tIntent.putExtra("role", "2");
                tIntent.putExtra("status", pkStatus);
                startActivity(tIntent);
                break;
            case R.id.ll_all_student:
                Intent sIntent = new Intent(this, PkTeacherStudentListActivity.class);
                sIntent.putExtra("pk_id", pk_id);
                sIntent.putExtra("pk_type", pk_type);
                sIntent.putExtra("role", "1");
                sIntent.putExtra("status", pkStatus);
                startActivity(sIntent);
                break;
            case R.id.ll_all_video:
                // Intent i = new Intent(this, PlayerActivity.class);
                // i.putExtra("vid", "XNzgyODExNDY4"); // XNzgyODExNDY4
                // XOTI4ODEwMTYw
                Intent i = new Intent(this, VideoListActivity.class);
                startActivity(i);
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
        listView.onRefreshComplete();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkInfoResponse) {
                PkInfoResponse pkInfoResult = (PkInfoResponse) response;
                if (pkInfoResult != null) {
                    final PkInfoResponse.Repbody body = pkInfoResult.getRepBody();
                    tv_news_title.setText(body.getNews_title());
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
                        tv_teacher_works_status.setText("初赛作品");
                        tv_student_works_status.setText("初赛作品");
                        pkStatus = "0";
                    } else if (body.getStage().equals("3") || body.getStage().equals("4")) {
                        tv_teacher_works_status.setText("复赛作品");
                        tv_student_works_status.setText("复赛作品");
                        pkStatus = "1";
                    } else if (body.getStage().equals("5") || body.getStage().equals("6")) {
                        tv_teacher_works_status.setText("决赛作品");
                        tv_student_works_status.setText("决赛作品");
                        pkStatus = "2";
                    }
                    loadWorksData();
                }
            } else if (response instanceof PkWorksQueryTeacherResponse) {
                PkWorksQueryTeacherResponse pkWGResult = (PkWorksQueryTeacherResponse) response;
                if (pkWGResult != null) {
                    PkWorksQueryTeacherResponse.Repbody body = pkWGResult.getRepBody();
                    if (teacherList == null || teacherList.size() == 0) {
                        LogUtils.e("没有老师数据");
                        teacherList.addAll(body.getList());
                        if (teacherAdapter == null) {
                            teacherAdapter = new PkTeacherGridViewAdapter(this, teacherList);
                            teacherGV.setAdapter(teacherAdapter);
                        } else {
                            if (teacherAdapter != null) {
                                teacherAdapter.notifyDataSetChanged();
                            }
                        }
                    } else if (teacherList.get(0).get_id().equals(body.getList().get(0).get_id())) {
                        LogUtils.e("老师数据没有更新");
                    } else {
                        LogUtils.e("老师数据更新");
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
                        if (studentList == null || studentList.size() == 0) {
                            LogUtils.e("没有学生数据");
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
                        } else if (studentList.get(0).get_id().equals(body.getList().get(0).get_id())) {
                            LogUtils.e("学生数据没有更新");
                        } else {
                            LogUtils.e("学生数据更新");
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
                } else {
                    PkWorksQueryStudentResponse ykvResult = (PkWorksQueryStudentResponse) response;
                    if (ykvResult != null) {
                        PkWorksQueryStudentResponse.Repbody body = ykvResult.getRepBody();
                        if (worksVideoList == null || worksVideoList.size() == 0) {
                            LogUtils.e("没有视频数据");
                            for (int i = 0; i < body.getList().size(); i++) {
                                worksVideoList.add(body.getList().get(i));
                            }
                            if (worksvideoAdapter == null) {
                                worksvideoAdapter = new PkWorksVideoGridViewAdapter(this, worksVideoList);
                                videoGV.setAdapter(worksvideoAdapter);
                            } else {
                                worksvideoAdapter.notifyDataSetChanged();
                            }
                        } else if (worksVideoList.get(0).get_id().equals(body.getList().get(0).get_id())) {
                            LogUtils.e("视频数据没有更新");
                        } else {
                            LogUtils.e("视频数据更新");
                            worksVideoList.clear();
                            for (int i = 0; i < body.getList().size(); i++) {
                                worksVideoList.add(body.getList().get(i));
                            }
                            if (worksvideoAdapter == null) {
                                worksvideoAdapter = new PkWorksVideoGridViewAdapter(this, worksVideoList);
                                videoGV.setAdapter(worksvideoAdapter);
                            } else {
                                worksvideoAdapter.notifyDataSetChanged();
                            }
                        }


                    }
                }
            } else if (response instanceof PkWorksRecommendResponse) {
                //初赛推荐作品
                PkWorksRecommendResponse goodWorksResult = (PkWorksRecommendResponse) response;
                if (goodWorksResult != null) {
                    PkWorksRecommendResponse.Repbody body = goodWorksResult.getRepBody();
                    tv_recommend_works.setText(body.getTip_title());
                    if (recommendList == null || recommendList.size() == 0) {
                        LogUtils.e("没有推荐数据");
//                        body.getList().clear();
                        for (int i = 0; i < body.getList().size(); i++) {
                            recommendList.add(body.getList().get(i));
                            if (body.getList().get(i).getWorks().getImgs() == null
                                    || body.getList().get(i).getWorks().getImgs().length == 0) {
                                ImageView[] iv = new ImageView[7];
                                m_list_imag.add(iv);
                            } else {
                                ImageView[] iv = new ImageView[body.getList().get(i).getWorks().getImgs().length];
                                m_list_imag.add(iv);
                            }
                        }
                        if (adapter == null) {
                            adapter = new PkTeacherStudentListAdapter(this, pk_id, pk_type, recommendList, m_list_imag,
                                    0);
                            listView.getRefreshableView().setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else if (recommendList.get(0).get_id().equals(body.getList().get(0).get_id())) {
                        LogUtils.e("推荐数据没有更新");
                    } else {
                        LogUtils.e("推荐数据更新");
                        recommendList.clear();
                        if (m_list_imag != null && m_list_imag.size() > 0) {
                            m_list_imag.clear();
                        }
                        if (adapter != null) {
                            adapter = null;
                        }
                        for (int i = 0; i < body.getList().size(); i++) {
                            recommendList.add(body.getList().get(i));
                            if (body.getList().get(i).getWorks().getImgs() == null
                                    || body.getList().get(i).getWorks().getImgs().length == 0) {
                                ImageView[] iv = new ImageView[7];
                                m_list_imag.add(iv);
                            } else {
                                ImageView[] iv = new ImageView[body.getList().get(i).getWorks().getImgs().length];
                                m_list_imag.add(iv);
                            }
                        }
                        if (adapter == null) {
                            adapter = new PkTeacherStudentListAdapter(this, pk_id, pk_type, recommendList, m_list_imag,
                                    0);
                            listView.getRefreshableView().setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
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
            } else if (response instanceof YoukuVodgpListResponse) { // 视频
                YoukuVodgpListResponse ykvResult = (YoukuVodgpListResponse) response;
                if (ykvResult != null) {
                    YoukuVodgpListResponse.YoukuVodgpBody body = ykvResult.getRepBody();
                    for (int i = 0; i < body.getList().size(); i++) {
                        videoList.add(body.getList().get(i));
                    }
                    if (videoAdapter == null) {
                        videoAdapter = new PkVideoGridViewAdapter(this, videoList);
                        videoGV.setAdapter(videoAdapter);
                    } else {
                        if (videoAdapter != null) {
                            videoAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
//        else
        //屏蔽錯誤提示
//            ToastUtil.show(response.getResMsg());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ZAN_UPDATE_CODE:
                if (resultCode == this.RESULT_OK) {
                    if (recommendList != null) {
                        if (data != null) {
                            LIST_INDEX = data.getIntExtra("position", LIST_INDEX);
                            if (data.getSerializableExtra("data") != null) {
                                recommendList.clear();
                                recommendList.addAll((ArrayList<PkContentInfo>) data.getSerializableExtra("data"));
                            }
                        }
                        if (LIST_INDEX >= recommendList.size()) {
                            LIST_INDEX = recommendList.size() - 1;
                        }
                        if (GIVE_OK) {
                            recommendList.get(LIST_INDEX).setIs_ok("1");
                            recommendList.get(LIST_INDEX).setOk_num(Integer.parseInt(recommendList.get(LIST_INDEX).getOk_num()) + 1 + "");
                        }

                        if (COMMENT) {
                            recommendList.get(LIST_INDEX)
                                    .setRemark_num(Integer.parseInt(recommendList.get(LIST_INDEX).getRemark_num()) + 1 + "");
                        }

                        // if (GIVE_OK || COMMENT) {
                        adapter.notifyDataSetChanged();
                        // }
//                        listView.getRefreshableView().setSelection(LIST_INDEX);
                    }
                }
                break;
        }
    }
}
