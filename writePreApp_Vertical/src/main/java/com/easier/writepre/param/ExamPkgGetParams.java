package com.easier.writepre.param;

/**
 * 获取考试试卷请求参数
 *
 * @author zhoulu
 */

public class ExamPkgGetParams extends BaseBodyParams {

    private String exam_id;
    private String type;

    public ExamPkgGetParams(String exam_id, String type) {
        this.exam_id = exam_id;
        this.type = type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_pkg_get";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}