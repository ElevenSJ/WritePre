package com.easier.writepre.param;

import java.util.List;

/**
 * 学习圈作业发布请求参数
 *
 * @author zhoulu
 */
public class TaskWorkTeacherPubParams extends BaseBodyParams {
    private String level = "";
    private String title = "";
    private List<SquarePostAddParams.ImgUrlPostAdd> img_url;


    public TaskWorkTeacherPubParams() {
    }

    public TaskWorkTeacherPubParams(String level, String title, List<SquarePostAddParams.ImgUrlPostAdd> img_url) {
        this.level = level;
        this.title = title;
        this.img_url = img_url;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_stu_task_pub";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}