package com.easier.writepre.param;

/**
 * 朝代书法家查询
 * 
 */
public class ResPeriodPenmenParams extends BaseBodyParams {

	private String period_id;

	@Override
	public String getProNo() {
		return "res_period_penmen_list";
	}

	public ResPeriodPenmenParams(String period_id) {
		this.period_id = period_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
