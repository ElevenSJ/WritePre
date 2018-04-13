package com.easier.writepre.param;

/**
 * 
 * 作品刷新
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksRefreshParams extends BaseBodyParams {

	private String works_id;

//	public String getWorks_id() {
//		return works_id;
//	}
//
//	public void setWorks_id(String works_id) {
//		this.works_id = works_id;
//	}

	public PkWorksRefreshParams(String works_id) {
		this.works_id = works_id;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_refresh";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
