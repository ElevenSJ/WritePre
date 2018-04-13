package com.easier.writepre.param;

/**
 * 书法家查询
 * 
 */
public class ResPenmenParams extends BaseBodyParams {

	@Override
	public String getProNo() {
		return "res_penmen";
	}

	public ResPenmenParams() {
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
