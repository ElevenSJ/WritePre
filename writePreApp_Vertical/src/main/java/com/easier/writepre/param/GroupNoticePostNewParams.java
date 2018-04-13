package com.easier.writepre.param;

import java.util.List;

/**
 * 公告获取
 *
 * @author zhoulu
 */
public class GroupNoticePostNewParams extends BaseBodyParams {

    private String circle_id = "";
    private String last_id = "";
    private String count = "";

    private List<SquarePostAddParams.ImgUrlPostAdd> img_url;


    public GroupNoticePostNewParams() {
    }

    public GroupNoticePostNewParams(String circle_id, String last_id, String count) {
        this.circle_id = circle_id;
        this.last_id = last_id;
        this.count = count;
    }


    @Override
    public String getProNo() {

        return "sj_circle_pub_news_query";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}