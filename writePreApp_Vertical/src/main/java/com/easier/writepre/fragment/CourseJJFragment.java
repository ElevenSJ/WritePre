package com.easier.writepre.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.AddCourseParams;
import com.easier.writepre.param.DelCourseParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.DelCourseResponse;
import com.easier.writepre.ui.CourseCatalogActivityNew;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程简介
 *
 * @author sunjie
 */
public class CourseJJFragment extends BaseFragment {

    // 课程信息
    private ScrollView mainScroll;
    private TextView name;
    private TextView hotNum;
    private TextView videoTag;
    private TextView extVideoTag;
    private ImageView store;
    private TextView courseInfo;
    //示范老师
    private RoundImageView teacherIcon;
    private TextView teacherName;
    private TextView teacherInfo;
    //拆讲老师
    private RoundImageView extTeacherIcon;
    private TextView extTeacherName;
    private TextView extTeacherInfo;


    @Override
    public int getContextView() {
        return R.layout.fragment_course_jj;
    }

    @Override
    protected void init() {

        mainScroll = (ScrollView) findViewById(R.id.mainScroll);

        //收藏
        store = (ImageView) findViewById(R.id.tv_store);
        store.setOnClickListener(this);
        updateStore();

        //课程信息
        name = (TextView) findViewById(R.id.tv_name);
        hotNum = (TextView) findViewById(R.id.tv_hot_num);
        videoTag = (TextView) findViewById(R.id.tv_tag);
        extVideoTag = (TextView) findViewById(R.id.ext_video_tag);

        //课程概述
        courseInfo = (TextView) findViewById(R.id.course_info_txt);

        //示范老师信息
        teacherIcon = (RoundImageView) findViewById(R.id.teacher_icon);
        teacherName = (TextView) findViewById(R.id.teacher_name_txt);
        teacherInfo = (TextView) findViewById(R.id.teacher_info_txt);

        //拆讲老师信息
        extTeacherIcon = (RoundImageView) findViewById(R.id.ext_teacher_icon);
        extTeacherName = (TextView) findViewById(R.id.ext_teacher_name_txt);
        extTeacherInfo = (TextView) findViewById(R.id.ext_teacher_info_txt);
    }

    /**
     * 更新收藏状态
     */
    private void updateStore() {
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            store.setVisibility(View.VISIBLE);
        } else {
            store.setVisibility(View.INVISIBLE);
        }
        if (MainActivity.myCourse.get(CourseCatalogActivityNew.courseId) != null) {
            store.setImageResource(R.drawable.collection_red);
            //友盟统计
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_ONE));
            var.add("收藏课程");
            YouMengType.onEvent(getActivity(), var, 1, CourseCatalogActivityNew.courseGroup + CourseCatalogActivityNew.courseName);
        } else {
            store.setImageResource(R.drawable.collection_gray);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateData();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            updateData();
        }

    }

    public void updateData() {
        if (CourseCatalogActivityNew.categoryList != null) {
            name.setText(CourseCatalogActivityNew.categoryList.getTitle()==null?"":CourseCatalogActivityNew.categoryList.getTitle());
            hotNum.setText(Html.fromHtml(TextUtils.isEmpty(CourseCatalogActivityNew.categoryList.getHotness()) ? "0"
                    : "<font color=gray>热度 </font>" + CourseCatalogActivityNew.categoryList.getHotness() + "℃ "));
            if (CourseCatalogActivityNew.categoryList.getHas_video().equals("0")) {
                videoTag.setVisibility(View.GONE);
            } else {
                videoTag.setVisibility(View.VISIBLE);
            }
            if (CourseCatalogActivityNew.categoryList.getHas_ext_video().equals("1")) {
                extVideoTag.setVisibility(View.VISIBLE);
            } else {
                extVideoTag.setVisibility(View.GONE);
            }
            courseInfo.setText(TextUtils.isEmpty(CourseCatalogActivityNew.categoryList.getMemo()) ? "暂无" : CourseCatalogActivityNew.categoryList.getMemo());
            //示范老师
            if (TextUtils.isEmpty(CourseCatalogActivityNew.categoryList.getTeacher_id())) {
                findViewById(R.id.teacher_layout).setVisibility(View.GONE);
            } else {
                findViewById(R.id.teacher_layout).setVisibility(View.VISIBLE);
                teacherIcon.setImageView(StringUtil.getHeadUrl(CourseCatalogActivityNew.categoryList.getHead_img()));
                teacherName.setText(CourseCatalogActivityNew.categoryList.getTeacher_name());
                teacherInfo.setText(CourseCatalogActivityNew.categoryList.getTeacher_desc());
            }
            //拆讲老师
            if (TextUtils.isEmpty(CourseCatalogActivityNew.categoryList.getExt_teacher_id())) {
                findViewById(R.id.ext_teacher_layout).setVisibility(View.GONE);
            } else {
                findViewById(R.id.ext_teacher_layout).setVisibility(View.VISIBLE);
                extTeacherIcon.setImageView(StringUtil.getHeadUrl(CourseCatalogActivityNew.categoryList.getExt_head_img()));
                extTeacherName.setText(CourseCatalogActivityNew.categoryList.getExt_teacher_name());
                extTeacherInfo.setText(CourseCatalogActivityNew.categoryList.getExt_teacher_desc());
            }

        }

    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_store:
                if (LoginUtil.checkLogin(getActivity())) {
                    MainActivity.isCourseUpdate = true;
                    MainActivity.isMyCourseUpdate = true;
                    if (MainActivity.myCourse.get(CourseCatalogActivityNew.courseId) != null) {
                        RequestManager.request(getActivity(), new DelCourseParams(CourseCatalogActivityNew.courseId, CourseCatalogActivityNew.type), DelCourseResponse.class, this,
                                Constant.URL);
                        MainActivity.myCourse.remove(CourseCatalogActivityNew.courseId);
                    } else {
                        MainActivity.myCourse.put(CourseCatalogActivityNew.courseId, CourseCatalogActivityNew.courseId);
                        RequestManager.request(getActivity(), new AddCourseParams(CourseCatalogActivityNew.courseId, CourseCatalogActivityNew.type), BaseResponse.class, this, Constant.URL);
                    }
                    updateStore();
                }
                break;
        }
    }

}
