package com.easier.writepre.param;

/**
 * 
 * 设置入圈口令
 * 
 */
public class CircleMemberRegByCodeParams extends BaseBodyParams {

	private String circle_id;
	private String code;

	public CircleMemberRegByCodeParams(String circle_id, String code) {
		this.code = code;
		this.circle_id = circle_id;
	}
	
	@Override
	public String getProNo() {
		return "sj_circle_user_req_by_code";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
