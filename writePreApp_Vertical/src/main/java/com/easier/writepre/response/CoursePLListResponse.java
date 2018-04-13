package com.easier.writepre.response;

import com.easier.writepre.entity.CourseList;
import com.easier.writepre.entity.CoursePLList;

import java.util.List;

public class CoursePLListResponse extends BaseResponse {

    private CoursePLListResponseBody repBody;

    public CoursePLListResponseBody getRepBody() {
        return repBody;
    }

    public void setRepBody(CoursePLListResponseBody repBody) {
        this.repBody = repBody;
    }

    public class CoursePLListResponseBody {
        private String is_remarked = "";
        private int remark_cnt;
        private int remark_level;

        public List<CoursePLList> getList() {
            return list;
        }

        public void setList(List<CoursePLList> list) {
            this.list = list;
        }

        private List<CoursePLList> list;

        public String getIs_remarked() {
            return is_remarked;
        }

        public void setIs_remarked(String is_remarked) {
            this.is_remarked = is_remarked;
        }

        public int getRemark_cnt() {
            return remark_cnt;
        }

        public void setRemark_cnt(int remark_cnt) {
            this.remark_cnt = remark_cnt;
        }

        public int getRemark_level() {
            return remark_level;
        }

        public void setRemark_level(int remark_level) {
            this.remark_level = remark_level;
        }
    }
}
