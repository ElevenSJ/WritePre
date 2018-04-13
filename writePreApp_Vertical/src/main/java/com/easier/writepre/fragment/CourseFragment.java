package com.easier.writepre.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CourseExpandAdapter;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.CourseList;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.CourseMainView;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.CourseChildCategoryParams;
import com.easier.writepre.param.CourseListParams;
import com.easier.writepre.param.MyCouresListParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BannersResponse.BannersBody;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseListResponse;
import com.easier.writepre.response.CourseListResponse.CourseListResponseBody;
import com.easier.writepre.response.CourseResponse;
import com.easier.writepre.response.CourseResponse.CourseResponseBody;
import com.easier.writepre.response.MyCourseListResponse;
import com.easier.writepre.response.MyCourseListResponse.MyCourseListBody;
import com.easier.writepre.ui.DiyCourseActivity;
import com.easier.writepre.ui.LoginActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.ViewPageIndicator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends BaseFragment {

    // 主页面listView
    private PullToRefreshExpandableListView listView;
    private CourseExpandAdapter courseAdapter;
    private View placeHolderView;

    // 空数据时view
    private RelativeLayout courseEmptyLayout;

    private String title;
    private String typeId;

    private Handler mHandler = new Handler();

    public static CourseFragment newInstance(String typeId,String title) {
        final CourseFragment f = new CourseFragment();
        final Bundle args = new Bundle();
        args.putString("typeId", typeId);
        args.putString("title", title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeId = getArguments().getString("typeId");
        title = getArguments().getString("title");
    }

    @Override
    public int getContextView() {
        return R.layout.course_fragment;
    }

    @Override
    protected void init() {
        findViewById(R.id.tv_login).setOnClickListener(this);

        // 空view
        courseEmptyLayout = (RelativeLayout) findViewById(R.id.empty_view);
        findViewById(R.id.tv_create_course).setOnClickListener(this);
        findViewById(R.id.play_video).setOnClickListener(this);

        // listView
        listView = (PullToRefreshExpandableListView) findViewById(R.id.listview);
        listView.setMode(Mode.PULL_FROM_START);

        listView.getRefreshableView().setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                // groupIndex = groupPosition;
                // loadingDlg.loading();
                if (typeId.equals("-100")) {

                } else {
                    CourseList courseList = (CourseList) courseAdapter.getGroup(groupPosition);
                    if (courseAdapter.getChildrenCount(groupPosition) == 0) {
                        RequestManager.request(getActivity(), new CourseChildCategoryParams(courseList.get_id(),courseList.getType()),
                                CourseResponse.class, CourseFragment.this, Constant.URL);
                    }
                }

            }
        });
        listView.setOnRefreshListener(new OnRefreshListener2() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        courseAdapter.clearMyData();
                        if (typeId.equals("-100")) {
                            getMyCourse("");
                        } else {
                            getCourse();
                        }
                    }
                }, 300);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        // if (typeId.equals("-100")) {
                        // getMyCourse(myLastId);
                        // }
                    }
                }, 300);
            }
        });

        placeHolderView = getActivity().getLayoutInflater().inflate(R.layout.list_head_banner, null);
        listView.getRefreshableView().addHeaderView(placeHolderView);

        courseAdapter = new CourseExpandAdapter(getActivity(), typeId,title);
        listView.getRefreshableView().setAdapter(courseAdapter);
        listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, false));
        mBannerViewPager = (ChildViewPager) placeHolderView.findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) placeHolderView.findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(getActivity());
        mBannerViewPager.setAdapter(advAdapter);

        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);
    }


    public void updateCourseEmptyView() {
        if (courseAdapter != null && courseAdapter.getGroupCount() != 0) {
            courseEmptyLayout.setVisibility(View.GONE);
            listView.setMode(Mode.PULL_FROM_START);
        } else {
            courseEmptyLayout.setVisibility(View.VISIBLE);
            listView.setMode(Mode.DISABLED);
        }
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (getUserVisibleHint()){
            if (typeId.equals("-100")) {
                if (courseAdapter.getChildData().size() == 0 || MainActivity.isMyCourseUpdate) {
                    getMyCourse("");
                    MainActivity.isMyCourseUpdate = false;
                }
            }else if(typeId.equals("-101")){

            }else{
                if (MainActivity.isCourseUpdate) {
                    courseAdapter.notifyDataSetChanged();
                    MainActivity.isCourseUpdate = false;
                }
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&isPrepared){
            if (typeId.equals("-100")) {
                if (courseAdapter.getChildData().size() == 0 || MainActivity.isMyCourseUpdate) {
                    getMyCourse("");
                    MainActivity.isMyCourseUpdate = false;
                }
            }else if(typeId.equals("-101")){
                updateCourseEmptyView();
            }else{
                if (MainActivity.isCourseUpdate) {
                    courseAdapter.notifyDataSetChanged();
                    MainActivity.isCourseUpdate = false;
                }
            }
        }
    }

    private void refreshData() {
        if (((MainActivity) getActivity()).getIndex() == MainActivity.TYPE_ONE) {
            if (CourseMainView.advs.size() == 0) {
                getAdvs();
            } else {
                if (advAdapter != null && advAdapter.getCount() == 0) {
                    setAdvs(CourseMainView.advs);
                }
            }
            if (typeId.equals("-100")) {
                // 我的课程
                if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                    if (courseAdapter != null) {
                        if (courseAdapter.getGroupCount() == 0) {
                            List<CourseList> list = new ArrayList<CourseList>();
                            CourseList courseType1 = new CourseList();
                            courseType1.set_id("0");
                            courseType1.setTitle("收藏课程");
                            list.add(courseType1);

                            CourseList courseType3 = new CourseList();
                            courseType3.set_id("vod");
                            courseType3.setTitle("视频课程");
                            list.add(courseType3);

                            CourseList courseType2 = new CourseList();
                            courseType2.set_id("1");
                            courseType2.setTitle("自选课程");
                            list.add(courseType2);
                            courseAdapter.setData(list);
                        }
                        if (courseAdapter.getChildData().size() == 0 || MainActivity.isMyCourseUpdate) {
                            getMyCourse("");
                            MainActivity.isMyCourseUpdate = false;
                        }
                    }
                }

            } else if (typeId.equals("-101")) {
                // 自选课程
                updateCourseEmptyView();
            } else {
                // 网络课程
                if (courseAdapter.getGroupCount() == 0) {
                    getCourse();
                } else {
                    if (MainActivity.isCourseUpdate) {
                        courseAdapter.notifyDataSetChanged();
                        MainActivity.isCourseUpdate = false;
                    }
                }
            }
        }
    }

    private void getMyCourse(String lastId) {
        RequestManager.request(getActivity(), new MyCouresListParams("50", lastId), MyCourseListResponse.class, this,
                Constant.URL);

    }

    private void getCourse() {
        RequestManager.request(getActivity(), new CourseListParams(typeId), CourseListResponse.class, this,
                Constant.URL);
    }

    /**
     * 获取广告宣传
     */
    private void getAdvs() {
        RequestManager.request(getActivity(), new BannersParams("3"), BannersResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_course:
                if (LoginUtil.checkLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), DiyCourseActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.play_video:
                Intent intent = new Intent(getActivity(), MediaActivity.class);
                intent.putExtra(MediaActivity.URL, Constant.DIY_COURSE_VIDEO_URL);
                startActivity(intent);
                break;
            case R.id.tv_login:
                if (((TextView) v).getText().toString().equals(getResources().getString(R.string.tv_no_login))) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    getMyCourse("");
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof BannersResponse) {
                BannersResponse bannersResult = (BannersResponse) response;
                if (bannersResult != null) {
                    BannersBody body = bannersResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null) {
                            CourseMainView.advs.clear();
                            CourseMainView.advs.addAll(body.getList());
                            setAdvs(CourseMainView.advs);
                        }
                    }
                }
            } else if (response instanceof CourseListResponse) {
                loadingDlg.dismissDialog();
                CourseListResponseBody data = ((CourseListResponse) response).getRepBody();
                courseAdapter.setData(data.getList());
                for (int i = 0; i < courseAdapter.getGroupCount(); i++) {
                    listView.getRefreshableView().expandGroup(i);
                }
            } else if (response instanceof CourseResponse) {
                CourseResponseBody courseData = ((CourseResponse) response).getRepBody();
                courseAdapter.setChildData(courseData.getList());
            } else if (response instanceof MyCourseListResponse) {
                loadingDlg.dismissDialog();
                MainActivity.myCourse.clear();
                MyCourseListBody myCourse = ((MyCourseListResponse) response).getRepBody();
                for (int i = 0; i < myCourse.getList().size(); i++) {
                    MainActivity.myCourse.put(myCourse.getList().get(i).getRef_lsn_id(),
                            myCourse.getList().get(i).get_id());
//                    if (i == myCourse.getList().size() - 1) {
//                        myLastId = myCourse.getList().get(i).get_id();
//                    }
                }
                courseAdapter.setMyChildData(myCourse.getList());
                for (int i = 0; i < courseAdapter.getGroupCount(); i++) {
                    listView.getRefreshableView().expandGroup(i);
                }
                if (MainActivity.myCourse.isEmpty()){
                    SPUtils.instance().put("hasMyCourse",false);
                }else{
                    SPUtils.instance().put("hasMyCourse",true);
                }
                // updateLoginEmptyView();
            }
        } else {
            loadingDlg.dismissDialog();
            ToastUtil.show(response.getResMsg());
            // if (response instanceof MyCourseListResponse) {
            // updateLoginEmptyView();
            // }
        }
    }

}
