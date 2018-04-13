package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleAllListAdapter;
import com.easier.writepre.adapter.CircleAllListAdapter.MsgViewHolder;
import com.easier.writepre.app.RongCloudEvent;
import com.easier.writepre.entity.CircleApplyMember;
import com.easier.writepre.entity.CircleDetail;
import com.easier.writepre.entity.CircleMsgInfo;
import com.easier.writepre.entity.GroupNoticeInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.CircleApplyMemberParams;
import com.easier.writepre.param.CircleGetDetailParams;
import com.easier.writepre.param.CircleJoinParams;
import com.easier.writepre.param.CircleMemberRegByCodeParams;
import com.easier.writepre.param.CircleMsgListParams;
import com.easier.writepre.param.GroupNoticeAddPostParams;
import com.easier.writepre.param.GroupNoticePostNewParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleApplyMemberResponse;
import com.easier.writepre.response.CircleDetailResponse;
import com.easier.writepre.response.CircleJoinResponse;
import com.easier.writepre.response.CircleMemberRegByCodeResponse;
import com.easier.writepre.response.CircleMsgResponse;
import com.easier.writepre.response.GroupNoticeAddPostResponse;
import com.easier.writepre.response.GroupNoticePostNewResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.rongyun.GroupNoticeListener;
import com.easier.writepre.rongyun.RongYunConversationActivity;
import com.easier.writepre.rongyun.RongYunUtils;
import com.easier.writepre.rongyun.WPNoticeMessage;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.CirclePopupWindow;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * 指定圈子动态页面
 */
public class CircleMsgListActivity extends BaseActivity implements GroupNoticeListener {

    PointF downP = new PointF();
    PointF curP = new PointF();

    private Button btFatie;
    private Button btJoin;
    private PullToRefreshListView listView;

    private List<CircleMsgInfo> list;

    private CircleAllListAdapter adapter;

    private String lastId = "9";

    private String circle_id;

    private String circle_name;

    private Handler handler;

    public final int REQUEST_CODE = 1001;

    public static final int GET_NOTICE_DATA = 5;//获取通知数据
    private CircleDetail mCircleBody;

    private int index = 0;

