package com.easier.writepre.param;

/**
 * 试题
 *
 * @author kai.zhong
 */
public class ExamParams extends BaseBodyParams {

    private String pkg_id;

    public ExamParams(String pkg_id) {
        this.pkg_id = pkg_id;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_stu_pkg_ex_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
