package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class PkWorksRemarkQueryParams extends BaseBodyParams {
	
	private String works_id, last_id, count;
	
//	public String getWorks_id() {
//		return works_id;
//	}
//
//	public void setWorks_id(String works_id) {
//		this.works_id = works_id;
//	}
//
//	public String getLast_id() {
//		return last_id;
//	}
//
//	public void setLast_id(String last_id) {
//		this.last_id = last_id;
//	}
//
//	public String getCount() {
//		return count;
//	}
//
//	public void setCount(String count) {
//		this.count = count;
//	}
	
	public PkWorksRemarkQueryParams(String works_id, String last_id,
			String count) {
		this.works_id = works_id;
		this.last_id = last_id;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_remark_query";
	}
	
	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
