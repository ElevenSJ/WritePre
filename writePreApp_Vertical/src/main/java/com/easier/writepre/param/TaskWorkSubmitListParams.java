package com.easier.writepre.param;

/**
 * 学习圈作业提交列表请求参数
 *
 * @author zhoulu
 */
public class TaskWorkSubmitListParams extends BaseBodyParams {

    private String last_id = "";
    private String count = "";
    private String task_id = "";

    public TaskWorkSubmitListParams() {
    }

    public TaskWorkSubmitListParams(String task_id, String last_id, String count) {
        this.task_id = task_id;
        this.last_id = last_id;
        this.count = count;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_stu_task_submit_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}