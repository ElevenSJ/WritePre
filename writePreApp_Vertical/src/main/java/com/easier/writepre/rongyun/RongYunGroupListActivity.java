package com.easier.writepre.rongyun;

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
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.RongyunGroupListAdapter;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleGetMyParams;
import com.easier.writepre.param.CircleMsgGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMsgResponse;
import com.easier.writepre.response.CircleMyResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.response.RongYunCheckInGroupResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.HanziToPinyin;
import com.easier.writepre.utils.PinyinCircleMsginfoComparator;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;
import com.easier.writepre.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * @author zhoulu
 * @version 创建时间：2016-8-31 下午3:15:13 类说明 群列表
 */
public class RongYunGroupListActivity extends BaseActivity implements
        OnRefreshListener2<ListView>, SectionIndexer {
    private ListView listView;
    private RongyunGroupListAdapter rongyunGroupListAdapter;
    private int start = 0;
    private int count = 30;
    private List<CircleInfo> list;
    private String token;
    private CircleInfo selectCircleInfo;
    private SideBar sideBar;
    private TextView dialog;
    private ClearEditText mClearEditText;
    private View emptyView;
    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinCircleMsginfoComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_rongyun_grouplist);
        rongyunGroupListAdapter = new RongyunGroupListAdapter(this);
        initView();
        loadRefresh();
    }

    private void initView() {
        setTopTitle("圈组列表");
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) this.findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) this
                .findViewById(R.id.title_layout_no_friends);
        pinyinComparator = new PinyinCircleMsginfoComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = rongyunGroupListAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }

            }
        });
        // 数据
        list = new ArrayList<CircleInfo>();
        listView = (ListView) findViewById(R.id.lv_group);
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
        listView.setAdapter(rongyunGroupListAdapter);
//        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectCircleInfo = rongyunGroupListAdapter
                                .getItem(position);
                        if (selectCircleInfo != null) {
                            RongYunUtils.getInstances().startGroupChat(RongYunGroupListActivity.this, selectCircleInfo.get_id(), selectCircleInfo.getName(), "0");
                        }
                    }
                });
    }

    protected void listViewLoadCircleData() {
        RequestManager.request(this, new CircleGetMyParams(), CircleMyResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        dlgLoad.loading();
        start = 0;
        count = 30;
        listViewLoadCircleData();
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {

    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleMyResponse) {
                CircleMyResponse gscrResult = (CircleMyResponse) response;
                if (gscrResult != null) {
                    CircleMyResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            if (!rBody.getList().isEmpty()) {
                                start++;
                                list.addAll(rBody.getList());
                                list = filledData(list);
                                // 根据a-z进行排序源数据
                                Collections.sort(list, pinyinComparator);
                                rongyunGroupListAdapter.setData(list);
                                listView.setAdapter(rongyunGroupListAdapter);
                                if (list != null && list.size() > 0) {
                                    sortListListener();
                                }
                            } else {
                                ((Button) emptyView.findViewById(R.id.btn_refresh)).setVisibility(View.GONE);
                                ToastUtil.show("没有圈组,赶快去创建或加入吧!");
                            }

                        }
                    }
                }
            }
        } else {
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
    private List<CircleInfo> filledData(List<CircleInfo> date) {
        List<CircleInfo> mSortList = new ArrayList<CircleInfo>();

        for (int i = 0; i < date.size(); i++) {
            CircleInfo sortModel = date.get(i);
            // 汉字转换成拼音
            String pinyin = HanziToPinyin.getPinYin(date.get(i).getName());
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
        List<CircleInfo> filterDateList = new ArrayList<CircleInfo>();
        tvNofriends.setVisibility(View.GONE);
        ((Button) emptyView.findViewById(R.id.btn_refresh)).setVisibility(View.GONE);
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;
        } else {
            filterDateList.clear();
            for (CircleInfo circleMsgInfo : list) {
                String name = circleMsgInfo.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || HanziToPinyin.getPinYin(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(circleMsgInfo);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        rongyunGroupListAdapter.setData(filterDateList);
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
