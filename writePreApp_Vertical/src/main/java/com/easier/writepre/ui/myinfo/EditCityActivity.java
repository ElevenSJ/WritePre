package com.easier.writepre.ui.myinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.easier.writepre.R;
import com.easier.writepre.db.DBCityHelper;
import com.easier.writepre.db.DatabaseCityHelper;
import com.easier.writepre.entity.City;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.manager.CityDataManager;
import com.easier.writepre.response.SocialInfoGetResponse;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.PingYinUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.widget.ClearEditText;
import com.easier.writepre.widget.MyLetterListView;
import com.easier.writepre.widget.MyLetterListView.OnTouchingLetterChangedListener;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditCityActivity extends BaseActivity implements OnScrollListener {

    private ImageView img_back;
    private BaseAdapter adapter;
    private ResultListAdapter resultListAdapter;
    private ListView personList;
    private ListView resultList;
    private TextView overlay; // 对话框首字母textview
    private MyLetterListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private OverlayThread overlayThread; // 显示首字母对话框
    private ArrayList<City> allCity_lists; // 所有城市列表
    private ArrayList<City> city_lists;// 城市列表
    private ArrayList<City> city_hot;// 热门城市列表
    private ArrayList<City> city_result;// 搜索的结果城市列表
    private ArrayList<String> city_history;// 最近城市列表
    private ClearEditText sh;
    private TextView tv_noresult;

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    private String currentCity; // 用于保存定位到的城市
    private int locateProcess = 1; // 记录当前定位的状态 正在定位-定位成功-定位失败
    private boolean isNeedFresh;

    // private DatabaseCityHelper helper;
    protected String selected_city;

    // private String city_url;
    protected Double longitude = 0.0;
    protected Double latitude = 0.0;

    private SocialPropEntity entity;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (isDestroyed) {
                return;
            }
            dlgLoad.dismissDialog();
            switch (msg.what) {
                case CityDataManager.INSERT_OK:
                    SPUtils.instance().put(SPUtils.CITY_DATA_URL, entity.getCity_url());
                    initView();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_city);

        insertCityData();

    }


    protected void initView() {
        setTopTitle("选择城市");

        // city_url = sp.getStringValue("city_url", "");
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        personList = (ListView) findViewById(R.id.list_view);
        allCity_lists = new ArrayList<City>();
        city_hot = new ArrayList<City>();
        city_result = new ArrayList<City>();
        city_history = new ArrayList<String>();
        resultList = (ListView) findViewById(R.id.search_result);
        sh = (ClearEditText) findViewById(R.id.sh);
        tv_noresult = (TextView) findViewById(R.id.tv_noresult);
        // helper = new DatabaseCityHelper(this);
        sh.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() == null || "".equals(s.toString())) {
                    letterListView.setVisibility(View.VISIBLE);
                    personList.setVisibility(View.VISIBLE);
                    resultList.setVisibility(View.GONE);
                    tv_noresult.setVisibility(View.GONE);
                } else {
                    city_result.clear();
                    letterListView.setVisibility(View.GONE);
                    personList.setVisibility(View.GONE);
                    getResultCityList(s.toString());
                    if (city_result.size() <= 0) {
                        tv_noresult.setVisibility(View.VISIBLE);
                        resultList.setVisibility(View.GONE);
                    } else {
                        tv_noresult.setVisibility(View.GONE);
                        resultList.setVisibility(View.VISIBLE);
                        resultListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<String, Integer>();
        overlayThread = new OverlayThread();
        isNeedFresh = true;
        personList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 4) {

//					Toast.makeText(getApplicationContext(), allCity_lists.get(position).getSname(), Toast.LENGTH_SHORT)
//							.show();
                    selected_city = allCity_lists.get(position).getSname();
                    longitude = allCity_lists.get(position).getLongitude();
                    latitude = allCity_lists.get(position).getLatitude();
                    // 设置返回值
                    setResultFinish(RESULT_OK);
                }
            }
        });
        locateProcess = 1;
        personList.setAdapter(adapter);
        personList.setOnScrollListener(this);
        resultListAdapter = new ResultListAdapter(this, city_result);
        resultList.setAdapter(resultListAdapter);
        resultList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Toast.makeText(getApplicationContext(), city_result.get(position).getSname(), Toast.LENGTH_SHORT)
