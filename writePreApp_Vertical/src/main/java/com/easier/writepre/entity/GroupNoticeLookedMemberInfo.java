package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 通知公告群成员查看结构体
 */
public class GroupNoticeLookedMemberInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _id = "";
    private String circle_id = "";
    private String pub_news_id = "";
    private String user_id = "";
    private String circle_uname = "";
    private String head_img = "";
    private String ctime = "";
    private String uname = "";
    private String role = "";
    private String is_teacher = "";

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(String is_teacher) {
        this.is_teacher = is_teacher;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

    public String getPub_news_id() {
        return pub_news_id;
    }

    public void setPub_news_id(String pub_news_id) {
        this.pub_news_id = pub_news_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCircle_uname() {
        return circle_uname;
    }

    public void setCircle_uname(String circle_uname) {
        this.circle_uname = circle_uname;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}
