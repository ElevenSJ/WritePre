package com.easier.writepre.entity;

public class ApplyForTeacherStatusInfo {
	private String _id = "";
	private String user_id = "";
	private String real_name = "";
	private String sex = "";
	private String birth_day = ""; // yyyyMMdd
	private String city = "";
	private String school_name = "";
	private String tel = "";
	private String tech_pic = "";
	private String status = ""; // ok 通过， no 拒绝
	private String status_msg = "";// 拒绝理由
	private String rep_time = ""; // 处理时间
	private String req_time = ""; // 请求时间
	private String dealed = ""; // 0 等待处理 1 已经处理
	private String ctime = "";
	private String req_id = "";
	

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
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth_day() {
		return birth_day;
	}

	public void setBirth_day(String birth_day) {
		this.birth_day = birth_day;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTech_pic() {
		return tech_pic;
	}

	public void setTech_pic(String tech_pic) {
		this.tech_pic = tech_pic;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus_msg() {
		return status_msg;
	}

	public void setStatus_msg(String status_msg) {
		this.status_msg = status_msg;
	}

	public String getRep_time() {
		return rep_time;
	}

	public void setRep_time(String rep_time) {
		this.rep_time = rep_time;
	}

	public String getReq_time() {
		return req_time;
	}

	public void setReq_time(String req_time) {
		this.req_time = req_time;
	}

	public String getDealed() {
		return dealed;
	}

	public void setDealed(String dealed) {
		this.dealed = dealed;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getReq_id() {
		return req_id;
	}

	public void setReq_id(String req_id) {
		this.req_id = req_id;
	}
	
}
