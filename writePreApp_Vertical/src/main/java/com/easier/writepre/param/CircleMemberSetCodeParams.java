package com.easier.writepre.param;

/**
 * 
 * 设置入圈口令
 * 
 */
public class CircleMemberSetCodeParams extends BaseBodyParams {

	private String circle_id;
	private String code;

	public CircleMemberSetCodeParams(String circle_id, String code) {
		this.code = code;
		this.circle_id = circle_id;
	}
	
	@Override
	public String getProNo() {
		return "sj_circle_accept_code_set";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
