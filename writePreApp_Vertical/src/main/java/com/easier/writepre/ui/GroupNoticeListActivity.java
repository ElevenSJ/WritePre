package com.easier.writepre.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.GroupNoticeListAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.CircleDetail;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.GroupNoticeInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleGetDetailParams;
import com.easier.writepre.param.GroupNoticePostNewParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleDetailResponse;
import com.easier.writepre.response.GroupNoticePostNewResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.rongyun.RongYunConversationActivity;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;

/**
 * 公告列表
 */
public class GroupNoticeListActivity extends BaseActivity {

    // 公告页面listView
    private PullToRefreshListView listView;
    private GroupNoticeListAdapter adapter;
    private String lastId = "9";
    private int count = 10;
    private String mTargetId;
    private ArrayList<GroupNoticeInfo> groupNoticeInfos = new ArrayList<>();
    private CircleDetail mCircleBody;
    public static final int REQUEST_SENDNOOTICE = 1000;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_groupnoticelist);
        init();
        setListViewListener();
        loadRefresh();
        getCircleDetail();
    }

    private void init() {
        setTopTitle("公告列表");
        mTargetId = getIntent().getStringExtra("mTargetId");
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        adapter = new GroupNoticeListAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onTopRightTxtClick(View view) {
        super.onTopRightTxtClick(view);
        Intent noticeIntent = new Intent(this, SendTopicActivity.class);
        noticeIntent.putExtra(SendTopicActivity.MODE_TYPE,
                SendTopicActivity.MODE_NOTICE);
        noticeIntent.putExtra("id", mTargetId);
        startActivityForResult(noticeIntent, REQUEST_SENDNOOTICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (REQUEST_SENDNOOTICE == requestCode) {
                dlgLoad.loading();
                loadRefresh();
            }
        }
    }

    /**
     * 获取圈子详情
     */
    private void getCircleDetail() {
        RequestManager.request(this, new CircleGetDetailParams(mTargetId),
                CircleDetailResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    return;
                }
                adapter.setSelectedIndex(position - 2);
//                if (adapter.getItem(position - 2) instanceof ContentInfo) {
//                    ContentInfo contentInfo = (ContentInfo) adapter.getItem(position - 2);
//                    SquareAllEssenceListAdapter.ViewHolder holder = (SquareAllEssenceListAdapter.ViewHolder) view.getTag();
//                    holder.tv_readCount.setTag(position - 2);
//                    adapter.requestReadAdd(holder.tv_readCount, contentInfo.get_id());
//                    Intent intent = new Intent(GroupNoticeListActivity.this, TopicDetailActivity.class);
//                    intent.putExtra("data", contentInfo);
//                    startActivity(intent);
//                } else {
//                    ActiveInfo activeInfo = (ActiveInfo) adapter.getItem(position - 2);
//                    Intent intent = new Intent(GroupNoticeListActivity.this, ActiveDetailActivity.class);
//                    intent.putExtra("id", activeInfo.get_id());
//                    startActivity(intent);
//                }
            }
        });
    }

    //获取通知公告数据
    private void getNoticeData(String lastId, int count) {
        GroupNoticePostNewParams groupNoticePostNewParams = new GroupNoticePostNewParams(mTargetId, lastId, count + "");
        RequestManager.request(this, groupNoticePostNewParams,
                GroupNoticePostNewResponse.class, this,
                SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    //刷新数据
    private void loadRefresh() {
        lastId = "9";
        adapter.clear();
        groupNoticeInfos.clear();
        getNoticeData(lastId, count);
    }

    //加载更多
    private void loadMore() {
        lastId = adapter.getLastDataId();
        if (TextUtils.isEmpty(lastId)) {
            loadRefresh();
            return;
        }
        getNoticeData(lastId, count);
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        listView.onRefreshComplete();
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof GroupNoticePostNewResponse) {
                GroupNoticePostNewResponse groupNoticePostNewResponse = (GroupNoticePostNewResponse) response;
                if (groupNoticePostNewResponse.getRepBody() != null) {
                    if (groupNoticePostNewResponse.getRepBody().getList() != null && !groupNoticePostNewResponse.getRepBody().getList().isEmpty()) {
                        groupNoticeInfos.addAll(groupNoticePostNewResponse.getRepBody().getList());
                        adapter.setData(groupNoticeInfos);
                    } else {
                        ToastUtil.show("暂无更多!");
                    }

                } else {
                    ToastUtil.show("暂无更多!");
                }
            } else if (response instanceof CircleDetailResponse) {
                CircleDetailResponse detailResponse = (CircleDetailResponse) response;
                mCircleBody = detailResponse.getRepBody();
                adapter.setCircleBody(mCircleBody);
                if (TextUtils.equals(mCircleBody.getRole(), "0")) {
                    setTopRightTxt("发布公告");
                } else {
                    setTopRightTxt(null);
                }
            }

        } else {
            ToastUtil.show(response.getResMsg());
        }
    }
}

