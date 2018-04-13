package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * 获取实践试卷内容返回的
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamDetailResponse extends BaseResponse {

    private XsfTecPracticeExamDetailBody repBody;

    public XsfTecPracticeExamDetailBody getRepBody() {
        return repBody;
    }

    public void setRepBody(XsfTecPracticeExamDetailBody repBody) {
        this.repBody = repBody;
    }

    public class XsfTecPracticeExamDetailBody implements Serializable{

        private List<XsfTecPracticeExamDetailInfo> list;

        public List<XsfTecPracticeExamDetailInfo> getList() {
            return list;
        }

        public void setList(List<XsfTecPracticeExamDetailInfo> list) {
            this.list = list;
        }
    }

    public class XsfTecPracticeExamDetailInfo implements Serializable {

        private String _id;

        private String cata_id;

        private String title;

        private String level;

        private String memo;

        private String ctime;

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        private List<Items> items;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCata_id() {
            return cata_id;
        }

        public void setCata_id(String cata_id) {
            this.cata_id = cata_id;
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

        public List<Items> getItems() {
            return items;
        }

        public void setItems(List<Items> items) {
            this.items = items;
        }
    }

    public class Items implements Serializable {

        private String _id;

        private String title;

        private String pra_id;
        private String ctime;
        private String pic_url;

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

        public String getPra_id() {
            return pra_id;
        }

        public void setPra_id(String pra_id) {
            this.pra_id = pra_id;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }
    }

}
