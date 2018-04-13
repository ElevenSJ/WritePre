package com.easier.writepre.response;

import java.io.Serializable;

/**
 * 考试进度返回
 *
 * @author zhoulu
 */
public class ExamRecResponse extends BaseResponse {

    private ExamRecInfo repBody;

    public ExamRecInfo getRepBody() {
        return repBody;
    }

    public void setRepBody(ExamRecInfo repBody) {
        this.repBody = repBody;
    }


    public class ExamRecInfo implements Serializable {
        private String level = "";//考试级别
        private String theory_score = "";//理论考试成绩     已有成绩的最高成绩
        private String theory_count = "";//0 1 2 3, //理论考试次数
        private String practice_score = ""; //实践考试成绩     已有成绩的最高成绩
        private String practice_count = "";// 0 1 2 3,//实践考试次数
        private String stage = ""; //考试进度：0 理论考、1 实践考、2 寄作品、3 收到作品、4 终止
        private String theory_info_url = "";//理论考试说明url
        private String practice_info_url = "";//实践考试说明url
        private String xsf_duty_url = "";//小书法师不作弊承诺责任书
        private String major_duty_url = "";//专业人才不作弊承诺责任书
        private String practice_count_limit = "";//实践考试还剩多少次
        private String theory_count_limit = "";//理论考试还剩多少次
        private String exam_status = ""; //等于2的情况下属于异常需要客户端弹窗提示
        private String start_time = "";//考试开始日期
        private String end_time = "";//考试结束日期

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getExam_status() {
            return exam_status;
        }

        public void setExam_status(String exam_status) {
            this.exam_status = exam_status;
        }

        public String getPractice_count_limit() {
            return practice_count_limit;
        }

        public void setPractice_count_limit(String practice_count_limit) {
            this.practice_count_limit = practice_count_limit;
        }

        public String getTheory_count_limit() {
            return theory_count_limit;
        }

        public void setTheory_count_limit(String theory_count_limit) {
            this.theory_count_limit = theory_count_limit;
        }

        public void setXsf_duty_url(String xsf_duty_url) {
            this.xsf_duty_url = xsf_duty_url;
        }

        public void setMajor_duty_url(String major_duty_url) {
            this.major_duty_url = major_duty_url;
        }

        public String getXsf_duty_url() {
            return xsf_duty_url;
        }

        public String getMajor_duty_url() {
            return major_duty_url;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getPractice_count() {
            return practice_count;
        }

        public void setPractice_count(String practice_count) {
            this.practice_count = practice_count;
        }

        public String getPractice_info_url() {
            return practice_info_url;
        }

        public void setPractice_info_url(String practice_info_url) {
            this.practice_info_url = practice_info_url;
        }

        public String getPractice_score() {
            return practice_score;
        }

        public void setPractice_score(String practice_score) {
            this.practice_score = practice_score;
        }

        public String getStage() {
            return stage;
        }

        public void setStage(String stage) {
            this.stage = stage;
        }

        public String getTheory_count() {
            return theory_count;
        }

        public void setTheory_count(String theory_count) {
            this.theory_count = theory_count;
        }

        public String getTheory_info_url() {
            return theory_info_url;
        }

        public void setTheory_info_url(String theory_info_url) {
            this.theory_info_url = theory_info_url;
        }

        public String getTheory_score() {
            return theory_score;
        }

        public void setTheory_score(String theory_score) {
            this.theory_score = theory_score;
        }

        @Override
        public String toString() {
            return "ExamRecInfo{" +
                    "level='" + level + '\'' +
                    ", theory_score='" + theory_score + '\'' +
                    ", theory_count='" + theory_count + '\'' +
                    ", practice_score='" + practice_score + '\'' +
                    ", practice_count='" + practice_count + '\'' +
                    ", stage='" + stage + '\'' +
                    ", theory_info_url='" + theory_info_url + '\'' +
                    ", practice_info_url='" + practice_info_url + '\'' +
                    '}';
        }
    }

}
