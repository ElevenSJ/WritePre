package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * 考试时间返回
 *
 * @author kai.zhong
 */
public class ExamTimeResponse extends BaseResponse {

    private ExamBody repBody;

    public ExamBody getRepBody() {
        return repBody;
    }

    public void setRepBody(ExamBody repBody) {
        this.repBody = repBody;
    }

    public class ExamBody {
        private String stu_type; // 小书法师或者专业人才
        private String pkg_id;           // 试卷id
        private String start_time;     // 开始考试时间
        private String current_time;  // 当前系统时间
        private String end_time;        // 考试结束时间
        private String exam_status; //考试状态  0：未完成   1：已完成

        public String getCurrent_time() {
            return current_time;
        }

        public void setCurrent_time(String current_time) {
            this.current_time = current_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getExam_status() {
            return exam_status;
        }

        public void setExam_status(String exam_status) {
            this.exam_status = exam_status;
        }

        public String getPkg_id() {
            return pkg_id;
        }

        public void setPkg_id(String pkg_id) {
            this.pkg_id = pkg_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getStu_type() {
            return stu_type;
        }

        public void setStu_type(String stu_type) {
            this.stu_type = stu_type;
        }

        @Override
        public String toString() {
            return "ExamBody{" +
                    "current_time='" + current_time + '\'' +
                    ", stu_type='" + stu_type + '\'' +
                    ", pkg_id='" + pkg_id + '\'' +
                    ", start_time='" + start_time + '\'' +
                    ", end_time='" + end_time + '\'' +
                    ", exam_status='" + exam_status + '\'' +
                    '}';
        }
    }


}
