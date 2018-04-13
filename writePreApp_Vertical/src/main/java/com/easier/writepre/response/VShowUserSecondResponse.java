package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * v展二级专辑服务响应数据
 */
public class VShowUserSecondResponse extends BaseResponse {

    private Repbody repBody;

    public Repbody getRepBody() {
        return repBody;
    }

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public class AlbumInfo implements Serializable {

        private String _id;

        private String face_url;

        private String title;

        private String pic_num;

        private String view_num;

        private String ctime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getFace_url() {
            return face_url;
        }

        public void setFace_url(String face_url) {
            this.face_url = face_url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPic_num() {
            return pic_num;
        }

        public void setPic_num(String pic_num) {
            this.pic_num = pic_num;
        }

        public String getView_num() {
            return view_num;
        }

        public void setView_num(String view_num) {
            this.view_num = view_num;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }
    }

    public class Repbody {

        private List<AlbumInfo> list;

        public void setList(List<AlbumInfo> list) {
            this.list = list;
        }

        public List<AlbumInfo> getList() {
            return list;
        }

    }

}