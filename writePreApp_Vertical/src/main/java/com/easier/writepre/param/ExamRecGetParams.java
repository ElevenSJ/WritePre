package com.easier.writepre.param;

/**
 * 获取考试进度请求参数
 * @author zhoulu
 */

public class ExamRecGetParams extends BaseBodyParams {

    private String stu_type;//0:小书法师 1:专业人才

    public ExamRecGetParams(String stu_type) {
        this.stu_type = stu_type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_record";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}