package com.easier.writepre.entity;

public class UserInfo {

    private String _id = "";

    private String head_img = "";

    private String uname = "";

    private String real_name = "";

    private String birth_day = "";

    private String addr = "";

    private String school_name = "";

    private String good_at = "";// 擅长

    private String is_care = "0";// 是否关注 0 未关注 1 已经关注

    private String care_type = "0";// 2:相互关注 其他:没有相互关注

    private String is_teacher = "0";// 是否是 老师 1 是， 0或其他 否

    private String post_cnt = "0";// 发帖数

    private String circle_cnt = "0";// 圈子数

    private String my_care_num = "0";// 我的关注数

    private String care_me_num = "0";// 粉丝数

    private String bei_tie = "";// 喜欢的碑帖

    private String signature = "";// 个性签名

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(String birth_day) {
        this.birth_day = birth_day;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getGood_at() {
        return good_at;
    }

    public void setGood_at(String good_at) {
        this.good_at = good_at;
    }

    public String getIs_care() {
        return is_care;
    }

    public void setIs_care(String is_care) {
        this.is_care = is_care;
    }

    public String getIs_teacher() {
        return is_teacher;
    }

    public void setIs_teacher(String is_teacher) {
        this.is_teacher = is_teacher;
    }

    public String getPost_cnt() {
        return post_cnt;
    }

    public void setPost_cnt(String post_cnt) {
        this.post_cnt = post_cnt;
    }

    public String getCircle_cnt() {
        return circle_cnt;
    }

    public void setCircle_cnt(String circle_cnt) {
        this.circle_cnt = circle_cnt;
    }

    public String getMy_care_num() {
        return my_care_num;
    }

    public void setMy_care_num(String my_care_num) {
        this.my_care_num = my_care_num;
    }

    public String getCare_me_num() {
        return care_me_num;
    }

    public void setCare_me_num(String care_me_num) {
        this.care_me_num = care_me_num;
    }

    public String getBei_tie() {
        return bei_tie;
    }

    public void setBei_tie(String bei_tie) {
        this.bei_tie = bei_tie;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCare_type() {
        return care_type;
    }

    public void setCare_type(String care_type) {
        this.care_type = care_type;
    }
}
