package com.easier.writepre.entity;

import java.util.List;

public class CourseResDir {
	
	private String _id;
	private String title;
	private String p_id;
	private String sort;
	private String memo;
	private String ctime;
	private List<CourseResDir> children;
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
	public String getP_id() {
		return p_id;
	}
	public void setP_id(String p_id) {
		this.p_id = p_id;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public List<CourseResDir> getChildren() {
		return children;
	}
	public void setChildren(List<CourseResDir> children) {
		this.children = children;
	}

}
