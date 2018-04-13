package com.easier.writepre.entity;

public class PkWorksRemarkQueryInfo {
	private String _id;		// 分区键
	private String works_id;		// shardingKey
	private String user_id;
	private String uname;
	private String head_img;
	private String reply_to;  // 回复给哪条评论
	private String reply_to_user;   //回复给user_id
	private String reply_to_uname;	// 回复给用户昵称  例如 @sky
	private String title;
	private String ctime;
	private String is_teacher = "";// 1 是， 0或其他  否
	private String voice_url="";//音频网络url
	private String voice_len="0";//音频时长

	public String getVoice_url() {
		return voice_url;
	}

	public void setVoice_url(String voice_url) {
		this.voice_url = voice_url;
	}

	public String getVoice_len() {
		return voice_len;
	}

	public void setVoice_len(String voice_len) {
		this.voice_len = voice_len;
	}

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getWorks_id() {
		return works_id;
	}
	public void setWorks_id(String works_id) {
		this.works_id = works_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
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
	public String getReply_to() {
		return reply_to;
	}
	public void setReply_to(String reply_to) {
		this.reply_to = reply_to;
	}
	public String getReply_to_user() {
		return reply_to_user;
	}
	public void setReply_to_user(String reply_to_user) {
		this.reply_to_user = reply_to_user;
	}
	public String getReply_to_uname() {
		return reply_to_uname;
	}
	public void setReply_to_uname(String reply_to_uname) {
		this.reply_to_uname = reply_to_uname;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getIs_teacher() {
		return is_teacher;
	}
	public void setIs_teacher(String is_teacher) {
		this.is_teacher = is_teacher;
	}
	
	
}
