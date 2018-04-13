package com.easier.writepre.param;

import java.util.List;

import com.easier.writepre.utils.SPUtils;

public class CircleUpdateParams extends BaseBodyParams {

	private String circle_id,name, desc, type, face_url,is_open;
	private int num_limit;
	private List<CircleAddParams.MarkParams> mark_no;

	
	public CircleUpdateParams() {
		
	}

	public CircleUpdateParams(String circle_id,CircleAddParams params ) {
		// super();
		this.circle_id = circle_id;
		this.name = params.getName();
		this.desc = params.getDesc();
		this.type = params.getType();
		this.face_url = params.getFace_url();
		this.num_limit = params.getNum_limit();
		this.is_open = params.getIs_open();
		this.mark_no = params.getMark_no();
	}
	
	

	public String getCircle_id() {
		return circle_id;
	}

	public void setCircle_id(String circle_id) {
		this.circle_id = circle_id;
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


	public List<CircleAddParams.MarkParams> getMark_no() {
		return mark_no;
	}


	public void setMark_no(List<CircleAddParams.MarkParams> mark_no) {
		this.mark_no = mark_no;
	}


	@Override
	public String getProNo() {
		return "sj_circle_update";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
