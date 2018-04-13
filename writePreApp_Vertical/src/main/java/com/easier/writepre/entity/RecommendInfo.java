package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 推荐结构体
 *
 * @author zhoulu
 */
public class RecommendInfo implements Serializable {
    private String _id = "";
    private String user_id = "";
    private String uname = "";
    private String head_img = "";
    private String is_teacher = "";
    private String isCared="";
    private String care_me_num="";// 粉丝数
    private String post_id="";
    private String []img_url;
    private String sort_num="";
    private String is_pub="";
    private String ctime="";
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(String is_teacher) {
        this.is_teacher = is_teacher;
    }

    public String getIsCared() {
        return isCared;
    }

    public void setIsCared(String isCared) {
        this.isCared = isCared;
    }

    public String getCare_me_num() {
        return care_me_num;
    }

    public void setCare_me_num(String care_me_num) {
        this.care_me_num = care_me_num;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String[] getImg_url() {
        return img_url;
    }

    public void setImg_url(String[] img_url) {
        this.img_url = img_url;
    }

    public String getSort_num() {
        return sort_num;
    }

    public void setSort_num(String sort_num) {
        this.sort_num = sort_num;
    }

    public String getIs_pub() {
        return is_pub;
    }

    public void setIs_pub(String is_pub) {
        this.is_pub = is_pub;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

}