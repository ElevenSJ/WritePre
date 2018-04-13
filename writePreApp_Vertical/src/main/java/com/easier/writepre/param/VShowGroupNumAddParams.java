package com.easier.writepre.param;

/**
 * V展二级界面请求接口
 */
public class VShowGroupNumAddParams extends BaseBodyParams {

    private String group_id;

    public VShowGroupNumAddParams(String group_id) {
        this.group_id = group_id;
    }

    @Override
    public String getProNo() {
        return "sj_vshow_group_view_num_add";
    }

    @Override
    public String getUrl() {
        return "login";
    }

}
