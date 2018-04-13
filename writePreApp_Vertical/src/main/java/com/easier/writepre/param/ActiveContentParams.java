package com.easier.writepre.param;

/**
 * 广场中全部活动查询
 *
 * @author sunjie
 */
public class ActiveContentParams extends BaseBodyParams {


    private String last_id;
    private int count;

    public ActiveContentParams(String last_id, int count) {
        this.count = count;
        this.last_id = last_id;
    }

    @Override
    public String getProNo() {
        return "sj_square_topic_query";
    }

    @Override
    public String getUrl() {
        return "login";
    }

}
