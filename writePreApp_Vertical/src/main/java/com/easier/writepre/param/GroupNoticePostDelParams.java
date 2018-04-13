package com.easier.writepre.param;

public class GroupNoticePostDelParams extends BaseBodyParams {

    private String circle_id;
    private String pub_news_id;

    public GroupNoticePostDelParams(String circle_id, String pub_news_id) {
        this.circle_id = circle_id;
        this.pub_news_id = pub_news_id;
    }

    @Override
    public String getProNo() {
        return "sj_circle_pub_news_del";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
