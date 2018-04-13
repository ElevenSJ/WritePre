package com.easier.writepre.param;

/**
 * 试卷提交
 *
 * @author zhoulu
 */
public class ExamTestSubmitParams extends BaseBodyParams {

    private String pkg_id;
    private String exam_answer;

    public ExamTestSubmitParams(String pkg_id, String exam_answer) {
        this.pkg_id = pkg_id;
        this.exam_answer = exam_answer;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_pkg_test_submit";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
