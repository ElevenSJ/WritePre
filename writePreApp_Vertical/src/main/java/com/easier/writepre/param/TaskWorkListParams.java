package com.easier.writepre.param;

/**
 * 学习圈作业列表请求参数
 *
 * @author zhoulu
 */
public class TaskWorkListParams extends BaseBodyParams {

    private String last_id = "";
    private String count = "";


    public TaskWorkListParams() {
    }

    public TaskWorkListParams(String last_id, String count) {
        this.last_id = last_id;
        this.count = count;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_stu_task_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}