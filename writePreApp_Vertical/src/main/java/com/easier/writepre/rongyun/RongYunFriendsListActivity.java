package com.easier.writepre.rongyun;

import io.rong.imkit.RongIM;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.RongyunFriendsListAdapter;
import com.easier.writepre.entity.JointAttentionInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleMemberParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMemberResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.response.RongYunJointAttentionListResponse;
import com.easier.writepre.response.TellSomeOneMemberResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.HanziToPinyin;
import com.easier.writepre.utils.PinyinFriendsComparator;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;
import com.easier.writepre.widget.SideBar;

/**
 * @author zhoulu
 * @version 创建时间：2016-8-31 下午3:15:13 类说明 好友列表
 */
public class RongYunFriendsListActivity extends BaseActivity implements
        OnRefreshListener2<ListView>, SectionIndexer {
    private ListView listView;
    private RongyunFriendsListAdapter rongyunFriendsListAdapter;
    private int start = 0;
    private int count = 30;
    private List<JointAttentionInfo> list;
    private String token;
    private JointAttentionInfo selectJointAttentionInfo;
    private SideBar sideBar;
    private TextView dialog;
    private ClearEditText mClearEditText;
    private View emptyView;
    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;
    private RelativeLayout group_layout;
    private String group_id;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinFriendsComparator pinyinComparator;

    public static int SELECT_TYPE;
    public static final int SELECT_FRIEND = 0;//选择好友
    public static final int SELECT_SOMEONE = 1;//@某人

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_rongyun_friendslist);
        rongyunFriendsListAdapter = new RongyunFriendsListAdapter(this);
        SELECT_TYPE = getIntent().getIntExtra("selectType", SELECT_FRIEND);
        initView();
        if (SELECT_TYPE == SELECT_FRIEND) {
            loadRefresh();
        } else {
            loadTellSB();
        }
    }

    private void initView() {
        group_layout = (RelativeLayout) findViewById(R.id.group_layout);
        group_layout.setOnClickListener(this);
        if (SELECT_TYPE == SELECT_FRIEND) {
            setTopTitle("好友列表");
            group_layout.setVisibility(View.VISIBLE);

        } else {
            setTopTitle("选择回复的人");
            group_layout.setVisibility(View.GONE);
            group_id = getIntent().getStringExtra("groupId");
        }
        // 数据
        list = new ArrayList<JointAttentionInfo>();
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) this.findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) this
                .findViewById(R.id.title_layout_no_friends);
        pinyinComparator = new PinyinFriendsComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = rongyunFriendsListAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }

            }
        });

        listView = (ListView) findViewById(R.id.lv_friends);
        emptyView = getLayoutInflater().inflate(R.layout.empty_view, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setVisibility(View.GONE);
        emptyView.findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRefresh();
            }
        });
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
//        listView.setMode(Mode.PULL_FROM_START);
        listView.setAdapter(rongyunFriendsListAdapter);