    private CirclePopupWindow popWindow;
    private Dialog dialog;
    private ImageView flag_drawer;//下拉通知显示开关
    private RelativeLayout rel_topNotice;//下拉通知布局
    private RelativeLayout rel_topNoticeContent;//通知内容布局
    private RelativeLayout rel_loading;//公告加载提示
    private LinearLayout ll_images, ll_qiandao;
    private TextView tv_notice_title, tv_image_count, tv_time, tv_notice_null;
    private Button btn_daka;
    private boolean isLoadingNotice;
    private List<GroupNoticeInfo> groupNoticeInfoList;
    private GroupNoticeInfo currentGroupNoticeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_msg_list);
        RongCloudEvent.getInstance().setGroupNoticeListener(this);
        circle_name = TextUtils.isEmpty(getIntent().getStringExtra(
                "circle_name")) ? "圈子动态" : getIntent().getStringExtra(
                "circle_name");
        circle_id = getIntent().getStringExtra("circle_id");
        initView();
        initTopNotice();
        getCircleDetail();
        handler.removeCallbacks(getUnReadrunnable);
        handler.postDelayed(getUnReadrunnable, 1000);
        isNeedsShowNotice(circle_id);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        RongCloudEvent.getInstance().setGroupNoticeListener(this);
        circle_name = TextUtils.isEmpty(getIntent().getStringExtra(
                "circle_name")) ? "圈子动态" : getIntent().getStringExtra(
                "circle_name");
        circle_id = getIntent().getStringExtra("circle_id");
        setTopTitle(circle_name);
        getCircleDetail();
        findViewById(R.id.notice_icon).setVisibility(View.GONE);
        handler.removeCallbacks(getUnReadrunnable);
        handler.postDelayed(getUnReadrunnable, 1000);
        isNeedsShowNotice(circle_id);
    }

    Runnable getUnReadrunnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                getCountUnRead();
                handler.postDelayed(this, 1000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public void getCountUnRead() {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getUnreadCount(Conversation.ConversationType.GROUP, circle_id, new RongIMClient.ResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    LogUtils.e("未读消息：" + integer);
                    if (findViewById(R.id.top_right_txt1).getVisibility() == View.VISIBLE)
                        if (integer > 0) {
                            findViewById(R.id.notice_icon).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.notice_icon).setVisibility(View.GONE);
                        }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    findViewById(R.id.notice_icon).setVisibility(View.GONE);
                }
            });
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_NOTICE_DATA:
                    //新通知到来
                    if ((boolean) flag_drawer.getTag()) {
                        if (!isLoadingNotice) {
                            rel_loading.setVisibility(View.VISIBLE);
                            tv_notice_null.setVisibility(View.GONE);
                            rel_topNoticeContent.setVisibility(View.GONE);
                            getNoticeData();
                        }
                    } else {
                        flag_drawer.performClick();
                    }
                    break;
            }
        }
    };

    private void isNeedsShowNotice(final String id) {
        if (RongIM.getInstance() != null && !TextUtils.isEmpty(id)) {
            RongIM.getInstance().getLatestMessages(Conversation.ConversationType.GROUP, id, 1, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
                @Override
                public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                    if (messages != null && !messages.isEmpty()) {
                        final MessageContent messageContent = messages.get(0).getContent();
                        if (messageContent instanceof WPNoticeMessage) {
                            if (!messages.get(0).getReceivedStatus().isDownload()) {
                                mHandler.sendEmptyMessage(GET_NOTICE_DATA);
                                io.rong.imlib.model.Message.ReceivedStatus receivedStatus = messages.get(0).getReceivedStatus();
                                receivedStatus.setDownload();
                                RongIM.getInstance().setMessageReceivedStatus(messages.get(0).getMessageId(), receivedStatus, null);
                            }
                        } else {
                            RongIM.getInstance().getHistoryMessages(Conversation.ConversationType.GROUP, "WP:NtcNtf", id, messages.get(0).getMessageId(), 1, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
                                @Override
                                public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                                    if (messages != null && !messages.isEmpty()) {
                                        if (messageContent instanceof WPNoticeMessage) {
                                            if (!messages.get(0).getReceivedStatus().isDownload()) {
                                                mHandler.sendEmptyMessage(GET_NOTICE_DATA);
                                                io.rong.imlib.model.Message.ReceivedStatus receivedStatus = messages.get(0).getReceivedStatus();
                                                receivedStatus.setDownload();
                                                RongIM.getInstance().setMessageReceivedStatus(messages.get(0).getMessageId(), receivedStatus, null);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    private void initTopNotice() {
        rel_topNotice = (RelativeLayout) findViewById(R.id.rel_topNotice);
        rel_topNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击查看群公告列表
                Intent intent = new Intent(CircleMsgListActivity.this, GroupNoticeListActivity.class);
                intent.putExtra("mTargetId", circle_id);
                startActivity(intent);
                //隐藏公告栏
                rel_topNotice.setVisibility(View.GONE);
                flag_drawer.setBackgroundResource(R.drawable.notice_downflag);
                flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
            }
        });
        rel_loading = (RelativeLayout) findViewById(R.id.rel_loading);
        tv_notice_null = (TextView) findViewById(R.id.tv_notice_null);
        ll_images = (LinearLayout) findViewById(R.id.ll_images);
        ll_qiandao = (LinearLayout) findViewById(R.id.ll_qiandao);
        rel_topNoticeContent = (RelativeLayout) findViewById(R.id.rel_topNoticeContent);
        tv_time = (TextView) findViewById(R.id.time);
        tv_notice_title = (TextView) findViewById(R.id.tv_notice_title);
        tv_image_count = (TextView) findViewById(R.id.tv_image_count);
        btn_daka = (Button) findViewById(R.id.btn_daka);
        ll_qiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentGroupNoticeInfo != null) {
                    requestLookNotice(currentGroupNoticeInfo.get_id(), currentGroupNoticeInfo.get_id());
                }
            }
        });
        flag_drawer = (ImageView) findViewById(R.id.flag_drawer);
        flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
        flag_drawer.setVisibility(View.VISIBLE);
        flag_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((boolean) flag_drawer.getTag()) {
                    //隐藏公告栏
                    rel_topNotice.setVisibility(View.GONE);
                    flag_drawer.setBackgroundResource(R.drawable.notice_downflag);
                } else {
                    //显示公告栏
                    rel_topNotice.setVisibility(View.VISIBLE);
                    flag_drawer.setBackgroundResource(R.drawable.notice_upwardflag);
                    if (!isLoadingNotice) {
                        rel_loading.setVisibility(View.VISIBLE);
                        tv_notice_null.setVisibility(View.GONE);
                        rel_topNoticeContent.setVisibility(View.GONE);
                        getNoticeData();
                    }
                }
                flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
            }
        });
    }

    //获取通知公告数据
    private void getNoticeData() {
        isLoadingNotice = true;
        GroupNoticePostNewParams groupNoticePostNewParams = new GroupNoticePostNewParams(circle_id, "9", "1");
        RequestManager.request(this, groupNoticePostNewParams,
                GroupNoticePostNewResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof GroupNoticePostNewResponse) {
                                GroupNoticePostNewResponse groupNoticePostNewResponse = (GroupNoticePostNewResponse) response;
                                if (groupNoticePostNewResponse.getRepBody() != null) {
                                    groupNoticeInfoList = groupNoticePostNewResponse.getRepBody().getList();
                                    if (groupNoticeInfoList != null && !groupNoticeInfoList.isEmpty()) {
                                        currentGroupNoticeInfo = groupNoticeInfoList.get(0);
                                    } else {
                                        currentGroupNoticeInfo = null;
                                    }
                                    upDateNotice();
                                }
                                isLoadingNotice = false;
                            }

                        } else {
                            ToastUtil.show(response.getResMsg());
                            isLoadingNotice = false;
                            upDateNotice();
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {

                    }
                },
                SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    //确认查看公告
    public void requestLookNotice(String id, String pub_news_id) {
        RequestManager.request(this, id, new GroupNoticeAddPostParams(pub_news_id), GroupNoticeAddPostResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {

                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof GroupNoticeAddPostResponse) {
                                if (currentGroupNoticeInfo != null) {
                                    if (TextUtils.equals(tag, currentGroupNoticeInfo.get_id())) {
                                        currentGroupNoticeInfo.setIs_viewed("1");
                                        currentGroupNoticeInfo.setView_num((Integer.parseInt(currentGroupNoticeInfo.getView_num()) + 1) + "");
                                        upDateNotice();
                                    }
                                }
                            }
                        } else {
                            ToastUtil.show(response.getResMsg());
                        }
                    }
                },
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    //设置下拉通知
    private void upDateNotice() {
        rel_loading.setVisibility(View.GONE);
        if (currentGroupNoticeInfo == null) {
            rel_topNoticeContent.setVisibility(View.GONE);
            tv_notice_null.setVisibility(View.VISIBLE);
            return;
        } else {
            tv_notice_null.setVisibility(View.GONE);
            rel_topNoticeContent.setVisibility(View.VISIBLE);
        }
        tv_notice_title.setText(currentGroupNoticeInfo.getTitle());
        tv_time.setText(currentGroupNoticeInfo.getCtime());
        if (currentGroupNoticeInfo.getImg_url() != null && currentGroupNoticeInfo.getImg_url().length > 0) {
            ll_images.setVisibility(View.VISIBLE);
            tv_image_count.setText(currentGroupNoticeInfo.getImg_url().length + "");
        } else {
            ll_images.setVisibility(View.GONE);
        }
        tv_time.setText(DateKit.Toymd(currentGroupNoticeInfo.getCtime()));
        if (TextUtils.equals("1", currentGroupNoticeInfo.getIs_viewed())) {
            findViewById(R.id.iv_image_daka).setEnabled(false);
            btn_daka.setEnabled(false);
            ll_qiandao.setEnabled(false);
        } else {
            findViewById(R.id.iv_image_daka).setEnabled(false);
            btn_daka.setEnabled(true);
            ll_qiandao.setEnabled(true);
        }
        btn_daka.setText(currentGroupNoticeInfo.getView_num());
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mCircleBody != null) {
            if (mCircleBody.getUser_id().equals(
                    SPUtils.instance().getLoginEntity().get_id())) {
                getCircleApplyMember();
            }
        }
    }

    private void updateData() {
        if (mCircleBody == null) {
            return;
        }
        circle_name = mCircleBody.getName();
        setTopTitle(circle_name);
        if (mCircleBody.getRole().equals("0")) {
            btFatie.setVisibility(View.VISIBLE);
            setTopRightTxt1("群聊");
            setTopRightTxt("管理");
            btJoin.setVisibility(View.GONE);
        } else if (mCircleBody.getRole().equals("1")) {
            btFatie.setVisibility(View.VISIBLE);
            setTopRightTxt1("群聊");
            setTopRightTxt("详情");
            btJoin.setVisibility(View.GONE);
        } else if (mCircleBody.getRole().equals("10")) {
            btFatie.setVisibility(View.GONE);
            btJoin.setVisibility(View.VISIBLE);
            setTopRightTxt("详情");
        } else if (mCircleBody.getRole().equals("11")) {
            btFatie.setVisibility(View.GONE);
            btJoin.setVisibility(View.VISIBLE);
            setTopRightTxt("详情");
        }
        if (mCircleBody.getIs_open().equals("1")
                || btFatie.getVisibility() == View.VISIBLE) {
            listViewLoadData(lastId);
        }
    }

    private void getCircleApplyMember() {
        RequestManager.request(this, new CircleApplyMemberParams(circle_id,
                "9", 10), CircleApplyMemberResponse.class, this, SPUtils
                .instance().getSocialPropEntity().getApp_socail_server());

    }

    private void initView() {

        popWindow = new CirclePopupWindow(this, itemsOnClick);
        handler = new Handler(Looper.getMainLooper());

        setTopTitle(circle_name);

        btFatie = (Button) findViewById(R.id.fab);
        btJoin = (Button) findViewById(R.id.join);

        listView = (PullToRefreshListView) findViewById(R.id.page_tab_listview);
        list = new ArrayList<CircleMsgInfo>();

        adapter = new CircleAllListAdapter(CircleMsgListActivity.this,listView.getRefreshableView());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.setSelectedIndex(position - 1);
                CircleMsgInfo msgInfo = (CircleMsgInfo) adapter
                        .getItem(position - 1);
                MsgViewHolder holder = (MsgViewHolder) view.getTag();
                holder.tv_readCount.setTag(position - 1);
                adapter.requestReadAdd(
                        msgInfo.getCircle_id(), msgInfo.get_id());
                Intent intent = new Intent(CircleMsgListActivity.this,
                        CircleMsgDetailActivity.class);
                intent.putExtra("data", msgInfo);
                intent.putExtra(CircleMsgDetailActivity.ACTIVITY_TYPE,
                        CircleMsgDetailActivity.LIST_ACTIVITY);
                startActivityForResult(intent, CircleMsgDetailActivity.DETAIL_CODE);
            }
        });

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

        btFatie.setOnClickListener(this);
        btJoin.setOnClickListener(this);

        listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, true));
    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadData(String lastId) {
        RequestManager.request(this, new CircleMsgListParams(circle_id, lastId,
                "20"), CircleMsgResponse.class, this, SPUtils.instance()
                .getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 获取圈子详情
     */
    private void getCircleDetail() {
        RequestManager.request(this, new CircleGetDetailParams(circle_id),
                CircleDetailResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                list.clear();
                listViewLoadData("9");
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
                if (list != null && list.size() > 0) {
                    listViewLoadData(adapter.getMsgData()
                            .get(adapter.getCount() - 1).get_id());
                }
            }
        }, 300);
    }

    protected void stopRefresh() {
        listView.onRefreshComplete();
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleMsgResponse) {
                CircleMsgResponse gscrResult = (CircleMsgResponse) response;
                if (gscrResult != null) {
                    CircleMsgResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody.getList() != null) {
                        list.addAll(rBody.getList());
                        adapter.setMsgData(0, list);
                    }
                }
            } else if (response instanceof CircleDetailResponse) {
                CircleDetailResponse detailResponse = (CircleDetailResponse) response;
                if (detailResponse != null) {
                    mCircleBody = detailResponse.getRepBody();
                    updateData();
                }

            } else if (response instanceof CircleJoinResponse) {
                ToastUtil.show("加入成功，请等待圈主审核！");
            } else if (response instanceof CircleApplyMemberResponse) {
                CircleApplyMemberResponse applyMemberResponse = (CircleApplyMemberResponse) response;
                if (applyMemberResponse != null) {
                    if (applyMemberResponse.getRepBody() != null) {
                        if (applyMemberResponse.getRepBody().getList() != null) {
                            updateNotify(applyMemberResponse.getRepBody()
                                    .getList());
                        }
                    }
                }
            } else if (response instanceof CircleMemberRegByCodeResponse) {
                dialog.dismiss();
                mCircleBody.setRole("1");
                btFatie.setVisibility(View.VISIBLE);
                setTopRightTxt1("群聊");
                setTopRightTxt("详情");
                btJoin.setVisibility(View.GONE);
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    private void updateNotify(List<CircleApplyMember> list) {
        if (list != null && !list.isEmpty()) {
            findViewById(R.id.notify_icon).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.notify_icon).setVisibility(View.GONE);
        }

    }

    @Override
    public void onTopRightTxtClick(View view) {
        Intent intent = new Intent(this, CircleDetailActivity.class);
        intent.putExtra("circle_id", circle_id);
        startActivity(intent);
    }

    @Override
    public void onTopRightTxtClick1(View view) {
        if (LoginUtil.checkLogin(this)) {
            umengIM();
            RongYunUtils.getInstances().startGroupChat(this, circle_id, circle_name, "0");
        }
    }

    private void umengIM() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add(SocialMainView.CONTENT[SocialMainView.TAB_CIRCLE]);
        var.add("圈子群聊");
        YouMengType.onEvent(this, var, 1, circle_name);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.fab:
                if (LoginUtil.checkLogin(this)) {
                    toCeartNewMsg();
                }
                break;
            case R.id.join:
                if (LoginUtil.checkLogin(this)) {
                    if(TextUtils.isEmpty(mCircleBody.getCode())){
                        joinCircle();
                    }else{
                        popWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * 加入圈子
     */
    private void joinCircle() {
        dlgLoad.loading();
        RequestManager.request(this, new CircleJoinParams(circle_id),
                CircleJoinResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());

    }

    /**
     * 发帖
     */
    private void toCeartNewMsg() {
        if (LoginUtil.checkLogin(CircleMsgListActivity.this)) {
            Intent intent = new Intent(this, SendTopicActivity.class);
            intent.putExtra(SendTopicActivity.MODE_TYPE,
                    SendTopicActivity.MODE_CIRCLE);
            intent.putExtra("id", circle_id);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (RESULT_OK == resultCode && REQUEST_CODE == requestCode) {
            loadNews();
        }
        if (CircleMsgDetailActivity.DETAIL_CODE == requestCode) {
            if (RESULT_OK == resultCode){
                adapter.replace(adapter.getSelectedIndex(),data.getSerializableExtra("data"));
            }else if (RESULT_CANCELED == resultCode){
                adapter.remove(adapter.getSelectedIndex());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        umeng();
    }

    private void umeng() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add(SocialMainView.CONTENT[SocialMainView.TAB_CIRCLE]);
        var.add("圈子详情");
        YouMengType.onEvent(this, var, getShowTime(), circle_name);
    }

    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            popWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_2:
                    dialog = new Dialog(CircleMsgListActivity.this, R.style.loading_dialog);
                    dialog.setContentView(R.layout.dialog_circle_code);
                    final EditText et_code = (EditText) dialog.findViewById(R.id.et_code);
                    TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_ok);
                    TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                    dialog.show();
                    tv_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(et_code.getText())) {
                                ToastUtil.show("口令不能为空");
                            } else {
                                RequestManager.request(CircleMsgListActivity.this, new CircleMemberRegByCodeParams(circle_id, et_code.getText().toString()),
                                        CircleMemberRegByCodeResponse.class, CircleMsgListActivity.this, SPUtils
                                                .instance().getSocialPropEntity().getApp_socail_server());
                            }
                        }
                    });
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    break;
                case R.id.btn_1:
