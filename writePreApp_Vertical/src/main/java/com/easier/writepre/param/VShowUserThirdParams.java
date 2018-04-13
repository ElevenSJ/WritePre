package com.easier.writepre.param;

/**
 * V展三级界面请求接口
 */
public class VShowUserThirdParams extends BaseBodyParams {

    private String group_id;

    public VShowUserThirdParams(String group_id) {
        this.group_id = group_id;
    }

    @Override
    public String getProNo() {
        return "sj_vshow_pic_query";
    }

    @Override
    public String getUrl() {
        //return "app";
        return "login";
    }

}
