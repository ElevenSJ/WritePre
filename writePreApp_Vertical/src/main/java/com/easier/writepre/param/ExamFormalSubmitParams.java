package com.easier.writepre.param;

/**
 * 试卷提交
 *
 * @author zhoulu
 */
public class ExamFormalSubmitParams extends BaseBodyParams {

    private String stu_type;
    private String pkg_id;
    private String exam_answer;

    public ExamFormalSubmitParams(String stu_type, String pkg_id, String answer) {
        this.stu_type = stu_type;
        this.pkg_id = pkg_id;
        this.exam_answer = answer;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_theory_submit";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
