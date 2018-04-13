package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.easier.writepre.R;
import com.easier.writepre.adapter.FoundBeiTieListAdapter;
import com.easier.writepre.adapter.SortGroupMemberAdapter;
import com.easier.writepre.entity.GroupMemberBean;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.FoundMainView;
import com.easier.writepre.param.ResPenmenParams;
import com.easier.writepre.param.ResPeriodPenmenParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ResPenmenResponse;
import com.easier.writepre.response.ResPeriodPenmenResponse;
import com.easier.writepre.utils.CharacterParser;
import com.easier.writepre.utils.HanziToPinyin;
import com.easier.writepre.utils.PinyinComparator;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;
import com.easier.writepre.widget.SideBar;
import com.umeng.analytics.MobclickAgent;

public class FindClassicWorksActivity extends BaseActivity implements SectionIndexer {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortGroupMemberAdapter adapter;
    private ClearEditText mClearEditText;

    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;

    private String period_title;

    private String period_id;

    //    private String[] person = {"李斯","王羲之","王献之","王珣","褚遂良","张旭","怀素","颜真卿","柳公权","颜真卿","黄庭坚","苏轼","米芾",};
    private List<PersonInfo> personInfoList = new ArrayList<PersonInfo>();
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
//    private CharacterParser characterParser;
    private List<GroupMemberBean> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_classic_works);
        initViews();
    }

    private void initViews() {
        period_title = getIntent().getStringExtra("period_title");
        period_id = getIntent().getStringExtra("period_id");
        setTopTitle(period_title);
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) this.findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) this
                .findViewById(R.id.title_layout_no_friends);
        // 实例化汉字转拼音类
//        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                if(adapter!=null) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                umeng(((GroupMemberBean) adapter.getItem(position)).getName());
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Intent intent = new Intent(FindClassicWorksActivity.this,
                        FoundBeiTieCalligrapherActivity.class);
                intent.putExtra("period_title", period_title);
                intent.putExtra("name", ((GroupMemberBean) adapter.getItem(position)).getName());
                intent.putExtra("link_url", ((GroupMemberBean) adapter.getItem(position)).getLink_url());
                intent.putExtra("penmen_id", ((GroupMemberBean) adapter.getItem(position)).getId());
                FindClassicWorksActivity.this.startActivity(intent);

            }
        });
        //获取数据
//        RequestManager.request(this,new ResPenmenParams(), ResPenmenResponse.class,
//                this, Constant.URL);
        RequestManager.request(this, new ResPeriodPenmenParams(period_id), ResPeriodPenmenResponse.class,
                this, Constant.URL);

    }

    //list排序后的监听操作
    private void sortListListener() {
        sortListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int section = getSectionForPosition(firstVisibleItem);
                int nextSection = getSectionForPosition(firstVisibleItem + 1);
                int nextSecPosition = getPositionForSection(+nextSection);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(SourceDateList.get(
                            getPositionForSection(section)).getSortLetters());
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        MarginLayoutParams params = (MarginLayoutParams) titleLayout
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
    private List<GroupMemberBean> filledData(List<PersonInfo> date) {
        List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

        for (int i = 0; i < date.size(); i++) {
            GroupMemberBean sortModel = new GroupMemberBean();
            sortModel.setName(date.get(i).getName());
            sortModel.setId(date.get(i).getId());
            sortModel.setLink_url(date.get(i).getLink_url());
            // 汉字转换成拼音
//            String pinyin = characterParser.getSelling(date.get(i).getName());
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
        List<GroupMemberBean> filterDateList = new ArrayList<GroupMemberBean>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
            tvNofriends.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (GroupMemberBean sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || HanziToPinyin.getPinYin(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
        if (filterDateList.size() == 0) {
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
        if (SourceDateList != null && SourceDateList.size() > 0)
            return SourceDateList.get(position).getSortLetters().charAt(0);
        return 0;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onResponse(BaseResponse response) {

        if ("0".equals(response.getResCode())) {
            if (response instanceof ResPeriodPenmenResponse) {
                ResPeriodPenmenResponse mResPenmenResponse = (ResPeriodPenmenResponse) response;
                if (mResPenmenResponse != null) {
                    ResPeriodPenmenResponse.ResPeriodPenmenBody mResPenmenBody = mResPenmenResponse
                            .getRepBody();
                    if (mResPenmenBody != null) {
                        for (int p = 0; p < mResPenmenBody.getList().size(); p++) {
                            PersonInfo personInfo = new PersonInfo();
                            personInfo.setId(mResPenmenBody.getList().get(p).get_id());
                            personInfo.setName(mResPenmenBody.getList().get(p).getText());
                            personInfo.setLink_url(mResPenmenBody.getList().get(p).getLink_url());
                            personInfoList.add(personInfo);
                        }
                        //        SourceDateList = filledData(getResources().getStringArray(R.array.date));
                        SourceDateList = filledData(personInfoList);

                        // 根据a-z进行排序源数据
                        Collections.sort(SourceDateList, pinyinComparator);
                        adapter = new SortGroupMemberAdapter(this, SourceDateList);
                        sortListView.setAdapter(adapter);
                        if(SourceDateList != null && SourceDateList.size()>0){
                            sortListListener();
                        }

                    }

                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    /**
     * 书法家信息
     */
    class PersonInfo {
        private String id;
        private String name;
        private String link_url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }
    }

    private void umeng(String name) {
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_FOR));
            var.add(FoundMainView.CONTENT[FoundMainView.index]);
            var.add(period_title);
            var.add(name);
            YouMengType.onEvent(this, var, 1, name);
    }
}
