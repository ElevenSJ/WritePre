package com.easier.writepre.param;

/**
 * 
 * 圈子设置管理员
 * 
 */
public class CircleSetAdminParams extends BaseBodyParams {

	private String circle_id;
	private String user_id;
	private String action;

	public CircleSetAdminParams(String circle_id, String user_id, String action) {
		this.circle_id = circle_id;
		this.user_id = user_id;
		this.action = action;
	}

	@Override
	public String getProNo() {
		return "sj_circle_set_admin";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
