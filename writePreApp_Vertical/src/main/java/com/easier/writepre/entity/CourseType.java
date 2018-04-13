package com.easier.writepre.entity;

import java.io.Serializable;

public class CourseType implements Serializable {

	private static final long serialVersionUID = 1L;

	private String _id;

	private String title;

	private String sort;

	private String ctime;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

}
