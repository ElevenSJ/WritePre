package com.easier.writepre.param;

import java.util.List;

import com.easier.writepre.utils.SPUtils;

public class CircleAddParams extends BaseBodyParams {

	private String name, desc, type, face_url,is_open;
	private int num_limit;
	private List<MarkParams> mark_no;

	
	public class MarkParams{
		int no;
		public MarkParams (int id){
			this.no = id;
		}
		public int getNo() {
			return no;
		}
		public void setNo(int no) {
			this.no = no;
		}
		
	}
	public CircleAddParams() {
	}

	public CircleAddParams(String name, String desc, String type,
			String face_url,int num_limit,String is_open,List<MarkParams> mark_no ) {
		// super();
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.face_url = face_url;
		this.num_limit = num_limit;
		this.is_open = is_open;
		this.mark_no = mark_no;
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


	public int getNum_limit() {
		return num_limit;
	}


	public void setNum_limit(int num_limit) {
		this.num_limit = num_limit;
	}


	public String getIs_open() {
		return is_open;
	}


	public void setIs_open(String is_open) {
		this.is_open = is_open;
	}


	public List<MarkParams> getMark_no() {
		return mark_no;
	}


	public void setMark_no(List<MarkParams> mark_no) {
		this.mark_no = mark_no;
	}


	@Override
	public String getProNo() {
		return "sj_circle_add";
	}

	@Override
	public String getUrl() {
		return "app";
	}
}
