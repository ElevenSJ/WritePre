package com.easier.writepre.mainview;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.TitlePopAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.CourseType;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.CourseFragment;
import com.easier.writepre.fragment.DiyCourseFragment;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CourseTypeListParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseTypeListResponse;
import com.easier.writepre.response.CourseTypeListResponse.CourseTypeResponseBody;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MainViewPager;
import com.easier.writepre.widget.TabIndicator;
import com.easier.writepre.widget.TabIndicator.OnTabSelectedListener;
import com.easier.writepre.widget.TitlePopupWindow;
import com.sj.autolayout.AutoLayoutActivity;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author dqt
 */
public class CourseMainView extends BaseView {

    public static ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

    public static final String TAB_INDEX = "course_tab_index";

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THR = 2;
    public static final int TYPE_FOR = 3;

    private List<CourseType> courseTypeList;

    private MainViewPager pager;

    private TabIndicator indicator;

    private CourseViewPagerAdapter adapter;

    private View topLayout;

    private ArrayList<String> CONTENT;

    private TitlePopupWindow titlePop;
    private TitlePopAdapter titlePopAdapter;

    private int pageIndex = 0;


    public CourseMainView(Context ctx) {
        super(ctx);
    }

    @Override
    public int getContextView() {
        return R.layout.main_course;
    }

    @Override
    public void initView() {
        setTopRight(R.drawable.plus_big);
        CONTENT = new ArrayList<String>();
        courseTypeList = new ArrayList<CourseType>();

        CourseType customCourse = new CourseType();
        customCourse.set_id("-101");
        customCourse.setTitle("自选课程");
        courseTypeList.add(customCourse);

        topLayout = findViewById(R.id.top_layout);
        pager = (MainViewPager) findViewById(R.id.course_main_viewpager);

        indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);
        indicator.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, String id, String name) {
                pageIndex = position;
                pager.setCurrentItem(position);
                titlePopAdapter.setChecked(position);
                umeng(pageIndex);
            }
        });
        // 更新下标
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                pageIndex = arg0;
                indicator.animateToTab(arg0);
                titlePopAdapter.setChecked(arg0);
                umeng(pageIndex);
            }

        });

        titlePop = new TitlePopupWindow(mCtx, R.layout.title_popupwindows);
        titlePop.setAnimationStyle(R.style.title_popwin_anim_style);
        titlePopAdapter = new TitlePopAdapter(mCtx);
        titlePop.setAdapater(titlePopAdapter);
        titlePopAdapter.setChecked(titlePop.getItemCheck());
        titlePop.setOnItemClick(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                titlePop.dismiss();
                titlePopAdapter.setChecked(position);
                pager.setCurrentItem(position);
            }
        });
    }

    private void umeng(int position) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_ONE));
        var.add(courseTypeList.get(position).getTitle());
        YouMengType.onEvent(mCtx,var,1,courseTypeList.get(position).getTitle());
    }

    @Override
    public void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        pageIndex = intent.getIntExtra(TAB_INDEX, pageIndex);
        titlePopAdapter.setChecked(pageIndex);
        pager.setCurrentItem(pageIndex);
    }


    @Override
    public void onTopRightClick(View view) {
        // TODO Auto-generated method stub

        super.onTopRightClick(view);
        titlePop.showAsDropDown(topLayout, 0, 0);
    }

    @Override
    public void showView(Intent intent) {
        if (isShowView) {
            LogUtils.e("非首次显示"); // 非首次显示
            if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                if (!courseTypeList.get(0).getTitle().equals("我的课程")) {
                    CourseType myCourse = new CourseType();
                    myCourse.set_id("-100");
                    myCourse.setTitle("我的课程");
                    courseTypeList.add(0, myCourse);
                    if (pageIndex != 0) {
                        pageIndex++;
                    }
                    setTabs();
                }
            } else {
                if (courseTypeList.get(0).getTitle().equals("我的课程")) {
                    courseTypeList.remove(0);
                    if (pageIndex != 0) {
                        pageIndex--;
                    }
                    setTabs();
                }
            }
            if (courseTypeList.size() < 3) {
                getCourseTypeList();
            }
        } else {
            LogUtils.e("首次显示"); // 首次显示
            if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                CourseType myCourse = new CourseType();
                myCourse.set_id("-100");
                myCourse.setTitle("我的课程");
                courseTypeList.add(0, myCourse);
            }
            getCourseTypeList();
        }
        super.showView(intent);
    }


    private void getCourseTypeList() {
//		dlgLoad.loading();
        RequestManager.request(mCtx, new CourseTypeListParams(), CourseTypeListResponse.class, this, Constant.URL);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("onResume");
        if (!isShowView) {
            return;
        }
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            if (!courseTypeList.get(0).getTitle().equals("我的课程")) {
                CourseType myCourse = new CourseType();
                myCourse.set_id("-100");
                myCourse.setTitle("我的课程");
                courseTypeList.add(0, myCourse);
                if (pageIndex != 0) {
                    pageIndex++;
                }
                setTabs();
            }
        } else {
            if (courseTypeList.get(0).getTitle().equals("我的课程")) {
                courseTypeList.remove(0);
                if (pageIndex != 0) {
                    pageIndex--;
                }
                setTabs();
            }
        }
    }

    @Override
    public void onStop() {
        LogUtils.e("onStop");

    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
//		dlgLoad.dismissDialog();
        if (response.getResCode().equals("0")) {
            if (response instanceof CourseTypeListResponse) {
                CourseTypeResponseBody data = ((CourseTypeListResponse) response).getRepBody();
                if (data.getList() != null && !data.getList().isEmpty()) {
                    for (int i = 0; i < data.getList().size(); i++) {
                        if (!courseTypeList.get(0).getTitle().equals("我的课程")) {
                            courseTypeList.add(0, data.getList().get(data.getList().size() - 1 - i));
                        } else {
                            courseTypeList.add(1, data.getList().get(data.getList().size() - 1 - i));
                        }
                        if (pageIndex != 0) {
                            pageIndex++;
                        }
                    }
                }
                setTabs();
            }
        } else {
            ToastUtil.show(response.getResMsg());
            if (response instanceof CourseTypeListResponse) {
                setTabs();
            }
        }
    }

    private void setTabs() {
        CONTENT.clear();
        for (int i = 0; i < courseTypeList.size(); i++) {
            CONTENT.add(courseTypeList.get(i).getTitle());
        }
        String[] strArr = new String[CONTENT.size()];
        CONTENT.toArray(strArr);
        titlePopAdapter.setData(strArr);
        if (adapter == null) {
            adapter = new CourseViewPagerAdapter(((AutoLayoutActivity) mCtx).getSupportFragmentManager());
            pager.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
//        pager.setOffscreenPageLimit(adapter.getCount()+1);

        indicator.setTab(strArr, CONTENT.get(pageIndex).equals("我的课程") ? ((Boolean)SPUtils.instance().get("hasMyCourse",false)?pageIndex:(pageIndex + 1)) : pageIndex);    //中小学书法标签项
    }

    private class CourseViewPagerAdapter extends FragmentStatePagerAdapter {

        final DiyCourseFragment diyCourseFragment= new DiyCourseFragment();

        public CourseViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return courseTypeList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            if (position==courseTypeList.size()-1){
                return diyCourseFragment;
            }else {
                String typeId = courseTypeList.get(position).get_id();
                String title = courseTypeList.get(position).getTitle();
                return CourseFragment.newInstance(typeId,title);
            }
        }

    }
}
