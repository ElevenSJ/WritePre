package com.easier.writepre.param;

/**
 * 获取考试试卷请求参数
 *
 * @author zhoulu
 */

public class ExamZipPkgGetParams extends BaseBodyParams {

    private String stu_type;

    public ExamZipPkgGetParams(String stu_type) {
        this.stu_type = stu_type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_theory_detail";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}