package com.easier.writepre.response;

import java.io.Serializable;

/**
 * 试卷提交返回
 *
 * @author zhoulu
 */
public class ExamSubmitResponse extends BaseResponse {

    private ExamSubmitResponseInfo repBody;

    public ExamSubmitResponseInfo getRepBody() {
        return repBody;
    }

    public void setRepBody(ExamSubmitResponseInfo repBody) {
        this.repBody = repBody;
    }


    public class ExamSubmitResponseInfo implements Serializable {
        private String _id = "";            // 考试记录id
        private String pkg_id = "";
        private String user_id = "";
        private String ctime = "";
        private String score = "";
        private String stu_time="";
        private String is_pass="";
        private String stu_type="";

        public String getIs_pass() {
            return is_pass;
        }

        public void setIs_pass(String is_pass) {
            this.is_pass = is_pass;
        }

        public String getStu_type() {
            return stu_type;
        }

        public void setStu_type(String stu_type) {
            this.stu_type = stu_type;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getPkg_id() {
            return pkg_id;
        }

        public void setPkg_id(String pkg_id) {
            this.pkg_id = pkg_id;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getStu_time() {
            return stu_time;
        }

        public void setStu_time(String stu_time) {
            this.stu_time = stu_time;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

}
