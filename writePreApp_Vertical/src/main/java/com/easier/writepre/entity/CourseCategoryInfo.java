package com.easier.writepre.entity;

import java.util.List;

public class CourseCategoryInfo extends  BaseCourseCategoryInfo {


	private List<CourseCategoryInfo> child;


	private String ref_course_id;

	private String memo;

	private String sort_num;

	private String depth;

	private String p_id;

	private String is_end="";

	private String loadable;

	private String is_read="";

	public List<CourseCategoryInfo> getChild() {
		return child;
	}

	public String getDepth() {
		return depth;
	}

	public String getIs_end() {
		return is_end;
	}

	public String getIs_read() {
		return is_read;
	}

	public String getLoadable() {
		return loadable;
	}

	public String getMemo() {
		return memo;
	}

	public String getP_id() {
		return p_id;
	}

	public String getRef_course_id() {
		return ref_course_id;
	}

	public String getSort_num() {
		return sort_num;
	}


	public void setChild(List<CourseCategoryInfo> child) {
		this.child = child;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public void setIs_end(String is_end) {
		this.is_end = is_end;
	}
	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}
	public void setLoadable(String loadable) {
		this.loadable = loadable;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public void setP_id(String p_id) {
		this.p_id = p_id;
	}
	public void setRef_course_id(String ref_course_id) {
		this.ref_course_id = ref_course_id;
	}
	public void setSort_num(String sort_num) {
		this.sort_num = sort_num;
	}

}
