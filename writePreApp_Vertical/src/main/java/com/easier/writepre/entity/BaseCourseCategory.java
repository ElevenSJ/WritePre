package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class BaseCourseCategory implements Serializable {

    private String _id;

    private String ref_sec_id;

    private String ref_ct_id;

    private String title;

    private String face_url;

    private String face_url_h;//封面

    private String face_url_m;//封面

    private String stu_num_op;

    private String stu_num;

    private String memo;

    private String circle_id;

    private String hotness;

    private String ctime;

    private String has_video = "";
    private String has_ext_video = "";


    //视频示范老师信息
    private String teacher_id = "";
    private String teacher_name = "";
    private String head_img = "";
    private String teacher_desc = "";

    //视频拆讲老师信息
    private String ext_teacher_id = "";
    private String ext_teacher_name = "";
    private String ext_head_img = "";
    private String ext_teacher_desc = "";


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRef_sec_id() {
        return ref_sec_id;
    }

    public void setRef_sec_id(String ref_sec_id) {
        this.ref_sec_id = ref_sec_id;
    }

    public String getRef_ct_id() {
        return ref_ct_id;
    }

    public void setRef_ct_id(String ref_ct_id) {
        this.ref_ct_id = ref_ct_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    public String getFace_url_h() {
        return face_url_h;
    }

    public void setFace_url_h(String face_url_h) {
        this.face_url_h = face_url_h;
    }

    public String getFace_url_m() {
        return face_url_m;
    }

    public void setFace_url_m(String face_url_m) {
        this.face_url_m = face_url_m;
    }

    public String getStu_num_op() {
        return stu_num_op;
    }

    public void setStu_num_op(String stu_num_op) {
        this.stu_num_op = stu_num_op;
    }

    public String getStu_num() {
        return stu_num;
    }

    public void setStu_num(String stu_num) {
        this.stu_num = stu_num;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

    public String getHotness() {
        return hotness;
    }

    public void setHotness(String hotness) {
        this.hotness = hotness;
    }

    public String getHas_video() {
        return has_video;
    }

    public void setHas_video(String has_video) {
        this.has_video = has_video;
    }

    public String getHas_ext_video() {
        return has_ext_video;
    }

    public void setHas_ext_video(String has_ext_video) {
        this.has_ext_video = has_ext_video;
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

    public String getExt_teacher_id() {
        return ext_teacher_id;
    }

    public void setExt_teacher_id(String ext_teacher_id) {
        this.ext_teacher_id = ext_teacher_id;
    }

    public String getExt_teacher_name() {
        return ext_teacher_name;
    }

    public void setExt_teacher_name(String ext_teacher_name) {
        this.ext_teacher_name = ext_teacher_name;
    }

    public String getExt_head_img() {
        return ext_head_img;
    }

    public void setExt_head_img(String ext_head_img) {
        this.ext_head_img = ext_head_img;
    }

    public String getExt_teacher_desc() {
        return ext_teacher_desc;
    }

    public void setExt_teacher_desc(String ext_teacher_desc) {
        this.ext_teacher_desc = ext_teacher_desc;
    }
}
