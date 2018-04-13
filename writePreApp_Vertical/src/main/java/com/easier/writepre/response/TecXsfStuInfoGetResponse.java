package com.easier.writepre.response;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 考生信息查询
 */
public class TecXsfStuInfoGetResponse extends BaseResponse {

    private TecXsfStuInfoGetBody repBody;

    public TecXsfStuInfoGetBody getRepBody() {
        return repBody;
    }

    public void setRepBody(TecXsfStuInfoGetBody repBody) {
        this.repBody = repBody;
    }

    public class TecXsfStuInfoGetBody implements Serializable {

        private String _id;    // 考生id
        private String user_id;// app用户id
        private String real_name = "未知";
        private String id_num;        // 身份证号
        private String city;
        private String addr;        // 详细地址
        private String school_name;        // 培训机构或学校地址
        private String school_contact;    // 联系方式
        private String photo_url;        // 证件照地址
        private String current_level;        // 当前等级
        private String level_price;        // 等级价格
        private String level_price_id;    // 价格id
        private String is_pay;            // ok | no是否支付过
        private String stu_level = "0";            // 报考等级
        private String stu_time = "0";            // 当前获得学时
        private String need_time = "0";            // 当前等级所需要的学时
        private String stu_status;        // study 报考学习中、available 可报考、to_verify 待校验
        private String circle_id;        // 学习圈子id
        private String circle_name;        // 学习圈子id
        private String sex;        // 性别
        private String ctime;
        private String is_xsf_teacher = "0";//考试管理员  1：是 0：否
        private String info_url;//报名信息说明页面url
        private String exam_type = "0";// 0(报考停止) 1(允许报考)
        private String exam_info ;// 报名(6月1日至6月30日)

        public String getExam_type() {
            return exam_type;
        }

        public String getExam_info() {
            return exam_info;
        }

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

        public String getReal_name() {
            if (TextUtils.isEmpty(real_name)) {
                return "未知";
            }
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getId_num() {
            return id_num;
        }

        public void setId_num(String id_num) {
            this.id_num = id_num;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
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

        public String getSchool_contact() {
            return school_contact;
        }

        public void setSchool_contact(String school_contact) {
            this.school_contact = school_contact;
        }

        public String getPhoto_url() {
            return photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }

        public String getCurrent_level() {
            return current_level;
        }

        public void setCurrent_level(String current_level) {
            this.current_level = current_level;
        }

        public String getLevel_price() {
            return level_price;
        }

        public void setLevel_price(String level_price) {
            this.level_price = level_price;
        }

        public String getLevel_price_id() {
            return level_price_id;
        }

        public void setLevel_price_id(String level_price_id) {
            this.level_price_id = level_price_id;
        }

        public String getIs_pay() {
            return is_pay;
        }

        public void setIs_pay(String is_pay) {
            this.is_pay = is_pay;
        }

        public String getStu_level() {
            if (TextUtils.isEmpty(stu_level)) {
                return "0";
            }
            return stu_level;
        }

        public void setStu_level(String stu_level) {
            this.stu_level = stu_level;
        }

        public String getStu_time() {
            if (TextUtils.isEmpty(stu_time)) {
                return "0";
            }
            return stu_time;
        }

        public void setStu_time(String stu_time) {
            this.stu_time = stu_time;
        }

        public String getNeed_time() {
            if (TextUtils.isEmpty(need_time)) {
                return "0";
            }
            return need_time;
        }

        public void setNeed_time(String need_time) {
            this.need_time = need_time;
        }

        public String getStu_status() {
            return stu_status;
        }

        public void setStu_status(String stu_status) {
            this.stu_status = stu_status;
        }

        public String getCircle_id() {
            return circle_id;
        }

        public void setCircle_id(String circle_id) {
            this.circle_id = circle_id;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getInfo_url() {
            return info_url;
        }

        public void setInfo_url(String info_url) {
            this.info_url = info_url;
        }

        public String getIs_xsf_teacher() {
            return is_xsf_teacher;
        }

        public void setIs_xsf_teacher(String is_xsf_teacher) {
            this.is_xsf_teacher = is_xsf_teacher;
        }

        public String getCircle_name() {
            return circle_name;
        }

        public void setCircle_name(String circle_name) {
            this.circle_name = circle_name;
        }
    }
}
