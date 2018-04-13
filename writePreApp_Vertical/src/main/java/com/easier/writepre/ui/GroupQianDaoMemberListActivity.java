package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.GroupNoticeLookedMemberListAdapter;
import com.easier.writepre.entity.GroupNoticeLookedMemberInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.GroupNoticePostMemberParams;
import com.easier.writepre.param.GroupNoticePostNewParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GroupNoticePostMemberResponse;
import com.easier.writepre.response.GroupNoticePostNewResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 签到成员列表
 */
public class GroupQianDaoMemberListActivity extends BaseActivity {

    // 成员页面listView
    private PullToRefreshListView listView;
    private GroupNoticeLookedMemberListAdapter adapter;
    private String lastId = "9";
    private int count = 10;
    private String mTargetId;
    private ArrayList<GroupNoticeLookedMemberInfo> groupNoticeInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_groupnoticelookedlist);
        init();
        setListViewListener();
        loadRefresh();
    }

    private void init() {
        setTopTitle("已签到成员列表");
        mTargetId = getIntent().getStringExtra("mTargetId");
        adapter = new GroupNoticeLookedMemberListAdapter(this);
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    private void setListViewListener() {

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadRefresh();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }

        });
        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtils.e("=========="+i);
                if (i < 1) {
                    return;
                }
                String user_id = ((GroupNoticeLookedMemberInfo) adapter.getItem(i-1)).getUser_id();
                if (!TextUtils.isEmpty(user_id)) {
                    Intent intent = new Intent(GroupQianDaoMemberListActivity.this, UserInfoActivity.class);
                    intent.putExtra("user_id", user_id);
                    GroupQianDaoMemberListActivity.this.startActivity(intent);
                }
            }
        });
    }

    //获取通知公告已查看成员数据
    private void getNoticeLookedMemberData(String lastId, int count) {
        GroupNoticePostMemberParams groupNoticePostMemberParams = new GroupNoticePostMemberParams(mTargetId, lastId, count + "");
        RequestManager.request(this, groupNoticePostMemberParams,
                GroupNoticePostMemberResponse.class, this,
                SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    //刷新数据
    private void loadRefresh() {
        lastId = "9";
        adapter.clear();
        groupNoticeInfos.clear();
        getNoticeLookedMemberData(lastId, count);
    }

    //加载更多
    private void loadMore() {
        lastId = adapter.getLastDataId();
        if (TextUtils.isEmpty(lastId)) {
            loadRefresh();
            return;
        }
        getNoticeLookedMemberData(lastId, count);
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        listView.onRefreshComplete();
        if ("0".equals(response.getResCode())) {
            if (response instanceof GroupNoticePostMemberResponse) {
                GroupNoticePostMemberResponse groupNoticePostMemberResponse = (GroupNoticePostMemberResponse) response;
                if (groupNoticePostMemberResponse.getRepBody() != null) {
                    if (groupNoticePostMemberResponse.getRepBody().getList() != null && !groupNoticePostMemberResponse.getRepBody().getList().isEmpty()) {
                        groupNoticeInfos.addAll(groupNoticePostMemberResponse.getRepBody().getList());
                        adapter.setData(groupNoticeInfos);
                    } else {
                        ToastUtil.show("暂无更多!");
                    }

                } else {
                    ToastUtil.show("暂无更多!");
                }
            }

        } else {
            ToastUtil.show(response.getResMsg());
        }
    }
}

