package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class CourseList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<CourseList> child;

	private String sec_id;
	private String _id;
	private String title;
	private String memo;
	private String type="";

	public List<CourseList> getChild() {
		return child;
	}

	public void setChild(List<CourseList> child) {
		this.child = child;
	}

	public String getSec_id() {
		return sec_id;
	}

	public void setSec_id(String sec_id) {
		this.sec_id = sec_id;
	}

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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
