package com.easier.writepre.entity;

public class VodCourseCategoryInfo extends  BaseCourseCategoryInfo {

	private String csvod_id;

	private String video_id;

	private String video_url;

	private String sort_num;

	private String ctime;


	public String getCsvod_id() {
		return csvod_id;
	}

	public void setCsvod_id(String csvod_id) {
		this.csvod_id = csvod_id;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public String getSort_num() {
		return sort_num;
	}

	public void setSort_num(String sort_num) {
		this.sort_num = sort_num;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
