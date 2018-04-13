package com.easier.writepre.param;

/**
 * 机构报名查询
 */
public class CalligraphyParams extends BaseBodyParams {

	@Override
	public String getProNo() {
		return "sj_org_req_query";
	}

	@Override
	public String getUrl() {
		return "app" ;
	}

}
