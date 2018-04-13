package com.easier.writepre.param;

/**
 * 学时获取请求参数
 *
 * @author zhoulu
 */
public class TimeGetParams extends BaseBodyParams {
    private String cs_id = "";


    public TimeGetParams() {
    }

    public TimeGetParams(String cs_id) {
        this.cs_id = cs_id;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_stu_cs_time_get";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}