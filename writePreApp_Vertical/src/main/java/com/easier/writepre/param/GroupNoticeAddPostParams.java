package com.easier.writepre.param;

public class GroupNoticeAddPostParams extends BaseBodyParams {

    private String pub_news_id;

    public GroupNoticeAddPostParams(String pub_news_id) {
        this.pub_news_id = pub_news_id;
    }

    @Override
    public String getProNo() {
        return "sj_circle_pub_news_view_add";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}
