package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkTeacherStudentListAdapter;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PkWorksSearchParams;
import com.easier.writepre.param.ResBeiTieSearchParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkWorksQueryStudentResponse;
import com.easier.writepre.response.ResBeiTieListSearchResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ClearEditText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 大赛作品搜索界面
 *
 * @author kai.zhong
 */
@SuppressLint("CutPasteId")
public class PkTeacherStudentSearchActivity extends BaseActivity implements TextView.OnEditorActionListener {

    String pk_id;
    String pk_type;

    private Handler handler;

    private Button btn_search;

    private ClearEditText et_search;

    private ImageView img_back;

    private ArrayList<PkContentInfo> list;

    // private SharedPrenfenceUtil sp;

    private PullToRefreshListView listView;

    private ArrayList<ImageView[]> m_list_imag;

    private PkTeacherStudentListAdapter adapter;

    private String last_id = "9", city = "", key_words = "";

    // private String serverUrl, imagePath, headPath, share_baseurl;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acticity_pk_teacher_student_search);
        init();
    }

    private void init() {
        pk_id = getIntent().getStringExtra("pk_id");
        pk_type= getIntent().getStringExtra("pk_type");
        // sp = new SharedPrenfenceUtil(this);
        handler = new Handler(Looper.getMainLooper());
        img_back = (ImageView) findViewById(R.id.img_back);
        btn_search = (Button) findViewById(R.id.btn_search);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        listView = (PullToRefreshListView) findViewById(R.id.list_pk_teacher_student_search);
        et_search.setOnEditorActionListener(this);
        img_back.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        et_search.setOnClickListener(this);
        list = new ArrayList<PkContentInfo>();
        m_list_imag = new ArrayList<ImageView[]>();
        // headPath = sp.getStringValue("group_info", ""); //基类里有
        // imagePath = sp.getStringValue("xzposs_info", "");
        // serverUrl = sp.getStringValue("app_socail_server_info", "");
        // share_baseurl = sp.getStringValue("share_baseurl_info", "");

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                if (!et_search.getText().toString().trim().equals("")) {
                    loadNews();
                } else
                    stopRefresh();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                if (!et_search.getText().toString().trim().equals("")) {
                    loadOlds();
                } else
                    stopRefresh();
            }

        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(PkTeacherStudentSearchActivity.this,
                        PkTeacherStudentInfoActivity.class);
                intent.putExtra("works_id", list.get(arg2 - 1).get_id());
                startActivity(intent);
            }
        });
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                list.clear();
                m_list_imag.clear();
                adapter = null;
                loadSearchData(pk_id,last_id, city, key_words);
            }
        }, 300);
    }

    /**
     * 加载更多
     */
    protected void loadOlds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                if (list != null && list.size() > 0) {
                    loadSearchData(pk_id,list.get(list.size() - 1).getUptime(), city,
                            key_words);
                }
            }
        }, 300);
    }

    protected void stopRefresh() {
        listView.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onTopLeftClick(v);
                break;

            case R.id.btn_search:
                if (et_search.getText().toString().trim().equals("")) {
                    ToastUtil.show("请正确输入搜索条件");
                } else {
                    key_words = et_search.getText().toString().trim();
                    if (list.size() > 0) {
                        list.clear();
                    }
                    loadSearchData(pk_id,last_id, city, key_words);
                    DeviceUtils.closeKeyboard(v,
                            PkTeacherStudentSearchActivity.this);
                }
                break;

            default:
                break;
        }

    }

    /**
     * 作品搜索
     */
    private void loadSearchData(String pk_id,String last_id, String city, String key_words) {
        RequestManager.request(this, new PkWorksSearchParams(pk_id,last_id, city,
                key_words, 2), PkWorksQueryStudentResponse.class, this, SPUtils
                .instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkWorksQueryStudentResponse) {
                PkWorksQueryStudentResponse pkqsResult = (PkWorksQueryStudentResponse) response;
                if (pkqsResult != null) {
                    PkWorksQueryStudentResponse.Repbody body = pkqsResult
                            .getRepBody();

                    if (body.getList().size() == 0) {
                        ToastUtil.show("未查询到结果");
                        return;
                    }

                    for (int i = 0; i < body.getList().size(); i++) {
                        list.add(body.getList().get(i));
                        if (body.getList().get(i).getWorks().getImgs() != null) {
                            ImageView[] iv = new ImageView[body.getList()
                                    .get(i).getWorks().getImgs().length];
                            m_list_imag.add(iv);
                        } else {
                            ImageView[] iv = new ImageView[7];
                            m_list_imag.add(iv);
                        }
                    }
                    if (adapter == null) {
                        adapter = new PkTeacherStudentListAdapter(
                                PkTeacherStudentSearchActivity.this, pk_id,pk_type,list,
                                m_list_imag, 0);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String key_words = et_search.getText().toString();
            if (!TextUtils.isEmpty(key_words)) {
                if (list.size() > 0) {
                    list.clear();
                }
                loadSearchData(pk_id,last_id, city, key_words);
                DeviceUtils.closeKeyboard(et_search,
                        PkTeacherStudentSearchActivity.this);
            } else
                ToastUtil.show("请输入搜索条件");
            return true;
        }
        return false;
    }
}
