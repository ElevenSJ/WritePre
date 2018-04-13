package com.easier.writepre.param;

import com.easier.writepre.ui.SendTopicActivity;

import java.util.List;

/**
 * 公告发布
 *
 * @author kai.zhong
 */
public class GroupNoticePostAddParams extends BaseBodyParams {

    private String circle_id = "";


    private String title;

    private List<SquarePostAddParams.ImgUrlPostAdd> img_url;


    public GroupNoticePostAddParams() {
    }

    public GroupNoticePostAddParams(String circle_id, String title,
                                    List<SquarePostAddParams.ImgUrlPostAdd> img_url) {
        this.circle_id = circle_id;
        this.title = title;
        this.img_url = img_url;
    }


    @Override
    public String getProNo() {

        return "sj_circle_pub_news_add";
    }

    @Override
    public String getUrl() {
        return "app";
    }

}