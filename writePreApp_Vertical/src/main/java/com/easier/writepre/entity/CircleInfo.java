package com.easier.writepre.entity;

import java.util.List;

public class CircleInfo extends CircleBase{

	private String user_id;

	private String name;

	private String desc;

	private String type;

	private String face_url;

	private List<Object> mark_no;

	private int num;
	
	private int num_limit;

	private int post_num;

	private int del_num;
	
	private int accuse_num;
	
	private String is_open;
	
	private String ctime;
	
	private int has_req;
	private String sortLetters="";

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFace_url() {
		return face_url;
	}

	public void setFace_url(String face_url) {
		this.face_url = face_url;
	}

	public List<Object> getMark_no() {
		return mark_no;
	}

	public void setMark_no(List<Object> mark_no) {
		this.mark_no = mark_no;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getNum_limit() {
		return num_limit;
	}

	public void setNum_limit(int num_limit) {
		this.num_limit = num_limit;
	}

	public int getPost_num() {
		return post_num;
	}

	public void setPost_num(int post_num) {
		this.post_num = post_num;
	}

	public int getDel_num() {
		return del_num;
	}

	public void setDel_num(int del_num) {
		this.del_num = del_num;
	}

	public int getAccuse_num() {
		return accuse_num;
	}

	public void setAccuse_num(int accuse_num) {
		this.accuse_num = accuse_num;
	}

	public String getIs_open() {
		return is_open;
	}

	public void setIs_open(String is_open) {
		this.is_open = is_open;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public int getHas_req() {
		return has_req;
	}

	public void setHas_req(int has_req) {
		this.has_req = has_req;
	}
	

	
}
