package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class PkWorksAccuseParams extends BaseBodyParams {
	private String works_id,title,type;
	
//	public String getWorks_id() {
//		return works_id;
//	}
//
//	public void setWorks_id(String works_id) {
//		this.works_id = works_id;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
	
	public PkWorksAccuseParams(String works_id, String title, String type) {
//		super();
		this.works_id = works_id;
		this.title = title;
		this.type = type;
	}

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "sj_pk_works_accuse";
	}
	
	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
