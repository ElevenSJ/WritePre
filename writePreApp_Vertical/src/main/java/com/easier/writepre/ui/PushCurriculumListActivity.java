package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.RecommendCourseAdapter;
import com.easier.writepre.entity.TecXsfCsInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TecXsfCsParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TecXsfCsResponse;
import com.easier.writepre.response.TecXsfStuInfoGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;

import java.util.List;

/**
 * 推送课程列表页面
 */
public class PushCurriculumListActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener2<ListView> {
    private PullToRefreshListView lv_course_push;
    private RecommendCourseAdapter recommendCourseAdapter;
    private int count = 10;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_pushcurriculum);
        init();
        mHandler.sendEmptyMessage(0);
    }

    private void init() {
        setTopTitle("小书法师推送课程");
        lv_course_push = (PullToRefreshListView) findViewById(R.id.lv_course_push);
        lv_course_push.setOnRefreshListener(this);
        lv_course_push.getRefreshableView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 1) {
                            return;
                        }
                    }
                });
        recommendCourseAdapter = new RecommendCourseAdapter(this);
        lv_course_push.setAdapter(recommendCourseAdapter);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:// 刷新
                    loadRefresh();
                    break;
                case 2://刷新推荐课程数据
                    List<TecXsfCsInfo> tecXsfCsInfos = (List<TecXsfCsInfo>) msg.obj;
                    updateRecommendCourse(tecXsfCsInfos);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 更新推荐课程数据
     *
     * @param tecXsfCsInfos
     */
    private void updateRecommendCourse(List<TecXsfCsInfo> tecXsfCsInfos) {
        if (tecXsfCsInfos == null) {
            return;
        }
        if (!tecXsfCsInfos.isEmpty()) {
            if (recommendCourseAdapter.getCount() == 0) {
                recommendCourseAdapter.setData(tecXsfCsInfos);
            } else {
                recommendCourseAdapter.addData(tecXsfCsInfos);
            }
        } else {
            ToastUtil.show("暂无更多!");
        }
    }

    /**
     * 获取推荐课程
     *
     * @param last_id 首次默认 9
     * @param count
     */
    private void requestTecXsfCsList(String last_id, String count) {
        TecXsfCsParams tecXsfCsParams = new TecXsfCsParams(last_id, count);
        RequestManager.request(this, tecXsfCsParams, TecXsfCsResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if (response == null) {
            return;
        }
        lv_course_push.onRefreshComplete();
        if ("0".equals(response.getResCode())) {
            if (response instanceof TecXsfCsResponse) {
                TecXsfCsResponse tecXsfCsResponse = (TecXsfCsResponse) response;
                if (tecXsfCsResponse != null) {
                    List<TecXsfCsInfo> tecXsfCsInfos = tecXsfCsResponse.getRepBody().getList();
                    mHandler.obtainMessage(2, tecXsfCsInfos).sendToTarget();
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        recommendCourseAdapter.clearData();
        requestTecXsfCsList(recommendCourseAdapter.getLastId(), count + "");
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        requestTecXsfCsList(recommendCourseAdapter.getLastId(), count + "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadRefresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadMore();
    }
}

