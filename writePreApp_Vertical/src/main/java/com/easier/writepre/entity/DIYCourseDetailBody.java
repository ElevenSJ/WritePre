package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class DIYCourseDetailBody implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CourseContentInfo> words_ref;

	private String memo;

	private String is_read="";

	private String title;

	private String index;

	public String getIndex() {
		return index;
	}

	public String getIs_read() {
		return is_read;
	}

	public String getMemo() {
		return memo;
	}

	public String getTitle() {
		return title;
	}

	public List<CourseContentInfo> getWords_ref() {
		return words_ref;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWords_ref(List<CourseContentInfo> words_ref) {
		this.words_ref = words_ref;
	}

}
