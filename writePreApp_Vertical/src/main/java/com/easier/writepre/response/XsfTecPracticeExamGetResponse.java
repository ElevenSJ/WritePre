package com.easier.writepre.response;

import java.io.Serializable;

/**
 * 实践试卷信息获取的信息
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamGetResponse extends BaseResponse {

    private XsfTecPracticeExamGetInfo repBody;

    public XsfTecPracticeExamGetInfo getRepBody() {
        return repBody;
    }

    public void setRepBody(XsfTecPracticeExamGetInfo repBody) {
        this.repBody = repBody;
    }


    public class XsfTecPracticeExamGetInfo implements Serializable {
        private String _id = "";        // 试卷id
        private String title = "";    // 标题
        private String desc = "";    // 试卷描述
        private String face_url = "";    // 试卷封面
        private String level = "";        // 1~5 级
        private String current_num = "";        // 当前卷题数
        private String ctime = "";

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

        public String getCurrent_num() {
            return current_num;
        }

        public void setCurrent_num(String current_num) {
            this.current_num = current_num;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getFace_url() {
            return face_url;
        }

        public void setFace_url(String face_url) {
            this.face_url = face_url;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "ExamZipPkgInfo{" +
                    "_id='" + _id + '\'' +
                    ", title='" + title + '\'' +
                    ", desc='" + desc + '\'' +
                    ", face_url='" + face_url + '\'' +
                    ", level='" + level + '\'' +
                    ", current_num='" + current_num + '\'' +
                    ", ctime='" + ctime + '\'' +
                    '}';
        }
    }
}
