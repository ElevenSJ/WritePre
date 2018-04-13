package com.easier.writepre.entity;

import java.io.Serializable;
import java.util.List;

public class CircleDetail implements Serializable{

	private String _id;

	private String user_id;

	private String uname;
	
	private String head_img;

	private String name;
	
	private String desc;

	private String type;

	private String face_url;

	private List<CircleMarkNo> mark_no;

	private int num;
	
	private int num_limit;

	private int post_num;

	private int del_num;
	
	private int accuse_num;
	
	private String is_open;
	
	private String ctime;
	
	private String role;

	private String code;//直接加入圈子口令

	private String circle_uname;//我的圈子昵称

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUname() {
		return uname;
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

	public List<CircleMarkNo> getMark_no() {
		return mark_no;
	}

	public void setMark_no(List<CircleMarkNo> mark_no) {
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCircle_uname() {
		return circle_uname;
	}

	public void setCircle_uname(String circle_uname) {
		this.circle_uname = circle_uname;
	}

	public class CircleMarkNo implements Serializable{
		private String _id;
		private int no;
		private String title;
		private String bgcolor;
		private String in_use;
		public String get_id() {
			return _id;
		}
		public void set_id(String _id) {
			this._id = _id;
		}
		public int getNo() {
			return no;
		}
		public void setNo(int no) {
			this.no = no;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getBgcolor() {
			return bgcolor;
		}
		public void setBgcolor(String bgcolor) {
			this.bgcolor = bgcolor;
		}
		public String getIn_use() {
			return in_use;
		}
		public void setIn_use(String in_use) {
			this.in_use = in_use;
		}
	}
}
