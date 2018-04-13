package com.easier.writepre.param;


import com.easier.writepre.entity.WorksItem;
import java.util.ArrayList;

/**
 * 提交作品
 */
public class CommitWorksParams extends BaseBodyParams {

    private String stu_type;

    private String pkg_id;

    private ArrayList<WorksItem> items;

    public CommitWorksParams(String stu_type, String pkg_id, ArrayList<WorksItem> items) {
        this.stu_type = stu_type;
        this.pkg_id = pkg_id;
        this.items = items;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_practice_submit";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}
