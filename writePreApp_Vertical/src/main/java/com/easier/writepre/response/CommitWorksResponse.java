package com.easier.writepre.response;

/**
 * 提交作品
 *
 * @author kai.zhong
 */
public class CommitWorksResponse extends BaseResponse {

    private Repbody repBody;

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public Repbody getRepBody() {
        return repBody;
    }

    public class Repbody {

    }
}