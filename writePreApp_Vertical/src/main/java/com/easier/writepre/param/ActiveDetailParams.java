package com.easier.writepre.param;

/**
 * 广场中全部活动查询
 *
 * @author sunjie
 */
public class ActiveDetailParams extends BaseBodyParams {


    private String topic_id;

    public ActiveDetailParams(String topic_id) {
        this.topic_id = topic_id;
    }

    @Override
    public String getProNo() {
        return "sj_square_topic_detail";
    }

    @Override
    public String getUrl() {
        return "login";
    }

}
