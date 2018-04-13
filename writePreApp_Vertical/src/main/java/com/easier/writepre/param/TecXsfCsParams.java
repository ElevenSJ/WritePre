package com.easier.writepre.param;

import java.util.List;

/**
 * 推荐课程推送请求参数
 *
 * @author zhoulu
 */
public class TecXsfCsParams extends BaseBodyParams {

    private String last_id = "";
    private String count = "";


    public TecXsfCsParams() {
    }

    public TecXsfCsParams(String last_id, String count) {
        this.last_id = last_id;
        this.count = count;
    }


    @Override
    public String getProNo() {

        return "tec_xsf_cs_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}