//                    ToastUtil.show("直接加入");
                    joinCircle();
                    break;
                default:
                    break;
            }

        }

    };

    private boolean isTouchPointInView(RelativeLayout view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }


    //隐藏公告栏
    private void goneNoticeView() {
        if ((boolean) flag_drawer.getTag()) {
            flag_drawer.performClick();
        }
        flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
    }

    private boolean isTouchPointInView(ImageView view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent arg0) {
        super.dispatchTouchEvent(arg0);
        curP.x = arg0.getX();
        curP.y = arg0.getY();
        if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
            downP.x = arg0.getX();
            downP.y = arg0.getY();
        } else if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
            float beginX = downP.x;
            float endX = curP.x;
            float beginY = downP.y;
            float endY = curP.y;
            float minMove = 120; // 最小滑动距离
            if (endX < beginX && beginX - endX > minMove && Math.abs(endY - beginY) < 60) {
                if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) && TextUtils.equals("群聊", ((TextView) findViewById(R.id.top_right_txt1)).getText().toString())) {
                    umengIM();
                    RongYunUtils.getInstances().startGroupChat(this, circle_id, circle_name, "0");
                }
            }

        } else if (arg0.getAction() == MotionEvent.ACTION_UP) {
            if (!isTouchPointInView(rel_topNotice, (int) arg0.getRawX(), (int) arg0.getRawY()) && !isTouchPointInView(flag_drawer, (int) arg0.getRawX(), (int) arg0.getRawY())) {
                goneNoticeView();
            }
        }
        return super.onTouchEvent(arg0);
    }

    @Override
    public void onNewNotice(String id) {
        if (TextUtils.equals(id, circle_id)) {
            mHandler.sendEmptyMessage(GET_NOTICE_DATA);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(getUnReadrunnable);
        RongCloudEvent.getInstance().setCheckFriendListener(null);
    }
}