//        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
//                        if (position < 1) {
//                            return;
//                        }
                        selectJointAttentionInfo = rongyunFriendsListAdapter
                                .getItem(position);
                        if (selectJointAttentionInfo != null) {
                            if (SELECT_TYPE == SELECT_FRIEND) {
                                // 检查是否是好友关系
                                RongYunUtils.getInstances().requestCheckIsFriend(
                                        RongYunFriendsListActivity.this,
                                        selectJointAttentionInfo.getUser_id(),
                                        RongYunFriendsListActivity.this);
                            } else {
                                //@某人数据返回
                                String headImageUrl = StringUtil.getHeadUrl(selectJointAttentionInfo
                                        .getHead_img());
                                UserInfo userInfo = new UserInfo(selectJointAttentionInfo.getUser_id(), selectJointAttentionInfo.getUname(),
                                        TextUtils.isEmpty(headImageUrl) ? null : Uri.parse(headImageUrl));
                                RongMentionManager.getInstance().mentionMember(userInfo);
                                RongYunFriendsListActivity.this.finish();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.group_layout:
                //TODO 跳转到群组页面
                startActivity(new Intent(this, RongYunGroupListActivity.class));
                break;
        }
    }

    /**
     * 获取群成员列表
     */
    private void loadTellSB() {
        //TODO 目前的请求方式存在问题 服务端做了分页 不能一次性请求所有成员,导致用户在@某人时需要不停的需要加载更多
        RequestManager.request(this,
                new CircleMemberParams(group_id, "9", 30),
                TellSomeOneMemberResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        dlgLoad.loading();
        if (list != null)
            list.clear();
        if (rongyunFriendsListAdapter != null) {
            rongyunFriendsListAdapter.clearData();
        }
        start = 0;
        count = 30;
        RongYunUtils.getInstances().requestJointAttentionList(this, this);
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        start = rongyunFriendsListAdapter.getCount();

    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
//        listView.onRefreshComplete();
        if ("0".equals(response.getResCode())) {
            if (response instanceof RongYunJointAttentionListResponse) {
                RongYunJointAttentionListResponse r = (RongYunJointAttentionListResponse) response;
                if (r.getRepBody().getList() != null) {
                    if (!r.getRepBody().getList().isEmpty()) {
                        start++;
                        list.addAll(r.getRepBody().getList());
                        list = filledData(list);
                        // 根据a-z进行排序源数据
                        Collections.sort(list, pinyinComparator);
                        rongyunFriendsListAdapter.setData(list);
                        listView.setAdapter(rongyunFriendsListAdapter);
                        if (list != null && list.size() > 0) {
                            sortListListener();
                        }
                    } else {
                        ((Button) emptyView.findViewById(R.id.btn_refresh)).setVisibility(View.GONE);
                        ToastUtil.show("没有好友,赶快去认识新朋友吧!");
                    }
                } else {
                    ToastUtil.show("没有好友,赶快去认识新朋友吧!");
                }
            } else if (response instanceof RongYunCheckFriendResponse) {
                RongYunCheckFriendResponse r = (RongYunCheckFriendResponse) response;
                if (TextUtils.equals("ok", r.getRepBody().getRes())) {
                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().startPrivateChat(
                                RongYunFriendsListActivity.this,
                                selectJointAttentionInfo.getUser_id(),
                                selectJointAttentionInfo.getUname());
                    }
                } else {
                    ToastUtil.show("很抱歉不是好友关系,不能发起聊天!");
                }
            } else if (response instanceof TellSomeOneMemberResponse) {
                TellSomeOneMemberResponse tellSomeOneMemberResponse= (TellSomeOneMemberResponse) response;
                if (tellSomeOneMemberResponse.getRepBody().getList() != null) {
                    if (!tellSomeOneMemberResponse.getRepBody().getList().isEmpty()) {
                        list.addAll(tellSomeOneMemberResponse.getRepBody().getList());
                        list = filledData(list);
                        // 根据a-z进行排序源数据
                        Collections.sort(list, pinyinComparator);
                        rongyunFriendsListAdapter.setData(list);
                        listView.setAdapter(rongyunFriendsListAdapter);
                        if (list != null && list.size() > 0) {
                            sortListListener();
                        }
                    } else {
                        ToastUtil.show("暂时没有可以回复的朋友!");
                    }
                } else {
                    ToastUtil.show("暂时没有可以回复的朋友!");
                }

            }

        } else {
            ((Button) emptyView.findViewById(R.id.btn_refresh)).setVisibility(View.VISIBLE);
            ToastUtil.show(response.getResMsg());
        }
        dlgLoad.dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dlgLoad.dismissDialog();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        // TODO Auto-generated method stub
        loadRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadMore();
    }

    //list排序后的监听操作
    private void sortListListener() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (getSectionForPosition(firstVisibleItem) == -1) {
                    return;
                }
                int section = getSectionForPosition(firstVisibleItem);
//                if (firstVisibleItem == 0) {
//                    return;
//                }
                int nextSection = getSectionForPosition(firstVisibleItem == 0 ? 0 : firstVisibleItem + 1);
                int nextSecPosition = getPositionForSection(+nextSection);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(list.get(
                            getPositionForSection(section)).getSortLetters());
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom() + 10;
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 这个时候不需要挤压效果 就把他隐藏掉
                titleLayout.setVisibility(View.GONE);
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<JointAttentionInfo> filledData(List<JointAttentionInfo> date) {
        List<JointAttentionInfo> mSortList = new ArrayList<JointAttentionInfo>();

        for (int i = 0; i < date.size(); i++) {
            JointAttentionInfo sortModel = date.get(i);
            if(TextUtils.isEmpty(date.get(i).getUname()))
            {
                date.get(i).setUname("未知");
            }
            // 汉字转换成拼音
            String pinyin = HanziToPinyin.getPinYin(date.get(i).getUname());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<JointAttentionInfo> filterDateList = new ArrayList<JointAttentionInfo>();
        tvNofriends.setVisibility(View.GONE);
        ((Button) emptyView.findViewById(R.id.btn_refresh)).setVisibility(View.GONE);
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;
        } else {
            filterDateList.clear();
            for (JointAttentionInfo jointAttentionInfo : list) {
                String name = jointAttentionInfo.getUname();
                if (name.indexOf(filterStr.toString()) != -1
                        || HanziToPinyin.getPinYin(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(jointAttentionInfo);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        rongyunFriendsListAdapter.setData(filterDateList);
        if (filterDateList.isEmpty()) {
            tvNofriends.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if (list != null && !list.isEmpty() && list.size() > 0)
            return list.get(position).getSortLetters().charAt(0);
        return -1;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
