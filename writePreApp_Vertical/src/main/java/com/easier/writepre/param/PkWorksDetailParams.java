package com.easier.writepre.param;

/**
 * 
 * 作品详情
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksDetailParams extends BaseBodyParams {

	private String works_id;

//	public String getWorks_id() {
//		return works_id;
//	}
//
//	public void setWorks_id(String works_id) {
//		this.works_id = works_id;
//	}

	public PkWorksDetailParams(String works_id) {
		this.works_id = works_id;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_detail";
	}

	@Override
	public String getUrl() {
		// return "app";
		return "login";
	}

}
