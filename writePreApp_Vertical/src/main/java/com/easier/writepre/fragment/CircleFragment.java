package com.easier.writepre.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleAllListAdapter;
import com.easier.writepre.adapter.CircleAllListAdapter.MsgViewHolder;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.entity.CircleMsgInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.CircleGetMyParams;
import com.easier.writepre.param.CircleMsgGetParams;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BannersResponse.BannersBody;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMsgResponse;
import com.easier.writepre.response.CircleMyResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.social.refreash.PullToRefreshListView.OnHeaderScrollListener;
import com.easier.writepre.ui.CircleCreatActivity;
import com.easier.writepre.ui.CircleListActivity;
import com.easier.writepre.ui.CircleMsgDetailActivity;
import com.easier.writepre.ui.CircleMsgListActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.ViewPageIndicator;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈子
 *
 * @author sunjie
 */
public class CircleFragment extends BaseFragment implements Listener<BaseResponse>, OnClickListener {

    private int headerTop = 0;

    // 主页面listView
    private PullToRefreshListView listView;

    // listView 头布局文件
    private View placeHolderView;

    // 圈子消息列表
    private List<CircleMsgInfo> msgList;

    // 圈子适配器
    private CircleAllListAdapter adapter;

    private List<CircleInfo> circleList;

    // 标签布局
    private LinearLayout tabs;

    private int tabsTop = 0;

    private Handler mHandler;

    private int flag = 0;

    public static final boolean NEED_RELAYOUT = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;

    private int headerScrollSize = 0;

    private TextView leftTab, rightTab;

    private Button fab;

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    private boolean isChange = false;

    private boolean isRequestBack = true;

    @Override
    public int getContextView() {
        return R.layout.fragment_circle;
    }

