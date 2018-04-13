package com.easier.writepre.param;

/**
 * 
 * 点赞
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksOkParams extends BaseBodyParams {

	private String works_id;

//	public String getWorks_id() {
//		return works_id;
//	}
//
//	public void setWorks_id(String works_id) {
//		this.works_id = works_id;
//	}

	public PkWorksOkParams(String works_id) {
		this.works_id = works_id;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_ok";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
