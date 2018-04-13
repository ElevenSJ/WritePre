package com.easier.writepre.param;

import java.util.List;

/**
 * 学习圈作业提交请求参数
 *
 * @author zhoulu
 */
public class TaskWorkStuSubmitParams extends BaseBodyParams {
    private String task_id = "";
    private String title = "";
    private List<SquarePostAddParams.ImgUrlPostAdd> img_url;;


    public TaskWorkStuSubmitParams() {
    }

    public TaskWorkStuSubmitParams(String task_id, String title, List<SquarePostAddParams.ImgUrlPostAdd> img_url) {
        this.task_id = task_id;
        this.title = title;
        this.img_url = img_url;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_stu_task_submit";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}