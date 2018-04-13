package com.easier.writepre.response;

import com.easier.writepre.entity.TecXsfCsInfo;

import java.util.List;

/**
 * 书法师推荐课程返回
 */
public class TecXsfCsResponse extends BaseResponse {

    private TecXsfCsResponse.TecXsfCsInfos repBody;

    public TecXsfCsResponse.TecXsfCsInfos getRepBody() {
        return repBody;
    }

    public void setRepBody(TecXsfCsResponse.TecXsfCsInfos repBody) {
        this.repBody = repBody;
    }

    public class TecXsfCsInfos {

        private List<TecXsfCsInfo> list;

        public void setList(List<TecXsfCsInfo> list) {
            this.list = list;
        }

        public List<TecXsfCsInfo> getList() {
            return list;
        }

    }
}
