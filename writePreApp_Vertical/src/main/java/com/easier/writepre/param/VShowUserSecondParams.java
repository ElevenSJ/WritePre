package com.easier.writepre.param;

/**
 * V展二级界面请求接口
 */
public class VShowUserSecondParams extends BaseBodyParams {

    private String vshow_user_id;

    public VShowUserSecondParams(String vshow_user_id) {
        this.vshow_user_id = vshow_user_id;
    }

    @Override
    public String getProNo() {
        return "sj_vshow_group_query";
    }

    @Override
    public String getUrl() {
        return "login";
    }

}
