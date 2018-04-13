package com.easier.writepre.param;

/**
 * 实践试卷信息获取
 *
 * @author zhaomaohan
 */

public class XsfTecPracticeExamGetParams extends BaseBodyParams {

    private String stu_type;

    public XsfTecPracticeExamGetParams(String stu_type) {
        this.stu_type = stu_type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_practice_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}