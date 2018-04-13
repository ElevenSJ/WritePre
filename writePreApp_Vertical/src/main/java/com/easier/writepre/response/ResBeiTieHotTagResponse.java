package com.easier.writepre.response;

import com.easier.writepre.entity.HotTagInfo;

import java.util.List;

/**
 * 碑帖推荐返回
 */
public class ResBeiTieHotTagResponse extends BaseResponse {

    private Repbody repBody;

    public Repbody getRepBody() {
        return repBody;
    }

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public class Repbody {

        private List<HotTagInfo> list;

        public void setList(List<HotTagInfo> list) {
            this.list = list;
        }

        public List<HotTagInfo> getList() {
            return list;
        }

    }
}
