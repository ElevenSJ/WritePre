package com.easier.writepre.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.easier.writepre.R;
import com.easier.writepre.adapter.FoundBeiTieCalligrapherGridViewAdapter;
import com.easier.writepre.db.DatabaseCityHelper;
import com.easier.writepre.entity.HotTagInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.FoundMainView;
import com.easier.writepre.param.ResBeiTieHotTagParams;
import com.easier.writepre.param.ResBeiTieListParams;
import com.easier.writepre.param.ResBeiTieSearchParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ResBeiTieHotTagResponse;
import com.easier.writepre.response.ResBeiTieListSearchResponse;
import com.easier.writepre.response.ResBeiTieListSearchResponse.ResBeiTieListSearchBody;
import com.easier.writepre.response.ResBeiTieListSearchResponse.ResBeiTieListSearchInfo;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;
import com.easier.writepre.widget.MyGridLayout;
import com.sj.autolayout.utils.AutoUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 碑帖
 */
public class FoundBeiTieCalligrapherActivity extends BaseActivity implements
        OnItemClickListener, TextView.OnEditorActionListener {

    private String link_url;

    private Intent intent;

    private String penmen_id; // 作家碑帖id

    private ClearEditText et_search;

    private GridView gridView;

    private String period_title = "";//朝代名称
    private String name = ""; // 书法家名字

    private DatabaseCityHelper helper;

    private FoundBeiTieCalligrapherGridViewAdapter adapter;

    private List<ResBeiTieListSearchInfo> list = new ArrayList<ResBeiTieListSearchInfo>();

    private LinearLayout ll_hot_beitie;
    private MyGridLayout labelTagViewGroup;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_calligrapher);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void init() {
        ll_hot_beitie = (LinearLayout) findViewById(R.id.ll_hot_beitie);
        labelTagViewGroup = (MyGridLayout) findViewById(R.id.flowLayout);
        gridView = (GridView) findViewById(R.id.found_calligrapher_gridview);
        gridView.setOnItemClickListener(this);
        helper = new DatabaseCityHelper(this);
        intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("SEARCH_TYPE", false)) {
                findViewById(R.id.rl_search_found_calligrapher).setVisibility(
                        View.VISIBLE);
                et_search = (ClearEditText) findViewById(R.id.et_search);
                et_search.setOnEditorActionListener(this);
                setTopTitle("经典碑帖搜索");
                requestHotTag();
            } else {
                period_title = intent.getStringExtra("period_title");
                link_url = intent.getStringExtra("link_url");
                penmen_id = intent.getStringExtra("penmen_id");
                name = intent.getStringExtra("name");
                setTopTitle(name);

                if (!TextUtils.isEmpty(penmen_id)) {
                    // setTopRight(R.drawable.calligrapher_jianjie);
                    if (!TextUtils.isEmpty(link_url)) {
                        setTopRightTxt("简介");
                    }
                    findViewById(R.id.top_right_txt).setOnClickListener(this);
                    //((TextView) findViewById(R.id.top_right_txt)).setTextColor(getResources().getColor(R.color.social_black));
                    RequestManager.request(this, new ResBeiTieListParams(
                                    penmen_id), ResBeiTieListSearchResponse.class,
                            this, Constant.URL);
                } else {
                    queryFacePic();  //离线碑帖
                }
            }
        }
        gridView.setFocusable(false);
    }

    /**
     * 获取热门碑帖搜索
     */
    private void requestHotTag() {
        RequestManager.request(this, new ResBeiTieHotTagParams(),
                ResBeiTieHotTagResponse.class, this, Constant.URL);
    }

    // 初始化标签
    private void initLabel(List<HotTagInfo> hotTagInfos) {
        if (hotTagInfos != null && hotTagInfos.size() > 0) {
            ll_hot_beitie.setVisibility(View.VISIBLE);
        } else {
            ll_hot_beitie.setVisibility(View.GONE);
            return;
        }
        for (int i = 0; i < hotTagInfos.size(); i++) {
            View markView = getLayoutInflater().inflate(R.layout.view_hottag, null);
            TextView name = (TextView) markView.findViewById(R.id.tv_hottag);
            name.setTag(hotTagInfos.get(i));
            name.setText(hotTagInfos.get(i).getBeitie_title());
            AutoUtils.autoSize(markView);
            labelTagViewGroup.addView(markView);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView temp = (TextView) v;
                    HotTagInfo hotTagInfo = (HotTagInfo) temp.getTag();
                    if (hotTagInfo != null) {
                        ResBeiTieListSearchInfo rbtsInfo = new ResBeiTieListSearchResponse().new ResBeiTieListSearchInfo();
                        Intent intent = new Intent(FoundBeiTieCalligrapherActivity.this,
                                FoundBeiTiePreviewActivity.class);
                        rbtsInfo.set_id(hotTagInfo.getBeitie_id());  // 20160520091854_19a_137
                        rbtsInfo.setCtime(hotTagInfo.getCtime());
                        rbtsInfo.setFace_url(hotTagInfo.getBeitie_face_url());
                        rbtsInfo.setLink_url(hotTagInfo.getBeitie_link_url());
                        rbtsInfo.setTitle(hotTagInfo.getBeitie_title());
                        rbtsInfo.setSort(hotTagInfo.getSort_num());
                        rbtsInfo.setPeriod("");
                        rbtsInfo.setMemo("");
                        rbtsInfo.setPenmen_id("");
                        rbtsInfo.setPenmen_name("");
                        intent.putExtra("beitie_entity", rbtsInfo);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (TextUtils.isEmpty(penmen_id)) {
        // list.clear();
        // queryFacePic();
        // }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_search:
                if (TextUtils.isEmpty(et_search.getText().toString().trim())) {
                    ToastUtil.show("请输入搜索条件");
                } else {
                    DeviceUtils.closeKeyboard(v,
                            FoundBeiTieCalligrapherActivity.this);
                    RequestManager.request(this, new ResBeiTieSearchParams(
                                    et_search.getText().toString().trim()),
                            ResBeiTieListSearchResponse.class, this, Constant.URL);
                }
                break;

            case R.id.top_right_txt: // top_right
                Intent intent = new Intent(this, BannerLinkActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("url", link_url);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onTopRightClick(View view) {
        super.onTopRightClick(view);
    }

    private void queryFacePic() { // 离线碑帖
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select distinct id,title,face_url from pic", null);
        /*
         * Cursor cursor = DBHelper.instance().query(
		 * "select distinct id,title,face_url from pic");
		 */

        while (cursor.moveToNext()) {
            ResBeiTieListSearchInfo rbtlsInfo = new ResBeiTieListSearchResponse().new ResBeiTieListSearchInfo();
            rbtlsInfo.set_id(cursor.getString(0));
            rbtlsInfo.setTitle(cursor.getString(1));
            rbtlsInfo.setFace_url(cursor.getString(2));
            rbtlsInfo.setPenmen_name(name);
            rbtlsInfo.setPeriod(period_title);
            list.add(rbtlsInfo);
        }
        adapter = new FoundBeiTieCalligrapherGridViewAdapter(this,
                list);
        gridView.setAdapter(adapter);
        cursor.close();
        db.close(); // db.close();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(FoundBeiTieCalligrapherActivity.this,
                FoundBeiTiePreviewActivity.class);
        if (TextUtils.isEmpty(list.get(arg2).getPeriod())) {
            list.get(arg2).setPeriod(period_title);
        }
        intent.putExtra("beitie_entity", (Serializable) list.get(arg2));
        intent.putExtra("index", arg2);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        } else {
            if (adapter != null) {
                list.remove(data.getIntExtra("index", 0));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof ResBeiTieListSearchResponse) {
                ResBeiTieListSearchResponse mResBeiTieListSearchResponse = (ResBeiTieListSearchResponse) response;
                if (mResBeiTieListSearchResponse != null) {
                    ResBeiTieListSearchBody mBeiTieListSearchBody = mResBeiTieListSearchResponse
                            .getRepBody();
                    if (mBeiTieListSearchBody != null) {
                        list = mBeiTieListSearchBody.getList();
                        if (list != null && list.size() > 0) {
                            ll_hot_beitie.setVisibility(View.GONE);
                            adapter = new FoundBeiTieCalligrapherGridViewAdapter(
                                    this, list);
                            gridView.setAdapter(adapter);
                        } else {
                            //ToastUtil.show("未查到数据");
                        }
                    }

                }
            } else if (response instanceof ResBeiTieHotTagResponse) {
                ResBeiTieHotTagResponse resBeiTieHotTagResponse = (ResBeiTieHotTagResponse) response;
                if (resBeiTieHotTagResponse != null)
                    initLabel(resBeiTieHotTagResponse.getRepBody().getList());

            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String txt = et_search.getText().toString();
            if (!TextUtils.isEmpty(txt)) {
                umengSearch(txt);
                RequestManager.request(this, new ResBeiTieSearchParams(
                                et_search.getText().toString().trim()),
                        ResBeiTieListSearchResponse.class, this, Constant.URL);
                ((ClearEditText) findViewById(R.id.et_search_transfer)).requestFocus();
                DeviceUtils.closeKeyboard(et_search,
                        FoundBeiTieCalligrapherActivity.this);
            } else
                ToastUtil.show("请输入搜索条件");
            return true;
        }
        return false;
    }


    /**
     * 友盟统计搜索
     *
     * @param txt
     */
    private void umengSearch(String txt) {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FOR));
        var.add(FoundMainView.CONTENT[FoundMainView.index]);
        var.add("搜索");
        YouMengType.onEvent(FoundBeiTieCalligrapherActivity.this, var, 1, txt);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
