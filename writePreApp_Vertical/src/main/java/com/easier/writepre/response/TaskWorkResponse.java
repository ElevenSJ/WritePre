package com.easier.writepre.response;

import com.easier.writepre.entity.TaskHomeWorkInfo;
import com.easier.writepre.entity.TecXsfCsInfo;

import java.util.List;

/**
 * 学习圈作业列表返回
 */
public class TaskWorkResponse extends BaseResponse {

    private TaskWorkResponse.TaskList repBody;

    public TaskWorkResponse.TaskList getRepBody() {
        return repBody;
    }

    public void setRepBody(TaskWorkResponse.TaskList repBody) {
        this.repBody = repBody;
    }

    public class TaskList {

        private List<TaskHomeWorkInfo> list;

        public void setList(List<TaskHomeWorkInfo> list) {
            this.list = list;
        }

        public List<TaskHomeWorkInfo> getList() {
            return list;
        }

    }
}
