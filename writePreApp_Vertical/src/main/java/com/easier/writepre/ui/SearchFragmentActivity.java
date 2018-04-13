package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerFragmentAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.SearchAllFragment;
import com.easier.writepre.fragment.SearchSameCityFragment;
import com.easier.writepre.fragment.SearchTeacherFragment;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;
import com.easier.writepre.widget.MainViewPager;
import com.easier.writepre.widget.TabIndicator;
import com.hp.hpl.sparta.Text;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面
 *
 * @author zhoulu
 */
public class SearchFragmentActivity extends BaseActivity implements TextView.OnEditorActionListener {
    private static final String[] CONTENT_TITLE = new String[]{"全部",
            "老师", "同城"};
    private static String[] CONTENT;
    private TabIndicator indicator;

    private MainViewPager pager;

    private ViewPagerFragmentAdapter adapter;
    private int selectNo = 0;
    private SearchAllFragment searchAllFragment;
    private SearchTeacherFragment searchTeacherFragment;
    private SearchSameCityFragment searchSameCityFragment;
    private String lastKeyWord = "";
    private ClearEditText et_search;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_searchfragment);
        CONTENT = CONTENT_TITLE;
        initView();
    }

    public ClearEditText getEt_search() {
        return et_search;
    }

    public String getLastKeyWord() {
        return lastKeyWord;
    }

    public void setLastKeyWord(String lastKeyWord) {
        this.lastKeyWord = lastKeyWord;
    }


    private void initView() {
        setTopTitle("搜索");
        // 加载控件
        et_search = (ClearEditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearData();
                lastKeyWord = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_search.setOnEditorActionListener(this);
        pager = (MainViewPager) findViewById(R.id.main_viewpager);
        indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);
        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(),
                CONTENT);
        searchAllFragment = new SearchAllFragment();
        searchTeacherFragment = new SearchTeacherFragment();
        searchSameCityFragment = new SearchSameCityFragment();
        adapter.addFragment(searchAllFragment);
        adapter.addFragment(searchTeacherFragment);
        adapter.addFragment(searchSameCityFragment);
        pager.setAdapter(adapter);
        indicator.setViewPage(pager);
        pager.setOffscreenPageLimit(2);
        indicator.setCurrentItem(selectNo);
//        indicator.setOnTabSelectedListener(new TabIndicator.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(int position, String id, String name) {
//                doSearch(position);
//            }
//        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                doSearch(position);
                indicator.animateToTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void doSearch(int position) {
        if (position != selectNo) {
            umeng();
        }
        selectNo = position;
        String txt = et_search.getText().toString();
        lastKeyWord = txt;
        switch (position) {
            case 0:
                if (!TextUtils.isEmpty(txt)) {
                    ((SearchAllFragment) adapter.getItem(0)).doSearchAction(txt);
                }
                break;
            case 1:
                ((SearchTeacherFragment) adapter.getItem(1)).doSearchAction(txt);
                break;
            case 2:
                ((SearchSameCityFragment) adapter.getItem(2)).doSearchAction(txt);
                break;

        }
    }

    private void clearData() {
        ((SearchAllFragment) adapter.getItem(0)).doSearchClear();
        ((SearchTeacherFragment) adapter.getItem(1)).doSearchClear();
        ((SearchSameCityFragment) adapter.getItem(2)).doSearchClear();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            String txt = getEt_search().getText().toString();
            switch (pager.getCurrentItem()) {
                case 0: //全部
                    if (!TextUtils.isEmpty(txt)) {
                        lastKeyWord = txt;
                        ((SearchAllFragment) adapter.getItem(0)).doSearchAction(txt);
                        DeviceUtils.closeKeyboard(et_search,
                                this);
                    } else {
                        ToastUtil.show("请输入搜索条件");
                    }
                    break;
                case 1: //老师
                    lastKeyWord = txt;
                    ((SearchTeacherFragment) adapter.getItem(1)).doSearchAction(txt);
                    DeviceUtils.closeKeyboard(et_search,
                            this);
                    break;
                case 2: //同城
                    lastKeyWord = txt;
                    ((SearchSameCityFragment) adapter.getItem(2)).doSearchAction(txt);
                    DeviceUtils.closeKeyboard(et_search,
                            this);
                    break;
            }

            return true;
        }
        return false;
    }

    /**
     * 跳转进入用户详情
     */
    public void inUserInfo(String userId) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        this.startActivity(intent);
    }

    /**
     * 跳转进入帖子详情
     */
    public void inTopicDetail(String postId) {
        Intent intent = new Intent(this, TopicDetailActivity.class);
        intent.putExtra("id", postId);
        this.startActivity(intent);
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
        var.add("找朋友");
        YouMengType.onEvent(this, var, getShowTime(), CONTENT_TITLE[selectNo]);

    }
}
