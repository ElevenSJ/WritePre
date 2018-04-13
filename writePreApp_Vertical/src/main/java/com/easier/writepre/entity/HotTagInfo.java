package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 碑帖推荐列表
 * 
 * @author zhoulu
 * 
 */
public class HotTagInfo implements Serializable {
	private String _id="";
	private String beitie_id="";
	private String beitie_title="";
	private String beitie_face_url="";
	private String beitie_link_url="";
	private String sort_num="";
	private String ctime="";

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getBeitie_face_url() {
		return beitie_face_url;
	}

	public void setBeitie_face_url(String beitie_face_url) {
		this.beitie_face_url = beitie_face_url;
	}

	public String getBeitie_id() {
		return beitie_id;
	}

	public void setBeitie_id(String beitie_id) {
		this.beitie_id = beitie_id;
	}

	public String getBeitie_title() {
		return beitie_title;
	}

	public void setBeitie_title(String beitie_title) {
		this.beitie_title = beitie_title;
	}

	public String getBeitie_link_url() {
		return beitie_link_url;
	}

	public void setBeitie_link_url(String beitie_link_url) {
		this.beitie_link_url = beitie_link_url;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getSort_num() {
		return sort_num;
	}

	public void setSort_num(String sort_num) {
		this.sort_num = sort_num;
	}
}