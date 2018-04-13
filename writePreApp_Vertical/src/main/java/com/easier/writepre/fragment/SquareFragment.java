package com.easier.writepre.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter.ViewHolder;
import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.ContentBase;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.ActiveContentParams;
import com.easier.writepre.param.BannersParams;
import com.easier.writepre.param.SquareContentGetParams;
import com.easier.writepre.response.ActiveContentResponse;
import com.easier.writepre.response.BannersResponse;
import com.easier.writepre.response.BannersResponse.BannersBody;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SquareContentGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.social.refreash.PullToRefreshListView.OnHeaderScrollListener;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.SendTopicActivity;
import com.easier.writepre.ui.TopicDetailActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.ViewPageIndicator;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.nineoldandroids.view.ViewHelper;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 广场
 *
 * @author kai.zhong
 */
public class SquareFragment extends BaseFragment {

    private int headerTop = 0;

    // 主页面listView
    private PullToRefreshListView listView;

    // listView 头布局文件
    private View placeHolderView;

    //广场贴集合
    private List<ContentInfo> list;

    //活动集合
    private List<ActiveInfo> activeInfos;

    // 标签布局
    private LinearLayout tabs;

    private int tabsTop = 0;

    private Handler mHandler;

    private int flag = 0;

    public static final boolean NEED_RELAYOUT = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;

    private int headerScrollSize = 0;

    private TextView leftTab, rightTab;

    private Button fab;

    private SquareAllEssenceListAdapter adapter;

    private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    public final static int REQUEST_CODE = 1001;

    private boolean isChange = false;

    private boolean isContentBack = true;//广场贴请求返回
    private boolean isActiveBack = true;//活动请求返回

    @Override
    public int getContextView() {
        return R.layout.fragment_square;
    }

