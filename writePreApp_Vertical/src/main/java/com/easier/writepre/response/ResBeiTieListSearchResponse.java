package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * 书法家相关碑帖查询 和 碑帖搜索
 */
public class ResBeiTieListSearchResponse extends BaseResponse implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -995008509189867585L;
    private ResBeiTieListSearchBody repBody;

    public ResBeiTieListSearchBody getRepBody() {
        return repBody;
    }

    public void setRepBody(ResBeiTieListSearchBody repBody) {
        this.repBody = repBody;
    }

    public class ResBeiTieListSearchBody implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1061453130164884865L;
        private List<ResBeiTieListSearchInfo> list;

        public void setList(List<ResBeiTieListSearchInfo> list) {
            this.list = list;
        }

        public List<ResBeiTieListSearchInfo> getList() {
            return list;
        }

    }

    public class ResBeiTieListSearchInfo implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = -8479016729163392054L;

        private String _id;

        private String penmen_id;

        private String penmen_name;

        private String title;

        private String face_url;

        private String link_url;

        private String sort;

        private String memo;

        private String ctime;

        private String period;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getPenmen_id() {
            return penmen_id;
        }

        public void setPenmen_id(String penmen_id) {
            this.penmen_id = penmen_id;
        }

        public String getPenmen_name() {
            return penmen_name;
        }

        public void setPenmen_name(String penmen_name) {
            this.penmen_name = penmen_name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFace_url() {
            return face_url;
        }

        public void setFace_url(String face_url) {
            this.face_url = face_url;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }
    }
}
