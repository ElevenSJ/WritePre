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
import com.easier.writepre.adapter.SearchAdapter;
import com.easier.writepre.entity.SearchInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.SearchParams;
import com.easier.writepre.response.BaseResponse;
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
 * 搜索老师
 *
 * @author zhoulu
 */
public class SearchTeacherFragment extends BaseFragment implements
        OnRefreshListener2<ListView> {
    private PullToRefreshListView listView;
    private View headView;
    private TextView tv_header_title;
    private SearchAdapter searchAdapter;
    private int teacherStart, teacherCount = 10;
    private ArrayList<SearchInfo> searchTeacherInfos = new ArrayList<>();
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) { // false标识第一次加载 true说明已经加载数据
        }

    }

    private void addHeader() {
        headView = View.inflate(getActivity(), R.layout.list_header_search, null);
        tv_header_title = (TextView) headView.findViewById(R.id.tv_header_title);
        listView.getRefreshableView().addHeaderView(headView);
        tv_header_title.setText("老师");
    }

    @Override
    protected void init() {
        searchFragmentActivity = (SearchFragmentActivity) getActivity();
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setMode(Mode.BOTH);
        addHeader();
        searchAdapter = new SearchAdapter(getActivity());
        listView.setAdapter(searchAdapter);
        searchAdapter.setData(searchTeacherInfos);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 2) {
                            return;
                        }
                        searchFragmentActivity.inUserInfo(((SearchInfo) searchAdapter.getItem(position - 2)).get_id());
                    }
                });
        if (getUserVisibleHint()) {
            loadRefresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadMore();
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        if (searchTeacherInfos != null)
            searchTeacherInfos.clear();
        if (searchAdapter != null)
            searchAdapter.clear();
        teacherStart = 0;
        requestSearchData("老师", searchFragmentActivity.getLastKeyWord(), "1", "", teacherStart, teacherCount);
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        teacherStart = searchTeacherInfos.size();
        requestSearchData("老师", searchFragmentActivity.getLastKeyWord(), "1", "", teacherStart, teacherCount);
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
     * 对外提供的关键字搜索方法
     *
     * @param searchKey
     */
    public void doSearchAction(String searchKey) {
        if (TextUtils.isEmpty(searchKey) && !searchTeacherInfos.isEmpty()) {

        } else {
            //请求接口
            if (searchAdapter != null) {
                searchAdapter.clear();
                searchAdapter.setKeyWord(searchKey);
            }
            searchTeacherInfos.clear();
            teacherStart = 0;
            requestSearchData("老师", searchKey, "1", "", teacherStart, teacherCount);
        }
    }

    /**
     * 对外提供的清除方法
     */
    public void doSearchClear() {
        //请求接口
        if (searchAdapter != null) {
            searchAdapter.clear();
            searchAdapter.setKeyWord("");
        }
        searchTeacherInfos.clear();
        teacherStart = 0;
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof SearchResponse) {
                SearchResponse searchResponseTemp = (SearchResponse) response;
                listView.setAdapter(searchAdapter);
                int select=0;
                int oldPosition = searchAdapter.getCount();
                if (searchResponseTemp.getRepBody().getList() != null) {

                    if (!searchResponseTemp.getRepBody().getList().isEmpty()) {
                        if (TextUtils.equals("老师", tag)) {
                            select=searchResponseTemp.getRepBody().getList().size();
                            searchTeacherInfos.addAll(searchResponseTemp.getRepBody().getList());
                        }
                    } else {
                        ToastUtil.show("暂无更多!");
                    }
                    searchAdapter.setData(searchTeacherInfos);
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


}
