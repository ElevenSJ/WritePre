package com.easier.writepre.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CourseCatalogAdapter;
import com.easier.writepre.entity.BaseCourseCategoryInfo;
import com.easier.writepre.entity.CourseCategoryInfo;
import com.easier.writepre.entity.CourseCategoryList;
import com.easier.writepre.entity.VodCourseCategoryInfo;
import com.easier.writepre.entity.VodCourseCategoryList;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.CourseCatalogActivityNew;
import com.easier.writepre.ui.CourseDetailActivity;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

/**
 * 课程目录
 *
 * @author sunjie
 */
public class CourseMLFragment extends BaseFragment {

    // 主页面listView
    private PullToRefreshExpandableListView listView;
    private CourseCatalogAdapter courseAdapter;
    private boolean isTop = true;

    @Override
    public int getContextView() {
        return R.layout.fragment_course_ml;
    }

    @Override
    protected void init() {

        listView = (PullToRefreshExpandableListView) findViewById(R.id.listview);
        courseAdapter = new CourseCatalogAdapter(getActivity());
        listView.getRefreshableView().setAdapter(courseAdapter);

        listView.getRefreshableView().setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // ToastUtil.show(courseAdapter.getGroup(groupPosition).getTitle());
                CourseMLFragment.this.onGroupClick(groupPosition);
                return true;
            }
        });
        listView.getRefreshableView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                                        long id) {
                CourseMLFragment.this.onChildClick(groupPosition,childPosition);
                return true;
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

    public void onChildClick(int groupPosition,int childPosition) {
        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
        intent.putExtra(CourseDetailActivity.CIRCLE_ID, CourseCatalogActivityNew.categoryList.getCircle_id());
        intent.putExtra(CourseDetailActivity.COURSE_CHILD_INDEX, childPosition);
        intent.putExtra(CourseDetailActivity.COURSE_GROUP_INDEX, groupPosition);
        intent.putExtra(CourseDetailActivity.TITLE, CourseCatalogActivityNew.categoryList.getTitle());
        intent.putExtra(CourseDetailActivity.GROUP,
                CourseCatalogActivityNew.courseGroup);
        intent.putExtra(CourseDetailActivity.TYPE,
                CourseCatalogActivityNew.courseType);
        startActivityForResult(intent, 100);
    }

    public void onGroupClick(int groupPosition) {
        if (courseAdapter==null||courseAdapter.getGroupCount()==0){
            return;
        }
        BaseCourseCategoryInfo baseCategory = courseAdapter.getGroup(groupPosition);
        if (baseCategory instanceof CourseCategoryInfo) {
            CourseCategoryInfo categoryInfo = (CourseCategoryInfo) baseCategory;
            if (!TextUtils.isEmpty(categoryInfo.getLoadable()) && categoryInfo.getIs_end().equals("1")) {
                Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                intent.putExtra(CourseDetailActivity.CIRCLE_ID, CourseCatalogActivityNew.categoryList.getCircle_id());
                intent.putExtra(CourseDetailActivity.COURSE_GROUP_INDEX, groupPosition);
                intent.putExtra(CourseDetailActivity.TITLE,
                        CourseCatalogActivityNew.categoryList.getTitle());
                intent.putExtra(CourseDetailActivity.GROUP,
                        CourseCatalogActivityNew.courseGroup);
                startActivityForResult(intent, 100);
            }
        } else {
            VodCourseCategoryInfo vodCategoryInfo = (VodCourseCategoryInfo) baseCategory;
            ((CourseCatalogActivityNew) getActivity()).playCourseDetail(vodCategoryInfo.getTitle(), vodCategoryInfo.getVideo_url());
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((CourseCatalogActivityNew) getActivity()).setChildViewScrollTop(isTop);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            ((CourseCatalogActivityNew) getActivity()).setChildViewScrollTop(isTop);
        }
    }


    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);

    }


    @Override
    public void onClick(View v) {

    }

    public void initData() {
        courseAdapter.setData(CourseCatalogActivityNew.categoryList);
        for (int i = 0; i < courseAdapter.getGroupCount(); i++) {
            listView.getRefreshableView().expandGroup(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 100) {
                courseAdapter.notifyDataSetChanged();
            }
        }
    }

}
