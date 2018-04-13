package com.easier.writepre.param;

/**
 * 理论测试题列表
 *
 * @author zhoulu
 */
public class ExamTheoreticalParams extends BaseBodyParams {

    private String pkg_id;

    public ExamTheoreticalParams(String pkg_id) {
        this.pkg_id = pkg_id;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_pkg_item_get";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
