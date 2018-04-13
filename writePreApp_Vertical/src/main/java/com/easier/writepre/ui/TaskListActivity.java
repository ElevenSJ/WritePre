package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.TaskHomeWorkAdapter;
import com.easier.writepre.entity.TaskHomeWorkInfo;
import com.easier.writepre.entity.TaskHomeWorkSubmitInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.TaskWorkListParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TaskWorkResponse;
import com.easier.writepre.rongyun.RongYunUtils;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import java.util.List;

/**
 * 作业列表页面
 */
public class TaskListActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener2<ListView> {
    private PullToRefreshListView lv_task_list;
    private int start = 0;
    private int count = 10;
    private boolean isTeacher;
    private TextView tv_btn;
    private TaskHomeWorkAdapter taskHomeWorkAdapter;
    private final int CODE_SENDTASKWORK = 1000;//发布走也通知刷新数据
    private final int CODE_CHECKTASKWORK = 1001;//审阅通知刷新数据
    private String circle_id;
    private String circle_name;

    private int itemPosition = -1;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_tasklist);
        isTeacher = getIntent().getBooleanExtra("isTeacher", false);
        circle_id = getIntent().getStringExtra("circle_id");
        circle_name = getIntent().getStringExtra("circle_name");
        init();
        requestTaskWorkList("9", count + "");
    }

    private void init() {
        setTopTitle("作业列表");
        tv_btn = (TextView) findViewById(R.id.tv_btn);
        tv_btn.setOnClickListener(this);
        lv_task_list = (PullToRefreshListView) findViewById(R.id.lv_task_list);
        lv_task_list.setOnRefreshListener(this);
        lv_task_list.getRefreshableView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 1) {
                            return;
                        }
                        itemPosition = position - 1;
                        TaskHomeWorkInfo taskHomeWorkInfo = taskHomeWorkAdapter.getItem(position - 1);
                        Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                        intent.putExtra("data", taskHomeWorkInfo);
                        intent.putExtra("isTeacher", isTeacher);
                        startActivityForResult(intent, CODE_CHECKTASKWORK);
                    }
                });
        taskHomeWorkAdapter = new TaskHomeWorkAdapter(this);
        taskHomeWorkAdapter.setNeedShowImage(true);
        taskHomeWorkAdapter.setTeacher(isTeacher);
        lv_task_list.setAdapter(taskHomeWorkAdapter);
        if (isTeacher) {
            tv_btn.setText("发布作业");
        } else {
            tv_btn.setText("交流");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_btn://根据条件做相应的跳转
                if (isTeacher) {
                    //老师角色跳转到发布作业页面
                    Intent intent = new Intent(this, SendTaskWorkActivity.class);
                    intent.putExtra("modeType", SendTaskWorkActivity.TYPE_TEACHER);
                    startActivityForResult(intent, CODE_SENDTASKWORK);
                } else {
                    //学生角色跳转到圈子
//                    toCircleList(circle_id);
                    RongYunUtils.getInstances().startGroupChat(this, circle_id, circle_name, "0");
                }
                break;
        }
    }

    private void toCircleList(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, CircleMsgListActivity.class);
        intent.putExtra("circle_id", id); // 圈子id
        startActivity(intent);
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
                case 3://刷新学习圈作业数据
                    List<TaskHomeWorkInfo> taskHomeWorkInfos = (List<TaskHomeWorkInfo>) msg.obj;
                    updateTaskInfos(taskHomeWorkInfos);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 更新学习圈作业数据
     *
     * @param taskHomeWorkInfos
     */
    private void updateTaskInfos(List<TaskHomeWorkInfo> taskHomeWorkInfos) {
        if (taskHomeWorkInfos == null) {
            return;
        }
        if (!taskHomeWorkInfos.isEmpty()) {
            if (taskHomeWorkAdapter.getCount() == 0) {
                taskHomeWorkAdapter.setData(taskHomeWorkInfos);
            } else {
                taskHomeWorkAdapter.addData(taskHomeWorkInfos);
            }
        } else {
            ToastUtil.show("暂无更多!");
        }
    }

    /**
     * 刷新数据
     */
    private void loadRefresh() {
        taskHomeWorkAdapter.clearData();
        requestTaskWorkList(taskHomeWorkAdapter.getLastId(), count + "");
    }

    /**
     * 获取更多数据
     */
    private void loadMore() {
        requestTaskWorkList(taskHomeWorkAdapter.getLastId(), count + "");
    }

    /**
     * 获取学习圈作业列表
     *
     * @param last_id 首次默认 9
     * @param count
     */
    private void requestTaskWorkList(String last_id, String count) {
        TaskWorkListParams taskWorkListParams = new TaskWorkListParams(last_id, count);
        RequestManager.request(this, taskWorkListParams, TaskWorkResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        lv_task_list.onRefreshComplete();
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof TaskWorkResponse) {
                TaskWorkResponse taskWorkResponse = (TaskWorkResponse) response;
                if (taskWorkResponse != null) {
                    List<TaskHomeWorkInfo> homeWorkInfos = taskWorkResponse.getRepBody().getList();
                    mHandler.obtainMessage(3, homeWorkInfos).sendToTarget();
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_SENDTASKWORK) {
                mHandler.sendEmptyMessage(0);
            }
            if (requestCode == CODE_CHECKTASKWORK) {
                if (data != null) {
                    TaskHomeWorkInfo taskHomeWorkInfo = (TaskHomeWorkInfo) data.getSerializableExtra("data");
                    if (taskHomeWorkInfo != null) {
                        taskHomeWorkAdapter.updateData(taskHomeWorkInfo);
                        //刷新作业代查看人数
                        updateView(taskHomeWorkInfo);
                    }
                }

            }
        }
    }

    private void updateView(TaskHomeWorkInfo taskHomeWorkInfo) {
        int start = lv_task_list.getRefreshableView().getFirstVisiblePosition();
        int end = lv_task_list.getRefreshableView().getLastVisiblePosition();
        for (int i = start, j = end; i <= j; i++) {
            View view = lv_task_list.getRefreshableView().getChildAt(i - start);
            if (view.getTag() != null && ((TaskHomeWorkInfo) lv_task_list.getRefreshableView().getItemAtPosition(i)).get_id().equals(taskHomeWorkInfo.get_id())) {
                taskHomeWorkAdapter.updateItemView(view, taskHomeWorkInfo);
                return;
            }
        }
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

