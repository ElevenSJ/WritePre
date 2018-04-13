package com.easier.writepre.param;

/**
 * 获取实践试卷内容
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamDetailParams extends BaseBodyParams {

    private String pkg_id;
    private String stu_type;

    public XsfTecPracticeExamDetailParams(String pkg_id,String stu_type) {
        this.pkg_id = pkg_id;
        this.stu_type = stu_type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_practice_detail";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
