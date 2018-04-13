package com.easier.writepre.param;

/**
 * 学习题库列表
 *
 * @author kai.zhong
 */
public class QuestionsListParams extends BaseBodyParams {

    public QuestionsListParams() {
    }

    @Override
    public String getProNo() {
        return "tec_xsf_stu_pkg_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