    @Override
    protected void init() {
        mHandler = new Handler();
        findViews();
        listViewAddHeader();
        setListViewListener();
        if (advs.isEmpty()) {
            getAdvs();
        }
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            flag = SocialMainView.getCircleItem();
            if (adapter != null) {
                adapter.setFlag(flag);
            }
            setTabClick(flag);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (advs.isEmpty()) {
            getAdvs();
        } else {
            setAdvs(advs);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isPrepared) {
            if (isVisibleToUser) { // false标识第一次加载 true说明已经加载数据
                LoginUtil.checkLogin(getActivity());
//                if (advs.isEmpty()) {
//                    getAdvs();
//                }
//                if (LoginUtil.checkLogin(getActivity())) {
//                    flag = SocialMainView.getCircleItem();
//                    if (adapter != null) {
//                        adapter.setFlag(flag);
//                    }
//                    setTabClick(flag);
//                }
            } else {
                uMeng(flag);
            }
        }
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (advs.isEmpty()) {
//            getAdvs();
//        }
//        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
//            flag = SocialMainView.getCircleItem();
//            if (adapter != null) {
//                adapter.setFlag(flag);
//            }
//            setTabClick(flag);
//        }
//    }

    /**
     * 获取广告宣传
     */
    private void getAdvs() {
        RequestManager.request(getActivity(), new BannersParams("1"), BannersResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());

    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadMsgData(String tag, String lastId) {
        isRequestBack = false;
        RequestManager.request(getActivity(), tag, new CircleMsgGetParams(lastId, "20"), CircleMsgResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 下拉获取圈子动态数据
     */
    private void loadMsgNews() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                listView.getRefreshableView().setSelection(0);
                if (isRequestBack) {
                    listViewLoadMsgData("isNew", "9");
                }
            }
        }, 300);
    }

    /**
     * 上拉获取圈子动态数据
     */
    protected void loadMsgOlds() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                if (msgList != null && msgList.size() > 0 && isRequestBack) {
                    listViewLoadMsgData("isOld", adapter.getMsgData().get(adapter.getCount() - 1).get_id());
                }
            }
        }, 300);
    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadCircleData(String lastId) {
        RequestManager.request(getActivity(), new CircleGetMyParams(), CircleMyResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 下拉刷新数据
     */
    private void loadCircleNews() {
        NoSwipeBackActivity.setListLabel(listView, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                listView.getRefreshableView().setSelection(0);
                listViewLoadCircleData("9");
            }
        }, 300);
    }

    /**
     * 加载更多
     */
    protected void loadCircleOlds() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                if (circleList != null && circleList.size() > 0) {
                    listViewLoadCircleData(adapter.getCircleData().get(adapter.getCount() - 1).get_id());
                }
            }
        }, 300);
    }

    private void setListViewListener() {

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                if (flag == 0) {
                    loadMsgNews();
                } else {
                    loadCircleNews();
                }
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
//                if (flag == 0) {
//                    loadMsgOlds();
//                } else {
//                    // loadCircleOlds();
//                }
            }

        });
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (flag == 0) {
                    loadMsgOlds();
                } else {
                    // loadCircleOlds();
                }
            }
        });
        listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, true) {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int scrollY = Math.max(-getScrollY(view), -tabsTop);
                if (NEED_RELAYOUT) {
                    headerTop = scrollY;
                    tabs.post(new Runnable() {
                        @Override
                        public void run() {
                            tabs.layout(0, headerTop, tabs.getWidth(), headerTop + tabs.getHeight());
                        }
                    });
                } else {
                    ViewHelper.setTranslationY(tabs, scrollY);
                }
            }
        });
        listView.setOnHeaderScrollListener(new OnHeaderScrollListener() {

            @Override
            public void onHeaderScroll(boolean isRefreashing, boolean istop, int value) {
                if (!istop) {
                    return;
                }
                if (isRefreashing) {
                    if (adapter.getCount() == 0) {
                        headerScrollSize = value + listView.getHeaderSize();
                    } else {
                        headerScrollSize = value;
                    }
                } else {
                    headerScrollSize = value;
                }
                if (NEED_RELAYOUT) {
                    tabs.post(new Runnable() {
                        @Override
                        public void run() {
                            tabs.layout(0, -headerScrollSize, tabs.getWidth(), -headerScrollSize + tabs.getHeight());
                        }
                    });
                } else {
                    ViewHelper.setTranslationY(tabs, -headerScrollSize);
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    return;
                }
                adapter.setSelectedIndex(position - 2);
                if (flag == 1) {
                    CircleInfo CircleInfo = (CircleInfo) adapter.getItem(position - 2);
                    toCircleList(CircleInfo.get_id());
                } else {
                    CircleMsgInfo MsgInfo = (CircleMsgInfo) adapter.getItem(position - 2);
                    int type = adapter.getItemViewType(position - 2);
                    if (type == 0) {
                        MsgViewHolder msgViewHolder = (MsgViewHolder) view.getTag();
                        msgViewHolder.tv_readCount.setTag(position - 2);
                        adapter.requestReadAdd(MsgInfo.getCircle_id(), MsgInfo.get_id());
                    }
                    Intent intent = new Intent(getActivity(), CircleMsgDetailActivity.class);
                    intent.putExtra("data", MsgInfo);
                    startActivityForResult(intent, CircleMsgDetailActivity.DETAIL_CODE);
                }
            }
        });

    }

    private void toCircleList(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), CircleMsgListActivity.class);
        intent.putExtra("circle_id", id); // 圈子id
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    private void listViewAddHeader() {
        placeHolderView = getActivity().getLayoutInflater().inflate(R.layout.social_list_empty_head, null);
        listView.getRefreshableView().addHeaderView(placeHolderView);

        placeHolderView.findViewById(R.id.layout_creat_circle).setOnClickListener(this);

        mBannerViewPager = (ChildViewPager) placeHolderView.findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) placeHolderView.findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(getActivity());
        mBannerViewPager.setAdapter(advAdapter);

        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);

        // 圈子动态数据
        msgList = new ArrayList<CircleMsgInfo>();
        // 圈子动态适配器
        adapter = new CircleAllListAdapter(getActivity(), listView.getRefreshableView());

        // 圈子数据
        circleList = new ArrayList<CircleInfo>();
        // 圈子适配器

        listView.setAdapter(adapter);

        listView.setMode(Mode.DISABLED);

        updateAdapter();
    }

    private void findViews() {

        fab = (Button) findViewById(R.id.fab);

        fab.setOnClickListener(this);

        tabs = (LinearLayout) findViewById(R.id.show_tabs);

        leftTab = (TextView) findViewById(R.id.txt_left);

        rightTab = (TextView) findViewById(R.id.txt_right);

        leftTab.setOnClickListener(this);

        rightTab.setOnClickListener(this);

        listView = (PullToRefreshListView) findViewById(R.id.listview);

    }

    private void toSearchCircle() {
        if (LoginUtil.checkLogin(getActivity())) {
            Intent intent = new Intent(getActivity(), CircleListActivity.class);
            startActivity(intent);
        }
    }

    private void toCreatCircle() {
        if (LoginUtil.checkLogin(getActivity())) {
            Intent intent = new Intent(getActivity(), CircleCreatActivity.class);
            startActivityForResult(intent, CircleCreatActivity.MODIFY);
        }
    }

    /**
     * 更新listview加载的适配器和布局
     */
    private void updateAdapter() {
        if (flag == 0) {
            // 初始化选择圈子动态
            adapter.setMsgData(flag, msgList);
        } else {
            adapter.setCircleData(flag, circleList);
        }
    }

    /**
     * 获取当前listview的滚动高度
     *
     * @return
     */
    public int getScrollY(AbsListView view) {
        tabsTop = tabs.getTop();
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        int firstVisiblePosition = view.getFirstVisiblePosition();
        if (firstVisiblePosition == 0) {
            return -top + headerScrollSize;
        } else if (firstVisiblePosition == 1) {
            return -top;
        } else {
            return -top + (firstVisiblePosition - 2) * c.getHeight() + placeHolderView.getHeight();
        }
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        loadingDlg.dismissDialog();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleMsgResponse) {
                CircleMsgResponse gscrResult = (CircleMsgResponse) response;
                if (gscrResult != null) {
                    CircleMsgResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (tag.equals("isNew")) {
                            msgList.clear();
                        }
                        if (rBody.getList() != null) {
                            if (rBody.getList().isEmpty()) {
                                NoSwipeBackActivity.setListLabel(listView, true);
                            }
                            msgList.addAll(rBody.getList());
                        }
                        updateAdapter();
                        isRequestBack = true;
                    }
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
            if (response instanceof CircleMsgResponse) {
                isRequestBack = true;
            }
        }

    }

    @Override
    public void onResponse(BaseResponse response) {
        loadingDlg.dismissDialog();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof BannersResponse) {
                BannersResponse bannersResult = (BannersResponse) response;
                if (bannersResult != null) {
                    BannersBody body = bannersResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null) {
                            advs.clear();
                            advs.addAll(body.getList());
                            setAdvs(advs);
                        }
                    }
                }
            }
            if (response instanceof CircleMyResponse) {
                CircleMyResponse myResult = (CircleMyResponse) response;
                if (myResult != null) {
                    CircleMyResponse.Repbody rBody = myResult.getRepBody();
                    if (rBody != null) {
                        circleList.clear();
                        if (rBody.getList() != null) {
                            circleList.addAll(rBody.getList());
                            updateAdapter();
                        }
                    }
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:
                // if (flag == 0) {
                // return;
                // }
                // if (!LoginUtil.checkLogin(getActivity())) {
                // return;
                // }
                loadingDlg.loading();
                setTabClick(0);
                break;
            case R.id.txt_right:
                // if (flag == 1) {
                // return;
                // }
                // if (!LoginUtil.checkLogin(getActivity())) {
                // return;
                // }
                loadingDlg.loading();
                setTabClick(1);
                break;
            case R.id.layout_creat_circle:
                toCreatCircle();
                break;
            case R.id.fab:
                toSearchCircle();
            default:
                break;
        }

    }

    public void setTabClick(int i) {
        if(fab == null){
            findViews();
        }
        NoSwipeBackActivity.setListLabel(listView, false);
        flag = i;
        SocialMainView.setCircleItem(flag);
        switch (i) {
            case 0:
                if (isChange) {
                    uMeng(1);
                }
                placeHolderView.findViewById(R.id.layout_creat_circle).setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                listView.setMode(Mode.BOTH);
                leftTab.setTextColor(getResources().getColor(R.color.social_red));
                rightTab.setTextColor(getResources().getColor(R.color.text_gray));
                updateAdapter();
                loadMsgNews();
                break;
            case 1:
                isChange = true;
                uMeng(0);
                placeHolderView.findViewById(R.id.layout_creat_circle).setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                listView.setMode(Mode.PULL_FROM_START);
                leftTab.setTextColor(getResources().getColor(R.color.text_gray));
                rightTab.setTextColor(getResources().getColor(R.color.social_red));
                updateAdapter();
                loadCircleNews();
                break;
            default:
                break;
        }

    }

    /**
     * 友盟统计
     */
    public void uMeng() {
        uMeng(flag);
    }

    /**
     * 友盟统计
     */
    private void uMeng(int index) {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add(SocialMainView.CONTENT[SocialMainView.TAB_CIRCLE]);
        if (index == 0) {
            var.add("圈子动态");
        } else {
            var.add("我的圈子");
        }
        YouMengType.onEvent(getActivity(), var, getShowTime(), var.get(var.size() - 1));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            uMeng(flag);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CircleMsgDetailActivity.DETAIL_CODE == requestCode) {
            if (getActivity().RESULT_OK == resultCode) {
                adapter.replace(adapter.getSelectedIndex(), data.getSerializableExtra("data"));
            } else if (getActivity().RESULT_CANCELED == resultCode) {
                adapter.remove(adapter.getSelectedIndex());
            }
        } else if (CircleCreatActivity.MODIFY == requestCode) {
            if (getActivity().RESULT_OK == resultCode) {
                loadCircleNews();
            }
        }
    }
}
