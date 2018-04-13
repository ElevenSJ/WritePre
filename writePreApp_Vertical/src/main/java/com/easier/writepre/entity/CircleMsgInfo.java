package com.easier.writepre.entity;

import java.util.List;

public class CircleMsgInfo extends CircleBase {

    private String circle_id;

    private String circle_name;

    private String user_id;

    private String uname;

    private String head_img;

    private String title;

    private String title_min;

    private List<Object> coord;

    private String city;

    private String vod_url;

    private String[] img_url;

    private List<Object> mark_no;

    private String is_good;

    private String is_ok = "0";

    private String allotter;

    private int ok_num;

    private int remark_num;

    private int accuse_num;

    private String ctime;

    private int view_num = 0;

    private String is_teacher = "";// 1 是， 0或其他  否

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

    public String getCircle_name() {
        return circle_name;
    }

    public void setCircle_name(String circle_name) {
        this.circle_name = circle_name;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_min() {
        return title_min;
    }

    public void setTitle_min(String title_min) {
        this.title_min = title_min;
    }

    public List<Object> getCoord() {
        return coord;
    }

    public void setCoord(List<Object> coord) {
        this.coord = coord;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVod_url() {
        return vod_url;
    }

    public void setVod_url(String vod_url) {
        this.vod_url = vod_url;
    }

    public String[] getImg_url() {
        return img_url;
    }

    public void setImg_url(String[] img_url) {
        this.img_url = img_url;
    }

    public List<Object> getMark_no() {
        return mark_no;
    }

    public void setMark_no(List<Object> mark_no) {
        this.mark_no = mark_no;
    }

    public String getIs_good() {
        return is_good;
    }

    public void setIs_good(String is_good) {
        this.is_good = is_good;
    }

    public String getAllotter() {
        return allotter;
    }

    public void setAllotter(String allotter) {
        this.allotter = allotter;
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

    public int getAccuse_num() {
        return accuse_num;
    }

    public void setAccuse_num(int accuse_num) {
        this.accuse_num = accuse_num;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getIs_ok() {
        return is_ok;
    }

    public void setIs_ok(String is_ok) {
        this.is_ok = is_ok;
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

}
