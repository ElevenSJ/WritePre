package com.easier.writepre.entity;

import java.io.Serializable;

public class CategoryInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getRef_repo_id() {
		return ref_repo_id;
	}

	public void setRef_repo_id(String ref_repo_id) {
		this.ref_repo_id = ref_repo_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getP_id() {
		return p_id;
	}

	public void setP_id(String p_id) {
		this.p_id = p_id;
	}

	public String getIs_end() {
		return is_end;
	}

	public void setIs_end(String is_end) {
		this.is_end = is_end;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getLoadable() {
		return loadable;
	}

	public void setLoadable(String loadable) {
		this.loadable = loadable;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	
	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	private boolean isCheck;

	private String _id;
	private String ref_repo_id;
	private String title;
	private String sort;
	private String p_id;
	private String is_end;
	private String depth;
	private String loadable;
	private String file_url;

}
