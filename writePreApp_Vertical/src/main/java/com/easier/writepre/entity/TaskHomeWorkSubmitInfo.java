package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 学生作业列表结构体
 *
 * @author zhoulu
 */
public class TaskHomeWorkSubmitInfo implements Serializable {
    private String _id = "";
    private String user_id = "";// 谁提交的
    private String task_id = "";//作业ID
    private String uname = "";
    private String head_img = "";
    private String title = "";
    private String[] img_url;
    private String level = "";
    private String is_confirm = "";// no | ok
    private String ctime = "";
    private int imageSelectIndex=0;//图片选择的下标
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

    public String getIs_confirm() {
        return is_confirm;
    }

    public void setIs_confirm(String is_confirm) {
        this.is_confirm = is_confirm;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public int getImageSelectIndex() {
        return imageSelectIndex;
    }

    public void setImageSelectIndex(int imageSelectIndex) {
        this.imageSelectIndex = imageSelectIndex;
    }
}