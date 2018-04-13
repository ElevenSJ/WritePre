package com.easier.writepre.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easier.writepre.R;
import com.easier.writepre.adapter.FindClassicWorksListAdapter;
import com.easier.writepre.adapter.FoundBeiTieListAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.FoundMainView;
import com.easier.writepre.param.ResPeriodParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ResPenmenResponse;
import com.easier.writepre.response.ResPeriodResponse;
import com.easier.writepre.response.ResPenmenResponse.ResPenmenBody;
import com.easier.writepre.response.ResPenmenResponse.ResPenmenInfo;
import com.easier.writepre.response.ResPeriodResponse.ResPeriodBody;
import com.easier.writepre.response.ResPeriodResponse.ResPeriodInfo;
import com.easier.writepre.ui.FindClassicWorksActivity;
import com.easier.writepre.ui.FoundBeiTieCalligrapherActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.ToastUtil;
import com.nineoldandroids.animation.ObjectAnimator;
import com.sj.autolayout.utils.AutoUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 经典碑帖(界面改造)
 */
public class FindClassicWorksFragment extends BaseFragment implements
        OnItemClickListener {

    // private View view;

    private ListView listView;

    private List<ResPeriodInfo> list; // 朝代数据

    private View headerView;

    private RelativeLayout mToolbar;

    private int mTouchSlop;

    private float mFirstY;

    private float mCurrentY;

    private int direction;

    private boolean mShow = true;

    private ObjectAnimator mAnimator;

    @Override
    public int getContextView() {
        return R.layout.find_classic_works;
    }

    @Override
    protected void init() {
        mTouchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
        headerView = getActivity().getLayoutInflater().inflate(R.layout.search_head, null);
        listView = (ListView) findViewById(R.id.lv_list);
        mToolbar = (RelativeLayout) findViewById(R.id.toolbar);
        headerView.setVisibility(View.INVISIBLE);
        listView.addHeaderView(headerView);
        listView.setOnItemClickListener(this);
        findViewById(R.id.et_search).setOnClickListener(this);
        RequestManager.request(getActivity(), new ResPeriodParams(),
                ResPeriodResponse.class, this, Constant.URL);
        scollShowHideSearch();
    }

    /**
     * 滑动listView监听显示隐藏头部输入框
     */
    public void scollShowHideSearch() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFirstY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentY = event.getY();
                        if (mCurrentY - mFirstY > mTouchSlop) {
                            direction = 0;// down
                        } else if (mFirstY - mCurrentY > mTouchSlop) {
                            direction = 1;// up
                        }
                        if (direction == 1) {
                            if (mShow) {
                                toolbarAnim(1);//show
                                mShow = !mShow;
                            }
                        } else if (direction == 0) {
                            if (!mShow) {
                                toolbarAnim(0);//hide
                                mShow = !mShow;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    private void toolbarAnim(int flag) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (flag == 0) {
            mAnimator = ObjectAnimator.ofFloat(mToolbar,
                    "translationY", mToolbar.getTranslationY(), 0);
        } else {
            mAnimator = ObjectAnimator.ofFloat(mToolbar,
                    "translationY", mToolbar.getTranslationY(),
                    -mToolbar.getHeight());
        }
        mAnimator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search:
                Intent intent = new Intent(getActivity(), FoundBeiTieCalligrapherActivity.class);
                intent.putExtra("SEARCH_TYPE", true);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof ResPeriodResponse) {
                ResPeriodResponse mResPeriodResponse = (ResPeriodResponse) response;
                if (mResPeriodResponse != null) {
                    ResPeriodBody mResPeriodBody = mResPeriodResponse
                            .getRepBody();
                    if (mResPeriodBody != null) {
                        list = mResPeriodBody.getList();
                        if (list != null && list.size() > 0) {
                            listView.setAdapter(new FindClassicWorksListAdapter(
                                    getActivity(), list));

                        }
                    }

                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //友盟統計
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FOR));
        var.add(FoundMainView.CONTENT[FoundMainView.index]);
        var.add(list.get(arg2 - 1).getText());
        YouMengType.onEvent(getActivity(), var, 1, list.get(arg2 - 1).getText());

        Intent intent = new Intent(getActivity(), FindClassicWorksActivity.class);
        intent.putExtra("period_id", list.get(arg2 - 1).get_id());
        intent.putExtra("period_title", list.get(arg2 - 1).getText());
        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && isPrepared) {
            umeng();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            umeng();
        }
    }

    /**
     * 友盟统计
     */
    private void umeng() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FOR));
        var.add(FoundMainView.CONTENT[FoundMainView.index]);
        YouMengType.onEvent(getActivity(), var, getShowTime(), FoundMainView.CONTENT[FoundMainView.index]);
    }
}
