package com.easier.writepre.param;

/**
 * 
 * 清除入圈口令
 * 
 */
public class CircleMemberClearCodeParams extends BaseBodyParams {

	private String circle_id;

	public CircleMemberClearCodeParams(String circle_id) {
		this.circle_id = circle_id;
	}
	
	@Override
	public String getProNo() {
		return "sj_circle_accept_code_clear";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
