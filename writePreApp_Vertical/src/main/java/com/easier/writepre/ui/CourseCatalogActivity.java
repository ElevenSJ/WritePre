package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CourseCatalogAdapter;
import com.easier.writepre.adapter.CourseExpandAdapter;
import com.easier.writepre.entity.CourseCategoryInfo;
import com.easier.writepre.entity.CourseCategoryList;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.AddCourseParams;
import com.easier.writepre.param.CourseCategoryParams;
import com.easier.writepre.param.DelCourseParams;
import com.easier.writepre.param.DelMyCourseParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseCategoryResponse;
import com.easier.writepre.response.DelCourseResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 课程目录 废弃
 */
public class CourseCatalogActivity extends BaseActivity {
    public final static String COURSE_ID = "course_id";

    // 主页面listView
    private PullToRefreshExpandableListView listView;
    private CourseCatalogAdapter courseAdapter;
    private View placeHolderView;

    // 课程信息
    private ImageView icon;
    private TextView name;
    private TextView hotNum;
    private TextView videoTag;
    private LinearLayout hotTag;
    private ImageView store;

    private String courseId = "";
    private String courseType = "";
    public static CourseCategoryList categoryList;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_catalog);
        courseId = getIntent().getStringExtra(COURSE_ID);
        initView();
        getCourseCatalog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // @Override
    // protected void onResume() {
    // // TODO Auto-generated method stub
    // super.onResume();
    // updateRead();
    // }

    private void updateRead() {
        if (categoryList != null) {
            if (courseAdapter != null) {
                courseAdapter.setData(categoryList);
            }
        }
    }

    /**
     * 获取课程
     */
    private void getCourseCatalog() {
        dlgLoad.loading();
        RequestManager.request(this, new CourseCategoryParams(courseId), CourseCategoryResponse.class, this,
                Constant.URL);
    }

    private void initView() {
        setTopTitle("课程详情");
        // setTopRight(R.drawable.ico_download);

        listView = (PullToRefreshExpandableListView) findViewById(R.id.listview);
        placeHolderView = getLayoutInflater().inflate(R.layout.course_child_item, null);
        placeHolderView.findViewById(R.id.header_divider).setVisibility(View.VISIBLE);

        icon = (ImageView) placeHolderView.findViewById(R.id.img_icon);
        store = (ImageView) placeHolderView.findViewById(R.id.tv_store);

        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            store.setVisibility(View.VISIBLE);
        } else {
            store.setVisibility(View.INVISIBLE);
        }
        if (MainActivity.myCourse.get(courseId) != null) {
            store.setImageResource(R.drawable.collection_red);
        } else {
            store.setImageResource(R.drawable.collection_gray);
        }
        store.setOnClickListener(this);
        name = (TextView) placeHolderView.findViewById(R.id.tv_name);
        hotNum = (TextView) placeHolderView.findViewById(R.id.tv_hot_num);
        videoTag = (TextView) placeHolderView.findViewById(R.id.tv_tag);
        hotTag = (LinearLayout) placeHolderView.findViewById(R.id.layout_hot_tag);

        listView.getRefreshableView().addHeaderView(placeHolderView);

        courseAdapter = new CourseCatalogAdapter(this);
        listView.getRefreshableView().setAdapter(courseAdapter);

        listView.getRefreshableView().setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // ToastUtil.show(courseAdapter.getGroup(groupPosition).getTitle());
                CourseCategoryInfo categoryInfo = (CourseCategoryInfo)courseAdapter.getGroup(groupPosition);
                if (!TextUtils.isEmpty(categoryInfo.getLoadable()) && categoryInfo.getIs_end().equals("1")) {
                    // courseAdapter.getGroup(groupPosition).setIs_read("1");
                    // courseAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(CourseCatalogActivity.this, CourseDetailActivity.class);
                    intent.putExtra(CourseDetailActivity.CIRCLE_ID, categoryList.getCircle_id());
                    intent.putExtra(CourseDetailActivity.COURSE_GROUP_INDEX, groupPosition);
                    intent.putExtra(CourseDetailActivity.TITLE,
                            categoryList.getTitle());
                    intent.putExtra(CourseDetailActivity.GROUP,
                            categoryInfo.getTitle());
                    startActivityForResult(intent, 100);
                }
                return true;
            }
        });
        listView.getRefreshableView().setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                                        long id) {
                Intent intent = new Intent(CourseCatalogActivity.this, CourseDetailActivity.class);
                intent.putExtra(CourseDetailActivity.CIRCLE_ID, categoryList.getCircle_id());
                intent.putExtra(CourseDetailActivity.COURSE_CHILD_INDEX, childPosition);
                intent.putExtra(CourseDetailActivity.COURSE_GROUP_INDEX, groupPosition);
                intent.putExtra(CourseDetailActivity.TITLE, categoryList.getTitle());
                intent.putExtra(CourseDetailActivity.GROUP,
                        courseAdapter.getGroup(groupPosition).getTitle());
                startActivityForResult(intent, 100);
                return true;
            }
        });

        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    /**
     * 更新课程信息
     */
    private void updateCourseInfo() {
        BitmapHelp.getBitmapUtils().display(icon, StringUtil.getImgeUrl(categoryList.getFace_url()),
                new BitmapLoadCallBack<View>() {

                    @Override
                    public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                                BitmapLoadFrom arg4) {
                        ((ImageView) arg0).setImageBitmap(arg2);

                    }

                    @Override
                    public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                        ((ImageView) arg0).setImageResource(R.drawable.empty_photo);
                    }
                });
        name.setText(categoryList.getTitle());
        hotNum.setText(Html.fromHtml(TextUtils.isEmpty(categoryList.getHotness()) ? "0"
                : "<font color=gray>热度 </font>" + categoryList.getHotness() + "℃ "));
        if (categoryList.getHas_video().equals("0")) {
            videoTag.setVisibility(View.INVISIBLE);
        } else {
            videoTag.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
            }
        }, 300);
    }

    @Override
    public void onTopRightClick(View v) {
        ToastUtil.show("下载");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_store:
                if (LoginUtil.checkLogin(this)) {
                    MainActivity.isCourseUpdate = true;
                    MainActivity.isMyCourseUpdate = true;
                    if (MainActivity.myCourse.get(courseId) != null) {
                        RequestManager.request(this, new DelCourseParams(courseId,courseType), DelCourseResponse.class, this,
                                Constant.URL);
                        MainActivity.myCourse.remove(courseId);
                        store.setImageResource(R.drawable.collection_gray);
                    } else {
                        MainActivity.myCourse.put(courseId, courseId);
                        store.setImageResource(R.drawable.collection_red);
                        RequestManager.request(this, new AddCourseParams(courseId,courseType), BaseResponse.class, this, Constant.URL);
                    }
                }
                break;
            case R.id.txt_left:
                ToastUtil.show("设置提醒");
                break;
            case R.id.txt_right:
                if (categoryList != null) {
                    if (categoryList.getList().size() == 0) {
                        ToastUtil.show("未获取到章节");
                    } else {
                        Intent intent = new Intent(this, CourseDetailActivity.class);
                        intent.putExtra(CourseDetailActivity.CIRCLE_ID, categoryList.getCircle_id());
                        intent.putExtra(CourseDetailActivity.TITLE, categoryList.getTitle());
                        for (int i = 0; i < categoryList.getList().size(); i++) {
                            if (!TextUtils.isEmpty(categoryList.getList().get(i).getIs_end())
                                    && categoryList.getList().get(i).getIs_end().equals("1")) {
                                if (!TextUtils.isEmpty(categoryList.getList().get(i).getIs_read())
                                        && categoryList.getList().get(i).getIs_read().equals("1")) {
                                    continue;
                                } else {
                                    intent.putExtra(CourseDetailActivity.COURSE_GROUP_INDEX, i);
                                    // categoryList.getList().get(i).setIs_read("1");
                                    break;
                                }
                            } else {
                                boolean isOk = false;
                                if (categoryList.getList().get(i).getChild() != null
                                        && !categoryList.getList().get(i).getChild().isEmpty()) {
                                    for (int j = 0; j < categoryList.getList().get(i).getChild().size(); j++) {
                                        if (!TextUtils.isEmpty(categoryList.getList().get(i).getChild().get(j).getIs_read())
                                                && categoryList.getList().get(i).getChild().get(j).getIs_read()
                                                .equals("1")) {
                                            continue;
                                        } else {
                                            // categoryList.getList().get(i).getChild().get(j).setIs_read("1");
                                            intent.putExtra(CourseDetailActivity.COURSE_GROUP_INDEX, i);
                                            intent.putExtra(CourseDetailActivity.COURSE_CHILD_INDEX, j);
                                            isOk = true;
                                            break;
                                        }
                                    }
                                    if (isOk) {
                                        break;
                                    }
                                }
                            }
                        }
                        // updateRead();
                        startActivityForResult(intent, 100);
                    }
                } else {
                    ToastUtil.show("未找到该课程");
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        categoryList = null;
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();
        if (response.getResCode().equals("0")) {
            if (response instanceof CourseCategoryResponse) {
                categoryList = ((CourseCategoryResponse) response).getRepBody();
                if (categoryList != null) {
                    updateCourseInfo();
                    courseAdapter.setData(categoryList);
                    for (int i = 0; i < courseAdapter.getGroupCount(); i++) {
                        listView.getRefreshableView().expandGroup(i);
                    }
                }
            }

        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                updateRead();
            }
        }
    }

}