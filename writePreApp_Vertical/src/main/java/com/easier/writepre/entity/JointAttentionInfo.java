package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * 相互关注的好友信息
 * 
 * @author zhoulu
 * 
 */
public class JointAttentionInfo implements Serializable {
	private String _id = "";
	private String uname = "";
	private String head_img = "";
	private String user_id="";
	private String signature="";
	private String sortLetters="";

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUname() {
		return uname;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	@Override
	public String toString() {
		return "JointAttentionInfo [_id=" + _id + ", uname=" + uname
				+ ", head_img=" + head_img + "]";
	}

}