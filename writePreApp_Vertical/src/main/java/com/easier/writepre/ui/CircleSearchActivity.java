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
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 圈子搜索
 */
public class CircleSearchActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private PullToRefreshListView listView;
    private CircleSearchListAdapter adapter;

    private ClearEditText etSearch;
    private ImageView imgSearch;

    private List<CircleInfo> list = new ArrayList<CircleInfo>();

    private Handler handler = new Handler();

    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_search);
        initView();
    }

    private void initView() {
        setTopTitle("搜索圈子");

        listView = (PullToRefreshListView) findViewById(R.id.listview);
        adapter = new CircleSearchListAdapter(this);
        listView.setAdapter(adapter);
        etSearch = (ClearEditText) findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener(this);
        imgSearch = (ImageView) findViewById(R.id.img_search);

        imgSearch.setOnClickListener(this);

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
                    intent.setClass(CircleSearchActivity.this, CircleDetailActivity.class);
                } else {
                    intent.setClass(CircleSearchActivity.this, CircleMsgListActivity.class);
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

    }

    /**
     * 首次获取数据
     *
     * @param
     */
    protected void listViewLoadData() {
        if (TextUtils.isEmpty(etSearch.getText().toString())) {
            ToastUtil.show("请输入圈子名");
        } else {
            RequestManager.request(this, new CircleSeaerchParams(etSearch.getText().toString(), start, 20, ""), CircleMyResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        }
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
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
                listView.onRefreshComplete();
                start = adapter.getCount();
                listViewLoadData();
            }
        }, 300);
    }

    @Override
    public void onTopRightClick(View v) {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_search:

                listViewLoadData();

                break;
            default:
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleMyResponse) {
                CircleMyResponse gscrResult = (CircleMyResponse) response;
                if (gscrResult != null) {
                    CircleMyResponse.Repbody rBody = gscrResult.getRepBody();
                    if (start == 0) {
                        list.clear();
                        list.addAll(rBody.getList());
                        if (rBody.getList().size() == 0) {
                            ToastUtil.show("该圈子不存在!");
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

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String txt = etSearch.getText().toString();
            if (!TextUtils.isEmpty(txt)) {
                listViewLoadData();
            }
            return true;
        }
        return false;
    }
}
