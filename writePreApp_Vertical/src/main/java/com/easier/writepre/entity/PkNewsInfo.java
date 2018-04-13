package com.easier.writepre.entity;

public class PkNewsInfo {
	private String _id;		// 新闻_id
	private String pk_id;		// shardingKey
	private String title;
	private String desc;
	private String face_url;
	private String url;
	private String ctime;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getPk_id() {
		return pk_id;
	}
	public void setPk_id(String pk_id) {
		this.pk_id = pk_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getFace_url() {
		return face_url;
	}
	public void setFace_url(String face_url) {
		this.face_url = face_url;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	
}
