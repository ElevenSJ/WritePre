package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class ContentInfo extends ContentBase {

    private String[] img_url;

    private int accuse;

    private String allotter;

    private String city;

    private List<Object> coord;

    private String is_good;

    private List<Object> mark_no;

    private int ok_num;

    private int remark_num;

    private String title_min;

    private String user_id;

    private String vod_url;

    private String is_ok = "0";

    private String uname;

    private String head_img;

    private int view_num = 0;

    private String is_teacher = "";// 1 是， 0或其他  否

    private String topic_id = "";    // 活动id
    private String topic_title = "";// 活动标题

    public String[] getImg_url() {
        return img_url;
    }

    public void setImg_url(String[] img_url) {
        this.img_url = img_url;
    }

    public int getAccuse() {
        return accuse;
    }

    public void setAccuse(int accuse) {
        this.accuse = accuse;
    }

    public String getAllotter() {
        return allotter;
    }

    public void setAllotter(String allotter) {
        this.allotter = allotter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Object> getCoord() {
        return coord;
    }

    public void setCoord(List<Object> coord) {
        this.coord = coord;
    }

    public String getIs_good() {
        return is_good;
    }

    public void setIs_good(String is_good) {
        this.is_good = is_good;
    }

    public List<Object> getMark_no() {
        return mark_no;
    }

    public void setMark_no(List<Object> mark_no) {
        this.mark_no = mark_no;
    }

    public int getOk_num() {
        return ok_num;
    }

    public void setOk_num(int ok_num) {
        this.ok_num = ok_num;
    }

    public int getRemark_num() {
        return remark_num;
    }

    public void setRemark_num(int remark_num) {
        this.remark_num = remark_num;
    }

    public String getTitle_min() {
        return title_min;
    }

    public void setTitle_min(String title_min) {
        this.title_min = title_min;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVod_url() {
        return vod_url;
    }

    public void setVod_url(String vod_url) {
        this.vod_url = vod_url;
    }

    public String getIs_ok() {
        return is_ok;
    }

    public void setIs_ok(String is_ok) {
        this.is_ok = is_ok;
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

    public int getView_num() {
        return view_num;
    }

    public void setView_num(int view_num) {
        this.view_num = view_num;
    }

    public String getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(String is_teacher) {
        this.is_teacher = is_teacher;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }
}