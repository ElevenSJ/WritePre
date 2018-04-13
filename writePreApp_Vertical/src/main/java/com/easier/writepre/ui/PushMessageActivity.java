package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PushMessageListAdapter;
import com.easier.writepre.db.DBPushMessageHelper;
import com.easier.writepre.entity.PushMessageEntity;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.manager.ActStackManager;
import com.easier.writepre.manager.BindRongYunTokenManager;
import com.easier.writepre.manager.InitManager;
import com.easier.writepre.param.MessageCommParams;
import com.easier.writepre.param.MessageFeedBackIdParams;
import com.easier.writepre.param.MessageUserParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SysPushMessageResponse;
import com.easier.writepre.response.UserPushMessageResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息通知
 */
public class PushMessageActivity extends BaseActivity {

    private PullToRefreshListView listView;
    private PushMessageListAdapter adapter;

    private TextView tvLeft, tvRight;

    private List<PushMessageEntity> list = new ArrayList<PushMessageEntity>();

    private Handler handler = new Handler();

    private int checkTitle = -1;

    private String commLastId = "";
    private String userLastId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_push);
        initLastId();
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        loadNews();

    }

    private void initLastId() {
        commLastId = SPUtils.instance().get(SPUtils.COMM_MESSAGE_LASTID, "").toString();
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            userLastId = SPUtils.instance().get(SPUtils.instance().getLoginEntity().get_id(), "").toString();
        }
    }

    private void initView() {
        setTopTitle("通知");

        listView = (PullToRefreshListView) findViewById(R.id.listview);
        tvLeft = (TextView) findViewById(R.id.txt_left);
        tvRight = (TextView) findViewById(R.id.txt_right);
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        listView.setMode(Mode.PULL_FROM_START);
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
        adapter = new PushMessageListAdapter(this);
        listView.setAdapter(adapter);
        listViewLoadData();
    }

    private void setTopSelected(int i) {
        if (checkTitle == i) {
            return;
        }
        checkTitle = i;
        switch (i) {
            case 0:
                tvLeft.setTextColor(getResources().getColor(R.color.social_red));
                tvRight.setTextColor(getResources().getColor(R.color.social_black));
                break;
            case 1:
                tvLeft.setTextColor(getResources().getColor(R.color.social_black));
                tvRight.setTextColor(getResources().getColor(R.color.social_red));
                break;
            default:
                break;
        }
        listViewLoadData();
    }

    /**
     * 获取网络数据
     */
    protected void listViewLoadData() {
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            RequestManager.request(this, new MessageUserParams(userLastId), UserPushMessageResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        }
        RequestManager.request(this, new MessageCommParams(commLastId), SysPushMessageResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
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
                listViewLoadDataFromDb();
            }
        }, 300);
    }

    /**
     * 从数据库获取消息
     */
    protected void listViewLoadDataFromDb() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTopRightClick(View v) {
        Intent intent = new Intent(this, CircleSearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTopLeftClick(View view) {
        onback();
    }

    @Override
    public void onBackPressed() {
        onback();
    }

    private void onback() {
//        LogUtils.e("ActStackManager.getInstance().getActivityStack().size():"+ActStackManager.getInstance().getActivityStack().size());
//        if (ActStackManager.getInstance().getActivityStack()!=null&&ActStackManager.getInstance().getActivityStack().size()<2){
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
//            startActivity(intent);
//        }
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.txt_left:
                setTopSelected(0);
                break;
            case R.id.txt_right:
                if (LoginUtil.checkLogin(this)) {
                    setTopSelected(1);
                }
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
        if (response instanceof SysPushMessageResponse) {
            if ("0".equals(response.getResCode())) {
                SysPushMessageResponse gscrResult = (SysPushMessageResponse) response;
                if (gscrResult != null) {
                    SysPushMessageResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            for (int i = 0; i < rBody.getList().size(); i++) {
                                DBPushMessageHelper.insert(rBody.getList().get(i));
                                if (i == rBody.getList().size() - 1) {
                                    commLastId = rBody.getList().get(i).get_id();
                                    SPUtils.instance().put(SPUtils.COMM_MESSAGE_LASTID, commLastId);
                                    feedBackLastId();
                                }
                            }
                            updateAdapter();
                        }
                    }

                }
            } else
                ToastUtil.show(response.getResMsg());
        }
        if (response instanceof UserPushMessageResponse) {
            if ("0".equals(response.getResCode())) {
                UserPushMessageResponse gscrResult = (UserPushMessageResponse) response;
                if (gscrResult != null) {
                    UserPushMessageResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            for (int i = 0; i < rBody.getList().size(); i++) {
                                DBPushMessageHelper.insert(rBody.getList().get(i));
                                if (i == rBody.getList().size() - 1) {
                                    userLastId = rBody.getList().get(i).get_id();
                                    SPUtils.instance().put(SPUtils.instance().getLoginEntity().get_id(), userLastId);
                                    feedBackLastId();
                                }
                            }
                            updateAdapter();
                        }
                    }

                }
            } else
                ToastUtil.show(response.getResMsg());
        }

    }

    private void feedBackLastId() {
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            RequestManager.request(this, new MessageFeedBackIdParams(userLastId, commLastId), BaseResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        }

    }

    private void updateAdapter() {
        list.clear();
        String user_id = SPUtils.instance().getLoginEntity().get_id();
        list.addAll(DBPushMessageHelper.queryAll(user_id));
        adapter.setData(list);
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
        var.add("查看通知消息");
        YouMengType.onEvent(this, var, getShowTime(), "查看通知消息");
    }
}
