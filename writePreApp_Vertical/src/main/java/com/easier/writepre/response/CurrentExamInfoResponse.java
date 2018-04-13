package com.easier.writepre.response;

import java.io.Serializable;

/**
 * 获取当前考试的返回内容
 */
public class CurrentExamInfoResponse extends BaseResponse {
    private ExamInfo repBody;

    public void setRepBody(ExamInfo repBody) {
        this.repBody = repBody;
    }

    public ExamInfo getRepBody() {
        return repBody;
    }

    public class ExamInfo implements Serializable {
        String _id = "";            // 考试id
        String title = "";
        String start_date = "";        // 开始日期
        String end_date = "";        // 结束日期
        int level = 5;// 等级
        int ex1_full ;        // 理论题满分
        int ex2_full ;        // 实践题满分
        int ex1_pass ;        // 理论考试及格分
        int ex2_pass ;        // 实践考试及格分
        String info_url = "";  // 新闻链接
        String type = "";        // 试卷类型：exam 考试、test  模拟
        String is_pub = "ok";        // ok 或 no
        String memo = "";
        String ctime = "";

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

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public int getEx1_full() {
            return ex1_full;
        }

        public void setEx1_full(int ex1_full) {
            this.ex1_full = ex1_full;
        }

        public int getEx1_pass() {
            return ex1_pass;
        }

        public void setEx1_pass(int ex1_pass) {
            this.ex1_pass = ex1_pass;
        }

        public int getEx2_full() {
            return ex2_full;
        }

        public void setEx2_full(int ex2_full) {
            this.ex2_full = ex2_full;
        }

        public int getEx2_pass() {
            return ex2_pass;
        }

        public void setEx2_pass(int ex2_pass) {
            this.ex2_pass = ex2_pass;
        }

        public String getInfo_url() {
            return info_url;
        }

        public void setInfo_url(String info_url) {
            this.info_url = info_url;
        }

        public String getIs_pub() {
            return is_pub;
        }

        public void setIs_pub(String is_pub) {
            this.is_pub = is_pub;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
