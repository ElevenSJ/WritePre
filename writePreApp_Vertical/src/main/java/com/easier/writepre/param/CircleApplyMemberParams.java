package com.easier.writepre.param;

/**
 * 圈 友
 * 
 * 
 */
public class CircleApplyMemberParams extends BaseBodyParams {
	
	private String circle_id;

	private String last_id;
	private int count;
	
	public CircleApplyMemberParams(String circle_id,String last_id,int count) {
		this.circle_id = circle_id;
		this.last_id = last_id;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_req_list";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
