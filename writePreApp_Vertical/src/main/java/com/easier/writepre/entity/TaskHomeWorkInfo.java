package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 学习圈作业结构体
 *
 * @author zhoulu
 */
public class TaskHomeWorkInfo implements Serializable {
    private String _id = "";
    private String user_id = "";// 谁发布的
    private String uname = "";
    private String head_img = "";
    private String title = "";
    private String[] img_url;
    private String level = "";
    private String submit_cnt = "0";// 提交人数
    private String confirm_cnt = "0";// 作业确认数
    private String is_submit = "";//	0 或 1 0:未提交作业 1:已提交作业
    private String ctime = "";
    private int imageSelectIndex = 0;//图片选择的下标

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getConfirm_cnt() {
        return confirm_cnt;
    }

    public void setConfirm_cnt(String confirm_cnt) {
        this.confirm_cnt = confirm_cnt;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String[] getImg_url() {
        return img_url;
    }

    public void setImg_url(String[] img_url) {
        this.img_url = img_url;
    }

    public String getIs_submit() {
        return is_submit;
    }

    public void setIs_submit(String is_submit) {
        this.is_submit = is_submit;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSubmit_cnt() {
        return submit_cnt;
    }

    public void setSubmit_cnt(String submit_cnt) {
        this.submit_cnt = submit_cnt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getImageSelectIndex() {
        return imageSelectIndex;
    }

    public void setImageSelectIndex(int imageSelectIndex) {
        this.imageSelectIndex = imageSelectIndex;
    }
}