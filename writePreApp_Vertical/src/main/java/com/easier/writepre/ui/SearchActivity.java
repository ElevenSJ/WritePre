package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.RecommendAdapter;
import com.easier.writepre.adapter.SearchAdapter;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.RecommendInfo;
import com.easier.writepre.entity.SearchInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.EditInfoParam;
import com.easier.writepre.param.RecommendParams;
import com.easier.writepre.param.SearchParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.EditMyInfoResponse;
import com.easier.writepre.response.RecommendResponse;
import com.easier.writepre.response.SearchResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面
 * Created by zhoulu on 2016/10/17.
 */

public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener {
    private int delayTime = 500;//设置连续输入延迟，时不请求接口
    private ClearEditText et_search;
    private RadioGroup radioGroup;
    private PullToRefreshListView pullToRefreshListView;
    private boolean isFirst = true;//判断是否是第一次进入 true 是  false 不是
    private View headView;
    private TextView tv_header_title;
    private SearchAdapter searchAdapter;
    private RecommendAdapter recommendAdapter;
    private ArrayList<RecommendInfo> recommendInfos = new ArrayList<>();
    private ArrayList<SearchInfo> searchAllInfos = new ArrayList<>();
    private ArrayList<SearchInfo> searchTeacherInfos = new ArrayList<>();
    private ArrayList<SearchInfo> searchSameCityInfos = new ArrayList<>();
    private ArrayList<SearchInfo> searchInfos = new ArrayList<>();
    private int recommendStart, recommendCount = 10;
    private int allStart, allCount = 10;
    private int teacherStart, teacherCount = 10;
    private int sameCityStart, sameCityCount = 10;
    private int searchStart, searchCount = 10;
    private static final int CALLBACK_CITY = 100;
    private String city;
    private String lastKeyWord = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        setTopTitle("搜索");
        if (!TextUtils.isEmpty(SPUtils.instance().getLoginEntity().getAddr().trim())) {
            city = SPUtils.instance().getLoginEntity().getAddr();
        }
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        searchAdapter = new SearchAdapter(this);
        recommendAdapter = new RecommendAdapter(this);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(this);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        addHeader();
        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position < 2) {
                    return;
                }
                if (isFirst) {
                    inTopicDetail(((RecommendInfo) recommendAdapter.getItem(position - 2)).getPost_id());
                } else {
                    inUserInfo(((SearchInfo) searchAdapter.getItem(position - 2)).get_id());
                }
            }
        });
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isFirst) {
                    refreshData();
                } else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.rb_all:
                            if (TextUtils.equals(lastKeyWord, et_search.getText().toString())) {
                                if (TextUtils.isEmpty(et_search.getText().toString())) {
                                    mHandler.sendEmptyMessage(0);
                                } else {
                                    refreshData();
                                }
                            } else {
                                ToastUtil.show("搜索条件改变,请重新搜索");
                            }
                            break;
                        case R.id.rb_teacher:
                            refreshData();
                            break;
                        case R.id.rb_samecity:
                            if (TextUtils.equals(lastKeyWord, et_search.getText().toString())) {
                                if (TextUtils.isEmpty(et_search.getText().toString())) {
                                    mHandler.sendEmptyMessage(0);
                                } else {
                                    refreshData();
                                }
                            } else {
                                ToastUtil.show("搜索条件改变,请重新搜索");
                            }
                            break;
                    }
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isFirst) {
                    loadMoreData();
                } else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.rb_all:
                            if (TextUtils.equals(lastKeyWord, et_search.getText().toString())) {
                                if (TextUtils.isEmpty(et_search.getText().toString())) {
                                    mHandler.sendEmptyMessage(0);
                                } else {
                                    loadMoreData();
                                }
                            } else {
                                mHandler.sendEmptyMessage(0);
                                ToastUtil.show("搜索条件改变,请重新搜索");
                            }
                            break;
                        case R.id.rb_teacher:
                            loadMoreData();
                            break;
                        case R.id.rb_samecity:
                            if (TextUtils.equals(lastKeyWord, et_search.getText().toString())) {
                                if (TextUtils.isEmpty(et_search.getText().toString())) {
                                    mHandler.sendEmptyMessage(0);
                                } else {
                                    loadMoreData();
                                }
                            } else {
                                mHandler.sendEmptyMessage(0);
                                ToastUtil.show("搜索条件改变,请重新搜索");
                            }
                            break;
                    }
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String keyWord = et_search.getText().toString();
                if (searchAdapter != null) {
                    searchAdapter.setKeyWord(keyWord);
                }
                mHandler.sendEmptyMessage(0);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_all:
                        if (isFirst) {
                            tv_header_title.setText("推荐关注");
                            refreshData();
                        } else {
                            tv_header_title.setText("全部");
                            if (!TextUtils.isEmpty(keyWord)) {
                                refreshData();
                            } else {
                                if (searchAllInfos != null)
                                    searchAllInfos.clear();
                                if (searchAdapter != null)
                                    searchAdapter.clear();
                                allStart = 0;
                                searchAdapter.setData(searchAllInfos);
                            }
                        }
                        ((RadioButton) findViewById(R.id.rb_all_select)).setChecked(true);
                        break;
                    case R.id.rb_teacher:
                        tv_header_title.setText("老师");
                        isFirst = false;
                        pullToRefreshListView.setAdapter(searchAdapter);
                        ((RadioButton) findViewById(R.id.rb_teacher_select)).setChecked(true);
                        refreshData();
                        break;
                    case R.id.rb_samecity:
                        tv_header_title.setText("同城");
                        isFirst = false;
                        pullToRefreshListView.setAdapter(searchAdapter);
                        ((RadioButton) findViewById(R.id.rb_samecity_select)).setChecked(true);
                        if (!TextUtils.isEmpty(keyWord)) {
                            refreshData();
                        } else {
                            if (searchSameCityInfos != null)
                                searchSameCityInfos.clear();
                            if (searchAdapter != null)
                                searchAdapter.clear();
                            sameCityStart = 0;
                            searchAdapter.setData(searchSameCityInfos);
                        }
                        break;
                }
            }
        });
        ((RadioButton) findViewById(R.id.rb_all)).setChecked(true);
        ((RadioButton) findViewById(R.id.rb_all)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFirst) {
                    isFirst = false;
                    if (recommendInfos != null)
                        recommendInfos.clear();
                    if (recommendAdapter != null)
                        recommendAdapter.clear();
                    pullToRefreshListView.setAdapter(searchAdapter);
                    searchAdapter.setData(searchAllInfos);
                    tv_header_title.setText("全部");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALLBACK_CITY) {
            if (resultCode == RESULT_OK) {
                city = data.getStringExtra("city");
                if (!TextUtils.isEmpty(SPUtils.instance().getLoginEntity().get_id())) {
                    //已登录，更新用户个人信息
                    uploadMyInfo();
                }
            }
        }
    }

    /**
     * 更新个人信息
     */
    private void uploadMyInfo() {
        LoginEntity body = SPUtils.instance().getLoginEntity();
        String uname = body.getUname();
        String birth_day = body.getBirth_day();
        String age = body.getAge();
        String sex = body.getSex();
        String addr = city;
        String fav = "";
        ;
        String interest = body.getInterest();
        String qq = "";
        ;
        String bei_tie = body.getBei_tie();
        String fav_font = body.getFav_font();
        String stu_time = "";
        ;
        String school = body.getSchool();
        String company = "";
        ;
        String profession = body.getProfession();
        String signature = body.getSignature();
        String email0 = body.getEmail0();
        String real_name = body.getReal_name();
        String goodat = body.getGood_at();
        List<Double> coord = new ArrayList<Double>();
        coord.add(0.0);
        coord.add(0.0);

        RequestManager.request(this, new EditInfoParam(uname, birth_day, age,
                sex, addr, fav, interest, qq, bei_tie, fav_font, stu_time,
                school, company, profession, signature, email0, real_name,
                coord, goodat), EditMyInfoResponse.class, this, Constant.URL);
    }

    /**
     * 跳转进入用户详情
     */
    private void inUserInfo(String userId) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    /**
     * 跳轉進入帖子詳情
     */
    public void inTopicDetail(String postId) {

        Intent intent = new Intent(this, TopicDetailActivity.class);
        intent.putExtra("id", postId);
        startActivity(intent);
    }

    private void addHeader() {
        headView = View.inflate(this, R.layout.list_header_search, null);
        tv_header_title = (TextView) headView.findViewById(R.id.tv_header_title);
        pullToRefreshListView.getRefreshableView().addHeaderView(headView);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (isFirst) {
            //推荐
            if (recommendInfos != null)
                recommendInfos.clear();
            if (recommendAdapter != null)
                recommendAdapter.clear();
            recommendStart = 0;
            requestRecommend();
        } else {
            //筛选
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rb_all:
                    if (searchAllInfos != null)
                        searchAllInfos.clear();
                    if (searchAdapter != null)
                        searchAdapter.clear();
                    allStart = 0;
                    requestSearchData("全部", et_search.getText().toString(), "", "", allStart, allCount);
                    break;
                case R.id.rb_teacher:
                    if (searchTeacherInfos != null)
                        searchTeacherInfos.clear();
                    if (searchAdapter != null)
                        searchAdapter.clear();
                    teacherStart = 0;
                    requestSearchData("老师", et_search.getText().toString(), "1", "", teacherStart, teacherCount);
                    break;
                case R.id.rb_samecity:
                    if (searchSameCityInfos != null)
                        searchSameCityInfos.clear();
                    if (searchAdapter != null)
                        searchAdapter.clear();
                    sameCityStart = 0;
                    if (TextUtils.isEmpty(city)) {
                        showAddressDialog();
                        break;
                    }
                    requestSearchData("同城", et_search.getText().toString(), "", city, sameCityStart, sameCityCount);
                    break;
            }

        }
    }

    /**
     * 获取地理位置
     */
    public void showAddressDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("是否获取城市信息?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("获取");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        if (isFirst) {
            //推荐
            recommendStart = recommendInfos.size();
            requestRecommend();
        } else {
            //筛选
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rb_all:
                    allStart = searchAllInfos.size();
                    requestSearchData("全部", et_search.getText().toString(), "", "", allStart, allCount);
                    break;
                case R.id.rb_teacher:
                    teacherStart = searchTeacherInfos.size();
                    requestSearchData("老师", et_search.getText().toString(), "1", "", teacherStart, teacherCount);
                    break;
                case R.id.rb_samecity:
                    sameCityStart = searchSameCityInfos.size();
                    requestSearchData("同城", et_search.getText().toString(), "", city, sameCityStart, sameCityCount);
                    break;
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://刷新完成
                    pullToRefreshListView.onRefreshComplete();
                    break;
                case 1:

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 快速输入时不加载选中项的详情
     */
    private void scheduleDismissOnScreenControls() {
        mHandler.removeCallbacks(mDismissOnScreenControlRunner);
        mHandler.postDelayed(mDismissOnScreenControlRunner, delayTime);//延迟

    }

    private Runnable mDismissOnScreenControlRunner = new Runnable() {
        @Override
        public void run() {
            //请求搜索接口
        }
    };

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String txt = et_search.getText().toString();
            if (!TextUtils.isEmpty(txt)) {
                //请求接口
                isFirst = false;
                searchStart = 0;
                lastKeyWord = txt;
                if (searchAdapter != null) {
                    searchAdapter.clear();
                    searchAdapter.setKeyWord(txt);
                }
                //筛选
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_all:
                        tv_header_title.setText("全部");
                        searchAllInfos.clear();
                        requestSearchData("全部", txt, "", "", searchStart, searchCount);
                        break;
                    case R.id.rb_teacher:
                        tv_header_title.setText("老师");
                        searchTeacherInfos.clear();
                        requestSearchData("老师", txt, "1", "", searchStart, searchCount);
                        break;
                    case R.id.rb_samecity:
                        tv_header_title.setText("同城");
                        searchSameCityInfos.clear();
                        if (TextUtils.isEmpty(city)) {
                            //判断是否登录，没有登录就弹出定位页面，登录了直接从登录信息里面取，没有则弹出定位页面
                            if (TextUtils.isEmpty(SPUtils.instance().getLoginEntity().get_id())) {
                                //未登录
                                Intent intentCity = new Intent(SearchActivity.this,
                                        EditCityActivity.class);
                                SearchActivity.this.startActivityForResult(intentCity, CALLBACK_CITY);
                                break;
                            } else {
                                if (!TextUtils.isEmpty(SPUtils.instance().getLoginEntity().getAddr())) {
                                    city = SPUtils.instance().getLoginEntity().getAddr();
                                } else {
                                    //未获取到城市信息
                                    Intent intentCity = new Intent(SearchActivity.this,
                                            EditCityActivity.class);
                                    SearchActivity.this.startActivityForResult(intentCity, CALLBACK_CITY);
                                    break;
                                }
                            }
                        }
                        requestSearchData("同城", txt, "", city, searchStart, searchCount);
                        break;
                }
                ((ClearEditText) findViewById(R.id.et_search)).requestFocus();
                DeviceUtils.closeKeyboard(et_search,
                        this);
            } else
                ToastUtil.show("请输入搜索条件");
            return true;
        }
        return false;
    }

    /**
     * 根据用户输入的条件请求接口
     */
    private void requestSearchData(String tag, String searchStr, String isTeacher, String addr, int start, int count) {
        RequestManager.request(this, tag, new SearchParams(TextUtils.isEmpty(searchStr) ? "" : searchStr, isTeacher, addr, "" + start, "" + count),
                SearchResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 获取推荐用户
     */
    private void requestRecommend() {
        RequestManager.request(this, new RecommendParams("" + recommendStart, "" + recommendCount),
                RecommendResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof SearchResponse) {
                SearchResponse searchResponseTemp = (SearchResponse) response;
                pullToRefreshListView.setAdapter(searchAdapter);
                if (searchResponseTemp.getRepBody().getList() != null) {
                    if (!searchResponseTemp.getRepBody().getList().isEmpty()) {
                        if (TextUtils.equals("全部", tag)) {
                            searchAllInfos.addAll(searchResponseTemp.getRepBody().getList());
                        } else if (TextUtils.equals("老师", tag)) {
                            searchTeacherInfos.addAll(searchResponseTemp.getRepBody().getList());
                        } else if (TextUtils.equals("同城", tag)) {
                            searchSameCityInfos.addAll(searchResponseTemp.getRepBody().getList());
                        }
                    } else {
                        ToastUtil.show("暂无更多!");
                    }
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.rb_all:
                            searchAdapter.setData(searchAllInfos);
                            break;
                        case R.id.rb_teacher:
                            searchAdapter.setData(searchTeacherInfos);
                            break;
                        case R.id.rb_samecity:
                            searchAdapter.setData(searchSameCityInfos);
                            break;
                    }
                } else {
                    ToastUtil.show("暂无更多!");
                }
            } else if (response instanceof EditMyInfoResponse) {
                SPUtils.instance().getLoginEntity().setAddr(city);
                SPUtils.instance().put(SPUtils.LOGIN_DATA,
                        new Gson().toJson(SPUtils.instance().getLoginEntity()));
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
        mHandler.sendEmptyMessage(0);
        dlgLoad.dismissDialog();
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof RecommendResponse) {
                if (isFirst) {
                    RecommendResponse recommendResponseTemp = (RecommendResponse) response;
                    pullToRefreshListView.setAdapter(recommendAdapter);
                    if (recommendResponseTemp.getRepBody().getList() != null) {
                        if (!recommendResponseTemp.getRepBody().getList().isEmpty()) {
                            recommendInfos.addAll(recommendResponseTemp.getRepBody().getList());
                            recommendAdapter.setData(recommendInfos);
                        } else {
                            ToastUtil.show("暂无更多!");
                        }
                    } else {
                        ToastUtil.show("暂无更多!");
                    }
                }
            }
            mHandler.sendEmptyMessage(0);
            dlgLoad.dismissDialog();
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }
}
