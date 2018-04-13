package com.easier.writepre.param;

/**
 * 理论测试列表
 *
 * @author zhoulu
 */
public class TheoreticalTestListParams extends BaseBodyParams {
    private String stu_type;//0:小书法师 1:专业人才
    public TheoreticalTestListParams(String stu_type) {
        this.stu_type=stu_type;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_pkg_test_list";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
