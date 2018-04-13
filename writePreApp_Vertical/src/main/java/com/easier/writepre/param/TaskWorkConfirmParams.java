package com.easier.writepre.param;

/**
 * 学习圈作业确认请求参数
 *
 * @author zhoulu
 */
public class TaskWorkConfirmParams extends BaseBodyParams {
    private String task_submit_id = "";


    public TaskWorkConfirmParams() {
    }

    public TaskWorkConfirmParams(String task_submit_id) {
        this.task_submit_id = task_submit_id;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_stu_task_confirm";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}