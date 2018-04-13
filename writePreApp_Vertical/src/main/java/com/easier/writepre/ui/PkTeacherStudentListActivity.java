package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkTeacherStudentListAdapter;
import com.easier.writepre.adapter.PkTeacherStudentListPopAdapter;
import com.easier.writepre.entity.PkCategoryInfo;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PKWorksMyselfParams;
import com.easier.writepre.param.PkCategoryParams;
import com.easier.writepre.param.PkWorksGoodedParams;
import com.easier.writepre.param.PkWorksQueryParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkCategoryResponse;
import com.easier.writepre.response.PkCategoryResponse.PkCategoryBody;
import com.easier.writepre.response.PkWorksQueryStudentResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.MyGridView.OnTouchInvalidPositionListener;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 大选（点击教师专区、学生专区全部查看）
 *
 * @author kai.zhong
 */
@SuppressLint("InflateParams")
public class PkTeacherStudentListActivity extends NoSwipeBackActivity {

    private int dqIndex = 0; // 0全部 1其他城市

    private int jdIndex = 0; // 0全部 1初选 2 复选 3现场大会

    private int sqIndex = 0; // 0全部 1学生 2 教师

    public static int flag = 0; // 0全部 1人气 2我的

    private Intent intent;

    private Handler handler;

    private View customView;

    private ImageView img_back;

    private ArrayList<PkContentInfo> list;

    private PopupWindow popupwindow;

    private ImageView iv_pk_seacher, iv_corner;

    private LinearLayout ll_pk_title;

    // private String
    // SPUtils.instance().getSocialPropEntity().getApp_socail_server(),
    // imagePath, headPath, share_baseurl;

    private TextView tv_pk_role_division, tv_all, tv_vote_more, tv_my;

    private PullToRefreshListView listView;

    private ArrayList<ImageView[]> m_list_imag;

    private PkTeacherStudentListAdapter adapter;

    private String pk_id = "", pk_type = "", status = "", role = "", pk_cata_id = "", city = "", pk_cata_title = "";

    private RelativeLayout rl_all, rl_vote_more, rl_my;

    private List<PkCategoryInfo> numberListZQBean;

    private GridView gridViewJD, gridViewSQ;

    private MyGridView gridViewZQ, gridViewDQ;

    private ArrayList<String> numberListDQ, numberListJD, numberListSQ, numberListZQ;

    private PkTeacherStudentListPopAdapter seriesAdapterDQ, seriesAdapterJD, seriesAdapterSQ, seriesAdapterZQ;

    public final static int REQUEST_ZAN_UPDATE_CODE = 0x000001;

    public final static int REQUEST_UPDATE_CODE = 0x000002;

    public static boolean GIVE_OK; // true 刷新赞数+1

    public static boolean COMMENT; // true 刷新评论数+1

    public static int LIST_INDEX;

