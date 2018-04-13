package com.easier.writepre.param;

/**
 * 理论考试时间获取
 *
 * @author zhoulu
 */
public class ExamTimeParams extends BaseBodyParams {

    private String pkg_id;

    private String stu_type;

    public ExamTimeParams(String pkg_id, String stu_type) {
        this.pkg_id = pkg_id;
        this.stu_type = stu_type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_theory_start";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
