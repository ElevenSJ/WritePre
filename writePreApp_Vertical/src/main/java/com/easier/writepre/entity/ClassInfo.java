package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class ClassInfo implements Serializable {

	private static final long serialVersionUID = 1L;

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

	public List<ClassHour> getChild() {
		return child;
	}

	public void setChild(List<ClassHour> child) {
		this.child = child;
	}
	

	public String getFace_url() {
		return face_url;
	}

	public void setFace_url(String face_url) {
		this.face_url = face_url;
	}



	private String title="";
	private String author="";
	private String memo="";
	private List<ClassHour> child;
	private String face_url="";

}
