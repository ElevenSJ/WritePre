package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class CourseResDirWord implements Serializable{
	
	private String _id;
	private String dir_id;
	private String exwords_id;
	private String title;
	private String pic_url;
	private String pic_url_min;
	private String vedio_pic;
	private String vedio_url;
	private String type;
	private String sfrom;
	private String ctime;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getDir_id() {
		return dir_id;
	}
	public void setDir_id(String dir_id) {
		this.dir_id = dir_id;
	}
	public String getExwords_id() {
		return exwords_id;
	}
	public void setExwords_id(String exwords_id) {
		this.exwords_id = exwords_id;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSfrom() {
		return sfrom;
	}
	public void setSfrom(String sfrom) {
		this.sfrom = sfrom;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

}
