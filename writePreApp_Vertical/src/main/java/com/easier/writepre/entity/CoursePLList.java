package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class CoursePLList implements Serializable {

	private static final long serialVersionUID = 1L;

	private String course_id;
	private String _id;
	private String user_id;
	private String title;
	private int level;
	private String ctime;
	private String uname;
	private String head_img;
	private String is_teacher = "";

	public String getCourse_id() {
		return course_id;
	}

	public void setCourse_id(String course_id) {
		this.course_id = course_id;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
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

	public String getIs_teacher() {
		return is_teacher;
	}

	public void setIs_teacher(String is_teacher) {
		this.is_teacher = is_teacher;
	}
}
