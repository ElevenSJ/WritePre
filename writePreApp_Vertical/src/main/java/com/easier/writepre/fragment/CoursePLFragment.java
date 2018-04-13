package com.easier.writepre.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CoursePlAdapter;
import com.easier.writepre.entity.CoursePLList;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CoursePLListParams;
import com.easier.writepre.param.CoursePLParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CoursePLListResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.CourseCatalogActivityNew;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.CoursePLDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程评论
 *
 * @author sunjie
 */
public class CoursePLFragment extends BaseFragment {

    private PullToRefreshListView listView;

    private Handler mHandler = new Handler();

    private List<CoursePLList> plList = new ArrayList<CoursePLList>();

    private CoursePlAdapter adapter;

    private boolean isRefreash = true;

    private boolean isReMark = true;

    private RatingBar markRatingBar;

    private LinearLayout ratingLayout;

    private RatingBar levelRatingBar;

    private TextView levelTxt;

    private CoursePLDialog dialog;

    private boolean isTop = true;

    @Override
    public int getContextView() {
        return R.layout.fragment_course_pl;
    }

    @Override
    protected void init() {

        listView = (PullToRefreshListView) findViewById(R.id.listview);

        View headView = View.inflate(getActivity(), R.layout.course_pl_list_head, null);

        ratingLayout = (LinearLayout) headView.findViewById(R.id.rating_layout);
        markRatingBar = (RatingBar) headView.findViewById(R.id.ratingBar);
        levelRatingBar = (RatingBar) headView.findViewById(R.id.ratingBar_info);
        levelTxt = (TextView) headView.findViewById(R.id.pl_num);
        markRatingBar.setRating(0f);
        markRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                    showPLDialog(rating);
                } else {
                    LoginUtil.showLoginDialog(CoursePLFragment.this.getActivity());
                }


            }
        });

        listView.getRefreshableView().addHeaderView(headView);

        adapter = new CoursePlAdapter(getActivity());
        listView.setAdapter(adapter);

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            // 顶部下拉刷新
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                loadNew();
            }

            // 底部加载更多
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();

            }

        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    isTop = true;
                } else {
                    isTop = false;
                }
                ((CourseCatalogActivityNew) getActivity()).setChildViewScrollTop(isTop);
            }
        });
    }

    private void loadNew() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRefreash = true;
                listView.onRefreshComplete();
                getCoursePl("9", 10);
            }
        }, 300);
    }

    private void showPLDialog(float rating) {
        if (dialog == null) {
            dialog = new CoursePLDialog(getActivity(),
                    R.style.loading_dialog, new CoursePLDialog.OKDialogListener() {
                @Override
                public void OnClick(View view, float rating, String content) {
                    if (LoginUtil.checkLogin(getActivity())) {
                        //友盟统计
                        List<String> var = new ArrayList<String>();
                        var.add(YouMengType.getName(MainActivity.TYPE_ONE));
                        var.add("提交评论");
                        YouMengType.onEvent(getActivity(),var,1,CourseCatalogActivityNew.courseGroup+CourseCatalogActivityNew.courseName);
                        loadingDlg.loading();
                        RequestManager.request(getActivity(), new CoursePLParams(CourseCatalogActivityNew.courseId, content.trim(), (int) rating), BaseResponse.class, CoursePLFragment.this,
                                Constant.URL);
                    }

                }
            });
        }
        dialog.show(rating);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    /**
     * 上拉获取圈子动态数据
     */
    protected void loadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRefreash = false;
                listView.onRefreshComplete();
                if (plList != null && plList.size() > 0) {
                    getCoursePl(plList.get(plList.size() - 1).get_id(), 10);
                }
            }
        }, 300);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            ((CourseCatalogActivityNew) getActivity()).setChildViewScrollTop(isTop);
            //友盟统计
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_ONE));
            var.add("查看评论");
            YouMengType.onEvent(getActivity(),var,1,CourseCatalogActivityNew.courseGroup+CourseCatalogActivityNew.courseName);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadNew();
    }

    private void getCoursePl(String lastId, int count) {
        if (!TextUtils.isEmpty(CourseCatalogActivityNew.courseId)) {
            RequestManager.request(getActivity(), new CoursePLListParams(CourseCatalogActivityNew.courseId, lastId, count), CoursePLListResponse.class, this,
                    Constant.URL);
        }
    }


    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof CoursePLListResponse) {
                loadingDlg.dismissDialog();
                CoursePLListResponse coursePLResult = (CoursePLListResponse) response;
                if (coursePLResult != null) {
                    CoursePLListResponse.CoursePLListResponseBody body = coursePLResult.getRepBody();
                    if (body != null) {
                        updateMarkInfo(body);
                        if (body.getList() != null) {
                            if (isRefreash) {
                                plList.clear();
                            }
                            plList.addAll(body.getList());
                            adapter.setData(plList);
                        }
                    }
                }
            } else if (response instanceof BaseResponse) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                loadNew();

            }
        } else {
            loadingDlg.dismissDialog();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ToastUtil.show(response.getResMsg());
        }


    }

    /**
     * 更新课程信息
     *
     * @param body
     */
    private void updateMarkInfo(CoursePLListResponse.CoursePLListResponseBody body) {
        isReMark = body.getIs_remarked().equals("1");
        if (isReMark) {
            ratingLayout.setVisibility(View.GONE);
        } else {
            ratingLayout.setVisibility(View.VISIBLE);
        }
        levelRatingBar.setRating(body.getRemark_cnt() != 0 ? (float) body.getRemark_level() / (float) body.getRemark_cnt() : 5f);
        levelTxt.setText("(" + body.getRemark_cnt() + "条评价)");
    }

    @Override
    public void onClick(View v) {

    }

}
