package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * v展三级专辑图片服务响应数据
 */
public class VShowUserThirdResponse extends BaseResponse {

    private Repbody repBody;

    public Repbody getRepBody() {
        return repBody;
    }

    public void setRepBody(Repbody repBody) {
        this.repBody = repBody;
    }

    public class PicInfo implements Serializable {

        private String _id;

        private String group_id;

        private String title;

        private String size_info;

        private String pic_url;

        private String ctime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSize_info() {
            return size_info;
        }

        public void setSize_info(String size_info) {
            this.size_info = size_info;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }
    }

    public class Repbody {

        private String photo_url;

        private String real_name;

        private String title;

        private String desc;

        public String getPhoto_url() {
            return photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
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

        public String getGroup_title() {
            return group_title;
        }

        public void setGroup_title(String group_title) {
            this.group_title = group_title;
        }

        private String group_title;

        private List<PicInfo> list;

        public void setList(List<PicInfo> list) {
            this.list = list;
        }

        public List<PicInfo> getList() {
            return list;
        }

    }

}