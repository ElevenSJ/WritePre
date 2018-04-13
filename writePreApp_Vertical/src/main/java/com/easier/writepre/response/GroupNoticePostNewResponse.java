package com.easier.writepre.response;

import com.easier.writepre.entity.GroupNoticeInfo;

import java.util.List;

/**
 * 公告发布返回
 */
public class GroupNoticePostNewResponse extends BaseResponse {

    private Repbody repBody;

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public Repbody getRepBody() {
        return repBody;
    }

    public class Repbody {
        public List<GroupNoticeInfo> getList() {
            return list;
        }

        public void setList(List<GroupNoticeInfo> list) {
            this.list = list;
        }

        private List<GroupNoticeInfo> list;
    }
}