package com.easier.writepre.response;

import com.easier.writepre.entity.GroupNoticeInfo;
import com.easier.writepre.entity.GroupNoticeLookedMemberInfo;

import java.util.List;

/**
 * 已查看公告成员返回
 */
public class GroupNoticePostMemberResponse extends BaseResponse {

    private Repbody repBody;

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public Repbody getRepBody() {
        return repBody;
    }

    public class Repbody {
        public List<GroupNoticeLookedMemberInfo> getList() {
            return list;
        }

        public void setList(List<GroupNoticeLookedMemberInfo> list) {
            this.list = list;
        }

        private List<GroupNoticeLookedMemberInfo> list;
    }
}