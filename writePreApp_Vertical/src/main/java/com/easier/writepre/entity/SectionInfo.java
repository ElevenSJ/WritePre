package com.easier.writepre.entity;

public class SectionInfo {

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSec_title() {
		return sec_title;
	}

	public void setSec_title(String sec_title) {
		this.sec_title = sec_title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	private String _id;
	private String sec_title;
	private String type;
	private String memo;

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	private boolean isCheck;

}