    @Override
    protected void init() {
        mHandler = new Handler();
        findViews();
        setListViewListener();
        listViewAddHeader();
        if (advs.isEmpty()) {
            getAdvs();
        }
        flag = SocialMainView.getSquareItem();
        setTabClick(flag);
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
            if (isVisibleToUser) {
//                if (advs.isEmpty()) {
//                    getAdvs();
//                }
//                flag = SocialMainView.getSquareItem();
//                setTab();
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
//        flag = SocialMainView.getSquareItem();
//        setTabClick(flag);
//    }

    /**
     * 获取广告宣传
     */
    private void getAdvs() {
        RequestManager.request(getActivity(), new BannersParams("0"), BannersResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 获取活动信息
     */
    protected void getActiveInfo(String lastId, String tag) {
        isActiveBack = false;
        RequestManager.request(getActivity(), tag, new ActiveContentParams(lastId, 20),
                ActiveContentResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadData(String lastId, String tag) {
        isContentBack = false;
        RequestManager.request(getActivity(), tag, new SquareContentGetParams(lastId, 20, flag == 0),
                SquareContentGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
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
                if (isContentBack && isActiveBack) {
                    listViewLoadData("9", "isNew");
                }
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
                if (list != null && list.size() > 0 && isContentBack && isActiveBack) {
                    listViewLoadData(list.get(list.size() - 1).get_id(), "isOld");
                }
            }
        }, 300);
    }

    private void setListViewListener() {

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadNews();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
//                loadOlds();
            }

        });

        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadOlds();
            }
        });
        listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, false) {

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
                    ViewHelper.setTranslationY(tabs, -value);
                }
            }
        });

        listView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    return;
                }
                adapter.setSelectedIndex(position - 2);
                if (adapter.getItem(position - 2) instanceof ContentInfo) {
                    ContentInfo contentInfo = (ContentInfo) adapter.getItem(position - 2);
                    ViewHolder holder = (ViewHolder) view.getTag();
                    holder.tv_readCount.setTag(position - 2);
                    adapter.requestReadAdd(contentInfo);
                    Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
                    intent.putExtra("data", contentInfo);
                    startActivityForResult(intent, TopicDetailActivity.DETAIL_CODE);
                } else {
                    ActiveInfo activeInfo = (ActiveInfo) adapter.getItem(position - 2);
                    Intent intent = new Intent(getActivity(), ActiveDetailActivity.class);
                    intent.putExtra("id", activeInfo.get_id());
                    startActivity(intent);
                }

            }
        });

    }

    private void listViewAddHeader() {
        placeHolderView = getActivity().getLayoutInflater().inflate(R.layout.social_list_empty_head, null);
        AutoUtils.autoSize(placeHolderView);
        listView.getRefreshableView().addHeaderView(placeHolderView);

        mBannerViewPager = (ChildViewPager) placeHolderView.findViewById(R.id.banner_viewpager);
        mBannerIndicator = (ViewPageIndicator) placeHolderView.findViewById(R.id.banner_indicator);

        advAdapter = new SocialAdvertiseAdapter(getActivity());
        mBannerViewPager.setAdapter(advAdapter);

        mBannerIndicator.setGravity(Gravity.CENTER);
        mBannerIndicator.setRadius(5);

        // 广场贴数据
        list = new ArrayList<ContentInfo>();
        //活动数据
        activeInfos = new ArrayList<ActiveInfo>();
        // 圈子动态适配器
        adapter = new SquareAllEssenceListAdapter(getActivity(), listView.getRefreshableView());

        listView.setAdapter(adapter);
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
        super.onResponse(tag, response);
        loadingDlg.dismissDialog();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof SquareContentGetResponse) {
                isContentBack = true;
                SquareContentGetResponse gscrResult = (SquareContentGetResponse) response;
                if (gscrResult != null) {
                    SquareContentGetResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (tag.equals("isNew")) {
                            list.clear();
                        }
                        if (rBody.getList() != null) {
                            if (rBody.getList().isEmpty()) {
                                NoSwipeBackActivity.setListLabel(listView, true);
                            } else {
                                list.addAll(rBody.getList());
                            }
                        }
                    }
                }
                if (tag.equals("isNew") || activeInfos.isEmpty()) {
                    getActiveInfo("9", "isNew");
                } else {
                    getActiveInfo(activeInfos.get(activeInfos.size() - 1).get_id(), "isOld");
                }
            } else if (response instanceof ActiveContentResponse) {
                isActiveBack = true;
                ActiveContentResponse acResult = (ActiveContentResponse) response;
                if (acResult != null) {
                    ActiveContentResponse.Repbody rBody = acResult.getRepBody();
                    if (tag.equals("isNew")) {
                        activeInfos.clear();
                    }
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            activeInfos.addAll(rBody.getList());
                        }
                    }

                }
                adapter.mergeData(list, activeInfos);
            }
        } else {
            ToastUtil.show(response.getResMsg());
            if (response instanceof ActiveContentResponse) {
                isActiveBack = true;
                adapter.mergeData(list, activeInfos);
            } else if (response instanceof SquareContentGetResponse) {
                isContentBack = true;
                if (tag.equals("isNew") || activeInfos.isEmpty()) {
                    getActiveInfo("9", "isNew");
                } else {
                    getActiveInfo(activeInfos.get(activeInfos.size() - 1).get_id(), "isOld");
                }
            }
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
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
        } else {
            ToastUtil.show(response.getResMsg());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            uMeng(flag);
        }
    }

    public void setTabClick(int i) {
        if (fab == null){
            findViews();
        }
        flag = i;
        SocialMainView.setSquareItem(flag);
        NoSwipeBackActivity.setListLabel(listView, false);
        fab.setVisibility(View.VISIBLE);
        if (flag == 0) {
            if (isChange) {
                uMeng(1);
            }
            leftTab.setTextColor(getResources().getColor(R.color.social_red));
            rightTab.setTextColor(getResources().getColor(R.color.text_gray));
        } else {
            uMeng(0);
            isChange = true;
            leftTab.setTextColor(getResources().getColor(R.color.text_gray));
            rightTab.setTextColor(getResources().getColor(R.color.social_red));
        }
        listView.getRefreshableView().setSelection(0);
        loadNews();
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
        var.add(SocialMainView.CONTENT[SocialMainView.TAB_SQUARE]);
        if (index == 0) {
            var.add("广场全部");
        } else {
            var.add("广场精华");
        }
        YouMengType.onEvent(getActivity(), var, getShowTime(), var.get(var.size() - 1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:
                if (flag != 0) {
                    loadingDlg.loading();
                    setTabClick(0);
                }
                break;
            case R.id.txt_right:
                if (flag != 1) {
                    loadingDlg.loading();
                    setTabClick(1);
                }
                break;
            case R.id.fab:
                // if (LoginUtil.checkLogin(getActivity())) {
                Intent intent = new Intent(getActivity(), SendTopicActivity.class);
                intent.putExtra(SendTopicActivity.MODE_TYPE,
                        SendTopicActivity.MODE_SQUARE);
                startActivityForResult(intent, REQUEST_CODE);
                // }
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (TopicDetailActivity.DETAIL_CODE == requestCode) {
            LogUtils.e(adapter.getSelectedIndex() + "");
            if (getActivity().RESULT_OK == resultCode) {
                adapter.replace(adapter.getSelectedIndex(), (ContentBase) data.getSerializableExtra("data"));
            } else if (getActivity().RESULT_CANCELED == resultCode) {
                adapter.remove(adapter.getSelectedIndex());
            }
        } else if (REQUEST_CODE == requestCode && getActivity().RESULT_OK == resultCode) {
            loadNews();
        }
    }

}
