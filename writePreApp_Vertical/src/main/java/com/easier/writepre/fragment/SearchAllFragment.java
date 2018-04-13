package com.easier.writepre.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.RecommendAdapter;
import com.easier.writepre.adapter.SearchAdapter;
import com.easier.writepre.entity.RecommendInfo;
import com.easier.writepre.entity.SearchInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.RecommendParams;
import com.easier.writepre.param.SearchParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.RecommendResponse;
import com.easier.writepre.response.SearchResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.SearchFragmentActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 搜索全部
 *
 * @author zhoulu
 */
public class SearchAllFragment extends BaseFragment implements
        OnRefreshListener2<ListView> {
    private PullToRefreshListView listView;
    private ArrayList<RecommendInfo> recommendInfos = new ArrayList<>();
    private ArrayList<SearchInfo> searchAllInfos = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private RecommendAdapter recommendAdapter;
    private boolean isFirst = true;
    private boolean isNeedRequestFirstData = true;
    private int allStart, allCount = 10;
    private int recommendStart, recommendCount = 10;
    private View headView;
    private TextView tv_header_title;
    private SearchFragmentActivity searchFragmentActivity;

    @Override
    public void onClick(View v) {
    }

    @Override
    public int getContextView() {
        // TODO Auto-generated method stub
        return R.layout.fragment_search;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:// 请求完成
                    listView.onRefreshComplete();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }


    private void addHeader() {
        headView = View.inflate(getActivity(), R.layout.list_header_search, null);
        tv_header_title = (TextView) headView.findViewById(R.id.tv_header_title);
        listView.getRefreshableView().addHeaderView(headView);
        if (isFirst) {
            tv_header_title.setText("推荐关注");
        } else {
            tv_header_title.setText("全部");
        }
    }

    @Override
    protected void init() {
        searchFragmentActivity = (SearchFragmentActivity) getActivity();
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setMode(Mode.BOTH);
        addHeader();
        // 数据
        searchAdapter = new SearchAdapter(getActivity());
        searchAdapter.setData(searchAllInfos);
        recommendAdapter = new RecommendAdapter(getActivity());
        recommendAdapter.setData(recommendInfos);
        listView.setAdapter(searchAdapter);
        if (isFirst) {
            listView.setAdapter(recommendAdapter);
        } else {
            listView.setAdapter(searchAdapter);
        }
        listView.setOnRefreshListener(this);
        listView.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 2) {
                            return;
                        }
                        if (isFirst) {
                            searchFragmentActivity.inTopicDetail(((RecommendInfo) recommendAdapter.getItem(position - 2)).getPost_id());
                        } else {
                            searchFragmentActivity.inUserInfo(((SearchInfo) searchAdapter.getItem(position - 2)).get_id());
                        }
                    }
                });
        if (getUserVisibleHint()) {
            loadRefresh();
        }
        isNeedRequestFirstData = false;
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(searchFragmentActivity.getEt_search().getText().toString()) && !isFirst) {
            mHandler.sendEmptyMessage(1);
            ToastUtil.show("请输入搜索条件!");
            return;
        }
        loadRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (TextUtils.isEmpty(searchFragmentActivity.getEt_search().getText().toString()) && !isFirst) {
            mHandler.sendEmptyMessage(1);
            ToastUtil.show("请输入搜索条件!");
            return;
        }
        loadMore();
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        if (isFirst) {
            //推荐
            if (recommendInfos != null)
                recommendInfos.clear();
            if (recommendAdapter != null)
                recommendAdapter.clear();
            recommendStart = 0;
            requestRecommend();
        } else {
            if (searchAllInfos != null)
                searchAllInfos.clear();
            if (searchAdapter != null)
                searchAdapter.clear();
            allStart = 0;
            requestSearchData("全部", searchFragmentActivity.getLastKeyWord(), "", "", allStart, allCount);
        }
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        if (isFirst) {
            //推荐
            recommendStart = recommendInfos.size();
            requestRecommend();
        } else {
            allStart = searchAllInfos.size();
            requestSearchData("全部", searchFragmentActivity.getLastKeyWord(), "", "", allStart, allCount);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 根据用户输入的条件请求接口
     */
    private void requestSearchData(String tag, String searchStr, String isTeacher, String addr, int start, int count) {
        RequestManager.request(getActivity(), tag, new SearchParams(TextUtils.isEmpty(searchStr) ? "" : searchStr, isTeacher, addr, "" + start, "" + count),
                SearchResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 获取推荐用户
     */
    private void requestRecommend() {
        RequestManager.request(getActivity(), new RecommendParams("" + recommendStart, "" + recommendCount),
                RecommendResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 对外提供的关键字搜索方法
     *
     * @param searchKey
     */
    public void doSearchAction(String searchKey) {
        isFirst = false;
        if (searchAdapter != null) {
            searchAdapter.clear();
            searchAdapter.setKeyWord(searchKey);
        }
        tv_header_title.setText("全部");
        searchAllInfos.clear();
        allStart = 0;
        requestSearchData("全部", searchKey, "", "", allStart, allCount);
    }

    /**
     * 对外提供的清除方法
     */
    public void doSearchClear() {
        isFirst = false;
        if (searchAdapter != null) {
            searchAdapter.clear();
            searchAdapter.setKeyWord("");
        }
        tv_header_title.setText("全部");
        searchAllInfos.clear();
        allStart = 0;
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof SearchResponse) {
                SearchResponse searchResponseTemp = (SearchResponse) response;
                listView.setAdapter(searchAdapter);
                tv_header_title.setText("全部");
                int select = 0;
                int oldPosition = searchAdapter.getCount();
                if (searchResponseTemp.getRepBody().getList() != null) {
                    if (!searchResponseTemp.getRepBody().getList().isEmpty()) {
                        if (TextUtils.equals("全部", tag)) {
                            select = searchResponseTemp.getRepBody().getList().size();
                            searchAllInfos.addAll(searchResponseTemp.getRepBody().getList());
                        }
                    } else {
                        ToastUtil.show("暂无更多!");
                    }
                    searchAdapter.setData(searchAllInfos);
                } else {
                    ToastUtil.show("暂无更多!");
                }
                if(select!=0)
                {
                    if(searchAdapter.getCount()-select>0)
                    {
                        select=searchAdapter.getCount()-select;
                    }
                }
                if (oldPosition != 0) {
                    listView.getRefreshableView().setSelection(select);
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof RecommendResponse) {
                if (isFirst) {
                    RecommendResponse recommendResponseTemp = (RecommendResponse) response;
                    listView.setAdapter(recommendAdapter);
                    tv_header_title.setText("推荐关注");
                    int select=0;
                    int oldPosition = recommendAdapter.getCount();
                    if (recommendResponseTemp.getRepBody().getList() != null) {
                        if (!recommendResponseTemp.getRepBody().getList().isEmpty()) {
                            select = recommendResponseTemp.getRepBody().getList().size();
                            recommendInfos.addAll(recommendResponseTemp.getRepBody().getList());
                            recommendAdapter.setData(recommendInfos);
                        } else {
                            ToastUtil.show("暂无更多!");
                        }
                    } else {
                        ToastUtil.show("暂无更多!");
                    }
                    if(select!=0)
                    {
                        if(recommendAdapter.getCount()-select>0)
                        {
                            select=recommendAdapter.getCount()-select;
                        }
                    }
                    if (oldPosition != 0) {
                        listView.getRefreshableView().setSelection(select);
                    }
                }
            }
            mHandler.sendEmptyMessage(1);
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }
}
