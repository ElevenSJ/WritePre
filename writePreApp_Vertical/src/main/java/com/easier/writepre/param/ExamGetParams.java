package com.easier.writepre.param;

/**
 * Created by SunJie on 17/3/9.
 */

public class ExamGetParams extends BaseBodyParams {

    private String type;

    public ExamGetParams(String type) {
        this.type = type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_get";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}