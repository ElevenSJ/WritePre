package com.easier.writepre.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.VShowUserGridViewAdapter;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.CourseMainView;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.VShowUserParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BannersResponse.BannersBody;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.VShowUserResponse;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.BannerLinkActivity;
import com.easier.writepre.ui.CircleMsgListActivity;
import com.easier.writepre.ui.PkTeacherStudentInfoActivity;
import com.easier.writepre.ui.VShowUserSecondActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.ChildViewPager.OnSingleTouchListener;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.ViewPageIndicator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 微展
 *
 * @author zhaomaohan
 */

@SuppressLint("ClickableViewAccessibility")
public class VShowUserFragment extends BaseFragment {

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    private MyGridView teacherGV;
    private VShowUserGridViewAdapter teacherAdapter;
    private List<VShowUserResponse.UserInfo> penmanList = new ArrayList<VShowUserResponse.UserInfo>();

    private PullToRefreshScrollView pull_refresh_scrollview;

    @Override
    public int getContextView() {
        return R.layout.fragment_v_show;
    }

    @Override
    protected void init() {
        findViews();
//        if (getUserVisibleHint()) {
//            RequestManager.request(getActivity(), new BannersParams("2"), BannersResponse.class, this,
//                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
//            loadData();
//        }
        if (advs.isEmpty()) {
            RequestManager.request(getActivity(), new BannersParams("4"), BannersResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        }
        if (penmanList.isEmpty()) {
            loadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isPrepared){
            if (isVisibleToUser) {
//                if (advs.isEmpty()) {
//                    RequestManager.request(getActivity(), new BannersParams("4"), BannersResponse.class, this,
//                            SPUtils.instance().getSocialPropEntity().getApp_socail_server());
//                }
//                if (penmanList.isEmpty()) {
//                    loadData();
//                }
            } else {
                // 相当于Fragment的onPause
            }
        }
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (advs.isEmpty()) {
//            RequestManager.request(getActivity(), new BannersParams("4"), BannersResponse.class, this,
//                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
//        }
//        if (penmanList.isEmpty()) {
//            loadData();
//        }
//    }

    private void findViews() {
        pull_refresh_scrollview = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
        teacherGV = (MyGridView) findViewById(R.id.gv_teacher);

        // kanner = (Kanner) findViewById(R.id.kanner);
        mBannerViewPager = (ChildViewPager) findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(getActivity());
        mBannerViewPager.setAdapter(advAdapter);

        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);
        teacherGV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                //跳转到v展二级界面
                Intent intent = new Intent(getActivity(), VShowUserSecondActivity.class);
                intent.putExtra("_id", penmanList.get(arg2).get_id());
                intent.putExtra("photo_url", penmanList.get(arg2).getPhoto_url());
                intent.putExtra("real_name", penmanList.get(arg2).getReal_name());
                intent.putExtra("desc", penmanList.get(arg2).getDesc());
                startActivity(intent);
            }
        });

        pull_refresh_scrollview.getLoadingLayoutProxy(false, true).setPullLabel("加载更多...");
        pull_refresh_scrollview.getLoadingLayoutProxy(false, true).setReleaseLabel("加载中...");
        pull_refresh_scrollview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载数据");


        pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                clear();
                loadData();
                pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadOldData();
            }
        });
        teacherGV.setFocusable(false);
    }

    /**
     * 加载更多
     */
    private void loadOldData() {
        RequestManager.request(getActivity(), new VShowUserParams(penmanList.size() + "", "10"),
                VShowUserResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void toCircleList(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), CircleMsgListActivity.class);
        intent.putExtra("circle_id", id); // 圈子id
        startActivity(intent);
    }

    /**
     * 清除数据
     */
    private void clear() {
        if (penmanList != null && penmanList.size() > 0) {
            penmanList.clear();
        }

        if (teacherGV != null) {
            teacherGV = null;
        }
    }

    /**
     * 请求接口数据
     */
    private void loadData() {
//		RequestManager.request(getActivity(), new PKInfoParams(), PkInfoResponse.class, this,
//				SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        RequestManager.request(getActivity(), new VShowUserParams("0", "10"),
                VShowUserResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 教师赛区
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        pull_refresh_scrollview.onRefreshComplete(); // 停止刷新
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
                            advs.clear();
                            advs.addAll(body.getList());
                            setAdvs(advs);
                        }
                    }
                }
            } else if (response instanceof VShowUserResponse) {
                VShowUserResponse pkWGResult = (VShowUserResponse) response;
                if (pkWGResult != null) {
                    VShowUserResponse.Repbody body = pkWGResult.getRepBody();
                    if (body.getList() != null && body.getList().size() > 0) {
                        for (int i = 0; i < body.getList().size(); i++) {
                            penmanList.add(body.getList().get(i));
                        }
                    } else
                        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
                    if (teacherAdapter == null) {
                        teacherAdapter = new VShowUserGridViewAdapter(getActivity(), penmanList);
                        teacherGV.setAdapter(teacherAdapter);
                    } else {
                        if (teacherAdapter != null) {
                            teacherAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }
}
