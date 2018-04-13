package com.easier.writepre.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.easier.writepre.R;
import com.easier.writepre.adapter.SearchAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.SearchInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.EditInfoParam;
import com.easier.writepre.param.SearchParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.EditMyInfoResponse;
import com.easier.writepre.response.SearchResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.SearchFragmentActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索同城
 *
 * @author zhoulu
 */
public class SearchSameCityFragment extends BaseFragment implements
        OnRefreshListener2<ListView> {
    private PullToRefreshListView listView;
    private View headView;
    private TextView tv_header_title;
    private SearchAdapter searchAdapter;
    private int sameCityStart, sameCityCount = 10;
    private ArrayList<SearchInfo> searchSameCityInfos = new ArrayList<>();
    private SearchFragmentActivity searchFragmentActivity;
    private static final int CALLBACK_CITY = 100;
    private static final int CALLBACK_GPS = 200;
    private String city;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private String currentCity; // 用于保存定位到的城市
    private int locateProcess = 1; // 记录当前定位的状态 正在定位-定位成功-定位失败
    private boolean isNeedFresh;
    protected Double longitude = 0.0;
    protected Double latitude = 0.0;

    @Override
    public void onClick(View v) {
    }

    @Override
    public int getContextView() {
        // TODO Auto-generated method stub
        return R.layout.fragment_search;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:// 请求完成
                    listView.onRefreshComplete();
                    break;
                case 2:// 定位成功
                    ToastUtil.show((String) msg.obj);
                    if (!TextUtils.isEmpty(searchFragmentActivity.getEt_search().getText().toString())) {
                        loadRefresh();
                    }
                    break;
                case 3:// 定位失败
                    ToastUtil.show((String) msg.obj);
                    if (Utils.getGPSIsOPen(getActivity())) {
                        showAddressDialog();
                    } else {
                        showSetGPSDialog();
                    }
                    break;
                case 4://重定位
                    LogUtils.e("重定位");
                    getCity();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALLBACK_GPS) {
            mHandler.sendEmptyMessage(4);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) { // false标识第一次加载 true说明已经加载数据

        }
    }

    private void addHeader() {
        headView = View.inflate(getActivity(), R.layout.list_header_search, null);
        tv_header_title = (TextView) headView.findViewById(R.id.tv_header_title);
        listView.getRefreshableView().addHeaderView(headView);
        tv_header_title.setText("同城");
    }

    @Override
    protected void init() {
        searchFragmentActivity = (SearchFragmentActivity) getActivity();
        if (!TextUtils.isEmpty(SPUtils.instance().getLoginEntity().getAddr().trim())) {
            city = SPUtils.instance().getLoginEntity().getAddr();
        } else {
            city = WritePreApp.CITY;
        }
        if (TextUtils.isEmpty(city)) {
            if (Utils.getGPSIsOPen(getActivity())) {
                getCity();
            } else {
                showSetGPSDialog();
            }
        }
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setMode(Mode.BOTH);
        addHeader();
        searchAdapter = new SearchAdapter(getActivity());
        listView.setAdapter(searchAdapter);
        searchAdapter.setData(searchSameCityInfos);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 2) {
                            return;
                        }
                        searchFragmentActivity.inUserInfo(((SearchInfo) searchAdapter.getItem(position - 2)).get_id());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadMore();
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        if (searchSameCityInfos != null)
            searchSameCityInfos.clear();
        if (searchAdapter != null)
            searchAdapter.clear();
        sameCityStart = 0;
        if (TextUtils.isEmpty(city)) {
            getCity();
            return;
        }
        requestSearchData("同城", searchFragmentActivity.getLastKeyWord(), "", city, sameCityStart, sameCityCount);
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        if (TextUtils.isEmpty(city)) {
            getCity();
            return;
        }
        sameCityStart = searchSameCityInfos.size();
        requestSearchData("同城", searchFragmentActivity.getLastKeyWord(), "", city, sameCityStart, sameCityCount);
    }

    /**
     * 根据用户输入的条件请求接口
     */
    private void requestSearchData(String tag, String searchStr, String isTeacher, String addr, int start, int count) {
        RequestManager.request(getActivity(), tag, new SearchParams(TextUtils.isEmpty(searchStr) ? "" : searchStr, isTeacher, addr, "" + start, "" + count),
                SearchResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 对外提供的关键字搜索方法
     *
     * @param searchKey
     */
    public void doSearchAction(String searchKey) {
        if (TextUtils.isEmpty(searchKey) && !searchSameCityInfos.isEmpty()) {

        } else {
            //请求接口
            if (searchAdapter != null) {
                searchAdapter.clear();
                searchAdapter.setKeyWord(searchKey);
            }
            tv_header_title.setText("同城");
            searchSameCityInfos.clear();
            sameCityStart = 0;
            if (TextUtils.isEmpty(city)) {
                getCity();
                return;
            }
            requestSearchData("同城", searchKey, "", city, sameCityStart, sameCityCount);
        }
    }

    /**
     * 对外提供的清除方法
     */
    public void doSearchClear() {
        //请求接口
        if (searchAdapter != null) {
            searchAdapter.clear();
            searchAdapter.setKeyWord("");
        }
        tv_header_title.setText("同城");
        searchSameCityInfos.clear();
        sameCityStart = 0;
    }

    /**
     * 获取地理位置提示
     */
    public void showAddressDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("是否重新获取城市信息?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("获取");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getCity();
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
     * 引导用户去开启GPS
     */
    public void showSetGPSDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("是否去开启GPS?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("去开启");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, CALLBACK_GPS);
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

    private void getCity() {
        isNeedFresh = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        ToastUtil.show("正在获取位置信息...");
                        mLocationClient = new LocationClient(getActivity().getApplicationContext());
                        mMyLocationListener = new MyLocationListener();
                        mLocationClient.registerLocationListener(mMyLocationListener);
                        InitLocation();
                        mLocationClient.start();
                    }
                }, 100);

            }
        }).start();
    }


    private void InitLocation() {
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0); // 10分钟扫描1次
        // 需要地址信息，设置为其他任何值（string类型，且不能为null）时，都表示无地址信息。
        option.setAddrType("all");
        // 设置是否返回POI的电话和地址等详细信息。默认值为false，即不返回POI的电话和地址信息。
        // option.setPoiExtraInfo(true);
        // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setProdName("通过GPS定位我当前的位置");
        // 禁用启用缓存定位数据
        option.disableCache(true);
        // 设置最多可返回的POI个数，默认值为3。由于POI查询比较耗费流量，设置最多返回的POI个数，以便节省流量。
        // option.setPoiNumber(3);
        // 设置定位方式的优先级。
        // 当gps可用，而且获取了定位结果时，不再发起网络请求，直接返回给用户坐标。这个选项适合希望得到准确坐标位置的用户。如果gps不可用，再发起网络请求，进行定位。
        option.setPriority(LocationClientOption.GpsFirst);
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            LogUtils.e("city = " + arg0.getCity() + isNeedFresh);
            if (!isNeedFresh) {
                return;
            }
            isNeedFresh = false;
            if (arg0.getCity() == null) {
                locateProcess = 3; // 定位失败
                mHandler.obtainMessage(3, "定位失败!").sendToTarget();
                return;
            }
            currentCity = arg0.getCity().substring(0, arg0.getCity().length() - 1);
            longitude = arg0.getLongitude();
            latitude = arg0.getLatitude();
            locateProcess = 2; // 定位成功
            city = currentCity;
            WritePreApp.CITY = city;
            mHandler.obtainMessage(2, "获取位置信息成功: " + city).sendToTarget();
            if (!TextUtils.isEmpty(SPUtils.instance().getLoginEntity().get_id())) {
                //已登录，更新用户个人信息
                uploadMyInfo();
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

        RequestManager.request(getActivity(), "个人信息", new EditInfoParam(uname, birth_day, age,
                sex, addr, fav, interest, qq, bei_tie, fav_font, stu_time,
                school, company, profession, signature, email0, real_name,
                coord, goodat), EditMyInfoResponse.class, this, Constant.URL);
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof SearchResponse) {
                SearchResponse searchResponseTemp = (SearchResponse) response;
                listView.setAdapter(searchAdapter);
                int select = 0;
                int oldPosition = searchAdapter.getCount();
                if (searchResponseTemp.getRepBody().getList() != null) {
                    if (!searchResponseTemp.getRepBody().getList().isEmpty()) {
                        if (TextUtils.equals("同城", tag)) {
                            select = searchResponseTemp.getRepBody().getList().size();
                            searchSameCityInfos.addAll(searchResponseTemp.getRepBody().getList());
                        }
                    } else {
                        ToastUtil.show("暂无更多!");
                    }
                    searchAdapter.setData(searchSameCityInfos);
                } else {
                    ToastUtil.show("暂无更多!");
                }
                if (select != 0) {
                    if (searchAdapter.getCount() - select > 0) {
                        select = searchAdapter.getCount() - select;
                    }
                }
                if (oldPosition != 0) {
                    listView.getRefreshableView().setSelection(select);
                }
            } else if (response instanceof EditMyInfoResponse) {
                LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
                loginEntity.setAddr(city);
                SPUtils.instance().put(SPUtils.LOGIN_DATA,
                        new Gson().toJson(loginEntity));
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onDestroy();
    }
}
