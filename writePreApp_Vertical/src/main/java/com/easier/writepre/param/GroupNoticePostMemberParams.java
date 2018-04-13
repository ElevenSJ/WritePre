package com.easier.writepre.param;

import java.util.List;

/**
 * 已查看成员参数
 *
 * @author zhoulu
 */
public class GroupNoticePostMemberParams extends BaseBodyParams {

    private String pub_news_id = "";
    private String last_id = "";
    private String count = "";

    private List<SquarePostAddParams.ImgUrlPostAdd> img_url;


    public GroupNoticePostMemberParams() {
    }

    public GroupNoticePostMemberParams(String pub_news_id, String last_id, String count) {
        this.pub_news_id = pub_news_id;
        this.last_id = last_id;
        this.count = count;
    }


    @Override
    public String getProNo() {

        return "sj_circle_pub_news_view_query";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}