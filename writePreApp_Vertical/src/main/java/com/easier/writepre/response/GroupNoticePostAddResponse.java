package com.easier.writepre.response;

/**
 * 公告发布返回
 */
public class GroupNoticePostAddResponse extends BaseResponse {

    private Repbody repBody;

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public Repbody getRepBody() {
        return repBody;
    }

    public class Repbody {
        String pub_news_id;

        public String getPub_news_id() {
            return pub_news_id;
        }

        public void setPub_news_id(String pub_news_id) {
            this.pub_news_id = pub_news_id;
        }
    }
}