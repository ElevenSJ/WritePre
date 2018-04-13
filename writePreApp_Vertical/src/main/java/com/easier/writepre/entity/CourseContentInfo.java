package com.easier.writepre.entity;

import java.io.Serializable;

public class CourseContentInfo implements Serializable {

	private static final long serialVersionUID = 1L;

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

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public String getPic_url_min() {
		return pic_url_min;
	}

	public void setPic_url_min(String pic_url_min) {
		this.pic_url_min = pic_url_min;
	}

	public String getVedio_pic() {
		return vedio_pic;
	}

	public void setVedio_pic(String vedio_pic) {
		this.vedio_pic = vedio_pic;
	}

	public String getVedio_url() {
		return vedio_url;
	}

	public void setVedio_url(String vedio_url) {
		this.vedio_url = vedio_url;
	}

	public String getSfrom() {
		return sfrom;
	}

	public void setSfrom(String sfrom) {
		this.sfrom = sfrom;
	}

	private String _id;
	private String title;
	private String pic_url;
	private String pic_url_min;
	private String vedio_pic;
	private String vedio_url;
	private String sfrom;

}