//						.show();
                selected_city = city_result.get(position).getSname();
                // 设置返回值
                setResultFinish(RESULT_OK);
            }
        });

        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // handler.postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        // dlgLoad.loading();
        // }
        // }, 0);
        //
        // }
        // }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // parseData();
                        initOverlay();
                        cityInit();
                        hotCityInit();
                        // hisCityInit();
                        setAdapter(allCity_lists, city_hot, city_history);

                        mLocationClient = new LocationClient(EditCityActivity.this.getApplicationContext());
                        mMyLocationListener = new MyLocationListener();
                        mLocationClient.registerLocationListener(mMyLocationListener);
                        InitLocation();
                        mLocationClient.start();
                    }
                }, 1000);

            }
        }).start();

    }

    private void insertCityData() {
        dlgLoad.loading();
        entity = SPUtils.instance().getSocialPropEntity();
        if (entity.getCity_url().equals((String) SPUtils.instance().get(SPUtils.CITY_DATA_URL, ""))) {
            handler.sendEmptyMessage(CityDataManager.INSERT_OK);
        } else {
            CityDataManager.getInstance(EditCityActivity.this, handler).getCityData(entity.getCity_url());
        }
    }

    /**
     * 读取本地文件中JSON字符串
     */
    // private String getJson(String path) {
    //
    // String result = "";
    //
    // try {
    // FileInputStream f = new FileInputStream(path);
    // BufferedReader bis = new BufferedReader(new InputStreamReader(f));
    // String line = "";
    // while ((line = bis.readLine()) != null) {
    // result += line;
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result;
    //
    // }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == 4) {
            finish();
        }
        // }else{
        // if(sh.getText().toString().trim().isEmpty()){
        // finish();
        // }
        // }
        return true;
    }

    protected void setResultFinish(int resultCode) {
        // TODO Auto-generated method stub
        Intent intent2 = new Intent();
        intent2.putExtra("city", selected_city);
        intent2.putExtra("longitude", longitude);
        intent2.putExtra("latitude", latitude);
        setResult(resultCode, intent2);
        onTopLeftClick(null);
    }

    // public void InsertCity(String name) {
    // SQLiteDatabase db = helper.getReadableDatabase();
    // Cursor cursor = db.rawQuery("select * from recentcity where name = '"
    // + name + "'", null);
    // if (cursor.getCount() > 0) { //
    // db.delete("recentcity", "name = ?", new String[] { name });
    // }
    // db.execSQL("insert into recentcity(name, date) values('" + name + "', "
    // + System.currentTimeMillis() + ")");
    // db.close();
    // }

    private void InitLocation() {
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
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

    private void cityInit() {
        City city = new City("定位", "0"); // 当前定位城市
        allCity_lists.add(city);
        city = new City("最近", "1"); // 最近访问的城市
        allCity_lists.add(city);
        city = new City("热门", "2"); // 热门城市
        allCity_lists.add(city);
        city = new City("全部", "3"); // 全部城市
        allCity_lists.add(city);
        city_lists = getCityList();
        allCity_lists.addAll(city_lists);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        City city = new City("北京", "2", 116.39564503788, 39.92998577808);
        city_hot.add(city);
        city = new City("上海", "2", 121.48789948569, 31.249161710015);
        city_hot.add(city);
        city = new City("深圳", "2", 114.02597365732, 22.546053546205);
        city_hot.add(city);
        city = new City("杭州", "2", 120.21937541572, 30.259244461536);
        city_hot.add(city);
        city = new City("广州", "2", 113.30764967515, 23.120049102076);
        city_hot.add(city);
    }

    // private void hisCityInit() {
    // // AseoZdpAseo.initFinalTimer(this, AseoZdpAseo.BOTH_TYPE);
    // SQLiteDatabase db = helper.getReadableDatabase();
    // Cursor cursor = db.rawQuery(
    // "select * from recentcity order by date desc limit 0, 3", null);
    // while (cursor.moveToNext()) {
    // city_history.add(cursor.getString(1));
    // }
    // cursor.close();
    // db.close();
    // }

    // private void insertCity(final List<CitysBean> citysList) {
    //
    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // DBHelper dbHelper = new DBHelper(EditCityActivity.this);
    // try {
    // dbHelper.createDataBase();
    // SQLiteDatabase db = dbHelper.getWritableDatabase();
    // db.execSQL("delete from city");
    // for (int i = 0; i < citysList.size(); i++) {
    // db.execSQL("insert into
    // city(id,bcode,longitude,latitude,full_spell,min_spell,name,sname)
    // values('"
    // + (i + 1)
    // + "','"
    // + citysList.get(i).getBcode()
    // + "','"
    // + citysList.get(i).getCoord().get(0)
    // + "','"
    // + citysList.get(i).getCoord().get(1)
    // + "','"
    // + citysList.get(i).getFull_spell()
    // + "','"
    // + citysList.get(i).getMin_spell()
    // + "','"
    // + citysList.get(i).getName()
    // + "','"
    // + citysList.get(i).getSname() + "')");
    // dlgLoad.dismissDialog();
    // }
    // db.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // }
    // }).start();
    //
    // }

    @SuppressWarnings("unchecked")
    private ArrayList<City> getCityList() {

        DBCityHelper dbHelper = new DBCityHelper(this);
        ArrayList<City> list = new ArrayList<City>();
        try {
            dbHelper.createDataBase();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from city", null);
            City city;
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(7), cursor.getString(4));
                list.add(city);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(list, comparator);
        return list;
    }

    @SuppressWarnings("unchecked")
    private void getResultCityList(String keyword) {
        DBCityHelper dbHelper = new DBCityHelper(this);
        try {
            dbHelper.createDataBase();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("select * from city where sname like \"%" + keyword
                    + "%\" or full_spell like \"%" + keyword + "%\"", null);
            City city;
            LogUtils.e("length = " + cursor.getCount());
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(7), cursor.getString(4));
                city_result.add(city);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(city_result, comparator);
    }

    /**
     * a-z排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getFull_spell().substring(0, 1);
            String b = rhs.getFull_spell().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    private void setAdapter(List<City> list, List<City> hotList, List<String> hisCity) {
        adapter = new ListAdapter(this, list, hotList, hisCity);
        personList.setAdapter(adapter);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            LogUtils.e("city = " + arg0.getCity());
            if (!isNeedFresh) {
                return;
            }
            isNeedFresh = false;
            if (arg0.getCity() == null) {
                locateProcess = 3; // 定位失败
                personList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return;
            }
            currentCity = arg0.getCity().substring(0, arg0.getCity().length() - 1);
            longitude = arg0.getLongitude();
            latitude = arg0.getLatitude();
            // LogUtils.e("当前定位的经纬度", "["+longitude+","+latitude+"]");
            locateProcess = 2; // 定位成功
            personList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        // @Override
        // public void onReceivePoi(BDLocation arg0) {
        //
        // }
    }

    private class ResultListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<City> results = new ArrayList<City>();

        public ResultListAdapter(Context context, ArrayList<City> results) {
            inflater = LayoutInflater.from(context);
            this.results = results;
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(results.get(position).getSname());
            return convertView;
        }

        class ViewHolder {
            TextView name;
        }
    }

    public class ListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<City> list;
        private List<City> hotList;
        private List<String> hisCity;
        final int VIEW_TYPE = 5;

        public ListAdapter(Context context, List<City> list, List<City> hotList, List<String> hisCity) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            this.context = context;
            this.hotList = hotList;
            this.hisCity = hisCity;
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = getAlpha(list.get(i).getFull_spell());
                // 上一个汉语拼音首字母，如果不存在为" "
                String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1).getFull_spell()) : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = getAlpha(list.get(i).getFull_spell());
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            return position < 4 ? position : 4;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView city;
            int viewType = getItemViewType(position);
            if (viewType == 0) { // 定位
                convertView = inflater.inflate(R.layout.frist_list_item, null);
                TextView locateHint = (TextView) convertView.findViewById(R.id.locateHint);
                city = (TextView) convertView.findViewById(R.id.lng_city);
                city.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateProcess == 2) {

//							Toast.makeText(getApplicationContext(), city.getText().toString(), Toast.LENGTH_SHORT)
//									.show();
                            selected_city = city.getText().toString();
                            setResultFinish(RESULT_OK);
                        } else if (locateProcess == 3) {
                            locateProcess = 1;
                            personList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mLocationClient.stop();
                            isNeedFresh = true;
                            InitLocation();
                            currentCity = "";
                            mLocationClient.start();
                        }
                    }
                });
                ProgressBar pbLocate = (ProgressBar) convertView.findViewById(R.id.pbLocate);
                if (locateProcess == 1) { // 正在定位
                    locateHint.setText("正在定位");
                    city.setVisibility(View.GONE);
                    pbLocate.setVisibility(View.VISIBLE);
                } else if (locateProcess == 2) { // 定位成功
                    locateHint.setText("当前定位城市");
                    city.setVisibility(View.VISIBLE);
                    city.setText(currentCity);
                    mLocationClient.stop();
                    pbLocate.setVisibility(View.GONE);
                } else if (locateProcess == 3) {
                    locateHint.setText("未定位到城市,请选择");
                    city.setVisibility(View.VISIBLE);
                    city.setText("重新选择");
                    pbLocate.setVisibility(View.GONE);
                }
            } else if (viewType == 1) { // 最近访问城市
                convertView = inflater.inflate(R.layout.recent_city, null);
                GridView rencentCity = (GridView) convertView.findViewById(R.id.recent_city);
                rencentCity.setAdapter(new HitCityAdapter(context, this.hisCity));
                rencentCity.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//						Toast.makeText(getApplicationContext(), city_history.get(position), Toast.LENGTH_SHORT).show();
                        selected_city = city_history.get(position);
                        setResultFinish(RESULT_OK);
                    }
                });
                TextView recentHint = (TextView) convertView.findViewById(R.id.recentHint);
                recentHint.setText("最近访问的城市");
            } else if (viewType == 2) {
                convertView = inflater.inflate(R.layout.recent_city, null);
                GridView hotCity = (GridView) convertView.findViewById(R.id.recent_city);
                hotCity.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//						Toast.makeText(getApplicationContext(), city_hot.get(position).getSname(), Toast.LENGTH_SHORT)
//								.show();
                        selected_city = city_hot.get(position).getSname();
                        longitude = city_hot.get(position).getLongitude();
                        latitude = city_hot.get(position).getLatitude();
                        setResultFinish(RESULT_OK);
                    }
                });
                hotCity.setAdapter(new HotCityAdapter(context, this.hotList));
                TextView hotHint = (TextView) convertView.findViewById(R.id.recentHint);
                hotHint.setText("热门城市");
            } else if (viewType == 3) {
                convertView = inflater.inflate(R.layout.total_item, null);
            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position >= 1) {
                    holder.name.setText(list.get(position).getSname());
                    String currentStr = getAlpha(list.get(position).getFull_spell());
                    String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getFull_spell()) : " ";
                    if (!previewStr.equals(currentStr)) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        holder.alpha.setText(currentStr);
                    } else {
                        holder.alpha.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }

    @Override
    protected void onStop() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onStop();
    }

    class HotCityAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<City> hotCitys;

        public HotCityAdapter(Context context, List<City> hotCitys) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.hotCitys = hotCitys;
        }

        @Override
        public int getCount() {
            return hotCitys.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_city, null);
            TextView city = (TextView) convertView.findViewById(R.id.city);
            city.setText(hotCitys.get(position).getSname());
            return convertView;
        }
    }

    class HitCityAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<String> hotCitys;

        public HitCityAdapter(Context context, List<String> hotCitys) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.hotCitys = hotCitys;
        }

        @Override
        public int getCount() {
            return hotCitys.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_city, null);
            TextView city = (TextView) convertView.findViewById(R.id.city);
            city.setText(hotCitys.get(position));
            return convertView;
        }
    }

    private boolean mReady;

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        if (isDestroyed) {
            return;
        }
        mReady = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    private boolean isScroll = false;

    private class LetterListViewListener implements OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                personList.setSelection(position);
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1000);
            }
        }
    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    // 获得汉语拼音首字母
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else if (str.equals("0")) {
            return "定位";
        } else if (str.equals("1")) {
            return "最近";
        } else if (str.equals("2")) {
            return "热门";
        } else if (str.equals("3")) {
            return "全部";
        } else {
            return "#";
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!isScroll) {
            return;
        }

        if (mReady) {
            String text;
            String name = allCity_lists.get(firstVisibleItem).getSname();
            String pinyin = allCity_lists.get(firstVisibleItem).getFull_spell();
            if (firstVisibleItem < 4) {
                text = name;
            } else {
                text = PingYinUtil.converterToFirstSpell(pinyin).substring(0, 1).toUpperCase();
            }
            overlay.setText(text);
            overlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(overlayThread);
            // 延迟一秒后执行，让overlay为不可见
            handler.postDelayed(overlayThread, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.img_back:
                setResultFinish(RESULT_CANCELED);
                break;

            default:
                break;
        }
    }

}