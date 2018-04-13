package com.easier.writepre.entity;

public class ActiveInfo extends ContentBase {

	private String img_url;
	private String img_url_h;
	private String desc;
	private String is_pub="";

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIs_pub() {
		return is_pub;
	}

	public void setIs_pub(String is_pub) {
		this.is_pub = is_pub;
	}

	public String getImg_url_h() {
		return img_url_h;
	}

	public void setImg_url_h(String img_url_h) {
		this.img_url_h = img_url_h;
	}
}