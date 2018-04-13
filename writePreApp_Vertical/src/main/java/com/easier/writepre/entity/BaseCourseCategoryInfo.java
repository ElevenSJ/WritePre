package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class BaseCourseCategoryInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String _id;

	private String title;

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
}
