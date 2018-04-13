package com.easier.writepre.response;

import com.easier.writepre.entity.TaskHomeWorkSubmitInfo;

import java.util.List;

/**
 * 学习圈作业提交列表返回
 */
public class TaskWorkSubmitListResponse extends BaseResponse {

    private TaskWorkSubmitListResponse.TaskSubmitList repBody;

    public TaskWorkSubmitListResponse.TaskSubmitList getRepBody() {
        return repBody;
    }

    public void setRepBody(TaskWorkSubmitListResponse.TaskSubmitList repBody) {
        this.repBody = repBody;
    }

    public class TaskSubmitList {

        private List<TaskHomeWorkSubmitInfo> list;

        public void setList(List<TaskHomeWorkSubmitInfo> list) {
            this.list = list;
        }

        public List<TaskHomeWorkSubmitInfo> getList() {
            return list;
        }

    }
}
