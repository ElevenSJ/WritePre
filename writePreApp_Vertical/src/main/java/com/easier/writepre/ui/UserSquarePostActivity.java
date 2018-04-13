package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter.ViewHolder;
import com.easier.writepre.entity.ContentBase;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.MySquarePostParams;
import com.easier.writepre.param.UserSquarePostParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SquareContentGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class UserSquarePostActivity extends BaseActivity implements
        OnRefreshListener2<ListView> {

    private String user_id;
    private String user_name;

    private PullToRefreshListView listView;

    private List<ContentInfo> list;

    private SquareAllEssenceListAdapter adapter;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_user_square_post);
        init();
    }

    private void init() {
        mHandler = new Handler();

        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        setTopTitle("TA的帖子");

        listView = (PullToRefreshListView) findViewById(R.id.listview);
        // 数据
        list = new ArrayList<ContentInfo>();

        adapter = new SquareAllEssenceListAdapter(this,listView.getRefreshableView());

        listView.setAdapter(adapter);


        listViewLoadData("9");

        listView.setOnRefreshListener(this);
        listView.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 1) {
                            return;
                        }
                        adapter.setSelectedIndex(position - 1);
                        if (adapter.getItem(position - 1) instanceof ContentInfo) {
                            ContentInfo contentInfo = (ContentInfo) adapter.getItem(position - 1);
                            ViewHolder holder = (ViewHolder) view.getTag();
                            holder.tv_readCount.setTag(position - 1);
                            adapter.requestReadAdd(contentInfo);
                            Intent intent = new Intent(UserSquarePostActivity.this,
                                    TopicDetailActivity.class);
                            intent.putExtra("data", contentInfo);
                            startActivityForResult(intent, TopicDetailActivity.DETAIL_CODE);
                        }
                    }
                });
        listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), true, true));
    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadData(String lastId) {
        RequestManager.request(UserSquarePostActivity.this,
                new UserSquarePostParams(user_id, lastId, "20"),
                SquareContentGetResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());

    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof SquareContentGetResponse) {
                SquareContentGetResponse gscrResult = (SquareContentGetResponse) response;
                if (gscrResult != null) {
                    SquareContentGetResponse.Repbody rBody = gscrResult
                            .getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            if (rBody.getList().isEmpty()) {
                                BaseActivity.setListLabel(listView, true);
                            } else {
                                list.addAll(rBody.getList());
                            }
                        }
                        adapter.mergeData(list,null);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadNews();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadOlds();

    }

    /**
     * 下拉获取数据
     */
    private void loadNews() {
        NoSwipeBackActivity.setListLabel(listView, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                list.clear();
                listViewLoadData("9");
            }
        }, 300);
    }

    /**
     * 上拉获取数据
     */
    protected void loadOlds() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                if (list != null && list.size() > 0) {
                    if (adapter.getItem(adapter.getCount() - 1) instanceof ContentInfo) {
                        listViewLoadData(((ContentInfo) adapter.getItem(adapter.getCount() - 1))
                                .get_id());
                    }
                }
            }
        }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == TopicDetailActivity.DETAIL_CODE) {
                if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getSerializableExtra("data") != null) {
                        adapter.replace(adapter.getSelectedIndex(),(ContentBase) data
                                .getSerializableExtra("data"));
                    }
                }else if (resultCode == RESULT_CANCELED) {
                    adapter.remove(adapter.getSelectedIndex());
                }

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        umeng();
    }

    private void umeng() {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FIV));
        var.add("查看帖子");
        YouMengType.onEvent(this,var,getShowTime(), user_name);
    }
}
