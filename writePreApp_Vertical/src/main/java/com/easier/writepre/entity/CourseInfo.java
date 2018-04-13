package com.easier.writepre.entity;

import java.io.Serializable;

public class CourseInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cata_course_id;
	
	private String circle_id;
	
	private String _id;

	private String ref_lsn_id;

	private String title;

	private String face_url;

	private String type="";

	private String ref_ct_id;

	private String stu_num_op;

	private String stu_num;

	private String memo;

	private String hotness;

	private String has_video = "";

	private String ctime = "";

	private String pub_name = "";

	private String has_ext_video = "";

	public CourseInfo(String title) {
		this.title = title;
	}

	public CourseInfo() {
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	private boolean isVisible;

	public String get_id() {
		return _id;
	}

	public String getFace_url() {
		return face_url;
	}

	public String getMemo() {
		return memo;
	}

	public String getRef_ct_id() {
		return ref_ct_id;
	}

	public String getRef_lsn_id() {
		return ref_lsn_id;
	}

	public String getStu_num() {
		return stu_num;
	}

	public String getStu_num_op() {
		return stu_num_op;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public void setFace_url(String face_url) {
		this.face_url = face_url;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setRef_ct_id(String ref_ct_id) {
		this.ref_ct_id = ref_ct_id;
	}

	public void setRef_lsn_id(String ref_lsn_id) {
		this.ref_lsn_id = ref_lsn_id;
	}

	public void setStu_num(String stu_num) {
		this.stu_num = stu_num;
	}

	public void setStu_num_op(String stu_num_op) {
		this.stu_num_op = stu_num_op;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getCata_course_id() {
		return cata_course_id;
	}

	public void setCata_course_id(String cata_course_id) {
		this.cata_course_id = cata_course_id;
	}

	public String getCircle_id() {
		return circle_id;
	}

	public void setCircle_id(String circle_id) {
		this.circle_id = circle_id;
	}

	public String getPub_name() {
		return pub_name;
	}

	public void setPub_name(String pub_name) {
		this.pub_name = pub_name;
	}

	public String getHas_ext_video() {
		return has_ext_video;
	}

	public void setHas_ext_video(String has_ext_video) {
		this.has_ext_video = has_ext_video;
	}
}
