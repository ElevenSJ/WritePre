package com.easier.writepre.response;

import com.easier.writepre.entity.PkInfoNew;

import java.util.List;

/**
 * 点赞返回
 *
 * @author sunjie
 */
public class PkGetInfoResponse extends BaseResponse {

    private PkNewsBody repBody;

    public void setRepBody(PkNewsBody repBody) {
        this.repBody = repBody;
    }

    public PkNewsBody getRepBody() {
        return repBody;
    }

    public class PkNewsBody {
        private List<PkInfoNew> list;

        public List<PkInfoNew> getList() {
            return list;
        }

        public void setList(List<PkInfoNew> list) {
            this.list = list;
        }
    }
}