package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleSearchListAdapter;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleSeaerchParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMyResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 圈子发现
 */
public class CircleListActivity extends BaseActivity {

    private PullToRefreshListView listView;
    private CircleSearchListAdapter adapter;

    private TextView tvLeft, tvCenter, tvRight;

    private List<CircleInfo> list = new ArrayList<CircleInfo>();

    private Handler handler = new Handler();

    private int start = 0;

    private int checkTitle = -1;

    private String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_list);
        initView();
    }

    private void initView() {
        setTopRight(R.drawable.ic_search);

        listView = (PullToRefreshListView) findViewById(R.id.listview);
        adapter = new CircleSearchListAdapter(this);
        listView.setAdapter(adapter);
        tvLeft = (TextView) findViewById(R.id.txt_left);
        tvCenter = (TextView) findViewById(R.id.txt_center);
        tvRight = (TextView) findViewById(R.id.txt_right);

        tvLeft.setOnClickListener(this);
        tvCenter.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        setTopSelected(0);

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadNews();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                loadOlds();
            }

        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CircleInfo CircleInfo = adapter.getItem(position - 1);
                Intent intent = new Intent();
                // 私有查看圈子介绍，公开查看圈子动态
                if (CircleInfo.getIs_open().equals("0")) {
                    intent.setClass(CircleListActivity.this, CircleDetailActivity.class);
                } else {
                    intent.setClass(CircleListActivity.this, CircleMsgListActivity.class);
                }
                intent.putExtra("circle_id", CircleInfo.get_id()); // 圈子id
                intent.putExtra("circle_name", CircleInfo.getName()); // 圈子名称
                if (CircleInfo.getUser_id().equals(SPUtils.instance().getLoginEntity().get_id())) {
                    intent.putExtra("is_role", true);
                } else {
                    intent.putExtra("is_role", false);
                }
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(),true,true));
    }

    private void setTopSelected(int i) {
        if (checkTitle == i) {
            return;
        }
        checkTitle = i;
        switch (i) {
            case 0:
                tag = tvLeft.getTag().toString();
                tvLeft.setTextColor(getResources().getColor(R.color.social_red));
                tvRight.setTextColor(getResources().getColor(R.color.social_black));
                tvCenter.setTextColor(getResources().getColor(R.color.social_black));
                break;
            case 1:
                tag = tvRight.getTag().toString();
                tvRight.setTextColor(getResources().getColor(R.color.social_red));
                tvLeft.setTextColor(getResources().getColor(R.color.social_black));
                tvCenter.setTextColor(getResources().getColor(R.color.social_black));
                break;
            case 2:
                tag = tvCenter.getTag().toString();
                tvCenter.setTextColor(getResources().getColor(R.color.social_red));
                tvLeft.setTextColor(getResources().getColor(R.color.social_black));
                tvRight.setTextColor(getResources().getColor(R.color.social_black));
                break;
            default:
                break;
        }
        list.clear();
        start = 0;
        listViewLoadData();
    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadData() {
        RequestManager.request(this, new CircleSeaerchParams("", start, 20, tag), CircleMyResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                start = 0;
                listViewLoadData();
            }
        }, 300);
    }

    /**
     * 加载更多
     */
    protected void loadOlds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                start = adapter.getCount();
                listViewLoadData();
            }
        }, 300);
    }

    @Override
    public void onTopRightClick(View v) {
        Intent intent = new Intent(this, CircleSearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.txt_left:
                setTopSelected(0);
                break;
            case R.id.txt_right:
                setTopSelected(1);
                break;
            case R.id.txt_center:
                setTopSelected(2);
                break;
            default:
                break;
        }
    }

    protected void stopRefresh() {
        listView.onRefreshComplete();
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleMyResponse) {
                CircleMyResponse gscrResult = (CircleMyResponse) response;
                if (gscrResult != null) {
                    CircleMyResponse.Repbody rBody = gscrResult.getRepBody();
                    if (start == 0) {
                        if (rBody.getList().size() != 0) {
                            list.clear();
                            list.addAll(rBody.getList());
                        }
                    } else {
                        list.addAll(rBody.getList());
                    }
                    adapter.setData(list);
                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }
}
