package com.easier.writepre.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ImageAdapter;
import com.easier.writepre.adapter.ImageListViewAdapter;
import com.easier.writepre.adapter.PkInfoAdapter;
import com.easier.writepre.adapter.PkTeacherStudentListAdapter;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.CourseMainView;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.PKGetParams;
import com.easier.writepre.param.PKInfoParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkGetInfoResponse;
import com.easier.writepre.response.PkInfoResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.PkOnLineDetailActivity;
import com.easier.writepre.ui.PkViewDetailActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.ViewPageIndicator;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;

/**
 * 大赛（新）
 *
 * @author 孙杰
 */

public class PkNewFragment extends BaseFragment {


    private PullToRefreshListView listView;

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    private View holderView;     // 头部布局文件

    private PkInfoAdapter adapter;


    @Override
    public int getContextView() {
        return R.layout.fragment_new_pk;
    }

    @Override
    protected void init() {
        initViews();
        if (advs.isEmpty()) {
            RequestManager.request(getActivity(), new BannersParams("2"), BannersResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        }
        loadData();
    }

    private void initViews() {
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        holderView = getActivity().getLayoutInflater().inflate(R.layout.list_head_banner, null);
        AutoUtils.autoSize(holderView);
        listView.getRefreshableView().addHeaderView(holderView);
        mBannerViewPager = (ChildViewPager) holderView.findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) holderView.findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(getActivity());
        mBannerViewPager.setAdapter(advAdapter);
        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);

        adapter = new PkInfoAdapter(getActivity());
        listView.setAdapter(adapter);

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

        listView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    return;
                }
                Intent intent = new Intent();
                if (!TextUtils.isEmpty(adapter.getItem(position-2).getType())&&adapter.getItem(position-2).getType().equals("view_pk")){
                    intent.setClass(getActivity(), PkViewDetailActivity.class);
                }else{
                    intent.setClass(getActivity(), PkOnLineDetailActivity.class);
                }
                intent.putExtra("pk_id",adapter.getItem(position-2).get_id());
                intent.putExtra("pk_title",adapter.getItem(position-2).getTitle());
                intent.putExtra("pk_type",adapter.getItem(position-2).getType());
                intent.putExtra("pk_startDate",adapter.getItem(position-2).getStart_date());
                intent.putExtra("pk_endDate",adapter.getItem(position-2).getEnd_date());
                startActivity(intent);
            }
        });

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadData();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    /**
     * 先请求大赛信息数据
     */
    private void loadData() {
        RequestManager.request(getActivity(), new PKGetParams(), PkGetInfoResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse( response);
        listView.onRefreshComplete();
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
                            setAdvs(advs);
                        }
                    }
                }
            } else if (response instanceof PkGetInfoResponse) {
                PkGetInfoResponse PkResult = (PkGetInfoResponse) response;
                if (PkResult != null) {
                    PkGetInfoResponse.PkNewsBody body = PkResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null) {
                            adapter.setData(body.getList());
                        }
                    }
                }
            } else
                ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
