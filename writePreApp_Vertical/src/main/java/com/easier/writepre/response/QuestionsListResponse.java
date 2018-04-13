package com.easier.writepre.response;

import java.util.List;

/**
 * 学习题库列表
 *
 * @author kai.zhong
 */
public class QuestionsListResponse extends BaseResponse {

    private QuestionsBody repBody;

    public QuestionsBody getRepBody() {
        return repBody;
    }

    public void setRepBody(QuestionsBody repBody) {
        this.repBody = repBody;
    }

    public class QuestionsBody {
        private String total;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        private List<QuestionsInfo> list;

        public List<QuestionsInfo> getList() {
            return list;
        }

        public void setList(List<QuestionsInfo> list) {
            this.list = list;
        }
    }

    public class QuestionsInfo {

        private String _id;

        private String title;

        private String desc;

        private String face_url;

        private String level;

        private String type;

        private String current_num;

        private String status;

        private String ctime;

        public String getCurrent_num() {
            return current_num;
        }

        public void setCurrent_num(String current_num) {
            this.current_num = current_num;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }
    }

}
