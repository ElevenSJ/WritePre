package com.easier.writepre.param;

/**
 * 朝代列表查询
 * 
 */
public class ResPeriodParams extends BaseBodyParams {

	@Override
	public String getProNo() {
		return "res_period_list";
	}

	public ResPeriodParams() {
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
