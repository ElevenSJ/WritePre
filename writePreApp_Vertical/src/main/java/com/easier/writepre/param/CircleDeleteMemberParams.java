package com.easier.writepre.param;

/**
 * 删除成员
 * 
 * 
 */
public class CircleDeleteMemberParams extends BaseBodyParams {
	
	private String circle_id;

	private String user_id;

	public CircleDeleteMemberParams(String circle_id,String user_id) {
		this.circle_id = circle_id;
		this.user_id = user_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_del";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
