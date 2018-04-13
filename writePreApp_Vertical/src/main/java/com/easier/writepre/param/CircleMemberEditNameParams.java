package com.easier.writepre.param;

/**
 * 
 * 圈成员编辑圈子昵称
 * 
 */
public class CircleMemberEditNameParams extends BaseBodyParams {

	private String user_id;
	private String circle_id;
	private String circle_uname;

	public CircleMemberEditNameParams(String user_id,String circle_id, String circle_uname) {
		this.user_id = user_id;
		this.circle_uname = circle_uname;
		this.circle_id = circle_id;
	}
	
	@Override
	public String getProNo() {
		return "sj_circle_user_uname_set";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
