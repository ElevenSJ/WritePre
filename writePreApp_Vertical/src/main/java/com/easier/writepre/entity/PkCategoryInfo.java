package com.easier.writepre.entity;

public class PkCategoryInfo {
	private String _id;		// 新闻_id
	private String title;	// 碑帖名
	private String role;	//参与角色： 0 学生与老师  1 学生  2 老师
	private String type;	// 0 软笔  1 硬笔 2 论文
	private String memo;
	private String ctime;
	private String signup;	// 0未报名 ， 1 已经报名
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getSignup() {
		return signup;
	}
	public void setSignup(String signup) {
		this.signup = signup;
	}
	
}
