package com.easier.writepre.param;

/**
 * 用户关注的
 * 
 * 
 */
public class UserCareParams extends BaseBodyParams {

	private String user_id;
	private String start;
	private String count;

	public UserCareParams(String user_id, String start, String count) {
		this.user_id = user_id;
		this.start = start;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_user_care_list";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
