package com.easier.writepre.param;

/**
 * 实践考试开始
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamStartParams extends BaseBodyParams {

    private String stu_type;
    private String pkg_id;

    public XsfTecPracticeExamStartParams(String stu_type, String pkg_id) {
        this.stu_type = stu_type;
        this.pkg_id = pkg_id;

    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_practice_start";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
