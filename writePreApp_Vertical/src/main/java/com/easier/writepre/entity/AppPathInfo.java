package com.easier.writepre.entity;

public class AppPathInfo {

	private String _id = "";

	private String version = "";

	private String title = "";

	private String pkg_url = "";

	private String ctime = "";

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPkg_url() {
		return pkg_url;
	}

	public void setPkg_url(String pkg_url) {
		this.pkg_url = pkg_url;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}