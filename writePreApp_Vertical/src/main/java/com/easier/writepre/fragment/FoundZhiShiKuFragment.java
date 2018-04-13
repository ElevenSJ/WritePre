package com.easier.writepre.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CategoryAdapter;
import com.easier.writepre.adapter.SectionAdapter;
import com.easier.writepre.entity.ChildCategoryList;
import com.easier.writepre.entity.SectionInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.FoundMainView;
import com.easier.writepre.param.CategoryParams;
import com.easier.writepre.param.SectionParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CategoryResponse;
import com.easier.writepre.response.CategoryResponse.CategoryResponseBody;
import com.easier.writepre.response.SectionResponse;
import com.easier.writepre.response.SectionResponse.SectionResponseBody;
import com.easier.writepre.ui.CourseDetailActivity;
import com.easier.writepre.ui.FoundZhiShiKuThreeListActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 知识库
 */
public class FoundZhiShiKuFragment extends BaseFragment implements DrawerLayout.DrawerListener {

    private DrawerLayout mDrawerLayout;

    private ListView drawerList;

    private ListView mainList;

    //private String ref_repo_id;

    private String title;

    private SectionAdapter mSectionAdapter;

    private CategoryAdapter categoryAdapter;// 二级列表数据源(目录)

    private List<SectionInfo> mSectionInfoList;// 一级列表数据源

    private List<ChildCategoryList> mChildCategoryList;// 二级列表数据源(目录)

    @Override
    public int getContextView() {
        return R.layout.found_zhishiku;
    }

    @Override
    protected void init() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.found_zhishiku_drawer_layout);
        drawerList = (ListView) findViewById(R.id.found_zhishiku_drawer_list);
        mainList = (ListView) findViewById(R.id.found_zhishiku_content_list);

        mDrawerLayout.setDrawerListener(this);

        mainList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mChildCategoryList != null) {
                    mChildCategoryList.clear();
                }

                for (int i = 0, j = mSectionInfoList.size(); i < j; i++) {
                    if (position == i) {
                        mSectionInfoList.get(i).setCheck(true);
                    } else {
                        mSectionInfoList.get(i).setCheck(false);
                    }
                }

                mSectionAdapter.notifyDataSetChanged();
                //ref_repo_id = mSectionInfoList.get(position).get_id();
                title = mSectionInfoList.get(position).getSec_title();
                switchingData(position);
            }
        });
        drawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                umengTitle(title+mChildCategoryList.get(position)
                        .getTitle());
                if ("1".equals(mChildCategoryList.get(position).getIs_end())) { // 1是末级目录

                    // 直接跳转到webview
                    // Intent intent = new Intent(getActivity(),
                    // FindDetailActivity.class);
                    Intent intent = new Intent(getActivity(),
                            CourseDetailActivity.class);
                    intent.putExtra("from_parent",
                            (Serializable) mChildCategoryList);
                    intent.putExtra("from_parent_index", position);
                    intent.putExtra("from_flag", true);
                    intent.putExtra(CourseDetailActivity.TYPE, FoundMainView.CONTENT[FoundMainView.index]);
                    intent.putExtra(CourseDetailActivity.GROUP, title);
                    intent.putExtra(CourseDetailActivity.TITLE, mChildCategoryList.get(position).getTitle());
                    getActivity().startActivity(intent);

                } else { // 到下一级目录

                    Intent intent = new Intent(getActivity(),
                            FoundZhiShiKuThreeListActivity.class);
                    intent.putExtra("group", title);
                    intent.putExtra("type", FoundMainView.CONTENT[FoundMainView.index]);
                    intent.putExtra("from_parent",
                            (Serializable) mChildCategoryList.get(position)
                                    .getChild());
                    intent.putExtra("title", mChildCategoryList.get(position)
                            .getTitle());
                    startActivity(intent);
                }

                for (int i = 0, j = mChildCategoryList.size(); i < j; i++) {
                    if (position == i) {
                        mChildCategoryList.get(i).setCheck(true);
                    } else {
                        mChildCategoryList.get(i).setCheck(false);
                    }
                }

                categoryAdapter.notifyDataSetChanged();
            }
        });

        RequestManager.request(getActivity(), new SectionParams(),
                SectionResponse.class, this, Constant.URL);
    }

    private void umengTitle(String title) {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FOR));
        var.add(FoundMainView.CONTENT[FoundMainView.index]);
        var.add(title);
        YouMengType.onEvent(getActivity(), var, 1, title);

    }

    /**
     * 切换版本调用目录数据
     *
     * @param position
     */
    public void switchingData(int position) {
        RequestManager.request(getActivity(), new CategoryParams(
                        mSectionInfoList.get(position).get_id()),
                CategoryResponse.class, this, Constant.URL);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof SectionResponse) {
                SectionResponse mSectionResponse = (SectionResponse) response;
                SectionResponseBody mSectionResponseBody = mSectionResponse
                        .getRepBody();
                if (mSectionResponseBody != null) {
                    mSectionInfoList = mSectionResponseBody.getList();
                    if (mSectionInfoList != null && mSectionInfoList.size() > 0) {
                        // mSectionInfoList.remove(0); // 移除经典碑帖
                        for (SectionInfo sectionInfo : mSectionInfoList) { // 移除经典碑帖
                            if (sectionInfo.getSec_title().equals("经典碑帖")) {
                                mSectionInfoList.remove(sectionInfo);
                                break;
                            }
                        }
                        // mSectionInfoList.get(0).setCheck(true); // 默认第一个被选中
                        mSectionAdapter = new SectionAdapter(getActivity(),
                                mSectionInfoList);
                        mainList.setAdapter(mSectionAdapter);
                        // ref_repo_id = mSectionInfoList.get(0).get_id(); //
                        // 默认请求第一个分类信息
                        // RequestManager.request(getActivity(), new
                        // CategoryParams(ref_repo_id),
                        // CategoryResponse.class, this, Constant.URL);
                    }
                }
            } else if (response instanceof CategoryResponse) {
                CategoryResponse mCategoryResponse = (CategoryResponse) response;
                if (mCategoryResponse != null) {
                    CategoryResponseBody mCategoryResponseBody = mCategoryResponse
                            .getRepBody();
                    if (mCategoryResponseBody != null) {
                        mChildCategoryList = mCategoryResponseBody.getList();
                        if (mChildCategoryList != null
                                && mChildCategoryList.size() > 0) {
                            categoryAdapter = new CategoryAdapter(
                                    getActivity(), mChildCategoryList);
                            drawerList.setAdapter(categoryAdapter);
                            drawerList.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_in));
                            mDrawerLayout.openDrawer(drawerList);// 弹窗代码
                        }
                    }

                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        FoundMainView.pager.setNoScroll(true);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        FoundMainView.pager.setNoScroll(false);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser&&isPrepared){
            umeng();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()){
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
