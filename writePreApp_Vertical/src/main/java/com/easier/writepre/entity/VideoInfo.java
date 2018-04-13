package com.easier.writepre.entity;

import java.io.Serializable;

public class VideoInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String _id;

	private String url;

}