    private String pkStatus;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acticity_pk_teacher_student_list);
        customView = getLayoutInflater().inflate(R.layout.pk_teacher_student_list_pop, null, false);
        init();
        if (TextUtils.isEmpty(pk_type) || !pk_type.equals("view_pk")) {
            loadTitle(role); // 碑帖名
        }
    }

    private void init() {
        intent = getIntent();
        handler = new Handler(Looper.getMainLooper());
        gridViewZQ = (MyGridView) customView.findViewById(R.id.video_detail_series_gridview_zq); // 賽事专区
        numberListDQ = new ArrayList<String>();
        numberListJD = new ArrayList<String>();
        numberListSQ = new ArrayList<String>();
        numberListZQ = new ArrayList<String>();
        iv_corner = (ImageView) findViewById(R.id.iv_corner);
        rl_all = (RelativeLayout) findViewById(R.id.rl_all);
        rl_my = (RelativeLayout) findViewById(R.id.rl_my);
        rl_vote_more = (RelativeLayout) findViewById(R.id.rl_vote_more);
        img_back = (ImageView) findViewById(R.id.img_back);
        listView = (PullToRefreshListView) findViewById(R.id.list_pk_teacher_student);
        iv_pk_seacher = (ImageView) findViewById(R.id.iv_pk_seacher);
        ll_pk_title = (LinearLayout) findViewById(R.id.ll_pk_title);
        tv_pk_role_division = (TextView) findViewById(R.id.tv_pk_role_division);
        tv_all = (TextView) findViewById(R.id.tv_all);
        tv_vote_more = (TextView) findViewById(R.id.tv_vote_more);
        tv_my = (TextView) findViewById(R.id.tv_my);
        ll_pk_title.setOnClickListener(this);
        iv_pk_seacher.setOnClickListener(this);
        img_back.setOnClickListener(this);
        rl_my.setOnClickListener(this);
        rl_vote_more.setOnClickListener(this);
        rl_all.setOnClickListener(this);
        pk_id = intent.getStringExtra("pk_id");
        pk_type = intent.getStringExtra("pk_type");
        role = intent.getStringExtra("role");
        status = intent.getStringExtra("status");
        if (pk_type != null && pk_type.equals("view_pk")) {
            tv_pk_role_division.setText("全部作品");
            findViewById(R.id.iv_corner).setVisibility(View.GONE);
            flag = intent.getIntExtra("flag", 0);
            switch (flag) {
                case 0:
                    tv_all.setTextColor(getResources().getColor(R.color.social_red));
                    tv_vote_more.setTextColor(Color.BLACK);
                    tv_my.setTextColor(Color.BLACK);
                    loadAllData("9", status, role, "", "");
                    break;
                case 1:
                    tv_all.setTextColor(Color.BLACK);
                    tv_vote_more.setTextColor(getResources().getColor(R.color.social_red));
                    tv_my.setTextColor(Color.BLACK);
                    loadVoteMoreData(status, role, "", city, 0);
                    break;
                case 2:
                    tv_all.setTextColor(Color.BLACK);
                    tv_vote_more.setTextColor(Color.BLACK);
                    tv_my.setTextColor(getResources().getColor(R.color.social_red));
                    loadMyData();
                    break;
            }
        } else {
            if (status.equals("0")) {
                pkStatus = "初选";
            } else if (status.equals("1")) {
                pkStatus = "复选";
            } else {
                pkStatus = "现场大会";
            }
            if (role.equals("1")) {
                tv_pk_role_division.setText(pkStatus + "/学生专区");
                sqIndex = 2;
                loadAllData("9", status, role, "", ""); // 加载(教师专区、学生专区全部)
            } else if (role.equals("2")) {
                tv_pk_role_division.setText(pkStatus + "/教师专区");
                sqIndex = 1;
                loadAllData("9", status, role, "", ""); // 加载(教师专区、学生专区全部)
            } else {
                tv_pk_role_division.setText("全部作品");
                sqIndex = 0;
                role = "";
                tv_all.setTextColor(Color.BLACK);
                tv_vote_more.setTextColor(Color.BLACK);
                tv_my.setTextColor(getResources().getColor(R.color.social_red));
                flag = 2;
                loadMyData();
            }
        }
        // sqIndex = role.equals("1") ? 2 : 1;
        list = new ArrayList<PkContentInfo>();
        m_list_imag = new ArrayList<ImageView[]>();
        numberListZQBean = new ArrayList<PkCategoryInfo>();
        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadNews();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                loadOlds();
            }

        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent;
                if (flag == 2) {
                    intent = new Intent(PkTeacherStudentListActivity.this, PkWorksManageActivity.class);
                    intent.putExtra("pk_id", pk_id);
                    intent.putExtra("pk_type", pk_type);
                    intent.putExtra("manage_works_id", list.get(arg2 - 1).get_id());
                    startActivityForResult(intent, REQUEST_UPDATE_CODE);
                } else {
                    intent = new Intent(PkTeacherStudentListActivity.this, PkMsgDetailActivity.class);
                    intent.putExtra("pk_id", pk_id);
                    intent.putExtra("pk_type", pk_type);
                    intent.putExtra("data", list);
                    intent.putExtra("position", arg2 - 1);
                    intent.putExtra("status", status);
                    intent.putExtra("role", role);
                    intent.putExtra("pk_cata_id", pk_cata_id);
                    intent.putExtra("city", city);
                    intent.putExtra("flag", flag);
                    LIST_INDEX = arg2 - 1;
                    GIVE_OK = false;
                    COMMENT = false;
                    startActivityForResult(intent, REQUEST_ZAN_UPDATE_CODE);
                }
            }
        });

    }

    /**
     * 教师专区 和 学生专区碑帖名字
     */
    private void loadTitle(String role) {
        RequestManager.request(this, new PkCategoryParams(role), PkCategoryResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    // 全部接口
    private void loadAllData(String last_id, String status, String role, String pk_cata_id, String city) {
        RequestManager.request(this, new PkWorksQueryParams(pk_id, last_id, status, role, pk_cata_id, city, "10"),
                PkWorksQueryStudentResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 全部接口
    }

    // 人气接口
    private void loadVoteMoreData(String status, String role, String pk_cata_id, String city, int start) {
        RequestManager.request(this, new PkWorksGoodedParams(pk_id, status, role, pk_cata_id, city, start, 10),
                PkWorksQueryStudentResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 人气接口
    }

    // 我的
    public void loadMyData() {
        if (LoginUtil.checkLogin(PkTeacherStudentListActivity.this)) {
            RequestManager.request(this, new PKWorksMyselfParams(), PkWorksQueryStudentResponse.class, this,
                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
        } else {
            adapter = new PkTeacherStudentListAdapter(PkTeacherStudentListActivity.this, pk_id, pk_type, list, m_list_imag, flag);
            listView.setAdapter(adapter);
        }
        // else {
        // ToastUtil.show("请登录后再试~");
        // adapter = new PkTeacherStudentListAdapter(
        // PkTeacherStudentListActivity.this, list, m_list_imag,
        // imagePath, headPath, share_baseurl,
        // SPUtils.instance().getSocialPropEntity().getApp_socail_server(),
        // flag);
        // listView.setAdapter(adapter);
        // // startActivity(new Intent(PkTeacherStudentListActivity.this,
        // // SocialLoginActivity.class));
        // }
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                // list.clear();
                clear();
                if (flag == 0) {
                    loadAllData("9", status, role, pk_cata_id, city);
                } else if (flag == 1) {
                    loadVoteMoreData(status, role, pk_cata_id, city, 0);
                } else {
                    loadMyData();
                }
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
                    if (flag == 0) {
                        loadAllData(list.get(list.size() - 1).getUptime(), status, role, pk_cata_id, city);
                    } else if (flag == 1) {
                        loadVoteMoreData(status, role, pk_cata_id, city, list.size() + 1);
                    } else {

                    }
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
            case R.id.ll_pk_title:
                if (pk_type.equals("view_pk")) {
                    return;
                }
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    iv_corner.setBackgroundResource(R.drawable.screen_bottom);
                    return;
                } else {
                    popView();
                    iv_corner.setBackgroundResource(R.drawable.screen_top);
                    popupwindow.showAsDropDown(v, 0, 20);
                }
                break;
            case R.id.iv_pk_seacher:
                Intent intent = new Intent(this, PkTeacherStudentSearchActivity.class);
                intent.putExtra("pk_id", pk_id);
                intent.putExtra("pk_type", pk_type);
                startActivity(intent);
                break;

            case R.id.img_back:
                onTopLeftClick(v);
                break;
            case R.id.rl_my:
                tv_all.setTextColor(Color.BLACK);
                tv_vote_more.setTextColor(Color.BLACK);
                tv_my.setTextColor(getResources().getColor(R.color.social_red));
                clear();
                flag = 2;
                loadMyData();
                break;

            case R.id.rl_all:
                tv_all.setTextColor(getResources().getColor(R.color.social_red));
                tv_vote_more.setTextColor(Color.BLACK);
                tv_my.setTextColor(Color.BLACK);
                clear();
                flag = 0;
                loadAllData("9", status, role, pk_cata_id, city);
                break;

            case R.id.rl_vote_more:
                tv_all.setTextColor(Color.BLACK);
                tv_vote_more.setTextColor(getResources().getColor(R.color.social_red));
                tv_my.setTextColor(Color.BLACK);
                clear();
                flag = 1;
                loadVoteMoreData(status, role, pk_cata_id, city, 0);
                break;

            default:
                break;
        }

    }

    /**
     * 清除数据
     */
    public void clear() {
        if (list != null && list.size() > 0) {
            list.clear();
        }
        if (m_list_imag != null && m_list_imag.size() > 0) {
            m_list_imag.clear();
        }
        if (adapter != null) {
            adapter = null;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkCategoryResponse) {
                PkCategoryResponse mPkCategoryResponse = (PkCategoryResponse) response;
                PkCategoryBody mPkCategoryBody = mPkCategoryResponse.getRepBody();
                if (numberListZQBean.size() > 0) {
                    numberListZQBean.clear();
                }

                if (numberListZQ.size() >= 0) {
                    numberListZQ.clear();
                    numberListZQ.add("全部");
                }
                for (int i = 0; i < mPkCategoryBody.getList().size(); i++) {
                    numberListZQBean.add(mPkCategoryBody.getList().get(i));
                    numberListZQ.add(mPkCategoryBody.getList().get(i).getTitle());
                }
                seriesAdapterZQ = new PkTeacherStudentListPopAdapter(this, numberListZQ, 0);
                gridViewZQ.setAdapter(seriesAdapterZQ);

            } else if (response instanceof PkWorksQueryStudentResponse) {
                PkWorksQueryStudentResponse pkqsResult = (PkWorksQueryStudentResponse) response;
                if (pkqsResult != null) {
                    PkWorksQueryStudentResponse.Repbody body = pkqsResult.getRepBody();
                    for (int i = 0; i < body.getList().size(); i++) {
                        list.add(body.getList().get(i));
                        if (body.getList().get(i).getWorks().getImgs() == null
                                || body.getList().get(i).getWorks().getImgs().length == 0) {
                            ImageView[] iv = new ImageView[7];
                            m_list_imag.add(iv);
                        } else {
                            ImageView[] iv = new ImageView[body.getList().get(i).getWorks().getImgs().length];
                            m_list_imag.add(iv);
                        }
                    }
                    if (adapter == null) {
                        adapter = new PkTeacherStudentListAdapter(PkTeacherStudentListActivity.this, pk_id, pk_type, list, m_list_imag,
                                flag);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    /**
     * 弹出pop窗口
     */
    public void popView() {

        popupwindow = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupwindow.setAnimationStyle(R.style.AnimationFade);

        gridViewDQ = (MyGridView) customView.findViewById(R.id.video_detail_series_gridview_dq);// 所属区域
        gridViewJD = (GridView) customView.findViewById(R.id.video_detail_series_gridview_jd);// 賽事阶段
        gridViewSQ = (GridView) customView.findViewById(R.id.video_detail_series_gridview_sq);// 賽事专区

        if (numberListDQ.size() == 0) {
            numberListDQ.add("全部");
        }

        if (numberListJD.size() == 0) {
            numberListJD.add("全部");
            numberListJD.add("初选");
            numberListJD.add("复选");
            numberListJD.add("现场大会");
        }

        if (numberListSQ.size() == 0) {
            numberListSQ.add("全部");
            numberListSQ.add("教师专区");
            numberListSQ.add("学生专区");
        }

        if (numberListZQ.size() == 0) {
            numberListZQ.add("全部");
            for (int i = 0; i < numberListZQBean.size(); i++) {
                numberListZQ.add(numberListZQBean.get(i).getTitle());
            }
        }
        seriesAdapterDQ = new PkTeacherStudentListPopAdapter(this, numberListDQ, dqIndex);
        gridViewDQ.setAdapter(seriesAdapterDQ);

        seriesAdapterJD = new PkTeacherStudentListPopAdapter(this, numberListJD, jdIndex);
        gridViewJD.setAdapter(seriesAdapterJD);

        seriesAdapterSQ = new PkTeacherStudentListPopAdapter(this, numberListSQ, sqIndex);
        gridViewSQ.setAdapter(seriesAdapterSQ);

        gridViewDQ.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seriesAdapterDQ.clearSelection(position);
                seriesAdapterDQ.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        dqIndex = 0;
                        city = "";
                        break;
                    case 1:
                        city = numberListDQ.get(position);
                        dqIndex = 1;
                        break;
                }
            }
        });

        gridViewJD.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seriesAdapterJD.clearSelection(position);
                seriesAdapterJD.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        status = "";
                        jdIndex = 0;
                        break;
                    case 1:
                        status = "0";
                        jdIndex = 1;
                        break;
                    case 2:
                        status = "1";
                        jdIndex = 2;
                        break;
                    case 3:
                        status = "2";
                        jdIndex = 3;
                        break;

                }
            }
        });

        gridViewSQ.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seriesAdapterSQ.clearSelection(position);
                seriesAdapterSQ.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        role = "";
                        loadTitle(role);
                        sqIndex = 0;
                        break;
                    case 1:
                        role = "2";
                        pk_cata_title = "";
                        loadTitle(role);
                        sqIndex = 1;
                        break;
                    case 2:
                        role = "1";
                        pk_cata_title = "";
                        loadTitle(role);
                        sqIndex = 2;
                        break;
                }
            }
        });

        gridViewZQ.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seriesAdapterZQ.clearSelection(position);
                seriesAdapterZQ.notifyDataSetChanged();
                if (position == 0) {
                    pk_cata_id = "";
                    pk_cata_title = "";
                } else {
                    pk_cata_title = numberListZQBean.get(position - 1).getTitle();
                    pk_cata_id = numberListZQBean.get(position - 1).get_id();
                }
            }
        });

        gridViewDQ.setOnTouchInvalidPositionListener(new OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                Intent intent = new Intent(PkTeacherStudentListActivity.this, EditCityActivity.class);
                startActivityForResult(intent, 5);
                return false; // 不终止路由事件让父级控件处理事件
            }
        });

        customView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LogUtils.e("city", "--->" + city + "**");
                // LogUtils.e("status", "--->" + status + "**");
                // LogUtils.e("role", "--->" + role + "**");
                // LogUtils.e("pk_id", "--->" + pk_cata_id + "**");
                clear();
                if (flag == 0) {
                    loadAllData("9", status, role, pk_cata_id, city);
                } else if (flag == 1) {
                    loadVoteMoreData(status, role, pk_cata_id, city, 0);
                }

                String statusName = "", roleName = "";
                if (pk_cata_title.equals("") && role.equals("")) {
                    if (status.equals("0")) {
                        statusName = "初选";
                    } else if (status.equals("1")) {
                        statusName = "复选";
                    } else if (status.equals("2")) {
                        statusName = "现场大会";
                    }
                } else {
                    if (status.equals("0")) {
                        statusName = "初选/";
                    } else if (status.equals("1")) {
                        statusName = "复选/";
                    } else if (status.equals("2")) {
                        statusName = "现场大会/";
                    }
                }
                if (pk_cata_title.equals("")) {
                    if (role.equals("1")) {
                        roleName = "学生专区";
                    } else if (role.equals("2")) {
                        roleName = "教师专区";
                    }
                } else {
                    if (role.equals("1")) {
                        roleName = "学生专区/";
                    } else if (role.equals("2")) {
                        roleName = "教师专区/";
                    }
                }
                if (statusName.equals("") && roleName.equals("") && pk_cata_title.equals("")) { // &&
                    // city.equals("")
                    tv_pk_role_division.setText("全部作品");
                } else {
                    tv_pk_role_division.setText(statusName + roleName + pk_cata_title);
                }
                popupwindow.dismiss();
                iv_corner.setBackgroundResource(R.drawable.screen_bottom);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_UPDATE_CODE:
                if (resultCode == RESULT_OK) {
                    loadNews();
                }
                break;
            case REQUEST_ZAN_UPDATE_CODE:
                if (resultCode == RESULT_OK) {
                    if (list != null) {
                        if (data != null) {
                            LIST_INDEX = data.getIntExtra("position", LIST_INDEX);
                            if (data.getSerializableExtra("data") != null) {
                                list.clear();
                                list.addAll((ArrayList<PkContentInfo>) data.getSerializableExtra("data"));
                            }
                        }
                        if (LIST_INDEX >= list.size()) {
                            LIST_INDEX = list.size() - 1;
                        }
                        if (GIVE_OK) {
                            list.get(LIST_INDEX).setIs_ok("1");
                            list.get(LIST_INDEX).setOk_num(Integer.parseInt(list.get(LIST_INDEX).getOk_num()) + 1 + "");
                        }

                        if (COMMENT) {
                            list.get(LIST_INDEX)
                                    .setRemark_num(Integer.parseInt(list.get(LIST_INDEX).getRemark_num()) + 1 + "");
                        }

                        // if (GIVE_OK || COMMENT) {
                        adapter.notifyDataSetChanged();
                        // }
//					listView.getRefreshableView().setSelection(LIST_INDEX);
                    }
                }
                break;
            case 5:
                switch (resultCode) {
                    case RESULT_OK: // 获取城市成功:
                        city = data.getExtras().getString("city");
                        if (numberListDQ.size() > 1) {
                            numberListDQ.remove(1);
                        }
                        numberListDQ.add(city);
                        dqIndex = 1;
                        seriesAdapterDQ = new PkTeacherStudentListPopAdapter(this, numberListDQ, dqIndex);
                        gridViewDQ.setAdapter(seriesAdapterDQ);
                        break;
                    case RESULT_CANCELED: // 获取城市失败:

                        break;
                    default:
                        break;
                }
        }
    }
}
