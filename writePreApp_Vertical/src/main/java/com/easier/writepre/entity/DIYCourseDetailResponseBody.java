package com.easier.writepre.entity;

import java.util.List;

public class DIYCourseDetailResponseBody {

	public List<DIYCourseDetailBody> getChild() {
		return child;
	}

	public void setChild(List<DIYCourseDetailBody> child) {
		this.child = child;
	}

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	

	public String getFace_url() {
		return face_url;
	}

	public void setFace_url(String face_url) {
		this.face_url = face_url;
	}


	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}




	private List<DIYCourseDetailBody> child;

	private String _id;
	private String title;
	private String author;
	private String memo;
	private String face_url;
	private String ctime;

}
