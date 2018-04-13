package com.easier.writepre.entity;

public class VersionInfo {
	private int is_must = 0;

	private int level;

	private String version;

	private String file_url;

	private int type;

	private int role;

	private String up_reason;

	private String v_date;

	public int getIs_must() {
		return is_must;
	}

	public void setIs_must(int is_must) {
		this.is_must = is_must;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getUp_reason() {
		return up_reason;
	}

	public void setUp_reason(String up_reason) {
		this.up_reason = up_reason;
	}

	public String getV_date() {
		return v_date;
	}

	public void setV_date(String v_date) {
		this.v_date = v_date;
	}

}
