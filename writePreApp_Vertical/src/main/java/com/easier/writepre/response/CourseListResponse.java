package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseList;

public class CourseListResponse extends BaseResponse {

    private CourseListResponseBody repBody;

    public CourseListResponseBody getRepBody() {
        return repBody;
    }

    public void setRepBody(CourseListResponseBody repBody) {
        this.repBody = repBody;
    }

    public class CourseListResponseBody {

        public List<CourseList> getList() {
            return list;
        }

        public void setList(List<CourseList> list) {
            this.list = list;
        }

        private List<CourseList> list;

    }
}
