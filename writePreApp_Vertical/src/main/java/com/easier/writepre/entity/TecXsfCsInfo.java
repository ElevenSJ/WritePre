package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 推送课程结构体
 *
 * @author zhoulu
 */
public class TecXsfCsInfo implements Serializable {
    private String _id = "";
    private String level = "";// 1~5 级 整型数据
    private String type;// 推送课类型： html video
    private String title = "";        // 身份证号
    private String desc = "";// 描述
    private String pic_url = "";        // 图片url
    private String html_url = "";        // html片段存储地址
    private String video_url = "";    // 视频地址
    private String is_pub = "";        // ok no
    private String ctime = "";        // 时间
    private String teacher_name = "";
    private String head_img = "";
    private String teacher_desc = "";
    private String teacher_id = "";

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getIs_pub() {
        return is_pub;
    }

    public void setIs_pub(String is_pub) {
        this.is_pub = is_pub;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getTeacher_desc() {
        return teacher_desc;
    }

    public void setTeacher_desc(String teacher_desc) {
        this.teacher_desc = teacher_desc;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }
}